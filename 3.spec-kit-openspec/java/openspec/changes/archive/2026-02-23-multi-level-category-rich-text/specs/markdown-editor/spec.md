## ADDED Requirements

### Requirement: Markdown editing mode
The content editor SHALL support Markdown input with real-time preview conversion to HTML.

#### Scenario: User switches to Markdown mode
- **WHEN** user toggles the "Markdown" mode switch in the editor
- **THEN** the editor displays a Markdown textarea with the content converted from HTML

#### Scenario: User edits content in Markdown mode
- **WHEN** user types Markdown syntax (e.g., `**bold**`, `# Heading`, `[link](url)`)
- **THEN** the preview panel shows the rendered HTML output in real-time

### Requirement: Markdown to HTML conversion
The system SHALL convert Markdown content to valid HTML when saving.

#### Scenario: Saving Markdown content
- **WHEN** user saves content that was authored in Markdown mode
- **THEN** the content is stored as HTML in the database
- **AND** the Markdown source is not preserved (conversion is one-way for storage)

### Requirement: HTML to Markdown conversion
The system SHALL convert existing HTML content to Markdown when switching to MD mode.

#### Scenario: Opening existing content in Markdown mode
- **WHEN** user opens existing HTML content and switches to Markdown mode
- **THEN** the editor displays Markdown equivalent of the HTML
- **AND** supported elements (headings, lists, links, images, code blocks) are converted correctly

### Requirement: Markdown syntax support
The Markdown editor SHALL support the following syntax elements:
- Headings (H1-H6)
- Bold, italic, strikethrough
- Ordered and unordered lists
- Links and images
- Code blocks with language specification
- Blockquotes
- Tables

#### Scenario: User enters code block with language
- **WHEN** user enters ` ```java` followed by code and ` ``` `
- **THEN** the preview shows syntax-highlighted code block

#### Scenario: User creates a table
- **WHEN** user enters Markdown table syntax
- **THEN** the preview shows a formatted HTML table

### Requirement: Editor toolbar enhancement
The editor toolbar SHALL provide buttons for Markdown syntax insertion.

#### Scenario: User clicks code block button
- **WHEN** user clicks the code block button in toolbar
- **THEN** a code block template is inserted at cursor position

#### Scenario: User clicks link button
- **WHEN** user clicks the link button
- **THEN** a link template `[text](url)` is inserted at cursor position
