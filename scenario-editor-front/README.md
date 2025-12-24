# Scenario Editor Frontend

Visual scenario editor for CBT Platform - веб-приложение для создания и редактирования сценариев курсов.

## Tech Stack

- **React 18** + **TypeScript 5**
- **Vite** - fast build tool
- **Material-UI** - component library
- **Zustand** - state management
- **Axios** - HTTP client
- **React Router** - routing

## Quick Start

```bash
# Install dependencies
npm install

# Create .env file
cp .env.example .env

# Start dev server
npm run dev
```

Application will be available at `http://localhost:3000`

## Development

```bash
npm run dev          # Start dev server with hot reload
npm run build        # Build for production
npm run preview      # Preview production build
npm run type-check   # Run TypeScript type checking
```

## Project Structure

```
scenario-editor-front/
├── src/
│   ├── components/          # Reusable UI components
│   │   ├── layout/          # Layout components (Header, Sidebar)
│   │   ├── blocks/          # Block editor components
│   │   └── common/          # Common components (Button, Input, etc.)
│   ├── pages/               # Page components (routes)
│   │   ├── LoginPage.tsx
│   │   ├── DraftsPage.tsx
│   │   ├── EditorPage.tsx
│   │   └── TemplatesPage.tsx
│   ├── services/            # API services
│   │   ├── api.ts           # Axios instance
│   │   ├── authService.ts
│   │   ├── draftService.ts
│   │   └── templateService.ts
│   ├── store/               # Zustand stores
│   │   ├── authStore.ts
│   │   ├── draftStore.ts
│   │   └── editorStore.ts
│   ├── types/               # TypeScript types
│   │   ├── auth.ts
│   │   ├── draft.ts
│   │   ├── block.ts
│   │   └── api.ts
│   ├── utils/               # Utility functions
│   │   ├── validation.ts
│   │   └── formatting.ts
│   ├── hooks/               # Custom React hooks
│   │   └── useDebounce.ts
│   ├── constants/           # Constants and configs
│   │   └── blockTypes.ts
│   ├── App.tsx              # Root component with routing
│   └── main.tsx             # Entry point
├── index.html
├── vite.config.ts
├── tsconfig.json
└── package.json
```

## Features

- **Authentication**: Login with JWT tokens (access + refresh)
- **Draft Management**: Create, edit, delete, version history
- **Visual Editor**: Drag-and-drop block builder
- **Block Templates**: Library of ready-to-use blocks
- **Validation**: Real-time scenario structure validation
- **Publishing**: Publish drafts to production courses
- **Version History**: Track all changes to drafts

## API Integration

Backend API runs on `http://localhost:8080/api`

Vite proxy configuration redirects `/api` requests to backend automatically.

## Environment Variables

Create `.env` file:

```
VITE_API_URL=http://localhost:8080/api
```

## User Roles

- **ADMIN**: Full access (create, edit, delete, publish)
- **EDITOR**: Create and edit drafts (cannot publish)
- **VIEWER**: Read-only access

## Build for Production

```bash
npm run build
```

Output will be in `dist/` folder. Deploy to any static hosting (Vercel, Netlify, etc.)

---

**Status**: 🚧 In Development
