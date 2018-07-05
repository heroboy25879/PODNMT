package com.hitachi_tstv.mist.it.pod_nmt;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TripActivity extends AppCompatActivity {

    @BindView(R.id.dateBtnTrip)
    Button dateBtnTrip;
//    @BindView(R.id.imgDriverTrip)
//    ImageView imgDriverTrip;
    @BindView(R.id.truckIdValTrip)
    TextView truckIdValTrip;
    @BindView(R.id.truckTypeValTrip)
    TextView truckTypeValTrip;
    @BindView(R.id.middenLinTrip)
    LinearLayout middenLinTrip;
    @BindView(R.id.tripListviewTrip)
    ListView tripListviewTrip;
    private String[][] suppSeqStrings, suppCodeStrings, suppNameStrings, planDtl2IdStrings;
    private String[] loginStrings,flag_completeStrings, positionStrings, planDtlIdStrings, placeTypeStrings, transportTypeStrings,endarraivalDateStrings;
    String planDateStrings, planIdString, dateString;

    @Override
    public void onBackPressed() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.logout,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:

                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle(R.string.alert);
                dialog.setCancelable(true);
                dialog.setIcon(R.drawable.warning);
                dialog.setMessage(R.string.alert_logout);

                dialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        ComponentName componentName = intent.getComponent();
                        Intent backToMainIntent = IntentCompat.makeRestartActivityTask(componentName);
                        startActivity(backToMainIntent);
                    }
                });

                dialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                dialog.show();
                break;
            case R.id.refresh:
                Log.d("Tag", " " + dateString + " " + Arrays.toString(loginStrings) + " " + planIdString);
                Intent intent1 = new Intent(TripActivity.this, TripActivity.class);
                intent1.putExtra("Login", loginStrings);
                intent1.putExtra("Date", dateString);
                intent1.putExtra("PlanId", planIdString);
                startActivity(intent1);
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);
        ButterKnife.bind(this);

        //get Intent data
        loginStrings = getIntent().getStringArrayExtra("Login");
        dateString = getIntent().getStringExtra("Date");
        planIdString = getIntent().getStringExtra("PlanId");

        if (planIdString == null || planIdString.equals("")) {
            SynTripData synTripData = new SynTripData(TripActivity.this);
            synTripData.execute();
        } else {
            SynTripData synTripData = new SynTripData(TripActivity.this, planIdString);
            synTripData.execute();
        }


    }

    private class SynTripData extends AsyncTask<String, Void, String> {

        private Context context;
        private String planIdString;
        UtilityClass utilityClass;

        public SynTripData(Context context) {
            this.context = context;
            planIdString = "";
            utilityClass = new UtilityClass(context);
        }

        public SynTripData(Context context, String planIdString) {
            this.context = context;
            this.planIdString = planIdString;
            utilityClass = new UtilityClass(context);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(String... strings) {
            try {
                String deviceId = utilityClass.getDeviceID();
                String serial = utilityClass.getSerial();
                String deviceName = utilityClass.getDeviceName();
                Log.d("Tag", deviceId + "  " + serial + "device name " + deviceName);
                Log.d("Tag", "Send ==> " + planIdString);
                OkHttpClient okHttpClient = new OkHttpClient();
                RequestBody requestBody = new FormBody.Builder()
                        .add("isAdd", "true")
                        .add("driver_id", loginStrings[0])
                        .add("planId", planIdString)
                        .add("device_id", deviceId)
                        .add("serial", serial)
                        .add("device_name",deviceName)
                        .build();

                Request.Builder builder = new Request.Builder();
                Request request = builder.url(MyConstant.urlGetPlanTrip).post(requestBody).build();
                Response response = okHttpClient.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                Log.d("Tag", "e doInBack ==>" + e.toString() + "line::" + e.getStackTrace()[0].getLineNumber());
                return null;
            }

        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("Tag", "JSON---->" + s);

            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONObject jsonObject1 = jsonObject.getJSONObject("truckInfo");

                String pathImg = MyConstant.serverString + MyConstant.projectString + "/" + "app/MasterData/driver/avatar/" + loginStrings[3];
                Log.d("Tag", "pathImg---->" + pathImg);

                dateBtnTrip.setText(jsonObject1.getString("planDate"));
                planDateStrings = jsonObject1.getString("planDate");

                truckIdValTrip.setText(jsonObject1.getString("license"));
                truckTypeValTrip.setText(jsonObject1.getString("truckType_code"));
//                Picasso.with(context)
//                        .load(pathImg)
//                        .into(imgDriverTrip);

//                if (loginStrings[6].equals("M")) {
//                    imgDriverTrip.setImageResource(R.drawable.male);
//
//                } else {
//
//                    imgDriverTrip.setImageResource(R.drawable.female);
//                }


                JSONArray jsonArray = jsonObject.getJSONArray("tripInfo");
                planDtlIdStrings = new String[jsonArray.length()];
                placeTypeStrings = new String[jsonArray.length()];
                transportTypeStrings = new String[jsonArray.length()];
                positionStrings = new String[jsonArray.length()];
                endarraivalDateStrings = new String[jsonArray.length()];
                flag_completeStrings = new String[jsonArray.length()];

                suppSeqStrings = new String[jsonArray.length()][];
                suppCodeStrings = new String[jsonArray.length()][];
                suppNameStrings = new String[jsonArray.length()][];
                planDtl2IdStrings = new String[jsonArray.length()][];

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject3 = jsonArray.getJSONObject(i);
                    planDtlIdStrings[i] = jsonObject3.getString("planDtlId");
                    placeTypeStrings[i] = jsonObject3.getString("placeType");
                    transportTypeStrings[i] = jsonObject3.getString("transport_type");
                    endarraivalDateStrings[i] = jsonObject3.getString("en_arrivalDate");
                    flag_completeStrings[i] = jsonObject3.getString("flag_complete");
                    positionStrings[i] = String.valueOf(i + 1);

                    Log.d("Tag", "------>planDtlIdStrings:::--> " + planDtlIdStrings[i]);

                    JSONArray detailArray = jsonObject3.getJSONArray("detail");

                    suppSeqStrings[i] = new String[detailArray.length()];
                    suppCodeStrings[i] = new String[detailArray.length()];
                    suppNameStrings[i] = new String[detailArray.length()];
                    planDtl2IdStrings[i] = new String[detailArray.length()];

                    for (int j = 0; j < detailArray.length(); j++) {
                        JSONObject jsonObject5 = detailArray.getJSONObject(j);
                        suppSeqStrings[i][j] = jsonObject5.getString("suppSeq");
                        suppCodeStrings[i][j] = jsonObject5.getString("suppCode");
                        suppNameStrings[i][j] = jsonObject5.getString("suppName");
                        planDtl2IdStrings[i][j] = jsonObject5.getString("planDtl2Id");
                        Log.d("Tag", "------>planDtl2Id:::--> " + i + j);

                    }
                }
                Log.d("TAG", "END DATE ==>  " + endarraivalDateStrings[0]);


                TripAdapter tripAdapter = new TripAdapter(TripActivity.this, planDtl2IdStrings, suppCodeStrings, suppNameStrings, suppSeqStrings, positionStrings,endarraivalDateStrings,flag_completeStrings);
                tripListviewTrip.setAdapter(tripAdapter);

                tripListviewTrip.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent intent = new Intent(TripActivity.this, JobActivity.class);
                        intent.putExtra("Login", loginStrings);
                        intent.putExtra("planId", planIdString);
                        intent.putExtra("planDtlId", planDtlIdStrings[i]);
                        intent.putExtra("planDate", planDateStrings);
                        intent.putExtra("position", positionStrings[i]);
                        startActivity(intent);
                        finish();
                    }
                });


            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("Tag", "Exception :: " + e + " Line : " + e.getStackTrace()[0].getLineNumber());
            }
        }
    }


    @OnClick(R.id.dateBtnTrip)
    public void onViewClicked() {
        Intent intent = new Intent(TripActivity.this, DateDeliveryActivity.class);
        intent.putExtra("Login", loginStrings);
        intent.putExtra("planId", planIdString);
        startActivity(intent);
        finish();
    }
}
