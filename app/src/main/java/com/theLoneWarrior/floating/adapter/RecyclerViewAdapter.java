package com.theLoneWarrior.floating.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.theLoneWarrior.floating.R;
import com.theLoneWarrior.floating.pojoClass.PackageInfoStruct;

import java.util.ArrayList;

import static com.theLoneWarrior.floating.R.id.tv;

/**
 * Created by HitRo on 02-05-2017.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.DataViewHolder> implements Filterable {

    private ArrayList<PackageInfoStruct> installedPackageDetails;
    private ListItemCheckListener reference;
    private ArrayList<PackageInfoStruct> filteredInstalledPackageDetail;
    private AppCompatActivity activity;
    private boolean checkBoxEnabled;

    public RecyclerViewAdapter(ListItemCheckListener reference, ArrayList<PackageInfoStruct> installedPackageDetails, boolean b) {
        this.installedPackageDetails = installedPackageDetails;
        filteredInstalledPackageDetail = installedPackageDetails;
        this.reference = reference;
        activity = (AppCompatActivity) reference;
        checkBoxEnabled = b;
    }

    @Override
    public RecyclerViewAdapter.DataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DataViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_layout, parent, false));
    }


    @Override
    public void onBindViewHolder(RecyclerViewAdapter.DataViewHolder holder, int position) {
        PackageInfoStruct packageInfoStruct = filteredInstalledPackageDetail.get(position);
        holder.textView.setText(packageInfoStruct.getAppName());

        // holder.imageView.setImageBitmap(StringToBitmap(packageInfoStruct.getBitmapString()));
        // Glide.with((activity).load(StringToBitmap(packageInfoStruct.getBitmapString())).into(holder.imageView);
        //Drawable d =new BitmapDrawable(activity.getResources(),StringToBitmap(packageInfoStruct.getBitmapString()));
        try {
            Glide.with(holder.itemView.getContext()).load(packageInfoStruct.getBitmapString()).into(holder.imageView);
        } catch (Exception e) {
            holder.imageView.setImageResource(R.drawable.default_image);
        }
        //  holder.imageView.setImageURI(packageInfoStruct.getBitmapString());
       /* try {
            holder.imageView.setImageBitmap(scaleDownBitmap(MediaStore.Images.Media.getBitmap(activity.getContentResolver(), packageInfoStruct.getBitmapString()), 50, (Context) reference));
        } catch (IOException e) {
            e.printStackTrace();
            holder.imageView.setImageURI(packageInfoStruct.getBitmapString());
        }*/
        if (checkBoxEnabled)
            holder.checkBox.setChecked(packageInfoStruct.checked);
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
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();

                if (charString.isEmpty()) {

                    filteredInstalledPackageDetail = installedPackageDetails;
                } else {

                    ArrayList<PackageInfoStruct> filteredList = new ArrayList<>();

                    for (PackageInfoStruct androidVersion : installedPackageDetails) {

                        if (androidVersion.getAppName().toLowerCase().contains(charString)) {

                            filteredList.add(androidVersion);
                        }
                    }

                    filteredInstalledPackageDetail = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredInstalledPackageDetail;
                return filterResults;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredInstalledPackageDetail = (ArrayList<PackageInfoStruct>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface ListItemCheckListener {
        void onListItemCheck(int checkedItemIndex, CheckBox cb, ArrayList<PackageInfoStruct> pb);
    }

    class DataViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textView;
        ImageView imageView;
        CheckBox checkBox;

        DataViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(tv);
            imageView = (ImageView) itemView.findViewById(R.id.iv);
            checkBox = (CheckBox) itemView.findViewById(R.id.cb);
            if (checkBoxEnabled) {
               checkBox.setVisibility(View.VISIBLE);
                checkBox.setOnClickListener(this);
            }
            else
            {
                checkBox.setVisibility(View.GONE);
            }
        }


        @Override
        public void onClick(View v) {
            int checkedPosition = getAdapterPosition();
            reference.onListItemCheck(checkedPosition, (CheckBox) v, filteredInstalledPackageDetail);
        }
    }


}
