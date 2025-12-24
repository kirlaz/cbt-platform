import { apiClient } from './api';
import {
  ScenarioDraft,
  DraftResponse,
  DraftVersionResponse,
  CreateDraftRequest,
  UpdateDraftRequest,
  PublishDraftRequest,
  ValidationResult,
} from '@/types/draft';
import { PageRequest, PageResponse } from '@/types/api';

class DraftService {
  private readonly baseUrl = '/editor/drafts';

  async getAllDrafts(params?: PageRequest): Promise<PageResponse<DraftResponse>> {
    return apiClient.get<PageResponse<DraftResponse>>(this.baseUrl, { params });
  }

  async getDraftById(id: string): Promise<ScenarioDraft> {
    return apiClient.get<ScenarioDraft>(`${this.baseUrl}/${id}`);
  }

  async createDraft(data: CreateDraftRequest): Promise<ScenarioDraft> {
    return apiClient.post<ScenarioDraft>(this.baseUrl, data);
  }

  async updateDraft(id: string, data: UpdateDraftRequest): Promise<ScenarioDraft> {
    return apiClient.put<ScenarioDraft>(`${this.baseUrl}/${id}`, data);
  }

  async deleteDraft(id: string): Promise<void> {
    return apiClient.delete<void>(`${this.baseUrl}/${id}`);
  }

  async validateDraft(id: string): Promise<ValidationResult> {
    return apiClient.post<ValidationResult>(`${this.baseUrl}/${id}/validate`);
  }

  async publishDraft(id: string, data: PublishDraftRequest): Promise<void> {
    return apiClient.post<void>(`${this.baseUrl}/${id}/publish`, data);
  }

  async getDraftVersions(id: string): Promise<DraftVersionResponse[]> {
    return apiClient.get<DraftVersionResponse[]>(`${this.baseUrl}/${id}/versions`);
  }

  async createVersion(id: string, changeDescription?: string): Promise<DraftVersionResponse> {
    return apiClient.post<DraftVersionResponse>(`${this.baseUrl}/${id}/versions`, {
      changeDescription,
    });
  }

  async restoreVersion(draftId: string, versionId: string): Promise<ScenarioDraft> {
    return apiClient.post<ScenarioDraft>(`${this.baseUrl}/${draftId}/versions/${versionId}/restore`);
  }
}

export const draftService = new DraftService();
