// pages/booking.jsx — BookMyShow-style: Movie Info → Date → Theatres+Shows → Seats → Payment
import { useState, useEffect, useMemo } from 'react'
import Head from 'next/head'
import { useRouter } from 'next/router'
import Navbar from '@/components/Navbar'
import Toast  from '@/components/Toast'
import { useApp } from '@/context/AppContext'
import { getMovieById, getTheatres, getShows, createBooking } from '@/lib/crud'
import { halls, paymentMethods, cities } from '@/data/db'
import styles from '@/styles/Booking.module.css'

const NEXT7 = Array.from({ length:7 }, (_, i) => {
  const d = new Date(); d.setDate(d.getDate() + i)
  return {
    str:     d.toISOString().split('T')[0],
    day:     d.toLocaleDateString('en-IN', { weekday:'short' }),
    date:    d.getDate(),
    month:   d.toLocaleDateString('en-IN', { month:'short' }),
    isToday: i === 0,
  }
})
const PREM = ['A','B']
const CONV = 30

export default function BookingPage() {
  const router  = useRouter()
  const { movieId } = router.query
  const { currentUser, showToast } = useApp()

  const [movie,      setMovie]     = useState(null)
  const [step,       setStep]      = useState(1) // 1=pick show, 2=seats, 3=payment
  const [selDate,    setSelDate]   = useState(NEXT7[0].str)
  const [allShows,   setAllShows]  = useState([])
  const [theatres,   setTheatres]  = useState([])
  const [selCity,    setSelCity]   = useState('CBE')
  const [selShow,    setSelShow]   = useState(null)
  const [selT,       setSelT]      = useState(null)
  const [selH,       setSelH]      = useState(null)
  const [seats,      setSeats]     = useState([])
  const [pay,        setPay]       = useState('')
  const [cardNum,    setCardNum]   = useState('')
  const [cardName,   setCardName]  = useState('')
  const [expiry,     setExpiry]    = useState('')
  const [cvv,        setCvv]       = useState('')
  const [upi,        setUpi]       = useState('')
  const [paying,     setPaying]    = useState(false)

  // Load movie
  useEffect(() => {
    if (!movieId) return
    getMovieById(movieId).then(r => setMovie(r.data)).catch(() => router.push('/search'))
  }, [movieId])

  // Load shows for this movie+date
  useEffect(() => {
    if (!movieId) return
    getShows({ movieId, date: selDate }).then(r => setAllShows(r.data || [])).catch(() => {})
  }, [movieId, selDate])

  // Load all theatres for selected city
  useEffect(() => {
    getTheatres(selCity).then(r => setTheatres(r.data || [])).catch(() => {})
  }, [selCity])

  // Group shows by theatre
  const theatreShowMap = useMemo(() => {
    const map = {}
    allShows.forEach(s => {
      if (!map[s.theatreId]) map[s.theatreId] = []
      map[s.theatreId].push(s)
    })
    return map
  }, [allShows])

  // Only theatres in selected city that have shows today
  const theatresWithShows = useMemo(() => {
    return theatres.filter(t => theatreShowMap[t.id] && theatreShowMap[t.id].length > 0)
  }, [theatres, theatreShowMap])

  const hall       = selH ? halls.find(h => h.id === selH) : null
  const bookedSts  = selShow && Array.isArray(selShow.bookedSeats) ? selShow.bookedSeats : []
  const tickets    = () => { const p=seats.filter(s=>PREM.includes(s[0])).length; return (seats.length-p)*(movie?.price?.standard||170)+p*(movie?.price?.premium||260) }
  const fee        = seats.length * CONV
  const total      = tickets() + fee

  const toggleSeat = id => {
    if (bookedSts.includes(id)) return
    setSeats(p => p.includes(id) ? p.filter(s=>s!==id) : p.length>=8 ? p : [...p, id])
  }

  const handleSelectShow = (show, theatre) => {
    setSelShow(show)
    setSelT(theatre)
    setSelH(show.hallId)
    setStep(2)
    setSeats([])
  }

  const handlePay = async () => {
    if (!currentUser) { showToast('Please sign in to book!','error'); router.push('/signin'); return }
    if (!pay)         { showToast('Select payment method','error'); return }
    if ((pay==='Credit Card'||pay==='Net Banking') && (!cardNum||!cardName||!expiry||!cvv)) { showToast('Fill all card details','error'); return }
    if (pay==='UPI' && !upi) { showToast('Enter UPI ID','error'); return }
    setPaying(true)
    try {
      const res = await createBooking({
        customerId:     currentUser.id,
        showId:         selShow.id,
        movieId:        movie.id,
        theatreId:      selT.id,
        hallId:         selH,
        cityId:         selCity,
        movieTitle:     movie.title,
        theatreName:    selT.name,
        hallName:       halls.find(h=>h.id===selH)?.name || '',
        showDate:       selDate,
        showTime:       selShow.time || selShow.showTime,
        seats,
        seatType:       seats.some(s=>PREM.includes(s[0])) ? 'Premium' : 'Standard',
        ticketAmount:   tickets(),
        convenienceFee: fee,
        totalAmount:    total,
        paymentMethod:  pay,
      })
      router.push(`/confirmation?data=${encodeURIComponent(JSON.stringify(res.data))}`)
    } catch(e) { showToast(e.message||'Booking failed','error') }
    finally { setPaying(false) }
  }

  if (!movie) return <><Navbar/><Toast/><div className={styles.loading}>Loading...</div></>

  const STEPS = ['Choose Show','Select Seats','Payment']

  return (
    <>
      <Head><title>Book — {movie.title} | CineVerse</title></Head>
      <Navbar/><Toast/>
      <div className={styles.page}>

        {/* ── Movie Info Header ── */}
        <div className={styles.movieBanner}>
          <div className={styles.movieBannerInner}>
            <button className={styles.backBtn} onClick={()=>router.back()}>← Back</button>
            <img src={movie.poster} alt={movie.title} className={styles.bannerPoster}
              onError={e=>{e.target.style.display='none'}}/>
            <div className={styles.bannerInfo}>
              <h1 className={styles.bannerTitle}>{movie.title}</h1>
              <div className={styles.bannerMeta}>
                <span className={styles.metaTag}>{movie.language}</span>
                <span className={styles.metaTag}>{movie.genre}</span>
                <span className={styles.metaTag}>{movie.duration}</span>
                <span className={styles.metaRating}>★ {movie.rating}</span>
              </div>
              <p className={styles.bannerDesc}>{movie.description}</p>
              <div className={styles.bannerCast}>
                <span className={styles.castLabel}>Cast:</span> {movie.cast}
              </div>
              <div className={styles.bannerDir}>
                <span className={styles.castLabel}>Director:</span> {movie.director}
              </div>
              <div className={styles.bannerPrices}>
                <span>Standard ₹{movie.price?.standard}</span>
                <span>Premium ₹{movie.price?.premium}</span>
              </div>
            </div>
          </div>
        </div>

        {/* ── Steps Bar ── */}
        <div className={styles.stepsBar}>
          {STEPS.map((s,i) => (
            <div key={s} style={{display:'flex',alignItems:'center'}}>
              <div className={`${styles.stepItem} ${step===i+1?styles.stepActive:step>i+1?styles.stepDone:''}`}>
                <div className={styles.stepCircle}>{step>i+1?'✓':i+1}</div>
                <span className={styles.stepName}>{s}</span>
              </div>
              {i<2 && <div className={styles.stepLine}/>}
            </div>
          ))}
        </div>

        {/* ── STEP 1: Pick Show (BookMyShow style) ── */}
        {step === 1 && (
          <div className={styles.step1Wrap}>

            {/* Date Row */}
            <div className={styles.dateRow}>
              {NEXT7.map(d => (
                <button key={d.str}
                  className={`${styles.dateBtn} ${selDate===d.str?styles.dateBtnActive:''}`}
                  onClick={() => { setSelDate(d.str); setSelShow(null) }}>
                  <span className={styles.dateDay}>{d.isToday ? 'Today' : d.day}</span>
                  <span className={styles.dateNum}>{d.date}</span>
                  <span className={styles.dateMon}>{d.month}</span>
                </button>
              ))}
            </div>

            {/* City Filter */}
            <div className={styles.cityFilterRow}>
              <span className={styles.filterLabel}>City:</span>
              <div className={styles.cityPills}>
                {cities.map(c => (
                  <button key={c.id}
                    className={`${styles.cityPill} ${selCity===c.id?styles.cityPillActive:''}`}
                    onClick={() => setSelCity(c.id)}>
                    {c.name}
                  </button>
                ))}
              </div>
            </div>

            {/* Theatres + Showtimes */}
            <div className={styles.theatresList}>
              {allShows.length === 0 && (
                <div className={styles.noShows}>
                  <div className={styles.noShowsIcon}>🎭</div>
                  <div>No shows available for this date in {cities.find(c=>c.id===selCity)?.name}</div>
                  <div style={{fontSize:13,color:'var(--t3)',marginTop:6}}>Try selecting a different date or city</div>
                </div>
              )}

              {theatresWithShows.length === 0 && allShows.length > 0 && (
                <div className={styles.noShows}>
                  <div>No shows in {cities.find(c=>c.id===selCity)?.name} for this date</div>
                  <div style={{fontSize:13,color:'var(--t3)',marginTop:6}}>Try a different city</div>
                </div>
              )}

              {theatresWithShows.map(theatre => {
                const tShows  = theatreShowMap[theatre.id] || []
                const tHalls  = halls.filter(h => h.theatreId === theatre.id)
                return (
                  <div key={theatre.id} className={styles.theatreRow}>
                    <div className={styles.theatreLeft}>
                      <div className={styles.theatreName}>{theatre.name}</div>
                      <div className={styles.theatreLoc}>📍 {theatre.location}</div>
                      <div className={styles.theatreFeats}>
                        {tHalls.flatMap(h=>h.features).filter((f,i,a)=>a.indexOf(f)===i).slice(0,4).map(f=>(
                          <span key={f} className={styles.featTag}>{f}</span>
                        ))}
                      </div>
                      <div className={styles.cancellable}>Cancellation available</div>
                    </div>
                    <div className={styles.theatreRight}>
                      <div className={styles.showsGrid}>
                        {tShows.map(s => {
                          const h    = halls.find(x => x.id === s.hallId)
                          const taken = Array.isArray(s.bookedSeats) ? s.bookedSeats.length : 0
                          const avail = h ? h.totalSeats - taken : 0
                          const isFull = avail === 0
                          const isFast = avail > 0 && avail < 20
                          return (
                            <button key={s.id}
                              className={`${styles.showBtn} ${isFull?styles.showFull:''} ${isFast?styles.showFast:''}`}
                              disabled={isFull}
                              onClick={() => handleSelectShow(s, theatre)}>
                              <span className={styles.showTime}>{s.time || s.showTime}</span>
                              {s.format && <span className={styles.showFormat}>{s.format}</span>}
                              {isFast && <span className={styles.fastFilling}>Fast Filling</span>}
                              {isFull && <span className={styles.fastFilling}>Housefull</span>}
                            </button>
                          )
                        })}
                      </div>
                    </div>
                  </div>
                )
              })}
            </div>
          </div>
        )}

        {/* ── STEP 2: Select Seats ── */}
        {step === 2 && hall && (
          <div className={styles.seatsWrap}>
            <div className={styles.seatsInner}>
              <div style={{display:'flex',justifyContent:'space-between',alignItems:'center',marginBottom:16}}>
                <h2 className={styles.sHead}>Select Seats</h2>
                <button className="btn-ghost" style={{padding:'8px 16px',fontSize:'12px'}} onClick={()=>setStep(1)}>← Change Show</button>
              </div>

              {/* Show summary bar */}
              <div className={styles.showSummaryBar}>
                <span>🎭 {selT?.name}</span>
                <span>·</span>
                <span>{hall?.name}</span>
                <span>·</span>
                <span>{selShow?.time || selShow?.showTime}</span>
                <span>·</span>
                <span>{new Date(selDate).toLocaleDateString('en-IN',{day:'numeric',month:'short',year:'numeric'})}</span>
                {selShow?.format && <><span>·</span><span className={styles.formatTag}>{selShow.format}</span></>}
              </div>

              <div className={styles.screenLabel}>— SCREEN —</div>
              <div className={styles.seatMap}>
                {hall.rows.map(row => (
                  <div key={row} className={styles.seatRow}>
                    <div className={styles.rowLabel}>{row}</div>
                    {Array.from({length:hall.seatsPerRow},(_,idx)=>{
                      const id=`${row}${idx+1}`, isB=bookedSts.includes(id), isSel=seats.includes(id), isP=PREM.includes(row)
                      const el=<div key={id} className={`${styles.seat} ${isP?styles.prem:''} ${isB?styles.booked:''} ${isSel?styles.sel:''}`} title={isB ? `${id} (Already Booked)` : id} onClick={()=>toggleSeat(id)}/>
                      if(idx===3||idx===9) return <span key={`g-${id}`} style={{display:'contents'}}><div className={styles.aisle}/>{el}</span>
                      return el
                    })}
                    <div className={styles.rowLabel}>{row}</div>
                  </div>
                ))}
              </div>

              <div className={styles.legend}>
                {[{l:'Available',st:{background:'var(--bg-surface)',borderColor:'rgba(201,168,76,0.2)'}},{l:'Selected',st:{background:'var(--gold)',borderColor:'var(--gold)'}},{l:'Booked',st:{background:'var(--bg-hover)',borderColor:'rgba(255,255,255,0.05)',opacity:0.4}},{l:'Premium (A-B)',st:{background:'var(--bg-surface)',borderColor:'rgba(192,33,58,0.4)'}}].map(x=>(
                  <div key={x.l} className={styles.legendItem}><div className={styles.legendSeat} style={x.st}/><span>{x.l}</span></div>
                ))}
              </div>

              <div className={styles.priceNote}>
                <span>🪑 Standard: ₹{movie.price?.standard}</span>
                <span>👑 Premium (A-B): ₹{movie.price?.premium}</span>
                <span>+ ₹{CONV} convenience fee per seat</span>
              </div>

              {seats.length > 0 && (
                <div className={styles.seatsSummary}>
                  <span>Selected: <strong>{seats.join(', ')}</strong></span>
                  <span>Tickets: <strong>₹{tickets()}</strong></span>
                  <span>Fee: <strong>₹{fee}</strong></span>
                  <span className={styles.totalInline}>Total: <strong>₹{total}</strong></span>
                </div>
              )}

              <div style={{display:'flex',gap:12,marginTop:20}}>
                <button className="btn-ghost" onClick={()=>setStep(1)}>← Back</button>
                <button className="btn-gold" style={{flex:1,justifyContent:'center'}}
                  onClick={()=>{if(seats.length===0){showToast('Select at least 1 seat','error');return};setStep(3)}}>
                  Proceed to Payment ({seats.length} seat{seats.length!==1?'s':''}) →
                </button>
              </div>
            </div>
          </div>
        )}

        {/* ── STEP 3: Payment ── */}
        {step === 3 && (
          <div className={styles.seatsWrap}>
            <div className={styles.seatsInner}>
              <div style={{display:'flex',justifyContent:'space-between',alignItems:'center',marginBottom:24}}>
                <h2 className={styles.sHead}>Payment</h2>
                <button className="btn-ghost" style={{padding:'8px 16px',fontSize:'12px'}} onClick={()=>setStep(2)}>← Change Seats</button>
              </div>

              {/* Order card */}
              <div className={styles.orderCard}>
                <div className={styles.orderCardRow}><span>Movie</span><span>{movie.title}</span></div>
                <div className={styles.orderCardRow}><span>Theatre</span><span>{selT?.name}</span></div>
                <div className={styles.orderCardRow}><span>Show</span><span>{selShow?.time || selShow?.showTime} · {new Date(selDate).toLocaleDateString('en-IN',{day:'numeric',month:'short',year:'numeric'})}</span></div>
                <div className={styles.orderCardRow}><span>Seats</span><span>{seats.join(', ')}</span></div>
                <div className={styles.orderCardRow}><span>Tickets</span><span>₹{tickets()}</span></div>
                <div className={styles.orderCardRow}><span>Convenience Fee</span><span>₹{fee}</span></div>
                <div className={`${styles.orderCardRow} ${styles.orderTotal}`}><span>Total Payable</span><span>₹{total}</span></div>
              </div>

              <label className="form-label" style={{marginTop:24,marginBottom:10,display:'block'}}>Select Payment Method</label>
              <div className={styles.payMethods}>
                {paymentMethods.map(pm => (
                  <div key={pm.id} className={`${styles.payCard} ${pay===pm.type?styles.payActive:''}`}
                    onClick={() => setPay(pm.type)}>
                    <div className={styles.payIcon}>{pm.icon}</div>
                    <div><div className={styles.payLabel}>{pm.label}</div><div className={styles.payDesc}>{pm.desc}</div></div>
                    <div className={`${styles.radio} ${pay===pm.type?styles.radioActive:''}`}/>
                  </div>
                ))}
              </div>

              {(pay==='Credit Card'||pay==='Net Banking') && (
                <div className={styles.cardForm}>
                  <div className={styles.twoCol}>
                    <div className="form-field"><label className="form-label">Card Number</label><div className="form-input-wrap"><span className="form-icon">💳</span><input className="form-input" placeholder="1234 5678 9012 3456" maxLength={19} value={cardNum} onChange={e=>setCardNum(e.target.value.replace(/\D/g,'').substring(0,16).replace(/(.{4})/g,'$1 ').trim())}/></div></div>
                    <div className="form-field"><label className="form-label">Cardholder Name</label><div className="form-input-wrap"><span className="form-icon">👤</span><input className="form-input" placeholder="Name on card" value={cardName} onChange={e=>setCardName(e.target.value)}/></div></div>
                    <div className="form-field"><label className="form-label">Expiry</label><div className="form-input-wrap"><span className="form-icon">📅</span><input className="form-input" placeholder="MM / YY" maxLength={7} value={expiry} onChange={e=>{const n=e.target.value.replace(/\D/g,'').substring(0,4);setExpiry(n.length>=2?n.substring(0,2)+' / '+n.substring(2):n)}}/></div></div>
                    <div className="form-field"><label className="form-label">CVV</label><div className="form-input-wrap"><span className="form-icon">🔒</span><input className="form-input" type="password" placeholder="•••" maxLength={3} value={cvv} onChange={e=>setCvv(e.target.value)}/></div></div>
                  </div>
                </div>
              )}

              {pay==='UPI' && (
                <div className={styles.cardForm}>
                  <div className="form-field"><label className="form-label">UPI ID</label><div className="form-input-wrap"><span className="form-icon">📱</span><input className="form-input" placeholder="yourname@upi" value={upi} onChange={e=>setUpi(e.target.value)}/></div></div>
                  <div style={{display:'flex',gap:8,flexWrap:'wrap',marginTop:4}}>
                    {['GPay','PhonePe','Paytm','BHIM'].map(a=><span key={a} style={{padding:'3px 10px',background:'var(--bg-surface)',border:'1px solid var(--border)',borderRadius:20,fontSize:11,color:'var(--text-muted)'}}>{a}</span>)}
                  </div>
                </div>
              )}

              {pay==='Cash' && (
                <div className={styles.cashInfo}>
                  <div style={{fontSize:28}}>💵</div>
                  <div><div className={styles.cashTitle}>Pay at Counter</div><div className={styles.cashDesc}>Arrive 15 minutes before showtime to pay at the theatre box office. Show your booking ID.</div></div>
                </div>
              )}

              <div style={{display:'flex',gap:12,marginTop:24}}>
                <button className="btn-ghost" onClick={()=>setStep(2)}>← Back</button>
                <button className="btn-gold" style={{flex:1,justifyContent:'center'}} onClick={handlePay} disabled={paying}>
                  {paying ? <span className="spinner"/> : `🎬 Pay ₹${total} & Confirm`}
                </button>
              </div>
            </div>
          </div>
        )}
      </div>
    </>
  )
}
