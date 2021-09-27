package com.example.rxjavaexample.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rxjavaexample.R;


/**
 * NestedScrollView 의 베이스 클래스
 */
public class BaseNestedScrollView extends NestedScrollView {
    private static final String TAG = BaseNestedScrollView.class.getSimpleName();
    private int recyclerViewID = NO_ID;

    public BaseNestedScrollView(@NonNull Context context) {
        this(context, null);
    }

    public BaseNestedScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseNestedScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.BaseNestedScrollView, defStyleAttr, 0);
            int n = typedArray.getIndexCount();

            for (int i = 0 ; i < n ; i++) {
                int attr = typedArray.getIndex(i);

                switch (attr) {
                    case R.styleable.BaseNestedScrollView_recyclerViewId:
                        recyclerViewID = typedArray.getResourceId(attr, NO_ID);
                        break;
                    default:
                        break;
                }
            }
            typedArray.recycle();
        }
    }

    @Override
    public void dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, @Nullable int[] offsetInWindow, int type, @NonNull int[] consumed) {
        super.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow, type, consumed);
    }

    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int axes, int type) {
        return super.onStartNestedScroll(child, target, axes, type);
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        final RecyclerView rv = (RecyclerView) target;
        if ((0 > dy && isRvScrolledToTop(rv)) || (0 < dy && !isNsvScrolledToBottom(this))) {
            scrollBy(0, dy);
            consumed[1] = dy;
            return;
        }
        super.onNestedPreScroll(target, dx, dy, consumed, type);
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type, @NonNull int[] consumed) {
        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type, consumed);
    }

    @Override
    public void onStopNestedScroll(@NonNull View target, int type) {
        super.onStopNestedScroll(target, type);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = measureWidth(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);
        int childCount = getChildCount();
        if (0 < childCount) {
            View subScrollableView = findViewById(recyclerViewID); // ex), ViewPagerId(내부에 RecyclerView 구성) or RecyclerViewId
            if (null != subScrollableView) {
                if (subScrollableView.isLayoutRequested()) {
                    // ViewPager 안에 RecyclerView 스크롤 시 상단 영역의 View 안보이도록 하기 위해 NestedScrollView 객체와 동일한 높이 재설정
                    ViewGroup.LayoutParams layoutParams = subScrollableView.getLayoutParams();
                    layoutParams.height = height;
                    subScrollableView.setLayoutParams(layoutParams);
                }
            }
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    // Width 값 반환
    private int measureWidth(int widthMeasureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            // 부모가 자녀의 정확한 크기를 결정
            result = specSize;
        } else if (specMode == MeasureSpec.AT_MOST) {
            // 자식은 지정된 크기까지 원하는만큼 커질 수 있는 상태
            result = specSize;
        } else {
            // 부모가 자녀에게 어떠한 제약도 안한 상태
        }

        return result;
    }

    // Height 값 반환
    private int measureHeight(int heightMeasureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(heightMeasureSpec);
        int specSize = MeasureSpec.getSize(heightMeasureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            // 부모가 자녀의 정확한 크기를 결정
            result = specSize;
        } else if (specMode == MeasureSpec.AT_MOST) {
            // 자식은 지정된 크기까지 원하는만큼 커질 수 있는 상태
            result = specSize;
        } else {
            // 부모가 자녀에게 어떠한 제약도 안한 상태
        }

        return result;
    }

    // RecyclerView 상단까지 스크롤된 여부 반환
    private static boolean isRvScrolledToTop(RecyclerView rv) {
        final LinearLayoutManager lm = (LinearLayoutManager) rv.getLayoutManager();
        if (null == lm) {
            return false;
        }
        if (lm.findFirstVisibleItemPosition() != 0) {
            return false;
        }
        View view = lm.findViewByPosition(0);
        if (null == view) {
            return false;
        }

        int top = view.getTop();
        return top == 0;
    }

    // NestedScrollView 하단까지 스크롤된 여부 반환
    private static boolean isNsvScrolledToBottom(NestedScrollView nsv) {
        // direction : 음수일 경우 scrolling up, 양수일 경우 scrolling down
        boolean canScrollVertically = nsv.canScrollVertically(1);
        return !canScrollVertically;
    }
}
