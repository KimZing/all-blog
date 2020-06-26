---
title: 入门到精通Java注解,这一篇就够了
date: 2020-04-06 00:31:10
tags:
- 注解
categories:
- Java
author: KimZing
type: 原创
toc: true
---

从注解的定义到使用以及原理，一步一步带你了解注解的真面目。

<!-- more -->

## 一、为什么要学注解？

​       在日常开发中，基本都是在使用别人定义或是各种框架的注解，比如Spring框架中常用的一些注解：`@Controller`、`@Service`、`@RequestMapping`，以此来实现某些功能，但是却不知道如何实现的，所以如果想学习这些框架的实现原理，那么注解就是我们必知必会的一个点。其次，可以利用注解来自定义一些实现，比如在某个方法上加一个自定义注解，就可以实现方法日志的自动记录打印，这样也可以展现足够的逼格。所以如果你想走上人生巅峰，更好的利用框架，又或者想要高一点的逼格，从团队中突出，那么学习注解都是前提。

## 二、注解是什么？

​        在Java中注解其实就是写在接口、类、属性、方法上的一个标签，或者说是一个特殊形式的注释，与普通的`//`或`/**/`注释不同的是：普通注释只是一个注释，而注解在**代码运行时**是可以被**反射**读取并进行相应的操作，而如果没有使用反射或者其他检查，那么注解是没有任何真实作用的，也不会影响到程序的正常运行结果。

​        举个例子`@Override`就是一个注解，它的作用是告诉阅读者(开发人员、编译器)这个方法重写了父类的方法，对于开发人员只是一个标志，而编译器则会多做一些事情，编译器如果发现方法标注了这个注解，就会检查这个方法到底是不是真的覆写了父类的方法，如果没有那就是在欺骗他的感情，甭废话，编译时直接给你报个错，不留情面的那种。而如果不添加`@Override`注解，程序也是可以正常运行的，不过缺乏了静态的检查，本来是想覆写父类的`hello`方法的，却写成了`he110`方法，这就会有些尴尬了。

> 在spring框架中加注的注解会影响到程序的运行，是因为spring内部使用反射操作了对应的注解。

​        上面的说法是为了方便理解的，那么下面来个稍微正式一点的：**注解是提供一种为程序元素设置元数据的方法**，理解起来还是一样的，程序元素就是指接口、类、属性、方法，这些都是属于程序的元素，那啥叫元数据呢？就是描述数据的数据(data about data)，举个简单的例子，系统上有一个`sm.png`文件，这个文件才是我们真正需要的数据本身，而这个文件的属性则可以称之为`sm.png`的元数据，是用来描述png文件的创建时间、修改时间、分辨率等信息的，这些信息无论是有还是没有都不影响它作为图片的性质，都可以使用图片软件打开。

- 元数据是添加到程序元素如方法、字段、类和包上的额外信息，注解就是一种载体形式
- 注解不能直接干扰程序代码的运行

<img src="http://images.kimzing.com/blog/元数据.png" style="align:left;height:400px">

## 三、为什么要使用注解？

​       以Spring为例，早期版本的Spring是通过XML文件的形式对整个框架进行配置的，一个缩减版的配置文件如下

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans">
    <!-- 配置事物管理器 -->
    <bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
        <property name="dataSource" ref="dataSource"/>
    </bean>
    <!-- 配置注解驱动事物管理 -->
    <tx:annotation-driven transaction-manager="transactionManager"/>
