# Quickstart: Multi-Level Categories & Rich Text Enhancement

**Feature Branch**: `001-multi-level-categories`
**Last Updated**: 2026-02-13

## Prerequisites

- **Backend**: Java 17+, Maven 3.6+
- **Frontend**: Node.js 18+, npm 9+
- **IDE**: IntelliJ IDEA or VS Code with Java and Vue extensions

## Quick Setup

### 1. Install Backend Dependencies

Add to `backend/pom.xml`:

```xml
<!-- Image Processing -->
<dependency>
    <groupId>net.coobird</groupId>
    <artifactId>thumbnailator</artifactId>
    <version>0.4.20</version>
</dependency>

<!-- File Type Detection (Security) -->
<dependency>
    <groupId>org.apache.tika</groupId>
    <artifactId>tika-core</artifactId>
    <version>2.9.1</version>
</dependency>

<!-- HTML Sanitization -->
<dependency>
    <groupId>com.googlecode.owasp-java-html-sanitizer</groupId>
    <artifactId>owasp-java-html-sanitizer</artifactId>
    <version>20220608.1</version>
</dependency>
```

### 2. Install Frontend Dependencies

```bash
cd frontend

# TipTap Editor
npm install @tiptap/vue-3 @tiptap/starter-kit @tiptap/extension-image @tiptap/extension-placeholder

# Image Compression
npm install browser-image-compression

# Element Plus (for tree component)
npm install element-plus
```

### 3. Create Upload Directory

```bash
mkdir -p backend/src/main/resources/uploads
```

## Development Workflow

### Start Backend

```bash
cd backend
mvn spring-boot:run
```

Backend runs at: http://localhost:8080

### Start Frontend

```bash
cd frontend
npm run dev
```

Frontend runs at: http://localhost:5173 (or configured port)

## Key Implementation Points

### Backend: Category Entity

```java
// backend/src/main/java/com/cms/domain/category/Category.java

@Entity
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Category> children = new ArrayList<>();

    private String path; // Materialized path: "/1/4/7/"
    private Integer displayOrder = 0;

    // Domain method: Circular reference check
    public boolean wouldCreateCircularReference(Category newParent) {
        if (newParent == null) return false;
        return newParent.isDescendantOf(this) || newParent.equals(this);
    }

    private boolean isDescendantOf(Category potentialAncestor) {
        Category current = this.parent;
        while (current != null) {
            if (current.equals(potentialAncestor)) return true;
            current = current.getParent();
        }
        return false;
    }
}
```

### Backend: Image Upload Service

```java
// backend/src/main/java/com/cms/application/ImageService.java

@Service
public class ImageService {
    private final Tika tika = new Tika();
    private static final Set<String> ALLOWED_TYPES = Set.of(
        "image/jpeg", "image/png", "image/gif", "image/webp"
    );
    private static final long MAX_SIZE = 10 * 1024 * 1024; // 10MB

    public Image uploadImage(MultipartFile file) throws IOException {
        // Validate by magic bytes (not extension!)
        String detectedType = tika.detect(file.getInputStream());
        if (!ALLOWED_TYPES.contains(detectedType)) {
            throw new IllegalArgumentException("Invalid file type: " + detectedType);
        }

        if (file.getSize() > MAX_SIZE) {
            throw new IllegalArgumentException("File size exceeds 10MB limit");
        }

        // Generate safe filename (UUID)
        String extension = getExtension(detectedType);
        String filename = UUID.randomUUID() + extension;

        // Re-encode for security (strips embedded malware)
        BufferedImage image = ImageIO.read(file.getInputStream());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, getFormatName(detectedType), baos);

        // Save to disk
        Path destination = Paths.get("uploads", filename);
        Files.write(destination, baos.toByteArray());

        // Create and return Image entity
        Image uploadedImage = new Image();
        uploadedImage.setFilename(filename);
        uploadedImage.setOriginalFilename(file.getOriginalFilename());
        uploadedImage.setMimeType(detectedType);
        uploadedImage.setFileSize(file.getSize());
        uploadedImage.setWidth(image.getWidth());
        uploadedImage.setHeight(image.getHeight());

        return imageRepository.save(uploadedImage);
    }
}
```

### Frontend: TipTap Editor Setup

