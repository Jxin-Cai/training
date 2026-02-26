#pragma once

#include "ContentStatus.hpp"
#include <string>
#include <cstdint>
#include <chrono>
#include <optional>

namespace cms::domain {

using Timestamp = std::chrono::system_clock::time_point;

struct ContentId {
  std::uint64_t value{0};
  bool operator==(const ContentId& o) const { return value == o.value; }
};

struct Content {
  ContentId id;
  std::string title;
  std::uint64_t categoryId{0};
  std::string markdownBody;
  std::string htmlBody;
  ContentStatus status{ContentStatus::Draft};
  std::optional<Timestamp> publishedAt;
  Timestamp createdAt{};
  Timestamp updatedAt{};

  Content() = default;
  Content(ContentId id_, std::string title_, std::uint64_t categoryId_,
         std::string markdownBody_, std::string htmlBody_, ContentStatus status_,
         std::optional<Timestamp> publishedAt_, Timestamp createdAt_, Timestamp updatedAt_)
      : id(id_), title(std::move(title_)), categoryId(categoryId_),
        markdownBody(std::move(markdownBody_)), htmlBody(std::move(htmlBody_)),
        status(status_), publishedAt(publishedAt_), createdAt(createdAt_), updatedAt(updatedAt_) {}

  bool isPublished() const { return status == ContentStatus::Published; }
};

}  // namespace cms::domain
