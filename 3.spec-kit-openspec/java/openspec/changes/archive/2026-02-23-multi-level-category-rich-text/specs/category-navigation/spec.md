## ADDED Requirements

### Requirement: Category tree with content counts
The category navigation SHALL display the number of published content items in each category.

#### Scenario: Viewing category tree on home page
- **WHEN** user views the home page
- **THEN** each category in the tree shows its content count in parentheses
- **AND** content count includes only PUBLISHED status content

#### Scenario: Category with no content
- **WHEN** a category has no published content
- **THEN** the count shows as (0)

### Requirement: Expandable multi-level tree navigation
The category navigation SHALL support expanding and collapsing categories to reveal subcategories.

#### Scenario: Expanding a category
- **WHEN** user clicks on a category that has children
- **THEN** the tree expands to show all child categories
- **AND** grandchildren are hidden until their parent is expanded

#### Scenario: Collapsing a category
- **WHEN** user clicks on an expanded category
- **THEN** all descendants are hidden

### Requirement: Subcategory display on category page
The category detail page SHALL display subcategories as a navigable list.

#### Scenario: Viewing category with subcategories
- **WHEN** user navigates to a category that has child categories
- **THEN** the page displays a list of subcategories with their content counts
- **AND** clicking a subcategory navigates to that category's page

#### Scenario: Viewing leaf category
- **WHEN** user navigates to a category with no children
- **THEN** no subcategory section is displayed

### Requirement: Hierarchical content filtering
When viewing a category page, the system SHALL display content from that category only (not subcategories).

#### Scenario: Viewing parent category content
- **WHEN** user views a parent category
- **THEN** only content directly in that category is shown
- **AND** content from child categories is NOT included

#### Scenario: User wants to view all content in tree
- **WHEN** user wants to see content from all subcategories
- **THEN** user must navigate to each subcategory individually
