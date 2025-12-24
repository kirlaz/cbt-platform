import { apiClient } from './api';
import {
  BlockTemplate,
  CreateTemplateRequest,
  UpdateTemplateRequest,
  TemplateCategory,
} from '@/types/template';
import { BlockType } from '@/types/block';
import { PageRequest, PageResponse } from '@/types/api';

interface TemplateFilters extends PageRequest {
  category?: TemplateCategory;
  blockType?: BlockType;
  tags?: string[];
  search?: string;
}

class TemplateService {
  private readonly baseUrl = '/editor/templates/blocks';

  async getAllTemplates(filters?: TemplateFilters): Promise<PageResponse<BlockTemplate>> {
    return apiClient.get<PageResponse<BlockTemplate>>(this.baseUrl, { params: filters });
  }

  async getTemplateById(id: string): Promise<BlockTemplate> {
    return apiClient.get<BlockTemplate>(`${this.baseUrl}/${id}`);
  }

  async createTemplate(data: CreateTemplateRequest): Promise<BlockTemplate> {
    return apiClient.post<BlockTemplate>(this.baseUrl, data);
  }

  async updateTemplate(id: string, data: UpdateTemplateRequest): Promise<BlockTemplate> {
    return apiClient.put<BlockTemplate>(`${this.baseUrl}/${id}`, data);
  }

  async deleteTemplate(id: string): Promise<void> {
    return apiClient.delete<void>(`${this.baseUrl}/${id}`);
  }

  async getTemplatesByCategory(category: TemplateCategory): Promise<BlockTemplate[]> {
    const response = await this.getAllTemplates({ category, size: 100 });
    return response.content;
  }

  async getTemplatesByBlockType(blockType: BlockType): Promise<BlockTemplate[]> {
    const response = await this.getAllTemplates({ blockType, size: 100 });
    return response.content;
  }

  async searchTemplates(search: string): Promise<BlockTemplate[]> {
    const response = await this.getAllTemplates({ search, size: 50 });
    return response.content;
  }
}

export const templateService = new TemplateService();