</beans>
```

​        在xml文件中可以定义Spring管理的Bean、事物切面等，话说当年非常流行xml配置的。优点呢就是整个项目的配置信息集中在一个文件中，从而方便管理，是**集中式的配置**。缺点也显而易见，当配置信息非常多的时候，配置文件会变得越来越大不易查看管理，特别是多人协作开发时会导致一定的相互干扰。

​       现在都提倡解耦、轻量化或者说微小化，那么注解就顺应了这一需求，各个包或模块在内部方法或类上使用注解即可实现指定功能，而且使用起来灰常方便，简单易懂。缺点呢就是不方便统一管理，如果需要修改某一类功能，则需要整体搜索逐个修改，是**分散式的存在各个角落**。

​       这里扩充一下，Spring注解替代了之前Spirng xml文件，是不是说spring的xml也是一种元数据呢？对的，spring的配置文件xml也是元数据的一种表现形式。**不过xml的方式是集中式的元数据，不需要和代码绑定的，而注解是一种分散式的元数据设置方式**。

## 四、注解的作用

​        作为 Java开发几乎都使用过一些框架，相信大家对注解的作用都是有所体会的，这里再啰嗦几句加深印象。根本来说注解就是一个注释标签。开发者的视角可以解读出这个类/方法/属性的作用以及该怎么使用，而从框架的视角则可以解析注解本身和其属性实现各种功能，编译器的角度则可以进行一些预检查(@Override)和抑制警告(@SuppressWarnings)等。

- 作为特定标记，用于告诉编译器一些信息
- 编译时动态处理，如动态生成代码
- 运行时动态处理，作为额外信息的载体，如获取注解信息

## 五、注解的分类

​        通常来说注解分为以下三类

- 元注解  --  java内置的注解，标明该注解的使用范围、生命周期等。
- 标准注解  --  Java提供的基础注解，标明过期的元素/标明是复写父类方法的方法/标明抑制警告。
- 自定义注解 -- 第三方定义的注解，含义和功能由第三方来定义和实现。

### 5.1 元注解

​        用于定义注解的注解，通常用于注解的定义上，标明该注解的使用范围、生效范围等。元XX 都代表最基本最原始的东西，因此，元注解就是最基本不可分解的注解，**我们不能去改变它只能使用它来定义*自定义的注解***。元注解包含以下五种： @Retention、@Target、@Documented、@Inherited、@Repeatable，其中最常用的是@Retention和@Target下面分别介绍一下这五种元注解。

#### @Retention   ★★★★★  

中文翻译为保留的意思，标明自定义注解的生命周期

```java
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Retention {
    RetentionPolicy value();
}
```

​        从编写Java代码到运行主要周期为`源文件`→ `Class文件` → `运行时数据`，@Retention则标注了自定义注解的信息要保留到哪个阶段，分别对应的value取值为`SOURCE` →`CLASS`→`RUNTIME`。

- SOURCE  源代码java文件，生成的class文件中就没有该信息了
- CLASS  class文件中会保留注解，但是jvm加载运行时就没有了
- RUNTIME 运行时，**如果想使用反射获取注解信息，则需要使用RUNTIME**，反射是在运行阶段进行反射的

示例：当RentionPolicy取值为SOURCE时，Class文件中不会保留注解信息，而取值为CLASS时，Class反编译文件中则保留了注解的信息

![](http://images.kimzing.com/blog/RetentionSource.png)

![](http://images.kimzing.com/blog/RetentionClass.png)

> 各个生命周期的用途： 

> 1. Source：一个最简单的用法，就是自定义一个注解例如@ThreadSafe，用来标识一个类时线程安全的，就和注释的作用一样，不过更引人注目罢了。
> 2. Class：这个有啥用呢？个人觉得主要是起到标记作用，还没有做实验，例如标记一个@Proxy，JVM加载时就会生成对应的代理类。
> 3. Runtime：反射实在运行阶段执行的，那么只有Runtime的生命周期才会保留到运行阶段，才能被反射读取，也是我们最常用的。

#### @Target   ★★★★★

中文翻译为**目标**，描述自定义注解的使用范围，允许自定义注解标注在哪些Java元素上(类、方法、属性、局部属性、参数...)

```java
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Target {
    ElementType[] value();
}
```

value是一个数组，可以有多个取值，说明同一个注解可以同时用于标注在不同的元素上。value的取值如下

| 值              | 说明                            |
| --------------- | ------------------------------- |
| TYPE            | 类、接口、注解、枚举            |
| FIELD           | 属性                            |
| MEHOD           | 方法                            |
| PARAMETER       | 方法参数                        |
| CONSTRUCTOR     | 构造函数                        |
| LOCAL_VARIABLE  | 局部变量(如循环变量、catch参数) |
| ANNOTATION_TYPE | 注解                            |
| PACKAGE         | 包                              |
| TYPE_PARAMETER  | 泛型参数  jdk1.8                |
| TYPE_USE        | 任何元素  jdk1.8                |

示例：自定义一个注解@MyAnnotation1想要用在类或方法上，就可以如下定义

```java
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface MyAnnotation {
    String description() default "";
}


