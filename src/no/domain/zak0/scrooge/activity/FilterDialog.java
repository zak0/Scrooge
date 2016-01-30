package no.domain.zak0.scrooge.activity;

import no.domain.zak0.R;
import no.domain.zak0.scrooge.dataclass.PoormanFilter;
import no.domain.zak0.scrooge.utils.DatabaseHelper;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import java.sql.Date;
import java.util.Calendar;

public class FilterDialog {

	private Context context;
	private DatabaseHelper db;
	private Dialog dialog;
	private PoormanFilter filter;

    private EditText edittext_before;
    private EditText edittext_after;
	
	public FilterDialog(Context ct, DatabaseHelper db, PoormanFilter filter) {
        this.context = ct;
        this.db = db;
        this.filter = filter;
	}

    public Dialog getDialog() {
        dialog = new Dialog(context);
        dialog.setTitle(R.string.filter_actions);
        dialog.setContentView(R.layout.dialog_filter);

        Calendar cal_before = Calendar.getInstance();
        Calendar cal_after = Calendar.getInstance();

        if(filter.getFilterTimeSpanBefore() == 0L) {
            cal_before.set(Calendar.HOUR_OF_DAY, 23);
            cal_before.set(Calendar.MINUTE, 59);
            cal_before.set(Calendar.SECOND, 59);
            cal_before.set(Calendar.MILLISECOND, 999);
        }
        else {
            cal_before.setTimeInMillis(filter.getFilterTimeSpanBefore());
        }

        if(filter.getFilterTimeSpanAfter() == 0L) {
            cal_after.set(Calendar.HOUR_OF_DAY, 0);
            cal_after.set(Calendar.MINUTE, 0);
            cal_after.set(Calendar.SECOND, 0);
            cal_after.set(Calendar.MILLISECOND, 0);
        }
        else {
            cal_after.setTimeInMillis(filter.getFilterTimeSpanAfter());
        }

        // if filter time span is not set, set it to default
        if(filter.getFilterTimeSpanAfter() == 0L && filter.getFilterTimeSpanBefore() == 0L) {
            filter.setFilterTimeSpan(cal_after.getTimeInMillis(), cal_before.getTimeInMillis());
        }

        Button cancel_button = (Button) dialog.findViewById(R.id.button_filter_cancel);
        Button set_button = (Button) dialog.findViewById(R.id.button_filter_set);

        edittext_after = (EditText) dialog.findViewById(R.id.editText_filter_time_from);
        edittext_before = (EditText) dialog.findViewById(R.id.editText_filter_time_to);

        // Date field onClickListeners (and datePickerDialogs...)
        final DatePickerDialog before_dialog = new DatePickerDialog(context, 0, new FilterOnDateSetListener(edittext_before, filter, context, FilterOnDateSetListener.FILTER_ON_DATE_SET_BEFORE), cal_before.get(Calendar.YEAR), cal_before.get(Calendar.MONTH), cal_before.get(Calendar.DAY_OF_MONTH));
        final DatePickerDialog after_dialog = new DatePickerDialog(context, 0, new FilterOnDateSetListener(edittext_after, filter, context, FilterOnDateSetListener.FILTER_ON_DATE_SET_AFTER), cal_after.get(Calendar.YEAR), cal_after.get(Calendar.MONTH), cal_after.get(Calendar.DAY_OF_MONTH));
        edittext_before.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!before_dialog.isShowing()) before_dialog.show();
            }
        });
        edittext_after.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!after_dialog.isShowing()) after_dialog.show();
            }
        });

        // Button onClickListeners
        set_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Time span filter
                CheckBox checkbox_timespan = (CheckBox) dialog.findViewById(R.id.checkBox_filter_time_span);
                if(checkbox_timespan.isChecked()) filter.enableFilter(PoormanFilter.FILTER_TIME_SPAN);
                else filter.disableFilter(PoormanFilter.FILTER_TIME_SPAN);

                ((PoormanActivity) context).refreshUi(true);
                dialog.dismiss();
            }
        });


        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


        // Set values from filter to UI controls
        setCurrentValuesToControls();

        return dialog;
    }

    // Sets current filter settings to filter dialog
    private void setCurrentValuesToControls() {
        // TODO: everything

        // Set enabled filters checkboxes
        CheckBox timespan_checkbox = (CheckBox) dialog.findViewById(R.id.checkBox_filter_time_span);

        for(int i = 0; i < filter.getActiveFilters().size(); i++) {
            switch(filter.getActiveFilters().get(i)) {
            case PoormanFilter.FILTER_TIME_SPAN:
                timespan_checkbox.setChecked(true);
                break;
            }
        }

        // Set time span filter
        Date date_before;
        Date date_after;

        if(filter.getFilterTimeSpanBefore() == 0L) date_before = new Date(System.currentTimeMillis());
        else date_before = new Date(filter.getFilterTimeSpanBefore());

        if(filter.getFilterTimeSpanAfter() == 0L) date_after = new Date(System.currentTimeMillis());
        else date_after = new Date(filter.getFilterTimeSpanAfter());

        edittext_before.setText(DateFormat.getDateFormat(context).format(date_before));
        edittext_after.setText(DateFormat.getDateFormat(context).format(date_after));

    }

}
