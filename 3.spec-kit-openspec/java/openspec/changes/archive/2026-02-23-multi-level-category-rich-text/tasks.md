## 1. Frontend Dependencies

- [x] 1.1 Install tiptap-markdown extension in frontend
- [x] 1.2 Install @tiptap/extension-code-block-lowlight for syntax highlighting (skipped - requires tiptap v3, using built-in CodeBlock from starter-kit)
- [x] 1.3 Install @tiptap/extension-table for table support

## 2. Backend Category Content Count

- [x] 2.1 Add countPublishedByCategory method to ContentRepository
- [x] 2.2 Update CategoryTreeService to include content counts in tree nodes
- [x] 2.3 Update CategoryTreeNode DTO to include contentCount field
- [x] 2.4 Update CategoryController tree endpoint to return counts

## 3. Frontend Markdown Editor

- [x] 3.1 Add Markdown mode toggle switch to ContentEditor.vue
- [x] 3.2 Configure tiptap-markdown extension for MD input/output
- [x] 3.3 Add code block button with language selector to toolbar
- [x] 3.4 Add link and blockquote buttons to toolbar
- [x] 3.5 Add table insertion button to toolbar
- [x] 3.6 Implement HTML to MD conversion when switching to MD mode
- [x] 3.7 Implement MD to HTML conversion when saving content
- [x] 3.8 Add split-view preview panel for MD mode

## 4. Frontend Category Navigation Enhancement

- [x] 4.1 Update Home.vue to display content counts in category tree
- [x] 4.2 Ensure el-tree supports expand/collapse for all category levels
- [x] 4.3 Update CategoryPage.vue to display subcategories section
- [x] 4.4 Add subcategory cards with content counts and navigation links
- [x] 4.5 Style multi-level tree indentation for visual hierarchy

## 5. Testing & Verification

- [x] 5.1 Test MD to HTML conversion for all supported syntax elements
- [x] 5.2 Test HTML to MD roundtrip preserves content
- [x] 5.3 Test category tree expansion at multiple levels
- [x] 5.4 Test subcategory navigation on category pages
- [x] 5.5 Verify content counts are accurate after content changes
