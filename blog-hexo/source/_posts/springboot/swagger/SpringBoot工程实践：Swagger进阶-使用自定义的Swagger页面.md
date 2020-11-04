---
title: SpringBoot工程实践：Swagger进阶-使用自定义的Swagger页面
date: 2020-01-16 23:22:27
tags:
- swagger
categories:
- springboot
author: KimZing
type: 原创
toc: true
---

## 一、简介

 Swagger的使用网上已经有很多的介绍了，这里就不重复的太多，但是Swagger官方的UI界面不是太直观，偶然发现一个开源项目[knife4j](https://gitee.com/xiaoym/knife4j), 在此感谢作者的奉献。这个项目没有使用Swagger官方的UI界面，重新定义了一套符合国人使用习惯的UI,感觉不错，就拿来用用。但是这个项目的文档有点乱，特别是刚出了`2.0.1`版本，没有找到详细的使用文档，所以根据项目的源码做了一些摸索，记录于此。

<!-- more -->

## 一、环境准备-SpringBoot

```xml
<!--可以只引入该包，这个包包含了所有依赖，暴露/doc.html页面-->
<dependency>
    <groupId>com.github.xiaoymin</groupId>
    <artifactId>knife4j-spring-boot-starter</artifactId>
    <version>2.0.1</version>
</dependency>
<!--可选，引入后，原/swagger-ui.html提供的页面仍可正常使用-->
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger-ui</artifactId>
    <version>2.9.2</version>
</dependency>
```

## 二、Swagger配置

### 1. 编写Swagger属性类

这样可以通过配置文件对Swagger进行配置，简单省事，不用改代码

```java
@Data
@ConfigurationProperties(prefix = "swagger")
public class SwaggerProperties {

    /**
     * 标题
     */
    private String title;

    /**
     * 文档描述
     */
    private String description;

    /**
     * 项目路径
     */
    private String termsOfServiceUrl;

    /**
     * 作者
     */
    private String authorName;

    /**
     * 邮箱
     */
    private String authorEmail;

    /**
     * 作者主页
     */
    private String authorUrl;

    /**
     * 版本
     */
    private String version;

    /**
     * 扫描的路径
     */
    private String basePackage;

}
```

### 2. 编写Swagger配置类

```java
// 声名为配置类
@Configuration
// 开启Swagger
@EnableSwagger2
// 导入Swagger属性类
@EnableConfigurationProperties(value = SwaggerProperties.class)
// 开启Knife4j的扩展功能， 如果没有需要，可以不开启
@EnableKnife4j
@Import(BeanValidatorPluginsConfiguration.class)
public class SwaggerConfiguration {

    @Bean
    public Docket createRestApi(SwaggerProperties swaggerProperties) {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(
                        new ApiInfoBuilder()
                                .title(swaggerProperties.getTitle())
                                .description(swaggerProperties.getDescription())
                                .termsOfServiceUrl(swaggerProperties.getTermsOfServiceUrl())
                                .contact(new Contact(swaggerProperties.getAuthorName(),
                                        swaggerProperties.getAuthorUrl(),
                                        swaggerProperties.getAuthorEmail()))
                                .version(swaggerProperties.getVersion())
                                .build())
                .select()
                .apis(RequestHandlerSelectors.basePackage(swaggerProperties.getBasePackage()))
                .paths(PathSelectors.any())
                .build();
    }

}
```

### 3. 配置文件

```yaml
# Swagger配置
swagger:
  title: SpringBoot Web Swagger使用示例
  description: swagger使用示例
  termsOfServiceUrl: http://localhost:8080/api
  authorName: kimzing
  authorEmail: kimzing@163.com
  authorUrl: http://kimzing.com
  version: 1.0.0
  basePackage: com.kimzing.web.controller

# knife开源的swagger ui配置
knife4j:
  # 是否是生产环境，如果是生产环境会默认关闭swagger
  production: false
  # 配置自定义markdown文件的位置
  markdowns: classpath:markdown/*
  # 配置认证功能
  basic:
    # 是否开启认证
    enable: true
    # 用户名
    username: admin
    # 密码
    password: 123456
```



## 三、Swagger原生注解

### 1. 注解说明

Swagger的使用注解有很多，这里我们只讲最常用的注解，以及这些注解中最常用的属性。

- @Api(tags = {"用户操作"})
    - 加在controller类上
    - tags表示该类的标签，在页面会独立显示一个菜单
- @ApiOperation(value = "保存用户", notes = "保存时，ID由数据库生成，无需填写，有则忽略", tags = "保存")
    - 加在相应的请求处理方法上
    - value表示该方法的说明
    - notes相当于对该方法的详细说明，也就是更加完整的描述
    - tags 表示标签，，在页面会独立显示一个菜单
- @ApiImplicitParam(name = "id", value = "用户ID", defaultValue = "1")
    - 方法只有一个基本类型参数时加在方法上。方法有多个参数时加在@ApiImplicitParams内
    - name 参数中属性的名字
    - value 对这个属性的描述
    - defaultValue 默认值，这个还是有必要填写的，在页面进行请求时，会自动填充
- @ApiImplicitParams(value = {})
    - 用在请求方法上
    - 这个注解必须和@ApiImplicitParam配合使用
    - 当请求方法中的请求参数很多的时候，例如saveUser(String username, Integer age, Date birthday, String phone)
- @ApiParam(value = "当前页", defaultValue = "1")
    - 加在请求方法的普通参数上
    - value的值是对该参数的说明
    - **与@ApiImplicitParam使用的效果等同，根据个人喜好进行使用**
- @ApiModel(value = "用户信息")
    - 加在请求方法的**对象类**上
    - value 对该对象参数的描述
    - 例如有一个请求方法save(UserDTO userDTO), 则需要加在UserDTO这个类上面(可以参照下面的示例)
- @ApiModelProperty(value = "用户ID", example = "1")
    - 加在请求方法的参数对象的属性上
    - value 对该属性的描述
    - example 属性的示例值，**在页面会自动填充该值**
    
### 2. 使用示例

```java
@Api(tags = {"用户操作"})
@RestController
@RequestMapping(value = "/user")
public class UserController {

    @PostMapping
    @ApiOperation(value = "保存用户", notes = "保存时，ID由数据库生成，无需填写，有则忽略", tags = "保存")
    public ApiResult save(@RequestBody UserDTO userDTO) {
        return ApiResult.success();
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除用户", notes = "删除后无法恢复", tags = "删除")
    @ApiImplicitParam(name = "id", value = "用户ID", defaultValue = "1")
    public ApiResult remove(@PathVariable Long id) {
        return ApiResult.success();
    }

    @PutMapping
    @ApiOperation(value = "更新用户", notes = "id必填，其它属性存在则更新，否则忽略", tags = "更新")
    public ApiResult update(@RequestBody UserDTO userDTO) {
        return ApiResult.success();
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "查找用户", notes = "根据id查找单个用户", tags = "查找")
    public ApiResult find(@PathVariable @ApiParam(value = "用户ID", defaultValue = "2") Long id) {
        return ApiResult.success();
    }

    @GetMapping("/list")
    @ApiOperation(value = "查找用户列表", notes = "根据id查找单个用户", tags = "查找")
    public ApiResult list(@RequestParam @ApiParam(value = "当前页", defaultValue = "1") Integer pageNum,
                          @RequestParam @ApiParam(value = "页大小", defaultValue = "10") Integer pageSize) {
        return ApiResult.success();
    }

}


@Data
@ApiModel(value = "用户实体")
public class UserDTO {

    @ApiModelProperty(value = "用户ID", example = "1", required = false)
    private Long id;

    @ApiModelProperty(value = "用户名", example = "rose", required = true)
    private String username;

    @ApiModelProperty(value = "用户密码", example = "123456", required = true)
    private String password;

    @ApiModelProperty(value = "用户年龄", example = "18", allowableValues = "range[1, 150]", required = false)
    private Integer age;

    @ApiModelProperty(value = "用户性别", example = "MAN", required = true)
    private GenderEnum gender;

}
```

页面效果示例

![示例](
http://oss.kimzing.com/blog/swagger.png?x-oss-process=style/KimZing)

## 四、扩展功能之认证与生产环境配置

默认这些页面是任何人都可以访问的，这样还是不太安全的，而且很多时候生产环境我们并不需要该文档，所以[knife4j](https://gitee.com/xiaoym/knife4j)对认证和生产进行了可选配置，核心配置如下

如果`knife4j.production`为true则会关闭Swagger页面。

如果配置了`basic.eable`为true,则访问`/doc.html`会需要进行basic认证

```yaml
knife4j:
  # 是否是生产环境，如果是生产环境会默认关闭swagger
  production: false
  # 配置认证功能
  basic:
    # 是否开启认证
    enable: true
    # 用户名
    username: admin
    # 密码
    password: 123456
```

## 五、扩展功能之编写自定义的文档

有时候我们希望在页面中能够编写一些自定义的文档来说明某些事项，这个时候就可以用到该功能，



### 1. 首先做如下配置：

```yaml
knife4j:
  # 配置自定义markdown文件的位置，可自己指定
  markdowns: classpath:markdown/*
```

### 2. 编写md文件

在项目的resources目录下建立`markdown`，放入编写好的md文件即可。

### 3. 打开配置

随后在页面中勾选`文档管理->个性化设置->启用Knife4j提供的增强功能`，刷新页面即可看到，如下示例

![](http://oss.kimzing.com/blog/swagger-md.png?x-oss-process=style/KimZing)



**参考项目**  [springboot-web](https://github.com/KimZing/springboot-web/tree/feature/swagger)

