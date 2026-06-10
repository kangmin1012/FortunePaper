/* FortunePaper · 첫 실행 온보딩 9단계 화면
   각 스크린은 IOSDevice 안에 들어가는 100% height column이며,
   상단 진행 표시 → 본문 → 하단 CTA의 일관된 리듬을 따릅니다. */

const STEPS = ['welcome', 'value', 'name', 'birth', 'gender', 'time', 'notify'];
const TOTAL = STEPS.length;

const ASSETS = {
  SUNNY:  (window.__resources && window.__resources.gradeSunny)  || 'design-system/assets/grade-sunny.svg',
  CLEAR:  (window.__resources && window.__resources.gradeClear)  || 'design-system/assets/grade-clear.svg',
  CLOUDY: (window.__resources && window.__resources.gradeCloudy) || 'design-system/assets/grade-cloudy.svg',
  RAINY:  (window.__resources && window.__resources.gradeRainy)  || 'design-system/assets/grade-rainy.svg',
  STORM:  (window.__resources && window.__resources.gradeStorm)  || 'design-system/assets/grade-storm.svg',
};

// ─────────────────────── shared chrome ───────────────────────
function StepShell({ step, total, onBack, children, footer, showProgress = true, skip, onSkip }) {
  return (
    <div style={{ display: 'flex', flexDirection: 'column', height: '100%', background: 'var(--bg-primary)', paddingTop: 56, overflow: 'hidden', boxSizing: 'border-box' }}>
      {/* top bar */}
      <div style={{ height: 48, display: 'flex', alignItems: 'center', padding: '0 8px', gap: 8 }}>
        {onBack ? (
          <button onClick={onBack} aria-label="뒤로" style={{
            width: 40, height: 40, borderRadius: 999, background: 'transparent', border: 'none',
            display: 'flex', alignItems: 'center', justifyContent: 'center', cursor: 'pointer',
          }}>
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="var(--text-primary)" strokeWidth="2.2" strokeLinecap="round" strokeLinejoin="round">
              <path d="M15 18l-6-6 6-6"/>
            </svg>
          </button>
        ) : <div style={{ width: 40 }} />}

        {showProgress && (
          <div style={{ flex: 1, display: 'flex', justifyContent: 'center' }}>
            <ProgressDots step={step} total={total} />
          </div>
        )}
        {!showProgress && <div style={{ flex: 1 }} />}

        {skip ? (
          <button onClick={onSkip} style={{
            height: 40, padding: '0 12px', borderRadius: 999, background: 'transparent', border: 'none',
            color: 'var(--text-tertiary)', fontFamily: 'inherit', fontSize: 13, fontWeight: 500, cursor: 'pointer',
          }}>건너뛰기</button>
        ) : <div style={{ width: 40 }} />}
      </div>

      {/* body */}
      <div style={{ flex: 1, display: 'flex', flexDirection: 'column', padding: '0 24px', overflow: 'hidden' }}>
        {children}
      </div>

      {/* footer CTA area */}
      {footer && (
        <div style={{ padding: '8px 24px 28px' }}>
          {footer}
        </div>
      )}
    </div>
  );
}

function ProgressDots({ step, total }) {
  // For 9 steps we show a thin bar instead of 9 dots — cleaner.
  const pct = Math.max(0, Math.min(1, (step + 1) / total));
  return (
    <div style={{ width: 120, height: 4, background: 'var(--cream-300)', borderRadius: 999, overflow: 'hidden' }}>
      <div style={{
        width: `${pct * 100}%`, height: '100%', background: 'var(--blue-500)',
        borderRadius: 999, transition: 'width 360ms cubic-bezier(.2,.7,.2,1)',
      }} />
    </div>
  );
}

function Eyebrow({ children }) {
  return (
    <div style={{
      fontSize: 11, color: 'var(--text-tertiary)',
      letterSpacing: '0.12em', textTransform: 'uppercase', fontWeight: 500,
      marginBottom: 12,
    }}>{children}</div>
  );
}

function Title({ children, small }) {
  return (
    <div style={{
      fontSize: small ? 24 : 28, fontWeight: 700, color: 'var(--text-primary)',
      letterSpacing: '-0.02em', lineHeight: 1.3, textWrap: 'balance',
    }}>{children}</div>
  );
}

