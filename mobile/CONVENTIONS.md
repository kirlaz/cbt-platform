# Mobile Conventions

> React Native 0.73 + Expo 50 + TypeScript

## Project Structure

```
src/
├── screens/           # Screen components
├── components/
│   ├── blocks/        # Block renderers
│   ├── exercises/     # Exercise components
│   ├── visualizations/
│   └── common/        # Reusable UI
├── navigation/        # React Navigation
├── store/             # Zustand stores
├── services/          # API calls
├── hooks/             # Custom hooks
├── utils/             # Helpers
├── constants/         # Theme, config
└── types/             # TypeScript types
```

---

## Component

```tsx
import React, { useCallback } from 'react';
import { View, Text, Pressable, StyleSheet } from 'react-native';
import Animated, { FadeIn } from 'react-native-reanimated';
import * as Haptics from 'expo-haptics';
import { colors, spacing } from '@/constants/theme';

interface CourseCardProps {
  course: Course;
  onPress: (course: Course) => void;
  isActive?: boolean;
}

export const CourseCard: React.FC<CourseCardProps> = ({
  course,
  onPress,
  isActive = false,
}) => {
  const handlePress = useCallback(() => {
    Haptics.impactAsync(Haptics.ImpactFeedbackStyle.Light);
    onPress(course);
  }, [course, onPress]);

  return (
    <Animated.View entering={FadeIn.duration(300)}>
      <Pressable
        style={({ pressed }) => [
          styles.container,
          isActive && styles.active,
          pressed && styles.pressed,
        ]}
        onPress={handlePress}
      >
        <Text style={styles.title}>{course.name}</Text>
        <Text style={styles.subtitle}>{course.description}</Text>
      </Pressable>
    </Animated.View>
  );
};

const styles = StyleSheet.create({
  container: {
    backgroundColor: colors.card,
    borderRadius: spacing.md,
    padding: spacing.lg,
    marginBottom: spacing.md,
  },
  active: {
    borderColor: colors.primary,
    borderWidth: 2,
  },
  pressed: {
    opacity: 0.8,
  },
  title: {
    fontSize: 18,
    fontWeight: '600',
    color: colors.text,
    marginBottom: spacing.xs,
  },
  subtitle: {
    fontSize: 14,
    color: colors.textSecondary,
  },
});
```

### Rules
- Functional components + TypeScript
- `interface` для props (не `type`)
- `React.FC<Props>` для типизации
- `StyleSheet.create()` — никаких inline styles
- `useCallback` для handlers
- `Reanimated` для анимаций
- `Haptics` для тактильной обратной связи
- Named export (не default)

---

## Screen

```tsx
import React, { useEffect } from 'react';
import { View, ScrollView, StyleSheet } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { useNavigation } from '@react-navigation/native';
import { useCourseStore } from '@/store/useCourseStore';
import { CourseCard } from '@/components/common/CourseCard';
import { Loading } from '@/components/common/Loading';
import { ErrorView } from '@/components/common/ErrorView';
import { colors, spacing } from '@/constants/theme';

export const CourseListScreen: React.FC = () => {
  const navigation = useNavigation();
  const { courses, isLoading, error, fetchCourses } = useCourseStore();

  useEffect(() => {
    fetchCourses();
  }, [fetchCourses]);

  const handleCoursePress = useCallback((course: Course) => {
    navigation.navigate('CourseDetail', { slug: course.slug });
  }, [navigation]);

  if (isLoading) return <Loading />;
  if (error) return <ErrorView message={error} onRetry={fetchCourses} />;

  return (
    <SafeAreaView style={styles.container} edges={['top']}>
      <ScrollView 
        contentContainerStyle={styles.content}
        showsVerticalScrollIndicator={false}
      >
        {courses.map(course => (
          <CourseCard
            key={course.id}
            course={course}
            onPress={handleCoursePress}
          />
        ))}
      </ScrollView>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: colors.background,
  },
  content: {
    padding: spacing.lg,
  },
});
```

