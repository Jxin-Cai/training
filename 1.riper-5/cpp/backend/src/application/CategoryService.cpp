#include "CategoryService.hpp"
#include <chrono>

namespace cms::application {

namespace {
using namespace cms::domain;
using Clock = std::chrono::system_clock;
}

Category CategoryService::create(std::string name, std::string description) {
  Category c;
  c.name = std::move(name);
  c.description = std::move(description);
  return repo_.create(c);
}

std::optional<Category> CategoryService::update(std::uint64_t id, std::string name, std::string description) {
  auto existing = repo_.findById(CategoryId{id});
  if (!existing) return std::nullopt;
  existing->name = std::move(name);
  existing->description = std::move(description);
  return repo_.update(*existing);
}

bool CategoryService::remove(std::uint64_t id) {
  return repo_.remove(CategoryId{id});
}

std::optional<Category> CategoryService::getById(std::uint64_t id) const {
  return repo_.findById(CategoryId{id});
}

std::vector<Category> CategoryService::listAll() const {
  return repo_.findAll();
}

}  // namespace cms::application
