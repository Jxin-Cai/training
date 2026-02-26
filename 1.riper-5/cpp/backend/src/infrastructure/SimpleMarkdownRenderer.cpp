#include "SimpleMarkdownRenderer.hpp"
#include <sstream>
#include <string>
#include <algorithm>

namespace cms::infrastructure {

namespace {

std::string escapeHtml(const std::string& s) {
  std::ostringstream out;
  for (unsigned char c : s) {
    switch (c) {
      case '&': out << "&amp;"; break;
      case '<': out << "&lt;"; break;
      case '>': out << "&gt;"; break;
      case '"': out << "&quot;"; break;
      case '\'': out << "&#39;"; break;
      default: out << c; break;
    }
  }
  return out.str();
}

std::string trim(const std::string& s) {
  auto it1 = std::find_if_not(s.begin(), s.end(), [](unsigned char ch) { return std::isspace(ch); });
  if (it1 == s.end()) return "";
  auto it2 = std::find_if_not(s.rbegin(), s.rend(), [](unsigned char ch) { return std::isspace(ch); }).base();
  return std::string(it1, it2);
}

std::string applyInline(const std::string& text) {
  // Very small subset: **strong** only.
  std::string out;
  out.reserve(text.size());
  bool inStrong = false;
  for (size_t i = 0; i < text.size(); ++i) {
    if (i + 1 < text.size() && text[i] == '*' && text[i + 1] == '*') {
      out += inStrong ? "</strong>" : "<strong>";
      inStrong = !inStrong;
      ++i;  // skip second '*'
    } else {
      out += text[i];
    }
  }
  if (inStrong) out += "</strong>";
  return out;
}

}  // namespace

std::string SimpleMarkdownRenderer::renderToHtml(const std::string& markdown) const {
  if (markdown.empty()) return "";

  // First escape to make output safe, then apply a very small Markdown subset
  // (headings, unordered lists, paragraphs, **strong**).
  std::string escaped = escapeHtml(markdown);

  std::istringstream in(escaped);
  std::string html;
  html.reserve(escaped.size() + 64);

  bool inParagraph = false;
  bool inList = false;

  std::string line;
  while (std::getline(in, line)) {
    std::string t = trim(line);

    if (t.empty()) {
      if (inParagraph) {
        html += "</p>";
        inParagraph = false;
      }
      if (inList) {
        html += "</ul>";
        inList = false;
      }
      continue;
    }

    // Unordered list item: "- " or "* "
    if (t.rfind("- ", 0) == 0 || t.rfind("* ", 0) == 0) {
      if (inParagraph) {
        html += "</p>";
        inParagraph = false;
      }
      if (!inList) {
        html += "<ul>";
        inList = true;
      }
      std::string itemText = applyInline(t.substr(2));
      html += "<li>" + itemText + "</li>";
      continue;
    }

    // Heading: "# " .. "###### "
    if (t[0] == '#') {
      size_t cnt = 0;
      while (cnt < t.size() && t[cnt] == '#') ++cnt;
      if (cnt > 0 && cnt <= 6 && cnt < t.size() && t[cnt] == ' ') {
        if (inParagraph) {
          html += "</p>";
          inParagraph = false;
        }
        if (inList) {
          html += "</ul>";
          inList = false;
        }
        std::string hText = applyInline(trim(t.substr(cnt + 1)));
        html += "<h" + std::to_string(cnt) + ">" + hText + "</h" + std::to_string(cnt) + ">";
        continue;
      }
    }

    // Normal paragraph text
    if (inList) {
      html += "</ul>";
      inList = false;
    }
    std::string paraText = applyInline(t);
    if (!inParagraph) {
      html += "<p>" + paraText;
      inParagraph = true;
    } else {
      html += "<br>" + paraText;
    }
  }

  if (inParagraph) html += "</p>";
  if (inList) html += "</ul>";

  return html;
}

}  // namespace cms::infrastructure
