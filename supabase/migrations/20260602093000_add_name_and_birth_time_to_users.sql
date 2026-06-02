-- ============================================================
-- 마이그레이션: users 테이블에 name · birth_time 컬럼 추가
-- 배경: 온보딩 디자인(Task 3.5, A안 — 디자인 우선) 반영
--   - name:       리포트 인사말·알림용 표시 이름 (필수, 최대 12자)
--   - birth_time: 태어난 시각 12시진(자~해), null이면 정오 대표값 (선택)
-- 참고: .claude/rules/prd.md §8 · architecture.md DB 스키마
-- ============================================================

-- ---------- name (필수) ----------
-- 기존 행(테스트 계정 등)이 있을 수 있으므로 nullable 로 추가 → 백필 → NOT NULL 승격 순으로 적용.
alter table public.users
  add column if not exists name text;

-- 기존 행에 placeholder 백필 (NOT NULL 제약 위반 방지)
update public.users
  set name = '사용자'
  where name is null;

alter table public.users
  alter column name set not null;

-- 길이 가드 (최대 12자, 공백만 입력 방지)
do $$
begin
  if not exists (
    select 1 from pg_constraint where conname = 'users_name_length_check'
  ) then
    alter table public.users
      add constraint users_name_length_check
      check (char_length(btrim(name)) between 1 and 12);
  end if;
end $$;

-- ---------- birth_time (선택) ----------
alter table public.users
  add column if not exists birth_time text;

-- 12시진(자~해)만 허용, null 허용
do $$
begin
  if not exists (
    select 1 from pg_constraint where conname = 'users_birth_time_check'
  ) then
    alter table public.users
      add constraint users_birth_time_check
      check (
        birth_time is null
        or birth_time in ('자','축','인','묘','진','사','오','미','신','유','술','해')
      );
  end if;
end $$;

-- ---------- 컬럼 코멘트 (문서화) ----------
comment on column public.users.name is '표시 이름. 리포트 인사말·알림용 (필수, 1~12자)';
comment on column public.users.birth_time is '태어난 시각 12시진(자~해). null이면 사주 계산 시 정오 대표값 사용 (선택)';
