# Getting Started with Scenario Editor Frontend

## Prerequisites

- Node.js 18+ and npm
- Backend API running on `http://localhost:8080`

## Installation

```bash
cd scenario-editor-front
npm install
```

## Configuration

Create `.env` file in the root directory:

```bash
cp .env.example .env
```

Default configuration:
```
VITE_API_URL=http://localhost:8080/api
```

## Running the Application

### Development Mode

```bash
npm run dev
```

Application will be available at `http://localhost:3000`

### Production Build

```bash
npm run build
npm run preview
```

## Default Login Credentials

```
Email: admin@cbt.com
Password: Admin123!
```

## Project Architecture

### Technology Stack

- **React 18** - UI library
- **TypeScript 5** - Type safety
- **Vite** - Build tool and dev server
- **Material-UI (MUI)** - Component library
- **Zustand** - State management
- **React Router** - Client-side routing
- **Axios** - HTTP client

### Folder Structure

```
src/
├── components/          # Reusable UI components
│   └── layout/          # Layout components (Header, Sidebar)
├── pages/               # Page components
│   ├── LoginPage.tsx    # Authentication
│   ├── DraftsPage.tsx   # Draft list and management
│   ├── EditorPage.tsx   # Draft editor
│   └── TemplatesPage.tsx # Block templates library
├── services/            # API integration layer
│   ├── api.ts           # Axios client with interceptors
│   ├── authService.ts   # Authentication API
│   ├── draftService.ts  # Draft management API
│   └── templateService.ts # Template API
├── store/               # Zustand state management
│   ├── authStore.ts     # User authentication state
│   ├── draftStore.ts    # Draft management state
│   └── templateStore.ts # Template state
├── types/               # TypeScript type definitions
│   ├── auth.ts
│   ├── draft.ts
│   ├── block.ts
│   ├── template.ts
│   └── api.ts
├── App.tsx              # Root component with routing
└── main.tsx             # Application entry point
```

## Features

### 1. Authentication
- Login with JWT tokens
- Automatic token refresh
- Role-based access control (ADMIN, EDITOR, VIEWER)

### 2. Draft Management
- View all scenario drafts
- Create new drafts
- Edit draft metadata
- Delete drafts
- Version history tracking

### 3. Visual Editor
- Course information editing
- Session management
- Block configuration
- JSON preview
- Real-time validation

### 4. Block Templates
- Browse template library
- Filter by category and block type
- Template usage statistics

### 5. Publishing Workflow
- Validate draft structure
- Publish to production courses
- Status tracking (DRAFT → IN_REVIEW → APPROVED → PUBLISHED)

## API Integration

The application communicates with the backend API via REST endpoints:

### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration
- `POST /api/auth/refresh` - Refresh access token
- `POST /api/auth/logout` - User logout

### Drafts
- `GET /api/editor/drafts` - List all drafts
- `GET /api/editor/drafts/:id` - Get draft details
- `POST /api/editor/drafts` - Create new draft
- `PUT /api/editor/drafts/:id` - Update draft
- `DELETE /api/editor/drafts/:id` - Delete draft
- `POST /api/editor/drafts/:id/validate` - Validate draft
- `POST /api/editor/drafts/:id/publish` - Publish draft
- `GET /api/editor/drafts/:id/versions` - Get version history

### Templates
- `GET /api/editor/templates/blocks` - List block templates
- `GET /api/editor/templates/blocks/:id` - Get template details
- `POST /api/editor/templates/blocks` - Create template
- `DELETE /api/editor/templates/blocks/:id` - Delete template

## User Roles

### ADMIN
- Full access to all features
- Can create, edit, delete drafts
- Can validate and publish drafts
- Can manage templates

### EDITOR
- Can create and edit drafts
- Cannot publish drafts
- Can use templates
- Read-only access to published courses

### VIEWER
- Read-only access to all drafts
- Cannot modify or publish
- Can view templates

## Development Workflow

### 1. Start Backend API
```bash
cd backend
./mvnw spring-boot:run
```

### 2. Start Frontend Dev Server
```bash
cd scenario-editor-front
npm run dev
```

### 3. Login
Navigate to `http://localhost:3000/login` and use default credentials

### 4. Create Draft
1. Click "New Draft" button
2. Enter title and description
3. Click "Create"

### 5. Edit Draft
1. Click on a draft card
2. Use tabs to navigate: Course Info, Sessions, JSON Preview
3. Make changes
4. Click "Save"

### 6. Validate & Publish
1. Click "Validate" to check draft structure
2. Fix any validation errors
3. Click "Publish" (ADMIN only)
4. Enter course slug
5. Confirm publication

## Troubleshooting

### CORS Errors
Ensure backend CORS configuration allows `http://localhost:3000`

### API Connection Issues
Check that backend is running on `http://localhost:8080`

### Authentication Errors
- Clear browser localStorage
- Restart both frontend and backend
- Check JWT secret consistency

### Build Errors
```bash
rm -rf node_modules package-lock.json
npm install
```

## Next Steps

### Phase 1 (Current) - Basic CRUD
✅ Authentication and authorization
✅ Draft list view
✅ Basic editor with tabs
✅ Validation and publishing
✅ Template library view

### Phase 2 - Enhanced Editor
- [ ] Drag-and-drop session builder
- [ ] Visual block editor with forms
- [ ] Block reordering
- [ ] Conditional navigation visual editor
- [ ] Real-time collaboration

### Phase 3 - Advanced Features
- [ ] Template preview with sample data
- [ ] Bulk operations
- [ ] Export/import scenarios
- [ ] Version comparison (diff view)
- [ ] Comments and annotations

## Resources

- [React Documentation](https://react.dev)
- [Material-UI Documentation](https://mui.com)
- [Vite Documentation](https://vitejs.dev)
- [Zustand Documentation](https://github.com/pmndrs/zustand)

---

**Need help?** Check the backend API documentation or raise an issue in the project repository.