### Rules
- `SafeAreaView` для всех экранов
- Обязательно: Loading, Error states
- Логика в hooks/store, не в компоненте
- `useCallback` для navigation handlers

---

## Zustand Store

```tsx
import { create } from 'zustand';
import { persist, createJSONStorage } from 'zustand/middleware';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { courseService } from '@/services/courseService';
import { Course, CourseDetail } from '@/types/course';

interface CourseState {
  // State
  courses: Course[];
  currentCourse: CourseDetail | null;
  isLoading: boolean;
  error: string | null;
  
  // Actions
  fetchCourses: () => Promise<void>;
  fetchCourse: (slug: string) => Promise<void>;
  startCourse: (slug: string) => Promise<void>;
  reset: () => void;
}

const initialState = {
  courses: [],
  currentCourse: null,
  isLoading: false,
  error: null,
};

export const useCourseStore = create<CourseState>()(
  persist(
    (set, get) => ({
      ...initialState,
      
      fetchCourses: async () => {
        set({ isLoading: true, error: null });
        try {
          const courses = await courseService.getAll();
          set({ courses, isLoading: false });
        } catch (error) {
          set({ error: 'Не удалось загрузить курсы', isLoading: false });
        }
      },
      
      fetchCourse: async (slug) => {
        set({ isLoading: true, error: null });
        try {
          const course = await courseService.getBySlug(slug);
          set({ currentCourse: course, isLoading: false });
        } catch (error) {
          set({ error: 'Курс не найден', isLoading: false });
        }
      },
      
      startCourse: async (slug) => {
        try {
          await courseService.start(slug);
          await get().fetchCourse(slug);
        } catch (error) {
          set({ error: 'Не удалось начать курс' });
        }
      },
      
      reset: () => set(initialState),
    }),
    {
      name: 'course-storage',
      storage: createJSONStorage(() => AsyncStorage),
      partialize: (state) => ({ courses: state.courses }),
    }
  )
);
```

### Rules
- `create<StateType>()` с типизацией
- State + Actions в одном store
- `persist` middleware для офлайн
- `partialize` — выбирать что сохранять
- Error handling в каждом action
- Русские сообщения об ошибках
- `reset()` для очистки

---

## API Service

```tsx
// services/api.ts
import axios from 'axios';
import * as SecureStore from 'expo-secure-store';
import { API_URL } from '@/constants/api';

export const api = axios.create({
  baseURL: API_URL,
  timeout: 30000,
  headers: { 'Content-Type': 'application/json' },
});

api.interceptors.request.use(async (config) => {
  const token = await SecureStore.getItemAsync('access_token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response?.status === 401) {
      // Handle refresh token
    }
    return Promise.reject(error);
  }
);
```

```tsx
// services/courseService.ts
import { api } from './api';
import { Course, CourseDetail } from '@/types/course';

export const courseService = {
  getAll: async (): Promise<Course[]> => {
    const { data } = await api.get('/courses');
    return data;
  },
  
  getBySlug: async (slug: string): Promise<CourseDetail> => {
    const { data } = await api.get(`/courses/${slug}`);
    return data;
  },
  
  start: async (slug: string): Promise<void> => {
    await api.post(`/courses/${slug}/start`);
  },
};
```

### Rules
- Axios instance с interceptors
- SecureStore для токенов
- Типизированные responses
- Object syntax для service

---

## Types