function Sub({ children }) {
  return (
    <div style={{
      fontSize: 14, color: 'var(--text-tertiary)', marginTop: 10, lineHeight: 1.6, textWrap: 'pretty',
    }}>{children}</div>
  );
}

// Kakao login button — follows Kakao Developers design guide.
//   container: #FEE500, radius 12px
//   symbol:    #000000 chat-bubble (shape/ratio/color is locked by guide)
//   label:     rgba(0,0,0,0.85), system font
function KakaoLoginButton({ onClick, label = '카카오 로그인' }) {
  return (
    <button
      onClick={onClick}
      style={{
        width: '100%', height: 50, borderRadius: 12,
        background: '#FEE500', border: 'none', cursor: 'pointer',
        display: 'flex', alignItems: 'center', justifyContent: 'center',
        position: 'relative', padding: '0 16px',
        fontFamily: 'inherit',
        WebkitTapHighlightColor: 'transparent',
      }}
    >
      {/* symbol — left-aligned per guide */}
      <span style={{ position: 'absolute', left: 18, top: '50%', transform: 'translateY(-50%)', display: 'flex' }}>
        <KakaoSymbol size={18} />
      </span>
      <span style={{
        fontSize: 15, fontWeight: 600, color: 'rgba(0,0,0,0.85)',
        letterSpacing: '-0.01em',
      }}>{label}</span>
    </button>
  );
}

function KakaoSymbol({ size = 18 }) {
  // Speech-bubble symbol; the guide locks its shape/ratio/color.
  return (
    <svg width={size} height={size} viewBox="0 0 18 18" aria-hidden="true">
      <path
        fill="#000000"
        d="M9 1.2C4.58 1.2 1 4.07 1 7.6c0 2.27 1.49 4.27 3.74 5.4l-.78 2.86c-.06.22.18.4.38.27l3.41-2.27c.4.05.82.07 1.25.07 4.42 0 8-2.86 8-6.4 0-3.53-3.58-6.4-8-6.4z"
      />
    </svg>
  );
}

