# AI 开发工具安装指南

快速安装 AI 驱动开发所需的所有工具和框架。

---

## 目录

- [前置工具安装](#前置工具安装)
  - [Node.js 与 npm](#nodejs-与-npm)
  - [Bun](#bun)
  - [Python](#python)
  - [C++](#c)
  - [Java](#java)
- [OpenCode](#opencode)
- [Spec-Kit](#spec-kit)
- [OpenSpec](#openspec)
- [BMAD 框架](#bmad-框架)
- [Superpowers 框架](#superpowers-框架)

---

## 前置工具安装

### Node.js 与 npm

**macOS 安装：**

```bash
# 方式 1：使用 Homebrew（推荐）
brew install node

# 方式 2：使用 nvm（Node Version Manager）
curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.0/install.sh | bash
nvm install --lts
```

**Windows 安装：**

```powershell
# 方式 1：从官网下载安装器（推荐）
# 访问 https://nodejs.org/ 下载 LTS 版本安装

# 方式 2：使用 Chocolatey
choco install nodejs-lts

# 方式 3：使用 Scoop
scoop install nodejs-lts
```

**验证安装：**

```bash
node --version
npm --version
```

---

### Bun

**macOS / Linux 安装：**

```bash
# 使用安装脚本（推荐）
curl -fsSL https://bun.sh/install | bash

# 或使用 Homebrew
brew install oven-sh/bun/bun
```

**Windows 安装：**

```powershell
# 使用 PowerShell
powershell -c "irm bun.sh/install.ps1 | iex"

# 或使用 Scoop
scoop install bun

# 或使用 npm（需要先安装 Node.js）
npm install -g bun
```

**验证安装：**

```bash
bun --version
npx --version
```

---

### Python

**macOS 安装：**

```bash
# 方式 1：使用 Homebrew（推荐）
brew install python@3.11

# 方式 2：使用 pyenv（多版本管理）
brew install pyenv
pyenv install 3.11.7
pyenv global 3.11.7

# 方式 3：从官网下载
# 访问 https://www.python.org/downloads/macos/
```

**Windows 安装：**

```powershell
# 方式 1：从官网下载安装器（推荐）
# 访问 https://www.python.org/downloads/windows/
# ⚠️ 安装时勾选 "Add Python to PATH"

# 方式 2：使用 Chocolatey
choco install python --version=3.11

# 方式 3：使用 Scoop
scoop install python

# 方式 4：使用 Microsoft Store
# 搜索 "Python 3.11" 直接安装
```

**验证安装：**

```bash
python --version
# 或
python3 --version

# 检查 pip
pip --version
# 或
pip3 --version
```

**包管理器安装：**

```bash
# 安装 uv（现代化的 Python 包管理器）
curl -LsSf https://astral.sh/uv/install.sh | sh  # macOS/Linux
# 或
powershell -c "irm https://astral.sh/uv/install.ps1 | iex"  # Windows

# 安装 poetry（项目依赖管理）
curl -sSL https://install.python-poetry.org | python3 -  # macOS/Linux
# 或
(Invoke-WebRequest -Uri https://install.python-poetry.org -UseBasicParsing).Content | python -  # Windows
```

---

### C++

**macOS 安装：**

```bash
# 方式 1：安装 Xcode Command Line Tools（推荐）
xcode-select --install

# 方式 2：使用 Homebrew 安装 GCC
brew install gcc

# 方式 3：安装 LLVM
brew install llvm
```

**验证 macOS 安装：**

```bash
# 检查 Clang（Apple 默认编译器）
clang --version

# 检查 GCC（如果安装）
gcc --version
g++ --version

# 检查 CMake（构建工具）
brew install cmake
cmake --version
```

**Windows 安装：**

```powershell
# 方式 1：Visual Studio（推荐，包含 MSVC 编译器）
# 下载地址: https://visualstudio.microsoft.com/downloads/
# 安装时选择 "Desktop development with C++"

# 方式 2：Visual Studio Build Tools（仅命令行工具）
# 下载地址: https://visualstudio.microsoft.com/downloads/
# 选择 "Build Tools for Visual Studio 2022"

# 方式 3：MinGW-w64（GCC for Windows）
choco install mingw
# 或
scoop install gcc

# 方式 4：安装 Clang/LLVM
choco install llvm
```

**验证 Windows 安装：**

```powershell
# 检查 MSVC（Visual Studio）
cl  # 需要在 "Developer Command Prompt for VS" 中运行

# 检查 MinGW GCC
gcc --version
g++ --version

# 检查 Clang
clang --version

# 检查 CMake
choco install cmake  # 如果未安装
cmake --version
```

**C++ 构建工具：**

```bash
# CMake（跨平台构建工具）
# macOS
brew install cmake

# Windows
choco install cmake
# 或
scoop install cmake

# Ninja（快速构建系统）
# macOS
brew install ninja

# Windows
choco install ninja
# 或
scoop install ninja

# vcpkg（C++ 包管理器）
git clone https://github.com/Microsoft/vcpkg.git
cd vcpkg
./bootstrap-vcpkg.sh  # macOS/Linux
# 或
./bootstrap-vcpkg.bat  # Windows
```

---

### Java

**macOS 安装：**

```bash
# 方式 1：使用 Homebrew 安装 OpenJDK（推荐）
brew install openjdk@17

# 配置环境变量（添加到 ~/.zshrc 或 ~/.bash_profile）
echo 'export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc

# 方式 2：使用 SDKMAN（多版本管理，推荐）
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk install java 17.0.10-tem  # Temurin JDK 17
sdk default java 17.0.10-tem

# 方式 3：从 Oracle 官网下载
# 访问 https://www.oracle.com/java/technologies/downloads/

# 方式 4：安装 Azul Zulu JDK
brew tap mdogan/zulu
brew install zulu-jdk17
```

**Windows 安装：**

```powershell
# 方式 1：使用 Chocolatey 安装 OpenJDK（推荐）
choco install openjdk17

# 方式 2：使用 Scoop 安装
scoop bucket add java
scoop install openjdk17

# 方式 3：使用 Microsoft Build of OpenJDK
# 下载地址: https://www.microsoft.com/openjdk

# 方式 4：从 Oracle 官网下载安装器
# 访问 https://www.oracle.com/java/technologies/downloads/
# ⚠️ 安装后需手动配置 JAVA_HOME 环境变量

# 方式 5：使用 SDKMAN（需要 Git Bash 或 WSL）
# 在 Git Bash 中运行
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk install java 17.0.10-tem
```

**配置 Windows 环境变量（手动安装时）：**

```powershell
# 1. 设置 JAVA_HOME（以管理员身份运行 PowerShell）
[System.Environment]::SetEnvironmentVariable('JAVA_HOME', 'C:\Program Files\Java\jdk-17', 'Machine')

# 2. 添加到 PATH
$path = [System.Environment]::GetEnvironmentVariable('Path', 'Machine')
[System.Environment]::SetEnvironmentVariable('Path', "$path;%JAVA_HOME%\bin", 'Machine')

# 3. 重启终端使其生效
```

**验证安装：**

```bash
# 检查 Java 版本
java -version

# 检查 Javac 编译器
javac -version

# 检查 JAVA_HOME
echo $JAVA_HOME  # macOS/Linux
echo %JAVA_HOME%  # Windows CMD
echo $env:JAVA_HOME  # Windows PowerShell
```

**Java 构建工具：**

```bash
# Maven（项目管理和构建工具）
# macOS
brew install maven

# Windows
choco install maven
# 或
scoop install maven

# 验证
mvn -version

# Gradle（现代构建工具）
# macOS
brew install gradle

# Windows
choco install gradle
# 或
scoop install gradle

# 验证
gradle -version

# jEnv（多版本 Java 管理，macOS/Linux）
brew install jenv
echo 'export PATH="$HOME/.jenv/bin:$PATH"' >> ~/.zshrc
echo 'eval "$(jenv init -)"' >> ~/.zshrc
jenv add /path/to/java/home
jenv versions
```

---

## OpenCode

> **重要**：OpenCode 是使用 OMO 的必要基础，也支持 Superpowers 等框架的安装。

### 前置要求

- Node.js v20+ 或 Bun
- 至少一个 AI 订阅（Claude Pro/Max、ChatGPT Plus、Gemini 或 GitHub Copilot）

### 安装步骤

**方式 1：使用安装脚本（推荐）**

```bash
# macOS / Linux
curl -fsSL https://opencode.ai/install.sh | bash

# Windows (PowerShell)
curl -fsSL https://opencode.ai/install | bash
```

**方式 2：使用包管理器**

```bash
# 使用 npm
npm install -g @opencode/cli

# 使用 Bun（推荐，速度更快）
bun install -g @opencode/cli
```

### 初始化配置

```bash
# 初始化 OpenCode
opencode init

# 根据提示配置 AI 模型订阅
# 可选：Claude API、OpenAI API、Gemini API、GitHub Copilot
```

### 验证安装

```bash
opencode --version
opencode help
```

---

## Spec-Kit

### 安装步骤

```bash
# 1. 安装 uv 包管理器 (要求 Python 3.11+)
curl -LsSf https://astral.sh/uv/install.sh | sh  # macOS/Linux
# 或
powershell -c "irm https://astral.sh/uv/install.ps1 | iex"  # Windows

# 2. 全局安装 Spec-Kit CLI
uv tool install specify-cli --from git+https://github.com/github/spec-kit.git

# 3. 初始化项目 (在项目目录下执行)
specify init

# 4. 验证安装
specify check
```

---

## OpenSpec

### 安装步骤

```bash
# 1. 全局安装 (要求 Node.js 20.19+)
npm install -g @fission-ai/openspec@latest

# 2. 初始化项目 (在项目目录下执行)
openspec init

# 3. 验证安装
openspec --version
```

---

## BMAD 框架

### 前置要求

- Node.js v20+
- AI IDE（Claude Code、Cursor、Windsurf 等）

### 安装步骤

```bash
# 安装命令
npx bmad-method install
```

按照安装向导提示完成配置，然后在项目目录中打开 AI IDE。

### 验证安装

```bash
# 在 AI IDE 会话框中运行
/bmad-help
```

成功后会看到完整的命令列表和使用指南。

---

## Superpowers 框架

### 前置要求

- Claude Code 或 OpenCode
- Git 跟踪的项目目录

### Claude Code 安装

```bash
# 1. 注册插件市场
/plugin marketplace add obra/superpowers-marketplace

# 2. 从市场安装插件
/plugin install superpowers@superpowers-marketplace
```

### OpenCode

#### 基于 opencode 安装,直接输入一下内容

```shell
Clone https://github.com/obra/superpowers to ~/.config/opencode/superpowers, then create directory ~/.config/opencode/plugins, then symlink ~/.config/opencode/superpowers/.opencode/plugins/superpowers.js to ~/.config/opencode/plugins/superpowers.js, then symlink ~/.config/opencode/superpowers/skills to ~/.config/opencode/skills/superpowers, then restart opencode.
```

#### macOS / Linux 手动安装

```bash
# 1. 安装或更新 Superpowers
if [ -d ~/.config/opencode/superpowers ]; then
  cd ~/.config/opencode/superpowers && git pull
else
  git clone https://github.com/obra/superpowers.git ~/.config/opencode/superpowers
fi

# 2. 创建目录
mkdir -p ~/.config/opencode/plugins ~/.config/opencode/skills

# 3. 删除旧的符号链接（如果存在）
rm -f ~/.config/opencode/plugins/superpowers.js
rm -rf ~/.config/opencode/skills/superpowers

# 4. 创建符号链接
ln -s ~/.config/opencode/superpowers/.opencode/plugins/superpowers.js ~/.config/opencode/plugins/superpowers.js
ln -s ~/.config/opencode/superpowers/skills ~/.config/opencode/skills/superpowers

# 5. 重启 OpenCode
```

#### Windows（PowerShell，以管理员身份运行或启用开发者模式） 手动安装

```powershell
# 1. 安装 Superpowers
git clone https://github.com/obra/superpowers.git "$env:USERPROFILE\.config\opencode\superpowers"

# 2. 创建目录
New-Item -ItemType Directory -Force -Path "$env:USERPROFILE\.config\opencode\plugins"
New-Item -ItemType Directory -Force -Path "$env:USERPROFILE\.config\opencode\skills"

# 3. 删除旧的链接（如果存在）
Remove-Item "$env:USERPROFILE\.config\opencode\plugins\superpowers.js" -Force -ErrorAction SilentlyContinue
Remove-Item "$env:USERPROFILE\.config\opencode\skills\superpowers" -Force -ErrorAction SilentlyContinue

# 4. 创建插件符号链接（需要开发者模式或管理员权限）
New-Item -ItemType SymbolicLink -Path "$env:USERPROFILE\.config\opencode\plugins\superpowers.js" -Target "$env:USERPROFILE\.config\opencode\superpowers\.opencode\plugins\superpowers.js"

# 5. 创建 skills 目录连接（无需特殊权限）
New-Item -ItemType Junction -Path "$env:USERPROFILE\.config\opencode\skills\superpowers" -Target "$env:USERPROFILE\.config\opencode\superpowers\skills"

# 6. 重启 OpenCode
```

#### Windows（Command Prompt，以管理员身份运行或启用开发者模式）

```cmd
:: 1. 安装 Superpowers
git clone https://github.com/obra/superpowers.git "%USERPROFILE%\.config\opencode\superpowers"

:: 2. 创建目录
mkdir "%USERPROFILE%\.config\opencode\plugins" 2>nul
mkdir "%USERPROFILE%\.config\opencode\skills" 2>nul

:: 3. 删除旧的链接（如果存在）
del "%USERPROFILE%\.config\opencode\plugins\superpowers.js" 2>nul
rmdir "%USERPROFILE%\.config\opencode\skills\superpowers" 2>nul

:: 4. 创建插件符号链接（需要开发者模式或管理员权限）
mklink "%USERPROFILE%\.config\opencode\plugins\superpowers.js" "%USERPROFILE%\.config\opencode\superpowers\.opencode\plugins\superpowers.js"

:: 5. 创建 skills 目录连接（无需特殊权限）
mklink /J "%USERPROFILE%\.config\opencode\skills\superpowers" "%USERPROFILE%\.config\opencode\superpowers\skills"

:: 6. 重启 OpenCode
```

#### Windows（Git Bash）

```bash
# 1. 安装 Superpowers
git clone https://github.com/obra/superpowers.git ~/.config/opencode/superpowers

# 2. 创建目录
mkdir -p ~/.config/opencode/plugins ~/.config/opencode/skills

# 3. 删除旧的链接（如果存在）
rm -f ~/.config/opencode/plugins/superpowers.js 2>/dev/null
rm -rf ~/.config/opencode/skills/superpowers 2>/dev/null

# 4. 创建插件符号链接（需要开发者模式或管理员权限）
cmd //c "mklink \"$(cygpath -w ~/.config/opencode/plugins/superpowers.js)\" \"$(cygpath -w ~/.config/opencode/superpowers/.opencode/plugins/superpowers.js)\""

# 5. 创建 skills 目录连接（无需特殊权限）
cmd //c "mklink /J \"$(cygpath -w ~/.config/opencode/skills/superpowers)\" \"$(cygpath -w ~/.config/opencode/superpowers/skills)\""

# 6. 重启 OpenCode
```

### 验证安装

```bash
# macOS / Linux
ls -l ~/.config/opencode/plugins/superpowers.js
ls -l ~/.config/opencode/skills/superpowers

# Windows PowerShell
Get-ChildItem "$env:USERPROFILE\.config\opencode\plugins" | Where-Object { $_.LinkType }
Get-ChildItem "$env:USERPROFILE\.config\opencode\skills" | Where-Object { $_.LinkType }

# 在 OpenCode 会话中使用 skill 工具列出所有技能
use skill tool to list skills
```

---

## 📚 参考资料

- [OpenCode 官方文档](https://opencode.ai/docs)
- [Spec-Kit GitHub](https://github.com/github/spec-kit)
- [OpenSpec GitHub](https://github.com/fission-codes/openspec)
- [BMAD 官方文档](https://github.com/bmad-code-org/BMAD-METHOD)
- [Superpowers 官方文档](https://github.com/obra/superpowers)

---

**版本**: v1.0  
**更新日期**: 2025-02-09  
**说明**: 本指南仅包含安装步骤，使用方法请参考完整操作手册
