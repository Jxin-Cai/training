#pragma once

#include "CmsRequestHandler.hpp"
#include "application/CategoryService.hpp"
#include "application/ContentService.hpp"
#include <Poco/Net/HTTPRequestHandlerFactory.h>
#include <Poco/Net/HTTPServerRequest.h>

namespace cms::presentation {

class CmsRequestHandlerFactory : public Poco::Net::HTTPRequestHandlerFactory {
 public:
  CmsRequestHandlerFactory(cms::application::CategoryService& categoryService,
                           cms::application::ContentService& contentService)
      : categoryService_(categoryService), contentService_(contentService) {}

  Poco::Net::HTTPRequestHandler* createRequestHandler(
      const Poco::Net::HTTPServerRequest&) override {
    return new CmsRequestHandler(categoryService_, contentService_);
  }

 private:
  cms::application::CategoryService& categoryService_;
  cms::application::ContentService& contentService_;
};

}  // namespace cms::presentation
