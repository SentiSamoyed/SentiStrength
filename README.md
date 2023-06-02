# SentiStrength

## 项目构成

- [***SentiStrength***](https://github.com/SentiSamoyed/SentiStrength)：SentiStrength 的核心部分与后端部分
- [***SentiStrength-FE***](https://github.com/SentiSamoyed/SentiStrength-FE)：SentiStrength 的前端部分
- [***Issue Tracker***](https://github.com/SentiSamoyed/IssueTracker)：SentiStrength 的 GitHub Issue 获取服务
- [***Process Documentation***](https://github.com/SentiSamoyed/ProcessDocumentation)：SentiStrength 开发过程文档

## 简介

### 什么是 SentiStrength?

- 它为 Mike Thelwall 等人根据 MySpace 网站数据开发的社交文本情绪分析工具；
- Mike Thelwall 等人最先于 2010 年的发表的论文 [_Sentiment Strength Detection in Short Informal
  Text_](https://doi.org/10.1002/asi.21416) 中提出了该工具；后来，他们对更多种类的社交文本进行了探索，并形成了论文 [
  _Sentiment Strength Detection for the Social Web_](https://doi.org/10.1002/asi.21662) 于 2012 年发表。若您想细致的了解该工具，推荐优先阅读
  _Sentiment Strength Detection in Short Informal Text_ ，其中的描述更为详细。
- 该工具的官网地址为：http://sentistrength.wlv.ac.uk；
- 官网提供了该工具的原版 jar 包，各种使用手册，以及可以试运行的 demo
  等。除此之外，它还罗列了与该工具有关的若干论文，并提供了工具开发过程中标注的数据集。若有任何疑问，可优先查看其官网。
- **该项目由反编译官网发布的 jar 包得来。**

### 我们在做什么？

> **EASIEST** (听着很简单但写起来很长的名字)
>
> Sentiment Analysis and Related Application
>
> ​ on Software Engineering Texts
>
> ​ from Collaborative Social Networks

将 SentiStrength 改造为适合软工情绪文本分析的工具，并：

- 为 SentiStrength 代码添加注释，使其有更好的可读性；
- 分析 SentiStrength 的项目需求；
- 优化 SentiStrength 的代码，排出潜在的 bug，对原有设计进行升级重构；
- 为 SentiStrength 设计全新的 Web 前端；
- 优化 SentiStrength 的运行效果
- and more...

## 构建与运行

目前项目通过 Gradle 进行构建；如用 IDEA 打开，需要先添加为 gradle project.

### 运行

```bash
> ./gradlew run [--args=<args...>]
```

### 构建

```bash
> ./gradlew build
> ./gradlew bootJar
```

Jar 会生成于 `build/libs`.

### 参数

SentiStrength 有两种运行模式，一种是和原版相同的普通运行，如：

```bash
> java -jar ./sentistrength-1.0.0.jar text i+love+you sentidata ../../src/SentiStrength_Data/                                                                           11:33:35
3 -1
```

具体参数可见 `help`.

还有一种是作为后端服务启动，使用方法：

```bash
Usage: ~ --web <Path to SentiStrength_Data>
```

比如：

```bash
java -jar ./sentistrength-1.0.0.jar --web ../../src/SentiStrength_Data/                                                                                               11:34:10

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
.....
```

## 服务端模式执行

从 2.1.0 版本开始，运行 SentiStrength-BE 需要先配置好相应的环境变量：

- 数据库地址+数据库 `DB_ADDRESS`，如 `localhost:3306/something`
- 数据库用户名 `DB_USER`
- 用户密码 `DB_PASSWORD`
- [Issue Tracker](https://github.com/SentiSamoyed/IssueTracker) 服务的地址 `TRACKER_URL`
  ，如 `http://localhost:8848/repo/`