// ─────────────────────────── 0. Welcome ───────────────────────────
function ScreenWelcome({ onNext, style = 'hero' }) {
  // Two style variants exposed via Tweaks:
  //  - 'hero': single SUNNY icon, brand wordmark, calm + centered (default)
  //  - 'parade': all 5 grade icons stacked on offset arcs, more editorial
  if (style === 'parade') {
    return (
      <div style={{ display: 'flex', flexDirection: 'column', height: '100%', background: 'var(--bg-primary)', overflow: 'hidden' }}>
        <div style={{ height: 64 }} />
        <div style={{ flex: 1, display: 'flex', flexDirection: 'column', alignItems: 'center', padding: '0 32px', minHeight: 0 }}>
          {/* hero icon — single clean focal point */}
          <img src={ASSETS.SUNNY} width="168" height="168"
            style={{ filter: 'drop-shadow(0 20px 32px rgba(255,184,0,.22))' }} alt="" />

          {/* supporting icons — tidy row, evenly spaced, no rotation */}
          <div style={{
            display: 'flex', gap: 14, alignItems: 'center', justifyContent: 'center',
            marginTop: 18,
          }}>
            {['CLEAR', 'CLOUDY', 'RAINY', 'STORM'].map((g) => (
              <img key={g} src={ASSETS[g]} width="44" height="44"
                style={{ filter: 'drop-shadow(0 6px 12px rgba(17,24,39,.08))' }} alt="" />
            ))}
          </div>

          {/* title block — left-aligned editorial feel */}
          <div style={{ alignSelf: 'stretch', marginTop: 'auto', paddingBottom: 8 }}>
            <div style={{ fontSize: 12, letterSpacing: '0.14em', color: 'var(--text-tertiary)', textTransform: 'uppercase', fontWeight: 600 }}>FortunePaper</div>
            <div style={{ fontSize: 32, fontWeight: 800, color: 'var(--text-primary)', letterSpacing: '-0.025em', marginTop: 8, lineHeight: 1.18 }}>
              한 장의 운세,<br/>매일 아침.
            </div>
            <div style={{ fontSize: 14, color: 'var(--text-tertiary)', marginTop: 12, lineHeight: 1.6 }}>
              사주를 기반으로 오늘의 흐름을<br/>다섯 단계의 날씨로 전해 드립니다.
            </div>
          </div>
        </div>
        <div style={{ padding: '12px 24px 28px' }}>
          <FPButton onClick={onNext}>시작하기</FPButton>
          <div style={{ fontSize: 11, color: 'var(--text-secondary)', textAlign: 'center', marginTop: 12 }}>
            계속하면 <span style={{ textDecoration: 'underline' }}>이용약관</span> 및 <span style={{ textDecoration: 'underline' }}>개인정보 처리방침</span>에 동의합니다.
          </div>
        </div>
      </div>
    );
  }

  // hero (default)
  return (
    <div style={{ display: 'flex', flexDirection: 'column', height: '100%', background: 'var(--bg-primary)', overflow: 'hidden' }}>
      <div style={{ height: 72 }} />
      <div style={{ flex: 1, display: 'flex', flexDirection: 'column', alignItems: 'center', padding: '0 24px', minHeight: 0 }}>
        <div style={{ fontSize: 12, letterSpacing: '0.16em', color: 'var(--text-tertiary)', textTransform: 'uppercase', fontWeight: 600 }}>
          FortunePaper
        </div>
        <div style={{ marginTop: 24, position: 'relative' }}>
          <img src={ASSETS.SUNNY} width="172" height="172"
            style={{ filter: 'drop-shadow(0 24px 36px rgba(255,184,0,.25))' }} alt="" />
        </div>
        <div style={{ fontSize: 28, fontWeight: 800, color: 'var(--text-primary)', letterSpacing: '-0.025em', textAlign: 'center', marginTop: 28, lineHeight: 1.25 }}>
          오늘의 흐름을<br/>한 장에 담아 드릴게요
        </div>
        <div style={{ fontSize: 14, color: 'var(--text-tertiary)', marginTop: 14, textAlign: 'center', lineHeight: 1.65, maxWidth: 280 }}>
          사주를 기반으로 매일 아침<br/>한 장의 운세 리포트를 보내 드립니다.
        </div>

        {/* tiny grade strip */}
        <div style={{ display: 'flex', gap: 10, marginTop: 22, alignItems: 'center' }}>
          {['SUNNY','CLEAR','CLOUDY','RAINY','STORM'].map((g) => (
            <img key={g} src={ASSETS[g]} width="28" height="28" style={{ opacity: 0.9 }} alt="" />
          ))}
        </div>
        <div style={{ fontSize: 11, color: 'var(--text-secondary)', marginTop: 10, letterSpacing: '0.06em' }}>
          5단계 날씨 등급
        </div>
      </div>
      <div style={{ padding: '8px 24px 28px' }}>
        <FPButton onClick={onNext}>시작하기</FPButton>
        <div style={{ fontSize: 11, color: 'var(--text-secondary)', textAlign: 'center', marginTop: 12, lineHeight: 1.5 }}>
          계속하면 <span style={{ textDecoration: 'underline' }}>이용약관</span> · <span style={{ textDecoration: 'underline' }}>개인정보 처리방침</span>에 동의합니다.
        </div>
      </div>
    </div>
  );
}

// ─────────────────────────── 1. Value props ───────────────────────────
function ScreenValue({ onNext, onBack, step, total }) {
  const items = [
    {
      icon: ASSETS.SUNNY,
      title: '한 장의 리포트',
      body: '스크롤 없이 오늘의 운세 흐름을 한 화면에 담아 드립니다.',
    },
    {
      icon: ASSETS.CLEAR,
      title: '사주 기반',
      body: '생년월일과 성별을 토대로 매일 새로운 등급을 계산합니다.',
    },
    {
      icon: ASSETS.CLOUDY,
      title: '매일 아침',
      body: '원하는 시간에 알림으로 오늘의 한 줄 요약을 전해 드립니다.',
    },
  ];
  return (
    <StepShell step={step} total={total} onBack={onBack}
      footer={<FPButton onClick={onNext}>다음</FPButton>}>
      <div style={{ paddingTop: 8, paddingBottom: 24 }}>
        <Eyebrow>포츈페이퍼란</Eyebrow>
        <Title>매일 한 장,<br/>오늘만큼의 위로.</Title>
      </div>
      <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
        {items.map((it, i) => (
          <div key={i} style={{
            background: 'var(--bg-surface)', borderRadius: 16, padding: '16px',
            boxShadow: 'var(--shadow-card)', display: 'flex', gap: 14, alignItems: 'center',
          }}>
            <img src={it.icon} width="56" height="56" alt="" />
            <div style={{ flex: 1, minWidth: 0 }}>
              <div style={{ fontSize: 15, fontWeight: 700, color: 'var(--text-primary)' }}>{it.title}</div>
              <div style={{ fontSize: 13, color: 'var(--text-tertiary)', marginTop: 4, lineHeight: 1.55 }}>
                {it.body}
              </div>
            </div>
          </div>
        ))}
      </div>
    </StepShell>
  );
}

