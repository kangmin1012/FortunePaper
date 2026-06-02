/* FortunePaper — shared UI components.
   Style follows the design tokens in /colors_and_type.css.
   These are visual-only — no real logic.                                    */

const FP_TOKENS = {
  bg: '#F6F5EE', surface: '#FFFFFF', elevated: '#FBFAF6',
  border: '#E5E7EB', borderSubtle: '#EEEEE6',
  text: '#111827', text2: '#6B7280', text3: '#9CA3AF', textDisabled: '#D1D5DB',
  blue: '#3B82F6', bluePress: '#2563EB',
  cream300: '#EEEEE6',
};

const GRADES = {
  // `headline` is a deeper variant of `color`, used for the grade keyword text
  // on a white card so it stays legible while still echoing the icon hue.
  SUNNY:  { kr: '화창',     color: '#FFD700', headline: '#C99000', text: '#7A5D00', tone: '적극적으로 도전하세요. 모든 게 잘 풀리는 날입니다.' },
  CLEAR:  { kr: '맑음',     color: '#87CEEB', headline: '#2E7DA8', text: '#1F5470', tone: '좋은 흐름이에요. 중요한 일을 오늘 처리하세요.' },
  CLOUDY: { kr: '구름',     color: '#B0BEC5', headline: '#607883', text: '#37474F', tone: '평온한 하루입니다. 무리하지 말고 꾸준하게 가요.' },
  RAINY:  { kr: '비',       color: '#5C8AC8', headline: '#3F6DAC', text: '#1F3B5C', tone: '조심이 필요해요. 그래도 괜찮아지는 날입니다.' },
  STORM:  { kr: '폭풍번개', color: '#546E7A', headline: '#37474F', text: '#263238', tone: '힘든 날이지만 지나갑니다. 오늘만큼은 쉬어가도 좋아요.' },
};

// ─────────────────────────── PrimaryButton ───────────────────────────
function FPButton({ children, onClick, variant = 'primary', disabled, fullWidth = true, size = 'lg' }) {
  const heights = { lg: 56, md: 48, sm: 40 };
  const fontSizes = { lg: 16, md: 15, sm: 14 };
  const base = {
    height: heights[size], borderRadius: 999, border: 'none', cursor: 'pointer',
    fontFamily: 'inherit', fontWeight: 600, fontSize: fontSizes[size],
    width: fullWidth ? '100%' : 'auto', padding: fullWidth ? 0 : '0 20px',
    transition: 'background 140ms cubic-bezier(.2,.7,.2,1), transform 140ms',
  };
  const styles = {
    primary:  { background: disabled ? '#E5E7EB' : FP_TOKENS.blue, color: disabled ? FP_TOKENS.text3 : '#fff' },
    outline:  { background: FP_TOKENS.surface, color: FP_TOKENS.text, border: `1px solid ${FP_TOKENS.border}` },
    ghost:    { background: 'transparent', color: FP_TOKENS.blue },
    cream:    { background: FP_TOKENS.cream300, color: '#374151' },
  };
  return (
    <button
      onClick={disabled ? undefined : onClick}
      disabled={disabled}
      style={{ ...base, ...styles[variant] }}
      onMouseDown={(e) => !disabled && (e.currentTarget.style.transform = 'scale(0.98)')}
      onMouseUp={(e) => (e.currentTarget.style.transform = 'scale(1)')}
      onMouseLeave={(e) => (e.currentTarget.style.transform = 'scale(1)')}
    >
      {children}
    </button>
  );
}

// ─────────────────────────── HeroGrade ───────────────────────────
// The center-of-everything: large clay icon + grade text + one-line summary.
function HeroGrade({ grade = 'SUNNY', dateText = '2026. 05. 23 · 토요일', name }) {
  const g = GRADES[grade];
  return (
    <div style={{
      display: 'flex', flexDirection: 'column', alignItems: 'center',
      padding: '20px 24px 28px',
    }}>
      <div style={{
        fontSize: 11, letterSpacing: '0.08em', color: FP_TOKENS.text2,
        textTransform: 'uppercase', marginBottom: 6,
      }}>{dateText}</div>
      {name && (
        <div style={{ fontSize: 13, color: FP_TOKENS.text2, marginBottom: 12 }}>
          {name} 님의 오늘
        </div>
      )}
      <div style={{ position: 'relative', width: 200, height: 200, marginTop: 8 }}>
        <img
          src={`design-system/assets/grade-${grade.toLowerCase()}.svg`}
          width="200" height="200"
          style={{ filter: `drop-shadow(0 20px 28px ${g.color}55)` }}
          alt={g.kr}
        />
      </div>
      <div style={{
        fontSize: 38, fontWeight: 700, letterSpacing: '-0.02em', marginTop: 16,
        color: g.headline,
      }}>{g.kr}</div>
      <div style={{
        fontSize: 16, color: FP_TOKENS.text2, textAlign: 'center', marginTop: 10,
        lineHeight: 1.5, maxWidth: 300, textWrap: 'pretty',
      }}>{g.tone}</div>
    </div>
  );
}

