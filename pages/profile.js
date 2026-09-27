// pages/profile.jsx — My Profile + Edit + My Bookings
import { useState, useEffect } from 'react'
import Head from 'next/head'
import Link from 'next/link'
import { useRouter } from 'next/router'
import Navbar from '@/components/Navbar'
import Toast  from '@/components/Toast'
import { useApp } from '@/context/AppContext'
import { getBookingsByCustomer } from '@/lib/crud'
import styles from '@/styles/Profile.module.css'

const CITIES   = ['Coimbatore','Chennai','Madurai','Trichy','Salem','Tirunelveli','Vellore','Erode']
const CITY_IDS = { Coimbatore:'CBE',Chennai:'CHN',Madurai:'MDU',Trichy:'TRY',Salem:'SLM',Tirunelveli:'TNV',Vellore:'VLR',Erode:'ERD' }
const CITY_NM  = Object.fromEntries(Object.entries(CITY_IDS).map(([k,v])=>[v,k]))

export default function ProfilePage() {
  const router = useRouter()
  const { currentUser, updateProfile, cancelUserBooking } = useApp()
  const [tab,      setTab]      = useState('profile')
  const [editing,  setEditing]  = useState(false)
  const [bookings, setBookings] = useState([])
  const [loadingB, setLoadingB] = useState(false)
  const [form,     setForm]     = useState({ name:'', phone:'', address:'', dob:'', gender:'', city:'' })

  useEffect(() => {
    if (!currentUser) { router.push('/signin'); return }
    setForm({ name:currentUser.name||'', phone:currentUser.phone||'', address:currentUser.address||'', dob:currentUser.dob||'', gender:currentUser.gender||'', city:CITY_NM[currentUser.city]||'' })
  }, [currentUser])

  useEffect(() => {
    if (tab==='bookings' && currentUser) {
      setLoadingB(true)
      getBookingsByCustomer(currentUser.id).then(r=>setBookings(r.data||[])).catch(()=>{}).finally(()=>setLoadingB(false))
    }
  }, [tab, currentUser])

  if (!currentUser) return null

  const handleSave = async e => {
    e.preventDefault()
    const res = await updateProfile({ ...form, city: CITY_IDS[form.city]||currentUser.city })
    if (res.success) setEditing(false)
  }

  const handleCancel = async id => {
    if (!confirm('Cancel this booking?')) return
    const res = await cancelUserBooking(id)
    if (res.success) setBookings(prev=>prev.map(b=>b.id===id?{...b,status:'CANCELLED'}:b))
  }

  const fmt = d => d ? new Date(d).toLocaleDateString('en-IN',{day:'numeric',month:'short',year:'numeric'}) : 'N/A'

  return (
    <>
      <Head><title>My Profile — CineVerse</title></Head>
      <Navbar/><Toast/>
      <div className={styles.page}>

        {/* Header */}
        <div className={styles.header}>
          <div className={styles.headerInner}>
            <div className={styles.avatar}>{currentUser.avatar}</div>
            <div>
              <div className={styles.userName}>{currentUser.name}</div>
              <div className={styles.userEmail}>{currentUser.email}</div>
              <div className={styles.userMeta}>📍 {CITY_NM[currentUser.city]||currentUser.city} &nbsp;·&nbsp; Member since {currentUser.joinedOn}</div>
            </div>
          </div>
        </div>

        {/* Tabs */}
        <div className={styles.tabs}>
          <button className={`${styles.tab} ${tab==='profile'?styles.tabActive:''}`} onClick={()=>setTab('profile')}>👤 My Profile</button>
          <button className={`${styles.tab} ${tab==='bookings'?styles.tabActive:''}`} onClick={()=>setTab('bookings')}>🎟 My Bookings</button>
        </div>

        <div className={styles.body}>

          {/* Profile Tab */}
          {tab==='profile'&&(
            <div className={styles.card}>
              <div className={styles.cardHeader}>
                <h2 className={styles.cardTitle}>Personal Details</h2>
                {!editing&&<button className="btn-ghost" style={{padding:'8px 20px',fontSize:'12px'}} onClick={()=>setEditing(true)}>✏ Edit Profile</button>}
              </div>
              {!editing ? (
                <div className={styles.detailGrid}>
                  {[['Full Name',currentUser.name],['Email Address',currentUser.email],['Phone Number',currentUser.phone],['City',CITY_NM[currentUser.city]||currentUser.city],['Date of Birth',currentUser.dob||'Not set'],['Gender',currentUser.gender||'Not set'],['Address',currentUser.address||'Not set'],['Member Since',currentUser.joinedOn]].map(([l,v])=>(
                    <div key={l} className={styles.detailItem}>
                      <div className={styles.detailLabel}>{l}</div>
                      <div className={styles.detailVal}>{v}</div>
                    </div>
                  ))}
                </div>
              ) : (
                <form onSubmit={handleSave}>
                  <div className={styles.formGrid}>
                    <div className="form-field"><label className="form-label">Full Name</label><div className="form-input-wrap"><span className="form-icon">👤</span><input className="form-input" value={form.name} onChange={e=>setForm(p=>({...p,name:e.target.value}))} required/></div></div>
                    <div className="form-field"><label className="form-label">Phone Number</label><div className="form-input-wrap"><span className="form-icon">📱</span><input className="form-input" value={form.phone} maxLength={10} onChange={e=>setForm(p=>({...p,phone:e.target.value}))}/></div></div>
                    <div className="form-field"><label className="form-label">City</label><div className="form-input-wrap"><span className="form-icon">📍</span><select className="form-input form-select" value={form.city} onChange={e=>setForm(p=>({...p,city:e.target.value}))}>{CITIES.map(c=><option key={c}>{c}</option>)}</select></div></div>
                    <div className="form-field"><label className="form-label">Date of Birth</label><div className="form-input-wrap"><span className="form-icon">📅</span><input className="form-input" type="date" value={form.dob} onChange={e=>setForm(p=>({...p,dob:e.target.value}))}/></div></div>
                    <div className="form-field"><label className="form-label">Gender</label><div className="form-input-wrap"><span className="form-icon">🧑</span><select className="form-input form-select" value={form.gender} onChange={e=>setForm(p=>({...p,gender:e.target.value}))}><option value="">Select</option><option>Male</option><option>Female</option><option>Prefer not to say</option></select></div></div>
                    <div className="form-field" style={{gridColumn:'1/-1'}}><label className="form-label">Address</label><div className="form-input-wrap"><span className="form-icon">🏠</span><input className="form-input" placeholder="Your address" value={form.address} onChange={e=>setForm(p=>({...p,address:e.target.value}))}/></div></div>
                  </div>
                  <div style={{display:'flex',gap:12,marginTop:8}}>
                    <button type="submit" className="btn-gold">Save Changes</button>
                    <button type="button" className="btn-ghost" onClick={()=>setEditing(false)}>Cancel</button>
                  </div>
                </form>
              )}
            </div>
          )}

          {/* Bookings Tab */}
          {tab==='bookings'&&(
            <div>
              <div className={styles.bookingsHeader}>
                <h2 className={styles.cardTitle}>My Bookings</h2>
                <Link href="/search" className="btn-gold" style={{padding:'10px 20px',fontSize:'12px',textDecoration:'none'}}>+ Book New Ticket</Link>
              </div>
              {loadingB&&<div className={styles.loadingTxt}>Loading bookings...</div>}
              {!loadingB&&bookings.length===0&&(
                <div className={styles.noBookings}>
                  <div style={{fontSize:48,marginBottom:16}}>🎟</div>
                  <div style={{marginBottom:20,fontSize:16}}>No bookings yet!</div>
                  <Link href="/" className="btn-gold" style={{textDecoration:'none'}}>Explore Movies</Link>
                </div>
              )}
              <div className={styles.bookingsList}>
                {bookings.map(b=>(
                  <div key={b.id} className={`${styles.bookingCard} ${b.status==='CANCELLED'?styles.cancelledCard:''}`}>
                    <div className={styles.bookingInfo}>
                      <div className={styles.bookingMovie}>{b.movieTitle}</div>
                      <div className={styles.bookingMeta}>
                        <span>🎭 {b.theatreName}</span><span>🏛 {b.hallName}</span>
                        <span>📅 {fmt(b.showDate)}</span><span>🕐 {b.showTime}</span>
                      </div>
                      <div className={styles.bookingSeats}>🪑 Seats: {b.seats?.join(', ')}</div>
                      <div className={styles.bookingPay}>💳 {b.paymentMethod} · ₹{b.totalAmount}</div>
                      <div className={styles.bookingId}>ID: #{b.id}</div>
                    </div>
                    <div className={styles.bookingRight}>
                      <div className={`${styles.statusBadge} ${b.status==='CONFIRMED'?styles.confirmed:styles.cancelledBadge}`}>
                        {b.status==='CONFIRMED'?'✓ Confirmed':'✗ Cancelled'}
                      </div>
                      {b.status==='CONFIRMED'&&(
                        <button className="btn-danger" style={{fontSize:'11px',padding:'7px 12px',marginTop:8}} onClick={()=>handleCancel(b.id)}>Cancel</button>
                      )}
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>
      </div>
    </>
  )
}
