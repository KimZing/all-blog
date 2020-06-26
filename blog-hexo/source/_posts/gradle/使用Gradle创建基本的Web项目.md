---
title: Gradle教程
date: 2020-3-25 19:42:24
tags:
- gradle
categories:
- 开发工具
author: KimZing
type: 原创
toc: true
---

通过Gretty插件内嵌jetty/tomcat容器启动Java web项目

<!-- more -->

## 一、环境要求

1. 一个好用的IDE编辑工具
2. Java环境(>=8)
3. Gradle构建工具(>=4.10.3)

## 二、创建应用结构

gradle使用`war`插件来进行java web项目构建，`war`插件继承了Java 插件并添加了对web应用程序的支持。默认情况下，使用`src/main/webapp`目录作为web的资源目录。 

为`webdemo`项目建立如下目录结构

```
webdemo/
    src/
        main/
            java/
            webapp/
        test
            java/
```

servlet或其他Java类都将放在`src / main / java`中，测试代码写在`src / test / java`，其他Web资源将放在`src / main / webapp`。

## 三、添加Gradle脚本

在项目根目录下创建一个`build.gradle`文件，内容如下：

```groovy
plugins {
    id 'war'  // ①
}

repositories {
    jcenter()
}

dependencies {
    providedCompile 'javax.servlet:javax.servlet-api:3.1.0' // ②
    testCompile 'junit:junit:4.12'
}
```

① 添加并使用war插件

② 引入servlet依赖包，版本为3.1.0或更高

war插件添加了providerCompile和providerRuntime，类似于常规Java应用程序中的compile和runtime。providerCompile表示本地运行时所需的依赖关系，但打包时不会将其添加到生成的webdemo.war文件中。

再添加war插件时，无需指定版本，war和jar两个插件的版本，gradle已经内置进行管理了。



## 四、编写Servlet

从3.0版本开始，可以使用注解来开发Servlet，下面就会使用这种方式。在`src/main/java`下创建包`org/gradle/demo`，添加servlet文件`HelloServlet.java`,内容如下: 

```java
package org.gradle.demo;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "HelloServlet", urlPatterns = {"/hello"}, loadStartup = 1) // ①
public class HelloServlet extends HttpServlet {
  
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    response.getWriter().print("Hello, World!");  // ②
  }
  
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    String name = request.getParameter("name");
    if (name == null) name = "World";
    request.setAttribute("user", name);
    request.getRequestDispatcher("response.jsp").forward(request, response); // ③
  }
  
}
```

①  基于注解的Servlet写法

② GET请求，返回基本字符串

③ POST请求，获取name参数并跳转到JSP页面



## 五、添加请求页面和JSP页面

首先在`src/main/webapp`下添加一个`index.html`文件，方便发送`GET`和`POST`请求。

`index.html`内容如下

```html
<html>
<head>
  <title>Web Demo</title>
</head>
<body>
<p>Say <a href="hello">Hello</a></p> 

<form method="post" action="hello">  
  <h2>Name:</h2>
  <input type="text" id="say-hello-text-input" name="name" />
  <input type="submit" id="say-hello-button" value="Say Hello" />
</form>
</body>
</html>
```



然后在`src/main/webapp`下添加一个`response.jsp`文件，也就是当接收到post请求时跳转的页面。`response.jsp`内容如下

```html
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <title>Hello Page</title>
    </head>
    <body>
        <h2>Hello, ${user}!</h2>
    </body>
</html>
```





## 六、添加`gretty`插件，以便于直接运行程序

[gretty](https://plugins.gradle.org/plugin/org.akhikhl.gretty)插件是社区支持的杰出插件，可以在Gradle插件存储库中找到。该插件支持在Jetty或Tomcat上运行或测试Web应用程序。

在`build.gradle`脚本的`plugins`中添加如下

```groovy
plugins {
    id 'war'
    id 'org.gretty' version '2.2.0' 
}
```



现在可以使用`appRun`任务运行我们的程序了，默认是使用Jetty容器。运行输出如下

```bash
$ gradle appRun
00:32:30 INFO  Jetty 9.2.24.v20180105 started and listening on port 8080
00:32:30 INFO  webdemo runs at:
00:32:30 INFO    http://localhost:8080/webdemo

> Task :appRun
Press any key to stop the server.
<===========--> 87% EXECUTING [10s]
> :appRun
```

出现http://localhost:8080/webdemo说明已经运行成功了, 访问这个地址出现index.html所呈现的页面，点击一下试试吧。

## 补充、切换Servlet容器及更多配置

gretty目前支持的容器如下

> Jetty
>
>  'jetty7', 'jetty8', 'jetty9', 'jetty93', 'jetty94'



> Tomcat
>
>  'tomcat7', 'tomcat8' 

只需要在`build.gradle`文件中添加如下block即可

```groovy
gretty {
  httpPort = 8083 // 端口
  contextPath = '/demo'  // 容器路径
  servletContainer = 'tomcat8' // 容器类型
}
```



