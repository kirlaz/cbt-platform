# Scenario Editor Frontend Architecture

## Overview

Visual web application for creating and managing CBT course scenarios. Built for psychologists and content creators (non-technical users).

## Tech Stack

- **React 18** - UI library with hooks
- **TypeScript 5** - Type-safe development
- **Vite 5** - Fast build tool, HMR
- **Material-UI 5** - Component library (AppBar, Drawer, Cards, Forms)
- **Zustand 4** - Lightweight state management
- **React Router 6** - Client-side routing
- **Axios** - HTTP client with interceptors

## Architecture Layers

```
┌─────────────────────────────────────────┐
│         Presentation Layer              │
│  (Pages + Components + Layout)          │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│         State Management Layer          │
│    (Zustand Stores: auth, draft, etc)   │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│         Service Layer                   │
│   (API Services: auth, draft, template) │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│         HTTP Client Layer               │
│  (Axios with JWT interceptors)          │
└─────────────────┬───────────────────────┘
                  │
                  ▼
         Backend REST API
    (http://localhost:8080/api)
```

## Folder Structure

```
src/
├── main.tsx                 # Entry point
├── App.tsx                  # Root component with routing
├── vite-env.d.ts            # Vite environment types
│
├── components/              # Reusable UI components
│   └── layout/
│       └── Layout.tsx       # AppBar + Sidebar + Outlet
│
├── pages/                   # Page components (routes)
│   ├── LoginPage.tsx        # Authentication page
│   ├── DraftsPage.tsx       # Draft list with cards
│   ├── EditorPage.tsx       # Draft editor (tabs: info, sessions, JSON)
│   └── TemplatesPage.tsx    # Block templates library
│
├── services/                # API integration
│   ├── api.ts               # Axios client + JWT interceptors
│   ├── authService.ts       # Login, register, logout
│   ├── draftService.ts      # CRUD, validate, publish drafts
│   └── templateService.ts   # Fetch block templates
│
├── store/                   # Zustand state management
│   ├── authStore.ts         # User, isAuthenticated, login/logout
│   ├── draftStore.ts        # Drafts list, currentDraft, CRUD actions
│   └── templateStore.ts     # Templates list, fetch actions
│
└── types/                   # TypeScript definitions
    ├── auth.ts              # User, AuthResponse, LoginRequest
    ├── draft.ts             # ScenarioDraft, DraftStatus, ValidationResult
    ├── block.ts             # Block, BlockType, Session
    ├── template.ts          # BlockTemplate, TemplateCategory
    └── api.ts               # ApiError, PageResponse
```

## Key Design Patterns

### 1. Service Layer Pattern
All API calls go through dedicated service classes:

```typescript
// services/draftService.ts
class DraftService {
  async getAllDrafts(): Promise<PageResponse<ScenarioDraft>> {
    return apiClient.get('/editor/drafts');
  }
}
export const draftService = new DraftService();
```

**Benefits**: Centralized API logic, easy mocking for tests, consistent error handling

### 2. Zustand State Management
Minimal boilerplate, no providers needed:

```typescript
// store/draftStore.ts
export const useDraftStore = create<DraftState>((set, get) => ({
  drafts: [],
  fetchDrafts: async () => {
    const response = await draftService.getAllDrafts();
    set({ drafts: response.content });
  },
}));

// Usage in component
const { drafts, fetchDrafts } = useDraftStore();
```

**Benefits**: Simple API, built-in selectors, TypeScript support

### 3. JWT Interceptor Pattern
Automatic token injection and refresh:

```typescript
// services/api.ts
this.client.interceptors.request.use((config) => {
  const token = getAccessToken();
  config.headers.Authorization = `Bearer ${token}`;
  return config;
});

this.client.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response?.status === 401) {
      // Refresh token and retry
    }
  }
);
```

**Benefits**: Automatic auth, transparent refresh, no manual token management

### 4. Private Route Pattern
Protected routes using React Router:

```typescript
// App.tsx
function PrivateRoute({ children }) {
  const isAuthenticated = useAuthStore((state) => state.isAuthenticated);
  return isAuthenticated ? children : <Navigate to="/login" />;
}

<Route path="/" element={<PrivateRoute><Layout /></PrivateRoute>}>
  <Route path="drafts" element={<DraftsPage />} />
</Route>
```

**Benefits**: Declarative auth checks, automatic redirects

## Data Flow

### Login Flow
```
1. User submits credentials → LoginPage
2. LoginPage calls authStore.login()
3. authStore calls authService.login()
4. authService calls API POST /auth/login
5. API returns { accessToken, refreshToken, user }
6. Tokens stored in localStorage
7. User object stored in authStore
8. Navigate to /drafts
```

### Draft Creation Flow
```
1. User clicks "New Draft" → DraftsPage
2. Dialog opens with form
3. User enters title/description
4. onClick calls draftStore.createDraft()
5. draftStore calls draftService.createDraft()
6. Service calls API POST /editor/drafts
7. API returns new ScenarioDraft
8. Store adds draft to state
9. Navigate to /drafts/:id (editor)
```

### Draft Publishing Flow
```
1. User clicks "Publish" → EditorPage
2. Dialog opens for course slug
3. User enters slug
4. onClick calls draftStore.publishDraft(id, slug)
5. Store calls draftService.publishDraft()
6. Service calls API POST /editor/drafts/:id/publish
7. API creates/updates Course
8. Draft status → PUBLISHED
9. Success notification
```

## Authentication & Authorization

