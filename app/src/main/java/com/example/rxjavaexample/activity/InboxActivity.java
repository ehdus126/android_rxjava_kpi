package com.example.rxjavaexample.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rxjavaexample.R;
import com.example.rxjavaexample.databinding.ActivityInboxBinding;
import com.example.rxjavaexample.databinding.InboxListItemLayoutBinding;
import com.example.rxjavaexample.model.InboxRes;
import com.example.rxjavaexample.view.CommonPopupWindow;
import com.example.rxjavaexample.viewmodel.InboxViewModel;

import java.io.Serializable;
import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;

/**
 * 인박스 리스트 조회 (Retrofit + RxJava)
 */
public class InboxActivity extends BaseActivity {
    private static final String TAG = InboxActivity.class.getSimpleName();

    public static class ArgInfo implements Serializable{
        public InboxViewModel.eCategoryType eCategoryType = InboxViewModel.eCategoryType.ALL;
    }

    private ArrayList<String> categoryTitleList = new ArrayList<>();
    private ArrayList<InboxRes.InboxListVo> inboxListVo = new ArrayList<>();
    private int inboxPage = 1;
    private int inboxTotalCount = 0;
    private String gbn = "";
    private int categorySelectedPosition = 0;

    private InboxAdapter adapter;
    private InboxViewModel viewModel;
    private ArgInfo argInfo;
    private ActivityInboxBinding binding;

    public static void start(@NonNull AppCompatActivity activity, ArgInfo argInfo) {
        Intent intent = new Intent(activity, InboxActivity.class);
        intent.putExtra(EXTRA_ACTIVITY_INFO, argInfo);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(InboxActivity.this, R.layout.activity_inbox);

        viewModel = new ViewModelProvider(InboxActivity.this, ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())).get(InboxViewModel.class);
        argInfo = (ArgInfo) getIntent().getSerializableExtra(EXTRA_ACTIVITY_INFO);

        adapter = new InboxAdapter();
        binding.inboxRecyclerView.setAdapter(adapter);

        setObserve();

        initLayout();

        // 인박스 첫번째 페이지 요청
        if (argInfo != null) {
            categorySelectedPosition = argInfo.eCategoryType.ordinal();
        }
        InboxViewModel.CategoryInfo categoryInfo = InboxViewModel.getCategoryInfoList().get(categorySelectedPosition);
        binding.inboxHeaderListItemLayout.inboxCategoryTextView.setText(categoryInfo.categoryTitle);
        requestInboxData(categoryInfo);

