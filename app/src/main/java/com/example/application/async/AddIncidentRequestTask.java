package com.example.application.async;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import com.example.application.data.IncidentMap;
import com.example.application.model.Incident;
import com.example.application.retrofit.IncidentAPI;
import com.example.application.retrofit.RetrofitService;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class AddIncidentRequestTask extends AsyncTask<Void, Void, Call<Integer>> {
    private final Incident incident;
    private final ArrayList <MultipartBody.Part> multipartFiles;

    public AddIncidentRequestTask(IncidentMap incidentMap, ArrayList <Bitmap> bitmaps, Context context) {
        this.incident = incidentMap.toIncident();
        this.multipartFiles = new ArrayList<>();
        for (int i = 0; i < bitmaps.size(); i++) {
            try {
                File f = new File(context.getCacheDir(), "file" + i);
                boolean isCreate = f.createNewFile();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmaps.get(i).compress(Bitmap.CompressFormat.JPEG, 100, bos);
                FileOutputStream fos = new FileOutputStream(f);
                fos.write(bos.toByteArray());
                fos.flush();
                fos.close();
                MultipartBody.Part filePart = MultipartBody.Part.createFormData(
                        "files", f.getName().concat(".jpeg"),
                        RequestBody.create(MediaType.parse("multipart/form-data"), f));
                multipartFiles.add(filePart);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected Call<Integer> doInBackground(Void... voids) {
        RetrofitService retrofitService = new RetrofitService();
        IncidentAPI incidentAPI = retrofitService.getRetrofit().create(IncidentAPI.class);
        Call<Integer> call = incidentAPI.addIncident(incident, multipartFiles);
        return call;
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
