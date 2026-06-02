/* FortunePaper · Daily Entry
   기존 유저가 그 날 처음 앱을 열었을 때 보는 진입 화면.

   3-state flow:
     awaiting   — 오늘 리포트가 아직 봉인된 상태. 유저가 버튼을 눌러야 계산이 시작됨.
     calculating — 사주 계산 중 (애니메이션)
     revealed   — 오늘의 리포트 (TodayScreen)

   상단 NavBar 와 하단 TabBar는 세 상태 내내 유지되어 일관된 홈 컨텍스트를 줍니다. */

const DE_ASSETS = {
  SUNNY:  'design-system/assets/grade-sunny.svg',
  CLEAR:  'design-system/assets/grade-clear.svg',
  CLOUDY: 'design-system/assets/grade-cloudy.svg',
  RAINY:  'design-system/assets/grade-rainy.svg',
  STORM:  'design-system/assets/grade-storm.svg',
};

// ─────────────── home shell (NavBar + body + TabBar) ───────────────
function HomeShell({ title, children, trailing }) {
  return (
    <div style={{ display: 'flex', flexDirection: 'column', height: '100%', background: 'var(--bg-primary)' }}>
      <div style={{ height: 56 }} />
      <div style={{
        height: 48, display: 'flex', alignItems: 'center', padding: '0 12px', gap: 4,
      }}>
        <div style={{ width: 40 }} />
        <div style={{
          flex: 1, textAlign: 'center', fontSize: 16, fontWeight: 600,
          color: 'var(--text-primary)',
        }}>{title}</div>
        {trailing || <div style={{ width: 40 }} />}
      </div>
      <div style={{ flex: 1, minHeight: 0, overflow: 'hidden' }}>{children}</div>
      <DETabBar />
    </div>
  );
}

function DETabBar() {
  const tabs = [
    { id: 'today',    kr: '오늘',  active: true,  d: 'M3 11.5L12 4l9 7.5V20a1 1 0 01-1 1h-5v-6h-6v6H4a1 1 0 01-1-1z' },
    { id: 'settings', kr: '설정',  d: 'M12 15a3 3 0 100-6 3 3 0 000 6zm7-3a7 7 0 00-.1-1.2l2-1.5-2-3.4-2.3.9a7 7 0 00-2-1.2L14 3h-4l-.6 2.6a7 7 0 00-2 1.2L5.1 5.9l-2 3.4 2 1.5A7 7 0 005 12c0 .4 0 .8.1 1.2l-2 1.5 2 3.4 2.3-.9a7 7 0 002 1.2L10 21h4l.6-2.6a7 7 0 002-1.2l2.3.9 2-3.4-2-1.5c.1-.4.1-.8.1-1.2z' },
  ];
  return (
    <div style={{
      display: 'flex', justifyContent: 'space-around', alignItems: 'center',
      background: 'var(--bg-surface)', borderTop: '1px solid var(--cream-300)',
      padding: '8px 8px 18px', flexShrink: 0,
    }}>
      {tabs.map((t) => {
        const c = t.active ? 'var(--blue-500)' : 'var(--text-secondary)';
        return (
          <div key={t.id} style={{
            flex: 1, display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 4, color: c,
          }}>
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none"
              stroke={c} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              <path d={t.d} />
            </svg>
            <div style={{ fontSize: 11, fontWeight: t.active ? 600 : 500 }}>{t.kr}</div>
          </div>
        );
      })}
    </div>
  );
}

function SettingsIcon() {
  return (
    <button style={{
      width: 40, height: 40, borderRadius: 999, background: 'transparent', border: 'none',
      display: 'flex', alignItems: 'center', justifyContent: 'center', cursor: 'pointer',
    }}>
      <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="var(--text-primary)" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
        <circle cx="12" cy="12" r="3"/>
        <path d="M19.4 15a1.7 1.7 0 0 0 .3 1.8l.1.1a2 2 0 0 1-2.8 2.8l-.1-.1a1.7 1.7 0 0 0-1.8-.3 1.7 1.7 0 0 0-1 1.5V21a2 2 0 1 1-4 0v-.1a1.7 1.7 0 0 0-1.1-1.5 1.7 1.7 0 0 0-1.8.3l-.1.1a2 2 0 0 1-2.8-2.8l.1-.1a1.7 1.7 0 0 0 .3-1.8 1.7 1.7 0 0 0-1.5-1H3a2 2 0 1 1 0-4h.1a1.7 1.7 0 0 0 1.5-1 1.7 1.7 0 0 0-.3-1.8l-.1-.1a2 2 0 1 1 2.8-2.8l.1.1a1.7 1.7 0 0 0 1.8.3h0a1.7 1.7 0 0 0 1-1.5V3a2 2 0 0 1 4 0v.1a1.7 1.7 0 0 0 1 1.5h0a1.7 1.7 0 0 0 1.8-.3l.1-.1a2 2 0 1 1 2.8 2.8l-.1.1a1.7 1.7 0 0 0-.3 1.8v0a1.7 1.7 0 0 0 1.5 1H21a2 2 0 1 1 0 4h-.1a1.7 1.7 0 0 0-1.5 1z"/>
      </svg>
    </button>
  );
}

