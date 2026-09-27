// pages/_app.jsx — Pages Router Root (replaces app/layout.jsx)
import Head from 'next/head'
import { AppProvider } from '@/context/AppContext'
import '@/styles/globals.css'

export default function MyApp({ Component, pageProps }) {
  return (
    <AppProvider>
      <Head>
        <title>CineVerse — Online Movie Ticket Booking</title>
        <meta name="description" content="Book movie tickets across Tamil Nadu" />
        <link
          href="https://fonts.googleapis.com/css2?family=Cormorant+Garamond:ital,wght@0,300;0,400;0,600;1,300;1,400&family=Outfit:wght@300;400;500;600;700&display=swap"
          rel="stylesheet"
        />
      </Head>
      <Component {...pageProps} />
    </AppProvider>
  )
}
