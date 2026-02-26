#pragma once

#include "domain/category/Category.hpp"
#include "domain/category/ICategoryRepository.hpp"
#include <vector>
#include <optional>

namespace cms::application {

class CategoryService {
 public:
  explicit CategoryService(cms::domain::ICategoryRepository& repo) : repo_(repo) {}

  cms::domain::Category create(std::string name, std::string description);
  std::optional<cms::domain::Category> update(std::uint64_t id, std::string name, std::string description);
  bool remove(std::uint64_t id);
  std::optional<cms::domain::Category> getById(std::uint64_t id) const;
  std::vector<cms::domain::Category> listAll() const;

 private:
  cms::domain::ICategoryRepository& repo_;
};

}  // namespace cms::application
