-- Create user_progress table
-- This table tracks user's progress through CBT courses with personalized userData

CREATE TABLE user_progress (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    course_id UUID NOT NULL,
    current_session_id VARCHAR(100),
    current_block_index INTEGER DEFAULT 0,
    user_data JSONB NOT NULL DEFAULT '{}'::jsonb,
    completed_sessions JSONB DEFAULT '[]'::jsonb,
    completed_blocks JSONB DEFAULT '[]'::jsonb,
    completion_percentage INTEGER NOT NULL DEFAULT 0,
    is_completed BOOLEAN NOT NULL DEFAULT false,
    started_at TIMESTAMP,
    completed_at TIMESTAMP,
    last_activity_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- Foreign keys
    CONSTRAINT fk_user_progress_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_progress_course FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,

    -- Constraints
    CONSTRAINT uq_user_progress_user_course UNIQUE(user_id, course_id),
    CONSTRAINT chk_completion_percentage CHECK (completion_percentage >= 0 AND completion_percentage <= 100),
    CONSTRAINT chk_current_block_index CHECK (current_block_index >= 0)
);

-- Create indexes for performance
CREATE INDEX idx_user_progress_user_id ON user_progress(user_id);
CREATE INDEX idx_user_progress_course_id ON user_progress(course_id);
CREATE INDEX idx_user_progress_user_course ON user_progress(user_id, course_id);
CREATE INDEX idx_user_progress_completed ON user_progress(is_completed);
CREATE INDEX idx_user_progress_last_activity ON user_progress(last_activity_at DESC);

-- Create GIN index for JSONB user_data queries (for future use)
CREATE INDEX idx_user_progress_user_data ON user_progress USING GIN(user_data);
CREATE INDEX idx_user_progress_completed_sessions ON user_progress USING GIN(completed_sessions);

-- Add comments
COMMENT ON TABLE user_progress IS 'Tracks user progress through CBT courses with personalized data';
COMMENT ON COLUMN user_progress.user_data IS 'JSONB field storing all user inputs and personalization data (name, triggers, assessments, journal entries)';
COMMENT ON COLUMN user_progress.current_session_id IS 'Current session ID (e.g., "onboarding", "session_1")';
COMMENT ON COLUMN user_progress.current_block_index IS 'Current block index within the session (0-based)';
COMMENT ON COLUMN user_progress.completed_sessions IS 'Array of completed session IDs';
COMMENT ON COLUMN user_progress.completed_blocks IS 'Array of completed block keys (format: "session_id:block_id")';
COMMENT ON COLUMN user_progress.completion_percentage IS 'Overall course completion (0-100)';
