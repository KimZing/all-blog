---
title: Java反射学习总结
date: 2020-3-31 19:53:44
tags:
- 反射
categories:
- Java
author: KimZing
type: 原创
toc: true
---

Java反射常用方法的总结和对应的操作示例。测试

<!-- more -->

## 一、简述Class

​        `Class`和`class`是不同的两个点，Class本身也是一个类型，和String、List本身没有什么差异, 而`class`只是一个关键字。Class可以理解为某个类型的元信息，包含其对应的`构造函数(Constructor)`、`方法(Method)`、`属性(Field)`以及其他相关信息(比如注解等信息)。

​        通过反射，也就是操作Class具体的对象，我们可以在运行期获取一个类型中各种访问权限的构造器、方法、属性，灵活的去创建某个类型的实例，调用其方法，设置其属性。可以认为这是Java提供的一个外挂，让我们可以做一些常规操作不能做到的操作。

​         Class的实例是由JVM进行创建的，并不能手动创建，只能进行获取，通过不同方式获取的Class实例都是同一个实例。

## 二、获取Class对象

获取Class对象的方法有三种，如下代码示例

```java
package com.kimzing.demo.reflect.demo;

import com.kimzing.demo.reflect.PersonReflect;

/**
 * 获取Class对象的三种方式.
 *
 * @author KimZing - kimzing@163.com
 * @since 2020/3/31 11:28
 */
public class ClassDemo {

    public static void main(String[] args) throws ClassNotFoundException {
        // 通过实例获取,需要导入对应包并创建实例
        PersonReflect personReflect = new PersonReflect();
        Class<? extends PersonReflect> clazz1 = personReflect.getClass();
        // 通过类获取，需要导入对应包
        Class<PersonReflect> clazz2 = PersonReflect.class;
        // 通过全路径获取，最灵活的方式，无需任何依赖，只需要传入包路径+类名即可
        Class<?> clazz3 = Class.forName("com.kimzing.demo.reflect.PersonReflect");

        // 结果为true，代表获取的Class对象都是同一个实例
        System.out.println(clazz1 == clazz2);
        System.out.println(clazz2 == clazz3);
    }

}

```



## 三、Class的方法总结

​        构造器、方法、属性都具有public、protected、default、private等权限修饰符。

​        Class对象包含类型的Constructor、Method、Field等信息，只要调用Class的相应方法即可获取，而这些方法的命名是具有共同的特征的。

- 获取所有构造器

获取所有公开的构造器使用`getConstructors()`。获取所有(包含public/protected/default/private)的构造器使用`getDeclaredConstructors()`

- 获取所有的方法

获取所有公开的方法使用`getMethods()`，同时会返回父类的所有公开方法。获取所有访问权限的方法使用`getDeclaredMethods()`。

- 获取所有的属性

获取所有公开权限的属性`getFields()`，同时会返回父类的公开属性。获取所有访问权限的属性`.getDeclaredFields()`

> 上面是获取的结果都是数组，也可以单独获取某个构造器、方法、属性

- 获取某个构造器

获取公开的构造器`getConstructor(Class<?>... parameterTypes)`,parameterTypes指的是构造器的参数。获取非公开的构造器`getDeclaredConstructor(Class<?>... parameterTypes)`

- 获取某个方法

获取某个公开的方法`getMethod(String name, Class<?>... parameterTypes)`，name指的是对应的方法名，而parameterTypes指的是对应的方法参数。获取非公开的某个方法使用`getDeclaredMethod(String name, Class<?>... parameterTypes)`。

- 获取某个属性

获取某个公开的属性`getField(String name)`,name指的是属性的名称。获取非公开的属性`getDeclaredField(String name)`。

​       总结一下：

- 获取公开(public)元信息，使用`get{元信息名}s`
- 获取所有的元信息，使用`getDeclared{元信息名}s`

## 四、操作对应的元信息

### 4.1 调用构造器创建类型实例

调用Constructor实例的`newInstance(Object ... initargs)`方法执行对应的构造函数，如果构造器是非公开的，那么需要先设置`setAccessible(boolean flag)`为`true`才可以调用。

### 4.2 调用方法执行

调用Method实例的`invoke(Object obj, Object... args)`方法执行对应的方法，obj指的是要执行方法的主体，也就是哪个对象执行这个方法，而args则是对应的参数。如果该方法是静态方法，obj可为null。如果方法是非公开的，那么需要先设置`setAccessible(boolean flag)`为`true`才可以调用。

### 4.3 赋值属性与读取属性值

调用Field实例的`set(Object obj, Object value)`,obj代表设置哪个对象的相应属性值，value代表要设置的属性的值。调用Field实例的`get(Object obj)`获取obj对象的对应的属性的值。如果获取的是静态的属性，那么obj可以为null。



## 五、代码示例

### 操作实例

```java
package com.kimzing.demo.reflect;

import java.time.LocalDateTime;

/**
 * 反射的目标.
 *
 * @author KimZing - kimzing@163.com
 * @since 2020/3/31 10:27
 */
public class PersonReflect {

    public String name;

    protected Integer age;

    Double amount;

    public static String hello = "HELLO";

    private LocalDateTime birth;

    public PersonReflect() {
    }

    public PersonReflect(String str) {
        System.out.println("公开构造函数:" + str);
    }

    PersonReflect(int i) {
        System.out.println("默认构造函数" + i);
    }

    protected PersonReflect(String str, int i) {
        System.out.println("受保护的构造函数" + str + i);
    }

    private PersonReflect(char ch) {
        System.out.println("私有构造函数" + ch);
    }

    public void method1() {
        System.out.println("public method");
    }

    void method2(String str) {
        System.out.println("public method" + str);
    }

    protected void method3() {
        System.out.println("protected method");
    }

    private void method4(Integer integer) {
        System.out.println("private method" + integer);
    }

    public static void hello() {
        System.out.println("Hello");
    }

}

```

