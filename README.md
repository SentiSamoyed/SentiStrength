# SentiStrength

## 简介

### 什么是 SentiStrength?

- 它为 Mike Thelwall 等人根据 MySpace 网站数据开发的社交文本情绪分析工具；
- Mike Thelwall 等人最先于 2010 年的发表的论文 [_Sentiment Strength Detection in Short Informal Text_](https://doi.org/10.1002/asi.21416) 中提出了该工具；后来，他们对更多种类的社交文本进行了探索，并形成了论文 [_Sentiment Strength Detection for the Social Web_](https://doi.org/10.1002/asi.21662) 于 2012 年发表。若您想细致的了解该工具，推荐优先阅读 _Sentiment Strength Detection in Short Informal Text_ ，其中的描述更为详细。
- 该工具的官网地址为：http://sentistrength.wlv.ac.uk；
- 官网提供了该工具的原版 jar 包，各种使用手册，以及可以试运行的 demo 等。除此之外，它还罗列了与该工具有关的若干论文，并提供了工具开发过程中标注的数据集。若有任何疑问，可优先查看其官网。
- **该项目由反编译官网发布的 jar 包得来。**

### 我们在做什么？

> **EASIEST** (听着很简单但写起来很长的名字)
>
> Sentiment Analysis and Related Application 
>
> ​	on Software Engineering Texts 
>
> ​		from Collaborative Social Networks

将 SentiStrength 改造为适合软工情绪文本分析的工具，并：

- 为 SentiStrength 代码添加注释，使其有更好的可读性；
- 分析 SentiStrength 的项目需求；
- 优化 SentiStrength 的代码，排出潜在的 bug，对原有设计进行升级重构；
- 为 SentiStrength 设计全新的 Web 前端；
- 优化 SentiStrength 的运行效果
- and more...

## 构建与运行

目前项目通过 Gradle 进行构建。

IDEA 打开后需要先添加为 gradle project.

两种运行方法：

- IDEA 侧面板的 `Tasks -> application -> run`
- 命令行 `gradle run`

> 带参数运行：`gradle run --args="your args"`