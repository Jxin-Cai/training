#pragma once

#include <string>
#include <cstdint>
#include <chrono>

namespace cms::domain {

using Timestamp = std::chrono::system_clock::time_point;

struct CategoryId {
  std::uint64_t value{0};
  bool operator==(const CategoryId& o) const { return value == o.value; }
};

struct Category {
  CategoryId id;
  std::string name;
  std::string description;
  Timestamp createdAt{};
  Timestamp updatedAt{};

  Category() = default;
  Category(CategoryId id_, std::string name_, std::string description_,
          Timestamp createdAt_, Timestamp updatedAt_)
      : id(id_), name(std::move(name_)), description(std::move(description_)),
        createdAt(createdAt_), updatedAt(updatedAt_) {}
};

}  // namespace cms::domain
