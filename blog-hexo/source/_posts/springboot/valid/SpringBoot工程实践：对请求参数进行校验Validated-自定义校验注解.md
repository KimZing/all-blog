---
title: SpringBoot工程实践：对请求参数进行校验Validated-自定义校验注解
date: 2020-01-15 18:43:00
tags:
- valid
- validated
categories:
- springboot
author: KimZing
type: 原创
toc: true
---

tst接口是对三方系统暴露的，这也就要求我们必需对参数的合法性进行校验，否则会引起各种不可预见的错误，最常见的就是NullPointException了。

<!-- more -->


接口是对三方系统暴露的，这也就要求我们必需对参数的合法性进行校验，否则会引起各种不可预见的错误，最常见的就是NullPointException了。

而如果在代码中使用if/else进行各种判断，十分的不优雅，健壮性代码侵入了业务代码，可读性就会变得很差。使用注解就可以很优雅的解决这个问题。

## 一、环境准备

SpringBoot项目

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

SpringMVC项目

```xml
<dependency>
    <groupId>org.hibernate.validator</groupId>
    <artifactId>hibernate-validator</artifactId>
    <version>6.0.18.Final</version>
</dependency>
```

## 二、开启校验

​        可以选择在Controller层或者Service层进行参数校验，controller里面各种注解已经够多了，个人还是喜好在Service的接口层进行参数校验，简单清爽。

​        开启校验的方式很简单，在对应的类或接口上加上`@Validated`注解，即可开启校验。

​       要使用校验需要不同的注解，每种注解支持的类型是不同的，可以打开源码进行查看，以`@Size`为例查看类注释中的`Supported types`

```java
/**
 * The annotated element size must be between the specified boundaries (included).
 * <p>
 * Supported types are:
 * <ul>
 *     <li>{@code CharSequence} (length of character sequence is evaluated)</li>
 *     <li>{@code Collection} (collection size is evaluated)</li>
 *     <li>{@code Map} (map size is evaluated)</li>
 *     <li>Array (array length is evaluated)</li>
 * </ul>
 * <p>
 * {@code null} elements are considered valid.
 *
 * @author Emmanuel Bernard
 */
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
@Retention(RUNTIME)
@Repeatable(List.class)
@Documented
@Constraint(validatedBy = { })
public @interface Size {
```

## 三、校验方法的普通参数

​        直接在对应的参数上加上相应的校验注解即可，一般会有两个注解属性`value`、`message`, value代表校验的边界，而message则代表当不符合该边界时，所提示的报错信息。如下示例

```java
@Validated
public interface UserService {

    ApiResult getByName(@NotBlank(message = "用户名不能为空") @Length(min = 1, max = 8, message = "用户名长度不符") String name);

    ApiResult getByAge(@Min(value = 1, message = "年龄起始范围不正确") Integer ageFrom, @Max(value = 150, message = "年龄结束范围不正确") Integer ageTo);

    ApiResult getByEmail(@Email(message = "邮箱格式不正确") String email);
}
```

## 四、校验方法中的对象参数

​       在需要校验的对象参数加上`@Valid`或者`@Validated`注解，然后在对象内部对应的属性上加上相应的校验注解即可(如果想要进行分组校验，就只能使用`@Validated`，下面会讲到)。如下示例

```java
@Validated
public interface UserService {

    ApiResult save(@Validated UserDTO userDTO);

    ApiResult update(@Valid UserDTO userDTO);

}


@Data
public class UserDTO {

    private Long id;

    /**
     * 根据正则校验手机号是否是由数字组成
     */
    @Pattern(regexp = "^\\d{11}$", message = "手机格式不正确,不是11位")
    private String telephone;

    /**
     * 校验该对象是否为null
     * 对于String来说，空字符串可通过校验，所以String应该使用@NotBlank进行校验，此处仅做示例而已。
     */
    @NotNull(message = "联系人不能为空")
    private String friendName;

    /**
     * 校验对象是否是空对象，可用于Array,Collection,Map,String
     */
    @NotEmpty(message = "家庭成员不能为空")
    private List families;

    /**
     * 校验长度，可以用于Array,Collection,Map,String
     */
    @Size(min = 4, max = 8, message = "用户名长度错误 by size")
    /**
     * 校验长度，只能用于String
     */
    @Length(min = 4, max = 8, message = "用户名长度错误 by length")
    private String username;

    /**
     * javax校验
     */
    @Max(value = 200, message = "年龄一般不会超过200 by max")
    @Min(value = 1, message = "年龄一般不能小于1 by min")
    /**
     * hibernate校验，效果等同
     */
    @Range(min = 0, max = 200, message = "年龄范围在0-200之间 by range")
    private Integer age;

    /**
     * 校验参数是否是False, 相反的是@AssertTrue
     */
    @AssertFalse(message = "用户初始化无需冻结")
    private Boolean lock;

    /**
     * String专用
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 12, message = "密码长度不对")
    private String password;

    /**
     * 时间必需是过去的时间
     */
    @Past(message = "生日只能为以前的时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime birth;

    /**
     * 校验Email
     */
    @Email(message = "邮件地址不正确")
    private String email;

}
```



