package com.example.application.async;

import android.os.AsyncTask;
import com.example.application.data.LoginData;
import com.example.application.retrofit.RetrofitService;
import com.example.application.retrofit.UserAPI;
import retrofit2.Call;

public class AuthorizationRequestTask extends AsyncTask<Void, Void, Call<Integer>> {
    private final LoginData loginData;
    public AuthorizationRequestTask(LoginData loginData) {
        this.loginData = loginData;
    }

    @Override
    protected Call<Integer> doInBackground(Void... voids) {
        RetrofitService retrofitService = new RetrofitService();
        UserAPI userAPI = retrofitService.getRetrofit().create(UserAPI.class);
        return userAPI.authorization(loginData);
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