// ─────────────────────────── 2. Name ───────────────────────────
function ScreenName({ onNext, onBack, step, total, value, onChange }) {
  const valid = value.trim().length >= 1;
  return (
    <StepShell step={step} total={total} onBack={onBack}
      footer={<FPButton onClick={onNext} disabled={!valid}>다음</FPButton>}>
      <div style={{ paddingTop: 12, paddingBottom: 28 }}>
        <Eyebrow>1 / 4 — 이름</Eyebrow>
        <Title>어떻게 불러<br/>드릴까요?</Title>
        <Sub>리포트 인사말과 알림에서만 사용됩니다.</Sub>
      </div>
      <div style={{ display: 'flex', flexDirection: 'column', gap: 6 }}>
        <div style={{ fontSize: 12, color: 'var(--text-secondary)' }}>이름</div>
        <input
          autoFocus
          value={value}
          onChange={(e) => onChange(e.target.value)}
          placeholder="예: 김민준"
          style={{
            height: 56, borderRadius: 14, padding: '0 16px',
            border: `1.5px solid ${value ? 'var(--blue-500)' : 'var(--border-default)'}`,
            background: 'var(--bg-surface)', fontSize: 18, fontFamily: 'inherit',
            color: 'var(--text-primary)', outline: 'none',
          }}
        />
        <div style={{ fontSize: 11, color: 'var(--text-secondary)', marginTop: 6 }}>
          한글 · 영문 · 숫자, 최대 12자
        </div>
      </div>
    </StepShell>
  );
}

// ─────────────────────────── 3. Birth date (wheel picker) ───────────────────────────
function ScreenBirth({ onNext, onBack, step, total, value, onChange }) {
  // value: { y, m, d }
  const years = []; for (let y = 1950; y <= 2010; y++) years.push(y);
  const months = []; for (let m = 1; m <= 12; m++) months.push(m);
  const days = []; for (let d = 1; d <= 31; d++) days.push(d);

  return (
    <StepShell step={step} total={total} onBack={onBack}
      footer={<FPButton onClick={onNext}>다음</FPButton>}>
      <div style={{ paddingTop: 12, paddingBottom: 16 }}>
        <Eyebrow>2 / 4 — 생년월일</Eyebrow>
        <Title>언제 태어나셨나요?</Title>
        <Sub>사주 계산에 사용되며 기기에만 안전하게 저장됩니다.</Sub>
      </div>

      <div style={{
        background: 'var(--bg-surface)', borderRadius: 20, padding: '8px 0',
        boxShadow: 'var(--shadow-card)', display: 'flex', position: 'relative',
        height: 196, alignItems: 'center', overflow: 'hidden', flexShrink: 0,
      }}>
        {/* selection band */}
        <div style={{
          position: 'absolute', left: 12, right: 12, top: 'calc(50% - 22px)', height: 44,
          background: 'var(--cream-200)', borderRadius: 10, pointerEvents: 'none',
        }} />
        {/* fades */}
        <div style={{ position: 'absolute', top: 0, left: 0, right: 0, height: 56,
          background: 'linear-gradient(180deg, rgba(255,255,255,1), rgba(255,255,255,0))', pointerEvents: 'none' }} />
        <div style={{ position: 'absolute', bottom: 0, left: 0, right: 0, height: 56,
          background: 'linear-gradient(0deg, rgba(255,255,255,1), rgba(255,255,255,0))', pointerEvents: 'none' }} />

        <Wheel items={years} suffix="년" value={value.y} onChange={(v) => onChange({ ...value, y: v })} />
        <Wheel items={months} suffix="월" value={value.m} onChange={(v) => onChange({ ...value, m: v })} pad />
        <Wheel items={days} suffix="일" value={value.d} onChange={(v) => onChange({ ...value, d: v })} />
      </div>

      <div style={{
        marginTop: 14, display: 'flex', gap: 6, alignItems: 'center', justifyContent: 'center',
        fontSize: 12, color: 'var(--text-tertiary)',
      }}>
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
          <rect x="3" y="11" width="18" height="11" rx="2"/><path d="M7 11V7a5 5 0 0110 0v4"/>
        </svg>
        정보는 외부로 전송되지 않습니다
      </div>
    </StepShell>
  );
}

