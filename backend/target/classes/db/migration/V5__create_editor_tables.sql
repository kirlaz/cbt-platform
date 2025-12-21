-- Create editor module tables
-- This migration adds tables for the scenario editor: drafts, versions, and block templates

-- ============================================
-- 1. Block Templates Table
-- ============================================
CREATE TABLE block_templates (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(200) NOT NULL,
    description VARCHAR(500),
    block_type VARCHAR(50) NOT NULL,
    category VARCHAR(50),
    block_json JSONB NOT NULL,
    is_system BOOLEAN NOT NULL DEFAULT false,
    is_active BOOLEAN NOT NULL DEFAULT true,
    usage_count INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,

    -- Constraints
    CONSTRAINT chk_block_templates_usage_count CHECK (usage_count >= 0)
);

-- Indexes for block_templates
CREATE INDEX idx_block_templates_block_type ON block_templates(block_type);
CREATE INDEX idx_block_templates_category ON block_templates(category);
CREATE INDEX idx_block_templates_active ON block_templates(is_active) WHERE deleted_at IS NULL;
CREATE INDEX idx_block_templates_system ON block_templates(is_system) WHERE deleted_at IS NULL;
CREATE INDEX idx_block_templates_usage_count ON block_templates(usage_count DESC);
CREATE INDEX idx_block_templates_deleted_at ON block_templates(deleted_at);
CREATE INDEX idx_block_templates_block_json ON block_templates USING GIN(block_json);

-- Comments for block_templates
COMMENT ON TABLE block_templates IS 'Reusable block templates for quick scenario building';
COMMENT ON COLUMN block_templates.block_json IS 'Block JSON template (id, type, content, config, etc.)';
COMMENT ON COLUMN block_templates.is_system IS 'System templates cannot be modified or deleted';
COMMENT ON COLUMN block_templates.usage_count IS 'Number of times this template was used';


-- ============================================
-- 2. Scenario Drafts Table
-- ============================================
CREATE TABLE scenario_drafts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(200) NOT NULL,
    slug VARCHAR(100),
    category VARCHAR(50),
    version VARCHAR(20) NOT NULL DEFAULT '1.0.0',
    scenario_json JSONB NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    is_valid BOOLEAN NOT NULL DEFAULT false,
    validation_errors JSONB,
    created_by_user_id UUID,
    last_modified_by_user_id UUID,
    published_course_id UUID,
    published_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,

    -- Foreign keys
    CONSTRAINT fk_scenario_drafts_created_by FOREIGN KEY (created_by_user_id) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_scenario_drafts_last_modified_by FOREIGN KEY (last_modified_by_user_id) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_scenario_drafts_published_course FOREIGN KEY (published_course_id) REFERENCES courses(id) ON DELETE SET NULL,

    -- Constraints
    CONSTRAINT chk_scenario_drafts_status CHECK (status IN ('DRAFT', 'VALIDATING', 'READY', 'PUBLISHED'))
);

-- Indexes for scenario_drafts
CREATE INDEX idx_scenario_drafts_slug ON scenario_drafts(slug) WHERE deleted_at IS NULL;
CREATE INDEX idx_scenario_drafts_category ON scenario_drafts(category) WHERE deleted_at IS NULL;
CREATE INDEX idx_scenario_drafts_status ON scenario_drafts(status) WHERE deleted_at IS NULL;
CREATE INDEX idx_scenario_drafts_is_valid ON scenario_drafts(is_valid);
CREATE INDEX idx_scenario_drafts_created_by ON scenario_drafts(created_by_user_id);
CREATE INDEX idx_scenario_drafts_last_modified_by ON scenario_drafts(last_modified_by_user_id);
CREATE INDEX idx_scenario_drafts_published_course ON scenario_drafts(published_course_id);
CREATE INDEX idx_scenario_drafts_updated_at ON scenario_drafts(updated_at DESC);
CREATE INDEX idx_scenario_drafts_deleted_at ON scenario_drafts(deleted_at);
CREATE INDEX idx_scenario_drafts_scenario_json ON scenario_drafts USING GIN(scenario_json);

-- Comments for scenario_drafts
COMMENT ON TABLE scenario_drafts IS 'Scenario drafts for the editor (before publication as courses)';
COMMENT ON COLUMN scenario_drafts.scenario_json IS 'Full scenario JSON (meta, global_config, user_profile_schema, sessions)';
COMMENT ON COLUMN scenario_drafts.status IS 'Draft workflow status: DRAFT, VALIDATING, READY, PUBLISHED';
COMMENT ON COLUMN scenario_drafts.is_valid IS 'Whether scenario passed validation';
COMMENT ON COLUMN scenario_drafts.validation_errors IS 'Validation errors JSONB if any';
COMMENT ON COLUMN scenario_drafts.deleted_at IS 'Soft delete timestamp (NULL = active draft)';


-- ============================================
-- 3. Scenario Draft Versions Table
-- ============================================
CREATE TABLE scenario_draft_versions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    draft_id UUID NOT NULL,
    version_number INTEGER NOT NULL,
    scenario_json JSONB NOT NULL,
    change_description VARCHAR(500),
    created_by_user_id UUID,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Foreign keys
    CONSTRAINT fk_scenario_draft_versions_draft FOREIGN KEY (draft_id) REFERENCES scenario_drafts(id) ON DELETE CASCADE,
    CONSTRAINT fk_scenario_draft_versions_created_by FOREIGN KEY (created_by_user_id) REFERENCES users(id) ON DELETE SET NULL,

    -- Constraints
    CONSTRAINT uq_scenario_draft_versions_draft_version UNIQUE(draft_id, version_number),
    CONSTRAINT chk_scenario_draft_versions_version_number CHECK (version_number > 0)
);

-- Indexes for scenario_draft_versions
CREATE INDEX idx_scenario_draft_versions_draft_id ON scenario_draft_versions(draft_id);
CREATE INDEX idx_scenario_draft_versions_draft_version ON scenario_draft_versions(draft_id, version_number DESC);
CREATE INDEX idx_scenario_draft_versions_created_by ON scenario_draft_versions(created_by_user_id);
CREATE INDEX idx_scenario_draft_versions_created_at ON scenario_draft_versions(created_at DESC);
CREATE INDEX idx_scenario_draft_versions_scenario_json ON scenario_draft_versions USING GIN(scenario_json);

-- Comments for scenario_draft_versions
COMMENT ON TABLE scenario_draft_versions IS 'Version history for scenario drafts (audit and rollback)';
COMMENT ON COLUMN scenario_draft_versions.version_number IS 'Sequential version number (1, 2, 3, ...)';
COMMENT ON COLUMN scenario_draft_versions.scenario_json IS 'Snapshot of scenario JSON at this version';
COMMENT ON COLUMN scenario_draft_versions.change_description IS 'Optional description of changes made';
