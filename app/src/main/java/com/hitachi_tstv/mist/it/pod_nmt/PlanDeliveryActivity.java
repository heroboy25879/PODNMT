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

public class PlanDeliveryActivity extends AppCompatActivity {

    @BindView(R.id.progess_truck)
    BootstrapProgressBar progessTruck;
    @BindView(R.id.spnSDAPercentage)
    Spinner percentageSpinner;
    @BindView(R.id.linPDABottom)
    LinearLayout linPDABottom;
    @BindView(R.id.txt_name)
    TextView factoryTextviewPD;
    @BindView(R.id.btn_arrival)
    Button btnArrivalPD;
    @BindView(R.id.btn_confirm)
    Button btnDeparturePD;
    @BindView(R.id.img_l)
    ImageView leftImageView;
    @BindView(R.id.img_b)
    ImageView backImageView;
    @BindView(R.id.img_r)
    ImageView rightImageView;
    @BindView(R.id.img_4)
    ImageView fourthImageView;
    @BindView(R.id.img_5)
    ImageView fifthImageView;
    @BindView(R.id.img_6)
    ImageView sixthImageView;
    @BindView(R.id.img_7)
    ImageView seventhImageView;
    @BindView(R.id.btn_savepic)
    Button savepicButton;

    private String[] planDtl2IdStrings, amountStrings, loginStrings, datePlanStrings;
    private String pathSeal1String, pathSeal2String, pathSeal3String, pathPack4String, pathPack5String, pathPack6String, pathPack7String;
    private String planDtlIdString, planDtl2IdString, planNameString, positionString, planIdString,
            timeArrivalString, transporttypeString, suppLatString, suppLonString, dateString, suppRadiusString, totalPercentageString, spinnerValueString, flagArrivalString;
    Uri seal1Uri, seal2Uri, seal3Uri, pack4Uri, pack5Uri, pack6Uri, pack7Uri;
    Bitmap imgSeal1Bitmap, imgSeal2Bitmap, imgSeal3Bitmap, imgPack4Bitmap, imgPack5Bitmap, imgPack6Bitmap, imgPack7Bitmap;
    Boolean imgSeal1ABoolean, imgSeal2ABoolean, imgSeal3ABoolean, imgPack4ABoolean, imgPack5ABoolean, imgPack6ABoolean, imgPack7ABoolean;
    Boolean doubleBackPressABoolean = false;
    UploadImageUtils uploadImageUtils;

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackPressABoolean) {
            Intent intent1 = new Intent(PlanDeliveryActivity.this, JobActivity.class);
            intent1.putExtra("Login", loginStrings);
            intent1.putExtra("planDtlId", planDtlIdString);
            intent1.putExtra("position", positionString);
            intent1.putExtra("planDate", dateString);
            intent1.putExtra("planId", planIdString);
            startActivity(intent1);
            finish();
        }

        doubleBackPressABoolean = true;
        Toast.makeText(this, getResources().getText(R.string.check_back), Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackPressABoolean = false;
            }
        }, 2000);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                Intent intent1 = new Intent(PlanDeliveryActivity.this, PlanDeliveryActivity.class);
                intent1.putExtra("planDtl2_id", planDtl2IdString);
                intent1.putExtra("Login", loginStrings);
                intent1.putExtra("planDtlId", planDtlIdString);
                intent1.putExtra("position", positionString);
                intent1.putExtra("Date", dateString);
                intent1.putExtra("planId", planIdString);
                intent1.putExtra("stationName", planNameString);
                intent1.putExtra("transporttype", transporttypeString);
                startActivity(intent1);
                finish();
                break;
        }


        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_delivery);
        ButterKnife.bind(this);


        setData();

        factoryTextviewPD.setText(planNameString);
        SynDeliveryData synDeliveryData = new SynDeliveryData(PlanDeliveryActivity.this);
        synDeliveryData.execute();

    }

    private void setData() {
        //get Intent data
        loginStrings = getIntent().getStringArrayExtra("Login");
        dateString = getIntent().getStringExtra("Date");
        planIdString = getIntent().getStringExtra("planId");
        planDtl2IdString = getIntent().getStringExtra("planDtl2_id");
        planDtlIdString = getIntent().getStringExtra("planDtlId");
        planNameString = getIntent().getStringExtra("stationName");
        transporttypeString = getIntent().getStringExtra("transporttype");
        positionString = getIntent().getStringExtra("position");
        timeArrivalString = getIntent().getStringExtra("timeArrival");


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

    String[] getSizeSpinner(int size) {
        String[] sizeStrings;
        switch (size) {
            case 100:
                sizeStrings = new String[11];
                sizeStrings[0] = "100";
                sizeStrings[1] = "90";
                sizeStrings[2] = "80";
                sizeStrings[3] = "70";
                sizeStrings[4] = "60";
                sizeStrings[5] = "50";
                sizeStrings[6] = "40";
                sizeStrings[7] = "30";
                sizeStrings[8] = "20";
                sizeStrings[9] = "10";
                sizeStrings[10] = "0";
                break;
            case 90:
                sizeStrings = new String[10];
                sizeStrings[0] = "90";
                sizeStrings[1] = "80";
                sizeStrings[2] = "70";
                sizeStrings[3] = "60";
                sizeStrings[4] = "50";
                sizeStrings[5] = "40";
                sizeStrings[6] = "30";
                sizeStrings[7] = "20";
                sizeStrings[8] = "10";
                sizeStrings[9] = "0";
                break;
            case 80:
                sizeStrings = new String[9];
                sizeStrings[0] = "80";
                sizeStrings[1] = "70";
                sizeStrings[2] = "60";
                sizeStrings[3] = "50";
                sizeStrings[4] = "40";
                sizeStrings[5] = "30";
                sizeStrings[6] = "20";
                sizeStrings[7] = "10";
                sizeStrings[8] = "0";
                break;
            case 70:
                sizeStrings = new String[8];
                sizeStrings[0] = "70";
                sizeStrings[1] = "60";
                sizeStrings[2] = "50";
                sizeStrings[3] = "40";
                sizeStrings[4] = "30";
                sizeStrings[5] = "20";
                sizeStrings[6] = "10";
                sizeStrings[7] = "0";
                break;
            case 60:
                sizeStrings = new String[7];
                sizeStrings[0] = "60";
                sizeStrings[1] = "50";
                sizeStrings[2] = "40";
                sizeStrings[3] = "30";
                sizeStrings[4] = "20";
                sizeStrings[5] = "10";
                sizeStrings[6] = "0";
                break;
            case 50:
                sizeStrings = new String[6];
                sizeStrings[0] = "50";
                sizeStrings[1] = "40";
                sizeStrings[2] = "30";
                sizeStrings[3] = "20";
                sizeStrings[4] = "10";
                sizeStrings[5] = "0";
                break;
            case 40:
                sizeStrings = new String[5];
                sizeStrings[0] = "40";
                sizeStrings[1] = "30";
                sizeStrings[2] = "20";
                sizeStrings[3] = "10";
                sizeStrings[4] = "0";
                break;
            case 30:
                sizeStrings = new String[4];
                sizeStrings[0] = "30";
                sizeStrings[1] = "20";
                sizeStrings[2] = "10";
                sizeStrings[3] = "0";
                break;
            case 20:
                sizeStrings = new String[3];
                sizeStrings[0] = "20";
                sizeStrings[1] = "10";
                sizeStrings[2] = "0";
                break;
            case 10:
                sizeStrings = new String[2];
                sizeStrings[0] = "10";
                sizeStrings[1] = "0";
                break;
            case 0:
                sizeStrings = new String[1];
                sizeStrings[0] = "0";
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
                colorStrings = new BootstrapBrand[11];

                colorStrings[0] = DefaultBootstrapBrand.DANGER;
                colorStrings[1] = DefaultBootstrapBrand.DANGER;
                colorStrings[2] = DefaultBootstrapBrand.DANGER;
                colorStrings[3] = DefaultBootstrapBrand.WARNING;
                colorStrings[4] = DefaultBootstrapBrand.WARNING;
                colorStrings[5] = DefaultBootstrapBrand.WARNING;
                colorStrings[6] = DefaultBootstrapBrand.SUCCESS;
                colorStrings[7] = DefaultBootstrapBrand.SUCCESS;
                colorStrings[8] = DefaultBootstrapBrand.SUCCESS;
                colorStrings[9] = DefaultBootstrapBrand.SUCCESS;
                colorStrings[10] = DefaultBootstrapBrand.SUCCESS;
                break;
            case 90:
                colorStrings = new BootstrapBrand[10];
                colorStrings[0] = DefaultBootstrapBrand.DANGER;
                colorStrings[1] = DefaultBootstrapBrand.DANGER;
                colorStrings[2] = DefaultBootstrapBrand.DANGER;
                colorStrings[3] = DefaultBootstrapBrand.WARNING;
                colorStrings[4] = DefaultBootstrapBrand.WARNING;
                colorStrings[5] = DefaultBootstrapBrand.WARNING;
                colorStrings[6] = DefaultBootstrapBrand.SUCCESS;
                colorStrings[7] = DefaultBootstrapBrand.SUCCESS;
                colorStrings[8] = DefaultBootstrapBrand.SUCCESS;
                colorStrings[9] = DefaultBootstrapBrand.SUCCESS;
                break;
            case 80:
                colorStrings = new BootstrapBrand[9];
                colorStrings[0] = DefaultBootstrapBrand.DANGER;
                colorStrings[1] = DefaultBootstrapBrand.DANGER;
                colorStrings[2] = DefaultBootstrapBrand.DANGER;
                colorStrings[3] = DefaultBootstrapBrand.WARNING;
                colorStrings[4] = DefaultBootstrapBrand.WARNING;
                colorStrings[5] = DefaultBootstrapBrand.WARNING;
                colorStrings[6] = DefaultBootstrapBrand.SUCCESS;
                colorStrings[7] = DefaultBootstrapBrand.SUCCESS;
                colorStrings[8] = DefaultBootstrapBrand.SUCCESS;


                break;
            case 70:
                colorStrings = new BootstrapBrand[8];
                colorStrings[0] = DefaultBootstrapBrand.WARNING;
                colorStrings[1] = DefaultBootstrapBrand.WARNING;
                colorStrings[2] = DefaultBootstrapBrand.WARNING;
                colorStrings[3] = DefaultBootstrapBrand.SUCCESS;
                colorStrings[4] = DefaultBootstrapBrand.SUCCESS;
                colorStrings[5] = DefaultBootstrapBrand.SUCCESS;
                colorStrings[6] = DefaultBootstrapBrand.SUCCESS;
                colorStrings[7] = DefaultBootstrapBrand.SUCCESS;
                break;
            case 60:
                colorStrings = new BootstrapBrand[7];
                colorStrings[0] = DefaultBootstrapBrand.WARNING;
                colorStrings[1] = DefaultBootstrapBrand.WARNING;
                colorStrings[2] = DefaultBootstrapBrand.SUCCESS;
                colorStrings[3] = DefaultBootstrapBrand.SUCCESS;
                colorStrings[4] = DefaultBootstrapBrand.SUCCESS;
                colorStrings[5] = DefaultBootstrapBrand.SUCCESS;
                colorStrings[6] = DefaultBootstrapBrand.SUCCESS;
                break;
            case 50:
                colorStrings = new BootstrapBrand[6];

                colorStrings[0] = DefaultBootstrapBrand.WARNING;
                colorStrings[1] = DefaultBootstrapBrand.SUCCESS;
                colorStrings[2] = DefaultBootstrapBrand.SUCCESS;
                colorStrings[3] = DefaultBootstrapBrand.SUCCESS;
                colorStrings[4] = DefaultBootstrapBrand.SUCCESS;
                colorStrings[5] = DefaultBootstrapBrand.SUCCESS;
                break;
            case 40:
                colorStrings = new BootstrapBrand[5];
                colorStrings[0] = DefaultBootstrapBrand.SUCCESS;
                colorStrings[1] = DefaultBootstrapBrand.SUCCESS;
                colorStrings[2] = DefaultBootstrapBrand.SUCCESS;
                colorStrings[3] = DefaultBootstrapBrand.SUCCESS;
                colorStrings[4] = DefaultBootstrapBrand.SUCCESS;

                break;
            case 30:
                colorStrings = new BootstrapBrand[4];
                colorStrings[0] = DefaultBootstrapBrand.SUCCESS;
                colorStrings[1] = DefaultBootstrapBrand.SUCCESS;
                colorStrings[2] = DefaultBootstrapBrand.SUCCESS;
                colorStrings[3] = DefaultBootstrapBrand.SUCCESS;

                break;
            case 20:
                colorStrings = new BootstrapBrand[3];

                colorStrings[0] = DefaultBootstrapBrand.SUCCESS;
                colorStrings[1] = DefaultBootstrapBrand.SUCCESS;
                colorStrings[2] = DefaultBootstrapBrand.SUCCESS;
                break;
            case 10:
                colorStrings = new BootstrapBrand[2];
                colorStrings[0] = DefaultBootstrapBrand.SUCCESS;
                colorStrings[1] = DefaultBootstrapBrand.SUCCESS;
                break;
            case 0:
                colorStrings = new BootstrapBrand[1];
                colorStrings[0] = DefaultBootstrapBrand.SUCCESS;
                break;
            default:
                colorStrings = null;
                break;
        }

        return colorStrings;
    }

    private class SynDeliveryData extends AsyncTask<String, Void, String> {
        private Context context;
        UtilityClass utilityClass;

        public SynDeliveryData(Context context) {
            this.context = context;
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
                OkHttpClient okHttpClient = new OkHttpClient();
                RequestBody requestBody = new FormBody.Builder()
                        .add("isAdd", "true")
                        .add("planDtl2_id", planDtl2IdString)
                        .add("device_id", deviceId)
                        .add("serial", serial)
                        .add("device_name", deviceName)
                        .build();

                Request.Builder builder = new Request.Builder();
                Request request = builder.url(MyConstant.urlGetTripDetailDelivery).post(requestBody).build();
                Response response = okHttpClient.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("Tag", "IoException..." + e.getStackTrace()[0].getLineNumber());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("Tag", "OnpostExecute:::--->" + s);

            //JSONArray jsonArray = new JSONArray(s);
            if (s.equals("notlogin")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(PlanDeliveryActivity.this, context.getResources().getText(R.string.notlogin), Toast.LENGTH_LONG).show();
                    }
                });
                onBackPressed();
            } else if (s.equals("duplicate")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(PlanDeliveryActivity.this, context.getResources().getText(R.string.duplicate), Toast.LENGTH_LONG).show();
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
                        suppLatString = jsonObject.getString("supp_lat");
                        suppLonString = jsonObject.getString("supp_lon");
                        suppRadiusString = jsonObject.getString("supp_radius");

                        flagArrivalString = jsonObject.getString("flagArrivaled");

                        Log.d("Tag", "suppLatString::::" + suppLatString);
                        Log.d("Tag", "suppLonString::::" + suppLonString);
                        Log.d("Tag", "suppRadiusString::::" + suppRadiusString);


                        Log.d("Tag", "A " + jsonObject.getString("total_percent_load").equals("null"));
                        Log.d("Tag", "B " + jsonObject.getString("total_percent_load"));

                        if (!(jsonObject.getString("total_percent_load").equals("null"))) {
                            totalPercentageString = jsonObject.getString("total_percent_load");
                        } else {
                            totalPercentageString = "100";
                        }
                        Float aFloat = Float.parseFloat(totalPercentageString);

                        progessTruck.setProgress(Math.round(aFloat));

                        final String[] size = getSizeSpinner(Math.round(aFloat));
                        final BootstrapBrand[] color = getColorSpinner(Math.round(aFloat));
                        spinnerValueString = size[0];


                        SpinnerAdaptor spinnerAdaptor = new SpinnerAdaptor(context, size, color);
                        percentageSpinner.setAdapter(spinnerAdaptor);
                        percentageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                spinnerValueString = size[i];
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });


                        if (flagArrivalString.equals("Y")) {
                            btnArrivalPD.setVisibility(View.GONE);
                            savepicButton.setVisibility(View.VISIBLE);
                            percentageSpinner.setEnabled(true);
                            btnDeparturePD.setEnabled(true);


                        } else {
                            btnArrivalPD.setVisibility(View.VISIBLE);
                            savepicButton.setVisibility(View.GONE);
                            btnDeparturePD.setEnabled(false);
                            percentageSpinner.setEnabled(false);

                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

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
                            .add("PlanDtl2_ID", planDtl2_IdString)
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
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, R.string.save_pic_unsuccess, Toast.LENGTH_LONG).show();
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


    @OnClick({R.id.btn_arrival, R.id.btn_confirm,R.id.btn_savepic,R.id.img_l,R.id.img_b,R.id.img_r,R.id.img_4,R.id.img_5,R.id.img_6,R.id.img_7})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_savepic:
                if (!Objects.equals(pathSeal1String, "")) {
                    SyncUploadPicture syncUploadPicture = new SyncUploadPicture(PlanDeliveryActivity.this, "Seal1.png", planDtl2IdString, imgSeal1Bitmap);
                    syncUploadPicture.execute();
                }
                if (!Objects.equals(pathSeal2String, "")) {
                    SyncUploadPicture syncUploadPicture = new SyncUploadPicture(PlanDeliveryActivity.this, "Seal2.png", planDtl2IdString, imgSeal2Bitmap);
                    syncUploadPicture.execute();
                }
                if (!Objects.equals(pathSeal3String, "")) {
                    SyncUploadPicture syncUploadPicture = new SyncUploadPicture(PlanDeliveryActivity.this, "Seal3.png", planDtl2IdString, imgSeal3Bitmap);
                    syncUploadPicture.execute();
                }
                if (!Objects.equals(pathPack4String, "")) {
                    SyncUploadPicture syncUploadPicture = new SyncUploadPicture(PlanDeliveryActivity.this, "Package1.png", planDtl2IdString, imgPack4Bitmap);
                    syncUploadPicture.execute();
                }
                if (!Objects.equals(pathPack5String, "")) {
                    SyncUploadPicture syncUploadPicture = new SyncUploadPicture(PlanDeliveryActivity.this, "Package2.png", planDtl2IdString, imgPack5Bitmap);
                    syncUploadPicture.execute();
                }
                if (!Objects.equals(pathPack6String, "")) {
                    SyncUploadPicture syncUploadPicture = new SyncUploadPicture(PlanDeliveryActivity.this, "Package3.png", planDtl2IdString, imgPack6Bitmap);
                    syncUploadPicture.execute();
                }
                if (!Objects.equals(pathPack7String, "")) {
                    SyncUploadPicture syncUploadPicture = new SyncUploadPicture(PlanDeliveryActivity.this, "Package4.png", planDtl2IdString, imgPack7Bitmap);
                    syncUploadPicture.execute();
                }
                break;
            case R.id.img_l:
                if (!imgSeal1ABoolean) {
                    File originalFile1 = new File(Environment.getExternalStorageDirectory() + "/DCIM/", "Seal1.png");
                    Intent cameraIntent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    seal1Uri = Uri.fromFile(originalFile1);
                    cameraIntent1.putExtra(MediaStore.EXTRA_OUTPUT, seal1Uri);
                    startActivityForResult(cameraIntent1, 1);
                }
                break;
            case R.id.img_b:
                if (!imgSeal2ABoolean) {
                    File originalFile1 = new File(Environment.getExternalStorageDirectory() + "/DCIM/", "Seal2.png");
                    Intent cameraIntent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    seal2Uri = Uri.fromFile(originalFile1);
                    cameraIntent1.putExtra(MediaStore.EXTRA_OUTPUT, seal2Uri);
                    startActivityForResult(cameraIntent1, 2);
                }
                break;
            case R.id.img_r:
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

                final UtilityClass utilityClass = new UtilityClass(PlanDeliveryActivity.this);
                if (loginStrings[4].equals("Y")) {
                    if (utilityClass.setLatLong(0)) {

                        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                        dialog.setTitle(R.string.alert);
                        dialog.setIcon(R.drawable.warning);
                        dialog.setCancelable(true);
                        dialog.setMessage(R.string.arrivalDialog);

                        AlertDialog.Builder builder = dialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                SynUpdateArrival synUpdateArrival = new SynUpdateArrival(utilityClass.getLatString(), utilityClass.getLongString(), utilityClass.getTimeString(), PlanDeliveryActivity.this);
                                synUpdateArrival.execute();
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

                final UtilityClass utilityClass1 = new UtilityClass(PlanDeliveryActivity.this);
                if (loginStrings[5].equals("Y")) {
                    if (utilityClass1.setLatLong(0)) {
                        AlertDialog.Builder dialog1 = new AlertDialog.Builder(this);
                        dialog1.setTitle(R.string.alert);
                        dialog1.setIcon(R.drawable.warning);
                        dialog1.setCancelable(true);
                        dialog1.setMessage(R.string.departDialog);

                        dialog1.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                if (utilityClass1.setLatLong(0)) {
                                    SynUpdateDeparture synUpdateDeparture = new SynUpdateDeparture(utilityClass1.getLatString(), utilityClass1.getLongString(), utilityClass1.getTimeString(), spinnerValueString, PlanDeliveryActivity.this);
                                    synUpdateDeparture.execute();
                                } else {
                                    Toast.makeText(getBaseContext(), getResources().getText(R.string.err_gps1), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        dialog1.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        dialog1.show();

                    }
                }

                break;
        }


    }

    private class SynUpdateArrival extends AsyncTask<Void, Void, String> {
        String latString, longString, timeString;
        Context context;
        UtilityClass utilityClass;

        public SynUpdateArrival(String latString, String longString, String timeString, Context context) {
            this.latString = latString;
            this.longString = longString;
            this.timeString = timeString;
            this.context = context;
            utilityClass = new UtilityClass(context);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(Void... voids) {
            try {
                Log.d("Tag", "Lat/Long : Plan ==> " + planDtl2IdString + "," + loginStrings[7] + "," + latString + "," + longString);

                String deviceId = utilityClass.getDeviceID();
                String serial = utilityClass.getSerial();
                String deviceName = utilityClass.getDeviceName();
                OkHttpClient okHttpClient = new OkHttpClient();
                RequestBody requestBody = new FormBody.Builder()
                        .add("isAdd", "true")
                        .add("drv_username", loginStrings[7])
                        .add("planDtl2_id", planDtl2IdString)
                        .add("Lat", latString)
                        .add("Lng", longString)
                        .add("device_id", deviceId)
                        .add("serial", serial)
                        .add("device_name", deviceName)
                        .build();

                Request.Builder builder = new Request.Builder();
                Request request = builder.url(MyConstant.urlUpdateArrival).post(requestBody).build();
                Response response = okHttpClient.newCall(request).execute();
                return response.body().string();

            } catch (Exception e) {
                Log.d("Tag", "e doInBack ==>" + e.toString() + "line::" + e.getStackTrace()[0].getLineNumber());
                return "";
            }
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("Tag", "onPostExecute:::-----> " + s);
            if (s.equals("Success")) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getBaseContext(), R.string.save_success, Toast.LENGTH_LONG).show();

                        btnArrivalPD.setVisibility(View.GONE);
                        savepicButton.setVisibility(View.VISIBLE);
                        percentageSpinner.setEnabled(true);
                        btnDeparturePD.setEnabled(true);
                    }
                });
            } else if (s.equals("notlogin")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(PlanDeliveryActivity.this, context.getResources().getText(R.string.notlogin), Toast.LENGTH_LONG).show();
                    }
                });
                onBackPressed();
            } else if (s.equals("duplicate")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(PlanDeliveryActivity.this, context.getResources().getText(R.string.duplicate), Toast.LENGTH_LONG).show();
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
                        Toast.makeText(getBaseContext(), getResources().getText(R.string.save_error), Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }

    private class SynUpdateDeparture extends AsyncTask<Void, Void, String> {
        String latString, longString, timeString, percentString;
        Context context;
        UtilityClass utilityClass;


        public SynUpdateDeparture(String latString, String longString, String timeString, String percentString, Context context) {
            this.latString = latString;
            this.longString = longString;
            this.timeString = timeString;
            this.percentString = percentString;
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
                OkHttpClient okHttpClient = new OkHttpClient();
                RequestBody requestBody = new FormBody.Builder()
                        .add("isAdd", "true")
                        .add("Driver_Name", loginStrings[7])
                        .add("PlanDtl2_ID", planDtl2IdString)
                        .add("percent_load", percentString)
                        .add("Lat", latString)
                        .add("Lng", longString)
                        .add("device_id", deviceId)
                        .add("serial", serial)
                        .add("device_name", deviceName)
                        .build();

                Request.Builder builder = new Request.Builder();
                Request request = builder.url(MyConstant.urlUpdateDeparture).post(requestBody).build();
                Response response = okHttpClient.newCall(request).execute();
                return response.body().string();

            } catch (Exception e) {
                Log.d("Tag", "e doInBack ==>" + e.toString() + "line::" + e.getStackTrace()[0].getLineNumber());
                return "";
            }

        }

        @Override
        protected void onPostExecute(final String s) {
            super.onPostExecute(s);
            Log.d("Tag", "onPostExecute:::---->Departure:::: " + s);
            if (s.equals("OK")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getBaseContext(), R.string.save_success, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(PlanDeliveryActivity.this, JobActivity.class);
                        intent.putExtra("Login", loginStrings);
                        intent.putExtra("planDtlId", planDtlIdString);
                        intent.putExtra("position", positionString);
                        intent.putExtra("planDate", dateString);
                        intent.putExtra("planId", planIdString);
                        startActivity(intent);
                        finish();
                    }
                });
            } else if (s.equals("notlogin")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(PlanDeliveryActivity.this, context.getResources().getText(R.string.notlogin), Toast.LENGTH_LONG).show();
                    }
                });
                onBackPressed();
            } else if (s.equals("duplicate")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(PlanDeliveryActivity.this, context.getResources().getText(R.string.duplicate), Toast.LENGTH_LONG).show();
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
                        Toast.makeText(getBaseContext(), s, Toast.LENGTH_LONG).show();
                    }
                });
            }

        }
    }


}
