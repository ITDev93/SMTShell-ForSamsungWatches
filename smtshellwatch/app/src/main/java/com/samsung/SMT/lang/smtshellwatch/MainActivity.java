package com.samsung.SMT.lang.smtshellwatch;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.wear.remote.interactions.RemoteActivityHelper;
import androidx.wear.widget.ConfirmationOverlay;

import com.google.common.util.concurrent.ListenableFuture;
import com.samsung.SMT.lang.smtshellwatch.shizuku.PackageInstallerUtils;
import com.samsung.SMT.lang.smtshellwatch.watch.AdditionalWatchServices;

import net.blufenix.smtshell.api.SMTShellAPI;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import rikka.shizuku.Shizuku;

/**
 * We need to keep the minSdkVersion at 22 or lower, so use @RequiresApi to use newer stuff.
 * This only needs to support Android 9.0 (API 28) and higher anyway.
 */
@RequiresApi(api = Build.VERSION_CODES.P)
public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_SHIZUKU = 9000;

    private static final String TAG = "SMTShell";

    private TextView mTextView;
    private Button mExploitBtn;
    private ProgressBar mSpinner;

    private LinearLayout noShizuku;
    private String shizukuURL;

    private void onShizukuRequestPermissionsResult(int requestCode, int grantResult) {
        boolean granted = grantResult == PackageManager.PERMISSION_GRANTED;
        if (granted) {
            onReady(true);
        }
    }

    private boolean checkShizukuPermission() {
        if (Shizuku.isPreV11()) {
            // Pre-v11 is unsupported
            return false;
        } else if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
            // Granted
            return true;
        } else if (Shizuku.shouldShowRequestPermissionRationale()) {
            // Users choose "Deny and don't ask again"
            return false;
        } else {
            // Request the permission
            Shizuku.requestPermission(REQUEST_CODE_SHIZUKU);
            return false;
        }
    }

    private final BroadcastReceiver mApiReadyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "client got ready msg");
            setSpinner(false);
            ActivityUtils.launchNewTask(MainActivity.this, AllTheButtons.class);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mApiReadyReceiver, new IntentFilter(SMTShellAPI.ACTION_API_READY));
        SMTShellAPI.ping(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mApiReadyReceiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = findViewById(R.id.text);
        mExploitBtn = findViewById(R.id.btn_exploit);
        mSpinner = findViewById(R.id.indeterminateBar);
        initAdditionalWatchServices();
        initExtraButtons();
    }

    private void initAdditionalWatchServices(){
        AdditionalWatchServices additionalWatchServices = new AdditionalWatchServices(getApplicationContext(), getSupportActionBar());
        additionalWatchServices.customActionBar();
        ScrollView scrollView = findViewById(R.id.scrollView);
        additionalWatchServices.bindHardwareRotary(scrollView);
    }

    private void initExtraButtons(){
        noShizuku = findViewById(R.id.noShizuku);
        Button openReadMe = findViewById(R.id.open_readme);
        Button openShizuku = findViewById(R.id.open_shizuku);

        String ReadMeURL = "https://github.com/ITDev93/SMTShell-ForSamsungWatches#how-to-downgrade-smt";
        openReadMe.setOnClickListener(view -> openURL(ReadMeURL));

        shizukuURL = "https://github.com/RikkaApps/Shizuku/releases";
        String shizukuPackage = "moe.shizuku.privileged.api";
        openShizuku.setOnClickListener(view -> {
            PackageManager pm = getPackageManager();
            try {
                pm.getPackageInfo(shizukuPackage, PackageManager.GET_ACTIVITIES);
                startActivity(pm.getLaunchIntentForPackage(shizukuPackage));
            } catch (PackageManager.NameNotFoundException e) {
                openURL(shizukuURL);
            }
        });
    }

    private void openURL(String url){
        Uri uriLink = Uri.parse(url);
        Intent intentAndroid = new Intent(Intent.ACTION_VIEW)
                .addCategory(Intent.CATEGORY_BROWSABLE)
                .setData(uriLink);

        Executor executor = Executors.newSingleThreadExecutor();
        RemoteActivityHelper remoteActivityHelper = new RemoteActivityHelper(this,executor);
        ListenableFuture<Void> result = remoteActivityHelper.startRemoteActivity(intentAndroid, null);
        ConfirmationOverlay confirmationOverlay = new ConfirmationOverlay();
        result.addListener(() -> {
            try {
                result.get();
                CharSequence message = "Check your Phone";
                runOnUiThread(() -> confirmationOverlay
                        .setType(ConfirmationOverlay.OPEN_ON_PHONE_ANIMATION)
                        .setMessage(message)
                        .setDuration(3000)
                        .setOnAnimationFinishedListener(() -> {
                            if(url.equals(shizukuURL)){
                                Toast.makeText(getApplicationContext(), "Shizuku not installed, install it on your watch!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .showOn(this));
            } catch (InterruptedException | ExecutionException e) {
                CharSequence message = "Phone Can't be reached";
                runOnUiThread(() -> confirmationOverlay
                        .setType(ConfirmationOverlay.FAILURE_ANIMATION)
                        .setMessage(message)
                        .setDuration(3000)
                        .showOn(this));
            }
        }, executor);

    }

    private void restartApp(){
        Intent i = getIntent();
        finish();
        startActivity(i);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (ConflictUtil.hasConflicts(this)) {
            ActivityUtils.launchNewTask(MainActivity.this, ConflictActivity.class);
            return;
        }

        onReady(false);

        Shizuku.addRequestPermissionResultListener(this::onShizukuRequestPermissionsResult);
        // TODO Small inherent race condition here because addBinderReceivedListener doesn't get
        //  called if we already have the binder, so if we receive the binder right after ping,
        //  but before adding the listener, nothing will happen.
        if (!Shizuku.pingBinder()) {
            Shizuku.addBinderReceivedListener(() -> {
                if (checkShizukuPermission()) {
                    onReady(true);
                }
            });
        } else {
            if (checkShizukuPermission()) {
                onReady(true);
            }
        }
    }

    void setSpinner(boolean on) {
        mExploitBtn.setEnabled(!on);
        mSpinner.setProgress(0);
        mSpinner.setVisibility(on ? View.VISIBLE : View.GONE);
    }

    private void onReady(boolean shizuku) {
        if (shizuku && !isVulnerableSMT()) {
            Toast.makeText(this, "Shizuku detected!", Toast.LENGTH_SHORT).show();
            mTextView.setText(R.string.downgrade_smt_prompt_shizuku);
            mExploitBtn.setText(R.string.downgrade_smt);
            mExploitBtn.setOnClickListener(v -> {
                setSpinner(true);
                AsyncTask.execute(() -> {
                    boolean success = downgradeSMT();
                    runOnUiThread(() -> {
                        if (!success) {
                            Toast.makeText(this, "failed to downgrade!", Toast.LENGTH_SHORT).show();
                        }
                        setSpinner(false);
                        onReady(true);
                    });
                });
            });
        } else if (!shizuku && !isVulnerableSMT()) {
            Toast.makeText(this, "Shizuku not detected!", Toast.LENGTH_SHORT).show();
            noShizuku.setVisibility(View.VISIBLE);
            mTextView.setText(R.string.downgrade_smt_prompt_restart);
            mExploitBtn.setText(R.string.restart_app);
            mExploitBtn.setOnClickListener(v -> {
                // user might grant permission here
                //onReady(Shizuku.pingBinder());
                //Watch does not have task manager, leaving app destroys it,
                //manual adb might work though but unlikely users would succeed, so better restart the app
                restartApp();
            });
        } else {
            mTextView.setText(R.string.exploit_prompt);
            mExploitBtn.setText(R.string.exploit);
            mExploitBtn.setOnClickListener(v -> {
                setSpinner(true);
                exploit();
                Toast.makeText(this, "Exploit triggered!", Toast.LENGTH_SHORT).show();
            });
        }
        mExploitBtn.setEnabled(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Shizuku.removeRequestPermissionResultListener(this::onShizukuRequestPermissionsResult);
    }

    boolean downgradeSMT() {
        if (!isVulnerableSMT()) {
            return PackageInstallerUtils.installApkFromAssets(this, "com.samsung.SMT_v3.0.02.2.apk");
        } else {
            return true;
        }
    }

    boolean isVulnerableSMT() {
        try {
            int versionCode = getPackageManager()
                    .getPackageInfo("com.samsung.SMT", PackageManager.GET_META_DATA)
                    .versionCode;
            return (300200002 == versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void exploit() {
        MyService.allow = true;
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.samsung.SMT", "com.samsung.SMT.SamsungTTSService"));
        startService(intent);
    }

}
