#pragma once

#include <string>

namespace cms::infrastructure {

class IMarkdownRenderer {
 public:
  virtual ~IMarkdownRenderer() = default;
  virtual std::string renderToHtml(const std::string& markdown) const = 0;
};

}  // namespace cms::infrastructure
