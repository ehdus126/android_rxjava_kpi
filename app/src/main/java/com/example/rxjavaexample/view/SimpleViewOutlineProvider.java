package com.example.rxjavaexample.view;

import android.graphics.Outline;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewOutlineProvider;

import androidx.annotation.NonNull;

/**
 * ViewOutlineProvider 의 베이스 클래스
 */
public class SimpleViewOutlineProvider extends ViewOutlineProvider {
    private static final String TAG = SimpleViewOutlineProvider.class.getSimpleName();
    private static final boolean DEBUG = false;

    public enum CornerType {
        ALL,
        LEFT,           // 좌측 코너
        TOP,            // 상단 코너
        RIGHT,          // 우측 코너
        BOTTOM,         // 하단 코너
        LEFT_TOP,       // 좌측 상단 코너
        LEFT_BOTTOM,    // 좌측 하단 코너
        RIGHT_TOP,      // 우측 상단 코너
        RIGHT_BOTTOM,   // 우측 하단 코너
    }

    private float radius;
    private CornerType enumCornerType = CornerType.ALL;

    public static void setOutlineProvider(@NonNull View view, float radius, @NonNull CornerType cornerType) {
        SimpleViewOutlineProvider provider = new SimpleViewOutlineProvider(radius, cornerType);
        view.setOutlineProvider(provider);
        view.setClipToOutline(true);
    }

    public SimpleViewOutlineProvider() {
    }

    public SimpleViewOutlineProvider(float radius) {
        this.radius = radius;
    }

    public SimpleViewOutlineProvider(float radius, @NonNull CornerType cornerType) {
        this.radius = radius;
        this.enumCornerType = cornerType;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public void setCornerType(@NonNull CornerType cornerType) {
        this.enumCornerType = cornerType;
    }

    @Override
    public void getOutline(View view, Outline outline) {
        int width = view.getWidth();
        int height = view.getHeight();
        if (DEBUG) {
            Log.d(TAG, "getOutline() width : " + width + ", height : " + height + ", view : " + view);
        }
        if (DEBUG) {
            Log.d(TAG, "getOutline() radius : " + radius + ", enumCornerType : " + enumCornerType);
        }

        int offset = (int) radius;
        Rect rect = new Rect(0, 0, width, height);
        switch (enumCornerType) {
            case LEFT:          // (0, 0, width, height) + (0, 0, offset, 0)
                rect.right += offset;
                break;

            case TOP:           // (0, 0, width, height) + (0, 0, 0, offset)
                rect.bottom += offset;
                break;

            case RIGHT:          // (0, 0, width, height) + (-offset, 0, 0, 0)
                rect.left -= offset;
                break;

            case BOTTOM:        // (0, 0, width, height) + (0, -offset, 0, 0)
                rect.top -= offset;
                break;

            case LEFT_TOP:      // (0, 0, width, height) + (0, 0, offset, offset)
                rect.right += offset;
                rect.bottom += offset;
                break;

            case LEFT_BOTTOM:   // (0, 0, width, height) + (0, -offset, offset, 0)
                rect.top -= offset;
                rect.right += offset;
                break;

            case RIGHT_TOP:     // (0, 0, width, height) + (-offset, 0, 0, offset)
                rect.left -= offset;
                rect.bottom += offset;
                break;

            case RIGHT_BOTTOM:  // (0, 0, width, height) + (-offset, -offset, 0, 0)
                rect.left -= offset;
                rect.top -= offset;
                break;

            case ALL:
            default:
                break;
        }
        if (DEBUG) {
            Log.d(TAG, "getOutline() rect : " + rect);
        }
        outline.setRoundRect(rect.left, rect.top, rect.right, rect.bottom, radius);
    }

    public static final ViewOutlineProvider OVAL = new ViewOutlineProvider() {
        @Override
        public void getOutline(View view, Outline outline) {
            int width = view.getWidth();
            int height = view.getHeight();
            Log.d(TAG, "getOutline() width : " + width + ", height : " + height + ", view : " + view);
            outline.setOval(0, 0, view.getWidth(), view.getHeight());
        }
    };
}
