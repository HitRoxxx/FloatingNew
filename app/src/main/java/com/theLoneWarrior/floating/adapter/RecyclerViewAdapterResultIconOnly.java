package com.theLoneWarrior.floating.adapter;

import android.app.Service;
import android.content.Context;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.theLoneWarrior.floating.R;
import com.theLoneWarrior.floating.pojoClass.AppInfo;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by HitRo on 02-05-2017.
 */

public class RecyclerViewAdapterResultIconOnly extends RecyclerView.Adapter<RecyclerViewAdapterResultIconOnly.DataViewHolder> {

    private ArrayList<AppInfo> result;
    private ListItemClickListener reference;
    private Service service;
    boolean flag1 = true, flag2 = true;
    int count, f;

    public RecyclerViewAdapterResultIconOnly(ListItemClickListener reference, ArrayList<AppInfo> result) {
        this.result = result;
        this.reference = reference;
        service = (Service) reference;

    }

    @Override
    public RecyclerViewAdapterResultIconOnly.DataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new DataViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_icon_only, parent, false));
    }


    @Override
    public void onBindViewHolder(RecyclerViewAdapterResultIconOnly.DataViewHolder holder, int position) {
        AppInfo result = this.result.get(position);
/*
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
        }*/

        try {
            //  Bitmap b =MediaStore.Images.Media.getBitmap(service.getContentResolver(), result.getBitmapString());
            //  b.compress(Bitmap.CompressFormat.JPEG, 50,null);
            holder.imageView.setImageBitmap(scaleDownBitmap(MediaStore.Images.Media.getBitmap(service.getContentResolver(), result.getBitmapString()), 40, (Context) reference));

            //  holder.imageView.setImageBitmap(scaleDownBitmap(b, 100, (Context) reference));
        } catch (IOException e) {
            e.printStackTrace();
            //Glide.with(holder.itemView).load(result.getBitmapString()).into(holder.imageView);
        }
       /* if (flag1) {
            if (!flag2) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    Runtime.getRuntime().gc();
                    Log.d("add", "" + count++);
                } else {
                    System.gc();
                }
                flag2 = true;
            } else {
                flag2 = false;
            }
            flag1 = false;
        } else {
            flag1 = true;
        }*/
        //Glide.with(holder.itemView).load(result.getBitmapString()).into(holder.imageView);

    }

  /*  private Bitmap StringToBitmap(String bitmapString) {
        try {
            byte[] encodeByte = Base64.decode(bitmapString, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        } catch (Exception e) {

            return null;
        }

    }*/

    private Bitmap scaleDownBitmap(Bitmap photo, int newHeight, Context context) {

        final float densityMultiplier = context.getResources().getDisplayMetrics().density;

        int h = (int) (newHeight * densityMultiplier);
        int w = (int) (h * photo.getWidth() / ((double) photo.getHeight()));

        photo = Bitmap.createScaledBitmap(photo, w, h, true);


       /* ByteArrayOutputStream bAOS = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 10, bAOS);
        byte[] b = bAOS.toByteArray();

        try{
        //    b=Base64.decode(Base64.encodeToString(b, Base64.DEFAULT),Base64.DEFAULT);
            photo= BitmapFactory.decodeByteArray(b, 0, b.length);
            bAOS.flush();
            bAOS.close();
        }catch(Exception e){
            e.getMessage();
            return null;
        }*/

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
      //  TextView textView;
        ImageView imageView;

        DataViewHolder(View itemView) {
            super(itemView);
           // textView = (TextView) itemView.findViewById(R.id.tv2);
           // textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            imageView = (ImageView) itemView.findViewById(R.id.iv1);
            imageView.setOnClickListener(this);
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
