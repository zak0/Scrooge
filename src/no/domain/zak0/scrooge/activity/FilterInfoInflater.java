package no.domain.zak0.scrooge.activity;

import android.content.Context;
import android.graphics.Typeface;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import no.domain.zak0.R;
import no.domain.zak0.scrooge.dataclass.PoormanFilter;

import java.util.Date;

/**
 * Created by jaakko on 7/31/13.
 *
 * Creates a view that contains info of the currently active filter.
 * This view is visible in transactions and summary tabs on the main activity.
 *
 */


public class FilterInfoInflater {

    private Context context;
    private PoormanFilter filter;

    public FilterInfoInflater(Context ct, PoormanFilter filter) {
        this.context = ct;
        this.filter = filter;
    }

    public View getInflatedView() {
        return getInflateView(R.string.filter_info_title_actions);
    }

    public View getInflateView(int title_resource_id) {

        LinearLayout parent = new LinearLayout(context);
        parent.setOrientation(LinearLayout.VERTICAL);
        parent.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        parent.setPadding(10,10,10,10);
        parent.setBackgroundResource(R.color.new_action_tags_bg);

        TextView title = new TextView(context);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setText(title_resource_id);

        TextView all_actions = new TextView(context);
        all_actions.setText(R.string.filter_info_all_actions);

        // compile time span string
        TextView timespan = new TextView(context);
        String timespan_string = context.getString(R.string.filter_info_timespan_begin);
        String before_string = ""; // string to house before -date of the time span
        String after_string = ""; // string to house after -date of the time span
        Date before_date = new Date(filter.getFilterTimeSpanBefore());
        Date after_date = new Date(filter.getFilterTimeSpanAfter());

        timespan_string += " "+DateFormat.getDateFormat(context).format(after_date);
        timespan_string += " "+context.getString(R.string.filter_info_timespan_middle);
        timespan_string += " "+DateFormat.getDateFormat(context).format(before_date);

        timespan.setText(timespan_string);

        // Add views to parent
        parent.addView(title);
        if(filter.getActiveFilters().size() <= 0) {
            parent.addView(all_actions);
        }
        else {
            for(int i = 0; i < filter.getActiveFilters().size(); i++) {
                switch(filter.getActiveFilters().get(i)) {
                    case PoormanFilter.FILTER_TIME_SPAN:
                        parent.addView(timespan);
                        break;
                }
            }
        }

        return parent;
    }


}
