/* FortunePaper · Settings
   하단 TabBar의 "설정" 탭에서 진입.
   - 설정 목록: 알림 설정 / 로그아웃 / 회원 탈퇴
   - 알림 설정: 회원가입 단계의 ScreenNotify 와 동일한 구조의 편집 화면
   - 로그아웃 · 회원 탈퇴: 확인 다이얼로그 → 확인 시 초기 화면(온보딩 Welcome) 으로 이동
*/

// ─────────────── shell with optional back ───────────────
function SettingsShell({ title, onBack, children, showTabBar = true }) {
  return (
    <div style={{ display: 'flex', flexDirection: 'column', height: '100%', background: 'var(--bg-primary)' }}>
      <div style={{ height: 56 }} />
      <div style={{
        height: 48, display: 'flex', alignItems: 'center', padding: '0 8px', gap: 8,
        flexShrink: 0,
      }}>
        {onBack ? (
          <button onClick={onBack} aria-label="뒤로" style={{
            width: 40, height: 40, borderRadius: 999, background: 'transparent', border: 'none',
            display: 'flex', alignItems: 'center', justifyContent: 'center', cursor: 'pointer',
          }}>
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none"
              stroke="var(--text-primary)" strokeWidth="2.2" strokeLinecap="round" strokeLinejoin="round">
              <path d="M15 18l-6-6 6-6"/>
            </svg>
          </button>
        ) : <div style={{ width: 40 }} />}
        <div style={{
          flex: 1, textAlign: 'center', fontSize: 16, fontWeight: 600, color: 'var(--text-primary)',
        }}>{title}</div>
        <div style={{ width: 40 }} />
      </div>
      <div style={{ flex: 1, minHeight: 0, overflow: 'hidden' }}>{children}</div>
      {showTabBar && <SettingsTabBar activeTab="settings" onSelect={() => {}} />}
    </div>
  );
}

// ─────────────── settings list ───────────────
function SettingsListScreen({ onOpenNotify, onLogout, onWithdraw, notifyTime }) {
  const items = [
    {
      id: 'notify',
      label: '알림 설정',
      meta: `매일 오전 ${notifyTime}`,
      onClick: onOpenNotify,
      chevron: true,
    },
    {
      id: 'logout',
      label: '로그아웃',
      onClick: onLogout,
      chevron: true,
    },
    {
      id: 'withdraw',
      label: '회원 탈퇴',
      onClick: onWithdraw,
      destructive: true,
      chevron: true,
    },
  ];

  return (
    <div style={{ padding: '12px 16px 0' }}>
      <div style={{
        background: 'var(--bg-surface)', borderRadius: 14,
        boxShadow: 'var(--shadow-card)', overflow: 'hidden',
      }}>
        {items.map((it, i) => (
          <button key={it.id} onClick={it.onClick} style={{
            display: 'flex', alignItems: 'center', gap: 12, width: '100%',
            padding: '16px 16px', background: 'transparent', border: 'none', cursor: 'pointer',
            borderBottom: i === items.length - 1 ? 'none' : '1px solid var(--cream-300)',
            fontFamily: 'inherit', textAlign: 'left',
          }}>
            <div style={{
              flex: 1, fontSize: 15, fontWeight: 500,
              color: it.destructive ? '#D14545' : 'var(--text-primary)',
            }}>{it.label}</div>
            {it.meta && (
              <div style={{ fontSize: 13, color: 'var(--text-tertiary)', fontFeatureSettings: '"tnum" 1' }}>
                {it.meta}
              </div>
            )}
            {it.chevron && (
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none"
                stroke="var(--text-tertiary)" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <path d="M9 6l6 6-6 6"/>
              </svg>
            )}
          </button>
        ))}
      </div>

      {/* version footer */}
      <div style={{
        marginTop: 24, textAlign: 'center', fontSize: 11, color: 'var(--text-tertiary)',
        letterSpacing: '0.06em',
      }}>FortunePaper · v1.0.0</div>
    </div>
  );
}

// ─────────────── notify-time edit (settings) ───────────────
function NotifyEditScreen({ value, onChange, onBack, onSave }) {
  const presets = [
    { t: '06:30', label: '이른 아침' },
    { t: '07:30', label: '출근 전' },
    { t: '08:30', label: '아침 시간' },
    { t: '09:30', label: '느긋한 아침' },
  ];

  return (
    <div style={{ display: 'flex', flexDirection: 'column', height: '100%', padding: '0 16px' }}>
      <div style={{ paddingTop: 4, paddingBottom: 16 }}>
        <div style={{
          fontSize: 22, fontWeight: 700, color: 'var(--text-primary)',
          letterSpacing: '-0.02em',
        }}>리포트를 언제 받아 보시겠어요?</div>
        <div style={{
          fontSize: 13, color: 'var(--text-tertiary)', marginTop: 8, lineHeight: 1.6,
        }}>설정한 시간에 매일 한 번, 오늘의 한 줄 요약을 알려 드립니다.</div>
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
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none"
                  stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                  <path d="M6 8a6 6 0 0 1 12 0c0 7 3 9 3 9H3s3-2 3-9"/>
                  <path d="M10.3 21a1.94 1.94 0 0 0 3.4 0"/>
                </svg>
              </div>
              <div style={{ flex: 1 }}>
                <div style={{
                  fontSize: 18, fontWeight: 700, color: 'var(--text-primary)',
                  fontFeatureSettings: '"tnum" 1',
                }}>오전 {p.t}</div>
                <div style={{ fontSize: 12, color: 'var(--text-tertiary)', marginTop: 2 }}>{p.label}</div>
              </div>
              <div style={{
                width: 22, height: 22, borderRadius: 999,
                border: active ? 'none' : '1.5px solid var(--border-default)',
                background: active ? 'var(--blue-500)' : 'transparent',
                display: 'flex', alignItems: 'center', justifyContent: 'center',
              }}>
                {active && (
                  <svg width="12" height="12" viewBox="0 0 24 24" fill="none"
                    stroke="#fff" strokeWidth="3.5" strokeLinecap="round" strokeLinejoin="round">
                    <path d="M20 6L9 17l-5-5"/>
                  </svg>
                )}
              </div>
            </button>
          );
        })}
      </div>

      <div style={{ marginTop: 'auto', padding: '12px 0 16px' }}>
        <FPButton onClick={onSave}>저장하기</FPButton>
      </div>
    </div>
  );
}

