// Supabase Edge Function: kakao-auth
// Kakao 액세스 토큰을 검증하고 Supabase 세션을 발급합니다.
//
// 필요한 Supabase Edge Function 환경변수:
//   KAKAO_AUTH_SECRET — 비밀번호 파생용 HMAC 시크릿 (임의의 긴 문자열)
//   (SUPABASE_URL, SUPABASE_ANON_KEY, SUPABASE_SERVICE_ROLE_KEY는 자동 주입)

import { serve } from "https://deno.land/std@0.208.0/http/server.ts";
import { createClient } from "https://esm.sh/@supabase/supabase-js@2";

const corsHeaders = {
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Headers":
    "authorization, x-client-info, apikey, content-type",
};

async function derivePassword(kakaoId: string): Promise<string> {
  const secret = Deno.env.get("KAKAO_AUTH_SECRET") ?? "fortune-paper-secret";
  const key = await crypto.subtle.importKey(
    "raw",
    new TextEncoder().encode(secret),
    { name: "HMAC", hash: "SHA-256" },
    false,
    ["sign"]
  );
  const signature = await crypto.subtle.sign(
    "HMAC",
    key,
    new TextEncoder().encode(kakaoId)
  );
  return btoa(String.fromCharCode(...new Uint8Array(signature))).slice(0, 32);
}

serve(async (req) => {
  if (req.method === "OPTIONS") {
    return new Response("ok", { headers: corsHeaders });
  }

  try {
    const { access_token } = await req.json();
    if (!access_token) {
      return new Response(JSON.stringify({ error: "access_token required" }), {
        status: 400,
        headers: { ...corsHeaders, "Content-Type": "application/json" },
      });
    }

    // 1. Kakao 토큰 검증 및 사용자 정보 조회
    const kakaoRes = await fetch("https://kapi.kakao.com/v2/user/me", {
      headers: { Authorization: `Bearer ${access_token}` },
    });
    if (!kakaoRes.ok) {
      return new Response(JSON.stringify({ error: "Invalid Kakao token" }), {
        status: 401,
        headers: { ...corsHeaders, "Content-Type": "application/json" },
      });
    }
    const kakaoUser = await kakaoRes.json();
    const kakaoId = String(kakaoUser.id);
    const email = `kakao_${kakaoId}@fortune.paper`;
    const password = await derivePassword(kakaoId);

    const supabaseUrl = Deno.env.get("SUPABASE_URL")!;
    const anonKey = Deno.env.get("SUPABASE_ANON_KEY")!;
    const serviceRoleKey = Deno.env.get("SUPABASE_SERVICE_ROLE_KEY")!;

    // 2. 로그인 시도 (기존 사용자)
    const authClient = createClient(supabaseUrl, anonKey);
    let { data: signInData, error: signInError } =
      await authClient.auth.signInWithPassword({ email, password });

    if (signInError) {
      // 신규 사용자: Supabase Auth 계정 생성
      const adminClient = createClient(supabaseUrl, serviceRoleKey, {
        auth: { autoRefreshToken: false, persistSession: false },
      });
      const { error: createError } =
        await adminClient.auth.admin.createUser({
          email,
          password,
          email_confirm: true,
          user_metadata: { kakao_id: kakaoId },
          app_metadata: { provider: "kakao", providers: ["kakao"] },
        });
      if (createError) throw createError;

      // 생성 후 재로그인
      const result = await authClient.auth.signInWithPassword({
        email,
        password,
      });
      signInData = result.data;
      if (result.error) throw result.error;
    }

    const session = signInData.session!;
    return new Response(
      JSON.stringify({
        access_token: session.access_token,
        refresh_token: session.refresh_token,
        expires_in: session.expires_in,
        kakao_id: kakaoId,
      }),
      { headers: { ...corsHeaders, "Content-Type": "application/json" } }
    );
  } catch (error) {
    console.error("kakao-auth error:", error);
    return new Response(
      JSON.stringify({ error: error.message ?? "Internal server error" }),
      {
        status: 500,
        headers: { ...corsHeaders, "Content-Type": "application/json" },
      }
    );
  }
});
