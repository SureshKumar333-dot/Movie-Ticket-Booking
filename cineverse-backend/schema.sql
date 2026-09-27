-- CineVerse MySQL Database Schema & Initial Data
CREATE DATABASE IF NOT EXISTS cineverse CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE cineverse;

-- 1. Users table (Admins, Managers, Customers)
CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(10) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    city VARCHAR(10),
    address TEXT,
    dob DATE,
    gender VARCHAR(20),
    avatar VARCHAR(10),
    joined_on DATE,
    role VARCHAR(30) NOT NULL
);

-- 2. Movies table
CREATE TABLE IF NOT EXISTS movies (
    id VARCHAR(10) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    language VARCHAR(50),
    genre VARCHAR(100),
    duration VARCHAR(50),
    rating VARCHAR(10),
    badge VARCHAR(20),
    description TEXT,
    cast_members TEXT,
    director VARCHAR(255),
    poster VARCHAR(500),
    price_standard INT,
    price_premium INT
);

-- 3. Theatres table
CREATE TABLE IF NOT EXISTS theatres (
    id VARCHAR(10) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    city VARCHAR(10),
    location VARCHAR(255),
    phone VARCHAR(50)
);

-- 4. Halls table
CREATE TABLE IF NOT EXISTS halls (
    id VARCHAR(10) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    theatre_id VARCHAR(10) NOT NULL,
    total_seats INT,
    features JSON,
    hall_rows JSON,
    seats_per_row INT
);

-- 5. Shows table
CREATE TABLE IF NOT EXISTS shows (
    id VARCHAR(10) PRIMARY KEY,
    movie_id VARCHAR(10) NOT NULL,
    theatre_id VARCHAR(10) NOT NULL,
    hall_id VARCHAR(10) NOT NULL,
    city_id VARCHAR(10),
    show_date DATE,
    show_time VARCHAR(20),
    available_seats INT,
    language VARCHAR(50),
    format VARCHAR(50)
);

-- 6. Bookings table (CRUD Operations)
CREATE TABLE IF NOT EXISTS bookings (
    id VARCHAR(10) PRIMARY KEY,
    customer_id VARCHAR(10) NOT NULL,
    show_id VARCHAR(10) NOT NULL,
    movie_id VARCHAR(10),
    theatre_id VARCHAR(10),
    hall_id VARCHAR(10),
    movie_title VARCHAR(255),
    theatre_name VARCHAR(255),
    hall_name VARCHAR(255),
    city_id VARCHAR(5),
    show_date DATE,
    show_time VARCHAR(20),
    seats JSON,
    seat_type VARCHAR(50),
    ticket_amount INT,
    convenience_fee INT,
    total_amount INT,
    payment_method VARCHAR(50),
    payment_status VARCHAR(50),
    status VARCHAR(50),
    booked_on DATETIME,
    updated_at DATETIME
);

-- 7. Cancellations table (Refunds & Cancellation CRUD)
CREATE TABLE IF NOT EXISTS cancellations (
    id VARCHAR(50) PRIMARY KEY,
    booking_id VARCHAR(10) NOT NULL,
    customer_id VARCHAR(10) NOT NULL,
    movie_title VARCHAR(255),
    theatre_name VARCHAR(255),
    show_date DATE,
    show_time VARCHAR(20),
    seats JSON,
    total_amount INT,
    refund_amount INT,
    refund_status VARCHAR(50),
    refund_method VARCHAR(50),
    reason TEXT,
    cancelled_at DATETIME
);

-- 8. Booking Changes table (Audit Trail & Change CRUD)
CREATE TABLE IF NOT EXISTS booking_changes (
    id VARCHAR(50) PRIMARY KEY,
    booking_id VARCHAR(10) NOT NULL,
    customer_id VARCHAR(10) NOT NULL,
    change_type VARCHAR(50) NOT NULL,
    description TEXT,
    before_state JSON,
    after_state JSON,
    fee_paid INT,
    changed_at DATETIME
);