## 五、分组校验

​        我们有一个UserDTO类，其中有id属性。当保存时，id不需要有值，由数据库自动生成。我们使用@Null注解校验。当更新时，id需要有值，根据ID去更新，我们使用@NotNull注解校验。

​       这个时候问题就来了，我们用的同一个UserDTO这就互相矛盾了，这个时候可以使用分组校验进行解决。

### 1. 定义分组类别

我们让SaveValidGroup和UpdateValidGroup继承了Default， 这样做的目的是当指定分组SaveValidGroup时,仍然会校验那些没有指定Group的校验属性。

否则就只会校验加了对应Group的属性。

```java
public interface SaveValidateGroup extends Default {}

public interface UpdateValidateGroup extends Default {}
```



### 2. 指定校验组

给每个校验注解指定groups属性，如果不指定则默认为javax.validation.groups.Default.class。

```java
@Validated
public interface UserService {
    
    ApiResult save(@Validated(value = SaveValidGroup.class) UserDTO userDTO);

    ApiResult update(@Validated(value = UpdateValidGroup.class) UserDTO userDTO);

}

@Data
public class UserDTO {

    @Null(message = "用户ID不为空", groups = SaveValidGroup.class)
    @NotNull(message = "用户ID为空", groups = UpdateValidGroup.class)
    private Long id;
    
}
```

## 六、自定义校验注解

有时候默认提供的校验注解无法满足我们的需要，我们需要自定义。

例如有一个String类型的gender属性，但其值只能有MAN/WOMAN/SECRET三种(用枚举更好，只是方便举例)，接下来编写我们自己的校验注解。

### 1. 创建校验注解

```
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = GenderValidate.class)
public @interface Gender {

    String message();

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

}
```
### 2.编写校验规则
校验规则也就是枚举Gender中指定的validateBy属性

```
public class GenderValidate implements ConstraintValidator<Gender, String> {

   @Override
   public void initialize(Gender gender) { }

   @Override
   public boolean isValid(String gender, ConstraintValidatorContext context) {
      return "MAN".equals(gender) || "WOMAN".equals(gender) || "SECRET".equals(gender);
   }

}
```



之后我们就可以像使用其它注解一样使用@Gender进行校验了。



## 七、关于@Valid和@Validated的区别联系

​         一直对@Valid和@Validated这两个注解非常疑惑，不知道怎么区分和使用。

1. 包位置

- @Valid: javax.validation， 是javax，也是就是jsr303中定义的规范注解
- @Validated: org.springframework.validation.annotation， 是spring自己封装的注解，并不是完全按照规范来的，可以看到类注释中`Spring's JSR-303 support but not JSR-303 specific.`。

2. 功能
@Valid就不用说了，是jsr303的规范。我们打开@Validated的源码，可以看到以下注释，

```
Variant of JSR-303's {@link javax.validation.Valid}, supporting the
specification of validation groups. Designed for convenient use with
Spring's JSR-303 support but not JSR-303 specific.
```
大致意思就是说@Validated是@Valid的一个变种，扩展了@Valid的功能，支持group分组校验的写法。
那么我们对于@Valid和@Validated就可以这么理解：
1. 能用@Valid的地方通常可以用@Validated替代。
2. 需要使用分组校验的时候使用@Validated注解。

## 八、项目参考

可以参考项目: [springboot-web](https://github.com/KimZing/springboot-web/tree/feature/validate),

项目中对校验信息做了封装，具体可以参考个人封装的[SpringBoot Starter](https://github.com/KimZing/springboot-starter/tree/master/base-springboot-starter)

欢迎Star~
