package com.cms.infrastructure.service;

import com.cms.domain.service.MarkdownRenderer;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class FlexmarkMarkdownRenderer implements MarkdownRenderer {
    
    private final Parser parser;
    private final HtmlRenderer htmlRenderer;
    private static final Pattern SCRIPT_PATTERN = Pattern.compile("<script[^>]*>.*?</script>", 
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    private static final Pattern EVENT_HANDLER_PATTERN = Pattern.compile("\\s+on\\w+\\s*=\\s*[\"'][^\"']*[\"']", 
            Pattern.CASE_INSENSITIVE);
    
    public FlexmarkMarkdownRenderer() {
        MutableDataSet options = new MutableDataSet();
        this.parser = Parser.builder(options).build();
        this.htmlRenderer = HtmlRenderer.builder(options).build();
    }
    
    @Override
    public String renderToHtml(String markdown) {
        if (markdown == null || markdown.isEmpty()) {
            return "";
        }
        Node document = parser.parse(markdown);
        String html = htmlRenderer.render(document);
        return sanitizeHtml(html);
    }
    
    @Override
    public String sanitizeHtml(String html) {
        if (html == null) {
            return "";
        }
        String sanitized = SCRIPT_PATTERN.matcher(html).replaceAll("");
        sanitized = EVENT_HANDLER_PATTERN.matcher(sanitized).replaceAll("");
        return sanitized;
    }
}
