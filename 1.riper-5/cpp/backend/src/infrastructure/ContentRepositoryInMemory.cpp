#include "ContentRepositoryInMemory.hpp"
#include <algorithm>

namespace cms::infrastructure {

namespace {
using namespace cms::domain;
using Clock = std::chrono::system_clock;
}

Content ContentRepositoryInMemory::create(Content c) {
  std::lock_guard lock(mutex_);
  c.id.value = nextId_++;
  c.createdAt = c.updatedAt = Clock::now();
  store_[c.id.value] = c;
  return c;
}

std::optional<Content> ContentRepositoryInMemory::update(Content c) {
  std::lock_guard lock(mutex_);
  auto it = store_.find(c.id.value);
  if (it == store_.end()) return std::nullopt;
  c.updatedAt = Clock::now();
  // Preserve original creation time; publishedAt is controlled by callers
  // (e.g., publish/unpublish flows) and should not be overwritten here.
  c.createdAt = it->second.createdAt;
  it->second = c;
  return c;
}

bool ContentRepositoryInMemory::remove(ContentId id) {
  std::lock_guard lock(mutex_);
  return store_.erase(id.value) > 0;
}

std::optional<Content> ContentRepositoryInMemory::findById(ContentId id) const {
  std::lock_guard lock(mutex_);
  auto it = store_.find(id.value);
  if (it == store_.end()) return std::nullopt;
  return it->second;
}

std::vector<Content> ContentRepositoryInMemory::findAll() const {
  std::lock_guard lock(mutex_);
  std::vector<Content> out;
  out.reserve(store_.size());
  for (const auto& [_, c] : store_) out.push_back(c);
  std::sort(out.begin(), out.end(), [](const Content& a, const Content& b) {
    return a.id.value < b.id.value;
  });
  return out;
}

std::vector<Content> ContentRepositoryInMemory::findPublishedOrderByPublishedAtDesc() const {
  std::lock_guard lock(mutex_);
  std::vector<Content> out;
  for (const auto& [_, c] : store_) {
    if (c.status == ContentStatus::Published && c.publishedAt.has_value())
      out.push_back(c);
  }
  std::sort(out.begin(), out.end(), [](const Content& a, const Content& b) {
    return a.publishedAt.value() > b.publishedAt.value();
  });
  return out;
}

std::vector<Content> ContentRepositoryInMemory::findByCategoryId(std::uint64_t categoryId) const {
  std::lock_guard lock(mutex_);
  std::vector<Content> out;
  for (const auto& [_, c] : store_) {
    if (c.categoryId == categoryId) out.push_back(c);
  }
  std::sort(out.begin(), out.end(), [](const Content& a, const Content& b) {
    return a.id.value < b.id.value;
  });
  return out;
}

}  // namespace cms::infrastructure