@MyAnnotation
public class AnnotationTest {

    // @MyAnnotation   用在属性上则会报错
    public String name;

    @MyAnnotation
    public void test(){}

}
```



#### @Inherited   ★★

是否可以被标注类的子类继承。被@Inherited修饰的注解是具有继承性的，在自定义的注解标注到某个类时，该类的子类会继承这个自定义注解。这里需要注意的是**只有当子类继承父类的时候，注解才会被继承**，类实现接口，或者接口继承接口，都是无法获得父接口上的注解声明的。正确的示例如下(通过反射获取注解)

![](http://images.kimzing.com/blog/20200403004522.png)

#### @Repeatable ★★

是否可以重复标注。这个注解其实是一个语法糖，jdk1.8之前也是有办法进行重复标注的，就是使用数组属性（自定义注解会讲到）。下面给一个例子，虽然我们标注的是多个@MyAnnotation，其实会给我们返回一个@MyAnnotations，相当于是Java帮我们把重复的注解放入了一个数组属性中，所以只是一个语法糖而已。

<img src="/Users/kimzing/Desktop/20200403010109.png" style="zoom:80%;" />

#### @Documented   ★

是否在生成的JavaDoc文档中体现，被标注该注解后，生成的javadoc中，会包含该注解，这里就不做演示了。

### 5.2 标准注解

标准注解有一下三个

- @Override  标记一个方法是覆写父类方法
- @Deprecated  标记一个元素为已过期，避免使用

​       支持的元素类型为：CONSTRUCTOR, FIELD, LOCAL_VARIABLE, METHOD, PACKAGE, PARAMETER, TYPE

- @SuppressWarnings  不输出对应的编译警告

比较常用且固定，下面给出一个简单示例

```java
@SuppressWarnings(value = {"unused", "rawtypes"})
public class StandardDemo extends Parent{

    @Override
    public void sayHello() {
        // unused 声明了list却没有使用
        // rawtypes 创建了泛型类却没有指定元素类型
        List list = new ArrayList();
    }

    @Deprecated
    public void walk() {

    }

}
```



### 5.3 自定义注解

注解定义格式

```java
public @interface 注解名 {
  修饰符 返回值 属性名() 默认值;
  修饰符 返回值 属性名() 默认值;
}
```

​        首先注解的修饰符一般是public的，定义注解一般都是要给三方使用的，不是public的又有什么意义呢？定义的类型使用`@interface`，可以猜出来和接口是有一些说不清道不明的关系的，其实注解就是一个接口，在程序运行时，JVM会为其生成对应的代理类。

​        然后内部的定义，这个有点四不像，说是方法吧它还有一个默认值，说它是属性吧它的后面还加了一个括号，我个人还是喜欢称之为**带默认返回值的接口方法**，通过后面的学习我们会进一步认识它的真面目。内部的修饰符只能是public的，即使不写也默认是public的，因为它本质上就是一个接口，而接口方法的默认访问权限就是pubilc的。

​       **注解是不能继承也不能实现其他类或接口的**，本身就是一个元数据了，确实没什么必要。

​      返回值支持的类型如下

- 基本类型 int float boolean byte double  char logn short
- String
- Class
- Enum
- Annotation
- 以上所有类型的数组类型

定义一个简单的接口示例

```java
// 保留至运行时
@Retention(RetentionPolicy.RUNTIME)
// 可以加在方法或者类上
@Target(value = {ElementType.TYPE,ElementType.METHOD})
public @interface RequestMapping {
    public String method() default "GET";
    public String path();
    public boolean required();
}
```

接下来我们来看下它到底是不是一个接口

首先编译一下该注解`javac RequestMapping.java `生成对应的`RequestMapping.class`文件，然后对其进行反编译`javap -v RequestMapping `,输出如下

```java
// ...
①public interface RequestMapping extends java.lang.annotation.Annotation
  //...
  ②public abstract java.lang.String method();
    descriptor: ()Ljava/lang/String;
    flags: ACC_PUBLIC, ACC_ABSTRACT
    AnnotationDefault:
      ③default_value: s#7
  public abstract java.lang.String path();
    descriptor: ()Ljava/lang/String;
    flags: ACC_PUBLIC, ACC_ABSTRACT

  public abstract boolean required();
    descriptor: ()Z
    flags: ACC_PUBLIC, ACC_ABSTRACT
}
//...

