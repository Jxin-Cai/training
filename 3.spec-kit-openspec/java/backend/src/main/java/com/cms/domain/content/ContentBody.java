package com.cms.domain.content;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContentBody {
    private static final PolicyFactory POLICY = new HtmlPolicyBuilder()
        .allowElements("h1", "h2", "h3", "h4", "h5", "h6",
                      "p", "br", "hr",
                      "ul", "ol", "li",
                      "strong", "b", "em", "i", "u",
                      "a", "img",
                      "blockquote", "pre", "code")
        .allowAttributes("href").onElements("a")
        .allowAttributes("src", "alt", "width", "height").onElements("img")
        .allowStandardUrlProtocols()
        .toFactory();

    private static final Pattern IMAGE_ID_PATTERN = Pattern.compile("/uploads/([a-f0-9\\-]+\\.[a-z]+)");

    private final String html;
    private final String plainText;
    private final Set<String> imageFilenames;

    private ContentBody(String html, String plainText, Set<String> imageFilenames) {
        this.html = html;
        this.plainText = plainText;
        this.imageFilenames = imageFilenames;
    }

    public static ContentBody fromHtml(String html) {
        String sanitized = sanitizeHtml(html);
        String plainText = extractPlainText(sanitized);
        Set<String> imageFilenames = extractImageFilenames(sanitized);
        return new ContentBody(sanitized, plainText, imageFilenames);
    }

    private static String sanitizeHtml(String html) {
        return POLICY.sanitize(html);
    }

    private static String extractPlainText(String html) {
        return html.replaceAll("<[^>]*>", "").replaceAll("\\s+", " ").trim();
    }

    private static Set<String> extractImageFilenames(String html) {
        Set<String> filenames = new HashSet<>();
        Matcher matcher = IMAGE_ID_PATTERN.matcher(html);
        while (matcher.find()) {
            filenames.add(matcher.group(1));
        }
        return filenames;
    }

    public String getSummary(int maxLength) {
        if (plainText.length() <= maxLength) {
            return plainText;
        }
        return plainText.substring(0, maxLength) + "...";
    }

    // Getters
    public String getHtml() { return html; }
    public String getPlainText() { return plainText; }
    public Set<String> getImageFilenames() { return imageFilenames; }
}
