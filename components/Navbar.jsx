// components/Navbar.jsx
import { useState } from 'react'
import Link from 'next/link'
import { useApp } from '@/context/AppContext'
import styles from './Navbar.module.css'

export default function Navbar() {
  const { currentUser, signOut } = useApp()
  const [open, setOpen] = useState(false)

  return (
    <nav className={styles.nav}>
      <Link href="/" className={styles.logo}>Cine<span>Verse</span></Link>

      <ul className={styles.links}>
        <li><Link href="/#movies">Now Showing</Link></li>
        <li><Link href="/search">Search</Link></li>
        <li><Link href="/orders">All Orders</Link></li>
        <li><Link href="/#cinemas">Cinemas</Link></li>
        <li><Link href="/#offers">Offers</Link></li>
      </ul>

      <div className={styles.actions}>
        {currentUser ? (
          <div className={styles.userMenu}>
            <div className={styles.avatar} onClick={() => setOpen(p => !p)}>
              {currentUser.avatar}
            </div>
            {open && (
              <div className={styles.dropdown}>
                <div className={styles.dropHead}>
                  <div className={styles.dropAv}>{currentUser.avatar}</div>
                  <div>
                    <div className={styles.dropName}>{currentUser.name}</div>
                    <div className={styles.dropEmail}>{currentUser.email}</div>
                  </div>
                </div>
                <div className={styles.divider} />
                <Link href="/profile" className={styles.dropLink} onClick={() => setOpen(false)}>👤 My Profile</Link>
                <Link href="/orders"  className={styles.dropLink} onClick={() => setOpen(false)}>📋 My Orders</Link>
                <div className={styles.divider} />
                <button className={styles.signOut} onClick={() => { signOut(); setOpen(false) }}>
                  Sign Out
                </button>
              </div>
            )}
          </div>
        ) : (
          <>
            <Link href="/signin" className="btn-ghost" style={{ padding:'8px 20px', fontSize:'13px' }}>Sign In</Link>
            <Link href="/signup" className="btn-gold"  style={{ padding:'8px 20px', fontSize:'13px' }}>Sign Up</Link>
          </>
        )}
      </div>
    </nav>
  )
}
