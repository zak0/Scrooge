package no.domain.zak0.scrooge.activity;

import java.sql.Date;
import android.text.format.DateFormat;
import java.util.Calendar;
import java.util.Locale;

import no.domain.zak0.scrooge.dataclass.PoormanAction;

import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.widget.DatePicker;
import android.widget.EditText;

public class NewActionOnDateSetListener implements OnDateSetListener {

	private EditText datebox;
	private PoormanAction action;
	private Context context;
	
	public NewActionOnDateSetListener(EditText date, PoormanAction act, Context ct) {
		this.datebox = date;
		this.action = act;
		this.context = ct;
	}
	
	@Override
	public void onDateSet(DatePicker arg0, int year, int month, int day) {
		Calendar cal = Calendar.getInstance(Locale.getDefault());
		cal.set(year, month, day, action.getHours(), action.getMinutes());
		Long cal_in_millis = cal.getTimeInMillis();
		
		datebox.setText(DateFormat.getDateFormat(context).format(new Date(cal.getTimeInMillis())));
		
		action.setTime(cal_in_millis);
	}

}