```

① 从这里可以看到，**注解的本质就是一个接口**，并且继承了`java.lang.annotation.Annotation`

② ③这里验证了上面所说的，内部的定义其实就是一个**带默认值的方法**

## 六、使用反射操作注解

​        反射的相关知识就不多做介绍了，不太了解的同学可以看下我的另外一篇博客[Java反射学习总结](https://blog.csdn.net/KingBoyWorld/article/details/105230415)。反射可以获取到Class对象，进而获取到Constructor、Field、Method等实例，点开源码结构发现Class、Constructor、Field、Method等均实现了`AnnotatedElement`接口，`AnnotatedElement`接口的方法如下

```java
// 判断该元素是否包含指定注解，包含则返回true
boolean isAnnotationPresent(Class<? extends Annotation> annotationClass)
// 返回该元素上对应的注解，如果没有返回null
<T extends Annotation> T getAnnotation(Class<T> annotationClass);
// 返回该元素上的所有注解，如果没有任何注解则返回一个空数组
Annotation[] getAnnotations();
// 返回指定类型的注解，如果没有返回空数组
T[] getAnnotationsByType(Class<T> annotationClass)
// 返回指定类型的注解，如果没有返回空数组，只包含直接标注的注解，不包含inherited的注解
T getDeclaredAnnotation(Class<T> annotationClass)
// 返回指定类型的注解，如果没有返回空数组，只包含直接标注的注解，不包含inherited的注解
T[] getDeclaredAnnotationsByType
// 返回该元素上的所有注解，如果没有任何注解则返回一个空数组，只包含直接标注的注解，不包含inherited的注解
Annotation[] getDeclaredAnnotations();
```

这就说明以上元素均可以通过反射获取该元素上标注的注解。

![](http://images.kimzing.com/blog/AnnotatedElement.png)

来一个完整的示例，show you the code

```java
// package-info.java
@AnyAnnotation(order = 0, desc = "包")
package demo.annotation.reflect;

// AnyAnnotation.java
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.PACKAGE, ElementType.TYPE, ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.FIELD,
        ElementType.LOCAL_VARIABLE, ElementType.PARAMETER})
public @interface AnyAnnotation {
    int order() default 0;
    String desc() default "";
}

// ReflectAnnotationDemo.java
@AnyAnnotation(order = 1, desc = "我是类上的注释")
public class ReflectAnnotationDemo {

    @AnyAnnotation(order = 2, desc = "我是成员属性")
    private String name;

    @AnyAnnotation(order = 3, desc = "我是构造器")
    public ReflectAnnotationDemo(@AnyAnnotation(order = 4, desc = "我是构造器参数") String name) {
        this.name = name;
    }

    @AnyAnnotation(order = 45, desc = "我是方法")
    public void method(@AnyAnnotation(order = 6, desc = "我是方法参数") String msg) {
        @AnyAnnotation(order = 7, desc = "我是方法内部变量") String prefix = "I am ";
        System.out.println(prefix + msg);
    }

