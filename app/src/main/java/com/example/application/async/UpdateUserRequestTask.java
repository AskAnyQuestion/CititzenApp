package com.example.application.async;

import android.os.AsyncTask;
import com.example.application.data.UpdateData;
import com.example.application.retrofit.RetrofitService;
import com.example.application.retrofit.UserAPI;
import retrofit2.Call;

public class UpdateUserRequestTask extends AsyncTask<Void, Void, Call<Integer>> {
    private final String oldLogin;
    private final Long oldPhone;
    private final String newLogin;
    private final Long newPhone;

    public UpdateUserRequestTask(String oldLogin, Long oldPhone, String newLogin, Long newPhone) {
        this.oldLogin = oldLogin;
        this.oldPhone = oldPhone;
        this.newLogin = newLogin;
        this.newPhone = newPhone;
    }

    @Override
    protected Call<Integer> doInBackground(Void... voids) {
        RetrofitService retrofitService = new RetrofitService();
        UserAPI userAPI = retrofitService.getRetrofit().create(UserAPI.class);
        UpdateData updateData = new UpdateData(oldLogin, oldPhone, newLogin, newPhone);
        return userAPI.updates(updateData);
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
