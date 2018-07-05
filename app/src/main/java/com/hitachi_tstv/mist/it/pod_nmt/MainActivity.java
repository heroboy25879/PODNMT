package com.hitachi_tstv.mist.it.pod_nmt;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.fabric.sdk.android.Fabric;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.imgMALogo)
    ImageView logoImageView;
    @BindView(R.id.edtMAUsername)
    EditText usernameEditText;
    @BindView(R.id.edtMAPassword)
    EditText passwordEditText;
    @BindView(R.id.btnMALogin)
    Button loginButton;

    String[] loginStrings;
    boolean doubleBackToExitPressedOnce = false;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //granted
                } else {
                    //not granted
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    //What is permission be request
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void requestForSpecificPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET,Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.BLUETOOTH,Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_CONTACTS}, 101);

    }

    //Check the permission is already have
    private boolean checkIfAlreadyhavePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);




        if (!checkIfAlreadyhavePermission()) {
            requestForSpecificPermission();
        }

        loginStrings = new String[MyConstant.getColumnLoginSize];

      //  usernameEditText.setText("71-8889");
       // usernameEditText.setText("72-4965");
       // passwordEditText.setText("1234");
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, getResources().getText(R.string.check_exit), Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    class SyncGetUserLogin extends AsyncTask<Void, Void, String> {
        Context context;
        String usernameString, passwordString;
        UtilityClass utilityClass;

        public SyncGetUserLogin(Context context, String usernameString, String passwordString) {
            this.context = context;
            this.usernameString = usernameString;
            this.passwordString = passwordString;
            utilityClass = new UtilityClass(context);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(Void... voids) {
            try {
                String deviceId = utilityClass.getDeviceID();
                String serial = utilityClass.getSerial();
                String deviceName = utilityClass.getDeviceName();
                Log.d("Tag", deviceId + "  " + serial + "device name " + deviceName);
                OkHttpClient okHttpClient = new OkHttpClient();
                RequestBody requestBody = new FormBody.Builder()
                        .add("isAdd", "true")
                        .add("username", usernameString)
                        .add("password", passwordString)
                        .add("device_id", deviceId)
                        .add("serial", serial)
                        .add("device_name",deviceName)
                        .build();
                Request.Builder builder = new Request.Builder();
                Request request = builder.post(requestBody).url(MyConstant.urlGetUserLogin).build();
                Response response = okHttpClient.newCall(request).execute();

                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("VAL-Tag-Main", "e ==> " + e + " Line " + e.getStackTrace()[0].getLineNumber());
                return "";
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.d("VAL-Tag-Main", "S ==> " + s);

            if (!(s == null)) {
                if (s.equals("[]")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, getResources().getText(R.string.errLogin), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    try {
                        JSONArray jsonArray = new JSONArray(s);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            for (int j = 0; j < MyConstant.getColumnLoginSize; j++) {
                                loginStrings[j] = jsonObject.getString(MyConstant.getColumnLogin[j]);
                            }
                        }
                        Log.d("VAL-Tag-Main", "login ==> " + Arrays.toString(loginStrings));

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d("VAL-Tag-Main", "e ==> " + e + " Line " + e.getStackTrace()[0].getLineNumber());
                    }

                    Intent intent = new Intent(MainActivity.this, TripActivity.class);
                    intent.putExtra("Login", loginStrings);
                    startActivity(intent);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, getResources().getText(R.string.login_success), Toast.LENGTH_LONG).show();
                        }
                    });
                    finish();

                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, getResources().getText(R.string.errLogin), Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }

    @OnClick(R.id.btnMALogin)
    public void onViewClicked() {
        SyncGetUserLogin syncGetUserLogin = new SyncGetUserLogin(this, usernameEditText.getText().toString(), passwordEditText.getText().toString());
        syncGetUserLogin.execute();
    }
}