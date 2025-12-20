-- Create courses table
-- This table stores CBT courses with their scenario JSON

CREATE TABLE courses (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    slug VARCHAR(100) UNIQUE NOT NULL,
    name VARCHAR(200) NOT NULL,
    description VARCHAR(1000),
    scenario_json JSONB NOT NULL,
    version VARCHAR(20) NOT NULL,
    free_sessions INTEGER NOT NULL DEFAULT 2,
    price DECIMAL(10, 2),
    image_url VARCHAR(500),
    estimated_duration_minutes INTEGER,
    category VARCHAR(50),
    is_active BOOLEAN NOT NULL DEFAULT true,
    is_published BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,

    CONSTRAINT chk_free_sessions CHECK (free_sessions >= 0),
    CONSTRAINT chk_price CHECK (price IS NULL OR price > 0),
    CONSTRAINT chk_duration CHECK (estimated_duration_minutes IS NULL OR estimated_duration_minutes > 0)
);

-- Create indexes for performance
CREATE INDEX idx_courses_slug ON courses(slug);
CREATE INDEX idx_courses_category ON courses(category);
CREATE INDEX idx_courses_active_published ON courses(is_active, is_published) WHERE deleted_at IS NULL;
CREATE INDEX idx_courses_deleted_at ON courses(deleted_at);
CREATE INDEX idx_courses_created_at ON courses(created_at DESC);

-- Create GIN index for JSONB scenario queries (for future use)
CREATE INDEX idx_courses_scenario_json ON courses USING GIN(scenario_json);

-- Add comments
COMMENT ON TABLE courses IS 'CBT courses with JSON scenarios';
COMMENT ON COLUMN courses.slug IS 'Unique URL-friendly identifier (e.g., "anxiety-management")';
COMMENT ON COLUMN courses.scenario_json IS 'Full course scenario in JSONB format (meta, sessions, config)';
COMMENT ON COLUMN courses.version IS 'Scenario version (e.g., "1.0.1")';
COMMENT ON COLUMN courses.free_sessions IS 'Number of free sessions before paywall';
COMMENT ON COLUMN courses.is_published IS 'Whether course is visible to users';
COMMENT ON COLUMN courses.deleted_at IS 'Soft delete timestamp (NULL = active course)';
