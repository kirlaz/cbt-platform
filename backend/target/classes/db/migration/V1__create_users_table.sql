-- Create users table
-- This table stores user accounts for the CBT platform

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    timezone VARCHAR(50) NOT NULL DEFAULT 'UTC',
    preferred_language VARCHAR(10) NOT NULL DEFAULT 'ru',
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login_at TIMESTAMP,
    deleted_at TIMESTAMP,

    CONSTRAINT chk_role CHECK (role IN ('USER', 'ADMIN')),
    CONSTRAINT chk_preferred_language CHECK (preferred_language IN ('ru', 'en'))
);

-- Create indexes for performance
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_deleted_at ON users(deleted_at);
CREATE INDEX idx_users_created_at ON users(created_at DESC);

-- Add comments
COMMENT ON TABLE users IS 'Platform users with authentication and profile data';
COMMENT ON COLUMN users.email IS 'User email address (unique, used for login)';
COMMENT ON COLUMN users.password_hash IS 'BCrypt hashed password';
COMMENT ON COLUMN users.role IS 'User role: USER or ADMIN';
COMMENT ON COLUMN users.deleted_at IS 'Soft delete timestamp (NULL = active user)';
