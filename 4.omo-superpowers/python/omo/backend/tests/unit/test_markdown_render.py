"""Tests for markdown rendering and XSS sanitization."""

import pytest
from app.services.markdown_render import render_markdown


class TestMarkdownRendering:
    """Test markdown to HTML conversion."""

    def test_basic_markdown(self):
        """Test basic markdown rendering."""
        md = "# Hello World\n\nThis is a paragraph."
        html = render_markdown(md)

        assert "<h1>Hello World</h1>" in html
        assert "<p>This is a paragraph.</p>" in html

    def test_code_block(self):
        """Test code block rendering."""
        md = "```python\nprint('hello')\n```"
        html = render_markdown(md)

        assert "<code" in html
        # Code content is rendered, possibly with HTML entities
        assert "print" in html

    def test_table(self):
        """Test table rendering."""
        md = "| Column 1 | Column 2 |\n|----------|----------|\n| Cell 1   | Cell 2   |"
        html = render_markdown(md)

        assert "<table>" in html
        assert "<td>Cell 1</td>" in html


class TestXSSSanitization:
    """Test XSS attack prevention."""

    def test_script_tag_removed(self):
        """Test that script tags are removed."""
        md = "# Hello\n<script>alert('xss')</script>"
        html = render_markdown(md)

        # Script tag itself must be removed
        assert "<script>" not in html
        # The script content is not executable as it's plain text now
        # This is the expected behavior - tag removed, text remains

    def test_onerror_attribute_removed(self):
        """Test that onerror attributes in HTML tags are removed."""
        # Direct HTML with onerror attribute
        md = "<img src=x onerror=alert(1)>"
        html = render_markdown(md)

        # onerror attribute should be stripped
        assert "onerror" not in html.lower()

    def test_javascript_protocol_removed(self):
        """Test that javascript: protocol is removed."""
        md = "[click](javascript:alert(1))"
        html = render_markdown(md)

        assert "javascript:" not in html

    def test_inline_event_handlers_removed(self):
        """Test that inline event handlers are removed."""
        md = "<p onclick='alert(1)'>Click me</p>"
        html = render_markdown(md)

        assert "onclick" not in html

    def test_iframe_removed(self):
        """Test that iframe elements are removed."""
        md = "<iframe src='http://evil.com'></iframe>"
        html = render_markdown(md)

        assert "<iframe" not in html

    def test_form_removed(self):
        """Test that form elements are removed."""
        md = "<form action='http://evil.com'><input type='text'></form>"
        html = render_markdown(md)

        assert "<form" not in html

    def test_safe_html_preserved(self):
        """Test that safe HTML is preserved."""
        md = "**bold** and *italic* and `code`"
        html = render_markdown(md)

        assert "<strong>bold</strong>" in html
        assert "<em>italic</em>" in html
        assert "<code>code</code>" in html

    def test_links_get_nofollow(self):
        """Test that links get rel="nofollow"."""
        md = "[link](http://example.com)"
        html = render_markdown(md)

        assert 'rel="nofollow"' in html
