package com.hitachi_tstv.mist.it.pod_nmt;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapProgressBar;
import com.beardedhen.androidbootstrap.api.attributes.BootstrapBrand;
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.hitachi_tstv.mist.it.pod_nmt.MyConstant.urlSavePicture;
import static com.hitachi_tstv.mist.it.pod_nmt.MyConstant.urlUploadPicture;

public class SupplierDeliveryActivity extends AppCompatActivity {

    @BindView(R.id.txt_name)
    TextView nameTextView;
    @BindView(R.id.progess_truck)
    BootstrapProgressBar truckProgress;
    @BindView(R.id.et_comment)
    EditText commentEditText;
    @BindView(R.id.btn_arrival)
    Button arrivalButton;
    @BindView(R.id.btn_confirm)
    Button confirmButton;
    @BindView(R.id.spnSDAPercentage)
    Spinner percentageSpinner;
    @BindView(R.id.editText)
    EditText PalletEditText;
    @BindView(R.id.img_left)
    ImageView leftImageView;
    @BindView(R.id.img_back)
    ImageView backImageView;
    @BindView(R.id.img_right)
    ImageView rightImageView;
    @BindView(R.id.img_4)
    ImageView fourthImageView;
    @BindView(R.id.img_5)
    ImageView fifthImageView;
    @BindView(R.id.img_6)
    ImageView sixthImageView;
    @BindView(R.id.img_7)
    ImageView seventhImageView;
    @BindView(R.id.linBottom)
    LinearLayout linBottom;
    @BindView(R.id.btn_savepic)
    Button savepicButton;

