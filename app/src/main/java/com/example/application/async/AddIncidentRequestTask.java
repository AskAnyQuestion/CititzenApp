package com.example.application.async;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import com.example.application.data.IncidentData;
import com.example.application.map.IncidentMap;
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
    private final IncidentData incidentData;
    public AddIncidentRequestTask(IncidentMap incidentMap, ArrayList<Uri> uris,
                                  ArrayList <Bitmap> bitmaps, Context context) {
        ArrayList <MultipartBody.Part> multipartFiles = new ArrayList<>();
        for (int i = 0; i < bitmaps.size(); i++) {
            try {
                File f = new File(context.getCacheDir(), "file" + i);
                boolean isCreate = f.createNewFile();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmaps.get(i).compress(Bitmap.CompressFormat.PNG, 100, bos);
                FileOutputStream fos = new FileOutputStream(f);
                fos.write(bos.toByteArray());
                fos.flush();
                fos.close();
                MultipartBody.Part filePart = MultipartBody.Part.createFormData(
                        "files", f.getName(),
                        RequestBody.create(MediaType.parse("multipart/form-data"), f));
                multipartFiles.add(filePart);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Incident incident = incidentMap.toIncident();
        this.incidentData = new IncidentData(incident, multipartFiles);
    }

    @Override
    protected Call<Integer> doInBackground(Void... voids) {
        RetrofitService retrofitService = new RetrofitService();
        IncidentAPI incidentAPI = retrofitService.getRetrofit().create(IncidentAPI.class);
        Call<Integer> call = incidentAPI.addIncident(/*incidentData.getIncident(),*/ incidentData.getList());
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
