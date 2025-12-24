import { create } from 'zustand';
import { BlockTemplate, CreateTemplateRequest, TemplateCategory } from '@/types/template';
import { BlockType } from '@/types/block';
import { templateService } from '@/services/templateService';

interface TemplateState {
  templates: BlockTemplate[];
  isLoading: boolean;
  error: string | null;

  // Actions
  fetchTemplates: (category?: TemplateCategory, blockType?: BlockType) => Promise<void>;
  createTemplate: (data: CreateTemplateRequest) => Promise<BlockTemplate>;
  deleteTemplate: (id: string) => Promise<void>;
  clearError: () => void;
}

export const useTemplateStore = create<TemplateState>((set) => ({
  templates: [],
  isLoading: false,
  error: null,

  fetchTemplates: async (category?: TemplateCategory, blockType?: BlockType) => {
    set({ isLoading: true, error: null });
    try {
      const response = await templateService.getAllTemplates({
        category,
        blockType,
        size: 100,
      });
      set({ templates: response.content, isLoading: false });
    } catch (error: any) {
      set({ error: error.message || 'Failed to fetch templates', isLoading: false });
    }
  },

  createTemplate: async (data: CreateTemplateRequest) => {
    set({ isLoading: true, error: null });
    try {
      const template = await templateService.createTemplate(data);
      set((state) => ({
        templates: [template, ...state.templates],
        isLoading: false,
      }));
      return template;
    } catch (error: any) {
      set({ error: error.message || 'Failed to create template', isLoading: false });
      throw error;
    }
  },

  deleteTemplate: async (id: string) => {
    set({ isLoading: true, error: null });
    try {
      await templateService.deleteTemplate(id);
      set((state) => ({
        templates: state.templates.filter((t) => t.id !== id),
        isLoading: false,
      }));
    } catch (error: any) {
      set({ error: error.message || 'Failed to delete template', isLoading: false });
      throw error;
    }
  },

  clearError: () => set({ error: null }),
}));
