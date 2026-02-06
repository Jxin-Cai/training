from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from app.config import get_settings
from app.presentation.api.categories import router as categories_router
from app.presentation.api.contents import router as contents_router


def create_application() -> FastAPI:
    """Create FastAPI application."""
    settings = get_settings()

    app = FastAPI(
        title=settings.app_name,
        version=settings.app_version,
        debug=settings.debug,
        docs_url="/docs",
        redoc_url="/redoc",
    )

    app.add_middleware(
        CORSMiddleware,
        allow_origins=settings.allowed_origins,
        allow_credentials=True,
        allow_methods=["*"],
        allow_headers=["*"],
    )

    app.include_router(categories_router, prefix=settings.api_prefix)

    app.include_router(contents_router, prefix=settings.api_prefix)

    # Include routers
    app.include_router(categories_router, prefix=settings.api_prefix)

    app.include_router(contents_router, prefix=settings.api_prefix)

    return app


app = create_application()


@app.get("/")
async def root():
    """Root endpoint."""
    return {"message": "CMS Backend API", "version": app.version}


@app.get("/health")
async def health_check():
    """Health check endpoint."""
    return {"status": "healthy"}
