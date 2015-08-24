package com.geekfan.collapsablelinearlayout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.IntEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;
import android.widget.TextView;


public class TagLayout extends ViewGroup {
    private final float DEFAULT_TAG_TEXT_PADDING_LEFT = 8;
    private final float DEFAULT_TAG_TEXT_PADDING_TOP = 5;
    private final float DEFAULT_TAG_TEXT_PADDING_RIGHT = 8;
    private final float DEFAULT_TAG_TEXT_PADDING_BOTTOM = 5;
    private float DEFAULT_HORIZONTAL_SPACEING = 8.0f;
    private float DEFAULT_VERTICAL_SPACEING = 8.0f;
    private final int DEFAULT_TAG_LAYOUT_COLOR = Color.parseColor("#00BFFF");
    private final int DEFAULT_TAG_LAYOUT_COLOR_PRESS = Color.parseColor("#88363636");
    private final int DEFAULT_TAG_TEXT_COLOR = Color.parseColor("#ffffff");
    private final int DEFAULT_TAG_DELETE_INDICATOR_COLOR = Color.parseColor("#ffffff");
    private final int DEFAULT_TAG_LAYOUT_BORDER_COLOR = Color.parseColor("#ffffff");
    private final int DEFAULT_TAG_COLLAPSNUM = 4;
    private final float DEFAULT_TAG_RADIUS = 20;
    private final float DEFAULT_TAG_TEXT_SIZE = 14f;
    private final float DEFAULT_TAG_LAYOUT_BORDER_SIZE = 0f;
    private LayoutInflater mInflater;
    private ValueAnimator animator;
    private Interpolator interpolator = new AccelerateDecelerateInterpolator();
    private boolean animating;
    private int fullHeight;
    private int rowLineHeight;
    private long duration = 600;
    private Drawable tag_backgroundColor;
    private int tag_layoutColor;
    private int tag_radius;
    private float tag_layoutBorderSize;
    private int tag_layoutBorderColor;
    private int tag_layoutColorPress;
    private int textPaddingLeft;
    private int textPaddingRight;
    private int textPaddingTop;
    private int texPaddingBottom;
    private int tag_deleteIndicatorColor;
    private String tag_deleteIcon="X";
    private int tag_collapsNum;
    private int tag_text_color;
    private float tag_text_size;
    /**
     * The horizontal tag spacing, default is 8.0dp.
     */
    private int mHorizontalSpacing;
    /**
     * The vertical tag spacing, default is 8.0dp.
     */
    private int mVerticalSpacing;
    /**
     * Listener used to dispatch tag change event.
     */
    private OnTagClick mTagClickListener;
    private OnTagDeleteListener mDeleteListener;

    public void setTagClickListener(OnTagClick mTagClickListener) {
        this.mTagClickListener = mTagClickListener;
    }

    public TagLayout(Context context) {
        this(context, null);
        init(context, null, 0);
    }

