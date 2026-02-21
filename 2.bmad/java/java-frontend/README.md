# Java CMS Frontend

A lightweight CMS frontend built with Vue.js 3 and Ant Design Vue.

## Tech Stack

- **Framework**: Vue.js 3.4
- **Build Tool**: Vite 5
- **Router**: Vue Router 4
- **State Management**: Pinia
- **HTTP Client**: Axios
- **UI Library**: Ant Design Vue 4
- **Markdown**: markdown-it + highlight.js

## Project Structure

```
src/
├── main.js              # Application entry
├── App.vue              # Root component
├── views/               # Page components
│   ├── Home.vue         # Frontend home page
│   ├── Article.vue      # Article detail page
│   ├── Category.vue     # Category page
│   ├── NotFound.vue     # 404 page
│   └── admin/           # Admin pages
│       ├── Login.vue
│       ├── Layout.vue
│       ├── Dashboard.vue
│       ├── ArticleList.vue
│       ├── ArticleEdit.vue
│       ├── CategoryManage.vue
│       └── UserManage.vue
├── components/          # Reusable components
│   ├── ArticleCard.vue
│   └── MarkdownEditor.vue
├── api/                 # API services
│   └── index.js
├── store/               # Pinia store
│   └── index.js
├── router/              # Vue Router config
│   └── index.js
├── utils/               # Utility functions
│   └── index.js
└── styles/              # Global styles
    └── global.css
```

## Getting Started

### Prerequisites

- Node.js 18+
- npm 9+

### Installation

```bash
# Install dependencies
npm install

# Start development server
npm run dev

# Build for production
npm run build

# Preview production build
npm run preview
```

### Development

```bash
npm run dev
```

The app will be available at http://localhost:3000

## Features

### Frontend (Public)
- Article list on homepage
- Article detail with Markdown rendering
- Category filtering
- Search by title

### Admin Panel
- Login/Logout
- Dashboard with statistics
- Article CRUD with Markdown editor
- Category management
- User management

## API Proxy

Development server proxies `/api` requests to `http://localhost:8080`

## License

Internal use only.
