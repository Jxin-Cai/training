#include "CategoryRepositoryInMemory.hpp"
#include <algorithm>

namespace cms::infrastructure {

namespace {
using namespace cms::domain;
using Clock = std::chrono::system_clock;
}

Category CategoryRepositoryInMemory::create(Category c) {
  std::lock_guard lock(mutex_);
  c.id.value = nextId_++;
  c.createdAt = c.updatedAt = Clock::now();
  store_[c.id.value] = c;
  return c;
}

std::optional<Category> CategoryRepositoryInMemory::update(Category c) {
  std::lock_guard lock(mutex_);
  auto it = store_.find(c.id.value);
  if (it == store_.end()) return std::nullopt;
  c.updatedAt = Clock::now();
  c.createdAt = it->second.createdAt;
  it->second = c;
  return c;
}

bool CategoryRepositoryInMemory::remove(CategoryId id) {
  std::lock_guard lock(mutex_);
  return store_.erase(id.value) > 0;
}

std::optional<Category> CategoryRepositoryInMemory::findById(CategoryId id) const {
  std::lock_guard lock(mutex_);
  auto it = store_.find(id.value);
  if (it == store_.end()) return std::nullopt;
  return it->second;
}

std::vector<Category> CategoryRepositoryInMemory::findAll() const {
  std::lock_guard lock(mutex_);
  std::vector<Category> out;
  out.reserve(store_.size());
  for (const auto& [_, c] : store_) out.push_back(c);
  std::sort(out.begin(), out.end(), [](const Category& a, const Category& b) {
    return a.id.value < b.id.value;
  });
  return out;
}

}  // namespace cms::infrastructure
