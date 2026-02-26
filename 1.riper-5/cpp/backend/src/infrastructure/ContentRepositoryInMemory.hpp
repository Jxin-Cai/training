#pragma once

#include "domain/content/Content.hpp"
#include "domain/content/IContentRepository.hpp"
#include <unordered_map>
#include <mutex>
#include <vector>
#include <algorithm>

namespace cms::infrastructure {

class ContentRepositoryInMemory : public cms::domain::IContentRepository {
 public:
  cms::domain::Content create(cms::domain::Content c) override;
  std::optional<cms::domain::Content> update(cms::domain::Content c) override;
  bool remove(cms::domain::ContentId id) override;
  std::optional<cms::domain::Content> findById(cms::domain::ContentId id) const override;
  std::vector<cms::domain::Content> findAll() const override;
  std::vector<cms::domain::Content> findPublishedOrderByPublishedAtDesc() const override;
  std::vector<cms::domain::Content> findByCategoryId(std::uint64_t categoryId) const override;

 private:
  mutable std::mutex mutex_;
  std::unordered_map<std::uint64_t, cms::domain::Content> store_;
  std::uint64_t nextId_{1};
};

}  // namespace cms::infrastructure
