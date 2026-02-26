/**
 * Minimal unit tests for in-memory repositories (no Poco dependency).
 * Build with: add this source and link domain + infrastructure objects.
 */
#include "infrastructure/CategoryRepositoryInMemory.hpp"
#include "infrastructure/ContentRepositoryInMemory.hpp"
#include "infrastructure/SimpleMarkdownRenderer.hpp"
#include "domain/category/Category.hpp"
#include "domain/content/Content.hpp"
#include "domain/content/ContentStatus.hpp"
#include <cassert>
#include <iostream>

#define ASSERT(x) do { if (!(x)) { std::cerr << "FAIL: " #x << std::endl; return 1; } } while(0)

int main() {
  using namespace cms::domain;
  using namespace cms::infrastructure;

  CategoryRepositoryInMemory catRepo;
  Category c1;
  c1.name = "Tech";
  c1.description = "Tech posts";
  auto created = catRepo.create(c1);
  ASSERT(created.id.value != 0);
  ASSERT(created.name == "Tech");
  auto found = catRepo.findById(created.id);
  ASSERT(found && found->name == "Tech");
  auto all = catRepo.findAll();
  ASSERT(all.size() == 1);
  created.name = "Technology";
  auto updated = catRepo.update(created);
  ASSERT(updated && updated->name == "Technology");
  bool removed = catRepo.remove(created.id);
  ASSERT(removed);
  ASSERT(!catRepo.findById(created.id));

  ContentRepositoryInMemory contentRepo;
  SimpleMarkdownRenderer renderer;
  Content cont;
  cont.title = "Hello";
  cont.categoryId = 1;
  cont.markdownBody = "# Hi";
  cont.htmlBody = renderer.renderToHtml(cont.markdownBody);
  cont.status = ContentStatus::Draft;
  auto cCreated = contentRepo.create(cont);
  ASSERT(cCreated.id.value != 0);
  auto cFound = contentRepo.findById(cCreated.id);
  ASSERT(cFound && cFound->title == "Hello");
  auto pubList = contentRepo.findPublishedOrderByPublishedAtDesc();
  ASSERT(pubList.empty());
  contentRepo.remove(cCreated.id);
  ASSERT(!contentRepo.findById(cCreated.id));

  std::cout << "All unit checks passed.\n";
  return 0;
}