// ─────────────────────────── LuckyTiles ───────────────────────────
// Three side-by-side tiles: color / direction / number
function LuckyTiles({ items }) {
  return (
    <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: 10 }}>
      {items.map((it, i) => (
        <div key={i} style={{
          background: FP_TOKENS.surface, borderRadius: 16, padding: '14px 12px',
          boxShadow: '0 1px 2px rgba(17,24,39,.04), 0 2px 8px rgba(17,24,39,.04)',
          display: 'flex', flexDirection: 'column', gap: 8, alignItems: 'flex-start',
        }}>
          <div style={{
            fontSize: 10.5, color: FP_TOKENS.text2, letterSpacing: '0.06em',
            textTransform: 'uppercase',
          }}>{it.label}</div>
          {it.swatch && (
            <div style={{ width: 26, height: 26, borderRadius: 8, background: it.swatch }} />
          )}
          <div style={{ fontSize: 18, fontWeight: 700, color: FP_TOKENS.text }}>{it.value}</div>
        </div>
      ))}
    </div>
  );
}

// ─────────────────────────── Section Card ───────────────────────────
function SectionCard({ title, children, action }) {
  return (
    <div style={{
      background: FP_TOKENS.surface, borderRadius: 16, padding: '16px 16px 18px',
      boxShadow: '0 1px 2px rgba(17,24,39,.04), 0 2px 8px rgba(17,24,39,.04)',
    }}>
      <div style={{ display: 'flex', alignItems: 'baseline', justifyContent: 'space-between', marginBottom: 10 }}>
        <div style={{ fontSize: 16, fontWeight: 700, color: FP_TOKENS.text }}>{title}</div>
        {action}
      </div>
      {children}
    </div>
  );
}

// ─────────────────────────── Category row ───────────────────────────
function CategoryRow({ icon, label, summary, grade }) {
  const g = GRADES[grade] || GRADES.CLEAR;
  return (
    <div style={{
      display: 'flex', gap: 12, alignItems: 'center', padding: '10px 0',
    }}>
      <div style={{
        width: 40, height: 40, borderRadius: 12, background: g.color + '33',
        display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: 18,
      }}>{icon}</div>
      <div style={{ flex: 1, minWidth: 0 }}>
        <div style={{ fontSize: 14, fontWeight: 600, color: FP_TOKENS.text }}>{label}</div>
        <div style={{
          fontSize: 12, color: FP_TOKENS.text2, marginTop: 2,
          overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap',
        }}>{summary}</div>
      </div>
      <div style={{
        fontSize: 11, color: g.text, background: g.color + '33',
        padding: '4px 10px', borderRadius: 999, fontWeight: 600,
      }}>{g.kr}</div>
    </div>
  );
}

// ─────────────────────────── Filter chips ───────────────────────────
function ChipRow({ items, value, onChange }) {
  return (
    <div style={{ display: 'flex', gap: 8, overflowX: 'auto', padding: '4px 0' }}>
      {items.map((it) => {
        const active = it === value;
        return (
          <button
            key={it}
            onClick={() => onChange(it)}
            style={{
              flexShrink: 0, height: 34, padding: '0 14px', borderRadius: 999,
              background: active ? FP_TOKENS.blue : FP_TOKENS.surface,
              color: active ? '#fff' : '#374151',
              border: active ? 'none' : `1px solid ${FP_TOKENS.border}`,
              fontSize: 13, fontWeight: 500, cursor: 'pointer', fontFamily: 'inherit',
            }}
          >{it}</button>
        );
      })}
    </div>
  );
}

