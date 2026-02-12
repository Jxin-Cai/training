package com.cms.domain.service;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.stereotype.Service;

/**
 * Markdown渲染领域服务
 * 使用commonmark-java库将Markdown转换为HTML
 * 
 * @author jxin
 * @date 2026-02-11
 */
@Service
public class MarkdownRenderService {
    
    private final Parser parser;
    private final HtmlRenderer renderer;
    
    public MarkdownRenderService() {
        this.parser = Parser.builder().build();
        this.renderer = HtmlRenderer.builder().build();
    }
    
    /**
     * 将Markdown文本渲染为HTML
     * 
     * @param markdown Markdown原始文本
     * @return 渲染后的HTML字符串
     */
    public String renderToHtml(String markdown) {
        if (markdown == null || markdown.trim().isEmpty()) {
            return "";
        }
        
        try {
            Node document = parser.parse(markdown);
            return renderer.render(document);
        } catch (Exception e) {
            throw new RuntimeException("Markdown渲染失败: " + e.getMessage(), e);
        }
    }
}
