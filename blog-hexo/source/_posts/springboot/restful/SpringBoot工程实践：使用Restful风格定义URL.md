---
title: SpringBoot工程实践：使用Restful风格定义URL
date: 2020-01-14 13:43:57
tags:
- restful
categories:
- springboot
author: KimZing
type: 原创
toc: true
---

## 一、简介

​       **首先restful只是一种风格，并不是具体的某项技术或框架**。就好比我们的坐姿，没使用restful之前，我们会翘着二郎腿，歪着头，而使用restful之后，就要求我们抬头，挺背，端正的坐着。不用restful我们就不可以坐着嘛？当然不是的，但是我们的精神面貌是不同的。


<!-- more -->

​		从代码角度来说，不用restful也可以进行正常开发，但是写出来的url的质量就参差不齐了，不利于统一管理，不能一看就懂。


## 二、传统url的理解

在以前的url中，我们主要使用get和post方法进行对服务器资源的请求。在数据量不是很大的时候使用get拼接参数请求，有一定数据量或者需要**一定程度**保证数据安全的时候使用post。

在这一点上，以前的方式中，get和post是没有本质区别的，都是用来发送请求，只是请求数据的封装不一样，get是在url上，而post是在请求的body中。这样导致没有统一的规范，每个人写出来的风格各有不同，导致项目接口很乱。如下的接口格式不言而喻。

- /user/get/1
- /findUser?id=1
- /DeleteUser?name=rose

## 三、资源和行为

使用传统的交互方式上，我们没有使用到资源和行为的相关概念。

(规范的)请求应该包含资源和行为的，那什么是资源和行为呢？

- 资源
    - 无论我们请求什么接口，返回的都是资源，可以理解为返回的html，json,xml等都是资源，那么我们的url就是对该资源的定位。

- 行为
    - 行为可以理解为我们要对这个资源做什么，我们通过url已经可以指向这个资源了，那么我们怎么表述对这个资源的操作呢？例如怎么删除、获取这个资源呢?其实这就是行为。在http的方法请求中，我们将post/delete/put/get/方法对应为对资源的增删改查。

例如：

- 使用put方法请求/user/1,就是将id为1的用户进行修改
- 使用get方法请求/user/1,就是获取id为1的用户的详情
- 使用get方法请求/user/name/{king}/age/{12},就是获取用户名为king,年龄为12的用户
- 使用post方法请求/user,就是要添加一个user
- 使用delete方法请求/user/1,就是将id为1的用户删除


## 四、rest风格

​		先看下我们没有使用restful之前的url的风格

| 操作 | url               | 请求方法 | 方法名                  | 说明                 |
| ---- | ----------------- | -------- | ----------------------- | -------------------- |
| C    | /saveUser         | post     | saveUser()/insertUser() | 新增用户             |
| R    | /getUserList      | get      | getUserList()           | 查询用户(分页，条件) |
| R    | /userDetail?id=1  | get      | getUserDetail()         | 获取单个用户详情     |
| U    | /updateUser?id=1  | post     | updateUser()            | 更新用户             |
| D    | /deleteUser/?id=1 | post     | removeUser()/deleteUser | 删除用户             |

​		那么如果我们使用restful的风格该是什么样子的？如下

| 操作 | url                        | 请求方法 | 方法名                  | 说明                           |
| ---- | -------------------------- | -------- | ----------------------- | ------------------------------ |
| C    | /user                      | post     | saveUser()/insertUser() | 新增用户                       |
| R    | /user                      | get      | listUser()              | 查询用户(分页，条件)           |
| R    | /user/1                    | get      | getUser()               | 获取单个用户详情               |
| R    | /user/name/{king}/age/{12} | get      | getUserByNameAndAge()   | 获取用户名为king年龄为12的用户 |
| U    | /user/1                    | put      | updateUser()            | 更新用户                       |
| D    | /user/1                    | delete   | removeUser()/deleteUser | 删除用户                       |

## 五、我们该怎么定义url

​		关于这一点，个人觉得要理解好资源的含义才能更好的定义url。例如user就是一个资源，那么对应的post、put、delete、get方法进行增删改查操作，传的参数简单且非安全那么就可以使用pathVariable。

​		风格规定是死的，项目中才能找到适合自己的一套最佳实践。



## 六、复杂的参数查询该使用什么方式呢？

​		有使用restful定义url经验的朋友经常会遇到一个问题点，查询资源时使用get方法，但如果参数过多则需要进行大量参数的拼接例如`/user/name/{name}/age/{age}...`,`/user?name=name&age=age&...`这样很明显是不够优雅的，那么该如何做更加合适呢？这里推荐两种方式

### 1. 使用post方式代替get方式

​		将参数封装为json body进行传输，后端直接解析为对应的查询对象即可。好处是简单省事，而坏处就是破坏了restful的风格，个人不太喜欢这种方式处理。

### 2. 使用json格式的字符串拼接到url后进行传参

​		例如: `/user?{"usernme":"rose","age":18,"gender":"MAN"}`, 这样就优雅了许多。但无论是springmvc还是其它web框架一般都不支持此种模式，所以这样做麻烦的地方就在于我们需要自己进行参数解析。

​		作者已经针对在springboot中使用此种方式做了解析封装，参考[在Get请求使用Json进行传参](https://github.com/KimZing/springboot-starter/blob/master/base-springboot-starter/doc/learn.md), 只需要在对应的方法参数上加上@JsonParam注解即可自动解析到该参数 上。

```java
// GET list?{"pageNum":1,"pageSize":10,"ageFrom":18,"ageTo":24}
@GetMapping("/list")
public ApiResult list(@JsonParam UserQueryDTO userQuery) {
    return userService.list(userQuery);
}
```

GitHub项目实例： [springboot-web](https://github.com/KimZing/springboot-web/tree/feature/restful)，欢迎star
