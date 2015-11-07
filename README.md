#RxWeather

![](http://upload-images.jianshu.io/upload_images/268450-f83f74893e35b51b.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

**Architecting  Android with RxJava**



-----------------


感谢[saulmm](https://github.com/saulmm)：

- [Material-Movies](https://github.com/saulmm/Material-Movies)

感谢[android10](https://github.com/android10):

- [Architecting Android…The clean way?](http://fernandocejas.com/2014/09/03/architecting-android-the-clean-way/)

- [Architecting Android…The evolution](http://fernandocejas.com/2015/07/18/architecting-android-the-evolution/)

通过解读两个非常优秀开发者的文章和项目，加入了Rxbus。

公开API接口采用[和风天气](http://www.heweather.com/)

-----------------

项目整体基于[Model View Presenter](http://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93presenter)，虽然实现了一些转场动画，但是很抱歉，目前这个版本并没有遵循[Material Design](http://www.google.com/design/spec/material-design/introduction.html)设计规范 : (



**结构分层：**

- **presentation：** Presenters、Views、ErrorHanding

- **usercase：** UseCase

- **model：** Service、Response、Entities

- **common：** Util、Constants、BusEvent、Rxbus、RxAndroid

-----------------


欢迎关注我的[博客](http://www.jianshu.com/users/df40282480b4)和[新郎微博](http://weibo.com/5367097592/profile?rightmod=1&wvr=6&mod=personinfo)

待续。。。。

