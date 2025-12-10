package de.eric_scheibler.tactileclock.ui.fragment;

import de.eric_scheibler.tactileclock.utils.TactileClockService.ScreenOffOn;
import android.widget.RadioGroup;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.CompoundButton;

import de.eric_scheibler.tactileclock.R;
import de.eric_scheibler.tactileclock.ui.dialog.SelectIntegerDialog.IntegerSelector;
import de.eric_scheibler.tactileclock.ui.dialog.SelectIntegerDialog.Token;
import de.eric_scheibler.tactileclock.ui.dialog.SelectIntegerDialog;
import de.eric_scheibler.tactileclock.utils.SettingsManager;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.SwitchCompat;


public class PowerButtonFragment extends Fragment implements IntegerSelector {

	// Store instance variables
	private SettingsManager settingsManagerInstance;

    // ui components
    private Button buttonPowerButtonLowerSuccessBoundary, buttonPowerButtonUpperSuccessBoundary;
    private SwitchCompat buttonEnableService;
    private RadioGroup radioScreenOffOnAction;

    // newInstance constructor for creating fragment with arguments
    public static PowerButtonFragment newInstance() {
        PowerButtonFragment powerButtonFragmentInstance = new PowerButtonFragment();
        return powerButtonFragmentInstance;
    }

	@Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settingsManagerInstance = SettingsManager.getInstance();
	}

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_power_button, container, false);
    }

	@Override public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

        // enable service
        buttonEnableService = (SwitchCompat) view.findViewById(R.id.buttonEnableService);
        buttonEnableService.setChecked(
                settingsManagerInstance.getPowerButtonServiceEnabled());
        buttonEnableService.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settingsManagerInstance.setPowerButtonServiceEnabled(isChecked);
            }
        });

        buttonPowerButtonLowerSuccessBoundary = (Button) view.findViewById(R.id.buttonPowerButtonLowerSuccessBoundary);
        buttonPowerButtonLowerSuccessBoundary.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                SelectIntegerDialog dialog = SelectIntegerDialog.newInstance(
                        Token.POWER_BUTTON_LOWER_SUCCESS_BOUNDARY,
                        (int) settingsManagerInstance.getPowerButtonLowerSuccessBoundary(),
                        (int) SettingsManager.DEFAULT_POWER_BUTTON_LOWER_SUCCESS_BOUNDARY);
                dialog.setTargetFragment(PowerButtonFragment.this, 1);
                dialog.show(getActivity().getSupportFragmentManager(), "SelectIntegerDialog");
            }
        });

        buttonPowerButtonUpperSuccessBoundary = (Button) view.findViewById(R.id.buttonPowerButtonUpperSuccessBoundary);
        buttonPowerButtonUpperSuccessBoundary.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                SelectIntegerDialog dialog = SelectIntegerDialog.newInstance(
                        Token.POWER_BUTTON_UPPER_SUCCESS_BOUNDARY,
                        (int) settingsManagerInstance.getPowerButtonUpperSuccessBoundary(),
                        (int) SettingsManager.DEFAULT_POWER_BUTTON_UPPER_SUCCESS_BOUNDARY);
                dialog.setTargetFragment(PowerButtonFragment.this, 1);
                dialog.show(getActivity().getSupportFragmentManager(), "SelectIntegerDialog");
            }
        });

        radioScreenOffOnAction = (RadioGroup) view.findViewById(R.id.radioScreenOffOnAction);
        switch (settingsManagerInstance.getScreenOffOnAction()) {
            case DO_NOTHING:
                radioScreenOffOnAction.check(R.id.buttonScreenOffOnDoNothing);
                break;
            case ERROR_VIBRATION:
                radioScreenOffOnAction.check(R.id.buttonScreenOffOnErrorVibration);
                break;
            case VIBRATE_TIME:
                radioScreenOffOnAction.check(R.id.buttonScreenOffOnVibrateTime);
                break;
        }
        radioScreenOffOnAction.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                ScreenOffOn screenOffOnAction = null;
                if (checkedId == R.id.buttonScreenOffOnDoNothing) {
                    screenOffOnAction = ScreenOffOn.DO_NOTHING;
                } else if (checkedId == R.id.buttonScreenOffOnErrorVibration) {
                    screenOffOnAction = ScreenOffOn.ERROR_VIBRATION;
                } else if (checkedId == R.id.buttonScreenOffOnVibrateTime) {
                    screenOffOnAction = ScreenOffOn.VIBRATE_TIME;
                }
                if (screenOffOnAction != null) {
                    settingsManagerInstance.setScreenOffOnAction(screenOffOnAction);
                }
            }
        });
    }

    @Override public void onPause() {
        super.onPause();
    }

    @Override public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override public void integerSelected(Token token, Integer newInteger) {
        if (newInteger != null) {
            switch (token) {
                case POWER_BUTTON_LOWER_SUCCESS_BOUNDARY:
                    settingsManagerInstance.setPowerButtonLowerSuccessBoundary(newInteger);
                    updateUI();
                    break;
                case POWER_BUTTON_UPPER_SUCCESS_BOUNDARY:
                    settingsManagerInstance.setPowerButtonUpperSuccessBoundary(newInteger);
                    updateUI();
                    break;
                default:
                    break;
            }
        }
    }

    private void updateUI() {
        buttonPowerButtonLowerSuccessBoundary.setText(
                String.format(
                    "%1$s: %2$s",
                    getResources().getString(R.string.buttonPowerButtonLowerSuccessBoundary),
                    getResources().getQuantityString(
                        R.plurals.milliseconds,
                        (int) settingsManagerInstance.getPowerButtonLowerSuccessBoundary(),
                        (int) settingsManagerInstance.getPowerButtonLowerSuccessBoundary()))
                );
        buttonPowerButtonUpperSuccessBoundary.setText(
                String.format(
                    "%1$s: %2$s",
                    getResources().getString(R.string.buttonPowerButtonUpperSuccessBoundary),
                    getResources().getQuantityString(
                        R.plurals.milliseconds,
                        (int) settingsManagerInstance.getPowerButtonUpperSuccessBoundary(),
                        (int) settingsManagerInstance.getPowerButtonUpperSuccessBoundary()))
                );
    }

}
