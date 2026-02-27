# Category API Contract

## Endpoints

### List Categories

**GET** `/api/categories`

**Response 200**:
```json
{
  "categories": [
    {
      "id": 1,
      "name": "技术文章",
      "created_at": "2026-02-27T10:00:00Z",
      "updated_at": "2026-02-27T10:00:00Z"
    }
  ]
}
```

---

### Get Category

**GET** `/api/categories/{id}`

**Response 200**:
```json
{
  "id": 1,
  "name": "技术文章",
  "created_at": "2026-02-27T10:00:00Z",
  "updated_at": "2026-02-27T10:00:00Z"
}
```

**Response 404**: Category not found

---

### Create Category

**POST** `/api/categories`

**Request Body**:
```json
{
  "name": "技术文章"
}
```

**Response 201**:
```json
{
  "id": 1,
  "name": "技术文章",
  "created_at": "2026-02-27T10:00:00Z",
  "updated_at": "2026-02-27T10:00:00Z"
}
```

**Response 400**: Validation error (name required or duplicate)

---

### Update Category

**PUT** `/api/categories/{id}`

**Request Body**:
```json
{
  "name": "技术教程"
}
```

**Response 200**:
```json
{
  "id": 1,
  "name": "技术教程",
  "created_at": "2026-02-27T10:00:00Z",
  "updated_at": "2026-02-27T11:00:00Z"
}
```

**Response 404**: Category not found

---

### Delete Category

**DELETE** `/api/categories/{id}`

**Response 204**: Successfully deleted

**Response 400**: Cannot delete - category has associated contents