// ─────────────────────────── TabBar ───────────────────────────
function TabBar({ active, onChange }) {
  const tabs = [
    { id: 'today',    kr: '오늘',  d: 'M3 11.5L12 4l9 7.5V20a1 1 0 01-1 1h-5v-6h-6v6H4a1 1 0 01-1-1z' },
    { id: 'history',  kr: '이력',  d: 'M12 8v4l3 2M3 12a9 9 0 1 0 18 0 9 9 0 0 0-18 0z' },
    { id: 'match',    kr: '궁합',  d: 'M12 21s-7-4.5-9.5-9.5C.5 7 3.5 3 7.5 3c2 0 3.5 1 4.5 2.5C13 4 14.5 3 16.5 3c4 0 7 4 5 8.5C19 16.5 12 21 12 21z' },
    { id: 'settings', kr: '설정',  d: 'M12 15a3 3 0 100-6 3 3 0 000 6zm7-3a7 7 0 00-.1-1.2l2-1.5-2-3.4-2.3.9a7 7 0 00-2-1.2L14 3h-4l-.6 2.6a7 7 0 00-2 1.2L5.1 5.9l-2 3.4 2 1.5A7 7 0 005 12c0 .4 0 .8.1 1.2l-2 1.5 2 3.4 2.3-.9a7 7 0 002 1.2L10 21h4l.6-2.6a7 7 0 002-1.2l2.3.9 2-3.4-2-1.5c.1-.4.1-.8.1-1.2z' },
  ];
  return (
    <div style={{
      display: 'flex', justifyContent: 'space-around', alignItems: 'center',
      background: FP_TOKENS.surface, borderTop: `1px solid ${FP_TOKENS.borderSubtle}`,
      padding: '8px 8px 18px',
    }}>
      {tabs.map((t) => {
        const isActive = active === t.id;
        const color = isActive ? FP_TOKENS.blue : FP_TOKENS.text3;
        return (
          <button key={t.id} onClick={() => onChange(t.id)}
            style={{
              flex: 1, background: 'transparent', border: 'none', padding: '4px 0',
              display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 4,
              color, cursor: 'pointer', fontFamily: 'inherit',
            }}>
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none"
              stroke={color} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              <path d={t.d} />
            </svg>
            <div style={{ fontSize: 11, fontWeight: isActive ? 600 : 500 }}>{t.kr}</div>
          </button>
        );
      })}
    </div>
  );
}

// ─────────────────────────── Header (in-app) ───────────────────────────
function AppHeader({ title, leading, trailing, onBack }) {
  return (
    <div style={{
      height: 48, display: 'flex', alignItems: 'center', padding: '0 8px',
      gap: 4, position: 'relative',
    }}>
      {onBack ? (
        <button onClick={onBack} style={{
          width: 40, height: 40, borderRadius: 999, background: 'transparent', border: 'none',
          display: 'flex', alignItems: 'center', justifyContent: 'center', cursor: 'pointer',
        }}>
          <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke={FP_TOKENS.text} strokeWidth="2.2" strokeLinecap="round" strokeLinejoin="round">
            <path d="M15 18l-6-6 6-6"/>
          </svg>
        </button>
      ) : (leading || <div style={{ width: 40 }} />)}
      <div style={{ flex: 1, textAlign: 'center', fontSize: 16, fontWeight: 600, color: FP_TOKENS.text }}>
        {title}
      </div>
      {trailing || <div style={{ width: 40 }} />}
    </div>
  );
}

// ─────────────────────────── Field (text + birth) ───────────────────────────
function Field({ label, value, onChange, placeholder, focused }) {
  const [isFocused, setF] = React.useState(false);
  const active = isFocused || focused;
  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 6 }}>
      <div style={{ fontSize: 12, color: FP_TOKENS.text3 }}>{label}</div>
      <input
        value={value}
        onChange={(e) => onChange && onChange(e.target.value)}
        placeholder={placeholder}
        onFocus={() => setF(true)} onBlur={() => setF(false)}
        style={{
          height: 52, borderRadius: 14, padding: '0 16px',
          border: active ? `1.5px solid ${FP_TOKENS.blue}` : `1px solid ${FP_TOKENS.border}`,
          background: FP_TOKENS.surface, fontSize: 16, fontFamily: 'inherit',
          color: FP_TOKENS.text, outline: 'none',
        }}
      />
    </div>
  );
}

// ─────────────────────────── Gender picker ───────────────────────────
function GenderPicker({ value, onChange }) {
  const opts = [{ id: 'F', kr: '여성' }, { id: 'M', kr: '남성' }];
  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 6 }}>
      <div style={{ fontSize: 12, color: FP_TOKENS.text3 }}>성별</div>
      <div style={{
        display: 'flex', background: FP_TOKENS.surface, padding: 4, borderRadius: 14,
        border: `1px solid ${FP_TOKENS.border}`, gap: 4,
      }}>
        {opts.map((o) => {
          const a = value === o.id;
          return (
            <button key={o.id} onClick={() => onChange(o.id)}
              style={{
                flex: 1, height: 44, borderRadius: 11, border: 'none',
                background: a ? FP_TOKENS.blue : 'transparent',
                color: a ? '#fff' : FP_TOKENS.text2,
                fontSize: 14, fontWeight: 600, cursor: 'pointer', fontFamily: 'inherit',
                transition: 'background 140ms',
              }}>{o.kr}</button>
          );
        })}
      </div>
    </div>
  );
}

// ─────────────────────────── ScrollScreen wrapper ───────────────────────────
function Screen({ children, scroll = true, pad = 16 }) {
  return (
    <div style={{
      flex: 1, background: FP_TOKENS.bg, overflowY: scroll ? 'auto' : 'hidden',
      padding: `0 ${pad}px`, paddingBottom: 16,
    }}>
      {children}
    </div>
  );
}

Object.assign(window, {
  FP_TOKENS, GRADES,
  FPButton, HeroGrade, LuckyTiles, SectionCard, CategoryRow,
  ChipRow, TabBar, AppHeader, Field, GenderPicker, Screen,
});
