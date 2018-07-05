package com.hitachi_tstv.mist.it.pod_nmt;

import android.content.Context;
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

public class TripAdapter extends BaseAdapter {
    TripViewHolder tripViewHolder;
    @BindView(R.id.tripTextviewTL)
    TextView tripTextviewTL;
    private Context context;
    private String[][] planDtl2IdStrings, suppCodeStrings, suppNameStrings, suppSeqStrings;
    private String[] endarraivalDateStrings,flag_completeStrings;
    private String[] positionStrings;

    public TripAdapter(Context context, String[][] planDtl2IdStrings, String[][] suppCodeStrings, String[][] suppNameStrings, String[][] suppSeqStrings, String[] positionStrings, String[] endarraivalDateStrings, String[] flag_completeStrings) {

        this.context = context;
        this.planDtl2IdStrings = planDtl2IdStrings;
        this.suppCodeStrings = suppCodeStrings;
        this.suppNameStrings = suppNameStrings;
        this.suppSeqStrings = suppSeqStrings;
        this.endarraivalDateStrings = endarraivalDateStrings;
        this.positionStrings = positionStrings;
        this.flag_completeStrings = flag_completeStrings;
    }


    @Override
    public int getCount() {
        return suppCodeStrings.length;

    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {

            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.trip_listview, viewGroup, false);

            tripViewHolder = new TripViewHolder(view);

            view.setTag(tripViewHolder);

        } else {

            tripViewHolder = (TripViewHolder) view.getTag();
        }

        tripViewHolder.imgStartTL.setImageResource(R.drawable.start);
        tripViewHolder.imgEndTL.setImageResource(R.drawable.end);

        tripViewHolder.tripTextviewTL.setText(context.getResources().getText(R.string.trip) + positionStrings[i]);
        tripViewHolder.stationStartTL.setText(suppCodeStrings[i][0] + ":" + suppNameStrings[i][0]);
        tripViewHolder.stationEndJobTL.setText(suppCodeStrings[i][1] + ":" + suppNameStrings[i][1]);

        if (!flag_completeStrings[i].equals("N")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                tripViewHolder.linearTrip.setForeground(context.getDrawable(R.drawable.layout_bg_3));
                tripViewHolder.linearTrip.setClickable(true);
            }

        } else {
            tripViewHolder.linearTrip.setForeground(null);
            tripViewHolder.linearTrip.setClickable(false);
        }
        Log.d("TAG", "End  ==>  " + endarraivalDateStrings[i]);

        return view;
    }

    static class TripViewHolder {
        @BindView(R.id.tripTextviewTL)
        TextView tripTextviewTL;
        @BindView(R.id.imgStartTL)
        ImageView imgStartTL;
        @BindView(R.id.stationStartTL)
        TextView stationStartTL;
        @BindView(R.id.imgEndTL)
        ImageView imgEndTL;
        @BindView(R.id.stationEndJobTL)
        TextView stationEndJobTL;
        @BindView(R.id.linearTrip)
        LinearLayout linearTrip;

        TripViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