```tsx
// types/course.ts
export interface Course {
  id: string;
  slug: string;
  name: string;
  description: string;
  isActive: boolean;
  sessionsCount: number;
}

export interface CourseDetail extends Course {
  scenarioJson: ScenarioJson;
  price: number;
  sessions: Session[];
}

// types/block.ts
export type BlockType =
  | 'STATIC'
  | 'INPUT'
  | 'SLIDER'
  | 'SINGLE_SELECT'
  | 'MULTI_SELECT'
  | 'LLM_CONVERSATION'
  | 'LLM_RESPONSE'
  | 'EXERCISE'
  | 'VISUALIZATION'
  | 'CALCULATION'
  | 'SESSION_COMPLETE'
  | 'PAYWALL';

export interface Block {
  id: string;
  type: BlockType;
  content: BlockContent;
  ui?: BlockUI;
  llm?: BlockLLM;
  next?: string;
  conditionalNext?: ConditionalNext[];
}

export interface BlockContent {
  text?: string;
  placeholder?: string;
  emoji?: string;
  options?: SelectOption[];
  min?: number;
  max?: number;
}

// types/user.ts
export interface UserData {
  name?: string;
  stressLevel?: number;
  triggers?: string[];
  [key: string]: unknown;
}
```

### Rules
- `interface` для объектов
- `type` для unions и primitives
- Extend для расширения
- `[key: string]: unknown` для гибких структур

---

## Theme

```tsx
// constants/theme.ts
export const colors = {
  // Brand
  primary: '#10B981',
  primaryDark: '#059669',
  
  // Background
  background: '#0F172A',
  card: '#1E293B',
  cardLight: '#334155',
  
  // Text
  text: '#F8FAFC',
  textSecondary: '#94A3B8',
  textMuted: '#64748B',
  
  // Semantic
  success: '#22C55E',
  warning: '#F59E0B',
  error: '#EF4444',
  
  // Stress
  stressLow: '#22C55E',
  stressMedium: '#F59E0B',
  stressHigh: '#EF4444',
} as const;

export const spacing = {
  xs: 4,
  sm: 8,
  md: 12,
  lg: 16,
  xl: 24,
  xxl: 32,
} as const;

export const borderRadius = {
  sm: 8,
  md: 12,
  lg: 16,
  xl: 24,
  full: 9999,
} as const;
```

---

## BlockRenderer Pattern

```tsx
// components/blocks/BlockRenderer.tsx
import React from 'react';
import { Block, BlockType } from '@/types/block';
import { UserData } from '@/types/user';
import { StaticBlock } from './StaticBlock';
import { SliderBlock } from './SliderBlock';
import { LLMConversationBlock } from './LLMConversationBlock';
// ... other imports

interface BlockRendererProps {
  block: Block;
  userData: UserData;
  onComplete: (data?: Record<string, unknown>) => void;
  onBack?: () => void;
}

const BLOCK_COMPONENTS: Record<BlockType, React.ComponentType<any>> = {
  STATIC: StaticBlock,
  SLIDER: SliderBlock,
  INPUT: InputBlock,
  SINGLE_SELECT: SingleSelectBlock,
  MULTI_SELECT: MultiSelectBlock,
  LLM_CONVERSATION: LLMConversationBlock,
  LLM_RESPONSE: LLMResponseBlock,
  EXERCISE: ExerciseBlock,
  VISUALIZATION: VisualizationBlock,
  CALCULATION: CalculationBlock,
  SESSION_COMPLETE: SessionCompleteBlock,
  PAYWALL: PaywallBlock,
};

export const BlockRenderer: React.FC<BlockRendererProps> = ({
  block,
  userData,
  onComplete,
  onBack,
}) => {
  const Component = BLOCK_COMPONENTS[block.type];
  
  if (!Component) {
    console.warn(`Unknown block type: ${block.type}`);
    return null;
  }
  
  return (
    <Component
      block={block}
      userData={userData}
      onComplete={onComplete}
      onBack={onBack}
    />
  );
};
```

---

## Custom Hook

