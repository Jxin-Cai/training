#pragma once

#include "domain/content/Content.hpp"
#include "domain/content/IContentRepository.hpp"
#include "infrastructure/IMarkdownRenderer.hpp"
#include <vector>
#include <optional>

namespace cms::application {

class ContentService {
 public:
  ContentService(cms::domain::IContentRepository& repo,
                 cms::infrastructure::IMarkdownRenderer& renderer)
      : repo_(repo), renderer_(renderer) {}

  cms::domain::Content create(std::string title, std::uint64_t categoryId,
                              std::string markdownBody);
  std::optional<cms::domain::Content> update(std::uint64_t id, std::string title,
                                             std::uint64_t categoryId, std::string markdownBody);
  bool remove(std::uint64_t id);
  std::optional<cms::domain::Content> getById(std::uint64_t id) const;
  std::vector<cms::domain::Content> listAll() const;
  std::vector<cms::domain::Content> listPublishedByPublishedAtDesc() const;
  std::optional<cms::domain::Content> publish(std::uint64_t id);
  std::optional<cms::domain::Content> unpublish(std::uint64_t id);

 private:
  cms::domain::IContentRepository& repo_;
  cms::infrastructure::IMarkdownRenderer& renderer_;
};

}  // namespace cms::application
