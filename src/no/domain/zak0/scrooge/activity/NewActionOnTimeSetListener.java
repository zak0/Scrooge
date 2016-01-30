package no.domain.zak0.scrooge.activity;

import java.sql.Date;
import android.text.format.DateFormat;
import java.util.Calendar;
import java.util.Locale;

import no.domain.zak0.scrooge.dataclass.PoormanAction;

import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.widget.EditText;
import android.widget.TimePicker;

public class NewActionOnTimeSetListener implements OnTimeSetListener {
	
	private EditText timebox;
	private PoormanAction action;
	private Context context;
	
	public NewActionOnTimeSetListener(EditText time, PoormanAction act, Context ct) {
		this.timebox = time;
		this.action = act;
		this.context = ct;
	}
	
	
	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		Calendar cal = Calendar.getInstance(Locale.getDefault());
		
		cal.setTimeInMillis(action.getTime());
		
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		
		Long cal_in_millis = cal.getTimeInMillis();
		cal_in_millis += hourOfDay * 60 * 60 * 1000;
		cal_in_millis += minute * 60 * 1000;
		
		timebox.setText(DateFormat.getTimeFormat(context).format(new Date(cal_in_millis)));
				
		action.setHoursAndMinutes(hourOfDay, minute);
		action.setTime(cal_in_millis);
	}

}
