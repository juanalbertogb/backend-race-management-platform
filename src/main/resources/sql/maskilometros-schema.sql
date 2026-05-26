-- Create races table
CREATE TABLE IF NOT EXISTS races (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT DEFAULT NULL,
    location VARCHAR(255) NOT NULL,
    race_date datetime NOT NULL,
    price DECIMAL(10,2) DEFAULT 0.0 NOT NULL,
    status VARCHAR(20) DEFAULT 'DRAFT' NOT NULL,
    max_participants INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT NULL,
    UNIQUE KEY unique_race_races (name, race_date),
    INDEX idx_location (location),
    INDEX idx_race_date (race_date),
    INDEX idx_status (status)

);

-- Create roles table
CREATE TABLE IF NOT EXISTS roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT NULL
);

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE ,
    password_hash VARCHAR(500) NOT NULL,
    mobile_number VARCHAR(20),
    enabled BOOLEAN NOT NULL,
    role_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT NULL,
    CONSTRAINT fk_users_role FOREIGN KEY(role_id) references roles(id),
    INDEX idx_users_roles (role_id)
);

-- Create registrarion table
CREATE TABLE IF NOT EXISTS registrations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING_PAYMENT',
    bib_number VARCHAR(20) DEFAULT NULL,
    user_id BIGINT NOT NULL,
    race_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT NULL,
    cancellation_date TIMESTAMP DEFAULT NULL,
    payment_deadline TIMESTAMP DEFAULT NULL,
    CONSTRAINT fk_user_registration FOREIGN KEY(user_id) references users(id),
    CONSTRAINT fk_race_registration FOREIGN KEY(race_id) references races(id),
    UNIQUE KEY unique_registration(user_id, race_id),
    INDEX idx_status(status),
    INDEX idx_user_id(user_id),
    INDEX idx_race_id(race_id)
);

-- Create payments table
CREATE TABLE IF NOT EXISTS payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    registration_id BIGINT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    currency VARCHAR(5) NOT NULL,
    idempotency_key VARCHAR(50) NOT NULL,
    external_payment_id VARCHAR(50) DEFAULT NULL,
    provider_message VARCHAR(100) DEFAULT NULL,
    provider VARCHAR(50) DEFAULT NULL,
    refunded_amount DECIMAL(10,2) DEFAULT NULL,
    refund_reason VARCHAR(150) DEFAULT NULL,
    refund_type VARCHAR(20) DEFAULT NULL,
    paid_at TIMESTAMP DEFAULT NULL,
    refunded_at TIMESTAMP DEFAULT NUll,
    failed_at TIMESTAMP DEFAULT NUll,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT NULL,
    CONSTRAINT fk_registration_payments FOREIGN KEY (registration_id) references registrations(id),
    UNIQUE KEY unique_payment(idempotency_key),
    UNIQUE KEY unique_registration(registration_id),
    INDEX idx_payments_status(status)
);