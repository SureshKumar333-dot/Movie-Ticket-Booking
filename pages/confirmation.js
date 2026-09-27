// pages/confirmation.jsx
import { useEffect, useState } from 'react'
import Head from 'next/head'
import Link from 'next/link'
import { useRouter } from 'next/router'
import Navbar from '@/components/Navbar'
import Toast  from '@/components/Toast'
import { useApp } from '@/context/AppContext'
import styles from '@/styles/Confirmation.module.css'

export default function ConfirmationPage() {
  const router = useRouter()
  const { currentUser } = useApp()
  const [booking, setBooking] = useState(null)

  useEffect(() => {
    const { data } = router.query
    if (data) { try { setBooking(JSON.parse(decodeURIComponent(data))) } catch { router.push('/') } }
  }, [router.query])

  const download = () => {
    if (!booking) return
    const a = document.createElement('a')
    a.href = URL.createObjectURL(new Blob([JSON.stringify(booking,null,2)],{type:'application/json'}))
    a.download = `${booking.id}.json`; a.click()
  }

  const fmt = d => d ? new Date(d).toLocaleDateString('en-IN',{weekday:'long',day:'numeric',month:'long',year:'numeric'}) : 'N/A'
  const fmtT = d => d ? new Date(d).toLocaleString('en-IN') : 'N/A'

  if (!booking) return <><Navbar/><Toast/><div style={{padding:'120px 48px',textAlign:'center',color:'var(--text-muted)'}}>Loading...</div></>

  return (
    <>
      <Head><title>Booking Confirmed — CineVerse</title></Head>
      <Navbar/><Toast/>
      <div className={styles.page}>
        <div className={styles.banner}>
          <div className={styles.bannerIcon}>🎬</div>
          <h1 className={styles.bannerTitle}>Booking Confirmed!</h1>
          <p className={styles.bannerSub}>Your tickets are ready. Enjoy the show!</p>
        </div>
        <div className={styles.content}>
          {/* Ticket */}
          <div className={styles.ticket}>
            <div className={styles.ticketTop}>
              <div className={styles.ticketLogo}>CineVerse</div>
              <div className={styles.ticketStatus}><span className={styles.dot}/>CONFIRMED</div>
            </div>
            <div className={styles.movieRow}>
              <div><div className={styles.movieTitle}>{booking.movieTitle}</div>
                <div className={styles.movieMeta}>{fmt(booking.showDate)} · {booking.showTime}</div>
              </div>
              <div className={styles.bookingId}>#{booking.id}</div>
            </div>
            <div className={styles.tearLine}><div className={styles.cL}/><div className={styles.dots}/><div className={styles.cR}/></div>
            <div className={styles.detailGrid}>
              {[{k:'Theatre',v:booking.theatreName},{k:'Hall',v:booking.hallName},{k:'Seats',v:booking.seats?.join(', ')},{k:'Seat Type',v:booking.seatType},{k:'Total Paid',v:`₹${booking.totalAmount}`},{k:'Payment',v:booking.paymentMethod}].map(d=>(
                <div key={d.k} className={styles.detailItem}>
                  <div className={styles.detailLabel}>{d.k}</div>
                  <div className={`${styles.detailVal} ${d.k==='Total Paid'?styles.goldVal:''}`}>{d.v}</div>
                </div>
              ))}
            </div>
            <div className={styles.tearLine}><div className={styles.cL}/><div className={styles.dots}/><div className={styles.cR}/></div>
            <div className={styles.guestRow}>
              <div className={styles.guestAvatar}>{currentUser?.avatar||'GU'}</div>
              <div><div className={styles.guestName}>{currentUser?.name||'Guest'}</div><div className={styles.guestEmail}>{currentUser?.email}</div></div>
              <div className={styles.payBadge}>✓ {booking.paymentStatus}</div>
            </div>
          </div>


          {/* Actions */}
          <div className={styles.actions}>
            <Link href="/orders" className="btn-ghost" style={{textDecoration:'none'}}>📋 All Orders</Link>
            <Link href="/" className="btn-gold" style={{textDecoration:'none'}}>🏠 Back to Home</Link>
          </div>
          <div className={styles.bookedAt}>Booked on {fmtT(booking.bookedOn)}</div>
        </div>
      </div>
    </>
  )
}
