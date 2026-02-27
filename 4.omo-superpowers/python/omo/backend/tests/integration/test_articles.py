"""Integration tests for CMS API."""

import pytest
from fastapi.testclient import TestClient
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker
from sqlalchemy.pool import StaticPool

from app.main import app, create_access_token
from app.database import Base, get_db
from app.crud import create_category, create_article
from app.services.markdown_render import render_markdown


# Create in-memory SQLite database for testing
SQLALCHEMY_DATABASE_URL = "sqlite:///:memory:"

engine = create_engine(
    SQLALCHEMY_DATABASE_URL,
    connect_args={"check_same_thread": False},
    poolclass=StaticPool,
)
TestingSessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)


def override_get_db():
    try:
        db = TestingSessionLocal()
        yield db
    finally:
        db.close()


app.dependency_overrides[get_db] = override_get_db


@pytest.fixture(autouse=True)
def setup_database():
    """Create tables before each test and drop after."""
    Base.metadata.create_all(bind=engine)
    yield
    Base.metadata.drop_all(bind=engine)


@pytest.fixture
def client():
    """Create test client."""
    return TestClient(app)


@pytest.fixture
def auth_token():
    """Create authentication token for testing."""
    return create_access_token({"sub": "admin"})


@pytest.fixture
def auth_headers(auth_token):
    """Create authentication headers."""
    return {"Authorization": f"Bearer {auth_token}"}


@pytest.fixture
def test_category(client, auth_headers):
    """Create a test category."""
    response = client.post(
        "/api/categories", json={"name": "Test Category"}, headers=auth_headers
    )
    return response.json()


class TestAuthentication:
    """Test authentication and authorization."""

    def test_login_success(self, client):
        """Test successful login."""
        # First create user via startup event
        from app.crud import create_user, get_user_by_username
        from app.database import SessionLocal

        db = SessionLocal()
        create_user(db, "admin", "admin123")
        db.close()

        response = client.post(
            "/api/auth/login", json={"username": "admin", "password": "admin123"}
        )

        assert response.status_code == 200
        assert "access_token" in response.json()

    def test_login_invalid_credentials(self, client):
        """Test login with invalid credentials."""
        response = client.post(
            "/api/auth/login", json={"username": "admin", "password": "wrong"}
        )

        assert response.status_code == 401

    def test_admin_endpoint_requires_auth(self, client, test_category):
        """Test that admin endpoints require authentication."""
        response = client.get("/api/articles")
        assert response.status_code == 401

    def test_admin_endpoint_with_auth(self, client, auth_headers):
        """Test that admin endpoints work with authentication."""
        response = client.get("/api/articles", headers=auth_headers)
        assert response.status_code == 200


class TestPublicEndpoints:
    """Test public (no auth required) endpoints."""

    def test_get_categories_no_auth(self, client):
        """Test that categories endpoint works without auth."""
        response = client.get("/api/categories")
        assert response.status_code == 200

    def test_get_published_articles_empty(self, client):
        """Test getting published articles when none exist."""
        response = client.get("/api/articles/published")
        assert response.status_code == 200
        assert response.json() == []

    def test_get_published_article_not_found(self, client):
        """Test getting non-existent published article."""
        response = client.get("/api/articles/published/999")
        assert response.status_code == 404


class TestArticlePublishing:
    """Test article publishing and visibility."""

    def test_draft_not_in_published_list(self, client, auth_headers, test_category):
        """Test that draft articles are not in published list."""
        # Create draft article
        client.post(
            "/api/articles",
            json={
                "title": "Draft Article",
                "content_md": "This is a draft",
                "category_id": test_category["id"],
                "status": "draft",
            },
            headers=auth_headers,
        )

        # Check published list
        response = client.get("/api/articles/published")
        assert response.status_code == 200
        assert len(response.json()) == 0

    def test_published_in_published_list(self, client, auth_headers, test_category):
        """Test that published articles appear in published list."""
        # Create published article
        client.post(
            "/api/articles",
            json={
                "title": "Published Article",
                "content_md": "This is published",
                "category_id": test_category["id"],
                "status": "published",
            },
            headers=auth_headers,
        )

        # Check published list
        response = client.get("/api/articles/published")
        assert response.status_code == 200
        assert len(response.json()) == 1
        assert response.json()[0]["title"] == "Published Article"

    def test_draft_not_accessible_via_public_endpoint(
        self, client, auth_headers, test_category
    ):
        """Test that draft cannot be accessed via public endpoint."""
        # Create draft article
        create_resp = client.post(
            "/api/articles",
            json={
                "title": "Draft Only",
                "content_md": "Secret content",
                "category_id": test_category["id"],
                "status": "draft",
            },
            headers=auth_headers,
        )
        article_id = create_resp.json()["id"]

        # Try to access via public endpoint
        response = client.get(f"/api/articles/published/{article_id}")
        assert response.status_code == 404

    def test_published_accessible_via_public_endpoint(
        self, client, auth_headers, test_category
    ):
        """Test that published article can be accessed via public endpoint."""
        # Create published article
        create_resp = client.post(
            "/api/articles",
            json={
                "title": "Public Article",
                "content_md": "Public content",
                "category_id": test_category["id"],
                "status": "published",
            },
            headers=auth_headers,
        )
        article_id = create_resp.json()["id"]

        # Access via public endpoint
        response = client.get(f"/api/articles/published/{article_id}")
        assert response.status_code == 200
        assert response.json()["title"] == "Public Article"


class TestPublishedAtSorting:
    """Test published_at sorting."""

    def test_published_articles_sorted_by_published_at(
        self, client, auth_headers, test_category
    ):
        """Test that published articles are sorted by published_at descending."""
        # Create first article (published first)
        client.post(
            "/api/articles",
            json={
                "title": "First Article",
                "content_md": "First content",
                "category_id": test_category["id"],
                "status": "published",
            },
            headers=auth_headers,
        )

        # Create second article (published second)
        client.post(
            "/api/articles",
            json={
                "title": "Second Article",
                "content_md": "Second content",
                "category_id": test_category["id"],
                "status": "published",
            },
            headers=auth_headers,
        )

        # Check order - second should be first (newest first)
        response = client.get("/api/articles/published")
        articles = response.json()

        assert len(articles) == 2
        # The second article should have a later published_at
        # So it should appear first in the list
        assert articles[0]["title"] == "Second Article"
