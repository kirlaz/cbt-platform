import { create } from 'zustand';
import { ScenarioDraft, DraftResponse, CreateDraftRequest, UpdateDraftRequest, ValidationResult } from '@/types/draft';
import { draftService } from '@/services/draftService';

interface DraftState {
  drafts: DraftResponse[];
  currentDraft: ScenarioDraft | null;
  validationResult: ValidationResult | null;
  isLoading: boolean;
  error: string | null;

  // Actions
  fetchDrafts: () => Promise<void>;
  fetchDraftById: (id: string) => Promise<void>;
  createDraft: (data: CreateDraftRequest) => Promise<ScenarioDraft>;
  updateDraft: (id: string, data: UpdateDraftRequest) => Promise<void>;
  deleteDraft: (id: string) => Promise<void>;
  validateDraft: (id: string) => Promise<ValidationResult>;
  publishDraft: (id: string, courseSlug: string) => Promise<void>;
  setCurrentDraft: (draft: ScenarioDraft | null) => void;
  clearError: () => void;
}

export const useDraftStore = create<DraftState>((set, get) => ({
  drafts: [],
  currentDraft: null,
  validationResult: null,
  isLoading: false,
  error: null,

  fetchDrafts: async () => {
    set({ isLoading: true, error: null });
    try {
      const response = await draftService.getAllDrafts({ size: 100 });
      set({ drafts: response.content, isLoading: false });
    } catch (error: any) {
      set({ error: error.message || 'Failed to fetch drafts', isLoading: false });
    }
  },

  fetchDraftById: async (id: string) => {
    set({ isLoading: true, error: null });
    try {
      const draft = await draftService.getDraftById(id);
      set({ currentDraft: draft, isLoading: false });
    } catch (error: any) {
      set({ error: error.message || 'Failed to fetch draft', isLoading: false });
    }
  },

  createDraft: async (data: CreateDraftRequest) => {
    set({ isLoading: true, error: null });
    try {
      const draft = await draftService.createDraft(data);
      set({ currentDraft: draft, isLoading: false });
      // Refresh the drafts list to get the lightweight DraftResponse
      await get().fetchDrafts();
      return draft;
    } catch (error: any) {
      set({ error: error.message || 'Failed to create draft', isLoading: false });
      throw error;
    }
  },

  updateDraft: async (id: string, data: UpdateDraftRequest) => {
    set({ isLoading: true, error: null });
    try {
      const updatedDraft = await draftService.updateDraft(id, data);
      set((state) => ({
        currentDraft: state.currentDraft?.id === id ? updatedDraft : state.currentDraft,
        isLoading: false,
      }));
      // Refresh the drafts list to get updated lightweight DraftResponse
      await get().fetchDrafts();
    } catch (error: any) {
      set({ error: error.message || 'Failed to update draft', isLoading: false });
      throw error;
    }
  },

  deleteDraft: async (id: string) => {
    set({ isLoading: true, error: null });
    try {
      await draftService.deleteDraft(id);
      set((state) => ({
        drafts: state.drafts.filter((d) => d.id !== id),
        currentDraft: state.currentDraft?.id === id ? null : state.currentDraft,
        isLoading: false,
      }));
    } catch (error: any) {
      set({ error: error.message || 'Failed to delete draft', isLoading: false });
      throw error;
    }
  },

  validateDraft: async (id: string) => {
    set({ isLoading: true, error: null });
    try {
      const result = await draftService.validateDraft(id);
      set({ validationResult: result, isLoading: false });
      return result;
    } catch (error: any) {
      set({ error: error.message || 'Failed to validate draft', isLoading: false });
      throw error;
    }
  },

  publishDraft: async (id: string, courseSlug: string) => {
    set({ isLoading: true, error: null });
    try {
      await draftService.publishDraft(id, { courseSlug });
      // Refresh draft to get updated status
      await get().fetchDraftById(id);
    } catch (error: any) {
      set({ error: error.message || 'Failed to publish draft', isLoading: false });
      throw error;
    }
  },

  setCurrentDraft: (draft: ScenarioDraft | null) => set({ currentDraft: draft }),

  clearError: () => set({ error: null }),
}));
