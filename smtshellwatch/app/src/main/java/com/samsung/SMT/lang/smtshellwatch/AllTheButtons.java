package com.samsung.SMT.lang.smtshellwatch;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.wear.widget.WearableLinearLayoutManager;
import androidx.wear.widget.WearableRecyclerView;

import com.samsung.SMT.lang.smtshellwatch.watch.AdditionalWatchServices;
import com.samsung.SMT.lang.smtshellwatch.watch.CustomScrollingLayoutCallback;
import com.samsung.SMT.lang.smtshellwatch.watch.SMTCapability;
import com.samsung.SMT.lang.smtshellwatch.watch.WearableRecycleViewAdapter;

import net.blufenix.smtshell.api.InternalAPI;
import net.blufenix.smtshell.api.SMTShellAPI;

import java.util.ArrayList;
import java.util.Arrays;

@RequiresApi(api = Build.VERSION_CODES.P)
public class AllTheButtons extends AppCompatActivity {

    ArrayList<SMTCapability> list = new ArrayList<>(Arrays.asList(
            new SMTCapability(
                    "Kill SMT Shell API",
                    "Clears application data for com.samsung.SMT, disabling all payloads and allowing the exploit to run again.",
                    "Kill", v -> InternalAPI.killAPI(this, success -> ActivityUtils.launchNewTask(this, MainActivity.class))),
            new SMTCapability(
                    "Local System Shell",
                    "Connect to a system shell directly in the app!",
                    "Launch", v -> ActivityUtils.launch(AllTheButtons.this, ShellActivity.class)),
            new SMTCapability(
                    "Reverse Shell",
                    "Start a reverse shell that can be connected to via:\n adb shell nc -l -p 9999",
                    "Launch", v -> SMTShellAPI.loadLibrary(this, getApplicationInfo().nativeLibraryDir + "/" + "libsmtshell.so")),
            new SMTCapability(
                    "Band Selection",
                    null,
                    "Launch", v -> SMTShellAPI.executeCommand(this, "am start com.samsung.android.app.telephonyui/.hiddennetworksetting.MainActivity")),
            new SMTCapability(
                    "Band Selection (Advanced)",
                    null,
                    "Launch", v -> SMTShellAPI.executeCommand(this, "am broadcast -a com.samsung.android.action.SECRET_CODE -d android_secret_code://2263 -n com.sec.android.RilServiceModeApp/.SecKeyStringBroadcastReceiver")),
            new SMTCapability(
                    "Change CSC",
                    null,
                    "Launch", v -> SMTShellAPI.executeCommand(this, "am start -n com.samsung.android.cidmanager/.modules.preconfig.PreconfigActivity -a com.samsung.android.action.SECRET_CODE -d secret_code://27262826 --ei type 2")),
            new SMTCapability(
                    "Service Menu",
                    null,
                    "Launch", v -> SMTShellAPI.executeCommand(this, "am broadcast -a com.samsung.android.action.SECRET_CODE -d android_secret_code://27663368378 -n com.sec.android.RilServiceModeApp/.SecKeyStringBroadcastReceiver")),
            new SMTCapability(
                    "Info Menu",
                    null,
                    "Launch", v -> SMTShellAPI.executeCommand(this, "am broadcast -a com.samsung.android.action.SECRET_CODE -d android_secret_code://0011 -n com.sec.android.RilServiceModeApp/.SecKeyStringBroadcastReceiver")),
            new SMTCapability(
                    "DSU Loader",
                    null,
                    "Launch", v -> SMTShellAPI.executeCommand(this, "am start -n com.android.settings/.development.DSULoader")),
            new SMTCapability(
                    "Spawn DSU Notification",
                    "Spawns a notification in the system tray that allows a selected DSU to be discarded.",
                    "Launch", v -> SMTShellAPI.executeCommand(this, "am start -n com.android.dynsystem/.VerificationActivity"))
    ));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allthebuttons);

        WearableRecyclerView wearableRecyclerView = findViewById(R.id.list);
        wearableRecyclerView.setEdgeItemsCenteringEnabled(true);

        CustomScrollingLayoutCallback customScrollingLayoutCallback = new CustomScrollingLayoutCallback();
        wearableRecyclerView.setLayoutManager(new WearableLinearLayoutManager(getApplicationContext(), customScrollingLayoutCallback));

        wearableRecyclerView.setAdapter(new WearableRecycleViewAdapter(list));

        initAdditionalWatchServices(wearableRecyclerView);
    }

    private void initAdditionalWatchServices(View view){
        AdditionalWatchServices additionalWatchServices = new AdditionalWatchServices(getApplicationContext(), getSupportActionBar());
        additionalWatchServices.customActionBar();
        additionalWatchServices.bindHardwareRotary(view);
    }

}
