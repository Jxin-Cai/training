#pragma once

#include "Content.hpp"
#include <vector>
#include <optional>

namespace cms::domain {

class IContentRepository {
 public:
  virtual ~IContentRepository() = default;

  virtual Content create(Content c) = 0;
  virtual std::optional<Content> update(Content c) = 0;
  virtual bool remove(ContentId id) = 0;
  virtual std::optional<Content> findById(ContentId id) const = 0;
  virtual std::vector<Content> findAll() const = 0;
  /** Published contents, ordered by publishedAt descending. */
  virtual std::vector<Content> findPublishedOrderByPublishedAtDesc() const = 0;
  virtual std::vector<Content> findByCategoryId(std::uint64_t categoryId) const = 0;
};

}  // namespace cms::domain