// Visual iOS-style wheel. Click an adjacent item to spin.
function Wheel({ items, value, onChange, suffix = '', pad = false }) {
  const idx = Math.max(0, items.indexOf(value));
  const ITEM_H = 36;
  // Render items with vertical translate so `value` is centered.
  return (
    <div style={{
      flex: 1, height: '100%', position: 'relative', overflow: 'hidden',
      borderLeft: pad ? '1px solid transparent' : 'none',
      borderRight: pad ? '1px solid transparent' : 'none',
    }}>
      <div style={{
        position: 'absolute', left: 0, right: 0, top: '50%',
        transform: `translateY(calc(-${idx} * ${ITEM_H}px - ${ITEM_H/2}px))`,
        transition: 'transform 220ms cubic-bezier(.2,.7,.2,1)',
      }}>
        {items.map((it, i) => {
          const dist = Math.abs(i - idx);
          const opacity = dist === 0 ? 1 : dist === 1 ? 0.55 : dist === 2 ? 0.28 : 0.12;
          const scale = dist === 0 ? 1 : dist === 1 ? 0.92 : 0.85;
          return (
            <button key={it} onClick={() => onChange(it)} style={{
              display: 'block', width: '100%', height: ITEM_H, lineHeight: `${ITEM_H}px`,
              textAlign: 'center', background: 'transparent', border: 'none', cursor: 'pointer',
              fontFamily: 'inherit', fontSize: 18,
              fontWeight: dist === 0 ? 700 : 500,
              color: dist === 0 ? 'var(--text-primary)' : 'var(--text-tertiary)',
              opacity, transform: `scale(${scale})`, transition: 'opacity 180ms, transform 180ms',
            }}>
              {it}{suffix}
            </button>
          );
        })}
      </div>
      {/* spinner affordance arrows */}
    </div>
  );
}

// ─────────────────────────── 4. Gender ───────────────────────────
function ScreenGender({ onNext, onBack, step, total, value, onChange }) {
  return (
    <StepShell step={step} total={total} onBack={onBack}
      footer={<FPButton onClick={onNext} disabled={!value}>다음</FPButton>}>
      <div style={{ paddingTop: 12, paddingBottom: 28 }}>
        <Eyebrow>3 / 4 — 성별</Eyebrow>
        <Title>성별을 알려 주세요</Title>
        <Sub>사주의 음양 균형을 풀이하는 데 사용됩니다.</Sub>
      </div>

      <div style={{ display: 'flex', gap: 12 }}>
        {[
          { id: 'F', kr: '여성', mark: '음', accent: '#E8B4D2' },
          { id: 'M', kr: '남성', mark: '양', accent: '#FFD27A' },
        ].map((o) => {
          const active = value === o.id;
          return (
            <button key={o.id} onClick={() => onChange(o.id)} style={{
              flex: 1, borderRadius: 20, padding: '24px 18px 20px',
              background: 'var(--bg-surface)', border: active ? '2px solid var(--blue-500)' : '2px solid transparent',
              boxShadow: active ? 'var(--shadow-raised)' : 'var(--shadow-card)',
              cursor: 'pointer', fontFamily: 'inherit', textAlign: 'left',
              transition: 'transform 140ms, box-shadow 140ms',
              transform: active ? 'translateY(-2px)' : 'none',
            }}>
              <div style={{
                width: 56, height: 56, borderRadius: 999, background: o.accent,
                display: 'flex', alignItems: 'center', justifyContent: 'center',
                fontSize: 24, fontWeight: 700, color: 'rgba(0,0,0,0.5)',
              }}>{o.mark}</div>
              <div style={{ fontSize: 20, fontWeight: 700, color: 'var(--text-primary)', marginTop: 18 }}>{o.kr}</div>
              <div style={{ fontSize: 12, color: 'var(--text-tertiary)', marginTop: 4 }}>
                {o.id === 'F' ? '여성 사주' : '남성 사주'}
              </div>
            </button>
          );
        })}
      </div>

      <div style={{ marginTop: 24, padding: 14, borderRadius: 12, background: 'var(--cream-300)' }}>
        <div style={{ fontSize: 12, color: 'var(--text-tertiary)', lineHeight: 1.6 }}>
          사주에서는 음(여)·양(남)의 흐름이 다르게 풀이됩니다.
          답변하지 않으시면 일반 흐름으로 계산됩니다.
        </div>
      </div>
    </StepShell>
  );
}