// ─────────────── iOS-style confirm dialog ───────────────
function ConfirmDialog({ title, message, confirmLabel, destructive, onConfirm, onCancel }) {
  return (
    <div style={{
      position: 'absolute', inset: 0,
      background: 'rgba(17,24,39,0.42)', backdropFilter: 'blur(2px)',
      display: 'flex', alignItems: 'center', justifyContent: 'center',
      padding: '0 32px', zIndex: 100,
      animation: 'de-fade-in 180ms ease-out forwards',
    }}>
      <div style={{
        background: 'rgba(255,255,255,0.96)', borderRadius: 16,
        maxWidth: 280, width: '100%', overflow: 'hidden',
        boxShadow: '0 24px 48px rgba(17,24,39,0.20)',
        animation: 'set-dialog-in 180ms cubic-bezier(.2,.7,.2,1) forwards',
      }}>
        <div style={{ padding: '20px 18px 16px', textAlign: 'center' }}>
          <div style={{
            fontSize: 15, fontWeight: 700, color: 'var(--text-primary)',
            letterSpacing: '-0.01em',
          }}>{title}</div>
          {message && (
            <div style={{
              fontSize: 12, color: 'var(--text-tertiary)', marginTop: 6,
              lineHeight: 1.55, textWrap: 'pretty',
            }}>{message}</div>
          )}
        </div>
        <div style={{
          display: 'flex', borderTop: '0.5px solid rgba(17,24,39,0.12)',
        }}>
          <button onClick={onCancel} style={{
            flex: 1, padding: '13px 0', background: 'transparent', border: 'none',
            borderRight: '0.5px solid rgba(17,24,39,0.12)',
            color: 'var(--text-primary)', fontSize: 15, fontFamily: 'inherit',
            cursor: 'pointer',
          }}>취소</button>
          <button onClick={onConfirm} style={{
            flex: 1, padding: '13px 0', background: 'transparent', border: 'none',
            color: destructive ? '#D14545' : 'var(--blue-500)',
            fontSize: 15, fontWeight: 600, fontFamily: 'inherit',
            cursor: 'pointer',
          }}>{confirmLabel}</button>
        </div>
      </div>
    </div>
  );
}

// ─────────────── tab bar with switchable active ───────────────
// Override of DETabBar that takes an `activeTab` prop. Keeps the same icons.
function SettingsTabBar({ activeTab = 'settings', onSelect }) {
  const tabs = [
    { id: 'today',    kr: '오늘',  d: 'M3 11.5L12 4l9 7.5V20a1 1 0 01-1 1h-5v-6h-6v6H4a1 1 0 01-1-1z' },
    { id: 'settings', kr: '설정',  d: 'M12 15a3 3 0 100-6 3 3 0 000 6zm7-3a7 7 0 00-.1-1.2l2-1.5-2-3.4-2.3.9a7 7 0 00-2-1.2L14 3h-4l-.6 2.6a7 7 0 00-2 1.2L5.1 5.9l-2 3.4 2 1.5A7 7 0 005 12c0 .4 0 .8.1 1.2l-2 1.5 2 3.4 2.3-.9a7 7 0 002 1.2L10 21h4l.6-2.6a7 7 0 002-1.2l2.3.9 2-3.4-2-1.5c.1-.4.1-.8.1-1.2z' },
  ];
  return (
    <div style={{
      display: 'flex', justifyContent: 'space-around', alignItems: 'center',
      background: 'var(--bg-surface)', borderTop: '1px solid var(--cream-300)',
      padding: '8px 8px 18px', flexShrink: 0,
    }}>
      {tabs.map((t) => {
        const active = t.id === activeTab;
        const c = active ? 'var(--blue-500)' : 'var(--text-secondary)';
        return (
          <button key={t.id} onClick={() => onSelect && onSelect(t.id)} style={{
            flex: 1, display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 4,
            color: c, background: 'transparent', border: 'none', cursor: 'pointer',
            fontFamily: 'inherit',
          }}>
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none"
              stroke={c} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              <path d={t.d} />
            </svg>
            <div style={{ fontSize: 11, fontWeight: active ? 600 : 500 }}>{t.kr}</div>
          </button>
        );
      })}
    </div>
  );
}

Object.assign(window, {
  SettingsShell, SettingsListScreen, NotifyEditScreen, ConfirmDialog, SettingsTabBar,
});