        // item 선택 시 상세 웹뷰로 이동
        Disposable disposable = adapter.getPublishSubject().subscribe(new Consumer<InboxRes.InboxListVo>() {
            @Override
            public void accept(InboxRes.InboxListVo inboxListVo) {
                InboxDetailWebActivity.ArgInfo argInfo = new InboxDetailWebActivity.ArgInfo();
                argInfo.url = inboxListVo.detailUrl;
                argInfo.gbn = inboxListVo.gbn;
                InboxDetailWebActivity.start(InboxActivity.this, argInfo);
            }
        });
        addCompositeDisposable(disposable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void setObserve() {
        Log.i(TAG, "setObserve() called. ");
        // 인박스 데이터 조회
        viewModel.getInboxMutableLiveData().observe(this, inboxRes -> {
            Log.i(TAG, "getInboxMutableLiveData.onChanged() called. page : " + inboxRes.page);
            loadInboxData(inboxRes);
        });

        // 인박스 이전 페이지 원복
        viewModel.getErrorMutableLiveData().observe(this, integer -> {
            if (integer == null) {
                return;
            }
            inboxPage = integer;
        });
    }

    private void initLayout() {
        Log.i(TAG, "initLayout() called.");

        // 팝업윈도우 title 표시
        categoryTitleList.clear();
        for (InboxViewModel.CategoryInfo categoryInfo : InboxViewModel.getCategoryInfoList()) {
            categoryTitleList.add(getString(categoryInfo.categoryTitle));
        }

        // 뒤로가기 버튼 클릭
        binding.backButton.setOnClickListener(v -> onBackPressed());

        // 인박스 카테고리 팝업 설정
        binding.inboxHeaderListItemLayout.getRoot().setOnClickListener(v -> {
            CommonPopupWindow.newInstance()
                    .setAtLocationView(binding.appBarLayout)
                    .setCategoryList(categoryTitleList)
                    .setSelectedPosition(categorySelectedPosition)
                    .setWidth(135)
                    .setOnClickListener(position -> {
                        if (isFinishing()) {
                            return;
                        }
                        categorySelectedPosition = position;
                        binding.inboxHeaderListItemLayout.inboxCategoryTextView.setText(categoryTitleList.get(categorySelectedPosition));
                        requestInboxData(InboxViewModel.getCategoryInfoList().get(categorySelectedPosition));
                    })
                    .showPopupWindow(this);

            // 인박스 리스트 최하단 위치 시 다음 리스트 요청
            binding.inboxRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (!recyclerView.canScrollVertically(1)) {
                        loadInboxNextData(gbn);
                    }
                }
            });
        });
    }

    // 인박스 리스트 다음 페이지 요청
    private void loadInboxNextData(String gbn) {
        Log.i(TAG, "loadInboxNextData() called. gbn : " + gbn);
        int totalCount = adapter.getItemCount();

        if (totalCount < inboxTotalCount) {
            inboxPage++;
            viewModel.requestInbox(this, gbn, inboxPage);
        } else {
            Toast.makeText(this, "마지막 리스트입니다.", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "loadInboxNextData() last page.");
        }
    }

    // 인박스 데이터 표시
    private void loadInboxData(InboxRes.AppVo inboxRes) {
        Log.i(TAG, "loadInboxData() called. inboxRes : " + inboxRes);
        int page = Integer.parseInt(inboxRes.page);

        if (page == 1) {
            inboxListVo.clear();
        }
        inboxTotalCount = Integer.parseInt(inboxRes.totalCnt);

        inboxListVo.addAll(inboxRes.list);
        adapter.setInboxData(inboxListVo);

        boolean isInboxDataEmpty = inboxListVo.isEmpty();
        binding.inboxRecyclerView.setVisibility(isInboxDataEmpty ? View.GONE : View.VISIBLE);
        binding.inboxEmptyItemView.getRoot().setVisibility(isInboxDataEmpty ? View.VISIBLE : View.GONE);
    }

    // 인박스 리스트 데이터 요청
    private void requestInboxData(InboxViewModel.CategoryInfo categoryInfo) {
        Log.i(TAG, "requestInboxData() called. categoryInfo : " + categoryInfo);
        // 인박스 리스트 변수 초기화
        inboxPage = 1;
        inboxTotalCount = 0;
        inboxListVo.clear();
        adapter.setInboxData(inboxListVo);
        binding.inboxRecyclerView.setVisibility(View.VISIBLE);
        binding.inboxEmptyItemView.getRoot().setVisibility(View.GONE);

        // 카테고리 재선택 시 페이지 1로 원복복
        gbn = categoryInfo.categoryGbn;
        viewModel.requestInbox(this, gbn, inboxPage);
    }

    /**
     * 인박스 어댑터
     */
    public class InboxAdapter extends RecyclerView.Adapter<InboxViewHolder> {
        private PublishSubject<InboxRes.InboxListVo> publishSubject;
        private ArrayList<InboxRes.InboxListVo> listVos;

        public PublishSubject<InboxRes.InboxListVo> getPublishSubject() {
            // PublishSubject 반환
            return publishSubject;
        }

        public InboxAdapter() {
            this.publishSubject = PublishSubject.create();
        }

        public void setInboxData(ArrayList<InboxRes.InboxListVo> inboxData) {
            this.listVos = inboxData;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public InboxViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            InboxListItemLayoutBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.inbox_list_item_layout, parent, false);
            return new InboxViewHolder(binding, parent.getContext());
        }

        @Override
        public void onBindViewHolder(@NonNull InboxViewHolder holder, int position) {
            holder.bind(listVos.get(position));

            // inbox item 선택
            holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.getClickObserver(inboxListVo.get(position)).subscribe(publishSubject);
                }
            });
        }

        @Override
        public int getItemCount() {
            return listVos.isEmpty() ? 0 : listVos.size();
        }
    }

    /**
     * 인박스 뷰홀더
     */
    public static class InboxViewHolder extends RecyclerView.ViewHolder {
        private InboxListItemLayoutBinding binding;
        private Context context;

        public InboxViewHolder(@NonNull InboxListItemLayoutBinding binding, Context context) {
            super(binding.getRoot());
            this.binding = binding;
            this.context = context;
        }

        public void bind(InboxRes.InboxListVo inboxListVo) {
            // 이미지 설정
            boolean isInboxImage = TextUtils.isEmpty(inboxListVo.imgUrl);
            binding.inboxImageView.setVisibility(isInboxImage ? View.GONE : View.VISIBLE);
            if (!isInboxImage) {
                Glide.with(context).load(inboxListVo.imgUrl).into(binding.inboxImageView);
            }

            // 타이틀 및 날짜 설정
            binding.inboxTitleTextView.setText(inboxListVo.title);
            binding.inboxDateTextView.setText(inboxListVo.startDate);
        }

        Observable<InboxRes.InboxListVo> getClickObserver(InboxRes.InboxListVo inboxRes) {
            return Observable.create(new ObservableOnSubscribe<InboxRes.InboxListVo>() {
                @Override
                public void subscribe(ObservableEmitter<InboxRes.InboxListVo> e) {
                    e.onNext(inboxRes);
                }
            });
        }
    }
}
