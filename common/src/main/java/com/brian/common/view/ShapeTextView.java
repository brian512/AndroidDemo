package com.brian.common.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.Gravity;

import com.brian.common.R;

/**
 * 超级TextView  实现shape所有的属性
 *
 * 所有与shape相关的属性设置之后调用此方法才生效
 *
 * https://github.com/lygttpod/SuperTextView/blob/master/library/src/main/java/com/allen/library/SuperButton.java
 */

public class ShapeTextView extends AppCompatTextView {

    private Context mContext;

    private static final int defaultColor = Color.TRANSPARENT;
    private static final int defaultSelectorColor = Color.TRANSPARENT;

    private int mSolidColor;
    private int mSelectorPressedColor;
    private int mSelectorDisableColor;
    private int mSelectorNormalColor;

    private float mCornersRadius;
    private float mCornersTopLeftRadius;
    private float mCornersTopRightRadius;
    private float mCornersBottomLeftRadius;
    private float mCornersBottomRightRadius;

    private int mStrokeWidth;
    private int mStrokeColor;

    private float mStrokeDashWidth;
    private float mStrokeDashGap;

    private int mSizeWidth;
    private int mSizeHeight;

    private int mGradientOrientation;

    private int mGradientAngle;
    private int mGradientCenterX;
    private int mGradientCenterY;
    private int mGradientGradientRadius;

    private int mGradientStartColor;
    private int mGradientCenterColor;
    private int mGradientEndColor;

    private int mGradientType;

    //"linear"	线形渐变。这也是默认的模式
    private static final int LINEAR = 0;
    //"radial"	辐射渐变。startColor即辐射中心的颜色
    private static final int RADIAL = 1;
    //"sweep"	扫描线渐变。
    private static final int SWEEP = 2;

    private boolean mGradientUseLevel;

    private boolean mUseSelector;


    //shape的样式
    public static final int RECTANGLE = 0;
    public static final int OVAL = 1;
    public static final int LINE = 2;
    public static final int RING = 3;


    //渐变色的显示方式
    public static final int TOP_BOTTOM = 0;
    public static final int TR_BL = 1;
    public static final int RIGHT_LEFT = 2;
    public static final int BR_TL = 3;
    public static final int BOTTOM_TOP = 4;
    public static final int BL_TR = 5;
    public static final int LEFT_RIGHT = 6;
    public static final int TL_BR = 7;

    //文字显示的位置方式
    public static final int TEXT_GRAVITY_CENTER = 0;
    public static final int TEXT_GRAVITY_LEFT = 1;
    public static final int TEXT_GRAVITY_RIGHT = 2;
    public static final int TEXT_GRAVITY_TOP = 3;
    public static final int TEXT_GRAVITY_BOTTOM = 4;


    private int mShapeType;

    private int mGravity;

    private GradientDrawable mGradientDrawable;


    public ShapeTextView(Context context) {
        this(context, null);
    }

