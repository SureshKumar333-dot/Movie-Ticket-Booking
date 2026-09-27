const BASE = process.env.NEXT_PUBLIC_API_URL || '/api'

let memoryToken = null

export const setAuthToken = (token) => {
  memoryToken = token
  if (typeof window !== 'undefined') {
    if (token) {
      localStorage.setItem('cineverse_jwt_token', token)
    } else {
      localStorage.removeItem('cineverse_jwt_token')
    }
  }
}

export const getAuthToken = () => {
  if (memoryToken) return memoryToken
  if (typeof window !== 'undefined') {
    return localStorage.getItem('cineverse_jwt_token')
  }
  return null
}

async function req(url, method = 'GET', body = null) {
  const headers = { 'Content-Type': 'application/json' }
  const token = getAuthToken()
  if (token) {
    headers['Authorization'] = `Bearer ${token}`
  }

  const opts = { method, headers }
  if (body) opts.body = JSON.stringify(body)

  try {
    const res = await fetch(url, opts)
    let data
    try {
      data = await res.json()
    } catch {
      data = {}
    }

    if (!res.ok) {
      const errorMsg = data.message || data.error || (data.errors && Object.values(data.errors).join(', ')) || `Request failed (${res.status})`
      throw new Error(errorMsg)
    }
    return data
  } catch (err) {
    throw err
  }
}

function buildQuery(params = {}) {
  const q = new URLSearchParams()
  Object.entries(params).forEach(([k, v]) => {
    if (v !== undefined && v !== null && v !== '') {
      q.append(k, v)
    }
  })
  const str = q.toString()
  return str ? `?${str}` : ''
}

import { movies as mockMovies, theatres as mockTheatres, shows as mockShows } from '@/data/db'

// ─── MOVIES ───────────────────────────────────────────────────────────────────
export const getMovies = async (p = {}) => {
  try {
    const res = await req(`${BASE}/movies${buildQuery(p)}`)
    if (res && res.data && res.data.length > 0) return res
  } catch (err) {
    console.warn('API getMovies fallback:', err.message)
  }
  return { success: true, data: mockMovies, count: mockMovies.length }
}

export const getMovieById = async (id) => {
  try {
    const res = await req(`${BASE}/movies/${id}`)
    if (res && res.data) return res
  } catch (err) {
    console.warn('API getMovieById fallback:', err.message)
  }
  const m = mockMovies.find(x => x.id === id)
  if (m) return { success: true, data: m }
  throw new Error('Movie not found')
}
export const createMovie  = (b)      => req(`${BASE}/movies`, 'POST', b)
export const updateMovie  = (id, b)  => req(`${BASE}/movies/${id}`, 'PUT', b)
export const deleteMovie  = (id)     => req(`${BASE}/movies/${id}`, 'DELETE')

// ─── CUSTOMERS & AUTH ─────────────────────────────────────────────────────────
export const getCustomers    = ()       => req(`${BASE}/customers`)
export const getCustomerById = (id)     => req(`${BASE}/customers/${id}`)
export const loginCustomer   = (e, p)   => req(`${BASE}/customers/login`, 'POST', { email: e, password: p })
export const registerAuth    = (b)      => req(`${BASE}/auth/register`, 'POST', b)
export const loginAuth       = (e, p)   => req(`${BASE}/auth/login`, 'POST', { email: e, password: p })
export const createCustomer  = (b)      => req(`${BASE}/customers`, 'POST', b)
export const updateCustomer  = (id, b)  => req(`${BASE}/customers/${id}`, 'PUT', b)
export const deleteCustomer  = (id)     => req(`${BASE}/customers/${id}`, 'DELETE')

// ─── BOOKINGS ─────────────────────────────────────────────────────────────────
export const getAllBookings         = (p = {}) => req(`${BASE}/bookings${buildQuery(p)}`)
export const getBookingsByCustomer = (cId)    => req(`${BASE}/bookings?customerId=${encodeURIComponent(cId)}`)
export const getBookingById        = (id)     => req(`${BASE}/bookings/${id}`)
export const createBooking         = (b)      => req(`${BASE}/bookings`, 'POST', b)
export const updateBooking         = (id, b)  => req(`${BASE}/bookings/${id}`, 'PUT', b)
export const deleteBooking         = (id)     => req(`${BASE}/bookings/${id}`, 'DELETE')

// ─── CANCELLATIONS ────────────────────────────────────────────────────────────
export const cancelBooking            = (bookingId, reason) => req(`${BASE}/cancellations`, 'POST', { bookingId, reason })
export const getCancellations         = (p = {})            => req(`${BASE}/cancellations${buildQuery(p)}`)
export const getCancellationById      = (id)                => req(`${BASE}/cancellations/${id}`)
export const updateCancellationStatus = (id, b)             => req(`${BASE}/cancellations/${id}`, 'PUT', b)

// ─── CHANGES ──────────────────────────────────────────────────────────────────
export const changeBooking = (bookingId, updates, changeType, description) =>
  req(`${BASE}/changes`, 'POST', { bookingId, updates, changeType, description })
export const getChanges    = (p = {}) => req(`${BASE}/changes${buildQuery(p)}`)
export const getChangeById = (id)     => req(`${BASE}/changes/${id}`)

// ─── THEATRES ─────────────────────────────────────────────────────────────────
export const getTheatres = async (city = '') => {
  try {
    const res = await req(`${BASE}/theatres${city ? `?city=${encodeURIComponent(city)}` : ''}`)
    if (res && res.data && res.data.length > 0) return res
  } catch (err) {
    console.warn('API getTheatres fallback:', err.message)
  }
  const list = city ? mockTheatres.filter(t => t.city === city) : mockTheatres
  return { success: true, data: list, count: list.length }
}
export const getTheatreById = (id)        => req(`${BASE}/theatres/${id}`)
export const createTheatre  = (b)         => req(`${BASE}/theatres`, 'POST', b)
export const updateTheatre  = (id, b)     => req(`${BASE}/theatres/${id}`, 'PUT', b)
export const deleteTheatre  = (id)        => req(`${BASE}/theatres/${id}`, 'DELETE')

// ─── SHOWS ────────────────────────────────────────────────────────────────────
export const getShows = async (p = {}) => {
  try {
    const res = await req(`${BASE}/shows${buildQuery(p)}`)
    if (res && res.data && res.data.length > 0) return res
  } catch (err) {
    console.warn('API getShows fallback:', err.message)
  }
  let list = [...mockShows]
  if (p.movieId) list = list.filter(s => s.movieId === p.movieId)
  if (p.theatreId) list = list.filter(s => s.theatreId === p.theatreId)
  if (p.hallId) list = list.filter(s => s.hallId === p.hallId)
  if (p.date) {
    const exact = list.filter(s => s.date === p.date || s.showDate === p.date)
    if (exact.length > 0) {
      list = exact
    } else {
      const distinctMap = {}
      list.forEach(s => {
        const key = `${s.theatreId}_${s.hallId}_${s.time || s.showTime}`
        if (!distinctMap[key]) {
          distinctMap[key] = { ...s, date: p.date, showDate: p.date }
        }
      })
      list = Object.values(distinctMap)
    }
  }
  return { success: true, data: list, count: list.length }
}
export const getShowById = (id)     => req(`${BASE}/shows/${id}`)
export const createShow  = (b)      => req(`${BASE}/shows`, 'POST', b)
export const updateShow  = (id, b)  => req(`${BASE}/shows/${id}`, 'PUT', b)
export const deleteShow  = (id)     => req(`${BASE}/shows/${id}`, 'DELETE')
