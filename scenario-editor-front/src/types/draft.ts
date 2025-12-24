/**
 * Draft status in the editing workflow
 * Must match backend: com.cbt.platform.editor.entity.DraftStatus
 */
export enum DraftStatus {
  DRAFT = 'DRAFT',
  VALIDATING = 'VALIDATING',
  READY = 'READY',
  PUBLISHED = 'PUBLISHED'
}

/**
 * Scenario draft (lightweight list view)
 * Matches backend: com.cbt.platform.editor.dto.DraftResponse
 */
export interface DraftResponse {
  id: string;
  name: string;
  slug: string | null;
  category: string | null;
  version: string;
  status: DraftStatus;
  isValid: boolean;
  createdByName: string;
  lastModifiedByName: string;
  createdAt: string;
  updatedAt: string;
  publishedAt: string | null;
}

/**
 * Scenario draft (full detail view with scenarioJson)
 * Matches backend: com.cbt.platform.editor.dto.DraftDetailResponse
 */
export interface ScenarioDraft {
  id: string;
  name: string;
  slug: string | null;
  category: string | null;
  version: string;
  scenarioJson: any; // JSON structure matching Course.scenarioJson
  status: DraftStatus;
  isValid: boolean;
  validationErrors: any | null; // JSON array of validation errors
  createdByUserId: string;
  createdByName: string;
  lastModifiedByUserId: string | null;
  lastModifiedByName: string | null;
  publishedCourseId: string | null;
  publishedAt: string | null;
  createdAt: string;
  updatedAt: string;
}

/**
 * Request to create a new draft
 * Matches backend: com.cbt.platform.editor.dto.CreateDraftRequest
 */
export interface CreateDraftRequest {
  name: string;
  slug?: string;
  category?: string;
  version?: string;
  scenarioJson: any; // Required: full scenario JSON
}

/**
 * Request to update an existing draft
 * Matches backend: com.cbt.platform.editor.dto.UpdateDraftRequest
 */
export interface UpdateDraftRequest {
  name?: string;
  slug?: string;
  category?: string;
  version?: string;
  scenarioJson?: any;
  changeDescription?: string;
}

/**
 * Request to publish a draft to a course
 */
export interface PublishDraftRequest {
  courseSlug: string;
  isActive?: boolean;
}

/**
 * Validation error
 */
export interface ValidationError {
  field: string;
  message: string;
  severity: 'ERROR' | 'WARNING' | 'INFO';
}

/**
 * Validation result
 */
export interface ValidationResult {
  isValid: boolean;
  errors: ValidationError[];
  warnings: ValidationError[];
}

/**
 * Draft version history entry
 * Matches backend: com.cbt.platform.editor.dto.DraftVersionResponse
 */
export interface DraftVersionResponse {
  id: string;
  draftId: string;
  versionNumber: number;
  scenarioJson: any;
  changeDescription: string | null;
  createdByUserId: string;
  createdByName: string;
  createdAt: string;
}
