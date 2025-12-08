package de.eric_scheibler.tactileclock.utils;

import android.view.accessibility.AccessibilityManager;
import android.content.Context;
import android.accessibilityservice.AccessibilityServiceInfo;
import androidx.annotation.RequiresApi;
import android.os.Vibrator;
import android.os.Build;
import android.os.VibrationEffect;
import androidx.core.view.AccessibilityDelegateCompat;
import android.view.View;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.annotation.NonNull;
import android.widget.Button;



public class Helper {


    /**
     * accessibility
     */

    public static boolean isScreenReaderEnabled() {
        AccessibilityManager accessibilityManager = (AccessibilityManager) ApplicationInstance.getContext().getSystemService(Context.ACCESSIBILITY_SERVICE);
        return ! accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_SPOKEN).isEmpty();
    }

    public static AccessibilityDelegateCompat getAccessibilityDelegateViewClassButton() {
        // informs talkback, that the selected ui element is a button
        return new AccessibilityDelegateCompat() {
            @Override public void onInitializeAccessibilityNodeInfo(
                    @NonNull View host, @NonNull AccessibilityNodeInfoCompat info) {
                super.onInitializeAccessibilityNodeInfo(host, info);
                info.setClassName(Button.class.getName());
            }
        };
    }


    /**
     * vibrations
     * */

    public static void vibrateOnce(long duration) {
        Vibrator vibrator = (Vibrator) ApplicationInstance.getContext().getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrateOnceForOAndNewer(
                        vibrator, duration, getVibrationAmplitude());
            } else {
                vibrator.vibrate(duration);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void vibrateOnceForOAndNewer(Vibrator vibrator, long duration, int amplitude) {
        vibrator.vibrate(
                VibrationEffect.createOneShot(duration, amplitude));
    }

    public static void vibratePattern(long[] timings) {
        Vibrator vibrator = (Vibrator) ApplicationInstance.getContext().getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibratePatternForOAndNewer(vibrator, timings);
            } else {
                vibrator.vibrate(timings, -1);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void vibratePatternForOAndNewer(Vibrator vibrator, long[] timings) {
        int[] amplitudes = new int[timings.length];
        for (int i=0; i<amplitudes.length; i++) {
            amplitudes[i] = i % 2 == 0 ? 0 : Helper.getVibrationAmplitude();
        }
        vibrator.vibrate(
                VibrationEffect.createWaveform(timings, amplitudes, -1));
    }

    public static void cancelVibration() {
        Vibrator vibrator = (Vibrator) ApplicationInstance.getContext().getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            vibrator.cancel();
        }
    }

    // amplitudes
    private static final int VIBRATION_AMPLITUDE_DEFAULT = 150;
    private static final int VIBRATION_AMPLITUDE_MAX = 250;

    private static int getVibrationAmplitude() {
        return SettingsManager.getInstance().getMaxStrengthVibrationsEnabled()
            ? VIBRATION_AMPLITUDE_MAX : VIBRATION_AMPLITUDE_DEFAULT;
    }

}
