##[开源中国（oschina）Android客户端主页返回按钮效果实现](http://www.jianshu.com/p/00461fbfbcaf)

先看看效果![](http://upload-images.jianshu.io/upload_images/2075640-81a68c4b467dfd61?imageMogr2/auto-orient/strip)开源中国的Android客户端的返回按钮就是这样子的，刚开始看的时候感觉好酷，然后就看源码，找着找着发现其实是Android封装好了的一个控件DrawerArrowDrawable，但是这个只是在Android5.0才加入的，于是乎就看了一下这个控件的源码，发现很容易就能抠出来单独用。于是，我用抠出来的DrawerArrowDrawable写了个demo：https://github.com/brian512/TestDrawerArrowDrawable
当然我也把该控件加入到我的[CodeBlog客户端](http://www.wandoujia.com/apps/com.brian.csdnblog)里面了：
```java
        mArrowDrawable = new DrawerArrowDrawable(this);
        mArrowDrawable.setColor(getResources().getColor(R.color.white));
        mTitleBar.setLeftDrawable(mArrowDrawable);
        sm.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScroll(float percentOpen) {
                mArrowDrawable.setProgress(percentOpen);
            }
        });
```
对，用起来就是这么简单。其实，这个控件就是需要不断的调用setProgress(percentOpen)来更新绘制的进度，然后在draw(Canvas canvas)方法里面根据percentOpen计算当前应该绘制的位置和图形：
```java
            final float rotation = lerp(0, ARROW_HEAD_ANGLE, mProgress * 2 - 1);
            final float upStartX = Math.round(mSize/2 * Math.cos(rotation));
            final float upStartY = Math.round(mSize/2 * Math.sin(rotation));

			// 设置线条起点
            mPath.moveTo(mOffsetLeft + mSize/2 - upStartX, mOffsetTop + mSize - upStartY);
            // 设置线条需要绘制的dx、dy，也就是在x轴的偏移量和在y轴的偏移量
            mPath.rLineTo(2 * upStartX, upStartY * 2);

            mPath.moveTo(mOffsetLeft + mSize/2 - upStartX, mOffsetTop + mSize + upStartY);
            mPath.rLineTo(2 * upStartX, upStartY * -2);
```
其中，mProgress为传入的percentOpen值，即进度；ARROW_HEAD_ANGLE为线条旋转最大角度；也就是说，这个类只能用于三杠和图标的切换动作（其实旋转角度及进度的计算是固定的，但是可以设置gap，size等参数实现其他类似的效果，但是变化不会太大），当然这个思路还是可以借鉴的：根据滑动的百分比来同步更新过度控件，其实在微信页面滑动切换时下面tab渐隐渐显的效果也是这个思路，当然还可以衍生出很多其他好玩的东西。更新一个效果：
![](http://upload-images.jianshu.io/upload_images/2075640-b802b4d4cf2a7f86?imageMogr2/auto-orient/strip)
这个效果就是根据动画更新传过来的插入值来更新mArrowDrawable的状态
```java
        alphaAnimator.addUpdateListener(new AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float)animation.getAnimatedValue();
                mArrowDrawable.setProgress(value);
            }
        });
```
具体的可以看源码：
![](http://upload-images.jianshu.io/upload_images/2075640-f50ae6b7a36c8666?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)demo：https://github.com/brian512/TestDrawerArrowDrawable

-------------
CodeBlog是我做的一个编程技术学习客户端，集成了很多技术网站上的博客，
[应用宝详情页](http://sj.qq.com/myapp/detail.htm?apkName=com.brian.csdnblog)
下个版本就会集成上面讲了那个控件。