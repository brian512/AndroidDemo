##[Android沉浸式状态栏的实现方案探讨](http://blog.csdn.net/brian512/article/details/52755539)

多次尝试实现Android沉浸式状态栏，资料很多也很杂。并且有好几种实现方案，网上有好些资料把几种方案都混在一起，暂时把效果实现了，但是遇到问题后就蛋疼了。于是，这两天我就把从根源上把这几种方案的原理都整理了一下。主要有四种方案，有的方案还可以细分：
 1. WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS；
 2. Window.setStatusBarColor(int)；
 3. View.setSystemUiVisibility(visibility);
 4. Window.setAttributes(params)；

---------
首先创建一个demo，在LinearLayout布局中显示一个全屏的ImageView：
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/blue"
    android:orientation="vertical">

    <ImageView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:scaleType="fitXY"
        android:src="@drawable/bg"/>

</LinearLayout>
```
为了让效果明显，将decorview的背景设置为黄色：
```
getWindow().getDecorView().setBackgroundColor(Color.YELLOW);
```
把主题背景设置为红色 
```
    <style name="AppTheme" parent="@style/BaseAppTheme">
        <item name="android:background">@color/red</item>
    </style>
```
运行效果如下图： 
![这里写图片描述](http://img.blog.csdn.net/20161008135124156)

###**方案一** FLAG_TRANSLUCENT_STATUS全屏布局
在values-v19目录下增加一个style：
```xml
    <style name="AppTheme" parent="@style/BaseAppTheme">
        <item name="android:windowTranslucentStatus">true</item>
        <item name="android:windowTranslucentNavigation">true</item>
        <item name="android:background">@color/red</item>
    </style>
```
或者使用java代码实现：
```java
// 在UI线程任何时候都可以调用
    private void setTranslucentSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            // Translucent status bar
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // Translucent navigation bar
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }
```
![这里写图片描述](http://img.blog.csdn.net/20161008133636414)
 
可以看到，图片已经延伸至状态栏和导航栏下面了，效果类似QQ空间头部背景图片。
有很多布局是不能这样显示的，因为状态栏的信息会覆盖住布局顶部的内容，于是我们在根布局设置一个属性：
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/blue"
    android:fitsSystemWindows="true"
    android:orientation="vertical">
```

