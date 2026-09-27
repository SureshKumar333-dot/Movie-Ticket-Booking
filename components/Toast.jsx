// components/Toast.jsx
import { useApp } from '@/context/AppContext'

export default function Toast() {
  const { toast } = useApp()
  if (!toast) return null
  return (
    <div style={{
      position:'fixed', bottom:28, right:28,
      background:'var(--bg-surface)', border:'1px solid var(--border-bright)',
      borderLeft:`3px solid ${toast.type==='error'?'var(--crimson-bright)':'var(--gold)'}`,
      borderRadius:3, padding:'14px 22px', fontSize:14, zIndex:9999,
      display:'flex', alignItems:'center', gap:10, minWidth:260,
      color:'var(--text-primary)', fontFamily:'var(--font-body)',
      animation:'fadeInUp 0.4s cubic-bezier(0.34,1.56,0.64,1)',
      boxShadow:'0 8px 32px rgba(0,0,0,0.5)'
    }}>
      <span>{toast.type==='error' ? '⚠' : '🎬'}</span>
      <span>{toast.msg}</span>
    </div>
  )
}
