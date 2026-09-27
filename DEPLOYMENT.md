# CineVerse Production Deployment Guide (Free / Low-Cost Tier)

This guide walks you through deploying the full-stack **CineVerse** application to the cloud:
1. **Database:** Free Managed MySQL (Aiven / TiDB Cloud)
2. **Backend:** Spring Boot on [Render](https://render.com) (or [Railway](https://railway.app))
3. **Frontend:** Next.js on [Vercel](https://vercel.com)

---

## 🏗️ Architecture Overview

```
[ User Browser ]
       │
       ▼
[ Vercel (Next.js 14) ] ─── (/api/* rewrite) ───► [ Render (Spring Boot 3) ]
                                                            │
                                                            ▼
                                                [ Managed MySQL (Aiven/TiDB) ]
```

---

## Step 1: Set Up Free Cloud MySQL Database

You need a live MySQL connection string (`jdbc:mysql://...`).

### Option A: TiDB Cloud Serverless (Free Forever, MySQL 8 Compatible)
1. Go to [tidbcloud.com](https://tidbcloud.com) and create a free account.
2. Click **Create Cluster** ➔ Select **Serverless** (Free).
3. Once created, click **Connect**:
   * Note the **Host**, **Port**, **User**, **Password**, and **Database Name** (`test` or create `cineverse`).
4. In the Web SQL Console, execute the contents of [`cineverse-backend/schema.sql`](./cineverse-backend/schema.sql) to create all tables and initial mock movies/theatres.

### Option B: Aiven for MySQL (Free Trial / Tier)
1. Go to [aiven.io](https://aiven.io) and create a free MySQL service.
2. Copy the Service URI, Host, Port, Username, and Password.
3. Import [`cineverse-backend/schema.sql`](./cineverse-backend/schema.sql).

---

## Step 2: Push Project to GitHub

1. Create a new repository on [GitHub](https://github.com/new) (e.g. `cineverse-app`).
2. In your local project terminal:
   ```bash
   git add .
   git commit -m "Configure full-stack project for cloud deployment"
   git branch -M main
   git remote add origin https://github.com/<YOUR_USERNAME>/cineverse-app.git
   git push -u origin main
   ```

---

## Step 3: Deploy Spring Boot Backend to Render

1. Sign up / Log in to [Render](https://dashboard.render.com).
2. Click **New +** ➔ **Web Service**.
3. Connect your GitHub repository (`cineverse-app`).
4. Configure the Web Service:
   * **Name:** `cineverse-backend`
   * **Root Directory:** `cineverse-backend`
   * **Runtime:** `Docker` (Render will automatically detect `cineverse-backend/Dockerfile`)
   * **Instance Type:** `Free`
5. Under **Environment Variables**, add:
   * `SPRING_DATASOURCE_URL`: `jdbc:mysql://<YOUR_DB_HOST>:<YOUR_DB_PORT>/<YOUR_DB_NAME>?createDatabaseIfNotExist=true&useSSL=true&allowPublicKeyRetrieval=true&serverTimezone=UTC`
   * `SPRING_DATASOURCE_USERNAME`: `<YOUR_DB_USER>`
   * `SPRING_DATASOURCE_PASSWORD`: `<YOUR_DB_PASSWORD>`
   * `CORS_ALLOWED_ORIGINS`: `https://<YOUR_VERCEL_APP_NAME>.vercel.app,http://localhost:3000`
   * `APP_JWT_SECRET`: `cineverse-super-secret-key-change-in-production-min-256-bits-long!!`
6. Click **Create Web Service**.
7. Once deployed, copy your backend URL (e.g., `https://cineverse-backend-xyz.onrender.com`).

---

## Step 4: Deploy Next.js Frontend to Vercel

1. Log in to [Vercel](https://vercel.com).
2. Click **Add New...** ➔ **Project**.
3. Import your GitHub repository (`cineverse-app`).
4. Configure Project:
   * **Framework Preset:** `Next.js`
   * **Root Directory:** `./` (leave default)
5. Expand **Environment Variables** and add:
   * `BACKEND_URL`: `https://cineverse-backend-xyz.onrender.com` (Your Render URL from Step 3, without trailing slash)
   * `NEXT_PUBLIC_API_URL`: `/api`
6. Click **Deploy**.

---

## Step 5: Final Check & CORS Alignment

1. Once Vercel finishes deploying, copy your production domain (e.g. `https://cineverse-app.vercel.app`).
2. Go back to Render ➔ `cineverse-backend` ➔ **Environment**.
3. Ensure `CORS_ALLOWED_ORIGINS` contains your exact Vercel URL:
   ```
   https://cineverse-app.vercel.app,http://localhost:3000
   ```
4. Render will automatically re-deploy with the updated CORS policy.

🎉 **Your CineVerse application is now live worldwide!**
