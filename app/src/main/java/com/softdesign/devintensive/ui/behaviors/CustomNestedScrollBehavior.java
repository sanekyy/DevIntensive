package com.softdesign.devintensive.ui.behaviors;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.AppCompatPopupWindow;
import android.util.AttributeSet;
import android.view.View;

import com.softdesign.devintensive.R;
import com.softdesign.devintensive.utils.UiHelper;

/**
 * Created by ihb on 20.07.16.
 */
public class CustomNestedScrollBehavior extends AppBarLayout.ScrollingViewBehavior{

    private final int mMaxAppbarHeight;
    private final int mMinAppbarHeight;
    private final int mMaxUserInfoHeight;

    public CustomNestedScrollBehavior(Context context, AttributeSet attrs){
        super(context, attrs);

        mMinAppbarHeight = UiHelper.getStatusBarHeight() + UiHelper.getActionBarHeight();
        mMaxAppbarHeight = context.getResources().getDimensionPixelSize(R.dimen.profile_image_size);
        mMaxUserInfoHeight = context.getResources().getDimensionPixelSize(R.dimen.user_info_size);
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {

        float friction = UiHelper.currentFriction(mMinAppbarHeight, mMaxAppbarHeight, dependency.getBottom());
        int offsetY = UiHelper.lerp((int) (mMaxUserInfoHeight/1.55), mMaxUserInfoHeight, friction);

        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
        lp.topMargin = offsetY;
        child.setLayoutParams(lp);

        return super.onDependentViewChanged(parent, child, dependency);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return dependency instanceof AppBarLayout;
    }
}
