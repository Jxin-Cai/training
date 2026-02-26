#pragma once

#include "Category.hpp"
#include <vector>
#include <optional>

namespace cms::domain {

class ICategoryRepository {
 public:
  virtual ~ICategoryRepository() = default;

  virtual Category create(Category c) = 0;
  virtual std::optional<Category> update(Category c) = 0;
  virtual bool remove(CategoryId id) = 0;
  virtual std::optional<Category> findById(CategoryId id) const = 0;
  virtual std::vector<Category> findAll() const = 0;
};

}  // namespace cms::domain