    public static void main(String[] args) throws NoSuchFieldException, NoSuchMethodException {
        Class<ReflectAnnotationDemo> clazz = ReflectAnnotationDemo.class;
        // 获取包上的注解，声明在package-info.java文件中
        Package packagee = Package.getPackage("demo.annotation.reflect");
        printAnnotation(packagee.getAnnotations());
        // 获取类上的注解
        Annotation[] annotations = clazz.getAnnotations();
        printAnnotation(annotations);
        // 获取成员属性注解
        Field name = clazz.getDeclaredField("name");
        Annotation[] annotations1 = name.getAnnotations();
        printAnnotation(annotations1);
        //获取构造器上的注解
        Constructor<ReflectAnnotationDemo> constructor = clazz.getConstructor(String.class);
        AnyAnnotation[] annotationsByType = constructor.getAnnotationsByType(AnyAnnotation.class);
        printAnnotation(annotationsByType);
        // 获取构造器参数上的注解
        Parameter[] parameters = constructor.getParameters();
        for (Parameter parameter : parameters) {
            Annotation[] annotations2 = parameter.getAnnotations();
            printAnnotation(annotations2);
        }
        // 获取方法上的注解
        Method method = clazz.getMethod("method", String.class);
        AnyAnnotation annotation = method.getAnnotation(AnyAnnotation.class);
        printAnnotation(annotation);
        // 获取方法参数上的注解
        Parameter[] parameters1 = method.getParameters();
        for (Parameter parameter : parameters1) {
            printAnnotation(parameter.getAnnotations());
        }
        // 获取局部变量上的注解
        /**
         * 查了一些资料，是无法获取局部变量的注解的，且局部变量的注解仅保留到Class文件中，运行时是没有的。
         * 这个更多是给字节码工具使用的，例如lombok可以嵌入编译流程，检测到有对应注解转换成相应的代码，
         * 而反射是无法进行操作的。当然也可以利用asm等工具在编译器完成你要做的事情
         */
    }

    public static void printAnnotation(Annotation... annotations) {
        for (Annotation annotation : annotations) {
            System.out.println(annotation);
        }
    }
}
```

> 感兴趣的同学可以将`@AnyAnnotation`的Retention中的生命周期改为SOURCE/CLASS试试，这时就获取不到任何注解信息了哦

## 七、注解的底层实现-动态代理

首先准备一下测试代码，如下

```java
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.TYPE)
public @interface Learn {
    String name() default "default";
    int age();
}

@Learn(age = 12)
public class LearnAnnotationReflect {

