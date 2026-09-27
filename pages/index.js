// pages/index.jsx — Home Page (Pages Router)
import { useState, useEffect, useMemo, useRef } from 'react'
import Head from 'next/head'
import Link from 'next/link'
import { useRouter } from 'next/router'
import Navbar from '@/components/Navbar'
import Toast  from '@/components/Toast'
import { getMovies, getTheatres } from '@/lib/crud'
import { cities } from '@/data/db'
import styles from '@/styles/Home.module.css'

const GENRES = ['all','Action','Drama','Comedy','Thriller','Romance','Sci-Fi']
const FILM_URLS = [
  './biker.webp',
  './dhurandhar.webp',
  './leader.webp',
  './satan.webp',
  './vaazha2.webp',
]

export default function HomePage() {
  const router = useRouter()
  const [movies, setMovies]     = useState([])
  const [theatres, setTheatres] = useState([])
  const [moviesError, setMoviesError] = useState('')
  const [moviesLoading, setMoviesLoading] = useState(true)
  const [activeGenre, setGenre] = useState('all')
  const [selectedCity, setCity] = useState('CBE')
  const stripRef = useRef(null)
  const rafRef   = useRef(null)

  useEffect(() => {
    setMoviesLoading(true)
    getMovies()
      .then(r => {
        setMovies(r.data || [])
        setMoviesError('')
      })
      .catch((err) => {
        setMovies([])
        setMoviesError(err.message || 'Could not load movies. Start the Spring Boot backend on port 8080.')
      })
      .finally(() => setMoviesLoading(false))
  }, [])
  useEffect(() => { getTheatres(selectedCity).then(r => setTheatres(r.data||[])).catch(()=>{}) }, [selectedCity])

  // Parallax scroll effect
  useEffect(() => {
    const onScroll = () => {
      if (!stripRef.current) return
      if (rafRef.current) cancelAnimationFrame(rafRef.current)
      rafRef.current = requestAnimationFrame(() => {
        if (stripRef.current) stripRef.current.style.transform = `translateY(${window.scrollY*0.45}px)`
      })
    }
    window.addEventListener('scroll', onScroll, { passive:true })
    return () => { window.removeEventListener('scroll', onScroll); if(rafRef.current) cancelAnimationFrame(rafRef.current) }
  }, [])

  const filtered = useMemo(
    () => movies.filter(m => activeGenre === 'all' || (m.genre || '').includes(activeGenre)),
    [movies, activeGenre]
  )
  const filmImages = [...FILM_URLS, ...FILM_URLS]

  return (
    <>
      <Head>
        <title>CineVerse — Book Movie Tickets Online</title>
        
      </Head>
      <Navbar />
      <Toast />

      {/* ── HERO ── */}
      <section className={styles.hero}>
        <div className={styles.heroBg} />

        {/* Parallax Film Strip */}
        <div className={styles.stripWrapper}>
          <div className={styles.strip} ref={stripRef}>
            {filmImages.map((url, i) => (
              <div key={i} className={styles.frame}>
                <div className={styles.sprockets}>
                  {[0,1,2,3].map(h => <div key={h} className={styles.hole} />)}
                </div>
                <div className={styles.frameInner}>
                  <img src={url} alt="movie" className={styles.frameImg}
                    onError={e => { e.target.style.display='none' }} />
                  <div className={styles.frameOverlay} />
                </div>
                <div className={styles.sprockets}>
                  {[0,1,2,3].map(h => <div key={h} className={styles.hole} />)}
                </div>
              </div>
            ))}
          </div>
          <div className={styles.fadeTop} />
          <div className={styles.fadeBottom} />
          <div className={styles.fadeLeft} />
        </div>

        <div className={styles.heroContent}>
          <div className={styles.eyebrow}>
            <div className={styles.eyebrowLine} />
            <span>Premium Cinema Experience</span>
          </div>
          <h1 className={styles.heroTitle}>The Big Screen<br />Awaits <em>You.</em></h1>
          <p className={styles.heroDesc}>
            Book tickets for theatres across Tamil Nadu. Select city, theatre, seats and confirm in seconds.
          </p>
          <div className={styles.heroActions}>
            <Link href="/search" className="btn-gold">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
                <polygon points="5 3 19 12 5 21 5 3" />
              </svg>
              Book Tickets
            </Link>
            <Link href="/search" className="btn-ghost">Search Movies</Link>
          </div>
          <div className={styles.heroStats}>
            {[{n:'8',l:'Cities'},{n:'14+',l:'Theatres'},{n:'10',l:'Movies'},{n:'4.9★',l:'Rating'}].map(s => (
              <div key={s.l} className={styles.stat}>
                <span className={styles.statNum}>{s.n}</span>
                <span className={styles.statLabel}>{s.l}</span>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* ── NOW SHOWING ── */}
      <div className="section" id="movies">
        <div className="section-header">
          <div>
            <span className="section-eyebrow">On Screen Now</span>
            <h2 className="section-title">Now Showing</h2>
          </div>
          <Link href="/search" className={styles.seeAll}>View All →</Link>
        </div>

        <div className={styles.genreTabs}>
          {GENRES.map(g => (
            <button key={g}
              className={`${styles.genreTab} ${activeGenre===g ? styles.genreActive : ''}`}
              onClick={() => setGenre(g)}>
              {g === 'all' ? 'All Movies' : g}
            </button>
          ))}
        </div>

        <div className={styles.moviesGrid}>
          {moviesLoading && <div className={styles.loading}>Loading movies...</div>}
          {!moviesLoading && moviesError && <div className={styles.loading}>{moviesError}</div>}
          {!moviesLoading && !moviesError && filtered.length === 0 && <div className={styles.loading}>No movies found.</div>}
          {filtered.map((m, i) => (
            <div key={m.id} className={styles.movieCard} style={{ animationDelay:`${i*0.06}s` }}
              onClick={() => router.push(`/booking?movieId=${m.id}`)}>
              <div className={styles.posterWrap}>
                <img src={m.poster} alt={m.title} className={styles.posterImg}
                  onError={e => { e.target.src = `https://placehold.co/300x450/0F0F18/C9A84C?text=${encodeURIComponent(m.title)}` }} />
                {m.badge && <div className={`badge badge-${m.badge}`}>{m.badge==='hot'?'🔥 Hot':'✦ New'}</div>}
                <div className={styles.cardOverlay}>
                  <button className={styles.bookBtn}>Book Now</button>
                </div>
              </div>
              <div className={styles.cardInfo}>
                <div className={styles.cardTitle}>{m.title}</div>
                <div className={styles.cardMeta}>
                  <span>⭐ {m.rating}</span><span>{m.language}</span><span>{m.duration}</span>
                </div>
                <div className={styles.cardTags}><span className={styles.tag}>{m.genre}</span></div>
                <div className={styles.cardCast}>{m.cast?.split(",")[0]}</div>
                <div className={styles.cardPrice}>From ₹{m.price?.standard}</div>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* ── CINEMAS BY CITY ── */}
      <div className={styles.cinemasSection} id="cinemas">
        <div className="section">
          <div className="section-header">
            <div>
              <span className="section-eyebrow">Find Near You</span>
              <h2 className="section-title">Cinemas by City</h2>
            </div>
          </div>
          <div className={styles.cityTabs}>
            {cities.map(c => (
              <button key={c.id}
                className={`${styles.cityTab} ${selectedCity===c.id ? styles.cityActive : ''}`}
                onClick={() => setCity(c.id)}>
                {c.name}
              </button>
            ))}
          </div>
          <div className={styles.theatresGrid}>
            {theatres.slice(0, 6).map(t => (
              <div key={t.id} className={styles.theatreCard}>
                <div className={styles.theatreIcon}>🎭</div>
                <div className={styles.theatreName}>{t.name}</div>
                <div className={styles.theatreLoc}>📍 {t.location}</div>
                <div className={styles.theatreHalls}>{t.halls.length} Hall{t.halls.length>1?'s':''}</div>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* ── OFFERS ── */}
      <div className="section" id="offers">
        <div className="section-header">
          <div>
            <span className="section-eyebrow">Save More</span>
            <h2 className="section-title">Exclusive Offers</h2>
          </div>
        </div>
        <div className={styles.offersGrid}>
          {[
            { type:'featured', label:'Limited Time',  headline:'Buy 2 Get\n1 Free',       desc:'Every Tuesday enjoy 3 tickets for the price of 2.', code:'TRIPLE2X'   },
            { type:'gold',     label:'Members Only',  headline:'30% Off\nWeekends',        desc:'CineVerse members get exclusive weekend discounts.',  code:'GOLD30'     },
            { type:'dark',     label:'First Order',   headline:'₹100 Off\nFirst Booking', desc:'New? Get ₹100 off your very first booking.',          code:'WELCOME100' },
          ].map(o => (
            <div key={o.code} className={`${styles.offerCard} ${styles[o.type]}`}>
              <div className={styles.offerLabel}>{o.label}</div>
              <div className={styles.offerHeadline}>
                {o.headline.split('\n').map((l, i) => <span key={i}>{l}{i===0 && <br />}</span>)}
              </div>
              <div className={styles.offerDesc}>{o.desc}</div>
              <div className={styles.offerCode}>{o.code}</div>
            </div>
          ))}
        </div>
      </div>

      {/* ── FOOTER ── */}
      <footer className={styles.footer}>
        <div className={styles.footerInner}>
          <div>
            <div className={styles.footerLogo}>CineVerse</div>
            <p className={styles.footerDesc}>Your gateway to cinema across Tamil Nadu. Built with Next.js 14 Pages Router.</p>
          </div>
          {[
            { title:'Movies',  links:['Now Showing','Coming Soon','Top Rated'] },
            { title:'Cinemas', links:['Find Near Me','IMAX','Premium'] },
            { title:'Help',    links:['FAQ','Refund Policy','Contact'] },
          ].map(col => (
            <div key={col.title}>
              <div className={styles.footerColTitle}>{col.title}</div>
              <ul className={styles.footerLinks}>
                {col.links.map(l => <li key={l}><a href="#">{l}</a></li>)}
              </ul>
            </div>
          ))}
        </div>
        <div className={styles.footerBottom}>
          <span>© 2026 CineVerse. All rights reserved.</span>
          <span>Next.js 14 Pages Router 🎬</span>
        </div>
      </footer>
    </>
  )
}