// ─────────────── 1. Awaiting state ───────────────
function AwaitingScreen({ onOpen, name, dateText, dow, streak, style = 'paper' }) {
  // Visual is a sealed paper "fortune slip" centered, with ghost grade icons
  // peeking out from behind it. Animated subtle float + the seal pulses.
  return (
    <div style={{
      display: 'flex', flexDirection: 'column', height: '100%', padding: '0 24px',
    }}>
      {/* greeting strip */}
      <div style={{
        display: 'flex', alignItems: 'baseline', gap: 8, padding: '4px 0 0',
      }}>
        <div style={{
          fontSize: 22, fontWeight: 700, color: 'var(--text-primary)', letterSpacing: '-0.02em',
        }}>안녕하세요, {name} 님</div>
      </div>
      <div style={{ fontSize: 13, color: 'var(--text-tertiary)', marginTop: 4 }}>
        {dateText} · {dow}
      </div>

      {/* visual */}
      <div style={{ flex: 1, position: 'relative', display: 'flex', alignItems: 'center', justifyContent: 'center', minHeight: 0 }}>
        {style === 'paper' ? <PaperSeal /> : <FanStack />}
      </div>

      {/* copy + CTA */}
      <div style={{ paddingBottom: 28 }}>
        <div style={{
          fontSize: 24, fontWeight: 800, color: 'var(--text-primary)',
          letterSpacing: '-0.02em', textAlign: 'center', lineHeight: 1.3,
        }}>
          오늘의 리포트가<br/>도착했어요
        </div>
        <div style={{
          fontSize: 14, color: 'var(--text-tertiary)', textAlign: 'center',
          marginTop: 10, lineHeight: 1.6, textWrap: 'pretty',
        }}>
          버튼을 눌러 오늘의 흐름을 펼쳐 보세요.
        </div>

        {/* streak badge */}
        {streak > 0 && (
          <div style={{
            display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 6,
            marginTop: 14,
          }}>
            <div style={{
              display: 'inline-flex', alignItems: 'center', gap: 6, padding: '6px 12px',
              borderRadius: 999, background: 'var(--cream-300)',
              fontSize: 12, fontWeight: 600, color: 'var(--text-primary)',
            }}>
              <svg width="14" height="14" viewBox="0 0 24 24" fill="#F59E0B" stroke="none">
                <path d="M13 2c.5 4-2 6-4 8-2 2-3 4-3 7a8 8 0 0 0 16 0c0-3-1-5-3-7-2-2-5-4-6-8z"/>
              </svg>
              {streak}일 연속 받기 중
            </div>
          </div>
        )}

        <div style={{ marginTop: 16 }}>
          <FPButton onClick={onOpen}>오늘의 리포트 열기</FPButton>
        </div>
      </div>
    </div>
  );
}

