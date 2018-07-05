package com.hitachi_tstv.mist.it.pod_nmt;

/**
 * Created by Tunyaporn on 9/26/2017.
 */
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Yaowaluk on 22/09/2560.
 */

public class PlanDeliveryAdapter extends BaseAdapter {
    PlanDeliveryViewHolder planDeliveryViewHolder;
    private Context context;
    private String[] planDtl2IdString, amountStrings;
    private String planDtlString;

    public PlanDeliveryAdapter(Context context, String planDtlString) {
        this.context = context;
        this.planDtl2IdString = planDtl2IdString;
        this.amountStrings = amountStrings;
        this.planDtlString = planDtlString;
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.plan_listview, viewGroup, false);
            planDeliveryViewHolder = new PlanDeliveryViewHolder(view);
            view.setTag(planDeliveryViewHolder);
        } else {
            planDeliveryViewHolder = (PlanDeliveryViewHolder) view.getTag();

        }
        planDeliveryViewHolder.supplierTxtview.setText("xxxxxxxxxxxxxx");
        planDeliveryViewHolder.amountTxtview.setText("1111111");


        return view;
    }

    static class PlanDeliveryViewHolder {
        @BindView(R.id.textView)
        TextView supplierTxtview;
        @BindView(R.id.textView2)
        TextView amountTxtview;

        PlanDeliveryViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

