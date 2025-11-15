package de.eric_scheibler.tactileclock.ui.activity;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TextClock;
import android.widget.TimePicker;

import androidx.appcompat.widget.Toolbar;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.eric_scheibler.tactileclock.R;
import de.eric_scheibler.tactileclock.data.HourFormat;
import de.eric_scheibler.tactileclock.data.TimeComponentOrder;
import androidx.appcompat.widget.SwitchCompat;
import android.widget.CompoundButton;
import de.eric_scheibler.tactileclock.utils.TactileClockService;


public class SettingsActivity extends AbstractActivity implements TimePickerDialog.OnTimeSetListener {

    private SwitchCompat switchMaxStrengthVibrations;
    private RadioGroup radioHourFormat, radioTimeComponentOrder;
    private SeekBar seekBarShort, seekBarLong;
    private TextView textViewShortValue, textViewLongValue;
    private Button buttonTest;
    private TextClock textClock;
    private Integer testHour, testMinute;

    @Override public int getLayoutResourceId() {
        return R.layout.activity_settings;
    }

	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        Calendar calendar = Calendar.getInstance();
        testHour = calendar.get(Calendar.HOUR_OF_DAY);
        testMinute = calendar.get(Calendar.MINUTE);

        // toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(
                getResources().getString(R.string.settingsActivityTitle));

        switchMaxStrengthVibrations = (SwitchCompat) findViewById(R.id.switchMaxStrengthVibrations);
        switchMaxStrengthVibrations.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton view, boolean isChecked) {
                if (isChecked != settingsManagerInstance.getMaxStrengthVibrationsEnabled()) {
                    settingsManagerInstance.setMaxStrengthVibrationsEnabled(isChecked);
                }
            }
        });

        // hour format
        radioHourFormat = (RadioGroup) findViewById(R.id.radioHourFormat);
        radioHourFormat.check(settingsManagerInstance.getHourFormat() == HourFormat.TWELVE_HOURS ? R.id.button12Hours : R.id.button24Hours);
        radioHourFormat.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.button12Hours) {
                    settingsManagerInstance.setHourFormat(HourFormat.TWELVE_HOURS);
                } else if (checkedId == R.id.button24Hours) {
                    settingsManagerInstance.setHourFormat(HourFormat.TWENTYFOUR_HOURS);
                }
                updateUI();
            }
        });

        // time component order: "hours minutes" or "minutes hours"
        radioTimeComponentOrder = (RadioGroup) findViewById(R.id.radioTimeComponentOrder);
        radioTimeComponentOrder.check(settingsManagerInstance.getTimeComponentOrder() == TimeComponentOrder.MINUTES_HOURS ? R.id.buttonMinutesHours : R.id.buttonHoursMinutes);
        radioTimeComponentOrder.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.buttonHoursMinutes) {
                    settingsManagerInstance.setTimeComponentOrder(TimeComponentOrder.HOURS_MINUTES);
                } else if (checkedId == R.id.buttonMinutesHours) {
                    settingsManagerInstance.setTimeComponentOrder(TimeComponentOrder.MINUTES_HOURS);
                }
                updateUI();
            }
        });

        // seekbars
        seekBarShort = (SeekBar) findViewById(R.id.seekBarShort);
        seekBarShort.setProgress(settingsManagerInstance.getShortVibration() / 5);
        seekBarShort.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                settingsManagerInstance.setShortVibration(progress * 5);
                updateUI();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(settingsManagerInstance.getShortVibration());
            }
        });

        seekBarLong = (SeekBar) findViewById(R.id.seekBarLong);
        seekBarLong.setProgress(settingsManagerInstance.getLongVibration() / 5);
        seekBarLong.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                settingsManagerInstance.setLongVibration(progress * 5);
                updateUI();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(settingsManagerInstance.getLongVibration());
            }
        });

        textViewShortValue = (TextView) findViewById(R.id.textViewShortValue);
        textViewLongValue = (TextView) findViewById(R.id.textViewLongValue);

        textClock = (TextClock) findViewById(R.id.textClock);
        textClock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                boolean is24HourFormat = settingsManagerInstance.getHourFormat() == HourFormat.TWENTYFOUR_HOURS;

                TimePickerDialog timePickerDialog = new TimePickerDialog(SettingsActivity.this, SettingsActivity.this, hour, minute, is24HourFormat);
                timePickerDialog.show();
            }
        });

        buttonTest = (Button) findViewById(R.id.buttonTest);
        buttonTest.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, TactileClockService.class);
                TactileClockService.TEST_HOUR = testHour;
                TactileClockService.TEST_MINUTE = testMinute;
                intent.setAction(TactileClockService.ACTION_VIBRATE_TEST_TIME);
                startService(intent);
            }
        });
    }

	@Override public void onResume() {
		super.onResume();
        updateUI();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        testHour = hourOfDay;
        testMinute = minute;
        updateUI();
    }

    private void updateUI() {
        switchMaxStrengthVibrations.setChecked(settingsManagerInstance.getMaxStrengthVibrationsEnabled());

        boolean is12HourFormat = settingsManagerInstance.getHourFormat() == HourFormat.TWELVE_HOURS;
        boolean isMinutesFirst = settingsManagerInstance.getTimeComponentOrder() == TimeComponentOrder.MINUTES_HOURS;

        textViewShortValue.setText(settingsManagerInstance.getShortVibration() + " ms");
        textViewLongValue.setText(settingsManagerInstance.getLongVibration() + " ms");

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, testHour);
        calendar.set(Calendar.MINUTE, testMinute);

        String pattern;
        if (is12HourFormat) {
            pattern = isMinutesFirst ? "mm:h a" : "h:mm a";
        } else {
            pattern = isMinutesFirst ? "mm:HH" : "HH:mm";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, getResources().getConfiguration().locale);
        textClock.setText(sdf.format(calendar.getTime()));
    }
}
