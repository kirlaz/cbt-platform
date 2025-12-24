import { Block, BlockType } from './block';

export interface BlockTemplate {
  id: string;
  name: string;
  description?: string;
  category: TemplateCategory;
  blockType: BlockType;
  templateContent: Block;
  tags?: string[];
  isPublic: boolean;
  usageCount: number;
  createdBy: string;
  createdAt: string;
  updatedAt: string;
}

export enum TemplateCategory {
  ASSESSMENT = 'ASSESSMENT',
  EDUCATION = 'EDUCATION',
  EXERCISE = 'EXERCISE',
  REFLECTION = 'REFLECTION',
  CONVERSATION = 'CONVERSATION',
  PROGRESS = 'PROGRESS',
  OTHER = 'OTHER'
}

export interface CreateTemplateRequest {
  name: string;
  description?: string;
  category: TemplateCategory;
  blockType: BlockType;
  templateContent: Block;
  tags?: string[];
  isPublic?: boolean;
}

export interface UpdateTemplateRequest {
  name?: string;
  description?: string;
  category?: TemplateCategory;
  templateContent?: Block;
  tags?: string[];
  isPublic?: boolean;
}
