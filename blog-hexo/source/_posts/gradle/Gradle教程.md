---
title: Gradle教程
date: 2020-3-23 16:31:30
tags:
- gradle
categories:
- 开发工具
author: KimZing
type: 原创
thumbnail: /gallery/flower.jpg
toc: true
---

Gradle的安装使用，深入的介绍Gradle的Task与Project相关概念和介绍

<!-- more -->

- Gradle5.6.4

## 一、Gradle是什么

1. 项目依赖管理工具：在gradle脚本文件引入插件编写对应的依赖坐标，就会下载对应依赖文件并添加进项目依赖
2. 项目构建工具：执行gradle内置或者我们编写的任务，即可构建出jar、war以及其他格式的最终文件
3. 和Maven/Ant属于一个领域的应用工具，不过Gradle是相对较新的，功能也很强大
4. 基于JVM，高性能，适用于几乎任何软件的构建，常见的有Android、Java项目
5. Gradle使用Groovy或kotlin进行书写，可自由选择(下文统一使用groovy编写)

## 二、为什么要Gradle

1. Gradle基于Groovy语言，提供了丰富且简洁的语法，甚至可以像写代码一样在Gradle的脚本文件中编写对应的语句执行对应的操作。
2. 在脚本文件`build.gradle`中，可以根据约定进行配置，简洁易读
3. 总结一下，就是很灵活，可扩展性和定制化比较好

> mark一下：spring framework就是使用gradle构建的

## 三、如何安装Gradle

