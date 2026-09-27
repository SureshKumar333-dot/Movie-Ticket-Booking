// lib/jsonServer.js — Base fetch helper for json-server (port 3001)
// All Next.js API routes proxy through here so the frontend never calls 3001 directly.

import fs from 'fs/promises'
import path from 'path'

const JS = process.env.JSON_SERVER_URL || 'http://localhost:3001'
const DB_PATH = path.join(process.cwd(), 'data', 'db.json')

async function readLocalDb(pathname, searchParams) {
  const raw = await fs.readFile(DB_PATH, 'utf8')
  const db = JSON.parse(raw)
  const segments = pathname.replace(/^\/+/, '').split('/').filter(Boolean)
  const key = segments[0]
  if (!key || !Array.isArray(db[key])) return null

  let rows = [...db[key]]

  /* /movies/M01 → single record (json-server shape), not the whole collection */
  if (segments[1]) {
    const item = rows.find((r) => String(r?.id) === String(segments[1]))
    return item ?? null
  }

  for (const [k, v] of searchParams.entries()) {
    const field = key === 'shows' && k === 'date' ? 'showDate' : k
    rows = rows.filter((row) => String(row?.[field]) === String(v))
  }
  return rows
}

export async function jsReq(path, method = 'GET', body = null) {
  const opts = {
    method,
    headers: { 'Content-Type': 'application/json' },
  }
  if (body) opts.body = JSON.stringify(body)

  try {
    const res  = await fetch(`${JS}${path}`, opts)
    const text = await res.text()

    let data
    try { data = JSON.parse(text) } catch { data = text }

    if (method === 'GET') {
      const u = new URL(path, 'http://localhost')
      const fallback = await readLocalDb(u.pathname, u.searchParams)
      const parts = u.pathname.replace(/^\/+/, '').split('/').filter(Boolean)
      const expectsArray = parts.length === 1
      const parsedOk = expectsArray
        ? Array.isArray(data)
        : data !== null && typeof data === 'object' && !Array.isArray(data)
      if ((!res.ok || !parsedOk) && fallback !== null) {
        return { ok: true, status: 200, data: fallback }
      }
    }

    return { ok: res.ok, status: res.status, data }
  } catch (err) {
    // If json-server is offline in local dev, serve read-only data from data/db.json.
    if (method === 'GET') {
      const u = new URL(path, 'http://localhost')
      const data = await readLocalDb(u.pathname, u.searchParams)
      if (data !== null) return { ok: true, status: 200, data }
    }
    return { ok: false, status: 503, data: { error: err?.message || 'json-server unavailable' } }
  }
}

/* ── tiny query-string builder ── */
export function qs(params = {}) {
  const p = new URLSearchParams()
  Object.entries(params).forEach(([k, v]) => {
    if (v !== undefined && v !== null && v !== '') p.append(k, v)
  })
  const s = p.toString()
  return s ? `?${s}` : ''
}