```vue
<!-- frontend/src/components/content/RichTextEditor.vue -->

<template>
  <div class="editor-container">
    <editor-content :editor="editor" class="editor-content" />

    <div class="editor-toolbar">
      <button @click="editor.chain().focus().toggleBold().run()">Bold</button>
      <button @click="editor.chain().focus().toggleItalic().run()">Italic</button>
      <button @click="triggerImageUpload">Upload Image</button>
      <input
        ref="fileInput"
        type="file"
        accept="image/jpeg,image/png,image/gif,image/webp"
        @change="handleImageUpload"
        style="display: none"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue';
import { useEditor, EditorContent } from '@tiptap/vue-3';
import StarterKit from '@tiptap/starter-kit';
import Image from '@tiptap/extension-image';
import Placeholder from '@tiptap/extension-placeholder';
import imageCompression from 'browser-image-compression';
import axios from 'axios';

const props = defineProps({
  modelValue: String
});

const emit = defineEmits(['update:modelValue']);

const fileInput = ref(null);

const editor = useEditor({
  content: props.modelValue,
  extensions: [
    StarterKit,
    Image.configure({
      inline: true,
      allowBase64: false
    }),
    Placeholder.configure({
      placeholder: 'Start writing...'
    })
  ],
  onUpdate: ({ editor }) => {
    emit('update:modelValue', editor.getHTML());
  }
});

function triggerImageUpload() {
  fileInput.value.click();
}

async function handleImageUpload(event) {
  const file = event.target.files[0];
  if (!file) return;

  try {
    // Client-side compression (target 2MB)
    const options = {
      maxSizeMB: 2,
      maxWidthOrHeight: 1920,
      useWebWorker: true
    };
    const compressedFile = await imageCompression(file, options);

    // Upload to server
    const formData = new FormData();
    formData.append('file', compressedFile);

    const response = await axios.post('/api/images/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    });

    // Insert image into editor
    editor.value.chain().focus()
      .setImage({ src: response.data.url })
      .run();
  } catch (error) {
    console.error('Image upload failed:', error);
    alert('Image upload failed: ' + error.message);
  }

  // Reset file input
  event.target.value = '';
}

onBeforeUnmount(() => {
  editor.value.destroy();
});
</script>
```

### Frontend: Category Tree Component

```vue
<!-- frontend/src/components/category/CategoryTree.vue -->

<template>
  <el-tree
    :data="treeData"
    :props="treeProps"
    :load="loadNode"
    lazy
    node-key="id"
    :default-expanded-keys="expandedKeys"
    @node-click="handleCategorySelect">
    <template #default="{ data }">
      <span class="category-node">
        {{ data.name }}
        <span class="content-count">({{ data.contentCount }})</span>
      </span>
    </template>
  </el-tree>
</template>

<script setup>
import { ref } from 'vue';
import axios from 'axios';

const emit = defineEmits(['category-selected']);

const treeData = ref([]);
const expandedKeys = ref([]);

const treeProps = {
  label: 'name',
  children: 'children',
  isLeaf: (data) => !data.hasChildren
};

async function loadNode(node, resolve) {
  try {
    const parentId = node.level === 0 ? null : node.data.id;
    const url = parentId
      ? `/api/categories/${parentId}/children`
      : '/api/categories?parentIsNull=true';

    const response = await axios.get(url);
    resolve(response.data);
  } catch (error) {
    console.error('Failed to load categories:', error);
    resolve([]);
  }
}

function handleCategorySelect(data) {
  emit('category-selected', data);
}
</script>
```

### Frontend: Navigation Between Frontend and Backend

```vue
<!-- frontend/src/pages/Home.vue -->

<template>
  <div class="home-page">
    <header>
      <h1>CMS Home</h1>
      <router-link to="/admin" class="admin-button">
        Go to Admin Panel
      </router-link>
    </header>

    <CategoryTree @category-selected="handleCategorySelect" />
    <ContentList :category-id="selectedCategoryId" />
  </div>
</template>

<!-- frontend/src/pages/admin/AdminLayout.vue -->

<template>
  <div class="admin-layout">
    <header>
      <h1>Admin Panel</h1>
      <router-link to="/" class="home-button">
        View Site
      </router-link>
    </header>

    <router-view />
  </div>
</template>
```

## Testing Quick Commands

### Backend Tests

```bash
cd backend

# Run all tests
mvn test

# Run specific test
mvn test -Dtest=CategoryServiceTest

# Run with coverage
mvn jacoco:report
```

