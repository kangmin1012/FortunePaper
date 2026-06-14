// Supabase Edge Function: fortune (v1.1 — stateless)
// 요청의 사주 정보(생년월일·성별·태어난 시각)로 Gemini를 호출해 오늘의 운세 리포트를 반환한다.
//   - DB 미사용: 어떤 유저 데이터도 저장하지 않는다 (캐시는 클라이언트 로컬 DataStore가 담당)
//   - verify_jwt = false 로 배포 (Supabase Auth 제거 — 게이트웨이 apikey 검사는 유지)
//
// 요청:  { "birth_date": "1995-01-01", "gender": "MALE", "birth_time": "자" | null }
// 응답:  { "date": "2026-06-10", "grade": "SUNNY", "summary": "...", "advice": "..." }
//
// 필요한 Edge Function 환경변수:
//   GEMINI_API_KEY — Google AI Studio API 키 (필수)

const corsHeaders = {
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Headers":
    "authorization, x-client-info, apikey, content-type",
};

const ALLOWED_GRADES = ["SUNNY", "CLEAR", "CLOUDY", "RAINY", "STORM"];
const ALLOWED_GENDERS = ["MALE", "FEMALE"];
const ALLOWED_BIRTH_TIMES = [
  "자", "축", "인", "묘", "진", "사", "오", "미", "신", "유", "술", "해",
];
const GEMINI_MODEL = "gemini-2.5-flash";

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
  const genderKo = gender === "MALE" ? "남성" : "여성";
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

// Gemini 무료 티어 한도 초과(429) 전용 에러 — 클라이언트가 "용지 소진" 안내를 띄울 수 있도록 구분한다.
class RateLimitedError extends Error {}

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

  if (res.status === 429) {
    // 무료 티어 RPM/RPD 한도 초과 — 본문은 무시하고 전용 에러로 올린다.
    throw new RateLimitedError("RATE_LIMITED");
  }
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

Deno.serve(async (req) => {
  if (req.method === "OPTIONS") {
    return new Response("ok", { headers: corsHeaders });
  }

  const json = (body: unknown, status = 200) =>
    new Response(JSON.stringify(body), {
      status,
      headers: { ...corsHeaders, "Content-Type": "application/json" },
    });

  try {
    const { birth_date, gender, birth_time } = await req.json();

    if (!/^\d{4}-\d{2}-\d{2}$/.test(String(birth_date ?? ""))) {
      return json({ error: "birth_date(YYYY-MM-DD) required" }, 400);
    }
    if (!ALLOWED_GENDERS.includes(String(gender ?? ""))) {
      return json({ error: "gender(MALE|FEMALE) required" }, 400);
    }
    const birthTime = birth_time == null || birth_time === ""
      ? null
      : String(birth_time);
    if (birthTime !== null && !ALLOWED_BIRTH_TIMES.includes(birthTime)) {
      return json({ error: "birth_time must be one of 12지지 (자~해) or null" }, 400);
    }

    const date = todayKst();
    const result = await callGemini(
      buildPrompt(String(birth_date), String(gender), birthTime, date),
    );

    return json({
      date,
      grade: result.grade,
      summary: result.summary,
      advice: result.advice,
    });
  } catch (error) {
    if (error instanceof RateLimitedError) {
      // 429 + 구분 코드 → 앱이 "오늘 용지가 다 떨어졌어요" 다이얼로그를 띄운다.
      return json({ error: "RATE_LIMITED" }, 429);
    }
    console.error("fortune error:", error);
    return json({ error: (error as Error).message ?? "Internal server error" }, 500);
  }
});
