## Why

The current CMS has basic category and content management but lacks Markdown editing support and multi-level category navigation on the frontend. Users need to author content in Markdown format (popular among technical writers) and navigate content through a hierarchical category structure. This change enhances the authoring experience and improves content discoverability.

## What Changes

- Add Markdown editing support to the content editor (bidirectional MD ↔ HTML conversion)
- Enhance the content editor toolbar with more formatting options (code blocks, links, blockquotes)
- Add multi-level category navigation with expandable tree structure on frontend
- Display subcategories on category pages to enable hierarchical browsing
- Show content counts per category in the navigation tree

## Capabilities

### New Capabilities

- `markdown-editor`: Rich text editor with Markdown support, including MD input, preview, and bidirectional conversion. Supports code blocks, tables, links, and images.

### Modified Capabilities

- `category-navigation`: Enhanced multi-level category navigation showing hierarchical tree with content counts and subcategory display on category pages.

## Impact

- **Frontend**: ContentEditor.vue - add Markdown mode toggle and conversion; CategoryPage.vue - show subcategories; Home.vue - enhance tree display
- **Backend**: CategoryController/Service - add content count per category; potentially MarkdownUtil for server-side MD conversion
- **Dependencies**: May need to add markdown-it or similar library for MD parsing
