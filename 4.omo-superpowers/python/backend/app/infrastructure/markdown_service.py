import markdown
from typing import Optional


class MarkdownService:
    """Service for converting markdown to HTML."""

    def __init__(self):
        self.md = markdown.Markdown(extensions=["codehilite", "fenced_code", "tables"])

    def convert_to_html(self, markdown_text: str) -> str:
        """Convert markdown text to HTML."""
        if not markdown_text:
            return ""

        # Reset the markdown instance to avoid state issues
        self.md.reset()
        return self.md.convert(markdown_text)

    def convert_with_bleach(self, markdown_text: str) -> str:
        """Convert markdown to HTML with basic sanitization."""
        import bleach

        html = self.convert_to_html(markdown_text)
        # Basic sanitization - allow common tags
        allowed_tags = [
            "p",
            "br",
            "strong",
            "em",
            "u",
            "ol",
            "ul",
            "li",
            "h1",
            "h2",
            "h3",
            "h4",
            "h5",
            "h6",
            "blockquote",
            "code",
            "pre",
            "a",
            "img",
            "table",
            "thead",
            "tbody",
            "tr",
            "th",
            "td",
        ]
        allowed_attributes = {
            "a": ["href", "title"],
            "img": ["src", "alt", "title"],
            "*": ["class"],  # Allow class on all elements for code highlighting
        }

        return bleach.clean(html, tags=allowed_tags, attributes=allowed_attributes)
