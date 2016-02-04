#RxWeather

**Architecting  Android with RxJava**
-----------------

![](http://upload-images.jianshu.io/upload_images/268450-f83f74893e35b51b.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


**Sketch**
-----------------
这是一个简单的天气预报项目，目的是用来演示如何使用RxJava构造一个清晰的Android应用框架，并且加入了Rxbus，为了节约时间成本，界面并没有经过特别的设计，配色和控件的摆放也没有遵循[Material Design](http://www.google.com/design/spec/material-design/introduction.html)设计规范，所以，它并不具备一款Android App所应该具备的素质，为此，我深感抱歉和遗憾。

我写了一篇文章，对这个项目做更深入的描述和解释：

[Architecting Android with RxJava](http://www.jianshu.com/p/943ceaccfdff)


**Architecture**
-----------------

[*Model View Presenter：*](http://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93presenter)

![MVP-Architecture](http://upload-images.jianshu.io/upload_images/268450-3951595406461dee.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

[*The Clean Architecture：*](http://blog.8thlight.com/uncle-bob/2012/08/13/the-clean-architecture.html)

![Clean-Architecture](http://upload-images.jianshu.io/upload_images/268450-5e567202af6e2671.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)



**Project Structure**
-----------------

![](http://upload-images.jianshu.io/upload_images/268450-377f30361e6fc627.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

- **presentation：** Presenters、Views、Exceptions

- **usercase：** UseCase

- **model：** Service、Entities

- **common：** Util、Constants、Event、RxBus、RxAndroid

**Public API**
-----------------

使用[百度地图](http://developer.baidu.com/map/)获取位置信息。

使用[和风天气](http://www.heweather.com/)公开接口，获取一周天气预报（暂不支持经纬度查询）。

使用[Openweathermap](http://openweathermap.org/)提供的部分资料。


**Usage**
-----------------

首先，因为目前这个版本不支持分页加载，所以，没有提供上拉加载功能。

其次，除了HeaderView天气是根据当前位置获取之外，其余天气均通过读取**domain module**下的**city.txt**文件来获取一周内天气。

通过以下步骤进行修改或增加：

1. 下载Openweathermap提供的[资料](http://bulk.openweathermap.org/sample/)，**city.list.json.gz**这个文件，找到需要的城市json字符串，复制到**city.txt**中**cities**数组中。

2. 在和风天气提供的[国内城市ID列表](http://www.heweather.com/documents/cn-city-list)中，找到对应城市的**ID**进行替换。

最后，搜索功能，输入城市地区的时候，请不要以“市”、“区”结尾，例如：用“北京”代替“北京市”，“朝阳”代替“朝阳区”。


**ScreenShot**
-----------------

![](http://i13.tietuku.com/37464c4740690777.gif)

**Developed By**
-----------------

- 小鄧子 - Hi4Joker@gmail.com

[小鄧子的简书](http://www.jianshu.com/users/df40282480b4/latest_articles)
 
[小鄧子的慕课网专题](http://www.imooc.com/myclub/article/uid/2536335)

<a href="http://weibo.com/5367097592/profile?rightmod=1&wvr=6&mod=personinfo">
  <img alt="Follow me on Weibo" src="http://upload-images.jianshu.io/upload_images/268450-50e41e15ac29b776.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240" />
</a>

<a href="http://chuantu.biz/t2/18/1446906570x1822611354.png">
  <img alt="Follow me on Wechat" src="http://upload-images.jianshu.io/upload_images/268450-1025666a7a10ec97.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240" />
</a>


**Acknowledgments**
-----------------

- [Material-Movies](https://github.com/saulmm/Material-Movies) - Saul Molinero

- [Architecting Android…The clean way?](http://fernandocejas.com/2014/09/03/architecting-android-the-clean-way/) - Fernando Cejas

- [Architecting Android…The evolution](http://fernandocejas.com/2015/07/18/architecting-android-the-evolution/) - Fernando Cejas

- [What is all this Clean Architecture jibber-jabber about? - Part 1](http://pguardiola.com/blog/clean-architecture-part-1/) - Pablo Guardiola

- [What is all this Clean Architecture jibber-jabber about? - Part 2](http://pguardiola.com/blog/clean-architecture-part-2/) - Pablo Guardiola 

