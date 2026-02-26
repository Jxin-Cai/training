#pragma once

#include "domain/category/Category.hpp"
#include "domain/category/ICategoryRepository.hpp"
#include <unordered_map>
#include <mutex>

namespace cms::infrastructure {

class CategoryRepositoryInMemory : public cms::domain::ICategoryRepository {
 public:
  cms::domain::Category create(cms::domain::Category c) override;
  std::optional<cms::domain::Category> update(cms::domain::Category c) override;
  bool remove(cms::domain::CategoryId id) override;
  std::optional<cms::domain::Category> findById(cms::domain::CategoryId id) const override;
  std::vector<cms::domain::Category> findAll() const override;

 private:
  mutable std::mutex mutex_;
  std::unordered_map<std::uint64_t, cms::domain::Category> store_;
  std::uint64_t nextId_{1};
};

}  // namespace cms::infrastructure
