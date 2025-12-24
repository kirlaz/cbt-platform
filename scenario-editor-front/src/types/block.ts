export enum BlockType {
  STATIC = 'STATIC',
  INPUT = 'INPUT',
  SLIDER = 'SLIDER',
  SINGLE_SELECT = 'SINGLE_SELECT',
  MULTI_SELECT = 'MULTI_SELECT',
  LLM_CONVERSATION = 'LLM_CONVERSATION',
  LLM_RESPONSE = 'LLM_RESPONSE',
  EXERCISE = 'EXERCISE',
  VISUALIZATION = 'VISUALIZATION',
  CALCULATION = 'CALCULATION',
  SESSION_COMPLETE = 'SESSION_COMPLETE',
  PAYWALL = 'PAYWALL'
}

export interface Block {
  id: string;
  type: BlockType;
  title?: string;
  content?: string;
  prompt?: string;
  systemPrompt?: string;
  userDataKeys?: string[];
  options?: SelectOption[];
  min?: number;
  max?: number;
  step?: number;
  defaultValue?: any;
  validation?: ValidationRule;
  storeAs?: string;
  next?: string;
  conditionalNext?: ConditionalNext[];
  exerciseType?: string;
  visualizationType?: string;
  calculationFormula?: string;
  metadata?: Record<string, any>;
}

export interface SelectOption {
  id: string;
  label: string;
  value: string;
}

export interface ValidationRule {
  required?: boolean;
  minLength?: number;
  maxLength?: number;
  pattern?: string;
  errorMessage?: string;
}

export interface ConditionalNext {
  condition: string;
  blockId: string;
}

export interface Session {
  id: string;
  title: string;
  description?: string;
  order: number;
  isFree: boolean;
  blocks: Block[];
}
