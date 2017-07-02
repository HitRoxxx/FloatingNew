package com.theLoneWarrior.floating.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.provider.MediaStore;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.theLoneWarrior.floating.R;
import com.theLoneWarrior.floating.SelectedApplication;
import com.theLoneWarrior.floating.helper.ItemTouchHelperAdapter;
import com.theLoneWarrior.floating.helper.ItemTouchHelperViewHolder;
import com.theLoneWarrior.floating.helper.OnStartDragListener;
import com.theLoneWarrior.floating.pojoClass.PackageInfoStruct;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import static com.theLoneWarrior.floating.R.id.tv;

/**
 * Created by HitRo on 02-05-2017.
 */

public class RecyclerViewAdapterSelectedApp extends RecyclerView.Adapter<RecyclerViewAdapterSelectedApp.DataViewHolder> implements ItemTouchHelperAdapter {

    private SelectedApplication reference;
    private ArrayList<PackageInfoStruct> filteredInstalledPackageDetail;
    private AppCompatActivity activity;
    private final OnStartDragListener mDragStartListener;


    public RecyclerViewAdapterSelectedApp(SelectedApplication reference, ArrayList<PackageInfoStruct> installedPackageDetails) {
        filteredInstalledPackageDetail = installedPackageDetails;
        this.reference = reference;
        activity = reference;
        mDragStartListener = reference;

    }

    @Override
    public RecyclerViewAdapterSelectedApp.DataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DataViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_layout_selected_app, parent, false));
    }


    @Override
    public void onBindViewHolder(final RecyclerViewAdapterSelectedApp.DataViewHolder holder, int position) {
        PackageInfoStruct packageInfoStruct = filteredInstalledPackageDetail.get(position);
        holder.textView.setText(packageInfoStruct.getAppName());

        // holder.imageView.setImageBitmap(StringToBitmap(packageInfoStruct.getBitmapString()));
        // Glide.with((activity).load(StringToBitmap(packageInfoStruct.getBitmapString())).into(holder.imageView);
        //Drawable d =new BitmapDrawable(activity.getResources(),StringToBitmap(packageInfoStruct.getBitmapString()));
      /*  try {
            Glide.with(holder.itemView).load(packageInfoStruct.getBitmapString()).into(holder.imageView);
        } catch (Exception e) {
            holder.imageView.setImageResource(R.drawable.default_image);
        }*/
        //  holder.imageView.setImageURI(packageInfoStruct.getBitmapString());
        try {
            holder.imageView.setImageBitmap(scaleDownBitmap(MediaStore.Images.Media.getBitmap(activity.getContentResolver(), packageInfoStruct.getBitmapString()), 50, reference));
        } catch (IOException e) {
            e.printStackTrace();
            holder.imageView.setImageURI(packageInfoStruct.getBitmapString());
        }
       /* if (checkBoxEnabled)
            holder.checkBox.setChecked(packageInfoStruct.checked);*/

        holder.itemView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, final MotionEvent event) {
                v.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                            mDragStartListener.onStartDrag(holder);
                        }
                        return false;
                    }
                });

                return false;
            }
        });
    }

    /* private Bitmap StringToBitmap(String bitmapString) {
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
        return filteredInstalledPackageDetail.size();
    }


    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(filteredInstalledPackageDetail, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        saveData();
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        filteredInstalledPackageDetail.remove(position);
        notifyItemRemoved(position);
        saveData();
    }

    private void saveData() {
        if(filteredInstalledPackageDetail!=null) {
            StringBuilder PacName = new StringBuilder("");

            for (PackageInfoStruct str : filteredInstalledPackageDetail) {

                PacName.append(str.getPacName()).append("+");

            }
            StringBuilder AppName = new StringBuilder("");

            for (PackageInfoStruct str : filteredInstalledPackageDetail) {

                AppName.append(str.getAppName()).append("+");

            }

            StringBuilder AppImage = new StringBuilder("");

            for (PackageInfoStruct str : filteredInstalledPackageDetail) {

                AppImage.append(str.getBitmapString()).append("+");

            }
            SharedPreferences selectedAppPreference = reference.getSharedPreferences("SelectedApp", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = selectedAppPreference.edit();
            editor.clear();
            editor.putString("AppName", String.valueOf(AppName));
            editor.putString("PacName", String.valueOf(PacName));
            editor.putString("AppImage", String.valueOf(AppImage));
            editor.apply();

        }
    }


    class DataViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {
        TextView textView;
        ImageView imageView;


        DataViewHolder(final View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(tv);
            imageView = (ImageView) itemView.findViewById(R.id.iv);

        }


        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(Color.WHITE);
        }
    }


}
