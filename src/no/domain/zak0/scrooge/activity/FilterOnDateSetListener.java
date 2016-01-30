package no.domain.zak0.scrooge.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.text.format.DateFormat;
import android.widget.DatePicker;
import android.widget.EditText;

import no.domain.zak0.scrooge.dataclass.PoormanFilter;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by jaakko on 7/30/13.
 */
public class FilterOnDateSetListener implements DatePickerDialog.OnDateSetListener {

    private EditText edittext;
    private PoormanFilter filter;
    private Context context;

    public static final int FILTER_ON_DATE_SET_BEFORE = 0;
    public static final int FILTER_ON_DATE_SET_AFTER = 1;

    private int before_or_after = 2;

    public FilterOnDateSetListener(EditText edittext, PoormanFilter filter, Context ct, int before_or_after) {
        this.edittext = edittext;
        this.filter = filter;
        this.context = ct;
        this.before_or_after = before_or_after;
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        Calendar cal = Calendar.getInstance(Locale.getDefault());

        // set after to beginning of day, and before to the end of day
        if(before_or_after == FILTER_ON_DATE_SET_AFTER) {
            cal.set(year, month, day, 0, 0, 0);
            cal.set(Calendar.MILLISECOND, 0);
        }

        else if(before_or_after == FILTER_ON_DATE_SET_BEFORE) {
            cal.set(year, month, day, 23, 59, 59);
            cal.set(Calendar.MILLISECOND, 999);
        }

        Long cal_in_millis = cal.getTimeInMillis();
        edittext.setText(DateFormat.getDateFormat(context).format(cal_in_millis));

        if(before_or_after == FILTER_ON_DATE_SET_AFTER) {
            filter.setFilterTimeSpan(cal_in_millis, filter.getFilterTimeSpanBefore());
        }
        else if(before_or_after == FILTER_ON_DATE_SET_BEFORE) {
            filter.setFilterTimeSpan(filter.getFilterTimeSpanAfter(), cal_in_millis);
        }
    }
}
