package com.samsung.SMT.lang.smtshellwatch.watch;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import androidx.appcompat.app.ActionBar;
import androidx.core.view.InputDeviceCompat;
import androidx.core.view.MotionEventCompat;
import androidx.core.view.ViewConfigurationCompat;

import com.samsung.SMT.lang.smtshellwatch.R;

public class AdditionalWatchServices {

    Context context;

    ActionBar actionBar;

    public AdditionalWatchServices(Context c, ActionBar ab){
        context =c;
        actionBar = ab;
    }

    public void customActionBar(){
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setCustomView(R.layout.centered_action_bar);
        }
    }

    public void bindHardwareRotary(View view){
        view.setOnGenericMotionListener((v, ev) -> {
            if (ev.getAction() == MotionEvent.ACTION_SCROLL &&
                    ev.isFromSource(InputDeviceCompat.SOURCE_ROTARY_ENCODER)
            ) {
                // Don't forget the negation here
                float delta = -ev.getAxisValue(MotionEventCompat.AXIS_SCROLL) *
                        ViewConfigurationCompat.getScaledVerticalScrollFactor(
                                ViewConfiguration.get(context), context
                        );
                // Swap these axes to scroll horizontally instead
                v.scrollBy(0, Math.round(delta));
                return true;
            }
            return false;
        });
    }
}
