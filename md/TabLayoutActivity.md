##[ViewPager实现页卡的最新方法--简洁的TabLayout](http://blog.csdn.net/brian512/article/details/51793430)

Google在2015年的Google IO大会上更新了Design Support Library，里面提供了几个封装好的MeterDesign风格控件，其中包括：

1. TextInputLayout ： 使用TextInputLayout将EditText进行了封装，提示信息会变成一个显示在EditText之上的floating label，这样用户就始终知道他们现在输入的是什么，而且过度动画是平滑的。还可以在下方通过setError设置Error提示
![这里写图片描述](http://img.blog.csdn.net/20160630160218850)

2. FloatingActionButton ： 负责显示界面基本操作的圆形按钮
![这里写图片描述](http://img.blog.csdn.net/20160630160229694)

3. Snackbar ： 为一个操作提供轻量级、快速的反馈
![这里写图片描述](http://img.blog.csdn.net/20160630160239833)

4. TabLayout ： 既实现了固定的选项卡（View的宽度平均分配），也实现了可滚动的选项卡（View宽度不固定同时可以横向滚动）
![这里写图片描述](http://img.blog.csdn.net/20160630160253723)

5. NavigationView ： 通过提供抽屉导航所需的框架让实现更简单

具体的介绍可以看看：[http://android-developers.blogspot.com/2015/05/android-design-support-library.html](http://android-developers.blogspot.com/2015/05/android-design-support-library.html)
这篇文章里面，记录一下TabLayout 的使用，还有几个不得不说的坑！

先看效果：
![这里写图片描述](http://img.blog.csdn.net/20160630194517991)
上面是TabLayout ，下面是ViewPager，最简单的一个demo。下面介绍一些如何实现。
首先是导入，在build.gradle 文件中加上这段代码
```
compile 'com.android.support:design:23.2.0'
```
然后Activity的布局文件内容如下：
```xml
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent" >

    <android.support.design.widget.TabLayout
        xmlns:app="http://schemas.android.com/apk/res-auto"
        android:id="@+id/tabs"
        android:layout_width="150dp"
        android:layout_height="wrap_content"
        android:layout_alignParentTop="true"
        android:layout_centerHorizontal="true"
        app:tabSelectedTextColor="@color/com_bg_black"
        app:tabTextColor="@color/light_gray"/>

    <android.support.v4.view.ViewPager
        android:id="@+id/vp_view"
        android:layout_below="@id/tabs"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>
</RelativeLayout>
```
简单吧！使用TabLayout中定义的属性时需要声明
```
xmlns:app="http://schemas.android.com/apk/res-auto"
```
上面使用到的app:tabSelectedTextColor 和 app:tabTextColor 分别是tab里选中和非选中的字体颜色，也可以在Java代码里设置。
在activity代码如下
```java
public class TabLayoutActivity extends Activity {
    @BindView(R.id.tabs) TabLayout mTabLayout;
    @BindView(R.id.vp_view) ViewPager mViewPager;

    private TextView view1, view2, view3;//页卡视图
    private List<View> mViewList = new ArrayList<>();//页卡视图集合

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabview);
        ButterKnife.bind(this);

        mViewPager = (ViewPager) findViewById(R.id.vp_view);
        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        view1 = getTextView("view1");
        view2 = getTextView("view2");
        view3 = getTextView("view3");

        //添加页卡视图
        mViewList.add(view1);
        mViewList.add(view2);
        mViewList.add(view3);

        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);//设置tab模式，当前为系统默认模式
//        mTabLayout.addTab(mTabLayout.newTab().setText(mTitleList.get(0)));//添加tab选项卡

        SimplePagerAdapter mAdapter = new SimplePagerAdapter(mViewList);
        mViewPager.setAdapter(mAdapter);//给ViewPager设置适配器
        mTabLayout.setupWithViewPager(mViewPager);//将TabLayout和ViewPager关联起来。
//        mTabLayout.setTabsFromPagerAdapter(mAdapter);//给Tabs设置适配器
    }

    private TextView getTextView(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setGravity(Gravity.CENTER);
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return textView;
    }


    //ViewPager适配器
    class SimplePagerAdapter extends PagerAdapter {
        private List<View> mViewList;

        public SimplePagerAdapter(List<View> mViewList) {
            this.mViewList = mViewList;
        }

        @Override
        public int getCount() {
            return mViewList.size();//页卡数
        }
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;//官方推荐写法
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mViewList.get(position));//添加页卡
            return mViewList.get(position);
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mViewList.get(position));//删除页卡
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return "Tab_" + position;//页卡标题
        }
    }

}
```
同样的简洁，最终实现的效果是滑动切换viewpager时，TabLayout同步滑动切换；点击选中TabLayout项时，viewpager自动滑动切换到对应页面。
在xml里面，除了上面用到的两个自定义属性，还有一些其他的属性，下面罗列出来：
```java
		public static final int TabLayout_tabBackground = 3;
		public static final int TabLayout_tabContentStart = 2;
		public static final int TabLayout_tabGravity = 5;
		public static final int TabLayout_tabIndicatorColor = 0;// 设置为透明即为隐藏
		public static final int TabLayout_tabIndicatorHeight = 1;
		public static final int TabLayout_tabMaxWidth = 7;
		public static final int TabLayout_tabMinWidth = 6;
		public static final int TabLayout_tabMode = 4;
		public static final int TabLayout_tabPadding = 15;
		public static final int TabLayout_tabPaddingBottom = 14;
		public static final int TabLayout_tabPaddingEnd = 13;
		public static final int TabLayout_tabPaddingStart = 11;
		public static final int TabLayout_tabPaddingTop = 12;
		public static final int TabLayout_tabSelectedTextColor = 10;// 选中的文字颜色
		public static final int TabLayout_tabTextAppearance = 8;// 可以设置文字大小和颜色
		public static final int TabLayout_tabTextColor = 9;// 非选中状态的文字颜色
```
从上面的属性里可以看到需要设置文字大小需要通过tabTextAppearance属性来定义，从TabLayout源码发现，只有这一种方式设置字体大小。
![这里写图片描述](http://img.blog.csdn.net/20160701164103374)
并且设置文字颜色时，tabTextColor的优先级高于tabTextAppearance。

####第一坑：
```
Caused by: java.lang.IllegalArgumentException: You need to use a Theme.AppCompat theme (or descendant) with the design library.
at android.support.design.widget.ThemeUtils.checkAppCompatTheme(ThemeUtils.java:34)
```
从提示上看，是说我们使用这个library里的控件时，需要把activity的style设置为Theme.AppCompat的衍生主题。我的设置如下：
```
<style name="AppTheme" parent="Theme.AppCompat.Light.DarkActionBar">
        <!-- Customize your theme here. -->
</style>
```
探其所以然：
![这里写图片描述](http://img.blog.csdn.net/20160630201557403)
ThemeUtils.java内容较少，就直接贴出来：
```java
class ThemeUtils {
    private static final int[] APPCOMPAT_CHECK_ATTRS = { R.attr.colorPrimary };
    static void checkAppCompatTheme(Context context) {
        TypedArray a = context.obtainStyledAttributes(APPCOMPAT_CHECK_ATTRS);
        final boolean failed = !a.hasValue(0);
        if (a != null) {
            a.recycle();
        }
        if (failed) {
            throw new IllegalArgumentException("You need to use a Theme.AppCompat theme "
                    + "(or descendant) with the design library.");
        }
    }
}
```
看到这里，仿佛仅仅校验R.attr.colorPrimary属性是否存在。于是把BaseTheme改为android:Theme.Light，并添加R.attr.colorPrimary属性（要求minSdk>=21），但是结果还是闪退，log都木有。估计是后面用到了Theme.AppCompat下面的其他属性，不然校验一个无用属性也是太无厘头了。最终，还是**乖乖使用Theme.AppCompat**.Light.DarkActionBar。


####第二坑
![这里写图片描述](http://img.blog.csdn.net/20160630204532976)
上图中的tab不见了，但是下面的指示条还在，并且点击事件也都有。原因是没有覆写PagerAdapter.getPageTitle(position).
查看TabLayout的源码发现这个：
![这里写图片描述](http://img.blog.csdn.net/20160630204855090)
天地良心啊，直接把我手动添加的tab全部移除了，然后自己调用PagerAdapter.getPageTitle(position)获得标题tab。这封装果然实用，但是不看源码还得是被坑的团团转！
所以：**不用自己添加tab，只需要覆写PagerAdapter.getPageTitle(position)。**

####第三坑
![这里写图片描述](http://img.blog.csdn.net/20160731094355672)
标题栏的英文字符全部转大写了。。。这就好尴尬了，在网上找了好几天资料（PS：百度出来的结果都差不多），都是说要设置textAllCaps属性：
```
    <!--TAB文字样式-->
    <style name="MyCustomTabTextAppearance" parent="TextAppearance.Design.Tab">
        <item name="textAllCaps">false</item>
    </style>
```
然而并没有用！查看TextAppearance.Design.Tab，发现textAllCaps默认为true，也就是全部转大写
![这里写图片描述](http://img.blog.csdn.net/20160731094825720)
最后找到解决方案后，才发现是自己看源码时太关注属性值了，也是眼瞎，竟没看出来textAllCaps是自定义的一个属性，Android内定义的属性都是以android:开头的，握草！
```
    <style name="TabLayoutTextAppearance" parent="TextAppearance.Design.Tab">
        <item name="textAllCaps">false</item>
        <item name="android:textAllCaps">false</item>
    </style>
```
最终是酱紫的！
![这里写图片描述](http://img.blog.csdn.net/20160731095446348)

好了，大坑目前就发现这几个，总得来说TabLayout还是很好用的，如果这个效果如果够用的话，还是用Google自家的！

---------------------
再说一点：
![这里写图片描述](http://img.blog.csdn.net/20160630205435363)
第一个红框标识，此处只是移除之前额外添加的滑动监听，因为Adapter已经更换，所以PageChangeListener也需要更换。
下面的第二个红框标出来，是想说，里面已经封装好了**不需要重复调用TabLayout.setTabsFromPagerAdapter(mAdapter)**，也就是我上面注释掉的一行代码。