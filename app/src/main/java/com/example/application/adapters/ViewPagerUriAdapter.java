package com.example.application.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import com.example.application.R;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ViewPagerUriAdapter extends PagerAdapter {
    Context context;
    ArrayList <Uri> imageUrls;

    public ViewPagerUriAdapter(Context context, ArrayList<Uri> imageUrls) {
           this.context = context;
           this.imageUrls = imageUrls;
    }

    @Override
    public int getCount() {
        return imageUrls.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull @NotNull View view, @NonNull @NotNull Object object) {
        return view == object;
    }

    @NonNull
    @NotNull
    @Override
    public Object instantiateItem(@NonNull @NotNull View container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.images_layout, (ViewGroup) container, false);
        ImageView imageView = view.findViewById(R.id.UploadImage);
        imageView.setImageURI(imageUrls.get(position));
        ((ViewGroup) container).addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull @NotNull View container, int position, @NonNull @NotNull Object object) {
        ((RelativeLayout) object).removeView(container);
    }
}
