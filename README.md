# AJSpider
一款基于Reactor线程模型的java爬虫框架
# AJSprider
[![](https://jitpack.io/v/zhuchangwu/AJSpider.svg)](https://jitpack.io/#zhuchangwu/AJSpider)

## 概述

AJSprider是笔者基于Reactor线程模式+Jsoup+HttpClient封装的一款轻量级java多线程网络爬虫框架,简单上手,小白也能玩爬虫, 
使用本框架,只需要关注如何解析(提供了无脑的匹配取值方法),而不闭关心线程的调度,源码的下载;

本项目仅供学习使用,禁止任何人用它非法盈利

## 坐标

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.zhuchangwu</groupId>
    <artifactId>AJSpider</artifactId>
    <version>1.0.0.SNAPSHOT</version>
</dependency>
```

## 使用说明

使用方法简单的没商量,三步打完收工

* 在自己的项目中引入坐标
* 继承`SpiderSingleThreadExecutor<T>`实现它的抽象方法
* 在main方法,创建启动器类`SpiderBootStrap`完成爬虫的启动


ok,现在进行第二步,重写`SpiderSingleThreadExecutor<T>`的抽象方法,他有两个抽象方法,子类必须实现,如下:

### 解析:
* 入参1: var1是框架根据url下载下来的String类型的html的源码,**需要用户把这里面需要的属性从html中解析下来封装进新创建的java对象中**
* 入参2: var2是框架自定义的容器,里面存放着两个集合
  * 集合1: 盛放用户在第一步新创建的对象并且已经付好值的对象
  * 集合2: 盛放需要下载二级任务 (比如,拿新闻来说,新闻的标题在url1上,点击标题查看新闻体进入的新的url算作是二级任务)
* 入参3: 框架提供的工具类,辅助第一步的解析
* **返回值: 将入参位置的容器返回**
```java
protected abstract SpiderSingleThreadExecutor<T>.SpiderContainer<T> resolution1(String var1, SpiderSingleThreadExecutor<T>.SpiderContainer<T> var2, SpiderResolutionUtil var3);
```

### 解析拓展:
如果用户存在二级任务,需要用户重写`SpiderSingleThreadExecutor`的`resolution2`,使用方式和`resolution1`相同


* 入参1: 存放的是 根据用户在`resolution1()`中放入容器的url集合批量下载的对应的html源码
* 入参2: spiderContainer是用户在`resolution1()`中返回的容器
* 入参3: 工具类,辅助用户将入参1html数组中的源码,解析进容器中的bean集合中
* 返回值: 将入参2返回
```java
protected SpiderSingleThreadExecutor<T>.SpiderContainer<T> resolution2(String[] htmls, SpiderSingleThreadExecutor<T>.SpiderContainer<T> spiderContainer, SpiderResolutionUtil util) {
}
```

### 持久化

* 入参1: 是用户自己解析并封装的容器中的bean集合
* 入参2: 工具类,辅助持久化

```java
public abstract void persistence(List<T> var1, PersistenceUtil<T> var2);
```

### 启动爬虫
创建启动器对象

* 添加任务队列
* 初始化线程执行器组
  * 入参1: 开启的线程数(不填,默认是2*CPU核数)
  * 入参2: 用户自定义的`SpiderSingleThreadExecutor`的实现类

```java
new SpiderBootStrap()
            .setTaskUrlQueue(taskQueue)
            .initThreadExcutorGroup(1,MyExecutor.class)
            .build();

```

## 完整Demo-拉取新闻

![](https://img2018.cnblogs.com/blog/1496926/201907/1496926-20190730122856768-2094657685.png)


快捷键F12,观察需要爬取的网页的源码,DIY解析过程(使用提供的辅助类基础的解析都ok,当然你是一个正则大牛,按自己的解析方式也很好)

```java
public class MyThreadExcutor extends SpiderSingleThreadExecutor<News> {

protected SpiderContainer<News> resolution1(String s, SpiderContainer<News> spiderContainer, SpiderResolutionUtil spiderResolutionUtil) {

    // 观察上图,我需要的新闻信息在一个id为wp_news_w6的div下
    // 选择如下方法,根据id以及标签名获取出li的数组
    String[] lis = spiderResolutionUtil.getElementsByIdAndTaggetName(s, "wp_news_w6", "ul", "li");

    // 大家一定要注意, 解析的步骤是一遍遍历上面的数组,一遍解析它,每次循环都创建一个新的对象盛放解析出来的字段
    for (int i = 0; i < lis.length; i++) {
        String html = lis[i];
        News qluNew = new News();

        // 使用工具方法,把用户提供的 前后缀 之间的值取出来
        // 注意了, 这里的前后缀一定得是先把源码输出到控制台,再复制过来
        String title = spiderResolutionUtil.getValueByPrefixSuffix(html, "\">", "</a></span> <span class=");
        String time = spiderResolutionUtil.getValueByPrefixSuffix(html, "<span class=\"news_meta\">", "</span> </li>");
        String url1 = spiderResolutionUtil.getValueByPrefixSuffix(html, "class=\"news_title\"><a href=\"", "\" target=\"_blank\"");


        // 将解析出来的值存放在用户创建出来的对象中
        qluNew.setTitle(title);
        qluNew.setDate(time);

        // 大家可以看到上面的图片,只用标题,时间,新闻体在二级url中,需要用户在这里完成拼接
        String perfix = "http://www.qlu.edu.cn";
        String targetUrl = perfix + url1;



        // 推荐大家在解析拼接二级url时多加几层判断,保证二级url的正确性
        // 我们学校的新闻模块,就存在使用中不同的url的现象, 拼接出来的效果是这个样"http://www.qlu.edu.cnhttp://2019sdh.qlu.edu.cn/2019/0305/c7334a122472/page.htm";
        // 当时也挺蒙的,不过在700条新闻中,大概存在5条
        // 我的处理是直接跳过这个url, 如果不处理,框架根据错误的url下载,解析就终止了
        if (targetUrl.substring(5, targetUrl.length()).contains("http:")) {
            //  说明上面的url拼接从新处理这个 url
            //  url不同很大程度上意味着  resolution2()  按照不同的模板解析
            continue; 
        }

        //最后,别忘了创建的bean添加的容器中,往后传播
        // 第一步
        spiderContainer.getBeanList().add(qluNew);
        // 第二步
        spiderContainer.getUrlList().add(perfix + url1);

    }
    // 返回容器
    return spiderContainer;
}
```

`resolution2()` 并不是抽象方法,只有当存在二级任务时,用户选择实现
```java
    @Override
    protected SpiderContainer<News> resolution2(String[] htmltxt, SpiderContainer<News> spiderContainer, SpiderResolutionUtil util) {
        
        // 遍历入参1中的下载好了的源码, 从新解析出新闻体的新的字段放入容器中的bean集合
        for (int i = 0; i < htmltxt.length; i++) {
            String body = util.getFirstElementValueByClass(htmltxt[i], "wp_articlecontent");
            spiderContainer.getBeanList().get(i).setBody(body);
        }

        for (News news : spiderContainer.getBeanList()) {
            System.out.println("Thread.name = "+Thread.currentThread().getName()+news);
        }
        // 返回容器
        return spiderContainer;
    }
```

持久化,用户根据自己的需求,选择如何持久化, list中存放的是前面用户解析出来的bean的集合

```java

    // persistenceUtil可以持久化图片到本地,前提是bean中仅有一个图片的url字段
    public void persistence(List<News> list, PersistenceUtil<News> persistenceUtil) {

    }
}

```

启动:
```java
public static void main(String[] args) {

    // 创建任务队列, 任意队列都可以,不要求线程安全
    LinkedBlockingQueue<String> taskQueue = new LinkedBlockingQueue();
    // 假设在准备任务
        String url ="http://www.qlu.edu.cn/38/list.htm";
        taskQueue.offer(url);

        for (int i=2;i<50;i++){
        String url2 = "http://www.qlu.edu.cn/38/list"+i+".htm";
        taskQueue.offer(url2);
        }

    SpiderBootStrap spiderBootStrap = new SpiderBootStrap();
    spiderBootStrap
            .initThreadExcutorGroup(10,MyThreadExcutor.class)
            .setTaskUrlQueue(taskQueue)
            .build();
}
```

## 重要的事情说三遍

**使用工具方法,需要的 前后缀 是需要从编译器的控制台复制过来的,直接赋值网页上的无效**

**使用工具方法,需要的 前后缀 是需要从编译器的控制台复制过的,直接赋值网页上的无效**

**使用工具方法,需要的 前后缀 是需要从编译器的控制台复制过的,直接赋值网页上的无效**

---

**笔者水平有限,请大佬批评指教!, 有任何issue请联系笔者, 如果您觉得还不错,欢迎star**
