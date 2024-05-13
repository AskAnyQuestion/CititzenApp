package com.example.application.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ListView;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.application.R;
import com.example.application.adapters.NewsAdapter;
import com.example.application.adapters.NotificationAdapter;
import com.example.application.async.GetNewsMaterialRequestTask;
import com.example.application.async.GetNewsRequestTask;
import com.example.application.exception.SERVER;
import com.example.application.model.News;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class NewsFragment extends Fragment {
    private View inflatedView = null;
    private ListView listView;
    private HashMap<Integer, Bitmap> hashMap;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public NewsFragment() {
    }

    public static NewsFragment newInstance(String param1, String param2) {
        NewsFragment fragment = new NewsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflatedView = inflater.inflate(R.layout.fragment_news, container, false);
        init();
        try {
            initNews();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return inflatedView;
    }

    private void initNews() throws ExecutionException, InterruptedException {
        GetNewsRequestTask task = new GetNewsRequestTask();
        task.execute();
        Call<List<News>> call = task.get();
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NotNull Call<List<News>> call, @NotNull Response<List<News>> response) {
                List<News> newsList = response.body();
                try {
                    initMaterial(newsList);
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onFailure(@NotNull Call<List<News>> call, @NotNull Throwable t) {

            }
        });
    }

    private void initMaterial(List<News> list) throws ExecutionException, InterruptedException {
        hashMap = new HashMap<>();
        GetNewsMaterialRequestTask task = new GetNewsMaterialRequestTask();
        task.execute();
        Call<ResponseBody> call = task.get();
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                ResponseBody rb = response.body();
                assert rb != null;
                InputStream is = rb.byteStream();
                /* Чтение архива с материалами */
                try {
                    ZipInputStream zis = (is instanceof ZipInputStream) ? (ZipInputStream) is
                            : new ZipInputStream(new BufferedInputStream(is));
                    ZipEntry ze;
                    BitmapFactory.Options opts = new BitmapFactory.Options();
                    while ((ze = zis.getNextEntry()) != null) {
                        String entryName = ze.getName();
                        String folder = entryName.split("/")[0];
                        int folderId = Integer.parseInt(folder);
                        Bitmap bitmaps = hashMap.get(folderId);
                        Bitmap preview = BitmapFactory.decodeStream(zis, null, opts);
                        hashMap.put(folderId, preview);
                    }
                    zis.close();
                    /* Материалы */
                    if (getActivity() != null) {
                        NewsAdapter newsAdapter = new NewsAdapter(getContext(), list, hashMap);
                        listView.setAdapter(newsAdapter);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {}
        });
    }

    private void init() {
        listView = inflatedView.findViewById(R.id.listIncidentNews);
    }
}