### 操作构造函数

```java
package com.kimzing.demo.reflect.demo;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * 构造器反射.
 *
 * @author KimZing - kimzing@163.com
 * @since 2020/3/31 11:26
 */
public class ConstructorReflectDemo {

    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class<?> pClass = Class.forName("com.kimzing.demo.reflect.PersonReflect");

        System.out.println("----获取所有的公有构造方法");
        Constructor<?>[] constructors = pClass.getConstructors();
        for (Constructor<?> constructor : constructors) {
            System.out.println(constructor);
        }

        System.out.println("----获取所有的构造方法，包含protected default private");
        constructors = pClass.getDeclaredConstructors();
        for (Constructor<?> constructor : constructors) {
            System.out.println(constructor);
        }
        System.out.println("----获取单个公开的构造方法");
        Constructor<?> constructor = pClass.getConstructor(String.class);
        System.out.println(constructor);

        System.out.println("----获取私有的构造方法");
        constructor = pClass.getDeclaredConstructor(char.class);
        System.out.println(constructor);

        System.out.println("----使用反射得到的构造器创建实例");
        // 任何包均可直接获取
        System.out.println("--公开构造方法");
        constructor = pClass.getConstructor(String.class);
        constructor.newInstance("Hello");
        // protected/default/private 均需要设置权限，如果和反射操作类处于同一个包下则无需设置权限
        System.out.println("--受保护的构造方法");
        constructor = pClass.getDeclaredConstructor(String.class, int.class);
        constructor.setAccessible(true);
        Object hello = constructor.newInstance("hello", 18);
        System.out.println("--默认的构造方法");
        constructor = pClass.getDeclaredConstructor(int.class);
        constructor.setAccessible(true);
        constructor.newInstance(24);
        System.out.println("--私有的构造方法");
        constructor = pClass.getDeclaredConstructor(char.class);
        constructor.setAccessible(true);
        constructor.newInstance('a');
    }

}

```

### 操作方法

```java
package com.kimzing.demo.reflect.demo;

import com.kimzing.demo.reflect.PersonReflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 方法反射.
 *
 * @author KimZing - kimzing@163.com
 * @since 2020/3/31 13:20
 */
public class MethodReflectDemo {

    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {
        Class<?> pClass = Class.forName("com.kimzing.demo.reflect.PersonReflect");
        System.out.println("----获取公开方法，包含父类方法");
        Method[] methods = pClass.getMethods();
        for (Method method : methods) {
            System.out.println(method);
        }

        System.out.println("----获取所有方法,包含非公开的方法");
        methods = pClass.getDeclaredMethods();
        for (Method method : methods) {
            System.out.println(method);
        }

        System.out.println("----获取单个的公开方法");
        Method method1 = pClass.getMethod("method1");
        System.out.println(method1);

        System.out.println("----获取单个的非公开的方法");
        Method method4 = pClass.getDeclaredMethod("method4", Integer.class);
        System.out.println(method4);

        System.out.println("----执行公开方法");
        PersonReflect personReflect = (PersonReflect) pClass.newInstance();
        method1.invoke(personReflect);

        System.out.println("----执行非公开方法");
        method4.setAccessible(true);
        method4.invoke(personReflect, 12);

        System.out.println("----执行静态方法");
        Method hello = pClass.getMethod("hello");
        hello.invoke(null);
    }

}

```

### 操作属性

```java
package com.kimzing.demo.reflect.demo;

import com.kimzing.demo.reflect.PersonReflect;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

/**
 * 属性反射.
 *
 * @author KimZing - kimzing@163.com
 * @since 2020/3/31 13:07
 */
public class FieldReflectDemo {

    public static void main(String[] args) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, InstantiationException {
        Class<?> pClass = Class.forName("com.kimzing.demo.reflect.PersonReflect");
        System.out.println("----获取公开的属性");
        Field[] fields = pClass.getFields();
        for (Field field : fields) {
            System.out.println(field);
        }

        System.out.println("----获取所有的属性");
        fields = pClass.getDeclaredFields();
        for (Field field : fields) {
            System.out.println(field);
        }

        System.out.println("----获取单个公开的属性");
        Field name = pClass.getField("name");
        System.out.println(name);

        System.out.println("----获取非公开的属性");
        Field birth = pClass.getDeclaredField("birth");
        System.out.println(birth);

        System.out.println("----设置属性的值");
        PersonReflect personReflect = (PersonReflect) pClass.newInstance();
        System.out.println("--设置公开的属性");
        name.set(personReflect, "KimZing");
        System.out.println("name的值为" + personReflect.name);
        System.out.println("--设置非公开的属性,并读取非公开属性");
        birth.setAccessible(true);
        birth.set(personReflect, LocalDateTime.now());
        System.out.println("birth的值为" + birth.get(personReflect));

        System.out.println("----获取静态的属性");
        Field hello = pClass.getField("hello");
        System.out.println(hello.get(null));
    }

}

```

