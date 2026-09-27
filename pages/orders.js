// pages/orders.jsx — All Orders with stats, filters, expand, cancel
import { useState, useEffect, useMemo } from 'react'
import Head from 'next/head'
import Link from 'next/link'
import Navbar from '@/components/Navbar'
import Toast  from '@/components/Toast'
import { useApp } from '@/context/AppContext'
import { getAllBookings, cancelBooking } from '@/lib/crud'
import { movies } from '@/data/db'
import styles from '@/styles/Orders.module.css'

const CITY_NAMES = { CBE:'Coimbatore',CHN:'Chennai',MDU:'Madurai',TRY:'Trichy',SLM:'Salem',TNV:'Tirunelveli',VLR:'Vellore',ERD:'Erode' }

export default function OrdersPage() {
  const { currentUser, showToast } = useApp()
  const [orders,     setOrders]     = useState([])
  const [loading,    setLoading]    = useState(true)
  const [search,     setSearch]     = useState('')
  const [statusF,    setStatusF]    = useState('All Status')
  const [payF,       setPayF]       = useState('All Payments')
  const [expandedId, setExpanded]   = useState(null)
  const [cancelling, setCancelling] = useState(null)

  useEffect(() => {
    setLoading(true)
    getAllBookings()
      .then(r => {
        const data = r.data || []
        setOrders(currentUser ? data.filter(b => b.customerId === currentUser.id) : data)
      })
      .catch(() => showToast('Failed to load orders','error'))
      .finally(() => setLoading(false))
  }, [currentUser])

  const filtered = useMemo(() => orders.filter(o => {
    const s   = search.toLowerCase()
    const ms  = !s || o.movieTitle?.toLowerCase().includes(s) || o.theatreName?.toLowerCase().includes(s) || o.id?.toLowerCase().includes(s) || o.seats?.join(' ').includes(s)
    const mSt = statusF==='All Status'   || o.status===statusF
    const mPy = payF==='All Payments'    || o.paymentMethod===payF
    return ms && mSt && mPy
  }), [orders, search, statusF, payF])

  const stats = useMemo(() => ({
    total:     orders.length,
    confirmed: orders.filter(o=>o.status==='CONFIRMED').length,
    cancelled: orders.filter(o=>o.status==='CANCELLED').length,
    spent:     orders.filter(o=>o.status==='CONFIRMED').reduce((s,o)=>s+(o.totalAmount||0),0),
    tickets:   orders.filter(o=>o.status==='CONFIRMED').reduce((s,o)=>s+(o.seats?.length||0),0),
  }), [orders])

  const hasFilter = search || statusF!=='All Status' || payF!=='All Payments'

  const handleCancel = async (id) => {
    if (!confirm('Cancel this booking?')) return
    setCancelling(id)
    try {
      await cancelBooking(id)
      setOrders(prev => prev.map(o => o.id===id ? {...o,status:'CANCELLED'} : o))
      showToast('Booking cancelled!')
    } catch(e) { showToast(e.message,'error') }
    finally { setCancelling(null) }
  }

  const fmt  = d => d ? new Date(d).toLocaleDateString('en-IN',{day:'numeric',month:'short',year:'numeric'}) : 'N/A'
  const fmtT = d => d ? new Date(d).toLocaleString('en-IN',{day:'numeric',month:'short',year:'numeric',hour:'2-digit',minute:'2-digit'}) : 'N/A'

  return (
    <>
      <Head><title>All Orders — CineVerse</title></Head>
      <Navbar/><Toast/>
      <div className={styles.page}>

        {/* Header */}
        <div className={styles.header}>
          <div className={styles.headerInner}>
            <div>
              <span className="section-eyebrow">Booking History</span>
              <h1 className={styles.title}>{currentUser ? `${currentUser.name}'s Orders` : 'All Orders'}</h1>
              <p className={styles.sub}>View, track and manage all movie ticket bookings</p>
            </div>
            <Link href="/search" className="btn-gold">+ Book New Ticket</Link>
          </div>
        </div>

        

        <div className={styles.body}>
          {/* Sidebar */}
          <aside className={styles.sidebar}>
            <div className={styles.sideTitle}> Filter Orders</div>
            <div className="form-field">
              <label className="form-label">Search</label>
              <div className="form-input-wrap"><span className="form-icon"></span>
                <input className="form-input" placeholder="Movie, theatre, ID, seat..." value={search} onChange={e=>setSearch(e.target.value)}/>
              </div>
            </div>
            <div className="form-field">
              <label className="form-label">Status</label>
              <div className="form-input-wrap"><span className="form-icon"></span>
                <select className="form-input form-select" value={statusF} onChange={e=>setStatusF(e.target.value)}>
                  {['All Status','CONFIRMED','CANCELLED'].map(s=><option key={s}>{s}</option>)}
                </select>
              </div>
            </div>
            <div className="form-field">
              <label className="form-label">Payment Method</label>
              <div className="form-input-wrap"><span className="form-icon"></span>
                <select className="form-input form-select" value={payF} onChange={e=>setPayF(e.target.value)}>
                  {['All Payments','UPI','Credit Card','Debit Card','Cash','Net Banking'].map(p=><option key={p}>{p}</option>)}
                </select>
              </div>
            </div>
            {hasFilter&&<button className="btn-ghost" style={{width:'100%',justifyContent:'center',marginTop:4}} onClick={()=>{setSearch('');setStatusF('All Status');setPayF('All Payments')}}>Clear Filters</button>}

            <div className={styles.quickSection}>
              <div className={styles.quickTitle}>Quick Filters</div>
              <div className={styles.quickPills}>
                {[{l:'✅ Confirmed',fn:()=>setStatusF('CONFIRMED')},
                {l:'❌ Cancelled',fn:()=>setStatusF('CANCELLED')},
                {l:'📱 UPI',fn:()=>setPayF('UPI')},
                {l:'💳 Card',fn:()=>setPayF('Credit Card')},
                {l:'💵 Cash',fn:()=>setPayF('Cash')}].map(f=>(
                  <button key={f.l} className={styles.quickPill} onClick={f.fn}>{f.l}</button>
                ))}
              </div>
            </div>
          </aside>

          {/* Orders */}
          <div className={styles.orderSection}>
            <div className={styles.listHeader}>
              <span className={styles.countTxt}>{loading?'Loading...':`${filtered.length} order${filtered.length!==1?'s':''} found`}</span>
              <span className={styles.sortTxt}>Sorted by: Latest First</span>
            </div>

            {!loading && filtered.length===0 && (
              <div className={styles.empty}>
                <div className={styles.emptyIcon}>🎟</div>
                <div className={styles.emptyTitle}>No orders found</div>
                <div className={styles.emptySub}>Try adjusting your filters or book a new ticket</div>
                <Link href="/search" className="btn-gold" style={{marginTop:20}}>Book a Ticket</Link>
              </div>
            )}

            {filtered.map(order => {
              const movie      = movies.find(m=>m.id===order.movieId)
              const displayTitle = movie?.title || order.movieTitle || 'Unknown Movie'
              const isExpanded = expandedId===order.id
              return (
                <div key={order.id} className={`${styles.orderCard} ${order.status==='CANCELLED'?styles.cancelledCard:''}`}>
                  <div className={styles.cardTop}>

                    {/* Poster */}
                    <div className={styles.posterWrap}>
                      <img src={movie?.poster} alt={displayTitle} className={styles.poster} onError={e=>{e.target.src=`https://placehold.co/70x100/0F0F18/C9A84C?text=${encodeURIComponent(displayTitle.slice(0,6)||'Movie')}`}}/>
                    </div>

                    {/* Info */}
                    <div className={styles.orderInfo}>
                      <div className={styles.orderMovie}>{displayTitle}</div>
                      <div className={styles.orderMeta}>
                        <span>🎭 {order.theatreName}</span>
                        <span>🏛 {order.hallName}</span>
                        <span>📅 {fmt(order.showDate)}</span>
                        <span>🕐 {order.showTime}</span>
                        {order.cityId&&<span>📍 {CITY_NAMES[order.cityId]||order.cityId}</span>}
                      </div>
                      <div className={styles.orderSeats}>🪑 Seats: <strong>{order.seats?.join(', ')}</strong><span className={styles.seatTypeBadge}>{order.seatType}</span></div>
                      <div className={styles.orderId}>Booking ID: <span>#{order.id}</span></div>
                      <div className={styles.bookedOn}>Booked on {fmtT(order.bookedOn)}</div>
                    </div>

                    {/* Right */}
                    <div className={styles.orderRight}>
                      <div className={`${styles.statusBadge} ${order.status==='CONFIRMED'?styles.confirmed:styles.cancelledBadge}`}>
                        {order.status==='CONFIRMED'?'✓ Confirmed':'✗ Cancelled'}
                      </div>
                      <div className={`${styles.payBadge} ${order.paymentStatus==='SUCCESS'?styles.payOk:styles.payPend}`}>
                        {order.paymentStatus==='SUCCESS'?'✓ Paid':'⌛ Pending'}
                      </div>
                      <div className={styles.amount}>₹{order.totalAmount?.toLocaleString('en-IN')}</div>
                      <div className={styles.payMethod}>{order.paymentMethod}</div>
                      <div className={styles.cardActions}>
                        <button className={styles.expandBtn} onClick={()=>setExpanded(isExpanded?null:order.id)}>
                          {isExpanded?'▲ Less':'▼ Details'}
                        </button>
                        {order.status==='CONFIRMED'&&(
                          <button className="btn-danger" style={{fontSize:'11px',padding:'6px 12px'}} disabled={cancelling===order.id} onClick={()=>handleCancel(order.id)}>
                            {cancelling===order.id?'...':'Cancel'}
                          </button>
                        )}
                      </div>
                    </div>
                  </div>

                  {/* Expanded */}
                  {isExpanded&&(
                    <div className={styles.expanded}>
                      <div className={styles.expandGrid}>
                        <div className={styles.expandBlock}>
                          <div className={styles.expandTitle}>📋 Booking Details</div>
                          <table className={styles.detailTable}><tbody>
                            {[['Booking ID',order.id],['Movie',displayTitle],['Theatre',order.theatreName],['Hall',order.hallName],['Show Date',fmt(order.showDate)],['Show Time',order.showTime],['Seats',order.seats?.join(', ')],['Seat Type',order.seatType],['No. of Tickets',order.seats?.length],['Booking Status',order.status]].map(([k,v])=>(
                              <tr key={k}><td className={styles.tdLabel}>{k}</td><td className={`${styles.tdVal} ${k==='Booking Status'?(order.status==='CONFIRMED'?styles.green:styles.red):''}`}>{v}</td></tr>
                            ))}
                          </tbody></table>
                        </div>
                        <div className={styles.expandBlock}>
                          <div className={styles.expandTitle}>💳 Payment Details</div>
                          <table className={styles.detailTable}><tbody>
                            {[['Payment Method',order.paymentMethod],['Payment Status',order.paymentStatus],['Ticket Amount',`₹${order.ticketAmount?.toLocaleString('en-IN')}`],['Convenience Fee',`₹${order.convenienceFee?.toLocaleString('en-IN')}`],['Total Paid',`₹${order.totalAmount?.toLocaleString('en-IN')}`],['Booked On',fmtT(order.bookedOn)]].map(([k,v])=>(
                              <tr key={k} className={k==='Total Paid'?styles.totalRow:''}><td className={styles.tdLabel}>{k}</td><td className={`${styles.tdVal} ${k==='Payment Status'?(order.paymentStatus==='SUCCESS'?styles.green:styles.yellow):''}`}>{v}</td></tr>
                            ))}
                          </tbody></table>
                        </div>
                        <div className={styles.expandBlock} style={{gridColumn:'1/-1'}}>
                          <div className={styles.expandTitle}>📄 Raw JSON Data</div>
                          <div className={styles.jsonBox}>
                            <div className={styles.jsonDots}><span style={{background:'#FF5F57'}}/><span style={{background:'#FFBD2E'}}/><span style={{background:'#28CA41'}}/></div>
                            <pre className={styles.jsonPre}>{JSON.stringify(order,null,2)}</pre>
                          </div>
                        </div>
                      </div>
                    </div>
                  )}
                </div>
              )
            })}
          </div>
        </div>
      </div>
    </>
  )
}
