package com.hitachi_tstv.mist.it.pod_nmt;

import android.content.Context;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Tunyaporn on 9/25/2017.
 */

public class JobAdapter extends BaseAdapter {
    private Context context;

    String dateString, tripNoString, subJobNoString, startDepartureDateString;
    String[] placeTypeStrings, planDtlIdStrings, timeArrivalStrings, stationNameStrings, receiveStatusStrings;
    ViewHolder viewholder;

    public JobAdapter(Context context, String[] planDtlIdStrings, String[] stationNameStrings, String[] timeArrivalStrings, String[] placeTypeStrings, String startDepartureDateString, String[] receiveStatusStrings) {
        this.context = context;
        this.planDtlIdStrings = planDtlIdStrings;
        this.timeArrivalStrings = timeArrivalStrings;
        this.stationNameStrings = stationNameStrings;
        this.placeTypeStrings = placeTypeStrings;
        this.startDepartureDateString = startDepartureDateString;
        this.receiveStatusStrings = receiveStatusStrings;
    }


    @Override
    public int getCount() {
        return planDtlIdStrings.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {

            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.listview_job, parent, false);

            viewholder = new ViewHolder(convertView);

            convertView.setTag(viewholder);

        } else {

            viewholder = (ViewHolder) convertView.getTag();
        }

        if (placeTypeStrings[position].equals("PLANT")) {
            viewholder.imgView.setImageResource(R.drawable.factory);
            viewholder.imgView.setColorFilter(new LightingColorFilter(Color.BLUE, Color.BLUE));
        } else {

            viewholder.imgView.setImageResource(R.drawable.home1);
        }
        viewholder.txtSup.setText(stationNameStrings[position]);
        viewholder.txtTime.setText(timeArrivalStrings[position]);

        if (receiveStatusStrings[position].equals("Y")) {
            viewholder.listJob.setForeground(context.getDrawable(R.drawable.layout_bg_3));
            viewholder.listJob.setClickable(true);


        } else {
            viewholder.listJob.setForeground(null);
            viewholder.listJob.setClickable(false);
        }
        Log.d("TAG", "receieve  ==>  " + receiveStatusStrings);

        return convertView;
    }

    static class ViewHolder {

        @BindView(R.id.imgView)
        ImageView imgView;
        @BindView(R.id.txtSup)
        TextView txtSup;
        @BindView(R.id.txtTime)
        TextView txtTime;
        @BindView(R.id.listJob)
        LinearLayout listJob;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
