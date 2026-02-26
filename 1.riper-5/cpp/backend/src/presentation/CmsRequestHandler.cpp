#include "CmsRequestHandler.hpp"
#include "domain/category/Category.hpp"
#include "domain/content/Content.hpp"
#include "domain/content/ContentStatus.hpp"
#include <Poco/JSON/Object.h>
#include <Poco/JSON/Array.h>
#include <Poco/JSON/Parser.h>
#include <Poco/StreamCopier.h>
#include <iostream>
#include <sstream>
#include <chrono>
#include <cstdlib>

namespace cms::presentation {

namespace {

using namespace Poco::JSON;

std::string timestampToIso(const cms::domain::Timestamp& t) {
  auto ms = std::chrono::duration_cast<std::chrono::milliseconds>(t.time_since_epoch()).count();
  time_t sec = ms / 1000;
  char buf[32];
  struct tm tm;
#ifdef _WIN32
  gmtime_s(&tm, &sec);
#else
  gmtime_r(&sec, &tm);
#endif
  std::strftime(buf, sizeof(buf), "%Y-%m-%dT%H:%M:%S", &tm);
  return std::string(buf) + "Z";
}

Object categoryToJson(const cms::domain::Category& c) {
  Object o;
  o.set("id", static_cast<std::int64_t>(c.id.value));
  o.set("name", c.name);
  o.set("description", c.description);
  o.set("createdAt", timestampToIso(c.createdAt));
  o.set("updatedAt", timestampToIso(c.updatedAt));
  return o;
}

Object contentToJson(const cms::domain::Content& c, bool includeHtml = true) {
  Object o;
  o.set("id", static_cast<std::int64_t>(c.id.value));
  o.set("title", c.title);
  o.set("categoryId", static_cast<std::int64_t>(c.categoryId));
  o.set("markdownBody", c.markdownBody);
  if (includeHtml) o.set("htmlBody", c.htmlBody);
  o.set("status", c.status == cms::domain::ContentStatus::Published ? "published" : "draft");
  if (c.publishedAt)
    o.set("publishedAt", timestampToIso(*c.publishedAt));
  else
    o.set("publishedAt", std::string());
  o.set("createdAt", timestampToIso(c.createdAt));
  o.set("updatedAt", timestampToIso(c.updatedAt));
  return o;
}

Object contentToPublicJson(const cms::domain::Content& c) {
  Object o;
  o.set("id", static_cast<std::int64_t>(c.id.value));
  o.set("title", c.title);
  o.set("categoryId", static_cast<std::int64_t>(c.categoryId));
  o.set("htmlBody", c.htmlBody);
  o.set("publishedAt", c.publishedAt ? timestampToIso(*c.publishedAt) : std::string());
  o.set("createdAt", timestampToIso(c.createdAt));
  return o;
}

std::uint64_t parseId(const std::string& s) {
  return static_cast<std::uint64_t>(std::strtoull(s.c_str(), nullptr, 10));
}

}  // namespace

void CmsRequestHandler::handleRequest(Poco::Net::HTTPServerRequest& request,
                                      Poco::Net::HTTPServerResponse& response) {
  response.setContentType("application/json; charset=utf-8");
  response.setChunkedTransferEncoding(true);

  std::string path = request.getURI();
  std::string method = request.getMethod();

  if (path == "/api/categories" && method == "GET") {
    handleCategories(request, response);
    return;
  }
  if (path == "/api/categories" && method == "POST") {
    std::string body = readBody(request);
    Poco::JSON::Parser p;
    auto parsed = p.parse(body);
    auto obj = parsed.extract<Poco::JSON::Object::Ptr>();
    std::string name = obj->getValue<std::string>("name");
    std::string desc = obj->optValue<std::string>("description", "");
    auto c = categoryService_.create(name, desc);
    Object out;
    out.set("data", categoryToJson(c));
    std::ostringstream ss;
    out.stringify(ss);
    sendJson(response, 200, ss.str());
    return;
  }
  if (path.size() > 14 && path.substr(0, 14) == "/api/categories/" && path.find('/', 14) == std::string::npos) {
    std::string idStr = path.substr(14);
    if (method == "GET") { handleCategoryById(request, response, idStr); return; }
    if (method == "PUT") {
      std::string body = readBody(request);
      Poco::JSON::Parser p;
      auto parsed = p.parse(body);
      auto obj = parsed.extract<Poco::JSON::Object::Ptr>();
      std::string name = obj->getValue<std::string>("name");
      std::string desc = obj->optValue<std::string>("description", "");
      auto updated = categoryService_.update(parseId(idStr), name, desc);
      if (!updated) { sendError(response, 404, "Category not found"); return; }
      Object out;
      out.set("data", categoryToJson(*updated));
      std::ostringstream ss;
      out.stringify(ss);
      sendJson(response, 200, ss.str());
      return;
    }
    if (method == "DELETE") {
      bool ok = categoryService_.remove(parseId(idStr));
      if (!ok) { sendError(response, 404, "Category not found"); return; }
      Object out;
      out.set("ok", true);
      std::ostringstream ss;
      out.stringify(ss);
      sendJson(response, 200, ss.str());
      return;
    }
  }

  if (path == "/api/contents" && method == "GET") {
    handleContents(request, response);
    return;
  }
  if (path == "/api/contents" && method == "POST") {
    std::string body = readBody(request);
    Poco::JSON::Parser p;
    auto parsed = p.parse(body);
    auto obj = parsed.extract<Poco::JSON::Object::Ptr>();
    std::string title = obj->getValue<std::string>("title");
    std::uint64_t categoryId = static_cast<std::uint64_t>(obj->getValue<std::int64_t>("categoryId"));
    std::string markdownBody = obj->optValue<std::string>("markdownBody", "");
    auto c = contentService_.create(title, categoryId, markdownBody);
    Object out;
    out.set("data", contentToJson(c));
    std::ostringstream ss;
    out.stringify(ss);
    sendJson(response, 200, ss.str());
    return;
  }
  if (path.size() > 14 && path.substr(0, 14) == "/api/contents/") {
    size_t nextSlash = path.find('/', 14);
    std::string idStr = nextSlash == std::string::npos ? path.substr(14) : path.substr(14, nextSlash - 14);
    if (nextSlash == std::string::npos) {
      if (method == "GET") { handleContentById(request, response, idStr); return; }
      if (method == "PUT") {
        std::string body = readBody(request);
        Poco::JSON::Parser p;
        auto parsed = p.parse(body);
        auto obj = parsed.extract<Poco::JSON::Object::Ptr>();
        std::string title = obj->getValue<std::string>("title");
        std::uint64_t categoryId = static_cast<std::uint64_t>(obj->getValue<std::int64_t>("categoryId"));
        std::string markdownBody = obj->optValue<std::string>("markdownBody", "");
        auto updated = contentService_.update(parseId(idStr), title, categoryId, markdownBody);
        if (!updated) { sendError(response, 404, "Content not found"); return; }
        Object out;
        out.set("data", contentToJson(*updated));
        std::ostringstream ss;
        out.stringify(ss);
        sendJson(response, 200, ss.str());
        return;
      }
      if (method == "DELETE") {
        bool ok = contentService_.remove(parseId(idStr));
        if (!ok) { sendError(response, 404, "Content not found"); return; }
        Object out;
        out.set("ok", true);
        std::ostringstream ss;
        out.stringify(ss);
        sendJson(response, 200, ss.str());
        return;
      }
    } else {
      std::string sub = path.substr(nextSlash + 1);
      if (sub == "publish" && method == "POST") {
        handleContentPublish(idStr, true, response);
        return;
      }
      if (sub == "unpublish" && method == "POST") {
        handleContentPublish(idStr, false, response);
        return;
      }
    }
  }

  if (path == "/api/uploads/markdown" && method == "POST") {
    std::string body = readBody(request);
    Poco::JSON::Parser p;
    auto parsed = p.parse(body);
    auto obj = parsed.extract<Poco::JSON::Object::Ptr>();
    std::string markdownBody = obj->getValue<std::string>("markdownBody");
    std::string title = obj->optValue<std::string>("title", "Untitled");
    std::uint64_t categoryId = static_cast<std::uint64_t>(obj->optValue<std::int64_t>("categoryId", 1));
    auto c = contentService_.create(title, categoryId, markdownBody);
    Object out;
    out.set("data", contentToJson(c));
    std::ostringstream ss;
    out.stringify(ss);
    sendJson(response, 200, ss.str());
    return;
  }

  if (path == "/api/public/contents" && method == "GET") {
    handlePublicContents(request, response);
    return;
  }
  // "/api/public/contents/" has length 21 (indexes 0..20)
  if (path.size() > 21 && path.substr(0, 21) == "/api/public/contents/") {
    std::string idStr = path.substr(21);
    if (method == "GET") {
      handlePublicContentById(idStr, response);
      return;
    }
  }

  sendError(response, 404, "Not Found");
}

void CmsRequestHandler::handleCategories(Poco::Net::HTTPServerRequest&, Poco::Net::HTTPServerResponse& res) {
  auto list = categoryService_.listAll();
  Array arr;
  for (const auto& c : list) arr.add(categoryToJson(c));
  Object out;
  out.set("data", arr);
  std::ostringstream ss;
  out.stringify(ss);
  sendJson(res, 200, ss.str());
}

void CmsRequestHandler::handleCategoryById(Poco::Net::HTTPServerRequest&, Poco::Net::HTTPServerResponse& res,
                                          const std::string& idStr) {
  auto c = categoryService_.getById(parseId(idStr));
  if (!c) { sendError(res, 404, "Category not found"); return; }
  Object out;
  out.set("data", categoryToJson(*c));
  std::ostringstream ss;
  out.stringify(ss);
  sendJson(res, 200, ss.str());
}

void CmsRequestHandler::handleContents(Poco::Net::HTTPServerRequest&, Poco::Net::HTTPServerResponse& res) {
  auto list = contentService_.listAll();
  Array arr;
  for (const auto& c : list) arr.add(contentToJson(c));
  Object out;
  out.set("data", arr);
  std::ostringstream ss;
  out.stringify(ss);
  sendJson(res, 200, ss.str());
}

void CmsRequestHandler::handleContentById(Poco::Net::HTTPServerRequest&, Poco::Net::HTTPServerResponse& res,
                                         const std::string& idStr) {
  auto c = contentService_.getById(parseId(idStr));
  if (!c) { sendError(res, 404, "Content not found"); return; }
  Object out;
  out.set("data", contentToJson(*c));
  std::ostringstream ss;
  out.stringify(ss);
  sendJson(res, 200, ss.str());
}

void CmsRequestHandler::handleContentPublish(const std::string& idStr, bool publish,
                                             Poco::Net::HTTPServerResponse& res) {
  auto updated = publish ? contentService_.publish(parseId(idStr)) : contentService_.unpublish(parseId(idStr));
  if (!updated) { sendError(res, 404, "Content not found"); return; }
  Object out;
  out.set("data", contentToJson(*updated));
  std::ostringstream ss;
  out.stringify(ss);
  sendJson(res, 200, ss.str());
}

void CmsRequestHandler::handlePublicContents(Poco::Net::HTTPServerRequest&, Poco::Net::HTTPServerResponse& res) {
  auto list = contentService_.listPublishedByPublishedAtDesc();
  Array arr;
  for (const auto& c : list) arr.add(contentToPublicJson(c));
  Object out;
  out.set("data", arr);
  std::ostringstream ss;
  out.stringify(ss);
  sendJson(res, 200, ss.str());
}

void CmsRequestHandler::handlePublicContentById(const std::string& idStr, Poco::Net::HTTPServerResponse& res) {
  auto c = contentService_.getById(parseId(idStr));
  if (!c) { sendError(res, 404, "Content not found"); return; }
  if (c->status != cms::domain::ContentStatus::Published) {
    sendError(res, 404, "Content not found");
    return;
  }
  Object out;
  out.set("data", contentToPublicJson(*c));
  std::ostringstream ss;
  out.stringify(ss);
  sendJson(res, 200, ss.str());
}

void CmsRequestHandler::sendJson(Poco::Net::HTTPServerResponse& res, int status, const std::string& body) {
  res.setStatus(static_cast<Poco::Net::HTTPResponse::HTTPStatus>(status));
  res.sendBuffer(body.data(), body.size());
}

void CmsRequestHandler::sendError(Poco::Net::HTTPServerResponse& res, int status, const std::string& message) {
  Poco::JSON::Object o;
  o.set("error", message);
  std::ostringstream ss;
  o.stringify(ss);
  res.setStatus(static_cast<Poco::Net::HTTPResponse::HTTPStatus>(status));
  res.sendBuffer(ss.str().data(), ss.str().size());
}

std::string CmsRequestHandler::readBody(Poco::Net::HTTPServerRequest& req) {
  std::istream& is = req.stream();
  std::ostringstream os;
  Poco::StreamCopier::copyStream(is, os);
  return os.str();
}

}  // namespace cms::presentation
