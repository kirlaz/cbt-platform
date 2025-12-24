-- Seed admin user for testing
-- Email: admin@cbt.com
-- Password: Admin123! (BCrypt hash below)

INSERT INTO users (
    email,
    password_hash,
    name,
    timezone,
    preferred_language,
    role,
    is_active
) VALUES (
    'admin@cbt.com',
    '$2a$10$umaC.vExUhp5G/MmXw2w/uIKIwajqH1XFbQjM/iiCA8AV/3CF5gmu', -- Admin123!
    'System Administrator',
    'UTC',
    'ru',
    'ADMIN',
    true
)
ON CONFLICT (email) DO NOTHING;

-- Add comment
COMMENT ON TABLE users IS 'Default admin user: admin@cbt.com / Admin123!';
