# Feature Specification: Multi-Level Categories & Rich Text Enhancement

**Feature Branch**: `001-multi-level-categories`
**Created**: 2026-02-13
**Status**: Draft
**Input**: User description: "实现且只实现 @doc/迭代1.md 中的需求， 这是一个pc项目。 我喜欢home就是前台，然后前台有按钮可以跳转到后台"

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Browse Content by Multi-Level Categories (Priority: P1)

As a visitor to the front-end website, I want to browse content organized by multi-level categories so that I can easily find content relevant to my interests at any level of the category hierarchy.

**Why this priority**: This is the core value proposition - enabling users to navigate and discover content through a hierarchical category structure. Without this, the multi-level category feature has no user-facing value.

**Independent Test**: Can be fully tested by creating a 3-level category hierarchy (e.g., Technology > Programming > Java), adding content to each level, and verifying that users can navigate through all levels and see appropriate content at each level. Delivers immediate value by improving content discoverability.

**Acceptance Scenarios**:

1. **Given** a visitor is on the home page, **When** they view the navigation bar, **Then** they see a hierarchical category menu showing parent and child categories
2. **Given** a visitor selects a parent category, **When** the category page loads, **Then** they see content from that category AND all its child categories
3. **Given** a visitor selects a child category, **When** the category page loads, **Then** they see only content specifically assigned to that child category
4. **Given** a category has multiple levels of children, **When** the visitor navigates, **Then** they can drill down through each level to reach the most specific category

---

### User Story 2 - Manage Multi-Level Categories in Admin (Priority: P2)

As a content manager, I want to create, edit, and delete categories at any level in the hierarchy so that I can organize content in a meaningful multi-level structure.

**Why this priority**: Essential for content organization but requires P1 to be valuable - categories without content display have limited value.

**Independent Test**: Can be fully tested by creating a category hierarchy with 5+ levels, editing category names and parent relationships, and deleting categories. Delivers value by enabling flexible content organization.

**Acceptance Scenarios**:

1. **Given** a content manager is in the admin panel, **When** they create a new category, **Then** they can optionally select a parent category to create a child category
2. **Given** a content manager views the category list, **When** the list displays, **Then** categories are shown in a hierarchical tree structure with visual indentation
3. **Given** a content manager edits a category, **When** they change the parent category, **Then** the category moves to the new position in the hierarchy
4. **Given** a content manager deletes a parent category, **When** the deletion is confirmed, **Then** all child categories and associated content are either deleted or reassigned based on user choice

---

### User Story 3 - Rich Text Editor with Image Upload (Priority: P3)

As a content editor, I want to use a rich text editor with image upload capability when creating or editing content so that I can create visually rich content with embedded images.

**Why this priority**: Enhances content creation experience but content can still be created and displayed without this feature using basic markdown.

**Independent Test**: Can be fully tested by creating content with various formatting (bold, italic, headings, lists), uploading images, and verifying that the content displays correctly on the front-end with all formatting and images intact. Delivers value by enabling rich, visually appealing content.

**Acceptance Scenarios**:

1. **Given** a content editor is creating new content, **When** they access the editor, **Then** they see a rich text editor with formatting toolbar
2. **Given** a content editor wants to add an image, **When** they click the image upload button and select an image file, **Then** the image is uploaded and inserted into the content at the cursor position
3. **Given** a content editor is editing markdown content, **When** they switch to preview mode, **Then** they see a rendered preview of how the content will appear on the front-end
4. **Given** a content editor saves content with images, **When** the content is displayed on the front-end, **Then** all images are properly embedded and displayed within the content

---

### User Story 4 - Navigate Between Frontend and Backend (Priority: P4)

As a user with both visitor and editor roles, I want to easily navigate between the frontend display and backend management so that I can seamlessly switch between viewing content and managing it.

**Why this priority**: Quality-of-life improvement for content managers but not critical for core functionality.

**Independent Test**: Can be fully tested by clicking the "Admin" button on the home page and verifying navigation to the admin panel, then clicking a "View Site" button in admin panel and verifying return to frontend.

**Acceptance Scenarios**:

1. **Given** a user is on the frontend home page, **When** they click the admin/backstage button, **Then** they are navigated to the backend management panel
2. **Given** a user is in the backend admin panel, **When** they click a view site button, **Then** they are navigated back to the frontend home page