![这里写图片描述](http://img.blog.csdn.net/20161008135106494)
 为了效果明显一点，我把背景设置为蓝色。可以看到此时的效果与第一步的区别仅仅是状态栏和导航栏的颜色为根布局的背景色，并且没有看到decorview的黄色背景。通过Hierarchy View也可以看到，导航栏和状态栏的view不见了，也就是全屏都是我们自己的Layout。所以说设置`android:fitsSystemWindows="true"` 之后，系统自动给我们的根布局加上了padding，并且paddingTop为状态栏高度，paddingBottom为导航栏高度。
 ![这里写图片描述](http://img.blog.csdn.net/20161008152650997)
 在这里引申出一个问题：状态栏和导航栏的view已经不见了，为何状态栏的信息还能显示？在此只是猜想一下是系统直接绘制在上面的，具体还得扒源码。到这里，之前我们说改变状态栏的颜色，这个说法是有问题的，因为我们改变的仅仅是状态栏下面的背景而已，状态栏本身只是一个悬浮在界面最上层的一行信息，而下面等高的一个view让我们认为状态栏的存在。等会儿顺便说一下如何隐藏状态栏信息，即真正的全屏。
![这里写图片描述](http://img.blog.csdn.net/20161009101400198)

###**方案二** 通过Window.setStatusBarColor(int)设置状态栏颜色

 Android5.1加入了新的方法来设置状态栏和导航栏背景色
```java
    private void setTranslucentSystemUI() {
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // Translucent status bar
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // Translucent navigation bar
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }
```
![这里写图片描述](http://img.blog.csdn.net/20161008145604017)
导航栏的颜色为decorview的黄色（三个操作按钮还在，只是看不出清楚），状态栏则显示了红色的主题背景，说明设置的主题背景是赋值到DecorView里面的mContentParent（DecorView里的子节点LinearLayout）的背景。
```
            window.setStatusBarColor(Color.GREEN);
            window.setNavigationBarColor(Color.GREEN);
```
![这里写图片描述](http://img.blog.csdn.net/20161008141024009)
![这里写图片描述](http://img.blog.csdn.net/20161009113205685)
上面两张图可以看出，状态栏和导航栏的背景view是覆盖在`DecorView.LinearLayout`上面的。顺便提醒一下，下面这样设置状态栏是无效的：
```
            window.setStatusBarColor(Color.GREEN);
            window.setNavigationBarColor(Color.GREEN);
            // Translucent status bar
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // Translucent navigation bar
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
```
从上面的分析也能知道，设置`FLAG_TRANSLUCENT_STATUS`后，状态栏背景view都不在了，设置颜色自然是没有效果的。

###**方案三** View.setSystemUiVisibility(visibility)
`View.setSystemUiVisibility(visibility)`设置状态栏与导航栏显示与否
在SDK16加入了一些属性（`View.SYSTEM_UI_FLAG_XXX`）来控制系统UI（状态栏和导航栏）

```java
    private void setSystemUIVisible(boolean visible) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            return;
        }
        int visibility = getWindow().getDecorView().getSystemUiVisibility();
        if (visible) {
            visibility &= (~View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
                            & (~View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
//                            & (~View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
                            & (~View.SYSTEM_UI_FLAG_FULLSCREEN)
//                            & (~View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
            ;
        } else {
            visibility |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_FULLSCREEN // 隐藏状态栏
                            
                            // 全屏布局，状态栏（非透明背景）会盖在布局上
                            // 与SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN一起使用后，可全屏显示布局
//                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN 
                                                                    
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // 隐藏状态栏
                            
                            // 全屏布局，导航栏（非透明背景）会盖在布局上
//                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION 
            ;
        }
        getWindow().getDecorView().setSystemUiVisibility(visibility);
    }
```
在设置了`FLAG_TRANSLUCENT_STATUS`和`FLAG_TRANSLUCENT_NAVIGATION`之后，已经是全屏布局了，所以再增加`SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN`和`SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION`是没有效果的，如果不用`FLAG_TRANSLUCENT_STATUS`的话，切换全屏布局和非全屏布局会导致界面重新布局而抖动，因为根容器的大小会发生改变。`SYSTEM_UI_FLAG_IMMERSIVE_STICKY`属性可以让界面在顶部下滑或底部上滑时显示出SYSTEM_UI，并在3S后自动隐藏。

###**方案四** Window.setAttributes(params)
还有一个方式动态切换全屏和非全屏（其实也是状态栏的显示与否）：
```java
    private void setFullScreenEnable(boolean enable) {
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        if (enable) {
            // 布局占用状态栏，并隐藏状态栏，不影响导航栏
            params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN; 
        } else {
            params.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        // 全屏布局，状态栏和导航栏覆盖在布局上
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS); 
        window.setAttributes(params);
    }
```
设置`FLAG_LAYOUT_NO_LIMITS`属性也可以将布局延伸到**状态栏和导航栏**下面，也就是全屏布局。然后再切换`FLAG_FULLSCREEN`属性时，只是隐藏与显示状态栏信息的效果。如果没有设置`FLAG_LAYOUT_NO_LIMITS`属性，切换`FLAG_FULLSCREEN`时，根容器的大小也会改变，所以会有界面抖动的情况。

------------
具体的效果实现就可以参照上面介绍的几个来实现，或者组合一下。欢迎留言讨论
 
参考：[与Status Bar和Navigation Bar相关的一些东西](http://angeldevil.me/2014/09/02/About-Status-Bar-and-Navigation-Bar/)
 
 