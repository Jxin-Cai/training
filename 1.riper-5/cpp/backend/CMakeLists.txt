cmake_minimum_required(VERSION 3.16)
project(cms_backend CXX)

set(CMAKE_CXX_STANDARD 17)

find_package(Poco COMPONENTS Net JSON Foundation Util REQUIRED)

add_executable(app
  src/main.cpp
  src/infrastructure/CategoryRepositoryInMemory.cpp
  src/infrastructure/ContentRepositoryInMemory.cpp
  src/infrastructure/SimpleMarkdownRenderer.cpp
  src/application/CategoryService.cpp
  src/application/ContentService.cpp
  src/presentation/CmsRequestHandler.cpp
)

target_include_directories(app PRIVATE
  ${CMAKE_CURRENT_SOURCE_DIR}/src
)
target_link_libraries(app PRIVATE Poco::Net Poco::JSON Poco::Foundation Poco::Util)

# Unit tests (no Poco required)
add_executable(unit_repos
  tests/unit_repos.cpp
  src/infrastructure/CategoryRepositoryInMemory.cpp
  src/infrastructure/ContentRepositoryInMemory.cpp
  src/infrastructure/SimpleMarkdownRenderer.cpp
)
target_include_directories(unit_repos PRIVATE ${CMAKE_CURRENT_SOURCE_DIR}/src)
add_test(NAME unit_repos COMMAND unit_repos)