// Paper-fortune-slip metaphor: a tilted white card sealed with a wax stamp,
// with three grade icons peeking out from behind it.
function PaperSeal() {
  return (
    <div style={{ position: 'relative', width: 240, height: 320 }}>
      {/* ghost icons behind the card */}
      <img src={DE_ASSETS.CLEAR} width="80" height="80" alt=""
        style={{ position: 'absolute', left: -28, top: 32, opacity: 0.55, transform: 'rotate(-12deg)', filter: 'drop-shadow(0 8px 16px rgba(17,24,39,.08))' }} />
      <img src={DE_ASSETS.CLOUDY} width="68" height="68" alt=""
        style={{ position: 'absolute', right: -16, top: 16, opacity: 0.5, transform: 'rotate(10deg)', filter: 'drop-shadow(0 8px 16px rgba(17,24,39,.08))' }} />
      <img src={DE_ASSETS.RAINY} width="56" height="56" alt=""
        style={{ position: 'absolute', right: -22, bottom: 36, opacity: 0.45, transform: 'rotate(14deg)', filter: 'drop-shadow(0 8px 16px rgba(17,24,39,.08))' }} />

      {/* the slip */}
      <div style={{
        position: 'absolute', left: '50%', top: '50%',
        width: 168, height: 248,
        transform: 'translate(-50%, -50%) rotate(-3deg)',
        background: 'var(--bg-surface)', borderRadius: 14,
        boxShadow: '0 18px 36px rgba(17,24,39,.10), 0 4px 10px rgba(17,24,39,.04)',
        display: 'flex', flexDirection: 'column',
        animation: 'de-float 5s ease-in-out infinite',
      }}>
        {/* top thin accent */}
        <div style={{
          height: 6, borderRadius: '14px 14px 0 0',
          background: 'var(--blue-500)',
        }} />

        {/* content */}
        <div style={{ flex: 1, padding: '20px 18px 18px', display: 'flex', flexDirection: 'column' }}>
          <div style={{
            fontSize: 10, letterSpacing: '0.18em', color: 'var(--text-tertiary)',
            textTransform: 'uppercase', fontWeight: 700,
          }}>오늘의 리포트</div>

          <div style={{
            fontSize: 40, fontWeight: 800, color: 'var(--text-primary)',
            letterSpacing: '-0.04em', marginTop: 14, lineHeight: 1.0,
            fontFeatureSettings: '"tnum" 1',
          }}>05.24</div>
          <div style={{
            fontSize: 14, color: 'var(--text-tertiary)', marginTop: 4,
          }}>일요일</div>

          {/* divider */}
          <div style={{ height: 1, background: 'var(--cream-300)', margin: '18px 0' }} />

          {/* 5-grade pip strip — muted, hinting at what's inside */}
          <div style={{ display: 'flex', gap: 6 }}>
            {[1,2,3,4,5].map((i) => (
              <div key={i} style={{
                width: 14, height: 14, borderRadius: 999,
                background: 'var(--cream-300)',
              }} />
            ))}
          </div>
          <div style={{ fontSize: 10, color: 'var(--text-tertiary)', marginTop: 8, letterSpacing: '0.04em' }}>
            5단계 · 미공개
          </div>

          {/* wax seal */}
          <div style={{
            marginTop: 'auto', display: 'flex', justifyContent: 'flex-end',
          }}>
            <div style={{
              width: 52, height: 52, borderRadius: 999,
              background: 'radial-gradient(circle at 35% 35%, #5B9CFF, #2563EB)',
              boxShadow: '0 4px 10px rgba(37,99,235,.35), inset 0 -2px 4px rgba(0,0,0,.15)',
              display: 'flex', alignItems: 'center', justifyContent: 'center',
              transform: 'rotate(-8deg)',
              animation: 'de-seal-pulse 2.2s ease-in-out infinite',
            }}>
              <div style={{
                fontFamily: 'Georgia, serif', fontSize: 22, fontWeight: 700,
                color: '#fff', letterSpacing: '-0.04em',
              }}>FP</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

// Alternative metaphor exposed via Tweaks — a fan of 5 grade icons stacked
function FanStack() {
  return (
    <div style={{ position: 'relative', width: 280, height: 280 }}>
      {['STORM', 'RAINY', 'CLOUDY', 'CLEAR', 'SUNNY'].map((g, i) => {
        const angle = (i - 2) * 8; // -16 → +16 deg
        const offsetY = i * -4;
        const z = i;
        const opacity = i === 4 ? 1 : 0.85 - (4 - i) * 0.05;
        return (
          <img key={g} src={DE_ASSETS[g]} width="160" height="160" alt=""
            style={{
              position: 'absolute', left: '50%', top: '50%',
              transform: `translate(-50%, -50%) translateY(${offsetY}px) rotate(${angle}deg)`,
              filter: 'drop-shadow(0 12px 24px rgba(17,24,39,.10))',
              opacity, zIndex: z,
            }} />
        );
      })}
      {/* Overlay veil to suggest "sealed" */}
      <div style={{
        position: 'absolute', inset: 0,
        background: 'radial-gradient(circle at 50% 50%, rgba(246,245,238,0), rgba(246,245,238,0.55) 70%)',
        pointerEvents: 'none',
      }} />
      <div style={{
        position: 'absolute', left: '50%', top: '50%', transform: 'translate(-50%, -50%)',
        padding: '8px 14px', borderRadius: 999, background: 'rgba(17,24,39,0.78)',
        color: '#fff', fontSize: 12, fontWeight: 600, letterSpacing: '0.04em',
      }}>오늘 · 미공개</div>
    </div>
  );
}

// ─────────────── 2. Calculating ───────────────
function CalculatingScreen({ name, onDone }) {
  React.useEffect(() => {
    const t = setTimeout(onDone, 2200);
    return () => clearTimeout(t);
  }, [onDone]);
  const [phase, setPhase] = React.useState(0);
  React.useEffect(() => {
    const id = setInterval(() => setPhase((p) => (p + 1) % 3), 600);
    return () => clearInterval(id);
  }, []);
  const phases = ['오늘의 흐름을 살피는 중', '음양의 균형을 짚는 중', '한 장으로 정리하는 중'];
  return (
    <div style={{ display: 'flex', flexDirection: 'column', height: '100%', alignItems: 'center', justifyContent: 'center', padding: '0 24px' }}>
      <div style={{ position: 'relative', width: 200, height: 200 }}>
        <div style={{ position: 'absolute', inset: 0, animation: 'de-spin 9s linear infinite' }}>
          {['SUNNY','CLEAR','CLOUDY','RAINY','STORM'].map((g, i) => {
            const angle = (i / 5) * Math.PI * 2;
            const r = 78;
            const cx = 100 + Math.cos(angle - Math.PI/2) * r - 20;
            const cy = 100 + Math.sin(angle - Math.PI/2) * r - 20;
            return (
              <img key={g} src={DE_ASSETS[g]} width="40" height="40" alt=""
                style={{ position: 'absolute', left: cx, top: cy, filter: 'drop-shadow(0 6px 14px rgba(17,24,39,.10))' }} />
            );
          })}
        </div>
        <div style={{ position: 'absolute', inset: 0, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
          <img src={DE_ASSETS.SUNNY} width="88" height="88" alt=""
            style={{ filter: 'drop-shadow(0 16px 28px rgba(255,184,0,.30))', animation: 'de-pulse 1.6s ease-in-out infinite' }} />
        </div>
      </div>
      <div style={{ fontSize: 20, fontWeight: 700, color: 'var(--text-primary)', marginTop: 28, letterSpacing: '-0.02em' }}>
        {name} 님의 오늘
      </div>
      <div style={{ fontSize: 13, color: 'var(--text-tertiary)', marginTop: 6, minHeight: 22 }}>
        {phases[phase]}<span style={{ display: 'inline-block', width: 18, textAlign: 'left' }}>{'.'.repeat(phase + 1)}</span>
      </div>
      <div style={{ width: 140, height: 4, background: 'var(--cream-300)', borderRadius: 999, marginTop: 24, overflow: 'hidden' }}>
        <div style={{ height: '100%', background: 'var(--blue-500)', borderRadius: 999, animation: 'de-bar 2.2s cubic-bezier(.2,.7,.2,1) forwards' }} />
      </div>
    </div>
  );
}

// ─────────────── 3. Revealed (today's report) ───────────────
const FORTUNE_BODY = {
  SUNNY: [
    '오늘은 한 해 중에서도 손에 꼽을 만큼 흐름이 좋은 날입니다.',
    '미뤄 두었던 결정과 제안을 가볍게 꺼내 보세요. 의외로 쉽게 풀립니다.',
    '대인 관계에서도 좋은 소식이 들어옵니다. 먼저 다가서면 더 좋아요.',
    '저녁에는 무리하지 말고, 가까운 사람과 짧은 시간이라도 함께 보내세요.',
  ],
  CLEAR: [
    '맑게 비치는 하루, 머릿속의 정리가 잘 되는 날입니다.',
    '중요한 메일·서류·계약처럼 “지금 해야 할 일”을 오전에 처리하세요.',
    '직감보다는 데이터와 기록이 더 큰 힘이 됩니다. 한 번 더 확인해 보세요.',
    '저녁의 산책이나 짧은 운동이 내일의 흐름까지 가볍게 만들어 줍니다.',
  ],
  CLOUDY: [
    '큰 일은 일어나지 않는 평온한 하루입니다. 욕심을 내려놓아도 충분해요.',
    '새 일을 시작하기보다는 어제까지 해 둔 일을 한 번 더 다듬는 날입니다.',
    '식사와 수면 패턴을 일정하게 유지하면 컨디션이 빠르게 회복돼요.',
    '결정을 미뤄도 괜찮습니다. 내일이 더 명료한 답을 줄 거예요.',
  ],
  RAINY: [
    '작은 마찰이 생기기 쉬운 날, 말과 글의 톤을 한 단계 부드럽게 잡아 주세요.',
    '예상하지 못한 지출이 보입니다. 큰 결제는 하루 미루는 편이 안전합니다.',
    '몸이 평소보다 쉽게 피로해질 수 있으니, 따뜻한 물과 가벼운 음식이 좋아요.',
    '괜찮아질 거란 믿음 하나면 충분합니다. 비는 결국 그칩니다.',
  ],
  STORM: [
    '오늘만큼은 쉬어가도 좋다고, 사주가 분명히 말해 주는 날입니다.',
    '중요한 결정이나 큰 발화는 내일 이후로 미루세요. 오해가 생기기 쉽습니다.',
    '믿을 만한 한 사람에게만 솔직히 털어놓아도 마음이 가벼워져요.',
    '하루를 일찍 마무리하고, 따뜻한 차 한 잔과 함께 잠자리에 드세요.',
  ],
};

const FORTUNE_SUMMARY = {
  SUNNY:  '미뤄 두었던 일을 펼치기 가장 좋은 날',
  CLEAR:  '머리가 맑게 정리되는 흐름의 날',
  CLOUDY: '욕심을 내려놓고 꾸준히 가는 날',
  RAINY:  '말과 지출을 한 박자 늦추는 날',
  STORM:  '오늘만큼은 쉬어가도 좋은 날',
};

function RevealedScreen({ name, dateText, dow, grade, onReset }) {
  const g = GRADES[grade];
  const body = FORTUNE_BODY[grade] || FORTUNE_BODY.SUNNY;
  const summary = FORTUNE_SUMMARY[grade] || FORTUNE_SUMMARY.SUNNY;
  return (
    <div style={{
      display: 'flex', flexDirection: 'column', height: '100%',
      padding: '0 16px', overflow: 'hidden',
      animation: 'de-fade-in 360ms cubic-bezier(.2,.7,.2,1) forwards',
    }}>
      {/* hero */}
      <div style={{
        display: 'flex', flexDirection: 'column', alignItems: 'center',
        padding: '6px 0 8px',
      }}>
        <img src={DE_ASSETS[grade]} width="120" height="120" alt=""
          style={{ marginTop: 4, filter: `drop-shadow(0 16px 24px ${g.color}55)` }} />
        <div style={{
          fontSize: 30, fontWeight: 800, color: g.headline,
          letterSpacing: '-0.02em', marginTop: 6,
        }}>{g.kr}</div>
      </div>

      {/* one-line summary card */}
      <div style={{
        background: 'var(--bg-surface)', borderRadius: 16, padding: '14px 18px',
        boxShadow: 'var(--shadow-card)', marginTop: 4,
        display: 'flex', alignItems: 'center', gap: 12,
      }}>
        <div style={{
          width: 28, height: 28, borderRadius: 999, flexShrink: 0,
          background: `${g.color}33`, color: g.headline,
          display: 'flex', alignItems: 'center', justifyContent: 'center',
        }}>
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.4" strokeLinecap="round" strokeLinejoin="round">
            <path d="M20 6L9 17l-5-5"/>
          </svg>
        </div>
        <div style={{
          flex: 1, fontSize: 14, fontWeight: 600, color: 'var(--text-primary)',
          lineHeight: 1.45, letterSpacing: '-0.01em', textWrap: 'pretty',
        }}>{summary}</div>
      </div>

      {/* fortune body */}
      <div style={{
        background: 'var(--bg-surface)', borderRadius: 16, padding: '14px 16px 16px',
        boxShadow: 'var(--shadow-card)', marginTop: 10,
        display: 'flex', flexDirection: 'column', gap: 10,
      }}>
        <div style={{
          display: 'flex', alignItems: 'center', gap: 8,
        }}>
          <div style={{ width: 4, height: 14, borderRadius: 2, background: g.headline }} />
          <div style={{ fontSize: 13, fontWeight: 700, color: 'var(--text-primary)' }}>
            오늘의 운세
          </div>
        </div>
        <div style={{ fontSize: 13, color: 'var(--text-primary)', lineHeight: 1.7, textWrap: 'pretty' }}>
          {body.map((line, i) => (
            <p key={i} style={{ margin: i === 0 ? 0 : '8px 0 0' }}>{line}</p>
          ))}
        </div>
      </div>
    </div>
  );
}

Object.assign(window, {
  HomeShell, AwaitingScreen, CalculatingScreen, RevealedScreen,
});
