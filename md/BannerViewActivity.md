##[使用ViewPager做轮播的banner控件](http://www.jianshu.com/p/43843d927ae2)

在网上查了些资料，该类控件虽然资料很多，但是都不具体，或者说或多或少都是有些坑的。经过两天的整理，最终得到一个相对满意的结果，在此记录一下核心的东西。
下面主要是PagerAdapter的封装，其他的控件拼凑部分就不描述了。
```java
/**
 * 使用views轮播控件{@link CyclicRollView}需要实现该类的abstract方法{@link #getView(View, int)} 和 {@link #getViewCount()}
 * @author huamm
 * @param <T> 需要展示的数据源类型
 */
public abstract class CyclicViewAdapter<T> extends PagerAdapter {
    private static final String TAG = CyclicViewAdapter.class.getSimpleName();

    private static final int MAX_RECYLE_VIEW = 3;
    private LinkedList<View> mViewCache = null;
    private View mCurrentView = null;

    public CyclicViewAdapter(Context context) {
        mViewCache = new LinkedList<>();
    }

    /**
     * 返回Integer.MAX_VALUE，实现 “无限循环滚动”
     */
    @Override
    public final int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public  boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

	// 回收资源
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View contentView = (View) object;
        //在此进行控件复用
        if (mViewCache.size() < MAX_RECYLE_VIEW) {
            mViewCache.add(contentView);
        }
        Log.v(TAG, "ViewCache.size=" + mViewCache.size());
        ((ViewPager) container).removeView(contentView);
    }

    @Override
    public final Object instantiateItem(ViewGroup container, int position) {
        // 先对position进行偏移矫正
        position = convert2ListPosition(position);
        View convertView = null;
        if (!mViewCache.isEmpty()) {
            convertView = mViewCache.removeFirst();
        }

        // 让调用者定义该convertview，与数据源解绑
        View child = getView(convertView, position);
        if (child == null) {
            return convertView;
        }

        // 如果View已经在之前添加到了一个父组件，则必须先remove，否则会抛出IllegalStateException。
        ViewParent vp = child.getParent();
        if (vp != null) {
            ViewGroup parent = (ViewGroup) vp;
            parent.removeView(child);
        }
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER;
        container.addView(child, params);
        // add listeners here if necessary
        return child;
    }

    /**
     * 获取view的总数，区别于{@link #getCount()}}
     * @return
     */
    public abstract int getViewCount();

    /**
     * 使用者实现该方法，用于绑定数据源和convertView
     * @param convertView
     * @param position
     * @return
     */
    public abstract View getView(View convertView, int position);

    /**
     * 设置并保存当前显示的view
     */
    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        mCurrentView = (View) object;
    }

    /**
     * 获取当前展示的view
     * @return
     */
    public View getPrimaryItem() {
        return mCurrentView;
    }

    /**
     * 将偏移位置转为列表中的位置
     * @param position
     * @return
     */
    protected int convert2ListPosition(int position) {
        while (position < 0) {
            position = getViewCount() + position;
        }
        position %= getViewCount();
        return position;
    }

    void onDestroy() {
        if (mViewCache != null) {
            mViewCache.clear();
        }
    }
}
```

[Demo下载](http://download.csdn.net/detail/brian512/9390551)