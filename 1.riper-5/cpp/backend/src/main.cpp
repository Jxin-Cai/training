#include "infrastructure/CategoryRepositoryInMemory.hpp"
#include "infrastructure/ContentRepositoryInMemory.hpp"
#include "infrastructure/SimpleMarkdownRenderer.hpp"
#include "application/CategoryService.hpp"
#include "application/ContentService.hpp"
#include "presentation/CmsRequestHandlerFactory.hpp"
#include <Poco/Net/HTTPServer.h>
#include <Poco/Net/ServerSocket.h>
#include <iostream>
#include <thread>
#include <chrono>

int main(int argc, char** argv) {
  using namespace cms;
  using namespace Poco::Net;

  infrastructure::CategoryRepositoryInMemory categoryRepo;
  infrastructure::ContentRepositoryInMemory contentRepo;
  infrastructure::SimpleMarkdownRenderer renderer;
  application::CategoryService categoryService(categoryRepo);
  application::ContentService contentService(contentRepo, renderer);

  ServerSocket socket(8080);
  HTTPServerParams::Ptr params = new HTTPServerParams;
  HTTPServer server(new presentation::CmsRequestHandlerFactory(categoryService, contentService),
                    socket, params);
  server.start();
  std::cout << "CMS backend listening on http://localhost:8080\n";
  std::cout << "API: GET/POST /api/categories, GET/PUT/DELETE /api/categories/{id}\n";
  std::cout << "     GET/POST /api/contents, GET/PUT/DELETE /api/contents/{id}, POST /api/contents/{id}/publish|unpublish\n";
  std::cout << "     GET /api/public/contents, GET /api/public/contents/{id}\n";
  std::cout << "Press Ctrl+C to stop the server.\n";

  // Simple blocking loop; process will terminate on SIGINT (Ctrl+C).
  while (true) {
    std::this_thread::sleep_for(std::chrono::seconds(60));
  }

  server.stop();
  return 0;
}
