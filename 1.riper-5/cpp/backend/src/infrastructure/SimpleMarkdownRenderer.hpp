#pragma once

#include "IMarkdownRenderer.hpp"
#include <sstream>
#include <cctype>

namespace cms::infrastructure {

/** Minimal Markdown-to-HTML for MVP: escape HTML, convert newlines to <br>, wrap in <p>. */
class SimpleMarkdownRenderer : public IMarkdownRenderer {
 public:
  std::string renderToHtml(const std::string& markdown) const override;
};

}  // namespace cms::infrastructure
