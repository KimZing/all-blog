---
title: Hexo博客安装记录
date: 2020-01-02 16:25:40
tags:
- hexo
categories:
- hexo
author: KimZing
type: 原创
thumbnail: /gallery/flower.jpg
toc: true
---

简要介绍Hexo的安装与主题的切换。详细的安装方式与配置请参考[官方文档](https://hexo.io/zh-cn/docs/)

<!-- more -->

## 一、环境准备

- Node  >= 8.x 
- Git

## 二、安装Hexo(二选一)

### 全局安装

```bash
npm install -g hexo-cli
```

### 局部安装

```bash
# 创建一个目录作为工作区(位置名称随意)
mkdir temp
cd temp
# 在工作区中进行局部安装
npm install hexo
# 设置环境变量(在工作区目录可以直接使用hexo命令)
echo 'PATH="$PATH:./node_modules/.bin"' >> ~/.zshrc
source ~/.zshrc
```

## 三、创建站点

```bash
cd temp
hexo init blog-hexo  # blog-hexo为站点目录，名称随意
```

## 四、运行站点

```bash
# 进入站点目录
cd blog-hexo
# 运行站点并访问 默认: http://localhost:4000
hexo serve
```

## 五、安装配置主题(以icarus示例)

> icarus详细的配置可以参考[官方仓库](https://github.com/ppoffice/hexo-theme-icarus)

```bash
cd blog-hexo
# 使用git将主题下载到themes目录
git clone https://github.com/ppoffice/hexo-theme-icarus.git themes/icarus
```

编辑`blog-hexo/_config.yml`, 将`theme: landscape`修改为`theme: icarus`。

## 六、FAQ

1. 重新执行`hexo serve`命令，发现报错如下

```bash
ERROR Package cheerio is not installed.
ERROR Please install the missing dependencies from the root directory of your Hexo site.
```

原因缺少`cheerio`依赖，进入`blog-hexo`目录，执行`npm i cheerio -S`命令进行安装即可，`-S`指安装并将其保存到当前项目的配置中，下次就会统一安装了。 

2. 重新执行`hexo serve`命令，发现如下信息

```bash
INFO  Checking dependencies
INFO  Validating the configuration file
WARN  themes/icarus/_config.yml is not found. We are creating one for you...
INFO  themes/icarus/_config.yml is created. Please restart Hexo to apply changes.
```

只是提示缺少`themes/icarus/_config.yml`文件，已经帮助我们生成，再次运行即可正常启动。