1. 首先到[官网下载](https://gradle.org/releases/)对应版本的安装包，下载完成后解压到任意目录，windows如`D:\Gradle`，mac/linux如`~/Gradle`。

2. 添加到环境变量，从而方便我们在任意目录执行`gradle`任务

```bash
# Windows示例（也可以将完整路径配置在Path变量中）
变量名:GRADLE_HOME 变量值: D:\Gradle
变量名:Path        变量值: %GRADLE_HOME%\bin
# Mac/Linux示例 
sudo cp -R ~/Gradle /usr/local/
sudo ln -s /usr/local/Gradle/bin/gradle /usr/local/bin/gradle
```

3. 在终端执行 `gradle -v` ,有相应信息输出，则说明安装成功

## 四、核心概念基础

projects 和 tasks是 Gradle 中最重要的两个概念。

> **任何一个 Gradle 构建都是由一个或多个 projects 组成。每个 project 包括许多可构建组成部分（划重点）。** 这完全取决于你要构建些什么。举个例子，每个 project 或许是一个 jar 包或者一个 web 应用，它也可以是一个由许多其他项目中产生的 jar 构成的 zip 压缩包。一个 project 不必描述它只能进行构建操作。**它也可以部署你的应用或搭建你的环境(也就是说有的项目是服务于项目的，和业务无关)**。

> **每个 project 都由多个 tasks 组成。每个 task 都代表了构建执行过程中的一个原子性操作。如编译，打包，生成 javadoc，发布到某个仓库等操作。（划重点）**

个人理解: 

1. gradle是用来构建项目的(不限于构建打包)，而有的项目是组合项目(父项目，多个子项目。或者叫多模块项目)，所以gradle要做的第一点就是如何管理项目(projects)。

2. 而要对每个项目做的操作(构建，打包，发版)就是任务(Task)。

**所以理解并学好这两块是学好gradle的前提。**

```bash
// 查看当前项目
// Either import the build with an IDE or run gradle projects from the command line. If only the root project is listed, it’s a single-project build. Otherwise it’s a multi-project build.
// 使用ide或者手动执行`gradle projects`,如果只有一个项目说明是单体的项目，如果有多个，则说明是一个组合项目，至于如何创建多项目并管理，后面会做说明

// 查看可运行的tasks
// if you have imported the build into an IDE, you should have access to a view that displays all the available tasks. From the command line, run gradle tasks.
//通过命令`gradle tasks`查看当前可用的task
```

## 五、Gradle构建的生命周期

简单了解Gradle构建的生命周期有助于理解任务的执行过程。

构建分为以下三个阶段：

### 初始化阶段

Gradle支持单项目和多项目构建。在初始化阶段，Gradle确定将要参与构建的项目，并为每个项目创建一个Project实例。

### 配置阶段

在此阶段，Project已经准备好了，这时候会执行所有的gradle脚本配置(任务中的配置语句或文件中的独立语句)。

### 执行阶段

gradle执行给定名称的任务。

## 六、任务-(简单任务学习)

> 为什么要学习Task

​        使用gradle构建Java项目时，会执行`gradle clean`清理项目构建文件，`gradle build`构建项目，`gradle jar`打包项目。clean/build/jar其实都是一个任务，只是这些任务是由`plugin jar`等插件来提供，可以称之为内置Task，使用gradle构建项目其实就是在执行相应的gradle任务。学会自定义Jar方便于我们去理解定制这些任务，例如如何将我们编写的自定义任务嵌入到内置的任务前后。下面学习的任务规则是适合插件提供的内置任务的。

###  6.1 任务的简单定义

任务类似于Java中定义的一个方法，不过定义的语法和内部的写法有些不太一样。我们编写一个`build.gradle`文件，内容如下

```groovy
// task声明这是一个任务
task hello {
    // 打印字符串
    println "Hello Gradle!"
}
```

在**build.gradle**目录位置打开终端执行`gradle hello`， 执行这条命令时，gradle会自动解析当前目录的build.gradle，输出如下

```bash
λ gradle hello
Starting a Gradle Daemon (subsequent builds will be faster)

> Configure project :
Hello Gradle!
```

> *输出的内容有点多，如果只需要有用的信息，可以执行`gradle -q hello`，输出就简化了很多*，如下

```bash
λ gradle -q hello
Hello Gradle!
```

### 6.2 任务的行为

个人理解: 简单来说，就是任务不同生命周期的执行动作，执行前的动作 `doFirst`, 执行后的动作 `doLast`，有两种定义方式。

 (可以大致理解为在Java类中定义了两个方法`init()`和`destory()`, 前者在执行前运行，后者在执行后运行。)

方式一

```groovy
// 括号可以省略。
// 其实doFirst和doLast就是任务的属性，而且这些属性是一个集合属性，可以有多个。通过方式二可以进一步理解。
task eat() { 
    doFirst {
        println "洗手"
    }
    
    doLast {
        println "刷盘子"
    }
    println "吃饭"
}
```

方式二

```groovy
task eat {
  println "吃饭"
}
eat.doLast {
  println "收拾桌子"
}
eat.doFirst {
  println "洗手"
}
eat.doLast {
  println("刷盘子")
}
eat.doLast { // 可以定义多个doLast,会按顺序执行。
  println "扫地"
}

// 使用场景：java项目中，如果想在compile完成时添加一个输出语句，可以进行追加doLast方法

/*
compile.doLast {
  println "项目编译完成"
}
*/
```

输出

```bsh
$ gradle -q eat
吃饭
洗手
收拾桌子
刷盘子
扫地
```

> 不难发现，doFirst和doLast是可以定义多个的，并且按顺序执行

这里有个小小的疑问，按照表面理解不应该是先执行doFirst，再执行eat内定义的`println "吃饭"`，最后执行doLast吗？

其实并不是这样，Gradle文档中有相应说明，eat{}内编写的`println "吃饭"`是在配置阶段运行的，而doFirst和doLast则是在运行阶段按照顺序运行的，所以才有了上面的输出结果。

**也就是说在配置阶段就会运行任务内或脚本内定义的语句。**

尝试运行如下脚本， `gradle test`, `gradle testBoth`，实践一下就知道了

```groo
println '配置阶段运行脚本文件内的语句.'

task configured {
    println '虽然没有执行configured任务，这段话任然会在配置阶段打印.'
}

task test {
    doLast {
        println '这段话只有执行test时才会打印.'
    }
}

task testBoth {
	doFirst {
	  println '执行testBoth前打印.'
	}
	doLast {
	  println '执行testBoth后打印.'
	}
	println '我是testBoth的配置语句.'
}
```



### 6.3 任务的依赖

任务之间是可以相互依赖的，consumer依赖producer, 那么执行consumer时，就会先执行producer任务。（不先生产怎么消费？）

```groovy
task consumer1 {
  doLast {
    println "consumer1: 吃了一个糖果"
  }
}

// 方式一：以属性的方式来定义依赖
// 利用这一点我们可以将我们的任务依赖于内置任务，例如 consumer1.dependsOn clean
// 注意：如果引用的task在声明该task之前，需要加上引号(''或"")，否则无法构建，在多项目构建中会使用到这一特性
consumer1.dependsOn "producer"

task producer {
  doFirst {
    println "生产一个糖果"
  }
}

// 方式二：在()内声明denpendsOn
task consumer2(dependsOn: producer) {
  doFirst {
    println "consumer2:生产了一个糖果"
  }
}

// 方式三: 在任务内声明
task consumer3 {
  dependsOn producer
  doLast {
	  println("consumer3:吃了一个糖果")
  }
}
```

输出

```bash
$ gradle -q consumer1
生产一个糖果
consumer1: 吃了一个糖果

$ gradle -q consumer2
生产一个糖果
consumer2:生产了一个糖果

$ gradle -q consumer3
生产一个糖果
consumer3:吃了一个糖果
```

> 小提示: gradle支持短命名的方式执行task，只要唯一即可，例如上面的命令可以改写成如下

```groovy
$ gradle -q c1
生产一个糖果
consumer1: 吃了一个糖果

$ gradle -q c2
生产一个糖果
consumer2:生产了一个糖果

$ gradle -q c3
生产一个糖果
consumer3:吃了一个糖果
```

### 6.4 动态任务

可以动态的创建任务并执行

```groovy
// counter从0开始，这是code规矩
4.times { counter -> 
     task "task${counter+1}" {
       doFirst {
         println "it is ${counter+1}"
       }
     }
}
```

输出

```bash
λ gradle -q task1 task2 task3 task4
it is 1
it is 2
it is 3
it is 4
```

> 动态任务也是可以定义以来的，自己动手试一下吧

### 6.5 为任务定义属性

任务其实可以理解为一个对象， 有doFirst和doLast等行为属性，同样可以自定义属性

```groovy
task road {
    ext.length = 10
}

task navi {
    doLast {
        println "道路长度:" + road.length
    }
}
```

输出

```bsh
λ gradle navi
道路长度:10
```

### 6.6 提取任务的公用方法

不同的任务中可能会包含相同的逻辑，重复书写是不太优雅的，这时可以将重复的逻辑抽取成相应的方法。如下示例

```groovy
task printName {
  doLast {
    println contactStr("Kim", "Zing")
  }
}

task getAgeAndAddress {
  doLast {
    println contactStr("18岁", "万象会所")
  }
}

String contactStr(String s1, String s2) {
  return s1 + s2
}
```

执行

```bash
λ gradle printName getAgeAndAddress -q
KimZing
18岁万象会所
```



### 6.7 默认任务

可以设置当前脚本中的默认执行任务

```groovy
defaultTasks 'name','hello'

task name {
  doLast {
	  println "gradle"
  }
}
task hello {
  doLast {
	  println 'Hello'
  }
}
```

执行`gradle`命令，name和hello两个任务都运行了

```bash
λ gradle -q
gradle
Hello
```

### 6.8  任务的生命周期钩子

文章开头简单介绍过任务的构建生命周期，Gradle具有配置阶段和执行阶段。在配置阶段完成之后，Gradle就可以知道将要执行的所有任务。 
Gradle提供了一个利用此信息的机会，可以嵌入自定义的行为。

用下面的例子来做说明，一个releasePackage任务用来打包release版本，一个snapshotPakcage任务来打包snapshot版本，希望达到的目的是执行相应的任务后，加上对应的版本后缀

```groovy
// 定义一个属性
def String tag = ""

task releasePackage {
  doLast {
    println "package $version.$tag"
  }
}

task snapshotPackage {
  doLast {
    println "package $version.$tag"
  }
}

gradle.taskGraph.whenReady {taskGraph ->
  // version是project对象的属性，所以可以直接使用，并在任务中获取
  version = "1.0"
  if (taskGraph.hasTask(":releasePackage")) {
    tag = "RELEASE"
  } else {
    tag = "SNAPSHOT"
  }
}
```

执行

```bash
λ gradle releasePackage -q
package 1.0.RELEASE

λ gradle snapshotPackage -q
package 1.0.SNAPSHOT
```

### 6.9 引用外部依赖的功能，供脚本使用

在很多项目的构建脚本中会看到buidscript{}代码块，**这个代码块主要是为脚本本身提供服务的**，其中也会定义repository,dependencies，但这些和在java项目中的repository/dependencies是两个领域的，小伙伴不要混淆了哈。

比如说需要对字符串进行base64操作，我们自己去定义方法处理肯定是过于浪费时间的，在java中有现成的第三方类库实现了这些功能，我们就可以直接在任务或方法中引用了。

```groovy
// 来自在buildscript中引入的依赖
import org.apache.commons.codec.binary.Base64

buildscript {
  repositories {
    mavenCentral()
  }
  dependencies {
    classpath "commons-codec:commons-codec:1.2"
  }
}

task encode {
  doLast {
    def byte[] encodedString= new Base64().encode("hello World".getBytes())
    println new String(encodedString)
  }
}
```

输出

```bash
λ gradle encode

> Task :encode
aGVsbG8gV29ybGQ=
```

这样我们就可以很轻松的利用第三方的能力，增加脚本的功能和灵活性

## 七、任务-(高级任务学习)

如果只是应用gradle进行项目构建，那么这部分知识可以简单了解即可。

### 7.1 定义任务的其他方式

```groovy
// 使用字符串的方式定义任务
task('hello') {
    doLast {
        println "hello"
    }
}

task('copy', type: Copy) {
    from(file('srcDir'))
    into(buildDir)
}
// 使用tasks来定义。在gradle内部维护的就是一个task的集合tasks
tasks.create('hello') {
    doLast {
        println "hello"
    }
}

tasks.create('copy', Copy) {
    from(file('srcDir'))
    into(buildDir)
}
// 使用动态关键词定义。作者也没明白这种方式的区别
task(hello) {
    doLast {
        println "hello"
    }
}

task(copy, type: Copy) {
    from(file('srcDir'))
    into(buildDir)
}
```

> Tips : 可以为任务添加描述，当执行gradle tasks时，会输出任务的描述，方便查看
>
> task hello {
>
> ​    description "this is demo hello"
>
> }

### 7.2 如何定位到一个任务，并获取其相关属性

获取任务实例，可以方便我们对 这个任务做一定的操作

首先编写一个`settings.gradle`文件，内容如下

```groovy
// 这个其实就是多项目中的一个子项目的声明方式
include ":projectA"
```

build.gradle文件内容如下

```groovy
// 根项目的方法,只是做了定义，没有具体行为
task hello
task copy(type: Copy)

// projectA项目中的hello方法
project(':projectA') {
    task hello
}

// 使用领域专用语言，也就是特定的语法
println hello.name
println project.hello.name

println copy.destinationDir
println project.copy.destinationDir

// 使用tasks集合进行获取
println tasks.hello.name
println tasks.named('hello').get().name

println tasks.copy.destinationDir
println tasks.named('copy').get().destinationDir

// 使用tasks通过路径获取, 打印出任务的路径
println tasks.getByPath('hello').path
println tasks.getByPath(':hello').path
println tasks.getByPath('projectA:hello').path
println tasks.getByPath(':projectA:hello').path
```

输出

```bash
λ gradle -q      
hello            
hello            
null             
null             
hello            
hello            
null             
null             
:hello           
:hello           
:projectA:hello  
:projectA:hello  
```

### 7.3 使用内置的任务模板（个人命名，非官方）

在上面的例子中，有一个`task copy(type: Copy)`,这个是什么意思呢？其实Gradle内置了一个Copy的任务模板，这个模板将复制文件的逻辑都已经写好了，我们可以直接使用，类似于Java中的父类，父类的方法我们可以直接使用，只要传入相应的参数就可以了。方式如下

```groovy
// 方式一： 使用属性的方式
task myCopy1(type: Copy)
Copy myCopy1 = tasks.getByName("myCopy1")
myCopy1.from 'resources'
myCopy1.into 'target'
myCopy1.include('**/*.txt', '**/*.xml', '**/*.properties')

// 方式二: 使用特殊的语法(类似于构造函)
task myCopy2(type: Copy)
myCopy2 {
   // 在内部赋值
   from 'resources'
   into 'target'
}
// 在外部赋值，与方式一相同。(这两种方式可以混用)
myCopy2.include('**/*.txt', '**/*.xml', '**/*.properties')

// 方式三: 直接定义
task myCopy3(type: Copy) {
  from "resources"
  into "target"
  include("*")
}
```

输出

```bash
λ gradle myCopy

BUILD SUCCESSFUL in 971ms
1 actionable task: 1 executed  // 执行成功了，文件已经复制了
```

### 7.4 自定义任务模板

直接上例子，有Java基础的很容易看懂

```groovy
import javax.inject.*;

class PrintNameTask extends DefaultTask {
  
  String username
  
  @Inject // 构造器需要加上该注解
  PrintNameTask(String first, String last) {
    username = first + last
  }
  
    @TaskAction
    def print(){
        println "name is $username"
    }
  
}
// 使用tasks直接创建任务
tasks.create("myTask1", PrintNameTask, "Kim", "Zing")
myTask1.doLast {
    println "任务完成"
}
// 使用构造参数方式声明任务
task myTask2(type: PrintNameTask, constructorArgs: ["Kim", "Zing"]) {
  doLast {
    // 可以读取任务中的属性
    println "任务执行完成 $username"
  }  
}
```

输出

```bash
λ gradle myTask1 -q
name is KimZing
任务完成

λ gradle myTask2 -q
name is KimZing
任务执行完成 KimZing
```



```groovy
// 模仿内置的Copy任务
class MyCopy extends DefaultTask {

    // 注意不要使用name等内置的属性名称
    String from
    String into
		@Optional
		String include;	
  
    @TaskAction
    def copy(){
        println "copy from $from into $into include $include"
    }
}

task lifeCopy (type: MyCopy) {
    from "KimZing"
    into "GirlFriend"
    include "all"
}
```

### 7.5 任务依赖的高级用法

taskX依赖taskY的基本写法

```groovy
task taskX {
    doLast {
        println 'taskX'
    }
}

task taskY {
    doLast {
        println 'taskY'
    }
}

taskX.dependsOn taskY
```

动态指定依赖的任务, taskx依赖所有以lib开头的任务

```groovy
task taskX {
    doLast {
        println 'taskX'
    }
}

taskX.dependsOn {
    tasks.findAll { task -> task.name.startsWith('lib') }
}

task lib1 {
    doLast {
        println 'lib1'
    }
}

task lib2 {
    doLast {
        println 'lib2'
    }
}

task notALib {
    doLast {
        println 'notALib'
    }
}
```

### 7.6 对任务进行排序

> 当和denpendsOn结合使用时，将会 被忽略，使用dependsOn的定义

#### 7.6.1 `mustRunAfter`

指定taskY必须在taskX运行之后运行

```groovy
task taskX {
    doLast {
        println 'taskX'
    }
}
task taskY {
    doLast {
        println 'taskY'
    }
}
taskY.mustRunAfter taskX
```

输出

```bash
λ gradle taskX -q                                  
taskX 

kimzing@DESKTOP-0O3S28B ~/Desktop                  
λ gradle taskY -q                                  
taskY         

λ gradle taskY taskX -q                            
taskX                                              
taskY
// 两个任务单独都是可以执行的，而如果一起执行时，则会先执行taskX
```

#### 7.6.2 shouldRunAfter

没太搞懂和`mustRunAfter`的区别，搞懂了再补充

### 7.7 覆盖任务

类似java中的override

```groovy
task hello {
  doLast {
   println "hello world" 
  }
}

task hello(overwrite: true) {
  doLast {
    println "new Hello"
  }
}
```

输出

```bash
λ gradle -q hello
new Hello
```

### 7.8 任务执行的前置条件配置

某些任务需要再特定的场景才会执行，这时候可以使用前置条件进行判断

```groovy
task eat {
  doLast {
    println "吃苹果"
  }
}

def int appleCount = 1

eat.onlyIf {
  // 只有有苹果时才会执行
  appleCount > 0
}
```

### 7.9 异常的处理

```groovy
task compile {
    doLast {
        println 'We are doing the compile.'
    }
}

compile.doFirst {
    if (true) { throw new StopExecutionException() }
}
task myTask {
    dependsOn('compile')
    doLast {
        println 'I am not affected'
    }
}
```

如果任务有异常，会结束当前任务，并继续执行接下来的任务

### 7.10 禁用任务

使用任务的enabled属性进行禁用或者启用任务

```groovy
task disableMe {
    println "禁用任务后，配置阶段的语句仍然会执行"
    doLast {
        println '如果禁用了，这段话就不会打印了.'
    }
}
disableMe.enabled = false
```

### 7.11 指定任务的超时时间

```groovy
task timeoutTask() {
    doLast {
        Thread.sleep(1000)
    }
    timeout = Duration.ofMillis(1500)
}
```

## 八、项目

### 项目的一些属性

| 属性名        | 类型                                                         | 说明                               |
| :------------ | :----------------------------------------------------------- | :--------------------------------- |
| `project`     | [Project](https://docs.gradle.org/5.6.4/dsl/org.gradle.api.Project.html) | 项目实例                           |
| `name`        | `String`                                                     | 项目目录的名字                     |
| `path`        | `String`                                                     | 项目的绝对路径                     |
| `description` | `String`                                                     | 项目描述                           |
| `projectDir`  | `File`                                                       | 包含build脚本的目录.               |
| `buildDir`    | `File`                                                       | 构建的输出目录`*projectDir*/build` |
| `group`       | `Object`                                                     | 组对象                             |
### 8.1 基本定义与使用

多项目结构示例

```bash
.
├── subProject1/
├── subProject2/
├── build.gradle
└── settings.gradle
```

settings.gradle是项目的配置文件，内容如下

```
rootProject.name = 'allProject'
include 'subProject1', "subProject2"
```

build.gradle文件如下

```groovy
Closure cl = {task -> println "name is $task.project.name"}
task("hello").doLast(cl)
project(":subProject1") {
  task("hello").doLast(cl)
}
project(":subProject2") {
  task("hello").doLast(cl)
}
```

首先我们执行`gradle projects`，输出如下, 可以看出来是一个多项目结构

```bash
λ gradle projects

Root project 'allProject'
\--- Project ':subProject1'
```

接下来我们自行`hello`任务， 打印项目名称

```bash
λ gradle hello -q
name is allProject
name is subProject1
name is subProject2
```

### 8.2 为所有项目定义通用任务

使用`allprojects`可以对所有项目进行定义, 可以减少重复的任务定义

```groovy
allprojects {
    task hello {
        doLast { task ->
            println "I'm $task.project.name"
        }
    }
}
```

输出

```bash
λ gradle hello -q
I'm allProject
I'm subProject1
I'm subProject2
```

### 8.3 为所有子项目定义任务的方式 subprojects

```groovy
subprojects {
    hello {
        doLast {
            println "- I am subProject"
        }
    }
}
```

输出

```bash
- I am subProject
- I am subProject
```

### 8.4 为单个子项目定义任务

使用project(":项目名")可以获取到对应project实例，并操作

```groovy
project(':subProject1').hello {
    doLast {
        println "- I am special."
    }
}
```

输出

```bash
λ gradle hello -q
- I am special.
```

### 8.5 为特定的符合某些条件的项目定制任务

```groovy
configure(subprojects.findAll {it.name.endsWith("2")}) {
    task hello {
        doLast {
            println '- I am end with 2.'
        }
    }
}
```

输出

```bash
λ gradle hello -q
- I am end with 2.
```

### 8.6 运行指定项目的任务

在上面的示例中，执行`gradle hello` 会运行所有项目的hello任务。可不可以只运行指定项目的任务呢？分别执行下面的指令试试

```groovy
allprojects {
    task hello {
        doLast { task ->
            println "I'm $task.project.name"
        }
    }
}
```

输出

```bash
λ gradle -q hello
I'm allProject
I'm subProject1
I'm subProject2

λ gradle -q :subProject1:hello
I'm subProject1

λ gradle -q :subProject2:hello
I'm subProject2

λ gradle -q :hello
I'm allProject
```



### 8.7 分离任务

在上面的示例中，都是在根项目中进行定义的，有点臃肿。其实在项目自己目录下的build.gradle文件定义属于自己模块的任务

接下来在`subProject1`目录下新建`build.gradle`文件，并添加任务,如下

```groovy
task say {
  doLast {
    println "Say Hello"
  }
}

hello.doLast {
  println "定义在任务内部的语句"
}
```

在根目录下执行任然可以执行到该任务， hello任务也赋予了新的行为

```bash
gradle hello -q
I'm allProject
I'm subProject1
- I am subProject
- I am special.
定义在任务内部的语句
I'm subProject2
- I am subProject

λ gradle say -q
Say Hello
```

### 8.8 标准的项目结构

和Maven的项目结构是一致的(当然我们也可以自定义)，这里就不多说了，请在百度上谷歌一下->

首先在根项目创建settings.gradle文件并对多项目做添加。接下来看下常用的脚本配置，如下:

```groovy
// 所有项目配置
allprojects {
    apply plugin: 'java'
    group = 'org.gradle.sample'
    version = '1.0'
}
// 所有子项目配置
subprojects {
    apply plugin: 'war'
    //指定仓库
    repositories {
        mavenCentral()
    }
    // 指定依赖
    dependencies {
        implementation "javax.servlet:servlet-api:2.5"
    }
}
```

### 九、使用Gradle脚本操作文件

参考[官方文档](https://docs.gradle.org/5.6.4/userguide/working_with_files.html), 太多了，翻译的太累

### 十、应用Gradle插件

参考[官方文档](https://docs.gradle.org/5.6.4/userguide/plugins.html), 简单来说就是用了这些插件，九内置了很多任务行为，方便我们进行不同项目的构建。

## 十一、日志

### 11.1 日志级别

前面使用的-q参数，其实就是日志级别中的--quiet，保持安静。执行命令式加上对应的级别就会输出对应的信息。

| Option             | Outputs Log Levels                           |
| :----------------- | :------------------------------------------- |
| no logging options | LIFECYCLE and higher                         |
| `-q` or `--quiet`  | QUIET and higher                             |
| `-w` or `--warn`   | WARN and higher                              |
| `-i` or `--info`   | INFO and higher                              |
| `-d` or `--debug`  | DEBUG and higher (that is, all log messages) |

### 11.2 打印日志

Gradle内置了logger实例，可以进行日志打印，示例build.gradle

```groovy
logger.quiet('An info log message which is always logged.')
logger.error('An error log message.')
logger.warn('A warning log message.')
logger.lifecycle('A lifecycle info log message.')
logger.info('An info log message.')
logger.debug('A debug log message.')
logger.trace('A trace log message.')
```

分别执行`gradle -q` `gradle -w` `gradle -i`看看输出吧

## 十二、Java项目管理 - Jar Plugin

### 12.1 仓库定义

#### 仓库的类型和编写方式

```groovy
// 从某个文件夹下载
repositories {
    flatDir {
        dirs 'lib'
    }
    flatDir {
        dirs 'lib1', 'lib2'
    }
}
// Maven仓库中心
repositories {
    mavenCentral()
}
// Jcenter中心
repositories {
    jcenter()
}
// Google仓库中心
repositories {
    google()
}
// 本地Maven仓库
repositories {
    mavenLocal()
}
// 自定义的仓库地址
repositories {
    maven {
        url "http://repo.mycompany.com/maven2"
    }
}
// 需要密码的仓库的配置方式- 基本认证方式
repositories {
    maven {
        url 'https://repo.mycompany.com/maven2'
        credentials {
            username "user"
            password "password"
        }
        authentication {
            basic(BasicAuthentication)
        }
    }
}
// 需要密码的仓库的配置方式- Header认证方式
repositories {
    maven {
        url "http://repo.mycompany.com/maven2"
        credentials(HttpHeaderCredentials) {
            name = "Private-Token"
            value = "TOKEN"
        }
        authentication {
            header(HttpHeaderAuthentication)
        }
    }
}
```

### 12.2 依赖的作用域

| 名称                            | 插件        | 作用域                   | 备注                                   |
| ------------------------------- | ----------- | ------------------------ | -------------------------------------- |
| **implementation**（常用）      | jar         | 编译和运行时             | 各个模块的依赖相互隔离，不能相互引用   |
| **compileOnly**（常用）         | jar         | 仅在编译时               |                                        |
| **runtimeOnly**（常用）         | jar         | 仅在运行时               | 比如mysql依赖<br />                    |
| **annotationProcessor**（常用） | jar         | 编译时                   | 比如lombok依赖                         |
| **api**                         | jar-library | 与**implementation**类似 | 不同的是该依赖会向上层项目传递，<br /> |
|                                 |             |                          |                                        |
| **compileClasspath**            | jar         |                          |                                        |
| **runtime** (已过期)            | jar         |                          |                                        |
| **compile**(已过期)             | jar         |                          | 等同于implementation                   |
| **runtimeClasspath**            | jar         |                          | 和runtime类似                          |
| **testCompile**(已过期)         | jar         |                          |                                        |
| **testImplementation**          | jar         | 测试时，编译和运行时     |                                        |
| **testCompileOnly**             | jar         | 测试时，仅在编译时       |                                        |
| **testCompileClasspath**        | jar         |                          |                                        |
| **testRuntime(已过期)**         | jar         |                          |                                        |
| **testRuntimeOnly**             | jar         | 测试时，仅在运行时       |                                        |
| **testRuntimeClasspath**        | jar         |                          | 和runtime类似                          |
| **archives**                    | jar         |                          | 在执行uploadArchives任务时会使用       |
| **default**                     | jar         |                          | 依赖于此项目的默认配置                 |

### 12.3 依赖的类型

适用所有作用域

```groovy
// 依赖指定坐标， 有多种书写方式
runtimeOnly group: 'org.springframework', name: 'spring-core', version: '2.5'
runtimeOnly 'org.springframework:spring-core:2.5',
            'org.springframework:spring-aop:2.5'
runtimeOnly('org.hibernate:hibernate:3.0.5') {
        transitive = true
    }
// 依赖本地jar文件或目录中的全部jar文件
dependencies {
    runtimeOnly files('libs/a.jar', 'libs/b.jar')
    runtimeOnly fileTree('libs') { include '*.jar' }
}
// 项目依赖
dependencies {
    implementation project(':shared')
}
```

### 12.4 依赖排除操作

```groovy
// 依赖排除-直接编写
dependencies {
    implementation('log4j:log4j:1.2.15') {
        exclude group: 'javax.jms', module: 'jms'
        exclude group: 'com.sun.jdmk', module: 'jmxtools'
        exclude group: 'com.sun.jmx', module: 'jmxri'
    }
}
// 依赖排除-通过配置进行编写
configurations {
    implementation {
        exclude group: 'javax.jms', module: 'jms'
        exclude group: 'com.sun.jdmk', module: 'jmxtools'
        exclude group: 'com.sun.jmx', module: 'jmxri'
    }
}

dependencies {
    implementation 'log4j:log4j:1.2.15'
}

// 强制使用指定版本-直接编写
dependencies {
    implementation 'org.apache.httpcomponents:httpclient:4.5.4'
    implementation('commons-codec:commons-codec:1.9') {
        force = true
    }
}
// 强制使用指定版本-通过配置进行编写
configurations {
    compileClasspath {
        resolutionStrategy.force 'commons-codec:commons-codec:1.9'
    }
}

dependencies {
    implementation 'org.apache.httpcomponents:httpclient:4.5.4'
}

// 禁用传递依赖解析-直接编写
dependencies {
    implementation('com.google.guava:guava:23.0') {
        transitive = false
    }
}

// 禁用传递依赖解析-配置
configurations.all {
    transitive = false
}

dependencies {
    implementation 'com.google.guava:guava:23.0'
}
```





### 12.5 定义源码版本和编译版本

```groovy
plugins {
    id 'java'
}

sourceCompatibility = '1.8'
targetCompatibility = '1.8'
```

### 12.6 指定编码

尚未验证

```groovy
tasks.withType(JavaCompile) {
    options.encoding = "UTF-8"
}
```

