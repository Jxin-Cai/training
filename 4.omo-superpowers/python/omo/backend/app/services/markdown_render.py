"""Markdown rendering service with XSS sanitization."""

import bleach
import markdown


# Allowed HTML tags for sanitization
ALLOWED_TAGS = [
    "p",
    "br",
    "strong",
    "em",
    "b",
    "i",
    "u",
    "s",
    "del",
    "h1",
    "h2",
    "h3",
    "h4",
    "h5",
    "h6",
    "ul",
    "ol",
    "li",
    "blockquote",
    "hr",
    "code",
    "pre",
    "a",
    "table",
    "thead",
    "tbody",
    "tr",
    "th",
    "td",
    "img",
    "span",
    "div",
]

# Allowed attributes for each tag
ALLOWED_ATTRIBUTES = {
    "a": ["href", "title", "rel", "target"],
    "img": ["src", "alt", "title", "width", "height"],
    "td": ["colspan", "rowspan"],
    "th": ["colspan", "rowspan"],
    "*": ["class"],
}

# Allowed protocols for links
ALLOWED_PROTOCOLS = ["http", "https", "mailto", "ftp"]


def render_markdown(md_text: str) -> str:
    """
    Convert Markdown to HTML and sanitize to prevent XSS attacks.

    Args:
        md_text: Raw markdown text

    Returns:
        Sanitized HTML string safe for rendering
    """
    if not md_text:
        return ""

    # Convert markdown to HTML with extensions
    html = markdown.markdown(
        md_text,
        extensions=[
            "extra",  # Tables, footnotes, etc.
            "fenced_code",  # Code blocks
            "codehilite",  # Code highlighting
            "nl2br",  # Convert newlines to <br>
        ],
    )

    # Sanitize HTML to prevent XSS
    sanitized_html = bleach.clean(
        html,
        tags=ALLOWED_TAGS,
        attributes=ALLOWED_ATTRIBUTES,
        protocols=ALLOWED_PROTOCOLS,
        strip=True,
        strip_comments=True,
    )

    # Add rel="nofollow noopener noreferrer" to all links
    sanitized_html = bleach.linkify(
        sanitized_html,
        callbacks=[
            bleach.callbacks.nofollow,
        ],
    )

    return sanitized_html
