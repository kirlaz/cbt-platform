# Quick Reference - Scenario Editor Frontend

## One-Line Start

```bash
cd scenario-editor-front && npm install && npm run dev
```

## URLs

- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080/api
- **Login**: admin@cbt.com / Admin123!

## Project Structure (Essential)

```
src/
├── pages/                   # 4 pages: Login, Drafts, Editor, Templates
├── services/                # API calls: authService, draftService, templateService
├── store/                   # State: authStore, draftStore, templateStore
├── types/                   # TypeScript: auth, draft, block, template, api
└── App.tsx                  # Routing + PrivateRoute
```

## Key Files

| File | Purpose |
|------|---------|
| `src/services/api.ts` | Axios client + JWT interceptors |
| `src/store/authStore.ts` | User authentication state |
| `src/store/draftStore.ts` | Draft CRUD operations |
| `src/pages/DraftsPage.tsx` | Draft list with create/delete |
| `src/pages/EditorPage.tsx` | Draft editor with validate/publish |
| `src/components/layout/Layout.tsx` | AppBar + Sidebar navigation |

## API Endpoints Used

```
POST   /api/auth/login              Login
POST   /api/auth/refresh            Refresh token
GET    /api/editor/drafts           List drafts
POST   /api/editor/drafts           Create draft
GET    /api/editor/drafts/:id       Get draft
PUT    /api/editor/drafts/:id       Update draft
DELETE /api/editor/drafts/:id       Delete draft
POST   /api/editor/drafts/:id/validate    Validate
POST   /api/editor/drafts/:id/publish     Publish
GET    /api/editor/templates/blocks       List templates
```

## State Management (Zustand)

**authStore**:
```typescript
const { user, isAuthenticated, login, logout } = useAuthStore();
```

**draftStore**:
```typescript
const {
  drafts,
  currentDraft,
  fetchDrafts,
  createDraft,
  updateDraft,
  deleteDraft,
  validateDraft,
  publishDraft
} = useDraftStore();
```

**templateStore**:
```typescript
const { templates, fetchTemplates } = useTemplateStore();
```

## Routing

```
/login              LoginPage (public)
/                   → /drafts (redirect)
/drafts             DraftsPage (private)
/drafts/:id         EditorPage (private)
/templates          TemplatesPage (private)
```

## TypeScript Types

**User**:
```typescript
interface User {
  id: string;
  email: string;
  role: 'ADMIN' | 'EDITOR' | 'VIEWER';
}
```

**ScenarioDraft**:
```typescript
interface ScenarioDraft {
  id: string;
  title: string;
  status: 'DRAFT' | 'IN_REVIEW' | 'APPROVED' | 'PUBLISHED';
  scenarioContent: {
    courseInfo: CourseInfo;
    sessions: Session[];
  };
}
```

**Block**:
```typescript
interface Block {
  id: string;
  type: BlockType;  // STATIC, INPUT, LLM_CONVERSATION, etc.
  title?: string;
  content?: string;
  next?: string;
  // ... type-specific fields
}
```

## Common Commands

```bash
npm install              Install dependencies
npm run dev              Start dev server (port 3000)
npm run build            Production build
npm run preview          Preview production build
npm run type-check       TypeScript validation
```

## Environment

```bash
# .env file
VITE_API_URL=http://localhost:8080/api
```

## Material-UI Components

- **Layout**: AppBar, Drawer, Toolbar
- **Navigation**: List, ListItem, ListItemButton
- **Content**: Card, CardContent, CardActions, Paper
- **Forms**: TextField, Button, Dialog
- **Feedback**: Alert, CircularProgress, Chip
- **Data**: Grid, Box, Typography, Tabs

## Authentication Flow

1. User enters email/password → LoginPage
2. POST /api/auth/login → returns { accessToken, refreshToken, user }
3. Tokens saved to localStorage
4. Navigate to /drafts
5. All requests include `Authorization: Bearer ${accessToken}`
6. On 401 → refresh token → retry request
7. Logout → clear localStorage → navigate to /login

## Draft Lifecycle

```
CREATE → DRAFT → VALIDATE → APPROVED → PUBLISH → PUBLISHED
```

## Role Permissions

| Action | ADMIN | EDITOR | VIEWER |
|--------|-------|--------|--------|
| View drafts | ✅ | ✅ | ✅ |
| Create draft | ✅ | ✅ | ❌ |
| Edit draft | ✅ | ✅ | ❌ |
| Delete draft | ✅ | ✅ | ❌ |
| Validate | ✅ | ✅ | ❌ |
| Publish | ✅ | ❌ | ❌ |

## Troubleshooting

**API connection error**:
- Check backend is running: http://localhost:8080
- Check .env has correct VITE_API_URL

**401 Unauthorized**:
- Clear localStorage in browser DevTools
- Login again

**CORS error**:
- Backend must allow origin: http://localhost:3000

**Build error**:
```bash
rm -rf node_modules package-lock.json
npm install
```

## Next Steps After MVP

- [ ] Drag-and-drop session builder
- [ ] Visual block editor with forms
- [ ] Real-time validation feedback
- [ ] Version comparison (diff view)
- [ ] Collaborative editing
- [ ] Search and filters
- [ ] Unit tests (Jest + RTL)
- [ ] E2E tests (Playwright)

---

**Quick Links**:
- [Full Getting Started Guide](./GETTING_STARTED.md)
- [Architecture Documentation](./ARCHITECTURE.md)
- [Backend API Docs](../backend/TEST_API.md)
- [Main Project Docs](../CLAUDE.md)
