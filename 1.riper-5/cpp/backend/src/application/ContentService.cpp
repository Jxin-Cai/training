#include "ContentService.hpp"
#include <chrono>

namespace cms::application {

namespace {
using namespace cms::domain;
using Clock = std::chrono::system_clock;
}

Content ContentService::create(std::string title, std::uint64_t categoryId, std::string markdownBody) {
  Content c;
  c.title = std::move(title);
  c.categoryId = categoryId;
  c.markdownBody = std::move(markdownBody);
  c.htmlBody = renderer_.renderToHtml(c.markdownBody);
  c.status = ContentStatus::Draft;
  return repo_.create(c);
}

std::optional<Content> ContentService::update(std::uint64_t id, std::string title,
                                              std::uint64_t categoryId, std::string markdownBody) {
  auto existing = repo_.findById(ContentId{id});
  if (!existing) return std::nullopt;
  existing->title = std::move(title);
  existing->categoryId = categoryId;
  existing->markdownBody = std::move(markdownBody);
  existing->htmlBody = renderer_.renderToHtml(existing->markdownBody);
  return repo_.update(*existing);
}

bool ContentService::remove(std::uint64_t id) {
  return repo_.remove(ContentId{id});
}

std::optional<Content> ContentService::getById(std::uint64_t id) const {
  return repo_.findById(ContentId{id});
}

std::vector<Content> ContentService::listAll() const {
  return repo_.findAll();
}

std::vector<Content> ContentService::listPublishedByPublishedAtDesc() const {
  return repo_.findPublishedOrderByPublishedAtDesc();
}

std::optional<Content> ContentService::publish(std::uint64_t id) {
  auto c = repo_.findById(ContentId{id});
  if (!c) return std::nullopt;
  c->status = ContentStatus::Published;
  if (!c->publishedAt) c->publishedAt = Clock::now();
  return repo_.update(*c);
}

std::optional<Content> ContentService::unpublish(std::uint64_t id) {
  auto c = repo_.findById(ContentId{id});
  if (!c) return std::nullopt;
  c->status = ContentStatus::Draft;
  return repo_.update(*c);
}

}  // namespace cms::application
