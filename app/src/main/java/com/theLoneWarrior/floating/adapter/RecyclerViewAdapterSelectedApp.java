package com.theLoneWarrior.floating.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.theLoneWarrior.floating.R;
import com.theLoneWarrior.floating.SelectedApplication;
import com.theLoneWarrior.floating.helper.ItemTouchHelperAdapter;
import com.theLoneWarrior.floating.helper.ItemTouchHelperViewHolder;
import com.theLoneWarrior.floating.helper.OnStartDragListener;
import com.theLoneWarrior.floating.pojoClass.AppInfo;

import java.util.ArrayList;
import java.util.Collections;

import static com.theLoneWarrior.floating.R.id.tv;

/**
 * Created by HitRo on 02-05-2017.
 */

public class RecyclerViewAdapterSelectedApp extends RecyclerView.Adapter<RecyclerViewAdapterSelectedApp.DataViewHolder> implements ItemTouchHelperAdapter {

    private SelectedApplication reference;
    private ArrayList<AppInfo> filteredInstalledPackageDetail;
    private final OnStartDragListener mDragStartListener;

    private int mExpandedPosition = -1;

    public RecyclerViewAdapterSelectedApp(SelectedApplication reference, ArrayList<AppInfo> installedPackageDetails) {
        filteredInstalledPackageDetail = installedPackageDetails;
        this.reference = reference;
        mDragStartListener = reference;
    }

    @Override
    public RecyclerViewAdapterSelectedApp.DataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DataViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_layout_selected_app, parent, false));
    }


    @Override
    public void onBindViewHolder(final DataViewHolder holder, int position) {
        final AppInfo packageInfoStruct = filteredInstalledPackageDetail.get(position);
        holder.textView.setText(packageInfoStruct.getAppName());
      /*  if ( 2 == holder.itemView.getContext().getSharedPreferences("Setting", Context.MODE_PRIVATE).getInt("OutputView", 0)&& position < 8)
        {
            holder.itemView.setBackgroundResource(R.color.halfPink);
        }*/

            if (packageInfoStruct.getAppName().equals("FloSo"))
                holder.uninstall.setVisibility(View.GONE);

        try {
            Glide.with(holder.itemView).load(packageInfoStruct.getBitmapString()).into(holder.imageView);
        } catch (Exception e) {
            holder.imageView.setImageResource(R.drawable.default_image);
        }

        final boolean isExpanded = position == mExpandedPosition;
        holder.expandableView.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        if (!holder.expandableView.isShown()) {
            holder.textView.setTextColor(Color.BLACK);
            holder.packageInfo.setText(packageInfoStruct.getPacName());
        }


        holder.itemView.setActivated(isExpanded);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mExpandedPosition = isExpanded ? -1 : holder.getAdapterPosition();
                notifyDataSetChanged();
            }

        });


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
        if (filteredInstalledPackageDetail != null) {
            StringBuilder PacName = new StringBuilder("");

            for (AppInfo str : filteredInstalledPackageDetail) {

                PacName.append(str.getPacName()).append("+");

            }
            StringBuilder AppName = new StringBuilder("");

            for (AppInfo str : filteredInstalledPackageDetail) {

                AppName.append(str.getAppName()).append("+");

            }

            StringBuilder AppImage = new StringBuilder("");

            for (AppInfo str : filteredInstalledPackageDetail) {

                AppImage.append(str.getBitmapString()).append("+");

            }
            StringBuilder AppSource = new StringBuilder("");

            for (AppInfo str : filteredInstalledPackageDetail) {

                AppSource.append(str.getSource()).append("+");

            }

            StringBuilder AppData = new StringBuilder("");

            for (AppInfo str : filteredInstalledPackageDetail) {

                AppData.append(str.getData()).append("+");

            }
            SharedPreferences selectedAppPreference = reference.getSharedPreferences("SelectedApp", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = selectedAppPreference.edit();
            editor.clear();
            editor.putString("AppName", String.valueOf(AppName));
            editor.putString("PacName", String.valueOf(PacName));
            editor.putString("AppImage", String.valueOf(AppImage));
            editor.putString("AppSource", String.valueOf(AppSource));
            editor.putString("AppData", String.valueOf(AppData));
            editor.apply();

        }
    }


    class DataViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder, View.OnClickListener {
        TextView textView;
        ImageView imageView;
        TextView packageInfo;
        Button share, extract, uninstall;
        ConstraintLayout expandableView;

        DataViewHolder(final View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(tv);
            imageView = (ImageView) itemView.findViewById(R.id.iv);
            packageInfo = (TextView) itemView.findViewById(R.id.packageName);

            share = (Button) itemView.findViewById(R.id.shareApk);
            uninstall = (Button) itemView.findViewById(R.id.uninstallApk);
            extract = (Button) itemView.findViewById(R.id.extractApk);

            expandableView = (ConstraintLayout) itemView.findViewById(R.id.expandableView);

            share.setOnClickListener(this);
            uninstall.setOnClickListener(this);
            extract.setOnClickListener(this);

        }


        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(Color.WHITE);
        }

        @Override
        public void onClick(View v) {

            final AppInfo packageInfoStruct = filteredInstalledPackageDetail.get(getAdapterPosition());
            reference.onButtonClickListener(packageInfoStruct, v);
        }
    }

    public interface ButtonClickListener {
        void onButtonClickListener(AppInfo app, View v);
    }
}
