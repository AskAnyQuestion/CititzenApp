package com.example.application.async;

import android.os.AsyncTask;
import com.example.application.model.News;
import com.example.application.retrofit.NewsAPI;
import com.example.application.retrofit.RetrofitService;
import retrofit2.Call;

import java.util.List;

public class GetNewsRequestTask extends AsyncTask<Void, Void, Call<List<News>>> {
    @Override
    protected Call<List<News>> doInBackground(Void... voids) {
        RetrofitService retrofitService = new RetrofitService();
        NewsAPI newsAPI = retrofitService.getRetrofit().create(NewsAPI.class);
        return newsAPI.get();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Call<List<News>> unused) {
        super.onPostExecute(unused);
    }
}