// ─────────────────────────── 5. Birth time (optional) ───────────────────────────
function ScreenTime({ onNext, onBack, step, total, value, onChange }) {
  const hours = [
    { id: '자', range: '23–01시' }, { id: '축', range: '01–03시' },
    { id: '인', range: '03–05시' }, { id: '묘', range: '05–07시' },
    { id: '진', range: '07–09시' }, { id: '사', range: '09–11시' },
    { id: '오', range: '11–13시' }, { id: '미', range: '13–15시' },
    { id: '신', range: '15–17시' }, { id: '유', range: '17–19시' },
    { id: '술', range: '19–21시' }, { id: '해', range: '21–23시' },
  ];
  return (
    <StepShell step={step} total={total} onBack={onBack}
      skip onSkip={() => { onChange(null); onNext(); }}
      footer={<FPButton onClick={onNext}>다음</FPButton>}>
      <div style={{ paddingTop: 12, paddingBottom: 20 }}>
        <Eyebrow>4 / 4 — 태어난 시각 (선택)</Eyebrow>
        <Title>몇 시에 태어났는지<br/>알고 계신가요?</Title>
        <Sub>모르셔도 괜찮아요. 더 정확한 풀이가 필요할 때만 선택해 주세요.</Sub>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: 8 }}>
        {hours.map((h) => {
          const active = value === h.id;
          return (
            <button key={h.id} onClick={() => onChange(h.id)} style={{
              padding: '10px 4px 12px', borderRadius: 12,
              background: active ? 'var(--blue-500)' : 'var(--bg-surface)',
              border: 'none', cursor: 'pointer', fontFamily: 'inherit',
              boxShadow: active ? 'none' : 'var(--shadow-card)',
              display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 4,
              transition: 'all 140ms',
            }}>
              <div style={{ fontSize: 18, fontWeight: 700, color: active ? '#fff' : 'var(--text-primary)' }}>{h.id}시</div>
              <div style={{ fontSize: 10, color: active ? 'rgba(255,255,255,.8)' : 'var(--text-tertiary)' }}>{h.range}</div>
            </button>
          );
        })}
      </div>

      <button onClick={() => { onChange(null); }} style={{
        marginTop: 16, padding: '12px', borderRadius: 12, background: 'transparent',
        border: `1px dashed ${value === null ? 'var(--blue-500)' : 'var(--border-default)'}`,
        color: value === null ? 'var(--blue-500)' : 'var(--text-tertiary)',
        fontFamily: 'inherit', fontSize: 13, fontWeight: 500, cursor: 'pointer',
      }}>잘 모르겠어요</button>
    </StepShell>
  );
}

