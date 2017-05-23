package com.theLoneWarrior.floating.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

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

    public RecyclerViewAdapter(ListItemCheckListener reference, ArrayList<PackageInfoStruct> installedPackageDetails) {
        this.installedPackageDetails = installedPackageDetails;
        filteredInstalledPackageDetail = installedPackageDetails;
        this.reference = reference;
    }

    @Override
    public RecyclerViewAdapter.DataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View v = LayoutInflater.from(context).inflate(R.layout.recycle_view_layout, parent, false);
        return new DataViewHolder(v);
    }


    @Override
    public void onBindViewHolder(RecyclerViewAdapter.DataViewHolder holder, int position) {
        PackageInfoStruct packageInfoStruct = filteredInstalledPackageDetail.get(position);
        holder.textView.setText(packageInfoStruct.getAppName());
        holder.imageView.setImageBitmap(StringToBitmap(packageInfoStruct.getBitmapString()));
        holder.checkBox.setChecked(packageInfoStruct.checked);
    }

    private Bitmap StringToBitmap(String bitmapString) {
        try {
            byte[] encodeByte = Base64.decode(bitmapString, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        } catch (Exception e) {
            return null;
        }

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
            checkBox.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            int checkedPosition = getAdapterPosition();
            reference.onListItemCheck(checkedPosition, (CheckBox) v, filteredInstalledPackageDetail);
        }
    }


}
