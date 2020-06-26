---
title: Java泛型全方位介绍与示例
date: 2020-3-27 19:20:41
tags:
- 泛型
categories:
- Java
author: KimZing
type: 原创
toc: true
---

对Java中的泛型的使用进行示例讲解，并对一些使用做了自己的分析

<!-- more -->

## 一、没有泛型的问题

```java
public class GenericDemo {

    public static void main(String[] args) {
        List stringList = getExpectStringList();
        stringList.stream()
                // 强转为String类型，因为期望的就是只放String
                .map(s -> ((String) s).toUpperCase())
                // 编译器不会报错，但是运行期因为含有非期望元素，会报错。
                .forEach(s -> System.out.println(s));
    }

    public static List getExpectStringList() {
        ArrayList stringList = new ArrayList();
        stringList.add("Hello");
        stringList.add("World");
        // 参数为object类型，也就是说可以添加任何类型，无法阻止其他人添加一个int的元素
        stringList.add(123);
        return stringList;
    }

}
```

运行报错信息：

```bash
Exception in thread "main" java.lang.ClassCastException: java.lang.Integer cannot be cast to java.lang.String
```

没有泛型的话，这种问题只能用很别脚的方式进行处理，如下使用`instanceof `进行判断

```java
public class GenericDemo {

    public static void main(String[] args) {
        List stringList = getExpectStringList();
        stringList.stream()
                // 强转为String类型，因为期望的就是只放String
                .map(s -> {
                    if (s instanceof String) {
                        return ((String) s);
                    }
                    return null;
                })
                .filter(s -> s != null)
                // 编译器不会报错，但是运行期因为含有非期望元素，会报错。
                .forEach(s -> System.out.println(s));
    }

}
```

输出如下

```bash
Hello
World
```

## 二、泛型解决的问题

在上面问题中，我们强调在编译期没有报错，而运行期却报错了，这并不是我们所期望的，更希望的是提早暴露错误，更早的去解决。

在创建支持泛型的类时，可以使用`<>`声明内部的类型，在编译期就可以检查出错误。



```java
public class GenericDemo {
  
    public static void main(String[] args) {
        test3();
    }

    public static void test3 () {
        List<String> stringList = getExpectStringListWithType();
        stringList.stream().forEach(s -> System.out.println(s));
    }
  
    public static List<String> getExpectStringListWithType() {
        ArrayList<String> stringList = new ArrayList();
        stringList.add("Hello");
        stringList.add("World");
        // 编译器会进行检查，编译报错
        // stringList.add(123);
        return stringList;
    }
}
```



对比一下，可以看出带来了很多方便的地方，

1. 无需进行类型转换了，可以自动推导容器内的元素类型，从而直接操作相应的元素方法
2. 当与声明的类型不匹配时，编译报错，可以及早发现问题。



## 三、泛型的分类

泛型有三种使用方式

- 泛型类：在类上定义泛型，类内部均可使用该类型， 但不可用于静态方法。
- 泛型接口：在接口上定义泛型，内部方法均可使用该类型
- 泛型方法：在方法上定义泛型，方法内部可使用

泛型的限制

- `泛型类`中的静态方法不能使用类上定义的泛型，但可以将静态方法定义为`泛型方法`
- 泛型不能使用基本数据类型
- 泛型相关信息不回进入到运行阶段，仅是在编译阶段做检查。

### 3.1 泛型类

定义格式

```java
// 泛型可以定义多个
public class 类名<泛型标识1, 泛型标识2> {
  // 构造函数
  public 类名 (泛型标识 参数) {}
  // 属性
  public 泛型标识 属性名
  // 方法
  public 泛型标识(返回值) 方法名(泛型标识 参数) {} 
}
```

实例：

```java
public class GenericClass<T> {

    private T param;

    public GenericClass(T param) {
        this.param = param;
    }

    public T getParam() {
        return param;
    }
  
    public static void main(String[] args) {
        GenericClass<String> hello = new GenericClass<>("Hello");
        GenericClass<Integer> num = new GenericClass<>(123);
    }

}
```



> 为什么不能用于静态方法呢？
>
> 在jvm加载Class的时候会将类中所有的常量，静态常量，静态方法写到内存的方法区内；
> 其次，所有的常量，静态常量和静态方法在方法区内有且只有一份，并为所属类所创建的所有对象共享；
>
> 这个时候如果创建两个不同类型的实例，jvm也不知道到底该用哪个类型。(实际上jvm不保存泛型信息)

### 3.2 泛型接口

与泛型类的定义比较类似，实例如下

