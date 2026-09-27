// pages/signup.jsx
import { useState } from 'react'
import Head from 'next/head'
import Link from 'next/link'
import { useRouter } from 'next/router'
import { useApp } from '@/context/AppContext'
import Toast from '@/components/Toast'
import styles from '@/styles/Auth.module.css'

const CITIES   = ['Coimbatore','Chennai','Madurai','Trichy','Salem','Tirunelveli','Vellore','Erode','Thanjavur','Tiruppur']
const CITY_IDS = { Coimbatore:'CBE',Chennai:'CHN',Madurai:'MDU',Trichy:'TRY',Salem:'SLM',Tirunelveli:'TNV',Vellore:'VLR',Erode:'ERD',Thanjavur:'TNJ',Tiruppur:'TPR' }

export default function SignUpPage() {
  const router = useRouter()
  const { signUp } = useApp()
  const [form, setForm]     = useState({ name:'',email:'',phone:'',password:'',confirm:'',city:'',dob:'',gender:'' })
  const [errors, setErrors] = useState({})
  const [loading, setLoading] = useState(false)
  const [showPass, setShowPass] = useState(false)
  const [done, setDone]     = useState(false)

  const handle = e => { setForm(p=>({...p,[e.target.name]:e.target.value})); setErrors(p=>({...p,[e.target.name]:''})) }

  const validate = () => {
    const errs = {}
    if (!form.name.trim() || form.name.trim().length < 3) errs.name = 'Full Name must be at least 3 characters'
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email))  errs.email = 'Enter a valid email address'
    if (!/^\d{10}$/.test(form.phone))                     errs.phone = 'Phone must be exactly 10 digits'
    if (form.password.length < 6)                          errs.password = 'Password must be at least 6 characters'
    if (form.password !== form.confirm)                    errs.confirm = 'Passwords do not match'
    if (!form.city)                                        errs.city = 'Please select your city'
    return errs
  }

  const submit = async e => {
    e.preventDefault()
    const errs = validate()
    if (Object.keys(errs).length) { setErrors(errs); return }
    setLoading(true)
    const res = await signUp({
      name:form.name.trim(), email:form.email.trim().toLowerCase(),
      password:form.password, phone:form.phone,
      city:CITY_IDS[form.city]||'CBE', dob:form.dob, gender:form.gender,
    })
    if (res.success) { setDone(true); setTimeout(() => router.push('/'), 1200) }
    else { setErrors({ email: res.error }); setLoading(false) }
  }

  if (done) return (
    <div className={styles.page}>
      <div className={styles.card} style={{ textAlign:'center', padding:'60px 40px' }}>
        <div style={{ fontSize:60, marginBottom:20 }}>🎉</div>
        <Link href="/" className={styles.logo}>Cine<span>Verse</span></Link>
        <h2 className={styles.heading} style={{ marginTop:16 }}>Account Created!</h2>
        <p className={styles.sub}>Redirecting you to home...</p>
      </div>
    </div>
  )

  return (
    <>
      <Head><title>Sign Up — CineVerse</title></Head>
      <Toast />
      <div className={styles.page}>
        <div className={`${styles.card} ${styles.cardWide}`}>
          <Link href="/" className={styles.logo}>Cine<span>Verse</span></Link>
          <h2 className={styles.heading}>Create Account</h2>
          <p className={styles.sub}>Register to start booking movie tickets 🍿</p>
          <form onSubmit={submit}>
            <div className={styles.twoCol}>
              <div className="form-field"><label className="form-label">Full Name *</label>
                <div className="form-input-wrap"><span className="form-icon">👤</span>
                  <input className={`form-input ${errors.name?'form-input-error':''}`} name="name" placeholder="Your full name" value={form.name} onChange={handle}/></div>
                {errors.name&&<div className="form-error-msg">{errors.name}</div>}
              </div>
              <div className="form-field"><label className="form-label">Email Address *</label>
                <div className="form-input-wrap"><span className="form-icon">✉</span>
                  <input className={`form-input ${errors.email?'form-input-error':''}`} type="email" name="email" placeholder="you@example.com" value={form.email} onChange={handle}/></div>
                {errors.email&&<div className="form-error-msg">{errors.email}</div>}
              </div>
              <div className="form-field"><label className="form-label">Phone Number *</label>
                <div className="form-input-wrap"><span className="form-icon">📱</span>
                  <input className={`form-input ${errors.phone?'form-input-error':''}`} name="phone" placeholder="10-digit mobile" maxLength={10} value={form.phone} onChange={handle}/></div>
                {errors.phone&&<div className="form-error-msg">{errors.phone}</div>}
              </div>
              <div className="form-field"><label className="form-label">City *</label>
                <div className="form-input-wrap"><span className="form-icon">📍</span>
                  <select className={`form-input form-select ${errors.city?'form-input-error':''}`} name="city" value={form.city} onChange={handle}>
                    <option value="">Select city</option>
                    {CITIES.map(c=><option key={c}>{c}</option>)}
                  </select></div>
                {errors.city&&<div className="form-error-msg">{errors.city}</div>}
              </div>
              <div className="form-field"><label className="form-label">Date of Birth</label>
                <div className="form-input-wrap"><span className="form-icon">📅</span>
                  <input className="form-input" type="date" name="dob" value={form.dob} onChange={handle}/></div>
              </div>
              <div className="form-field"><label className="form-label">Gender</label>
                <div className="form-input-wrap"><span className="form-icon">🧑</span>
                  <select className="form-input form-select" name="gender" value={form.gender} onChange={handle}>
                    <option value="">Select gender</option>
                    <option>Male</option><option>Female</option><option>Prefer not to say</option>
                  </select></div>
              </div>
              <div className="form-field"><label className="form-label">Password *</label>
                <div className="form-input-wrap"><span className="form-icon">🔒</span>
                  <input className={`form-input ${errors.password?'form-input-error':''}`} type={showPass?'text':'password'} name="password" placeholder="Min 6 characters" value={form.password} onChange={handle}/>
                  <button type="button" className={styles.eyeBtn} onClick={()=>setShowPass(p=>!p)}>{showPass?'🙈':'👁'}</button></div>
                {errors.password&&<div className="form-error-msg">{errors.password}</div>}
              </div>
              <div className="form-field"><label className="form-label">Confirm Password *</label>
                <div className="form-input-wrap"><span className="form-icon">🔒</span>
                  <input className={`form-input ${errors.confirm?'form-input-error':''}`} type={showPass?'text':'password'} name="confirm" placeholder="Re-enter password" value={form.confirm} onChange={handle}/></div>
                {errors.confirm&&<div className="form-error-msg">{errors.confirm}</div>}
              </div>
            </div>
            <button className="submit-btn" type="submit" disabled={loading}>
              {loading ? <span className="spinner"/> : 'Create Account'}
            </button>
          </form>
          <div className={styles.divider}><span>or</span></div>
          <p className={styles.switchTxt}>Already have an account?{' '}
            <Link href="/signin" className={styles.switchLink}>Sign In</Link>
          </p>
        </div>
      </div>
    </>
  )
}
