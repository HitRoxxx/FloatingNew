package com.theLoneWarrior.floating.adapter;

import android.app.Service;
import android.content.Context;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.theLoneWarrior.floating.R;
import com.theLoneWarrior.floating.pojoClass.AppInfo;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by HitRo on 02-05-2017.
 */

public class RecyclerViewAdapterResult extends RecyclerView.Adapter<RecyclerViewAdapterResult.DataViewHolder> {

    private ArrayList<AppInfo> result;
    private ListItemClickListener reference;
    private Service service;
    boolean flag1 = true, flag2 = true;
    int count, f;

    public RecyclerViewAdapterResult(ListItemClickListener reference, ArrayList<AppInfo> result) {
        this.result = result;
        this.reference = reference;
        service = (Service) reference;

    }

    @Override
    public RecyclerViewAdapterResult.DataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new DataViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_layout_result, parent, false));
    }


    @Override
    public void onBindViewHolder(RecyclerViewAdapterResult.DataViewHolder holder, int position) {
        AppInfo result = this.result.get(position);

        if (result.getAppName().length() <= 15) {
            holder.textView.setText(result.getAppName());
        } else {
            String sb = result.getAppName();
            String[] split = sb.split("\\s+");
            if (split[0].length() > 15) {
                String tenDigitAppName = split[0].substring(0, 9);
                holder.textView.setText(tenDigitAppName);
            } else {
                holder.textView.setText(split[0]);
            }
        }

        try {

            holder.imageView.setImageBitmap(scaleDownBitmap(MediaStore.Images.Media.getBitmap(service.getContentResolver(), result.getBitmapString()), 60, (Context) reference));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private Bitmap scaleDownBitmap(Bitmap photo, int newHeight, Context context) {

        final float densityMultiplier = context.getResources().getDisplayMetrics().density;

        int h = (int) (newHeight * densityMultiplier);
        int w = (int) (h * photo.getWidth() / ((double) photo.getHeight()));

        photo = Bitmap.createScaledBitmap(photo, w, h, true);

        return photo;
    }


    @Override
    public int getItemCount() {
        try {
            return result.size();
        } catch (Exception ignored) {
        }
        return 0;
    }

    class DataViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textView;
        ImageView imageView;

        DataViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.tv2);
            textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            imageView = (ImageView) itemView.findViewById(R.id.iv1);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            int checkedPosition = getAdapterPosition();
            reference.onListItemClick(checkedPosition, result);
        }
    }

    public interface ListItemClickListener {
        void onListItemClick(int checkedItemIndex, ArrayList<AppInfo> result);
    }


}