    String planDtl2IdString, suppCodeString, suppNameString, totalPercentageString, spinnerValueString, flagArrivalString, positionString, planDtlIdString;
    String dateString, planIdString, transportTypeString, pathSeal1String, pathSeal2String, pathSeal3String, pathPack4String, pathPack5String, pathPack6String, pathPack7String;
    Uri seal1Uri, seal2Uri, seal3Uri, pack4Uri, pack5Uri, pack6Uri, pack7Uri;
    Bitmap imgSeal1Bitmap, imgSeal2Bitmap, imgSeal3Bitmap, imgPack4Bitmap, imgPack5Bitmap, imgPack6Bitmap, imgPack7Bitmap;
    Boolean doubleBackPressABoolean = false;
    BootstrapBrand BootstrapBrandValueString;
    Boolean imgSeal1ABoolean, imgSeal2ABoolean, imgSeal3ABoolean, imgPack4ABoolean, imgPack5ABoolean, imgPack6ABoolean, imgPack7ABoolean;
    String[] loginStrings;
    UploadImageUtils uploadImageUtils;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                Intent intent1 = new Intent(SupplierDeliveryActivity.this, SupplierDeliveryActivity.class);
                intent1.putExtra("planDtl2_id", planDtl2IdString);
                intent1.putExtra("Login", loginStrings);
                intent1.putExtra("planDtlId", planDtlIdString);
                intent1.putExtra("position", positionString);
                intent1.putExtra("Date", dateString);
                intent1.putExtra("planId", planIdString);
                intent1.putExtra("transporttype", transportTypeString);
                startActivity(intent1);
                finish();
                break;
        }


        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier_delivery);
        ButterKnife.bind(this);

        setData();

        SyncGetTripDetailPickup syncGetTripDetailPickup = new SyncGetTripDetailPickup(this);
        syncGetTripDetailPickup.execute();
    }

    private void setData() {
        planDtl2IdString = getIntent().getStringExtra("planDtl2_id");
        loginStrings = getIntent().getStringArrayExtra("Login");
        planDtlIdString = getIntent().getStringExtra("planDtlId");
        positionString = getIntent().getStringExtra("position");
        dateString = getIntent().getStringExtra("Date");
        planIdString = getIntent().getStringExtra("planId");
        transportTypeString = getIntent().getStringExtra("transporttype");

        pathSeal1String = "";
        pathSeal2String = "";
        pathSeal3String = "";
        pathPack4String = "";
        pathPack5String = "";
        pathPack6String = "";
        pathPack7String = "";

        imgSeal1ABoolean = false;
        imgSeal2ABoolean = false;
        imgSeal3ABoolean = false;
        imgPack4ABoolean = false;
        imgPack5ABoolean = false;
        imgPack6ABoolean = false;
        imgPack7ABoolean = false;
    }

    @Override
    public void onBackPressed() {
        if (doubleBackPressABoolean) {
            Intent intent = new Intent(SupplierDeliveryActivity.this, JobActivity.class);
            intent.putExtra("Login", loginStrings);
            intent.putExtra("planId", planIdString);
            intent.putExtra("planDtlId", planDtlIdString);
            intent.putExtra("planDate", dateString);
            intent.putExtra("position", positionString);
            startActivity(intent);
            finish();
        }

        this.doubleBackPressABoolean = true;
        Toast.makeText(this, getResources().getText(R.string.check_back), Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackPressABoolean = false;
            }
        }, 2000);
    }

    String[] getSizeSpinner(int size) {
        String[] sizeStrings;
        switch (size) {
            case 100:
                sizeStrings = new String[1];
                sizeStrings[0] = "100";
                break;
            case 90:
                sizeStrings = new String[2];
                sizeStrings[0] = "90";
                sizeStrings[1] = "100";
                break;
            case 80:
                sizeStrings = new String[3];
                sizeStrings[0] = "80";
                sizeStrings[1] = "90";
                sizeStrings[2] = "100";
                break;
            case 70:
                sizeStrings = new String[4];
                sizeStrings[0] = "70";
                sizeStrings[1] = "80";
                sizeStrings[2] = "90";
                sizeStrings[3] = "100";
                break;
            case 60:
                sizeStrings = new String[5];
                sizeStrings[0] = "60";
                sizeStrings[1] = "70";
                sizeStrings[2] = "80";
                sizeStrings[3] = "90";
                sizeStrings[4] = "100";
                break;
            case 50:
                sizeStrings = new String[6];
                sizeStrings[0] = "50";
                sizeStrings[1] = "60";
                sizeStrings[2] = "70";
                sizeStrings[3] = "80";
                sizeStrings[4] = "90";
                sizeStrings[5] = "100";
                break;
            case 40:
                sizeStrings = new String[7];
                sizeStrings[0] = "40";
                sizeStrings[1] = "50";
                sizeStrings[2] = "60";
                sizeStrings[3] = "70";
                sizeStrings[4] = "80";
                sizeStrings[5] = "90";
                sizeStrings[6] = "100";
                break;
            case 30:
                sizeStrings = new String[8];
                sizeStrings[0] = "30";
                sizeStrings[1] = "40";
                sizeStrings[2] = "50";
                sizeStrings[3] = "60";
                sizeStrings[4] = "70";
                sizeStrings[5] = "80";
                sizeStrings[6] = "90";
                sizeStrings[7] = "100";
                break;
            case 20:
                sizeStrings = new String[9];
                sizeStrings[0] = "20";
                sizeStrings[1] = "30";
                sizeStrings[2] = "40";
                sizeStrings[3] = "50";
                sizeStrings[4] = "60";
                sizeStrings[5] = "70";
                sizeStrings[6] = "80";
                sizeStrings[7] = "90";
                sizeStrings[8] = "100";
                break;
            case 10:
                sizeStrings = new String[10];
                sizeStrings[0] = "10";
                sizeStrings[1] = "20";
                sizeStrings[2] = "30";
                sizeStrings[3] = "40";
                sizeStrings[4] = "50";
                sizeStrings[5] = "60";
                sizeStrings[6] = "70";
                sizeStrings[7] = "80";
                sizeStrings[8] = "90";
                sizeStrings[9] = "100";
                break;
            case 0:
                sizeStrings = new String[11];
                sizeStrings[0] = "0";
                sizeStrings[1] = "10";
                sizeStrings[2] = "20";
                sizeStrings[3] = "30";
                sizeStrings[4] = "40";
                sizeStrings[5] = "50";
                sizeStrings[6] = "60";
                sizeStrings[7] = "70";
                sizeStrings[8] = "80";
                sizeStrings[9] = "90";
                sizeStrings[10] = "100";
                break;
            default:
                sizeStrings = null;
                break;
        }

        return sizeStrings;
    }

    BootstrapBrand[] getColorSpinner(int color) {
        BootstrapBrand[] colorStrings;
        switch (color) {
            case 100:
                colorStrings = new BootstrapBrand[1];
                colorStrings[0] = DefaultBootstrapBrand.DANGER;
                break;
            case 90:
                colorStrings = new BootstrapBrand[2];
                colorStrings[0] = DefaultBootstrapBrand.DANGER;
                colorStrings[1] = DefaultBootstrapBrand.DANGER;
                break;
            case 80:
                colorStrings = new BootstrapBrand[3];
                colorStrings[0] = DefaultBootstrapBrand.DANGER;
                colorStrings[1] = DefaultBootstrapBrand.DANGER;
                colorStrings[2] = DefaultBootstrapBrand.DANGER;
                break;
            case 70:
                colorStrings = new BootstrapBrand[4];
                colorStrings[0] = DefaultBootstrapBrand.WARNING;
                colorStrings[1] = DefaultBootstrapBrand.DANGER;
                colorStrings[2] = DefaultBootstrapBrand.DANGER;
                colorStrings[3] = DefaultBootstrapBrand.DANGER;
                break;
            case 60:
                colorStrings = new BootstrapBrand[5];
                colorStrings[0] = DefaultBootstrapBrand.WARNING;
                colorStrings[1] = DefaultBootstrapBrand.WARNING;
                colorStrings[2] = DefaultBootstrapBrand.DANGER;
                colorStrings[3] = DefaultBootstrapBrand.DANGER;
                colorStrings[4] = DefaultBootstrapBrand.DANGER;
                break;
            case 50:
                colorStrings = new BootstrapBrand[6];
                colorStrings[0] = DefaultBootstrapBrand.WARNING;
                colorStrings[1] = DefaultBootstrapBrand.WARNING;
                colorStrings[2] = DefaultBootstrapBrand.WARNING;
                colorStrings[3] = DefaultBootstrapBrand.DANGER;
                colorStrings[4] = DefaultBootstrapBrand.DANGER;
                colorStrings[5] = DefaultBootstrapBrand.DANGER;
                break;
            case 40:
                colorStrings = new BootstrapBrand[7];
                colorStrings[0] = DefaultBootstrapBrand.SUCCESS;
                colorStrings[1] = DefaultBootstrapBrand.WARNING;
                colorStrings[2] = DefaultBootstrapBrand.WARNING;
                colorStrings[3] = DefaultBootstrapBrand.WARNING;
                colorStrings[4] = DefaultBootstrapBrand.DANGER;
                colorStrings[5] = DefaultBootstrapBrand.DANGER;
                colorStrings[6] = DefaultBootstrapBrand.DANGER;
                break;
            case 30:
                colorStrings = new BootstrapBrand[8];
                colorStrings[0] = DefaultBootstrapBrand.SUCCESS;
                colorStrings[1] = DefaultBootstrapBrand.SUCCESS;
                colorStrings[2] = DefaultBootstrapBrand.WARNING;
                colorStrings[3] = DefaultBootstrapBrand.WARNING;
                colorStrings[4] = DefaultBootstrapBrand.WARNING;
                colorStrings[5] = DefaultBootstrapBrand.DANGER;
                colorStrings[6] = DefaultBootstrapBrand.DANGER;
                colorStrings[7] = DefaultBootstrapBrand.DANGER;
                break;
            case 20:
                colorStrings = new BootstrapBrand[9];
                colorStrings[0] = DefaultBootstrapBrand.SUCCESS;
                colorStrings[1] = DefaultBootstrapBrand.SUCCESS;
                colorStrings[2] = DefaultBootstrapBrand.SUCCESS;
                colorStrings[3] = DefaultBootstrapBrand.WARNING;
                colorStrings[4] = DefaultBootstrapBrand.WARNING;
                colorStrings[5] = DefaultBootstrapBrand.WARNING;
                colorStrings[6] = DefaultBootstrapBrand.DANGER;
                colorStrings[7] = DefaultBootstrapBrand.DANGER;
                colorStrings[8] = DefaultBootstrapBrand.DANGER;
                break;
            case 10:
                colorStrings = new BootstrapBrand[10];
                colorStrings[0] = DefaultBootstrapBrand.SUCCESS;
                colorStrings[1] = DefaultBootstrapBrand.SUCCESS;
                colorStrings[2] = DefaultBootstrapBrand.SUCCESS;
                colorStrings[3] = DefaultBootstrapBrand.SUCCESS;
                colorStrings[4] = DefaultBootstrapBrand.WARNING;
                colorStrings[5] = DefaultBootstrapBrand.WARNING;
                colorStrings[6] = DefaultBootstrapBrand.WARNING;
                colorStrings[7] = DefaultBootstrapBrand.DANGER;
                colorStrings[8] = DefaultBootstrapBrand.DANGER;
                colorStrings[9] = DefaultBootstrapBrand.DANGER;
                break;
            case 0:
                colorStrings = new BootstrapBrand[11];
                colorStrings[0] = DefaultBootstrapBrand.SUCCESS;
                colorStrings[1] = DefaultBootstrapBrand.SUCCESS;
                colorStrings[2] = DefaultBootstrapBrand.SUCCESS;
                colorStrings[3] = DefaultBootstrapBrand.SUCCESS;
                colorStrings[4] = DefaultBootstrapBrand.SUCCESS;
                colorStrings[5] = DefaultBootstrapBrand.WARNING;
                colorStrings[6] = DefaultBootstrapBrand.WARNING;
                colorStrings[7] = DefaultBootstrapBrand.WARNING;
                colorStrings[8] = DefaultBootstrapBrand.DANGER;
                colorStrings[9] = DefaultBootstrapBrand.DANGER;
                colorStrings[10] = DefaultBootstrapBrand.DANGER;
                break;
            default:
                colorStrings = null;
                break;
        }

        return colorStrings;
    }

    class SyncGetTripDetailPickup extends AsyncTask<Void, Void, String> {
        Context context;
        UtilityClass utilityClass;

        public SyncGetTripDetailPickup(Context context) {
            this.context = context;
            utilityClass = new UtilityClass(context);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(Void... voids) {
            try {
                String deviceId = utilityClass.getDeviceID();
                String serial = utilityClass.getSerial();
                String deviceName = utilityClass.getDeviceName();
                Log.d("Tag", planDtl2IdString);
                Log.d("Tag", deviceId + "  " + serial + "device name " + deviceName);
                OkHttpClient okHttpClient = new OkHttpClient();
                RequestBody requestBody = new FormBody.Builder()
                        .add("isAdd", "true")
                        .add("planDtl2_id", planDtl2IdString)
                        .add("device_id", deviceId)
                        .add("serial", serial)
                        .add("device_name", deviceName)
                        .build();
                Request.Builder builder = new Request.Builder();
                Request request = builder.url(MyConstant.urlGetTripDetailPickup).post(requestBody).build();
                Response response = okHttpClient.newCall(request).execute();

                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("VAL-Tag-SupDA", "e ==> " + e + " Line " + e.getStackTrace()[0].getLineNumber());
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.d("VAL-Tag-SupDA", s);
            if (s.equals("notlogin")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SupplierDeliveryActivity.this, context.getResources().getText(R.string.notlogin), Toast.LENGTH_LONG).show();
                    }
                });
                onBackPressed();
            } else if (s.equals("duplicate")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SupplierDeliveryActivity.this, context.getResources().getText(R.string.duplicate), Toast.LENGTH_LONG).show();
                    }
                });
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                ComponentName componentName = intent.getComponent();
                Intent backToMainIntent = IntentCompat.makeRestartActivityTask(componentName);
                startActivity(backToMainIntent);
            } else {

                try {
                    JSONArray jsonArray = new JSONArray(s);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        suppCodeString = jsonObject.getString("supp_code");
                        suppNameString = jsonObject.getString("supp_name");
                        flagArrivalString = jsonObject.getString("flagArrivaled");
                        if (jsonObject.getString("total_percent_load").equals("null")) {
                            totalPercentageString = "0";
                        } else {
                            totalPercentageString = jsonObject.getString("total_percent_load");
                        }
                    }
                    Float aFloat = Float.parseFloat(totalPercentageString);

                    nameTextView.setText(suppNameString);
                    truckProgress.setProgress(Math.round(aFloat));

                    final String[] size = getSizeSpinner(Math.round(aFloat));
                    final BootstrapBrand[] color = getColorSpinner(Math.round(aFloat));

                    spinnerValueString = size[0];
                    BootstrapBrandValueString = color[0];
                    SpinnerAdaptor spinnerAdaptor = new SpinnerAdaptor(context, size, color);
                    percentageSpinner.setAdapter(spinnerAdaptor);

                    percentageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            spinnerValueString = size[i];
                            BootstrapBrandValueString = color[i];
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                    if (flagArrivalString.equals("Y")) {
                        arrivalButton.setVisibility(View.GONE);
                        savepicButton.setVisibility(View.VISIBLE);
                        percentageSpinner.setEnabled(true);
                        PalletEditText.setEnabled(true);
                        commentEditText.setEnabled(true);
                        confirmButton.setEnabled(true);


                    } else {
                        percentageSpinner.setEnabled(false);
                        PalletEditText.setEnabled(false);
                        commentEditText.setEnabled(false);
                        confirmButton.setEnabled(false);

                    }


                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }

        }
    }

    class SyncUpdateArrival extends AsyncTask<Void, Void, String> {
        Context context;
        String planDtl2String, usernameString, lat, lng;
        UtilityClass utilityClass;

        public SyncUpdateArrival(Context context, String planDtl2String, String usernameString, String lat, String lng) {
            this.context = context;
            this.planDtl2String = planDtl2String;
            this.usernameString = usernameString;
            this.lat = lat;
            this.lng = lng;
            utilityClass = new UtilityClass(context);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(Void... voids) {
            try {


                String deviceId = utilityClass.getDeviceID();
                String serial = utilityClass.getSerial();
                String deviceName = utilityClass.getDeviceName();
                OkHttpClient okHttpClient = new OkHttpClient();
                RequestBody requestBody = new FormBody.Builder()
                        .add("isAdd", "true")
                        .add("planDtl2_id", planDtl2IdString)
                        .add("Lat", lat)
                        .add("Lng", lng)
                        .add("drv_username", loginStrings[7])
                        .add("device_id", deviceId)
                        .add("serial", serial)
                        .add("device_name", deviceName)
                        .build();
                Request.Builder builder = new Request.Builder();
                Request request = builder.url(MyConstant.urlUpdateArrival).post(requestBody).build();
                Response response = okHttpClient.newCall(request).execute();
                return response.body().string();

            } catch (IOException e) {
                e.printStackTrace();
                return "";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("Tag", s);


            Log.d("Tag", "Bool ==> " + (s.equals("Success")));

            if (s.equals("Success")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, context.getResources().getString(R.string.save_success), Toast.LENGTH_LONG).show();
                        arrivalButton.setVisibility(View.GONE);
                        savepicButton.setVisibility(View.VISIBLE);

                        percentageSpinner.setEnabled(true);
                        PalletEditText.setEnabled(true);
                        commentEditText.setEnabled(true);
                        confirmButton.setEnabled(true);
                    }
                });
            } else if (s.equals("notlogin")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SupplierDeliveryActivity.this, context.getResources().getText(R.string.notlogin), Toast.LENGTH_LONG).show();
                    }
                });
                onBackPressed();
            } else if (s.equals("duplicate")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SupplierDeliveryActivity.this, context.getResources().getText(R.string.duplicate), Toast.LENGTH_LONG).show();
                    }
                });
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                ComponentName componentName = intent.getComponent();
                Intent backToMainIntent = IntentCompat.makeRestartActivityTask(componentName);
                startActivity(backToMainIntent);
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, context.getResources().getString(R.string.save_error), Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }

    class SyncUpdateDeparture extends AsyncTask<Void, Void, String> {
        Context context;
        String lat, lng, qtyString, percentString, remarkString;
        UtilityClass utilityClass;

        public SyncUpdateDeparture(Context context, String lat, String lng, String qtyString, String percentString, String remarkString) {
            this.context = context;
            this.lat = lat;
            this.lng = lng;
            this.qtyString = qtyString;
            this.percentString = percentString;
            this.remarkString = remarkString;
            utilityClass = new UtilityClass(context);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(Void... voids) {
            try {
                String deviceId = utilityClass.getDeviceID();
                String serial = utilityClass.getSerial();
                String deviceName = utilityClass.getDeviceName();
                OkHttpClient okHttpClient = new OkHttpClient();
                RequestBody requestBody = new FormBody.Builder()
                        .add("PlanDtl2_ID", planDtl2IdString)
                        .add("isAdd", "true")
                        .add("Driver_Name", loginStrings[7])
                        .add("pallet_qty", qtyString)
                        .add("percent_load", percentString)
                        .add("remarkSupp", remarkString)
                        .add("Lat", lat)
                        .add("Lng", lng)
                        .add("device_id", deviceId)
                        .add("serial", serial)
                        .add("device_name", deviceName)
                        .build();
                Request.Builder builder = new Request.Builder();
                Request request = builder.url(MyConstant.urlUpdateDeparture).post(requestBody).build();
                Response response = okHttpClient.newCall(request).execute();
                return response.body().string();

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(final String s) {
            super.onPostExecute(s);

            Log.d("Tag", "S ==> " + s);
            Log.d("Tag", "Bool ==> " + (s.equals("OK")));

            if (s.equals("OK")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, context.getResources().getString(R.string.save_success), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(SupplierDeliveryActivity.this, JobActivity.class);
                        intent.putExtra("Login", loginStrings);
                        intent.putExtra("planId", planIdString);
                        intent.putExtra("planDtlId", planDtlIdString);
                        intent.putExtra("planDate", dateString);
                        intent.putExtra("position", positionString);
                        startActivity(intent);
                        finish();
                    }
                });
            } else if (s.equals("notlogin")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SupplierDeliveryActivity.this, context.getResources().getText(R.string.notlogin), Toast.LENGTH_LONG).show();
                    }
                });
                onBackPressed();
            } else if (s.equals("duplicate")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SupplierDeliveryActivity.this, context.getResources().getText(R.string.duplicate), Toast.LENGTH_LONG).show();
                    }
                });
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                ComponentName componentName = intent.getComponent();
                Intent backToMainIntent = IntentCompat.makeRestartActivityTask(componentName);
                startActivity(backToMainIntent);
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, s, Toast.LENGTH_LONG).show();
                    }
                });
            }

        }
    }

    private Bitmap rotateBitmap(Bitmap src) {

        // create new matrix
        Matrix matrix = new Matrix();
        // setup rotation degree
        matrix.postRotate(90);
        Bitmap bmp = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        return bmp;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    pathSeal1String = seal1Uri.getPath();
                    try {
                        imgSeal1Bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(seal1Uri));
                        if (imgSeal1Bitmap.getHeight() < imgSeal1Bitmap.getWidth()) {
                            imgSeal1Bitmap = rotateBitmap(imgSeal1Bitmap);
                        }
                        leftImageView.setImageBitmap(imgSeal1Bitmap);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 2:
                if (resultCode == RESULT_OK) {
                    pathSeal2String = seal2Uri.getPath();
                    try{
                        imgSeal2Bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(seal2Uri));
                        if (imgSeal2Bitmap.getHeight() < imgSeal2Bitmap.getWidth()) {
                            imgSeal2Bitmap = rotateBitmap(imgSeal2Bitmap);
                        }
                        backImageView.setImageBitmap(imgSeal2Bitmap);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 3:
                if (resultCode == RESULT_OK) {
                    pathSeal3String = seal3Uri.getPath();
                    try{
                        imgSeal3Bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(seal3Uri));
                        if (imgSeal3Bitmap.getHeight() < imgSeal3Bitmap.getWidth()) {
                            imgSeal3Bitmap = rotateBitmap(imgSeal3Bitmap);
                        }
                        rightImageView.setImageBitmap(imgSeal3Bitmap);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 4:
                if (resultCode == RESULT_OK) {
                    pathPack4String = pack4Uri.getPath();
                    try{
                        imgPack4Bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(pack4Uri));
                        if (imgPack4Bitmap.getHeight() < imgPack4Bitmap.getWidth()) {
                            imgPack4Bitmap = rotateBitmap(imgPack4Bitmap);
                        }
                        fourthImageView.setImageBitmap(imgPack4Bitmap);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 5:
                if (resultCode == RESULT_OK) {
                    pathPack5String = pack5Uri.getPath();
                    try{
                        imgPack5Bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(pack5Uri));
                        if (imgPack5Bitmap.getHeight() < imgPack5Bitmap.getWidth()) {
                            imgPack5Bitmap = rotateBitmap(imgPack5Bitmap);
                        }
                        fifthImageView.setImageBitmap(imgPack5Bitmap);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 6:
                if (resultCode == RESULT_OK) {
                    pathPack6String = pack6Uri.getPath();
                    try{
                        imgPack6Bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(pack6Uri));
                        if (imgPack6Bitmap.getHeight() < imgPack6Bitmap.getWidth()) {
                            imgPack6Bitmap = rotateBitmap(imgPack6Bitmap);
                        }
                        sixthImageView.setImageBitmap(imgPack6Bitmap);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 7:
                if (resultCode == RESULT_OK) {
                    pathPack7String = pack7Uri.getPath();
                    try{
                        imgPack7Bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(pack7Uri));
                        if (imgPack7Bitmap.getHeight() < imgPack7Bitmap.getWidth()) {
                            imgPack7Bitmap = rotateBitmap(imgPack7Bitmap);
                        }
                        seventhImageView.setImageBitmap(imgPack7Bitmap);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    class SyncUploadPicture extends AsyncTask<Void, Void, String> {
        Context context;
        String mFileNameString, planDtl2_IdString;
        Bitmap bitmap;
        ProgressDialog progressDialog;

        public SyncUploadPicture(Context context, String mFileNameString, String planDtl2_IdString, Bitmap bitmap) {
            this.context = context;
            this.mFileNameString = mFileNameString;
            this.planDtl2_IdString = planDtl2_IdString;
            this.bitmap = bitmap;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            uploadImageUtils = new UploadImageUtils();
            final String result = uploadImageUtils.uploadFile(mFileNameString, urlUploadPicture, bitmap, planDtl2_IdString, "P");
            if (result.equals("NOK")) {
                return "NOK";
            } else {
                try {
                    OkHttpClient okHttpClient = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder()
                            .add("isAdd", "true")
                            .add("PlanDtl2_ID",planDtl2_IdString)
                            .add("File_Name", mFileNameString)
                            .add("File_Path", result)
                            .add("username", loginStrings[7])
                            .build();
                    Request.Builder builder = new Request.Builder();
                    Request request = builder.post(requestBody).url(urlSavePicture).build();
                    Response response = okHttpClient.newCall(request).execute();
                    return response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                    return "NOK";
                }
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("Tag", s);
            progressDialog.dismiss();

            if (s.equals("OK")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switch (mFileNameString) {
                            case "Seal1.png":
                                pathSeal1String = "";
                                break;
                            case "Seal2.png":
                                pathSeal2String = "";
                                break;
                            case "Seal3.png":
                                pathSeal3String = "";
                                break;
                            case "Package1.png":
                                pathPack4String = "";
                                break;
                            case "Package2.png":
                                pathPack5String = "";
                                break;
                            case "Package3.png":
                                pathPack6String = "";
                                break;
                            case "Package4.png":
                                pathPack7String = "";
                                break;
                        }
                        Toast.makeText(context, R.string.save_pic_success, Toast.LENGTH_LONG).show();
                    }
                });
            }else{
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, R.string.save_pic_unsuccess, Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }

    @OnClick({R.id.btn_savepic, R.id.btn_arrival, R.id.btn_confirm, R.id.img_left, R.id.img_back, R.id.img_right, R.id.img_4, R.id.img_5, R.id.img_6, R.id.img_7})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_savepic:
                if (!Objects.equals(pathSeal1String, "")) {
                    SyncUploadPicture syncUploadPicture = new SyncUploadPicture(SupplierDeliveryActivity.this, "Seal1.png", planDtl2IdString, imgSeal1Bitmap);
                    syncUploadPicture.execute();
                }
                if (!Objects.equals(pathSeal2String, "")) {
                    SyncUploadPicture syncUploadPicture = new SyncUploadPicture(SupplierDeliveryActivity.this, "Seal2.png", planDtl2IdString, imgSeal2Bitmap);
                    syncUploadPicture.execute();

                }
                if (!Objects.equals(pathSeal3String, "")) {
                    SyncUploadPicture syncUploadPicture = new SyncUploadPicture(SupplierDeliveryActivity.this, "Seal3.png", planDtl2IdString, imgSeal3Bitmap);
                    syncUploadPicture.execute();

                }
                if (!Objects.equals(pathPack4String, "")) {
                    SyncUploadPicture syncUploadPicture = new SyncUploadPicture(SupplierDeliveryActivity.this, "Package1.png", planDtl2IdString, imgPack4Bitmap);
                    syncUploadPicture.execute();

                }
                if (!Objects.equals(pathPack5String, "")) {
                    SyncUploadPicture syncUploadPicture = new SyncUploadPicture(SupplierDeliveryActivity.this, "Package2.png", planDtl2IdString, imgPack5Bitmap);
                    syncUploadPicture.execute();

                }
                if (!Objects.equals(pathPack6String, "")) {
                    SyncUploadPicture syncUploadPicture = new SyncUploadPicture(SupplierDeliveryActivity.this, "Package3.png", planDtl2IdString, imgPack6Bitmap);
                    syncUploadPicture.execute();

                }
                if (!Objects.equals(pathPack7String, "")) {
                    SyncUploadPicture syncUploadPicture = new SyncUploadPicture(SupplierDeliveryActivity.this, "Package4.png", planDtl2IdString, imgPack7Bitmap);
                    syncUploadPicture.execute();

                }
                break;
            case R.id.img_left:
                if (!imgSeal1ABoolean) {
                    File originalFile1 = new File(Environment.getExternalStorageDirectory() + "/DCIM/", "Seal1.png");
                    Intent cameraIntent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    seal1Uri = Uri.fromFile(originalFile1);
                    cameraIntent1.putExtra(MediaStore.EXTRA_OUTPUT, seal1Uri);
                    startActivityForResult(cameraIntent1, 1);
                }
                break;
            case R.id.img_back:
                if (!imgSeal2ABoolean) {
                    File originalFile1 = new File(Environment.getExternalStorageDirectory() + "/DCIM/", "Seal2.png");
                    Intent cameraIntent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    seal2Uri = Uri.fromFile(originalFile1);
                    cameraIntent1.putExtra(MediaStore.EXTRA_OUTPUT, seal2Uri);
                    startActivityForResult(cameraIntent1, 2);
                }
                break;
            case R.id.img_right:
                if (!imgSeal3ABoolean) {
                    File originalFile1 = new File(Environment.getExternalStorageDirectory() + "/DCIM/", "Seal3.png");
                    Intent cameraIntent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    seal3Uri = Uri.fromFile(originalFile1);
                    cameraIntent1.putExtra(MediaStore.EXTRA_OUTPUT, seal3Uri);
                    startActivityForResult(cameraIntent1, 3);
                }
                break;
            case R.id.img_4:
                if (!imgPack4ABoolean) {
                    File originalFile1 = new File(Environment.getExternalStorageDirectory() + "/DCIM/", "Package1.png");
                    Intent cameraIntent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    pack4Uri = Uri.fromFile(originalFile1);
                    cameraIntent1.putExtra(MediaStore.EXTRA_OUTPUT, pack4Uri);
                    startActivityForResult(cameraIntent1, 4);
                }
                break;
            case R.id.img_5:
                if (!imgPack5ABoolean) {
                    File originalFile1 = new File(Environment.getExternalStorageDirectory() + "/DCIM/", "Package2.png");
                    Intent cameraIntent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    pack5Uri = Uri.fromFile(originalFile1);
                    cameraIntent1.putExtra(MediaStore.EXTRA_OUTPUT, pack5Uri);
                    startActivityForResult(cameraIntent1, 5);
                }
                break;
            case R.id.img_6:
                if (!imgPack6ABoolean) {
                    File originalFile1 = new File(Environment.getExternalStorageDirectory() + "/DCIM/", "Package3.png");
                    Intent cameraIntent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    pack6Uri = Uri.fromFile(originalFile1);
                    cameraIntent1.putExtra(MediaStore.EXTRA_OUTPUT, pack6Uri);
                    startActivityForResult(cameraIntent1, 6);
                }
                break;
            case R.id.img_7:
                if (!imgPack7ABoolean) {
                    File originalFile1 = new File(Environment.getExternalStorageDirectory() + "/DCIM/", "Package4.png");
                    Intent cameraIntent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    pack7Uri = Uri.fromFile(originalFile1);
                    cameraIntent1.putExtra(MediaStore.EXTRA_OUTPUT, pack7Uri);
                    startActivityForResult(cameraIntent1, 7);
                }
                break;
            case R.id.btn_arrival:
                UtilityClass utilityClass = new UtilityClass(SupplierDeliveryActivity.this);
                utilityClass.setLatLong(0);
                final String latitude = utilityClass.getLatString();
                final String longitude = utilityClass.getLongString();
                if (loginStrings[4].equals("Y")) {
                    if (utilityClass.setLatLong(0)) {
                        // if (Double.parseDouble(utilityClass.getDistanceMeter(suppLatString, suppLonString)) >= Double.parseDouble(suppRadiusString)) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                        dialog.setTitle(R.string.alert);
                        dialog.setIcon(R.drawable.warning);
                        dialog.setCancelable(true);
                        dialog.setMessage(R.string.arrivalDialog);
                        dialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (!(latitude == null)) {
                                    SyncUpdateArrival syncUpdateArrival = new SyncUpdateArrival(SupplierDeliveryActivity.this, planDtl2IdString, loginStrings[0], latitude, longitude);
                                    syncUpdateArrival.execute();
                                } else {
                                    Toast.makeText(SupplierDeliveryActivity.this, getResources().getString(R.string.save_error), Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                        dialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        dialog.show();
                    }
                }

                break;
            case R.id.btn_confirm:
                utilityClass = new UtilityClass(SupplierDeliveryActivity.this);
                utilityClass.setLatLong(0);
                final String latitude1 = utilityClass.getLatString();
                final String longitude1 = utilityClass.getLongString();
                Log.d("Tag", "Spinner ==> " + spinnerValueString);

                if (loginStrings[5].equals("Y")) {
                    if (utilityClass.setLatLong(0)) {
                        // if(Double.parseDouble(utilityClass.getDistanceMeter(suppLatString,suppLonString)) >= Double.parseDouble(suppRadiusString)) {

                        AlertDialog.Builder dialog1 = new AlertDialog.Builder(this);
                        dialog1.setTitle(R.string.alert);
                        dialog1.setIcon(R.drawable.warning);
                        dialog1.setCancelable(true);
                        dialog1.setMessage(R.string.departDialog);

                        dialog1.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                if (!(latitude1 == null)) {
                                    SyncUpdateDeparture syncUpdateDeparture = new SyncUpdateDeparture(SupplierDeliveryActivity.this, latitude1, longitude1, PalletEditText.getText().toString(), spinnerValueString, commentEditText.getText().toString());
                                    syncUpdateDeparture.execute();

                                } else {
                                    Toast.makeText(SupplierDeliveryActivity.this, getResources().getString(R.string.save_error), Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                        dialog1.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        dialog1.show();
                        break;
                    }
                }
        }
    }
}