#pragma once

#include "application/CategoryService.hpp"
#include "application/ContentService.hpp"
#include <Poco/Net/HTTPRequestHandler.h>
#include <Poco/Net/HTTPServerRequest.h>
#include <Poco/Net/HTTPServerResponse.h>

namespace cms::presentation {

class CmsRequestHandler : public Poco::Net::HTTPRequestHandler {
 public:
  CmsRequestHandler(cms::application::CategoryService& categoryService,
                    cms::application::ContentService& contentService)
      : categoryService_(categoryService), contentService_(contentService) {}

  void handleRequest(Poco::Net::HTTPServerRequest& request,
                     Poco::Net::HTTPServerResponse& response) override;

 private:
  cms::application::CategoryService& categoryService_;
  cms::application::ContentService& contentService_;

  void handleCategories(Poco::Net::HTTPServerRequest& req, Poco::Net::HTTPServerResponse& res);
  void handleCategoryById(Poco::Net::HTTPServerRequest& req, Poco::Net::HTTPServerResponse& res,
                          const std::string& idStr);
  void handleContents(Poco::Net::HTTPServerRequest& req, Poco::Net::HTTPServerResponse& res);
  void handleContentById(Poco::Net::HTTPServerRequest& req, Poco::Net::HTTPServerResponse& res,
                         const std::string& idStr);
  void handleContentPublish(const std::string& idStr, bool publish,
                            Poco::Net::HTTPServerResponse& res);
  void handlePublicContents(Poco::Net::HTTPServerRequest& req, Poco::Net::HTTPServerResponse& res);
  void handlePublicContentById(const std::string& idStr, Poco::Net::HTTPServerResponse& res);

  void sendJson(Poco::Net::HTTPServerResponse& res, int status, const std::string& body);
  void sendError(Poco::Net::HTTPServerResponse& res, int status, const std::string& message);
  std::string readBody(Poco::Net::HTTPServerRequest& req);
};

}  // namespace cms::presentation
