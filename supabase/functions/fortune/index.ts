// Supabase Edge Function: fortune
// 사주(생년월일·성별·태어난 시각) 기반으로 오늘의 운세 리포트를 생성한다.
//   - 당일 캐시가 있으면 그대로 반환 (하루 1회 생성)
//   - 없으면 Gemini API 호출 → { grade, summary, advice } → fortunes 테이블 저장 후 반환
//   - fortunes 는 1일 보관: 새 리포트 생성 시 해당 유저의 과거 레코드 삭제
//
// 필요한 Edge Function 환경변수:
//   GEMINI_API_KEY — Google AI Studio API 키 (필수)
//   (SUPABASE_URL, SUPABASE_SERVICE_ROLE_KEY 는 자동 주입)

import { serve } from "https://deno.land/std@0.208.0/http/server.ts";
import { createClient } from "https://esm.sh/@supabase/supabase-js@2";

const corsHeaders = {
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Headers":
    "authorization, x-client-info, apikey, content-type",
};

const ALLOWED_GRADES = ["SUNNY", "CLEAR", "CLOUDY", "RAINY", "STORM"];
const GEMINI_MODEL = "gemini-1.5-flash";

// KST(Asia/Seoul) 기준 오늘 날짜 (YYYY-MM-DD)
function todayKst(): string {
  // en-CA 로케일은 YYYY-MM-DD 포맷을 반환한다.
  return new Date().toLocaleDateString("en-CA", { timeZone: "Asia/Seoul" });
}

function buildPrompt(
  birthDate: string,
  gender: string,
  birthTime: string | null,
  date: string,
): string {
  const genderKo = gender === "MALE" ? "남성" : gender === "FEMALE" ? "여성" : gender;
  const timeKo = birthTime
    ? `${birthTime}시 (12지시)`
    : "미상 — 정오(午)를 대표값으로 가정";

  return `당신은 따뜻하고 긍정적인 사주 운세 상담가입니다.
아래 사람의 사주 정보와 오늘 날짜를 바탕으로 '오늘의 종합운'을 작성하세요.

[사주 정보]
- 생년월일: ${birthDate}
- 성별: ${genderKo}
- 태어난 시각: ${timeKo}
- 오늘 날짜: ${date}

[작성 규칙]
1. 운세의 강도를 날씨 5단계 중 하나로 표현합니다.
   - SUNNY(화창): 모든 게 잘 풀리는 날, 적극적으로 도전
   - CLEAR(맑음): 좋은 흐름, 중요한 일을 처리하기 좋음
   - CLOUDY(구름): 평온한 하루, 무리 말고 꾸준하게
   - RAINY(비): 조심이 필요한 날, 그래도 괜찮아짐
   - STORM(폭풍번개): 힘든 날이지만 지나감, 오늘은 쉬어가도 됨
2. 모든 등급에서 위협·공포·부정적 판단을 절대 쓰지 않습니다.
   '나쁘다'가 아니라 '조심이 필요하다'로 표현하고, 위로와 격려로 끝맺습니다.
3. 반드시 한국어 존댓말로 작성합니다 (반말 금지).
4. summary: 오늘의 종합운 한 문장, 20자 이내.
5. advice: 오늘의 조언, 50자 이내.

JSON 형식으로만 답하세요.`;
}

interface FortuneResult {
  grade: string;
  summary: string;
  advice: string;
}

async function callGemini(prompt: string): Promise<FortuneResult> {
  const apiKey = Deno.env.get("GEMINI_API_KEY");
  if (!apiKey) throw new Error("GEMINI_API_KEY 가 설정되지 않았습니다");

  const url =
    `https://generativelanguage.googleapis.com/v1beta/models/${GEMINI_MODEL}:generateContent?key=${apiKey}`;

  const res = await fetch(url, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      contents: [{ parts: [{ text: prompt }] }],
      generationConfig: {
        temperature: 0.9,
        responseMimeType: "application/json",
        responseSchema: {
          type: "OBJECT",
          properties: {
            grade: { type: "STRING", enum: ALLOWED_GRADES },
            summary: { type: "STRING" },
            advice: { type: "STRING" },
          },
          required: ["grade", "summary", "advice"],
        },
      },
    }),
  });

  if (!res.ok) {
    const detail = await res.text();
    throw new Error(`Gemini API 오류 (${res.status}): ${detail}`);
  }

  const data = await res.json();
  const text = data?.candidates?.[0]?.content?.parts?.[0]?.text;
  if (!text) throw new Error("Gemini 응답이 비어 있습니다");

  const parsed = JSON.parse(text) as FortuneResult;
  const grade = String(parsed.grade ?? "").toUpperCase();

  return {
    grade: ALLOWED_GRADES.includes(grade) ? grade : "CLOUDY",
    summary: String(parsed.summary ?? "").slice(0, 40).trim(),
    advice: String(parsed.advice ?? "").slice(0, 80).trim(),
  };
}

serve(async (req) => {
  if (req.method === "OPTIONS") {
    return new Response("ok", { headers: corsHeaders });
  }

  const json = (body: unknown, status = 200) =>
    new Response(JSON.stringify(body), {
      status,
      headers: { ...corsHeaders, "Content-Type": "application/json" },
    });

  try {
    const { user_id } = await req.json();
    if (!user_id) return json({ error: "user_id required" }, 400);

    const supabaseUrl = Deno.env.get("SUPABASE_URL")!;
    const serviceRoleKey = Deno.env.get("SUPABASE_SERVICE_ROLE_KEY")!;
    const admin = createClient(supabaseUrl, serviceRoleKey, {
      auth: { autoRefreshToken: false, persistSession: false },
    });

    const date = todayKst();

    // 1. 당일 캐시 확인
    const { data: cached } = await admin
      .from("fortunes")
      .select("*")
      .eq("user_id", user_id)
      .eq("date", date)
      .maybeSingle();
    if (cached) return json(cached);

    // 2. 사주 계산에 필요한 사용자 정보 조회
    const { data: user, error: userError } = await admin
      .from("users")
      .select("birth_date, gender, birth_time")
      .eq("id", user_id)
      .maybeSingle();
    if (userError) throw userError;
    if (!user) return json({ error: "사용자를 찾을 수 없습니다" }, 404);

    // 3. Gemini 호출
    const prompt = buildPrompt(
      user.birth_date,
      user.gender,
      user.birth_time ?? null,
      date,
    );
    const result = await callGemini(prompt);

    // 4. 1일 보관: 이 유저의 과거 리포트 삭제
    await admin.from("fortunes").delete().eq("user_id", user_id).neq("date", date);

    // 5. 신규 리포트 저장 후 반환 (FortuneDto 형태)
    const { data: inserted, error: insertError } = await admin
      .from("fortunes")
      .insert({
        user_id,
        date,
        grade: result.grade,
        summary: result.summary,
        advice: result.advice,
      })
      .select("*")
      .single();
    if (insertError) throw insertError;

    return json(inserted);
  } catch (error) {
    console.error("fortune error:", error);
    return json({ error: (error as Error).message ?? "Internal server error" }, 500);
  }
});