    public ShapeTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShapeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        getAttr(attrs);
        init();
    }

    private void getAttr(AttributeSet attrs) {
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.ShapeTextView);

        mGravity = typedArray.getInt(R.styleable.ShapeTextView_sGravity, 0);

        mShapeType = typedArray.getInt(R.styleable.ShapeTextView_sShapeType, GradientDrawable.RECTANGLE);

        mSolidColor = typedArray.getColor(R.styleable.ShapeTextView_sSolidColor, defaultColor);

        mSelectorPressedColor = typedArray.getColor(R.styleable.ShapeTextView_sSelectorPressedColor, defaultSelectorColor);
        mSelectorDisableColor = typedArray.getColor(R.styleable.ShapeTextView_sSelectorDisableColor, defaultSelectorColor);
        mSelectorNormalColor = typedArray.getColor(R.styleable.ShapeTextView_sSelectorNormalColor, defaultSelectorColor);

        mCornersRadius = typedArray.getDimensionPixelSize(R.styleable.ShapeTextView_sCornersRadius, 0);
        mCornersTopLeftRadius = typedArray.getDimensionPixelSize(R.styleable.ShapeTextView_sCornersTopLeftRadius, 0);
        mCornersTopRightRadius = typedArray.getDimensionPixelSize(R.styleable.ShapeTextView_sCornersTopRightRadius, 0);
        mCornersBottomLeftRadius = typedArray.getDimensionPixelSize(R.styleable.ShapeTextView_sCornersBottomLeftRadius, 0);
        mCornersBottomRightRadius = typedArray.getDimensionPixelSize(R.styleable.ShapeTextView_sCornersBottomRightRadius, 0);

        mStrokeWidth = typedArray.getDimensionPixelSize(R.styleable.ShapeTextView_sStrokeWidth, 0);
        mStrokeDashWidth = typedArray.getDimensionPixelSize(R.styleable.ShapeTextView_sStrokeDashWidth, 0);
        mStrokeDashGap = typedArray.getDimensionPixelSize(R.styleable.ShapeTextView_sStrokeDashGap, 0);

        mStrokeColor = typedArray.getColor(R.styleable.ShapeTextView_sStrokeColor, defaultColor);

        mSizeWidth = typedArray.getDimensionPixelSize(R.styleable.ShapeTextView_sSizeWidth, 0);
        mSizeHeight = typedArray.getDimensionPixelSize(R.styleable.ShapeTextView_sSizeHeight, dip2px(mContext, 48));

        mGradientOrientation = typedArray.getInt(R.styleable.ShapeTextView_sGradientOrientation, -1);

        mGradientAngle = typedArray.getDimensionPixelSize(R.styleable.ShapeTextView_sGradientAngle, 0);
        mGradientCenterX = typedArray.getDimensionPixelSize(R.styleable.ShapeTextView_sGradientCenterX, 0);
        mGradientCenterY = typedArray.getDimensionPixelSize(R.styleable.ShapeTextView_sGradientCenterY, 0);
        mGradientGradientRadius = typedArray.getDimensionPixelSize(R.styleable.ShapeTextView_sGradientGradientRadius, 0);

        mGradientStartColor = typedArray.getColor(R.styleable.ShapeTextView_sGradientStartColor, -1);
        mGradientCenterColor = typedArray.getColor(R.styleable.ShapeTextView_sGradientCenterColor, -1);
        mGradientEndColor = typedArray.getColor(R.styleable.ShapeTextView_sGradientEndColor, -1);

        mGradientType = typedArray.getInt(R.styleable.ShapeTextView_sGradientType, LINEAR);
        mGradientUseLevel = typedArray.getBoolean(R.styleable.ShapeTextView_sGradientUseLevel, false);

        mUseSelector = typedArray.getBoolean(R.styleable.ShapeTextView_sUseSelector, false);

        typedArray.recycle();
    }

    private void init() {
        setClickable(true);

        if (Build.VERSION.SDK_INT < 16) {
            setBackgroundDrawable(mUseSelector ? getSelector() : getDrawable(0));
        } else {
            setBackground(mUseSelector ? getSelector() : getDrawable(0));
        }

        setSGravity();
    }


    /**
     * 获取设置之后的Selector
     *
     * @return stateListDrawable
     */
    public StateListDrawable getSelector() {

        StateListDrawable stateListDrawable = new StateListDrawable();

        //注意该处的顺序，只要有一个状态与之相配，背景就会被换掉
        //所以不要把大范围放在前面了，如果sd.addState(new[]{},normal)放在第一个的话，就没有什么效果了
        stateListDrawable.addState(new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled}, getDrawable(android.R.attr.state_pressed));
        stateListDrawable.addState(new int[]{-android.R.attr.state_enabled}, getDrawable(-android.R.attr.state_enabled));
        stateListDrawable.addState(new int[]{}, getDrawable(android.R.attr.state_enabled));

        return stateListDrawable;
    }

    /**
     * 设置GradientDrawable
     *
     * @param state 按钮状态
     * @return mGradientDrawable
     */
    public GradientDrawable getDrawable(int state) {
        mGradientDrawable = new GradientDrawable();

        setShape();
        setOrientation();
        setSize();
        setBorder();
        setRadius();
        setSelectorColor(state);

        return mGradientDrawable;
    }

    /**
     * 设置文字对其方式
     */
    private void setSGravity() {
        switch (mGravity) {
            case TEXT_GRAVITY_CENTER:
                setGravity(Gravity.CENTER);
                break;
            case TEXT_GRAVITY_LEFT:
                setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
                break;
            case TEXT_GRAVITY_RIGHT:
                setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
                break;
            case TEXT_GRAVITY_TOP:
                setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
                break;
            case TEXT_GRAVITY_BOTTOM:
                setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
                break;
        }
    }

    /**
     * 设置Selector的不同状态的颜色
     *
     * @param state 按钮状态
     */
    private void setSelectorColor(int state) {
        if (mGradientOrientation == -1) {
            switch (state) {
                case android.R.attr.state_pressed:
                    mGradientDrawable.setColor(mSelectorPressedColor);
                    break;
                case -android.R.attr.state_enabled:
                    mGradientDrawable.setColor(mSelectorDisableColor);
                    break;
                case android.R.attr.state_enabled:
                    mGradientDrawable.setColor(mSelectorNormalColor);
                    break;
            }
        }

    }

    /**
     * 设置背景颜色
     * 如果设定的有Orientation 就默认为是渐变色的Button，否则就是纯色的Button
     */
    private void setOrientation() {
        if (mGradientOrientation != -1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mGradientDrawable.setOrientation(getOrientation(mGradientOrientation));

                if (mGradientCenterColor == -1) {
                    mGradientDrawable.setColors(new int[]{mGradientStartColor, mGradientEndColor});
                } else {
                    mGradientDrawable.setColors(new int[]{mGradientStartColor, mGradientCenterColor, mGradientEndColor});
                }

                switch (mGradientType) {
                    case LINEAR:
                        mGradientDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
                        break;
                    case RADIAL:
                        mGradientDrawable.setGradientType(GradientDrawable.RADIAL_GRADIENT);
                        mGradientDrawable.setGradientRadius(mGradientGradientRadius);
                        break;
                    case SWEEP:
                        mGradientDrawable.setGradientType(GradientDrawable.SWEEP_GRADIENT);
                        break;
                }


                mGradientDrawable.setUseLevel(mGradientUseLevel);

                if (mGradientCenterX != 0 && mGradientCenterY != 0) {
                    mGradientDrawable.setGradientCenter(mGradientCenterX, mGradientCenterY);
                }

            }
        } else {
            mGradientDrawable.setColor(mSolidColor);
        }
    }


    /**
     * 设置颜色渐变类型
     *
     * @param gradientOrientation
     * @return Orientation
     */
    private GradientDrawable.Orientation getOrientation(int gradientOrientation) {
        GradientDrawable.Orientation orientation = null;
        switch (gradientOrientation) {
            case TOP_BOTTOM:
                orientation = GradientDrawable.Orientation.TOP_BOTTOM;
                break;
            case TR_BL:
                orientation = GradientDrawable.Orientation.TR_BL;
                break;
            case RIGHT_LEFT:
                orientation = GradientDrawable.Orientation.RIGHT_LEFT;
                break;
            case BR_TL:
                orientation = GradientDrawable.Orientation.BR_TL;
                break;
            case BOTTOM_TOP:
                orientation = GradientDrawable.Orientation.BOTTOM_TOP;
                break;
            case BL_TR:
                orientation = GradientDrawable.Orientation.BL_TR;
                break;
            case LEFT_RIGHT:
                orientation = GradientDrawable.Orientation.LEFT_RIGHT;
                break;
            case TL_BR:
                orientation = GradientDrawable.Orientation.TL_BR;
                break;
        }
        return orientation;
    }

    /**
     * 设置shape类型
     */
    private void setShape() {

        switch (mShapeType) {
            case RECTANGLE:
                mGradientDrawable.setShape(GradientDrawable.RECTANGLE);
                break;
            case OVAL:
                mGradientDrawable.setShape(GradientDrawable.OVAL);
                break;
            case LINE:
                mGradientDrawable.setShape(GradientDrawable.LINE);
                break;
            case RING:
                mGradientDrawable.setShape(GradientDrawable.RING);
                break;
        }
    }


    private void setSize() {
        if (mShapeType == RECTANGLE) {
            mGradientDrawable.setSize(mSizeWidth, mSizeHeight);
        }
    }

    /**
     * 设置边框  宽度  颜色  虚线  间隙
     */
    private void setBorder() {
        mGradientDrawable.setStroke(mStrokeWidth, mStrokeColor, mStrokeDashWidth, mStrokeDashGap);
    }

    /**
     * 只有类型是矩形的时候设置圆角半径才有效
     */
    private void setRadius() {
        if (mShapeType == GradientDrawable.RECTANGLE) {
            if (mCornersRadius != 0) {
                mGradientDrawable.setCornerRadius(mCornersRadius);//设置圆角的半径
            } else {
                //1、2两个参数表示左上角，3、4表示右上角，5、6表示右下角，7、8表示左下角
                mGradientDrawable.setCornerRadii(
                        new float[]
                                {
                                        mCornersTopLeftRadius, mCornersTopLeftRadius,
                                        mCornersTopRightRadius, mCornersTopRightRadius,
                                        mCornersBottomRightRadius, mCornersBottomRightRadius,
                                        mCornersBottomLeftRadius, mCornersBottomLeftRadius
                                }
                );
            }
        }
    }


    /////////////////对外暴露的方法//////////////

    /**
     * 设置Shape类型
     *
     * @param type 类型
     *
     * @return 对象
     */
    public ShapeTextView setShapeType(int type) {
        this.mShapeType = type;
        return this;
    }

    /**
     * 设置文字对其方式
     *
     * @param gravity 对齐方式
     * @return 对象
     */
    public ShapeTextView setTextGravity(int gravity) {
        this.mGravity = gravity;
        return this;
    }

    /**
     * 设置按下的颜色
     *
     * @param color 颜色
     * @return 对象
     */
    public ShapeTextView setShapeSelectorPressedColor(int color) {
        this.mSelectorPressedColor = color;
        return this;
    }

    /**
     * 设置正常的颜色
     *
     * @param color 颜色
     * @return 对象
     */
    public ShapeTextView setShapeSelectorNormalColor(int color) {
        this.mSelectorNormalColor = color;
        return this;
    }

    /**
     * 设置不可点击的颜色
     *
     * @param color 颜色
     * @return 对象
     */
    public ShapeTextView setShapeSelectorDisableColor(int color) {
        this.mSelectorDisableColor = color;
        return this;
    }

    /**
     * 设置填充的颜色
     *
     * @param color 颜色
     * @return 对象
     */
    public ShapeTextView setShapeSolidColor(int color) {
        this.mSolidColor = color;
        return this;
    }

    /**
     * 设置边框宽度
     *
     * @param strokeWidth 边框宽度值
     * @return 对象
     */
    public ShapeTextView setShapeStrokeWidth(int strokeWidth) {
        this.mStrokeWidth = dip2px(mContext, strokeWidth);
        return this;
    }

    /**
     * 设置边框颜色
     *
     * @param strokeColor 边框颜色
     * @return 对象
     */
    public ShapeTextView setShapeStrokeColor(@ColorInt int strokeColor) {
        this.mStrokeColor = strokeColor;
        return this;
    }

    /**
     * 设置边框虚线宽度
     *
     * @param strokeDashWidth 边框虚线宽度
     * @return 对象
     */
    public ShapeTextView setShapeSrokeDashWidth(float strokeDashWidth) {
        this.mStrokeDashWidth = dip2px(mContext, strokeDashWidth);
        return this;
    }

    /**
     * 设置边框虚线间隙
     *
     * @param strokeDashGap 边框虚线间隙值
     * @return 对象
     */
    public ShapeTextView setShapeStrokeDashGap(float strokeDashGap) {
        this.mStrokeDashGap = dip2px(mContext, strokeDashGap);
        return this;
    }

    /**
     * 设置圆角半径
     *
     * @param radius 半径
     * @return 对象
     */
    public ShapeTextView setShapeCornersRadius(float radius) {
        this.mCornersRadius = dip2px(mContext, radius);
        return this;
    }

    /**
     * 设置左上圆角半径
     *
     * @param radius 半径
     * @return 对象
     */
    public ShapeTextView setShapeCornersTopLeftRadius(float radius) {
        this.mCornersTopLeftRadius = dip2px(mContext, radius);
        return this;
    }

    /**
     * 设置右上圆角半径
     *
     * @param radius 半径
     * @return 对象
     */
    public ShapeTextView setShapeCornersTopRightRadius(float radius) {
        this.mCornersTopRightRadius = dip2px(mContext, radius);
        return this;
    }

    /**
     * 设置左下圆角半径
     *
     * @param radius 半径
     * @return 对象
     */
    public ShapeTextView setShapeCornersBottomLeftRadius(float radius) {
        this.mCornersBottomLeftRadius = dip2px(mContext, radius);
        return this;
    }

    /**
     * 设置右下圆角半径
     *
     * @param radius 半径
     * @return 对象
     */
    public ShapeTextView setShapeCornersBottomRightRadius(float radius) {
        this.mCornersBottomRightRadius = dip2px(mContext, radius);
        return this;
    }

    /**
     * 设置shape的宽度
     *
     * @param sizeWidth 宽
     * @return 对象
     */
    public ShapeTextView setShapeSizeWidth(int sizeWidth) {
        this.mSizeWidth = sizeWidth;
        return this;
    }

    /**
     * 设置shape的高度
     *
     * @param sizeHeight 高
     * @return 对象
     */
    public ShapeTextView setShapeSizeHeight(int sizeHeight) {
        this.mSizeHeight = sizeHeight;
        return this;
    }

    /**
     * 设置背景渐变方式
     *
     * @param gradientOrientation 渐变类型
     * @return 对象
     */
    public ShapeTextView setShapeGradientOrientation(int gradientOrientation) {
        this.mGradientOrientation = gradientOrientation;
        return this;
    }

    /**
     * 设置渐变中心X
     *
     * @param gradientCenterX 中心x
     * @return 对象
     */
    public ShapeTextView setShapeGradientCenterX(int gradientCenterX) {
        this.mGradientCenterX = gradientCenterX;
        return this;
    }

    /**
     * 设置渐变中心Y
     *
     * @param gradientCenterY 中心y
     * @return 对象
     */
    public ShapeTextView setShapeGradientCenterY(int gradientCenterY) {
        this.mGradientCenterY = gradientCenterY;
        return this;
    }

    /**
     * 设置渐变半径
     *
     * @param gradientGradientRadius 渐变半径
     * @return 对象
     */
    public ShapeTextView setShapeGradientGradientRadius(int gradientGradientRadius) {
        this.mGradientGradientRadius = gradientGradientRadius;
        return this;
    }

    /**
     * 设置渐变开始的颜色
     *
     * @param gradientStartColor 开始颜色
     * @return 对象
     */
    public ShapeTextView setShapeGradientStartColor(int gradientStartColor) {
        this.mGradientStartColor = gradientStartColor;
        return this;
    }

    /**
     * 设置渐变中间的颜色
     *
     * @param gradientCenterColor 中间颜色
     * @return 对象
     */
    public ShapeTextView setShapeGradientCenterColor(int gradientCenterColor) {
        this.mGradientCenterColor = gradientCenterColor;
        return this;
    }

    /**
     * 设置渐变结束的颜色
     *
     * @param gradientEndColor 结束颜色
     * @return 对象
     */
    public ShapeTextView setShapeGradientEndColor(int gradientEndColor) {
        this.mGradientEndColor = gradientEndColor;
        return this;
    }

    /**
     * 设置渐变类型
     *
     * @param gradientType 类型
     * @return 对象
     */
    public ShapeTextView setShapeGradientType(int gradientType) {
        this.mGradientType = gradientType;
        return this;
    }

    /**
     * 设置是否使用UseLevel
     *
     * @param gradientUseLevel true  or  false
     * @return 对象
     */
    public ShapeTextView setShapeGradientUseLevel(boolean gradientUseLevel) {
        this.mGradientUseLevel = gradientUseLevel;
        return this;
    }

    /**
     * 是否使用selector
     *
     * @param useSelector true  or  false
     * @return 对象
     */
    public ShapeTextView setShapeUseSelector(boolean useSelector) {
        this.mUseSelector = useSelector;
        return this;
    }

    /**
     * 使用shape
     * 所有与shape相关的属性设置之后调用此方法才生效
     */
    public void setUseShape() {
        init();
    }


    /**
     * 单位转换工具类
     *
     * @param context  上下文对象
     * @param dipValue 值
     * @return 返回值
     */
    private int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

}