// ─────────────────────────── 6. Notification time ───────────────────────────
function ScreenNotify({ onNext, onBack, step, total, value, onChange }) {
  const presets = [
    { t: '06:30', label: '이른 아침' },
    { t: '07:30', label: '출근 전' },
    { t: '08:30', label: '아침 시간' },
    { t: '09:30', label: '느긋한 아침' },
  ];
  return (
    <StepShell step={step} total={total} onBack={onBack}
      footer={<FPButton onClick={onNext}>완료하기</FPButton>}>
      <div style={{ paddingTop: 12, paddingBottom: 18 }}>
        <Eyebrow>마지막 — 알림</Eyebrow>
        <Title>리포트를 언제<br/>받아 보시겠어요?</Title>
        <Sub>설정한 시간에 매일 한 번, 오늘의 한 줄 요약을 알려 드립니다.</Sub>
      </div>

      <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
        {presets.map((p) => {
          const active = value === p.t;
          return (
            <button key={p.t} onClick={() => onChange(p.t)} style={{
              display: 'flex', alignItems: 'center', gap: 14, padding: '14px 16px',
              borderRadius: 16, background: 'var(--bg-surface)',
              border: active ? '2px solid var(--blue-500)' : '2px solid transparent',
              boxShadow: 'var(--shadow-card)', cursor: 'pointer', fontFamily: 'inherit',
              textAlign: 'left',
            }}>
              <div style={{
                width: 40, height: 40, borderRadius: 999,
                background: active ? 'var(--blue-500)' : 'var(--cream-300)',
                color: active ? '#fff' : 'var(--text-tertiary)',
                display: 'flex', alignItems: 'center', justifyContent: 'center',
              }}>
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                  <path d="M6 8a6 6 0 0 1 12 0c0 7 3 9 3 9H3s3-2 3-9"/><path d="M10.3 21a1.94 1.94 0 0 0 3.4 0"/>
                </svg>
              </div>
              <div style={{ flex: 1 }}>
                <div style={{ fontSize: 18, fontWeight: 700, color: 'var(--text-primary)', fontFeatureSettings: '"tnum" 1' }}>
                  오전 {p.t}
                </div>
                <div style={{ fontSize: 12, color: 'var(--text-tertiary)', marginTop: 2 }}>{p.label}</div>
              </div>
              <div style={{
                width: 22, height: 22, borderRadius: 999,
                border: active ? 'none' : '1.5px solid var(--border-default)',
                background: active ? 'var(--blue-500)' : 'transparent',
                display: 'flex', alignItems: 'center', justifyContent: 'center',
              }}>
                {active && (
                  <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="#fff" strokeWidth="3.5" strokeLinecap="round" strokeLinejoin="round">
                    <path d="M20 6L9 17l-5-5"/>
                  </svg>
                )}
              </div>
            </button>
          );
        })}
      </div>

      <div style={{ marginTop: 14, fontSize: 12, color: 'var(--text-tertiary)', textAlign: 'center', lineHeight: 1.5 }}>
        시간은 설정에서 언제든지 바꿀 수 있습니다.
      </div>
    </StepShell>
  );
}