```java
public interface GenericInterface <T, E>{

    /**
     * 将两种类型拼接成字符串返回
     */
    String contact(T t, E e);

}
```

有两种方式进行实现：

1. 具现化泛型参数，不同的实现类来做不同的功能

```java
// 拼接String和Integer
public class GenricInterfaceImpl1 implements GenericInterface<String, Integer> {

    @Override
    public String contact(String s, Integer i) {
        return String.format("string:%s integer:%d", s, i);
    }

    public static void main(String[] args) {
        GenricInterfaceImpl1 genricInterfaceImpl1 = new GenricInterfaceImpl1();
        System.out.println(genricInterfaceImpl1.contact("a", 1));
    }
}

```

```java
// 拼接Integer和Float
public class GenricInterfaceImpl2 implements GenericInterface<Integer, Float> {

    @Override
    public String contact(Integer s, Float i) {
        return String.format("integer:%s float:%f", s, i);
    }

    public static void main(String[] args) {
        GenricInterfaceImpl2 genricInterfaceImpl2 = new GenricInterfaceImpl2();
        System.out.println(genricInterfaceImpl2.contact(23, 1f));
    }
}
```

2. 仍然继承泛型特征，可以对原有功能进行扩展，仍然是实现通用的功能，决定权交给使用者。

```java
public class GenricInterfaceImpl3<T,E,F> implements GenericInterface<T, E> {

    private F f;

    public GenricInterfaceImpl3(F f) {
        this.f = f;
    }

    @Override
    public String contact(T t, E e) {
        return String.format("t is %s, e is %s, f is %s", t, e, f);
    }

    public static void main(String[] args) {
        GenricInterfaceImpl3<String, String, Integer> impl3 =
                new GenricInterfaceImpl3<>(12);
        System.out.println(impl3.contact("Hello", "World"));
    }
}
```

> 需要注意的是实现类的泛型声明至少要包含父类的声明，可以多，但不能少，而且名字要一样，但顺序可以不一样。

### 3.3 泛型方法

既可以定义在泛型类、泛型接口，也能用在普通方法和静态方法里，是一种比较灵活的方式，针对的对象就是该方法及方法内部。

如果定义在泛型类、泛型接口中，方法上定义的泛型和泛型类、泛型接口上的泛型是没有任何关系的，甚至重名都不会相互影响。同样，各个泛型方法之间也不会相互影响

泛型方法需要进行泛型声明，声明使用`<>`，示例如下

```java
public class GenericMethod {

    public <T> String convertToString(T t) {
        return t.toString();
    }

    public static <T> String addPrefix(T t) {
        return "pre:" + t.toString();
    }

    public static void main(String[] args) {
        GenericMethod genericMethod = new GenericMethod();
        System.out.println(genericMethod.convertToString(12));
        System.out.println(GenericMethod.addPrefix(new int[]{1, 2}));
    }

}
```

## 四、常用泛型字母的含义

泛型的定义名称是可以随意取的，但是通常有一套约定，方便不同人进行相同的理解。

| 定义  | 含义    | 说明                                                         |
| ----- | ------- | ------------------------------------------------------------ |
| E     | Element | 在集合中实用，代表元素的意思                                 |
| T,S,U | Type    | 代表Java中某个类型。如果需要使用多个泛型类型，可以将S作为第二个泛型类型，可以将U作为第三个泛型类型 |
| K     | Key     | 键                                                           |
| V     | Value   | 代表值                                                       |
| N     | Number  | 数值类型                                                     |
| R     | Result  | 返回的结果类型                                               |

## 五、通配符

### 5.1 泛型会自动转型吗？

看下这个例子

```java
public class Animal {}
public class Dog extends Animal {}

public class AnimalGenericDemo<T> {

    public static void printArray(List<Animal> animals) {}

    public static void main(String[] args) {
        List<Dog> dogs = new ArrayList<>();
        // 泛型是没有继承关系的，是1就是1，这样就比较死板。下面这段代码会报错，
        // 错误信息：需要类型为List<Animal>，实际类型为List<Dog>
        // printArray(dogs);
    }
}
```

通过上面的例子，可以看出泛型是不会自动转型的。这时候就可以使用通配符了，修改如下

```java
public class AnimalGenericDemo<T> {

    public static void printArray(List<?> animals) {}

    public static void main(String[] args) {
        List<Dog> dogs = new ArrayList<>();
        printArray(dogs);
    }
}
```

使用`<?>`来代表任何类型，但是这样问题就来了，我们原本使用泛型就是为了在编译时的确定性，但是现在又可以传入随意的元素类型了。

>  通配符<?>通常用于泛型方法的调用代码和形参，**不能用于定义类和泛型方法。**