    public static void main(String[] args) {
        Class<LearnAnnotationReflect> clazz = LearnAnnotationReflect.class;
 				// 判断class元素是否存在对应的注解
        if (!clazz.isAnnotationPresent(Learn.class)) {
            return;
        }
        // 获取相应的注解，并打印属性
        Learn learn = clazz.getAnnotation(Learn.class);
        System.out.println(learn.name());
        System.out.println(learn.age());
    }
}
```

​        在`System.out.println(learn.name());`打一个断点，以Debug模式运行，查看learn这个对象到底是什么

![](http://images.kimzing.com/blog/debug-annotation.png)

​        从上面的截图可以看出，jdk为Learn生成了一个叫`$Proxy1`的代理对象，并且包含了一个内部成员`AnnotationIvocationHandler`，接下来就是调用`$Proxy1.name()`进行获取name的值，那么我们来看下`$Proxy1`到底是一个什么样的对象，在jdk8中可以添加JVM参数`-Dsun.misc.ProxyGenerator.saveGeneratedFiles`来保存代理类，更高版本可以使用`-Djdk.proxy.ProxyGenerator.saveGeneratedFiles=true`来保存代理类。在Idea中的设置方法如下

![](http://images.kimzing.com/blog/proxy-save.png)

​        重新运行程序，就会发现在项目根目录多了如下类，其中`$Proxy1`就是`Learn`注解对应的代理类

![](http://images.kimzing.com/blog/20200405201332.png)        

​        当我们调用`Learn.name()`时，其实就是调用这个代理类的name方法，如下

```java
    public final String name() throws  {
        try {
            return (String)super.h.invoke(this, m3, (Object[])null);
        } catch (RuntimeException | Error var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }
```

​        代理类的name方法中主要是调用h的`invoke`方法传入当前对象，以及`m3`这个`方法元素`，m3如下

```java
m3 = Class.forName("demo.annotation.runtime.Learn").getMethod("name");
```

​        在`5.3`讲解的内容时，我们反编译了注解的class文件，知道在编译注解时，实际上编译为了一个接口，接口中定义了想干的属性的方法。

​       那么基本的流程我们就可以梳理出来了：

1. 通过反射我们可以获取对应元素上的注解@Learn，前面说过注解本质是一个接口，也就是获取到了Learn接口的代理对象。
2. Learn代理对象提供了相应的同名方法，内部声明了原注解的相应方法`Method`，如`method3`
3. 之后通过代理对象父类的h成员属性，也就是AnnotationInvocationHandler去执行invoke方法
4. AnnotationInvocationHandler在初始化时，会包含一个memberValues的map，key就是方法名，value就是对应的属性值，在invoka内部通过Method的name从memberValues中获取到对应的值并返回

​       

​       接下来我们来看下`AnnotationInvocationHandler`中的invoke方法相关信息，如下

```java
// 当前注解类型
private final Class<? extends Annotation> type;
// 当前注解的相关属性集合，key是方法名，value是对应的值
private final Map<String, Object> memberValues;

// jdk会将对应的属性信息传过来
AnnotationInvocationHandler(Class<? extends Annotation> var1, Map<String, Object> var2) {
            this.type = var1;
            this.memberValues = var2;
    }


public Object invoke(Object var1, Method var2, Object[] var3) {
        // 方法名
        String var4 = var2.getName();
        // 参数类型
        Class[] var5 = var2.getParameterTypes();
        // 如果是equals方法，则调用对应方法
        if (var4.equals("equals") && var5.length == 1 && var5[0] == Object.class) {
            return this.equalsImpl(var3[0]);
        // 不是equals方法，却有参数，说明是错误的，注解的方法是不允许有参数的
        } else if (var5.length != 0) {
            throw new AssertionError("Too many parameters for an annotation method");
        } else {
            // 定义一个变量var7 默认值-1
            byte var7 = -1;
            // 不同的方法赋予var7不同的值
            switch(var4.hashCode()) {
            case -1776922004:
                if (var4.equals("toString")) {
                    var7 = 0;
                }
                break;
            case 147696667:
                if (var4.equals("hashCode")) {
                    var7 = 1;
                }
                break;
            case 1444986633:
                if (var4.equals("annotationType")) {
                    var7 = 2;
                }
            }

          	// 返回对应方法的处理
            switch(var7) {
            case 0:
                return this.toStringImpl();
            case 1:
                return this.hashCodeImpl();
            case 2:
                return this.type;
            // 默认方法, 也就是我们自定义的属性方法
            default:
                // 从map集合中获取对应的值
                Object var6 = this.memberValues.get(var4);
                if (var6 == null) {
                    throw new IncompleteAnnotationException(this.type, var4);
                } else if (var6 instanceof ExceptionProxy) {
                    throw ((ExceptionProxy)var6).generateException();
                } else {
                    if (var6.getClass().isArray() && Array.getLength(var6) != 0) {
                        var6 = this.cloneArray(var6);
                    }

                    return var6;
                }
            }
        }
    }
```

> 跟着源码debug几次就能明白整体流程了

##  八、总结：注解工作流程

- 通过键值对的形式为注解属性赋值
- 编译器检查注解的使用范围  (将注解信息写入元素属性表 attribute)
- 运行时JVM将单个Class的runtime的所有注解属性取出并最终存入map里
- 创建AnnotationInvocationHandler实例并传入前面的map
- JVM使用JDK动态代理为注解生成代理类，并初始化处理器
-  调用invoke方法，通过传入方法名返回注解对应的属性值