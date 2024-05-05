package com.example.application.async;

import android.os.AsyncTask;
import com.example.application.model.User;
import com.example.application.retrofit.RetrofitService;
import com.example.application.retrofit.UserAPI;
import retrofit2.Call;

public class UpdateTokenRequestTask extends AsyncTask<Void, Void, Call<Integer>> {
    private final User user;
    public UpdateTokenRequestTask(User user) {
        this.user = user;
    }
    @Override
    protected Call<Integer> doInBackground(Void... voids) {
        RetrofitService retrofitService = new RetrofitService();
        UserAPI userAPI = retrofitService.getRetrofit().create(UserAPI.class);
        return userAPI.userUpdate(user);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Call<Integer> unused) {
        super.onPostExecute(unused);
    }
}
