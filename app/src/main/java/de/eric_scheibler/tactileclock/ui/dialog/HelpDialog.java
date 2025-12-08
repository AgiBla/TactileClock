package de.eric_scheibler.tactileclock.ui.dialog;

import de.eric_scheibler.tactileclock.utils.Helper;
import timber.log.Timber;
import java.util.Calendar;
import android.app.AlertDialog;
import android.app.Dialog;

import android.content.Context;
import android.content.DialogInterface;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;

import de.eric_scheibler.tactileclock.R;
import de.eric_scheibler.tactileclock.utils.SettingsManager;
import android.content.Intent;
import de.eric_scheibler.tactileclock.utils.TactileClockService;
import android.widget.Button;
import android.os.Handler;
import android.os.Looper;


public class HelpDialog extends DialogFragment {
    public static final String REQUEST_DIALOG_CLOSED = "dialogClosed";

    public static HelpDialog newInstance() {
        HelpDialog helpDialogInstance = new HelpDialog();
        return helpDialogInstance;
    }


    private SettingsManager settingsManagerInstance;
    private Handler handler;

	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        handler = new Handler(Looper.getMainLooper());
        settingsManagerInstance = SettingsManager.getInstance();
    }

    @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (settingsManagerInstance.getFirstStart()) {
            // must force a click on the "OK" button if the app is launched for the first time
            // otherwise it's not guaranteed that the REQUEST_DIALOG_CLOSED action is sent
            setCancelable(false);
        }

        final ViewGroup nullParent = null;
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_help, nullParent);

        return new AlertDialog.Builder(getActivity())
            .setTitle(getResources().getString(R.string.helpDialogTitle))
            .setView(view)
            .setPositiveButton(
                    getResources().getString(R.string.dialogOK),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
            .setNeutralButton(
                    getResources().getString(R.string.buttonTestVibration),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
            .create();
    }

    @Override public void onStart() {
        super.onStart();
        final AlertDialog dialog = (AlertDialog)getDialog();
        if(dialog != null) {

            Button buttonPositive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            buttonPositive.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View view) {
                    getParentFragmentManager().setFragmentResult(
                            REQUEST_DIALOG_CLOSED, new Bundle());
                    dismiss();
                }
            });

            Button buttonNeutral = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
            buttonNeutral.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View view) {
                    Calendar calendar = Calendar.getInstance();
                    Context context = getContext();
                    if (context != null) {

                        Intent intent = new Intent(context, TactileClockService.class);
                        TactileClockService.TEST_HOUR = calendar.get(Calendar.HOUR_OF_DAY);
                        TactileClockService.TEST_MINUTE = calendar.get(Calendar.MINUTE);
                        intent.setAction(TactileClockService.ACTION_VIBRATE_TEST_TIME);

                        if (Helper.isScreenReaderEnabled()) {
                            // requires a short delay
                            // otherwise Talkback consumes the test vibration if Talkbacks vibration feedback is enabled
                            handler.postDelayed(() -> {
                                context.startService(intent);
                            }, 250);
                        } else {
                            context.startService(intent);
                        }
                    }
                }
            });
        }
    }

}