    public TagLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init(context, attrs, 0);
    }

    public TagLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);

    }

    private void init(Context ctx, AttributeSet attrs, int defStyle) {
        mInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // Load styled attributes.
        final TypedArray a = ctx.obtainStyledAttributes(attrs,
                R.styleable.TagGroup, defStyle, 0);
        try {
            mHorizontalSpacing = (int) a.getDimension(
                    R.styleable.TagGroup_horizontalSpacing,
                    DEFAULT_HORIZONTAL_SPACEING);
            mVerticalSpacing = (int) a.getDimension(
                    R.styleable.TagGroup_verticalSpacing,
                    DEFAULT_VERTICAL_SPACEING);
            tag_backgroundColor = a.getDrawable(R.styleable.TagGroup_backgroundColor);
            tag_layoutColor = a.getColor(R.styleable.TagGroup_layoutColor, DEFAULT_TAG_LAYOUT_COLOR);
            tag_radius = (int) a.getFloat(R.styleable.TagGroup_radius, DEFAULT_TAG_RADIUS);
            tag_layoutBorderSize = a.getDimension(R.styleable.TagGroup_layoutBorderSize, DEFAULT_TAG_LAYOUT_BORDER_SIZE);
            tag_layoutBorderColor = a.getColor(R.styleable.TagGroup_layoutBorderColor, DEFAULT_TAG_LAYOUT_BORDER_COLOR);
            tag_layoutColorPress = a.getColor(R.styleable.TagGroup_layoutColorPress, DEFAULT_TAG_LAYOUT_COLOR_PRESS);
            tag_deleteIndicatorColor = a.getColor(R.styleable.TagGroup_deleteIndicatorColor, DEFAULT_TAG_TEXT_COLOR);
            tag_text_color = a.getColor(R.styleable.TagGroup_textColor, DEFAULT_TAG_TEXT_COLOR);
            tag_text_size = a.getDimension(R.styleable.TagGroup_textSize, DEFAULT_TAG_TEXT_SIZE);
            tag_collapsNum = a.getInt(R.styleable.TagGroup_collapsNum, DEFAULT_TAG_COLLAPSNUM);

            this.textPaddingLeft = (int) a.getDimension(R.styleable.TagGroup_textPaddingLeft, dip2px(this.getContext(), DEFAULT_TAG_TEXT_PADDING_LEFT));
            this.textPaddingRight = (int) a.getDimension(R.styleable.TagGroup_textPaddingRight, dip2px(this.getContext(), DEFAULT_TAG_TEXT_PADDING_RIGHT));
            this.textPaddingTop = (int) a.getDimension(R.styleable.TagGroup_textPaddingTop, dip2px(this.getContext(), DEFAULT_TAG_TEXT_PADDING_TOP));
            this.texPaddingBottom = (int) a.getDimension(R.styleable.TagGroup_textPaddingBottom, dip2px(this.getContext(), DEFAULT_TAG_TEXT_PADDING_BOTTOM));
        } finally {
            a.recycle();
        }

    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new TagLayout.LayoutParams(getContext(), attrs);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int parentLeft = getPaddingLeft();
        final int parentRight = r - l - getPaddingRight();
        final int parentTop = getPaddingTop();
        final int parentBottom = b - t - getPaddingBottom();

        int childLeft = parentLeft;
        int childTop = parentTop;

        int rowMaxHeight = 0;

        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            final int width = child.getMeasuredWidth();
            final int height = child.getMeasuredHeight();

            if (child.getVisibility() != GONE) {
                if (childLeft + width > parentRight) { // Next line
                    childLeft = parentLeft;
                    childTop += rowMaxHeight + mVerticalSpacing;
                    rowMaxHeight = height;
                } else {
                    rowMaxHeight = Math.max(rowMaxHeight, height);
                }
                child.layout(childLeft, childTop, childLeft + width, childTop
                        + height);

                childLeft += width + mHorizontalSpacing;
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        measureChildren(widthMeasureSpec, heightMeasureSpec);

        int width = 0;
        int height = 0;

        int row = 0; // The row counter.
        int rowWidth = 0; // Calc the current row width.
        int rowMaxHeight = 0; // Calc the max tag height, in current row.

        final int count = getChildCount();
        if (count != 0) {
            rowLineHeight = tag_collapsNum * (getChildAt(0).getMeasuredHeight() + mVerticalSpacing);
        }

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            final int childWidth = child.getMeasuredWidth();
            final int childHeight = child.getMeasuredHeight();

            if (child.getVisibility() != GONE) {
                rowWidth += childWidth;
                if (rowWidth > widthSize) { // Next line.
                    rowWidth = childWidth; // The next row width.
                    height += rowMaxHeight + mVerticalSpacing;
                    rowMaxHeight = childHeight; // The next row max height.
                    row++;
                } else { // This line.
                    rowMaxHeight = Math.max(rowMaxHeight, childHeight);
                }
                rowWidth += mHorizontalSpacing;
            }
        }

        // Account for the last row height.
        height += rowMaxHeight;

        // Account for the padding too.
        height += getPaddingTop() + getPaddingBottom();

        // If the tags grouped in one row, set the width to wrap the tags.
        if (row == 0) {
            width = rowWidth;
            width += getPaddingLeft() + getPaddingRight();
        } else {// If the tags grouped exceed one line, set the width to match
            // the parent.
            width = widthSize;
        }

        setMeasuredDimension(widthMode == MeasureSpec.EXACTLY ? widthSize
                : width, heightMode == MeasureSpec.EXACTLY ? heightSize
                : height);
        fullHeight = height;
    }


    protected void appendTag(String tag,boolean isDelable) {
        final View tagLayout = (View) mInflater.inflate(R.layout.tag_layout_item, null);
        tagLayout.setBackgroundDrawable(getSelector());

        final TextView tagView = (TextView) tagLayout.findViewById(R.id.tag_item_contain);
        tagView.setText(tag);
        tagLayout.setTag(tag);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tagView.getLayoutParams();
        params.setMargins(textPaddingLeft, textPaddingTop, textPaddingRight, texPaddingBottom);
        tagView.setLayoutParams(params);
        tagView.setTextColor(tag_text_color);
        tagView.setTextSize(TypedValue.COMPLEX_UNIT_SP, tag_text_size);
        tagLayout.setOnClickListener(new OnTagClickListener());

        if(isDelable){
            TextView deletableView = (TextView) tagLayout.findViewById(R.id.tag_item_delete);
            deletableView.setVisibility(View.VISIBLE);
            deletableView.setText(tag_deleteIcon);
            int offset = dip2px(getContext(), 2f);
            deletableView.setPadding(offset, textPaddingTop, textPaddingRight + offset, texPaddingBottom);
            deletableView.setTextColor(tag_deleteIndicatorColor);
            deletableView.setTextSize(TypedValue.COMPLEX_UNIT_SP, tag_text_size);
            deletableView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    TagLayout.this.removeView(tagLayout);
                    if (mDeleteListener != null) {
                        mDeleteListener.onTagDeleted(tagView.getTag().toString());
                    }
                }
            });
        }

        addView(tagLayout);
    }

    public interface OnTagClick {
        void onClick(String tag);
    }

    public interface OnTagDeleteListener {
        void onTagDeleted(String tag);
    }

    private Drawable getSelector() {
        if (tag_backgroundColor != null) return tag_backgroundColor;
        StateListDrawable states = new StateListDrawable();
        GradientDrawable gd_normal = new GradientDrawable();
        gd_normal.setColor(tag_layoutColor);
        gd_normal.setCornerRadius(tag_radius);
        if (tag_layoutBorderSize > 0) {
            gd_normal.setStroke(dip2px(getContext(), tag_layoutBorderSize), tag_layoutBorderColor);
        }
        GradientDrawable gd_press = new GradientDrawable();
        gd_press.setColor(tag_layoutColorPress);
        gd_press.setCornerRadius(tag_radius);
        states.addState(new int[]{android.R.attr.state_pressed}, gd_press);
        //must add state_pressed first��or state_pressed will not take effect
        states.addState(new int[]{}, gd_normal);
        return states;
    }

    class OnTagClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            if (mTagClickListener != null) {
                mTagClickListener.onClick(v.getTag().toString());
            }
        }
    }

    private class HeightEvaluator extends IntEvaluator {
        @Override
        public Integer evaluate(float fraction, Integer startValue,
                                Integer endValue) {
            int height = super.evaluate(fraction, startValue, endValue);
            ViewGroup.LayoutParams params = getLayoutParams();
            params.height = height;
            setLayoutParams(params);
            return height;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (animating) {
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if ((w != oldw || h != oldh) && !animating && h != 0) {
            if (fullHeight > rowLineHeight) {
//                    collapse();
                ViewGroup.LayoutParams params = getLayoutParams();
                params.height = rowLineHeight;
                setLayoutParams(params);
            }
        }
    }

    public void setAnimationDuration(long duration) {
        this.duration = duration;
    }

    public long getAnimationDuration() {
        return duration;
    }

    public void setInterpolator(Interpolator interpolator) {
        this.interpolator = interpolator;
    }

    public Interpolator getInterpolator() {
        return interpolator;
    }

    public void expand() {
        animateHeight(rowLineHeight, fullHeight);
    }

    public void collapse() {
        animateHeight(fullHeight, rowLineHeight);
    }

    public void toggle() {
        if (getHeight() != fullHeight) {
            expand();
            collapseObserver.isCollaps(false);
        } else {
            collapse();
            collapseObserver.isCollaps(true);
        }
    }

    private void animateHeight(int startHeight, int endHeight) {
        long previousAnimationPlayTime = -1;
        if (animator != null && animator.isRunning()) {
            previousAnimationPlayTime = animator.getCurrentPlayTime();
            animator.cancel();
        }
        animator = ValueAnimator.ofObject(new HeightEvaluator(), startHeight,
                endHeight);
        animator.setInterpolator(interpolator);
        animator.setDuration(duration);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                animating = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                animating = false;
            }
        });
        animator.start();
        if (previousAnimationPlayTime != -1) {
            animator.setCurrentPlayTime(duration - previousAnimationPlayTime);
        }
    }

    private onCollapseObserver collapseObserver;
    public interface onCollapseObserver {
        public void isCollaps(boolean flag);
    }

    public void setOnCollapseObserver(onCollapseObserver lineObserver) {
        this.collapseObserver = lineObserver;
    }

    public int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) ((dipValue * scale) + 0.5f);
    }
}
