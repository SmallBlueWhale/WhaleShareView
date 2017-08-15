package szu.hyx.whale.whaleshareanim;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.animation.OvershootInterpolator;

/**
 * funtion :一个分享的view
 * author  :smallbluewhale.
 * date    :2017/8/10 0010.
 * version :1.0.
 */
public class WhaleShareView extends ViewGroup {
    private Context context;
    private int MAX_COUNT = 3;          //当前行最大容纳的数量,每行最多能有几个
    private long interval = 300;        //动画间隔
    private float WIDTH_MARGIN = 50;         //左右控件之间的宽度
    private float HEIGHT_MARGIN = 100;        //上下控件之间的宽度

    public WhaleShareView(Context context) {
        super(context);
        this.context = context;
        setInterpolator();
    }

    public WhaleShareView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setInterpolator();
    }

    public WhaleShareView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        setInterpolator();
    }

    public WhaleShareView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        setInterpolator();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }


    private void setInterpolator() {
        Animation animation = AnimationUtils.loadAnimation(context , R.anim.share_item_anim);
        OvershootInterpolator overshootInterpolator = new OvershootInterpolator(1f);
        animation.setInterpolator(overshootInterpolator);
        LayoutAnimationController layoutAnimationController = new LayoutAnimationController(animation);
        layoutAnimationController.setDelay(0.5f);
        layoutAnimationController.setOrder(LayoutAnimationController.ORDER_NORMAL);
        setLayoutAnimation(layoutAnimationController);
    }

    /*
    * 固定套路
    * */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //获得它的父容器为它设置的测量模式和大小
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        //记录当wrap_content的情况
        int width = 0, height = 0;
        int count = getChildCount();
        int childWidth = 0, childHeight = 0;
        //子view如果少于每行限定的最大数量，那么我们就取测量长度为这个viewgroup的长度
        if (count <= MAX_COUNT) {
            for (int i = 0; i < count; i++) {
                View childView = getChildAt(i);
                //计算所有子view的宽高
                measureChild(childView, widthMeasureSpec, heightMeasureSpec);
                //当前子空间占据的宽度
                width += childView.getMeasuredWidth();
                //当前子空间占据的高度,取最大值
                height = Math.max(height, childView.getMeasuredHeight());
            }
            width += WIDTH_MARGIN * (count + 1);
            height += HEIGHT_MARGIN * 2;
        }
        //大于等于最大长度，都让它宽度等于match_parent时的宽度
        else {
            width = widthSize;
            for (int i = 0; i < count; i++) {
                View childView = getChildAt(i);
                //计算所有子view的宽高
                measureChild(childView, widthMeasureSpec, heightMeasureSpec);
                //当前子空间占据的高度,取最大值
                height += childView.getMeasuredHeight();
            }
            height += height * (count / MAX_COUNT + 1);
        }
        setMeasuredDimension((widthMode == MeasureSpec.EXACTLY) ? widthSize : width, (heightMode == MeasureSpec.EXACTLY) ? heightSize : height);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        View childView = null;
        float childWidth = 0;
        float childHeight = 0;
        float childLeft = 0;
        float childRight = 0;
        float childTop = 0;
        float childBottom = 0;
        //获取宽度以及高度，方便子控件的摆放
        float width = getMeasuredWidth();
        float height = getMeasuredHeight();
        int count = getChildCount();
        //总共多少行
        int lineCount = getChildCount() / MAX_COUNT + 1;
        //分两种情况一种小于每行最大个数的情况
        if (count <= MAX_COUNT) {
            for (int i = 0; i < count; i++) {
                childView = getChildAt(i);
                childWidth = childView.getMeasuredWidth();
                childHeight = childView.getMeasuredHeight();
                childTop = HEIGHT_MARGIN;
                childBottom = childTop + childHeight;
                //奇数情况
                if (count % 2 == 1) {

                    childLeft = (count / 2 - i) >= 0 ?
                            width / 2 - childWidth / 2 - (count / 2 - i) * (WIDTH_MARGIN + childWidth)
                            : width / 2 + childWidth / 2 + (i - count / 2) * WIDTH_MARGIN + (i - count / 2 - 1) * childWidth
                    ;
                    childRight = childLeft + childWidth;
                }
                //偶数情况
                else {
                    childLeft = (count / 2 - i) > 0 ?
                            width / 2 - (count / 2 - i) * (WIDTH_MARGIN + childWidth) + WIDTH_MARGIN / 2
                            : width / 2 + (i - count / 2) * (WIDTH_MARGIN / 2 + childWidth) + WIDTH_MARGIN / 2
                    ;
                    childRight = childLeft + childWidth;
                }
                childView.layout((int) childLeft, (int) childTop, (int) childRight, (int) childBottom);
            }
        }
        //一种大于每行最大个数的情况
        else {
            /*.
            * 遍歷
            * */
            //总共多少行
            for (int i = 0; i < count; i++) {
                //当前行的第几位
                int currentItem = i % MAX_COUNT;
                int currentLineCount = i / MAX_COUNT;
                childView = getChildAt(i);
                childWidth = childView.getMeasuredWidth();
                childHeight = childView.getMeasuredHeight();
                childTop = currentLineCount * (HEIGHT_MARGIN + childHeight) + HEIGHT_MARGIN;
                childBottom = childTop + childHeight;
                //奇数情况
                if (MAX_COUNT % 2 == 1) {
                    childLeft = (MAX_COUNT / 2 - currentItem) >= 0 ?
                            width / 2 - childWidth / 2 - (MAX_COUNT / 2 - currentItem) * (WIDTH_MARGIN + childWidth)
                            : width / 2 + childWidth / 2 + (currentItem - MAX_COUNT / 2) * WIDTH_MARGIN + (currentItem - MAX_COUNT / 2 - 1) * childWidth;
                    childRight = childLeft + childWidth;
                }
                //偶数情况
                else {
                    childLeft = (MAX_COUNT / 2 - currentItem) > 0 ?
                            width / 2 - (MAX_COUNT / 2 - currentItem) * (WIDTH_MARGIN + childWidth) + WIDTH_MARGIN / 2
                            : width / 2 + (currentItem - MAX_COUNT / 2) * (WIDTH_MARGIN / 2 + childWidth) + WIDTH_MARGIN / 2
                    ;
                    childRight = childLeft + childWidth;
                }
                childView.layout((int) childLeft, (int) childTop, (int) childRight, (int) childBottom);
            }

        }
    }

}