### Token Storage
- **Access Token**: Short-lived (15 min), stored in localStorage
- **Refresh Token**: Long-lived (7 days), stored in localStorage
- **Auto Refresh**: Interceptor catches 401, refreshes token, retries request

### Role-Based Access
```typescript
// authService.ts
isAdmin() { return user?.role === 'ADMIN'; }
isEditor() { return ['ADMIN', 'EDITOR'].includes(user?.role); }
```

UI hides buttons based on role:
```typescript
// DraftsPage.tsx
{isEditor && <Button>New Draft</Button>}
{canPublish && <Button>Publish</Button>}
```

## State Management Strategy

### Zustand Stores

**authStore**: Global user state
- `user`, `isAuthenticated`, `isLoading`, `error`
- Actions: `login()`, `logout()`, `checkAuth()`

**draftStore**: Draft management
- `drafts[]`, `currentDraft`, `validationResult`
- Actions: `fetchDrafts()`, `createDraft()`, `updateDraft()`, `publishDraft()`

**templateStore**: Template library
- `templates[]`
- Actions: `fetchTemplates()`, `createTemplate()`

### Why Zustand over Redux?
- Less boilerplate (no actions, reducers, middleware)
- No Provider wrapper needed
- Built-in TypeScript support
- Simpler async handling
- Smaller bundle size

## API Integration

### Base URL Configuration
```
Development: http://localhost:8080/api (via Vite proxy)
Production: https://api.cbt-app.com/api
```

### Error Handling
All API errors normalized to `ApiError`:
```typescript
interface ApiError {
  message: string;
  status: number;
  errors?: FieldError[];
}
```

Errors stored in Zustand stores and displayed via MUI `<Alert>` components.

### Pagination
Backend uses Spring Data pagination:
```typescript
interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  // ...
}
```

Frontend currently fetches `size: 100` (no pagination UI yet).

## UI/UX Patterns

### Material-UI Components Used
- **AppBar + Drawer**: Main layout with sidebar
- **Card**: Draft cards in grid
- **Dialog**: Modals for create/publish
- **Tabs**: Editor page (Course Info / Sessions / JSON)
- **Alert**: Error/success messages
- **Chip**: Status badges
- **CircularProgress**: Loading states

### Responsive Design
- Grid layout for draft cards (xs=12, sm=6, md=4)
- Fixed drawer (240px width)
- Mobile: Drawer can be collapsed (future enhancement)

### Loading States
```typescript
if (isLoading && drafts.length === 0) {
  return <CircularProgress />;
}
```

### Error States
```typescript
{error && <Alert severity="error">{error}</Alert>}
```

### Empty States
```typescript
{drafts.length === 0 && (
  <Typography>No drafts yet. Click "New Draft".</Typography>
)}
```

## Future Enhancements

### Phase 2: Enhanced Editor
- [ ] Drag-and-drop session builder
- [ ] Visual block editor with dynamic forms
- [ ] Inline editing (no dialog modals)
- [ ] Block reordering
- [ ] Conditional navigation visual builder
- [ ] Real-time collaboration (WebSockets)

### Phase 3: Advanced Features
- [ ] Version comparison (diff view)
- [ ] Template preview with sample data
- [ ] Bulk operations (duplicate, delete multiple)
- [ ] Export/import JSON scenarios
- [ ] Comments and annotations
- [ ] Search and filters
- [ ] Dark mode

### Phase 4: User Experience
- [ ] Keyboard shortcuts
- [ ] Undo/redo
- [ ] Autosave
- [ ] Offline support
- [ ] Mobile responsive improvements

## Testing Strategy (Future)

### Unit Tests (Jest + RTL)
- Components: LoginPage, DraftsPage rendering
- Stores: Zustand store actions
- Services: API service methods (mocked axios)
- Utils: Helper functions

### Integration Tests
- Auth flow: Login → Navigate → Logout
- Draft CRUD: Create → Edit → Save → Delete
- Publishing: Validate → Publish → Verify status

### E2E Tests (Playwright)
- Full user journeys
- Cross-browser testing
- Visual regression testing

## Performance Considerations

### Bundle Size
- Material-UI: ~300KB (tree-shakeable)
- React + ReactDOM: ~130KB
- Zustand: ~1KB
- Axios: ~13KB
- Total (gzipped): ~150-200KB

### Optimization Techniques
- Code splitting (React.lazy for pages)
- Vite's automatic chunking
- Material-UI's tree-shaking
- Image optimization (future: use WebP)

### Loading Performance
- Vite HMR: <100ms updates
- Production build: Pre-rendering
- CDN deployment (future)

## Deployment

### Development
```bash
npm run dev  # localhost:3000
```

### Production Build
```bash
npm run build  # Output: dist/
npm run preview  # Test production build
```

### Hosting Options
- **Vercel**: Zero-config, automatic deployments
- **Netlify**: Built-in redirects for SPA
- **Cloudflare Pages**: Global CDN
- **AWS S3 + CloudFront**: Custom domain

### Environment Variables
```
VITE_API_URL=https://api.cbt-app.com/api
```

## Security Considerations

### XSS Prevention
- React's automatic escaping
- No `dangerouslySetInnerHTML` used
- Content Security Policy (CSP) headers

### CSRF Protection
- JWT tokens (stateless, no cookies)
- SameSite cookie attribute (future: if using cookies)

### Token Security
- Access token: Short expiry (15 min)
- Refresh token: HTTP-only cookie (future enhancement)
- Logout: Clear tokens immediately

### HTTPS Only
- Production: Enforce HTTPS
- HSTS headers
- Secure cookie flags

---

**Last Updated**: December 2024
**Maintainer**: CBT Platform Team