// ─────────────────────────── 7. Calculating (loading) ───────────────────────────
function ScreenCalc({ onNext, name }) {
  React.useEffect(() => {
    const t = setTimeout(onNext, 2400);
    return () => clearTimeout(t);
  }, [onNext]);

  const [phase, setPhase] = React.useState(0);
  React.useEffect(() => {
    const id = setInterval(() => setPhase((p) => (p + 1) % 3), 600);
    return () => clearInterval(id);
  }, []);

  const phases = ['음양을 살피는 중', '오늘의 흐름을 짚는 중', '한 장으로 정리하는 중'];

  return (
    <div style={{ display: 'flex', flexDirection: 'column', height: '100%', background: 'var(--bg-primary)', overflow: 'hidden' }}>
      <div style={{ height: 64 }} />
      <div style={{ flex: 1, display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', padding: '0 24px', minHeight: 0 }}>
        {/* orbiting clay icons */}
        <div style={{ position: 'relative', width: 220, height: 220 }}>
          <div style={{
            position: 'absolute', inset: 0, animation: 'fp-spin 8s linear infinite',
          }}>
            {['SUNNY','CLEAR','CLOUDY','RAINY','STORM'].map((g, i) => {
              const angle = (i / 5) * Math.PI * 2;
              const r = 88;
              const cx = 110 + Math.cos(angle - Math.PI/2) * r - 24;
              const cy = 110 + Math.sin(angle - Math.PI/2) * r - 24;
              return (
                <img key={g} src={ASSETS[g]} width="48" height="48"
                  style={{ position: 'absolute', left: cx, top: cy, filter: 'drop-shadow(0 6px 14px rgba(17,24,39,.10))' }} alt="" />
              );
            })}
          </div>
          <div style={{
            position: 'absolute', inset: 0, display: 'flex', alignItems: 'center', justifyContent: 'center',
          }}>
            <img src={ASSETS.SUNNY} width="96" height="96"
              style={{ filter: 'drop-shadow(0 16px 28px rgba(255,184,0,.30))', animation: 'fp-pulse 1.6s ease-in-out infinite' }} alt="" />
          </div>
        </div>

        <div style={{ fontSize: 22, fontWeight: 700, color: 'var(--text-primary)', marginTop: 36, letterSpacing: '-0.02em' }}>
          {name} 님의 사주
        </div>
        <div style={{ fontSize: 14, color: 'var(--text-tertiary)', marginTop: 8, minHeight: 22 }}>
          {phases[phase]}<span style={{ display: 'inline-block', width: 18, textAlign: 'left' }}>{'.'.repeat(phase + 1)}</span>
        </div>

        <div style={{ width: 160, height: 4, background: 'var(--cream-300)', borderRadius: 999, marginTop: 28, overflow: 'hidden' }}>
          <div style={{
            height: '100%', background: 'var(--blue-500)', borderRadius: 999,
            animation: 'fp-bar 2.4s cubic-bezier(.2,.7,.2,1) forwards',
          }} />
        </div>
      </div>
      <div style={{ height: 60 }} />
    </div>
  );
}

// ─────────────────────────── 8. First reveal ───────────────────────────
function ScreenReveal({ onDone, name }) {
  const [show, setShow] = React.useState(false);
  React.useEffect(() => { const t = setTimeout(() => setShow(true), 80); return () => clearTimeout(t); }, []);

  return (
    <div style={{ display: 'flex', flexDirection: 'column', height: '100%', background: 'var(--bg-primary)', overflow: 'hidden' }}>
      <div style={{ height: 56, paddingTop: 56, display: 'flex', alignItems: 'flex-end', justifyContent: 'center', boxSizing: 'content-box', flexShrink: 0 }}>
        <div style={{ fontSize: 11, color: 'var(--text-tertiary)', letterSpacing: '0.12em', textTransform: 'uppercase', fontWeight: 600 }}>
          준비 완료
        </div>
      </div>
      <div style={{
        flex: 1, display: 'flex', flexDirection: 'column', alignItems: 'center',
        padding: '0 36px', opacity: show ? 1 : 0, transform: show ? 'translateY(0)' : 'translateY(12px)',
        transition: 'opacity 360ms cubic-bezier(.2,.7,.2,1), transform 360ms cubic-bezier(.2,.7,.2,1)',
        minHeight: 0,
      }}>
        <div style={{ fontSize: 22, fontWeight: 700, color: 'var(--text-primary)', textAlign: 'center', letterSpacing: '-0.02em', marginTop: 10, lineHeight: 1.3 }}>
          {name} 님의 첫 번째<br/>리포트가 도착했어요
        </div>

        {/* Card preview */}
        <div style={{
          marginTop: 18, width: '100%', background: 'var(--bg-surface)', borderRadius: 24,
          boxShadow: 'var(--shadow-raised)', padding: '18px 16px 20px', position: 'relative',
        }}>
          <div style={{
            fontSize: 11, letterSpacing: '0.08em', color: 'var(--text-tertiary)',
            textTransform: 'uppercase', textAlign: 'center',
          }}>2026. 05. 23 · 토요일</div>

          <div style={{ display: 'flex', justifyContent: 'center', marginTop: 10 }}>
            <img src={ASSETS.SUNNY} width="112" height="112" style={{ filter: 'drop-shadow(0 16px 24px rgba(255,184,0,.22))' }} alt="" />
          </div>

          <div style={{
            fontSize: 28, fontWeight: 800, color: '#C99000',
            textAlign: 'center', marginTop: 8, letterSpacing: '-0.02em',
          }}>화창</div>

          <div style={{
            fontSize: 13, color: 'var(--text-tertiary)', textAlign: 'center', marginTop: 6,
            lineHeight: 1.55, textWrap: 'pretty', padding: '0 4px',
          }}>
            적극적으로 도전하세요.<br/>모든 게 잘 풀리는 날입니다.
          </div>

          <div style={{
            display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: 8, marginTop: 14,
            paddingTop: 12, borderTop: '1px solid var(--cream-300)',
          }}>
            {[
              { k: '색', v: '코발트', sw: '#3B82F6' },
              { k: '방향', v: '남동' },
              { k: '숫자', v: '7' },
            ].map((it, i) => (
              <div key={i} style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 6 }}>
                <div style={{ fontSize: 10, color: 'var(--text-tertiary)', letterSpacing: '0.06em', textTransform: 'uppercase' }}>{it.k}</div>
                {it.sw && <div style={{ width: 16, height: 16, borderRadius: 4, background: it.sw }} />}
                <div style={{ fontSize: 14, fontWeight: 700, color: 'var(--text-primary)' }}>{it.v}</div>
              </div>
            ))}
          </div>
        </div>
      </div>
      <div style={{ padding: '12px 24px 28px', flexShrink: 0 }}>
        <FPButton onClick={onDone}>오늘의 리포트 보기</FPButton>
      </div>
    </div>
  );
}

Object.assign(window, {
  STEPS, TOTAL, Wheel, ScreenWelcome, ScreenValue, ScreenName, ScreenBirth,
  ScreenGender, ScreenTime, ScreenNotify, ScreenCalc, ScreenReveal,
});
