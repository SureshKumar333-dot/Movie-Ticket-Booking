// pages/search.jsx
import { useState, useEffect, useMemo } from 'react'
import Head from 'next/head'
import { useRouter } from 'next/router'
import Navbar from '@/components/Navbar'
import Toast  from '@/components/Toast'
import { getMovies } from '@/lib/crud'
import { cities, languages, genres } from '@/data/db'
import styles from '@/styles/Search.module.css'

export default function SearchPage() {
  const router = useRouter()
  const [movies, setMovies]   = useState([])
  const [loading, setLoading] = useState(true)
  const [filters, setFilters] = useState({ title:'', language:'', genre:'', cityId:'' })
  const set = (k, v) => setFilters(p => ({ ...p, [k]:v }))
  const hasFilter = Object.values(filters).some(v => v !== '')

  useEffect(() => {
    getMovies().then(r => setMovies(r.data||[])).catch(()=>{}).finally(() => setLoading(false))
  }, [])

  const results = useMemo(() => movies.filter(m => {
    const matchTitle = m.title.toLowerCase().includes(filters.title.toLowerCase())
    const matchLang  = !filters.language || filters.language==='All Languages' || m.language===filters.language
    const matchGenre = !filters.genre    || filters.genre==='All Genres'       || m.genre===filters.genre
    return matchTitle && matchLang && matchGenre
  }), [movies, filters])

  return (
    <>
      <Head><title>Search Movies — CineVerse</title></Head>
      <Navbar /><Toast />
      <div className={styles.page}>
        <div className={styles.header}>
          <div className={styles.headerInner}>
            <span className="section-eyebrow">Find Your Movie</span>
            <h1 className={styles.title}>Search Movies</h1>
            <p className={styles.sub}>Search by title, language, genre or city</p>
          </div>
        </div>
        <div className={styles.body}>
          <aside className={styles.sidebar}>
            <div className={styles.sideTitle}> Filter Movies</div>
            <div className="form-field">
              <label className="form-label">Movie Title</label>
              <div className="form-input-wrap"><span className="form-icon"></span>
                <input className="form-input" placeholder="Search by title..." value={filters.title} onChange={e=>set('title',e.target.value)}/>
              </div>
            </div>
            <div className="form-field">
              <label className="form-label">City</label>
              <div className="form-input-wrap"><span className="form-icon"></span>
                <select className="form-input form-select" value={filters.cityId} onChange={e=>set('cityId',e.target.value)}>
                  <option value="">All Cities</option>
                  {cities.map(c=><option key={c.id} value={c.id}>{c.name}</option>)}
                </select>
              </div>
            </div>
            <div className="form-field">
              <label className="form-label">Language</label>
              <div className="form-input-wrap"><span className="form-icon"></span>
                <select className="form-input form-select" value={filters.language} onChange={e=>set('language',e.target.value)}>
                  {languages.map(l=><option key={l}>{l}</option>)}
                </select>
              </div>
            </div>
            <div className="form-field">
              <label className="form-label">Genre</label>
              <div className="form-input-wrap"><span className="form-icon"></span>
                <select className="form-input form-select" value={filters.genre} onChange={e=>set('genre',e.target.value)}>
                  {genres.map(g=><option key={g}>{g}</option>)}
                </select>
              </div>
            </div>
            {hasFilter && <button className="btn-ghost" style={{width:'100%',justifyContent:'center'}}
              onClick={()=>setFilters({title:'',language:'',genre:'',cityId:''})}>Clear Filters</button>}
          </aside>
          <div className={styles.results}>
            <div className={styles.resultsHeader}>
              <span className={styles.count}>{loading?'Loading...':`${results.length} movie${results.length!==1?'s':''} found`}</span>
            </div>
            {!loading && results.length===0 ? (
              <div className={styles.empty}>
                <div className={styles.emptyIcon}>🎬</div>
                <div>No movies found</div>
                <button className="btn-ghost" style={{marginTop:16}} onClick={()=>setFilters({title:'',language:'',genre:'',cityId:''})}>Clear Filters</button>
              </div>
            ) : (
              <div className={styles.grid}>
                {results.map(m=>(
                  <div key={m.id} className={styles.card} onClick={()=>router.push(`/booking?movieId=${m.id}`)}>
                    <div className={styles.posterWrap}>
                      <img src={m.poster} alt={m.title} onError={e=>{e.target.src=`https://placehold.co/300x450/0F0F18/C9A84C?text=${encodeURIComponent(m.title)}`}}/>
                      {m.badge&&<div className={`badge badge-${m.badge}`}>{m.badge==='hot'?'🔥':'✦'}</div>}
                      <div className={styles.cardOverlay}><button className={styles.bookBtn}>Book Tickets</button></div>
                    </div>
                    <div className={styles.cardInfo}>
                      <div className={styles.cardTitle}>{m.title}</div>
                      <div className={styles.cardMeta}><span>⭐ {m.rating}</span><span>{m.language}</span><span>{m.duration}</span></div>
                      <div className={styles.cardTag}>{m.genre}</div>
                      <div className={styles.cardPrice}>From ₹{m.price?.standard}</div>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      </div>
    </>
  )
}
