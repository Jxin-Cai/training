# Content API Contract

## Endpoints

### List Published Contents (Frontend)

**GET** `/api/contents`

**Query Parameters**:
- `page`: number (default: 1)
- `limit`: number (default: 10)

**Response 200**:
```json
{
  "contents": [
    {
      "id": 1,
      "title": "React 入门教程",
      "category_id": 1,
      "category_name": "技术文章",
      "html_content": "<h1>React 入门教程</h1>...",
      "status": "published",
      "published_at": "2026-02-27T10:00:00Z",
      "created_at": "2026-02-26T10:00:00Z"
    }
  ],
  "total": 10,
  "page": 1,
  "limit": 10
}
```

---

### List All Contents (Admin)

**GET** `/api/admin/contents`

**Query Parameters**:
- `page`: number (default: 1)
- `limit`: number (default: 10)
- `status`: string (optional: published/draft)

**Response 200**:
```json
{
  "contents": [
    {
      "id": 1,
      "title": "React 入门教程",
      "category_id": 1,
      "category_name": "技术文章",
      "markdown_content": "# React 入门教程\n\n...",
      "html_content": "<h1>React 入门教程</h1>...",
      "status": "published",
      "published_at": "2026-02-27T10:00:00Z",
      "created_at": "2026-02-26T10:00:00Z"
    }
  ],
  "total": 10,
  "page": 1,
  "limit": 10
}
```

---

### Get Content by ID

**GET** `/api/contents/{id}` (for frontend - returns published only)

**GET** `/api/admin/contents/{id}` (for admin - returns all)

**Response 200**:
```json
{
  "id": 1,
  "title": "React 入门教程",
  "category_id": 1,
  "category_name": "技术文章",
  "markdown_content": "# React 入门教程\n\n...",
  "html_content": "<h1>React 入门教程</h1>...",
  "status": "published",
  "published_at": "2026-02-27T10:00:00Z",
  "created_at": "2026-02-26T10:00:00Z"
}
```

**Response 404**: Content not found or not published

---

### Create Content

**POST** `/api/contents`

**Request Body**:
```json
{
  "title": "React 入门教程",
  "category_id": 1,
  "markdown_content": "# React 入门教程\n\n这是入门教程内容",
  "status": "draft"
}
```

**Response 201**:
```json
{
  "id": 1,
  "title": "React 入门教程",
  "category_id": 1,
  "markdown_content": "# React 入门教程\n\n这是入门教程内容",
  "html_content": "<h1>React 入门教程</h1><p>这是入门教程内容</p>",
  "status": "draft",
  "published_at": null,
  "created_at": "2026-02-27T10:00:00Z"
}
```

**Response 400**: Validation error

---

### Update Content

**PUT** `/api/contents/{id}`

**Request Body**:
```json
{
  "title": "React 进阶教程",
  "markdown_content": "# React 进阶教程\n\n这是进阶内容",
  "status": "published"
}
```

**Response 200**:
```json
{
  "id": 1,
  "title": "React 进阶教程",
  "category_id": 1,
  "markdown_content": "# React 进阶教程\n\n这是进阶内容",
  "html_content": "<h1>React 进阶教程</h1><p>这是进阶内容</p>",
  "status": "published",
  "published_at": "2026-02-27T12:00:00Z",
  "created_at": "2026-02-27T10:00:00Z"
}
```

---

### Delete Content

**DELETE** `/api/contents/{id}`

**Response 204**: Successfully deleted

---

## File Upload

### Upload Markdown File

**POST** `/api/contents/upload`

**Request**: multipart/form-data
- file: .md file

**Response 200**:
```json
{
  "content": "# 上传的文件内容\n\n..."
}
```