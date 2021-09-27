package com.example.rxjavaexample.view;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;

import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rxjavaexample.R;
import com.example.rxjavaexample.databinding.CommonPopupWindowLayoutBinding;
import com.example.rxjavaexample.databinding.CommonPopupWindowListLayoutBinding;

import java.util.ArrayList;

/**
 * 공통 윈도우팝업
 */
public class CommonPopupWindow {
    private static final String TAG = CommonPopupWindow.class.getSimpleName();

    public static CommonPopupWindow newInstance() {
        return new CommonPopupWindow();
    };

    public interface OnClickListener {
        void onClick(int position);
    }

    private OnClickListener onClickListener;
    private ArrayList<String> categoryList;
    private int selectedPosition;
    private int width;
    private View atLocationView;

    public CommonPopupWindow setCategoryList(ArrayList<String> categoryList) {
        this.categoryList = categoryList;
        return this;
    }

    public CommonPopupWindow setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
        return this;
    }

    public CommonPopupWindow setWidth(int width) {
        this.width = width;
        return this;
    }

    public CommonPopupWindow setAtLocationView(View atLocationView) {
        this.atLocationView = atLocationView;
        return this;
    }

    public CommonPopupWindow setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        return this;
    }

    public void showPopupWindow(Context context) {
        PopupWindow popupWindow = new PopupWindow();
        CommonPopupWindowLayoutBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.common_popup_window_layout, null, false);

        Resources resources = context.getResources();
        popupWindow.setBackgroundDrawable(ResourcesCompat.getDrawable(resources, R.drawable.shape_popup_window_layout, null));
        popupWindow.setContentView(binding.popupWindowLayout);

        int radius = dpToPx(resources, 8);
        SimpleViewOutlineProvider.setOutlineProvider(binding.popupWindowLayout, radius, SimpleViewOutlineProvider.CornerType.ALL);

        int layoutWidth = dpToPx(resources, width);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(layoutWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        binding.recyclerView.setLayoutParams(params);

        CategoryAdapter adapter = new CategoryAdapter();
        adapter.setCategoryList(categoryList);
        adapter.setSelectedPosition(selectedPosition);
        adapter.setOnClickListener(position -> {
            if (onClickListener != null) {
                onClickListener.onClick(position);
            }
            popupWindow.dismiss();
        });
        binding.recyclerView.setAdapter(adapter);

        popupWindow.setElevation(dpToPx(resources, 8));
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(atLocationView, Gravity.NO_GRAVITY, dpToPx(resources, 24), dpToPx(resources, 115));
        popupWindow.update();
    }

    private int dpToPx(Resources resources, float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
    }

    private static class CategoryAdapter extends RecyclerView.Adapter<CategoryViewHolder> {
        private ArrayList<String> categoryList;
        private int selectedPosition = 0;
        private OnClickListener onClickListener;

        public void setCategoryList(ArrayList<String> categoryList) {
            this.categoryList = categoryList;
            notifyDataSetChanged();
        }

        public void setSelectedPosition(int position) {
            this.selectedPosition = position;
        }

        public void setOnClickListener(OnClickListener onClickListener) {
            this.onClickListener = onClickListener;
        }

        @NonNull
        @Override
        public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            CommonPopupWindowListLayoutBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.common_popup_window_list_layout, parent, false);
            return new CategoryViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
            holder.bind(categoryList.get(position));
            holder.binding.categoryItemLayout.setSelected(position == selectedPosition);
            holder.binding.getRoot().setOnClickListener(v -> onClickListener.onClick(position));
        }

        @Override
        public int getItemCount() {
            return categoryList.isEmpty() ? 0 : categoryList.size();
        }
    }

    private static class CategoryViewHolder extends RecyclerView.ViewHolder {
        private CommonPopupWindowListLayoutBinding binding;

        public CategoryViewHolder(@NonNull CommonPopupWindowListLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(String categoryName) {
            binding.categoryNameTextView.setText(categoryName);
        }
    }
}