> **泛型是用来定义类型并供内部使用的，而通配符更像是对泛型的类型范围做一个约束。**

> 泛型是泛型，通配符是通配符。接下来要说的上下界都是针对通配符而言，不要混淆在一起了。

**总结一下：泛型是没有多态的,它可不认为Dog是Animal的子类（泛型只是编译级别,在运行期间,泛型是会擦除的）**

### 5.2 泛型上界

上面的例子中定义比较死板，只能传入`List<Animal>`，如果想要传入子类呢？那么就可以使用通配符加上约束条件，格式: `<? extends Animal>`

上面的例子改写如下即可编译通过并成功运行

```java
public class Animal {}
public class Dog extends Animal {}
public class Cat extends Animal {}

    public static List<? extends Animal> getList1(List<? extends Animal> animals) {
        // 上界限定时，无法对其进行修改
        // animals.add(new Cat());
        return animals;
    }

    public static void main(String[] args) {
        List<Dog> dogs = new ArrayList<>();
        dogs.add(new Dog());
        List<? extends Animal> list1 = getList1(dogs);
        // 站在使用者的角度，想转换回原来的类型，而如果添加了Cat，当然会报错了。
        list1.stream().forEach(d -> System.out.println(((Dog) d)));
    }
}
```

> 为啥不能添加元素呢？
>
> 我们传递进去的参数是明确的`List<Dog>`，如果内部允许添加元素，就可以添加`Cat`类型的元素，在之后的操作中` list1.stream().forEach(d -> System.out.println(((Dog) d)));`就会产生不确定性，例如强制转型为Dog，其中却有一个Cat，很明显就会发生类型转换异常了。

### 5.3 泛型下界

与泛型上界是相反的，使用super进行定义。这里的方法参数必须传入Dog的父类，这个蛮好理解的。

> Tips: 可以指定多个限定范围

```java
public class Animal {}
public class Cat extends Animal {}
public class Dog extends Animal {}
public class Samoyed extends Dog {}


public class AnimalGenericDemo<T> {
  
    public static void main(String[] args) {
        List<Animal> animals = new ArrayList<>();
        animals.add(new Animal());
        List<? super Dog> list2 = getList2(animals);
        // 取出的是Object类型
        Object object = list2.get(0);
        // 站在使用者的角度，想转换回原来的类型Animal, 
        //我们在内部添加了Dog的子类Samoyed, Samoyed同样也是Animal的子类，所以转换时不会报错的。
        list2.stream().forEach(d -> System.out.println(((Animal) d)));

    }

    public static List<? super Dog> getList2(List<? super Dog> animals) {
        animals.add(new Samoyed());
        return animals;
    }


}
```

接下来就懵逼了，我tm为啥这里的方法内部可以添加元素，而且还添加的是子类的元素类型，而且返回的还是Object类型？百思不得姐……

> 先来看看为啥返回的是Object类型？

前面说过泛型下界只能传入是该类型的父类，那父类可就多了，最上层的是Object，那么获取元素的时候，JVM也不知道你添加的到底是哪个父类，所以->返回Object，没毛病。

> 为啥这里就可以添加元素，还是子类的类型？

我们仔细捋一捋，传入的是Dog的父类Animal，我们在方法内添加了一个Dog的子类Samoyed，Java中多重继承，所以Samoyed也是Animal的子类。这时候我们将其所有元素强转为Animal肯定不会报错的，所以当然支持了。(有点绕，你想想，你再仔细想想)

## 六、更多

在上面的泛型上下界中，使用的都是通配符?进行限定，那普通的泛型参数可以吗？当然是可以的，不过只能使用`extends`进行上界的限定，这一点和通配符的限定比较类似，而且拥有实际的用途。

```java
public class GenericDemo<T extends List> {}
```



那为什么不能使用像通配符`? super Dog`这样进行下界限定呢？

因为这样是没有使用意义的，加入Java允许定义下界，那么写出的代码将会像下面这样

```java
public class Animal {}
public class Cat extends Animal {}
public class Dog extends Animal {}
public class Samoyed extends Dog {}

public class Demo<T super Samoyed> {
  
  private T param;

    public GenericClass(T param) {
        this.param = param;
    }

    public T getParam() {
        return param;
    }

    public static void main(String[] args) {
        GenericClass<Dog> dog = new GenericClass<>(new Dog);
        // 仔细看这里
        dog.getParam();
    }
}
```

在第21行，到底返回什么合适呢？像通配符一样返回Object ？给我个Object有啥意义呢？所以也就不支持了。

个人是这样理解的，如有错误，希望指正。