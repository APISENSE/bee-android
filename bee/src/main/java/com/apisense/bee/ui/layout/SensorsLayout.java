package com.apisense.bee.ui.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class SensorsLayout extends ViewGroup {
    private int lineHeight;

    public static class LayoutParams extends ViewGroup.LayoutParams {
        public LayoutParams(int width, int height) {
            super(width, height);
        }
    }

    public SensorsLayout(Context context) {
        super(context);
    }

    public SensorsLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();

        int count = getChildCount();

        int xPosition = getPaddingLeft();
        int yPosition = getPaddingTop();

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);

            if (child.getVisibility() == GONE) {
                continue;
            }

            LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();

            lineHeight = Math.max(lineHeight, layoutParams.height);

            if (xPosition + layoutParams.width > width) {
                xPosition = getPaddingLeft();
                yPosition += layoutParams.height;
            }

            xPosition = xPosition + layoutParams.width;
        }

        int height = yPosition + lineHeight;

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int count = getChildCount();
        int width = right - left;
        int xPosition = getPaddingLeft();
        int yPosition = getPaddingTop();


        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);

            if (child.getVisibility() == GONE) {
                continue;
            }

            LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();

            if (xPosition + layoutParams.width > width) {
                xPosition = getPaddingLeft();
                yPosition = yPosition + lineHeight;
            }

            child.layout(xPosition, yPosition, xPosition + layoutParams.width, yPosition + layoutParams.height);

            xPosition = xPosition + layoutParams.width;
        }
    }
}