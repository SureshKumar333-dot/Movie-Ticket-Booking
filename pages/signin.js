// pages/signin.jsx
import { useState } from 'react'
import Head from 'next/head'
import Link from 'next/link'
import { useRouter } from 'next/router'
import { useApp } from '@/context/AppContext'
import Toast from '@/components/Toast'
import styles from '@/styles/Auth.module.css'

export default function SignInPage() {
  const router = useRouter()
  const { signIn } = useApp()
  const [form, setForm]   = useState({ email:'', password:'' })
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const [showPass, setShowPass] = useState(false)

  const handle = e => { setForm(p=>({...p,[e.target.name]:e.target.value})); setError('') }

  const submit = async e => {
    e.preventDefault()
    setLoading(true)
    const res = await signIn(form.email, form.password)
    if (res.success) router.push('/')
    else { setError(res.error); setLoading(false) }
  }

  return (
    <>
      <Head><title>Sign In — CineVerse</title></Head>
      <Toast />
      <div className={styles.page}>
        <div className={styles.card}>
          <Link href="/" className={styles.logo}>Cine<span>Verse</span></Link>
          <h2 className={styles.heading}>Welcome Back</h2>
          <p className={styles.sub}>Sign in to book your favourite movies 🎬</p>
          <form onSubmit={submit}>
            <div className="form-field">
              <label className="form-label">Email Address</label>
              <div className="form-input-wrap"><span className="form-icon">✉</span>
                <input className="form-input" type="email" name="email" placeholder="you@example.com" value={form.email} onChange={handle} required/>
              </div>
            </div>
            <div className="form-field">
              <label className="form-label">Password</label>
              <div className="form-input-wrap"><span className="form-icon">🔒</span>
                <input className="form-input" type={showPass?'text':'password'} name="password" placeholder="Enter password" value={form.password} onChange={handle} required/>
                <button type="button" className={styles.eyeBtn} onClick={()=>setShowPass(p=>!p)}>{showPass?'🙈':'👁'}</button>
              </div>
            </div>
            {error && <div className={styles.errBox}>⚠ {error}</div>}
            <button className="submit-btn" type="submit" disabled={loading}>
              {loading ? <span className="spinner"/> : 'Sign In'}
            </button>
          </form>
          <div className={styles.divider}><span>or</span></div>
          <p className={styles.switchTxt}>Don't have an account?{' '}
            <Link href="/signup" className={styles.switchLink}>Sign Up Free</Link>
          </p>
        </div>
      </div>
    </>
  )
}