---

### Edge Cases

- What happens when a category is deleted that has content assigned to it? System should either prevent deletion, reassign content to parent category, or allow user to choose destination category.
- What happens when an image upload fails due to file size or network error? System should display clear error message and allow retry without losing other content edits.
- What happens when a user creates a circular category reference (Category A is parent of Category B which is parent of Category A)? System must detect and prevent circular references.
- What happens when displaying a category with no content? System should show empty state message suggesting related categories or encouraging user to check back later.
- What happens when image files are very large? System should enforce reasonable size limits and provide compression or resizing before upload.
- What happens when a user navigates to a deeply nested category (5+ levels)? Navigation should remain usable and show breadcrumb trail for context.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST support unlimited levels of category hierarchy through self-referencing category structure
- **FR-002**: Frontend MUST display a multi-level category navigation menu showing hierarchical structure
- **FR-003**: Frontend MUST allow filtering content by selecting any category at any level in the hierarchy
- **FR-004**: Frontend MUST display content from a parent category including all content from its child categories
- **FR-005**: Backend MUST provide category management interface with create, read, update, and delete operations
- **FR-006**: Backend MUST allow setting a parent category when creating or editing a category
- **FR-007**: Backend MUST display categories in a hierarchical tree structure with visual indentation
- **FR-008**: Backend MUST prevent deletion of categories with content without explicit user confirmation and reassignment option
- **FR-009**: Backend MUST detect and prevent circular category references (where a category becomes its own ancestor)
- **FR-010**: Backend content editor MUST include a rich text editor supporting common formatting (bold, italic, headings, lists, links)
- **FR-011**: Rich text editor MUST be compatible with markdown format for backward compatibility with existing content
- **FR-012**: Rich text editor MUST support image upload functionality with drag-and-drop or file selection
- **FR-013**: System MUST store uploaded images and embed them in content for display
- **FR-014**: Frontend MUST render content with embedded images correctly on content detail pages
- **FR-015**: Frontend home page MUST include a visible button or link to access the backend admin panel
- **FR-016**: Backend admin panel MUST include a visible button or link to return to the frontend
- **FR-017**: System MUST validate image file types and enforce size limits for uploads
- **FR-018**: System MUST provide user feedback during image upload (progress indicator and success/error messages)

### Key Entities

- **Category**: Represents a content category with hierarchical relationship. Each category can have one parent category and multiple child categories, forming a tree structure. Key attributes include name, description, parent reference, and display order.
- **Content**: Represents an article or content piece with rich text body. Each content item belongs to one category. Key attributes include title, rich text body (with embedded images), publication status, category reference, creation and update timestamps.
- **Image**: Represents an uploaded image file that can be embedded in content. Key attributes include file reference, upload timestamp, and associated content items.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Users can navigate through at least 5 levels of category hierarchy without performance degradation or confusion
- **SC-002**: Category pages load and display content within 2 seconds for categories containing up to 100 content items
- **SC-003**: Content editors can upload images and see them embedded in content within 10 seconds from initiating upload to seeing the image in the editor
- **SC-004**: 95% of uploaded images display correctly on frontend without broken image links
- **SC-005**: Users can switch between frontend and backend with a single click, with navigation completing in under 1 second
- **SC-006**: Content editors report high satisfaction (4 out of 5 or higher) with the rich text editing experience compared to plain markdown editing
- **SC-007**: Zero instances of circular category references being created in the system
- **SC-008**: 100% of category deletions with associated content require explicit user confirmation before completion

## Assumptions

- Users have modern web browsers with JavaScript enabled (Chrome, Firefox, Safari, Edge - latest 2 versions)
- Image uploads are limited to common web formats (JPEG, PNG, GIF, WebP) with maximum file size of 10MB per image
- Content managers have basic familiarity with content management systems and rich text editors
- The system will handle up to 1000 categories and 10,000 content items without performance issues
- Frontend and backend are deployed as separate applications communicating via HTTP API
- Image storage is handled by the backend with appropriate file system or cloud storage configuration
- Existing single-level categories from MVP will be migrated to the new multi-level structure as top-level categories with no parent
- The home page serves as the primary frontend entry point as specified by user preference
