package com.hitachi_tstv.mist.it.pod_nmt;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DateDeliveryActivity extends AppCompatActivity {

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(DateDeliveryActivity.this, TripActivity.class);
        intent.putExtra("Login", loginStrings);
        intent.putExtra("Date", dateString);
        intent.putExtra("PlanId", planIdString);

        startActivity(intent);
        finish();
    }

    @BindView(R.id.lisDADate)
    ListView lisDADate;

    String[] loginStrings, deliveryDateStrings, sumjobStrings, planIdStrings;
    String dateString,planIdString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_delivery);
        ButterKnife.bind(this);
        loginStrings = getIntent().getStringArrayExtra("Login");
        dateString = getIntent().getStringExtra("Date");
        planIdString = getIntent().getStringExtra("planId");

        SyncGetDate syncGetDate = new SyncGetDate(DateDeliveryActivity.this);
        syncGetDate.execute();
    }
    @OnItemClick(R.id.lisDADate)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(DateDeliveryActivity.this, TripActivity.class);
        intent.putExtra("Login", loginStrings);
        intent.putExtra("Date", deliveryDateStrings[position]);
        intent.putExtra("PlanId", planIdStrings[position]);
        Log.d("Tag", "Send ==> " + deliveryDateStrings[position] + " " + Arrays.toString(loginStrings)+ " "+ planIdStrings[position]);
        startActivity(intent);
        finish();
    }

    class SyncGetDate extends AsyncTask<Void, Void, String> {
        Context context;
        UtilityClass utilityClass;


        public SyncGetDate(Context context) {
            this.context = context;
            utilityClass = new UtilityClass(context);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(Void... params) {
            try {
                String deviceId = utilityClass.getDeviceID();
                String serial = utilityClass.getSerial();
                String deviceName = utilityClass.getDeviceName();
                Log.d("Tag", deviceId + "  " + serial + "device name ");
                OkHttpClient okHttpClient = new OkHttpClient();
                RequestBody requestBody = new FormBody.Builder()
                        .add("isAdd", "true")
                        .add("driver_id",loginStrings[0])
                        .add("device_id", deviceId)
                        .add("serial", serial)
                        .add("device_name",deviceName)
                        .build();
                Request.Builder builder = new Request.Builder();
                Request request = builder.post(requestBody).url(MyConstant.urlGetPlanDate).build();
                Response response = okHttpClient.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                Log.d("Tag", "Error Date Activity SyncGetDate do in back ==> " + e +" Line " + e.getStackTrace()[0].getLineNumber());
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("Tag", "S ==> " + s);

            try {
                JSONArray jsonArray = new JSONArray(s);
                deliveryDateStrings = new String[jsonArray.length()];
                sumjobStrings = new String[jsonArray.length()];
                planIdStrings = new String[jsonArray.length()];

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    deliveryDateStrings[i] = jsonObject.getString("planDate");
                    sumjobStrings[i] = jsonObject.getString("cnt_store");
                    planIdStrings[i] = jsonObject.getString("planId");
                }

                PlanDateAdaptor planDateAdaptor = new PlanDateAdaptor(context, deliveryDateStrings, sumjobStrings);
                lisDADate.setAdapter(planDateAdaptor);

            } catch (JSONException e) {
                Log.d("Tag", "Error Date Activity SyncGetDate on post JSONArray ==> " + e +" Line " + e.getStackTrace()[0].getLineNumber());

            }
        }

        protected class PlanDateAdaptor extends BaseAdapter {
            Context context;
            String[] dateStrings, jobStrings;
            ViewHolder viewHolder;

            public PlanDateAdaptor(Context context, String[] dateStrings, String[] jobStrings) {
                this.context = context;
                this.dateStrings = dateStrings;
                this.jobStrings = jobStrings;
            }

            @Override
            public int getCount() {
                return dateStrings.length;
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }



            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(context).inflate(R.layout.date_listview, null);
                    viewHolder = new ViewHolder(convertView);
                    convertView.setTag(viewHolder);

                } else {
                    viewHolder = (ViewHolder) convertView.getTag();
                }

                String date, job;

                date = getResources().getString(R.string.Date) + " : " + dateStrings[position];
                job = jobStrings[position] + " " + getResources().getString(R.string.trip);
                viewHolder.dateTextView.setText(date);
                viewHolder.sumjobTextView.setText(job);

                return convertView;
            }


            class ViewHolder {
                @BindView(R.id.txtDLVDate)
                TextView dateTextView;
                @BindView(R.id.txtDLVSumjob)
                TextView sumjobTextView;

                ViewHolder(View view) {
                    ButterKnife.bind(this, view);
                }
            }
        }
    }
}