// context/AppContext.jsx — Global App Context with Spring Boot & MySQL CRUD
import { createContext, useContext, useState, useEffect, useCallback } from 'react'
import {
  loginCustomer,
  createCustomer,
  updateCustomer  as apiUpdateCustomer,
  cancelBooking   as apiCancelBooking,
  changeBooking   as apiChangeBooking,
  getCancellations,
  getChanges,
  setAuthToken,
  getAuthToken,
} from '@/lib/crud'

const Ctx = createContext(null)

export function AppProvider({ children }) {
  const [currentUser, setUser]   = useState(null)
  const [toast,       setToast]  = useState(null)
  const [loading,     setLoading]= useState(false)

  // Restore user session on mount
  useEffect(() => {
    try {
      const savedUser = localStorage.getItem('cineverse_current_user')
      const token = getAuthToken()
      if (savedUser && token) {
        setUser(JSON.parse(savedUser))
      }
    } catch {}
  }, [])

  /* ── Toast ── */
  const showToast = useCallback((msg, type = 'info') => {
    setToast({ msg, type })
    setTimeout(() => setToast(null), 3200)
  }, [])

  /* ── Auth ── */
  const signIn = useCallback(async (email, password) => {
    setLoading(true)
    try {
      const res = await loginCustomer(email, password)
      const data = res.data || {}
      const token = data.token
      const user = data.user || data

      if (token) setAuthToken(token)
      if (user) {
        setUser(user)
        try {
          localStorage.setItem('cineverse_current_user', JSON.stringify(user))
        } catch {}
      }

      showToast(`Welcome back, ${user.name || 'User'}! 🎬`)
      return { success: true, user }
    } catch (e) {
      showToast(e.message, 'error')
      return { success: false, error: e.message }
    } finally { setLoading(false) }
  }, [showToast])

  const signUp = useCallback(async (formData) => {
    setLoading(true)
    try {
      const res = await createCustomer(formData)
      const data = res.data || {}
      const token = data.token
      const user = data.user || data

      if (token) setAuthToken(token)
      if (user) {
        setUser(user)
        try {
          localStorage.setItem('cineverse_current_user', JSON.stringify(user))
        } catch {}
      }

      showToast(`Welcome to CineVerse, ${user.name || 'User'}! 🎉`)
      return { success: true, user }
    } catch (e) {
      showToast(e.message, 'error')
      return { success: false, error: e.message }
    } finally { setLoading(false) }
  }, [showToast])

  const signOut = useCallback(() => {
    setAuthToken(null)
    setUser(null)
    try {
      localStorage.removeItem('cineverse_current_user')
    } catch {}
    showToast('Signed out successfully!')
  }, [showToast])

  /* ── Profile ── */
  const updateProfile = useCallback(async (updates) => {
    if (!currentUser) return
    setLoading(true)
    try {
      const res = await apiUpdateCustomer(currentUser.id, updates)
      const updated = { ...currentUser, ...(res.data || {}) }
      setUser(updated)
      try {
        localStorage.setItem('cineverse_current_user', JSON.stringify(updated))
      } catch {}
      showToast('Profile updated! ✅')
      return { success: true }
    } catch (e) {
      showToast(e.message, 'error')
      return { success: false }
    } finally { setLoading(false) }
  }, [currentUser, showToast])

  /* ── Cancel Booking (CRUD Delete / Status Update in MySQL) ── */
  const cancelUserBooking = useCallback(async (bookingId, reason = '') => {
    try {
      const res = await apiCancelBooking(bookingId, reason)
      showToast(res.message || 'Booking cancelled! Refund in 5-7 days. 💸')
      return { success: true, data: res.data }
    } catch (e) {
      showToast(e.message, 'error')
      return { success: false, error: e.message }
    }
  }, [showToast])

  /* ── Change Booking (CRUD Update & Audit Log in MySQL) ── */
  const changeUserBooking = useCallback(async (bookingId, updates, changeType, description = '') => {
    try {
      const res = await apiChangeBooking(bookingId, updates, changeType, description)
      showToast(res.message || 'Booking updated! ✅')
      return { success: true, data: res.data, updatedBooking: res.updatedBooking }
    } catch (e) {
      showToast(e.message, 'error')
      return { success: false, error: e.message }
    }
  }, [showToast])

  /* ── History Helpers ── */
  const fetchUserCancellations = useCallback(async () => {
    if (!currentUser) return []
    try {
      const res = await getCancellations({ customerId: currentUser.id })
      return res.data || []
    } catch { return [] }
  }, [currentUser])

  const fetchUserChanges = useCallback(async () => {
    if (!currentUser) return []
    try {
      const res = await getChanges({ customerId: currentUser.id })
      return res.data || []
    } catch { return [] }
  }, [currentUser])

  return (
    <Ctx.Provider value={{
      currentUser,
      toast,
      loading,
      showToast,
      signIn,
      signUp,
      signOut,
      updateProfile,
      cancelUserBooking,
      changeUserBooking,
      fetchUserCancellations,
      fetchUserChanges,
    }}>
      {children}
    </Ctx.Provider>
  )
}

export const useApp = () => useContext(Ctx)
