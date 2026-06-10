-- v1.1 로컬 전환 (docs/spec-local-user-migration.md §6)
-- 서버 유저 DB 폐기: 유저 정보는 기기 로컬(DataStore)로, 운세 캐시는 로컬 캐시로 단일화.
-- fortunes가 users를 FK 참조하므로 fortunes를 먼저 DROP 한다.

drop table if exists public.fortunes;
drop table if exists public.users;
