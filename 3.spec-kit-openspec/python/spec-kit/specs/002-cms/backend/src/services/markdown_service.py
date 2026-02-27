import markdown


def render_markdown(md_content: str) -> str:
    return markdown.markdown(
        md_content, extensions=["extra", "codehilite", "fenced_code", "tables"]
    )
