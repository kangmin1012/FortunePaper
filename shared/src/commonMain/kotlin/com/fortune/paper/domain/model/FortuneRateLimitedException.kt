package com.fortune.paper.domain.model

/**
 * Gemini 무료 티어 한도 초과(서버 429, body `{ "error": "RATE_LIMITED" }`)를 나타내는 도메인 예외.
 * 일반 오류와 달리 리포트 화면에서 "오늘 용지가 다 떨어졌어요" 다이얼로그로 안내한다.
 */
class FortuneRateLimitedException : Exception("RATE_LIMITED")
