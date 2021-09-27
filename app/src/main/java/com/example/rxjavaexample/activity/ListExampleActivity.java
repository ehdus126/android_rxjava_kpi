package com.example.rxjavaexample.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rxjavaexample.R;
import com.example.rxjavaexample.databinding.ActivityListExampleBinding;
import com.example.rxjavaexample.databinding.ListExampleItemLayoutBinding;
import com.example.rxjavaexample.model.ListItemVo;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

/**
 * Copyright ⓒ 2021 Starbucks Coffee Company. All Rights Reserved.| Confidential
 *
 * @ Description : 단말 내 설치된 앱 정보를 리스트로 보여주는 예제
 * @ Class : ListExampleActivity
 * @ Created by : limdoyeon
 * @ Created Date : 2021. 07. 28.
 */
public class ListExampleActivity extends BaseActivity {
    private static final String TAG = ListExampleActivity.class.getSimpleName();
    private ActivityListExampleBinding binding;
    private ListExampleAdapter adapter;
    private PackageManager packageManager;

    public static void start(@NonNull AppCompatActivity activity) {
        Intent intent = new Intent(activity, ListExampleActivity.class);
        activity.startActivity(intent);
    }

    /**
     * Rx Java 의 람다표현식은 세가지 제네릭 함수형 인터페이스 중 하나로 나타냄
     * 1. Predicate<T> : T 값을 받아서 참, 거짓을 반환
     * 2. Consumer<T> : T 값을 받아서  로직 처리 (return 값 없음)
     * 3. Function<T, R> : T 값을 받아서 R 값을 반환
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(ListExampleActivity.this, R.layout.activity_list_example);

        // Adapter 설정
        adapter = new ListExampleAdapter();
        //adapter.setListItem(list);
        binding.recyclerView.setAdapter(adapter);

        // 클릭 이벤트 시 토스트 팝업 제공
        Disposable disposable = adapter.getPublishSubject().subscribe(new Consumer<ListItemVo>() {
            @Override
            public void accept(ListItemVo listItemVo) throws Exception {
                showToast(listItemVo.text);
            }
        });
        addCompositeDisposable(disposable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart() called.");

        if (adapter == null) {
            return;
        }

        // 설치된 앱 정보 받아옴
        Disposable disposable = getItemObservable().observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ListItemVo>() {
                    @Override
                    public void accept(ListItemVo listItemVo) throws Exception {
                        // 받아온 item 정보들을 adapter 에 설정
                        adapter.updateListItem(listItemVo);
                        adapter.notifyDataSetChanged();
                    }
                });
       addCompositeDisposable(disposable);
    }

    /**
     * PackageManager 를 이용해 설치된 앱정보를 가져와 List Item 객체로 변경
     */
    private Observable<ListItemVo> getItemObservable() {
        Log.i(TAG, "getItemObservable() called.");
        packageManager = getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        // queryIntentActivities : 설치된 앱 중 CATEGORY_LAUNCHER 타입의 앱만 결과로 가져옴
        return Observable.fromIterable(packageManager.queryIntentActivities(intent, 0))
                .sorted(new ResolveInfo.DisplayNameComparator(packageManager))
                .subscribeOn(Schedulers.io())
                  .observeOn(Schedulers.io())
                // map() : 원하는 값으로 변환하는 함수
                // 람다식
//                .map(item -> {
//                    Drawable image = item.activityInfo.loadIcon(packageManager);
//                    String text = item.activityInfo.loadLabel(packageManager).toString();
//                    return ListItemVo.of(image, text);
//
//                });
                // Function I/F 적용
                .map(getAppInfo);
    }

    // Data 클래스에 앱 정보 추가 후 반환
    private Function<ResolveInfo, ListItemVo> getAppInfo = new Function<ResolveInfo, ListItemVo>() {
        @Override
        public ListItemVo apply(ResolveInfo resolveInfo) throws Exception {
            Drawable image = resolveInfo.activityInfo.loadIcon(packageManager);
            String text = resolveInfo.activityInfo.loadLabel(packageManager).toString();
            return ListItemVo.of(image, text);
        }
    };

    private void showToast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    /**
     * List 예제 Adapter
     */
    private static class ListExampleAdapter extends RecyclerView.Adapter<ListExampleAdapter.ListViewHolder> {
        private ArrayList<ListItemVo> list = new ArrayList<>();
        private PublishSubject<ListItemVo> publishSubject;

        public void updateListItem(ListItemVo listItem) {
            list.add(listItem);
        }

        public PublishSubject<ListItemVo> getPublishSubject() {
            // PublishSubject 반환
            return publishSubject;
        }

        public ListExampleAdapter() {
            // Adapter 생성 시 PublishSubject 생성
            this.publishSubject = PublishSubject.create();
        }

        @NonNull
        @Override
        public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ListExampleItemLayoutBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.list_example_item_layout, parent, false);
            return new ListViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
            ListItemVo listItemVo = list.get(position);
            holder.bind(listItemVo);
            // item 선택 시
            holder.binding.getRoot().setOnClickListener(v -> {
                holder.getClickObserver(listItemVo).subscribe(publishSubject);
            });
        }

        @Override
        public int getItemCount() {
            return list.isEmpty() ? 0 : list.size();
        }

        /**
         * List 예제 ViewHolder
         */
        private static class ListViewHolder extends RecyclerView.ViewHolder {
            private ListExampleItemLayoutBinding binding;

            public ListViewHolder(ListExampleItemLayoutBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }

            void bind(ListItemVo listItemVo) {
                binding.imageView.setImageDrawable(listItemVo.image);
                binding.textView.setText(listItemVo.text);
            }

            /**
             * 리액티브 프로그래밍에서는 Click 이벤트를 분리된 Observable 에 생성 (콜백 지옥 부분을 대처)
             * 클릭 이벤트
             */
            Observable<ListItemVo> getClickObserver(ListItemVo listItemVo) {
                return Observable.create(new ObservableOnSubscribe<ListItemVo>() {
                    @Override
                    public void subscribe(ObservableEmitter<ListItemVo> e) throws Exception {
                        e.onNext(listItemVo);
                    }
                });
            }
        }
    }
}