```tsx
// hooks/useTimer.ts
import { useState, useEffect, useCallback, useRef } from 'react';

interface UseTimerOptions {
  duration: number;
  onComplete?: () => void;
  autoStart?: boolean;
}

export const useTimer = ({ duration, onComplete, autoStart = false }: UseTimerOptions) => {
  const [timeLeft, setTimeLeft] = useState(duration);
  const [isRunning, setIsRunning] = useState(autoStart);
  const intervalRef = useRef<NodeJS.Timeout>();

  useEffect(() => {
    if (!isRunning) return;
    
    intervalRef.current = setInterval(() => {
      setTimeLeft((prev) => {
        if (prev <= 1) {
          setIsRunning(false);
          onComplete?.();
          return 0;
        }
        return prev - 1;
      });
    }, 1000);
    
    return () => clearInterval(intervalRef.current);
  }, [isRunning, onComplete]);

  const start = useCallback(() => setIsRunning(true), []);
  const pause = useCallback(() => setIsRunning(false), []);
  const reset = useCallback(() => {
    setIsRunning(false);
    setTimeLeft(duration);
  }, [duration]);

  return { timeLeft, isRunning, start, pause, reset };
};
```

---

## Template Utils

```tsx
// utils/template.ts
export function resolveTemplate(
  template: string,
  userData: Record<string, unknown>
): string {
  return template.replace(/\{\{([^}]+)\}\}/g, (match, path) => {
    const value = getValueByPath(userData, path.trim());
    return value !== undefined ? String(value) : match;
  });
}

function getValueByPath(obj: Record<string, unknown>, path: string): unknown {
  // Handle array: triggers[0]
  const arrayMatch = path.match(/^(\w+)\[(\d+)\]$/);
  if (arrayMatch) {
    const arr = obj[arrayMatch[1]];
    if (Array.isArray(arr)) {
      return arr[parseInt(arrayMatch[2], 10)];
    }
  }
  
  // Handle nested: user.profile.name
  return path.split('.').reduce((acc: any, key) => acc?.[key], obj);
}
```

---

## Naming Conventions

| Type | Pattern | Example |
|------|---------|---------|
| Screen | `{Name}Screen.tsx` | `CourseListScreen.tsx` |
| Component | `{Name}.tsx` | `CourseCard.tsx` |
| Store | `use{Name}Store.ts` | `useCourseStore.ts` |
| Service | `{name}Service.ts` | `courseService.ts` |
| Hook | `use{Name}.ts` | `useTimer.ts` |
| Type file | `{name}.ts` | `course.ts` |
| Constant | `UPPER_SNAKE` | `API_URL` |

---

## Common Patterns

### Loading State
```tsx
if (isLoading) return <Loading />;
if (error) return <ErrorView message={error} onRetry={retry} />;
```

### Keyboard Avoiding
```tsx
<KeyboardAvoidingView
  behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
  style={{ flex: 1 }}
>
  {/* content */}
</KeyboardAvoidingView>
```

### List Performance
```tsx
<FlatList
  data={items}
  renderItem={renderItem}
  keyExtractor={(item) => item.id}
  removeClippedSubviews
  maxToRenderPerBatch={10}
  windowSize={5}
/>
```

### Animations
```tsx
// Enter
entering={FadeIn.duration(300)}

// Exit
exiting={FadeOut.duration(200)}

// Layout
layout={Layout.springify()}

// Shared
sharedTransitionTag="course-card"
```

---

## Testing

```tsx
import { render, fireEvent } from '@testing-library/react-native';
import { CourseCard } from './CourseCard';

describe('CourseCard', () => {
  const mockCourse = {
    id: '1',
    slug: 'test',
    name: 'Test Course',
    description: 'Description',
  };

  it('renders course name', () => {
    const { getByText } = render(
      <CourseCard course={mockCourse} onPress={jest.fn()} />
    );
    expect(getByText('Test Course')).toBeTruthy();
  });

  it('calls onPress when pressed', () => {
    const onPress = jest.fn();
    const { getByText } = render(
      <CourseCard course={mockCourse} onPress={onPress} />
    );
    fireEvent.press(getByText('Test Course'));
    expect(onPress).toHaveBeenCalledWith(mockCourse);
  });
});
```

---

*Version 1.0*