### Frontend Tests

```bash
cd frontend

# Run all tests
npm run test

# Run with coverage
npm run test:coverage

# Run specific test file
npm run test CategoryTree.spec.js
```

## API Quick Reference

### Categories

```bash
# Get all categories
GET /api/categories

# Get category tree
GET /api/categories/tree

# Create category
POST /api/categories
{
  "name": "Programming",
  "parentId": 1,
  "description": "Programming tutorials"
}

# Update category
PUT /api/categories/2
{
  "name": "Web Development",
  "parentId": 1
}

# Delete category
DELETE /api/categories/2?handleChildren=REASSIGN_TO_PARENT

# Get category content (with descendants)
GET /api/categories/1/content?includeDescendants=true&status=PUBLISHED
```

### Content

```bash
# Create content
POST /api/content
{
  "title": "Getting Started with Vue.js",
  "body": "<h1>Hello Vue</h1><img src=\"/uploads/abc123.jpg\" />",
  "categoryId": 2,
  "status": "DRAFT"
}

# Publish content
POST /api/content/1/publish

# Unpublish content
POST /api/content/1/unpublish
```

### Images

```bash
# Upload image
POST /api/images/upload
Content-Type: multipart/form-data
file: <binary>

# Response:
{
  "id": 123,
  "url": "/uploads/550e8400-e29b-41d4-a716-446655440000.jpg",
  "filename": "550e8400-e29b-41d4-a716-446655440000.jpg",
  "originalFilename": "my-image.png",
  "mimeType": "image/png",
  "fileSize": 245760,
  "width": 1920,
  "height": 1080
}
```

## Common Issues & Solutions

### Issue: Circular Reference Error

**Problem**: Attempting to set a category's parent to one of its descendants.

**Solution**: The backend validates this automatically. Frontend should catch 400 error:

```javascript
try {
  await axios.put(`/api/categories/${id}`, { parentId: newParentId });
} catch (error) {
  if (error.response?.status === 400) {
    alert('Cannot set parent: would create circular reference');
  }
}
```

### Issue: Image Upload Fails

**Problem**: Image upload returns 400 or 413 error.

**Checklist**:
1. File size under 10MB? (Client should compress to 2MB)
2. File type is JPEG, PNG, GIF, or WebP?
3. File is actually an image (not renamed executable)?

**Solution**:
```javascript
// Add validation before upload
if (file.size > 10 * 1024 * 1024) {
  alert('File too large. Maximum size is 10MB');
  return;
}

if (!['image/jpeg', 'image/png', 'image/gif', 'image/webp'].includes(file.type)) {
  alert('Invalid file type. Supported: JPEG, PNG, GIF, WebP');
  return;
}
```

### Issue: Category Tree Not Loading

**Problem**: Tree component shows empty or loading forever.

**Debug**:
```javascript
// Check API response
const response = await axios.get('/api/categories/tree');
console.log('Tree data:', response.data);

// Check backend logs
// Ensure repository methods return correct data structure
```

### Issue: Images Not Displaying in Content

**Problem**: Content shows broken image icons.

**Checklist**:
1. Image URL in HTML matches `/uploads/{filename}` format?
2. Backend serving static files from uploads directory?
3. File exists in `backend/src/main/resources/uploads/`?

**Solution** (Spring Boot config):

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
            .addResourceLocations("classpath:/uploads/");
    }
}
```

## Performance Tips

1. **Category Tree**: Use lazy loading (only load children when parent expanded)
2. **Image Upload**: Always compress on client before upload
3. **Content List**: Paginate results (> 50 items per page)
4. **Category Queries**: Use path prefix matching for descendant queries
5. **Caching**: Cache category tree on frontend until changes detected

## Next Steps

1. Run `npm run dev` and `mvn spring-boot:run`
2. Test category CRUD at `/admin/categories`
3. Test content creation with image upload at `/admin/content/new`
4. Verify frontend navigation between home and admin
5. Test circular reference prevention
6. Test image upload with various file sizes and types

## Useful Commands

```bash
# Clean and rebuild
cd backend && mvn clean install
cd frontend && npm run build

# Check for dependency updates
cd backend && mvn versions:display-dependency-updates
cd frontend && npm outdated

# Format code
cd backend && mvn spotless:apply
cd frontend && npm run format

# Run linter
cd frontend && npm run lint
```
