package com.cms.domain.service;

public interface MarkdownRenderer {
    String renderToHtml(String markdown);
    String sanitizeHtml(String html);
}
