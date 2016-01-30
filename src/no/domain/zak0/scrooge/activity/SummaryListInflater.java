package no.domain.zak0.scrooge.activity;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import no.domain.zak0.R;
import no.domain.zak0.scrooge.dataclass.PoormanAction;
import no.domain.zak0.scrooge.dataclass.PoormanFilter;
import no.domain.zak0.scrooge.dataclass.PoormanTag;
import no.domain.zak0.scrooge.utils.DatabaseHelper;

import java.util.Vector;

/**
 * Created by jaakko on 7/31/13.
 */
public class SummaryListInflater {

    private Context context;
    private PoormanFilter filter;
    private DatabaseHelper db;

    public SummaryListInflater(Context ct, PoormanFilter filter, DatabaseHelper db) {
        this.context = ct;
        this.filter = filter;
        this.db = db;
    }

    // 20130810: changed to take list of actions as parameter. this is to prevent one db query
    public View getInflatedView(Vector<PoormanAction> actions) {
        LinearLayout parent = new LinearLayout(context);
        parent.setOrientation(LinearLayout.VERTICAL);
        //parent.setPadding(10,10,10,10);

        View horiz_divider;
        horiz_divider = new View(context);
        horiz_divider.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,2));
        horiz_divider.setBackgroundColor(context.getResources().getColor(R.color.horiz_divider));

        // get all the transactions that match filter settings
        // 20130810: changed to parameter
        //Vector<PoormanAction> actions = db.getFilteredActions(filter);

        Vector<PoormanTag> tags;

        // if filter contains tags, only use selected tags
        if(filter.isFilterEnabled(PoormanFilter.FILTER_TAGS)) {
            tags = (Vector<PoormanTag>) filter.getFilterTags().clone();
        }
        // ... else use all tags
        else {
            tags = db.getAllTags();
        }


        double sum = 0.0;

        double[] tag_sums = new double[tags.size()]; // array that contains sums of each tag in filter
                                                     // indexes in this array and tags vector match

        // initialize tag sums vector
        for(int i = 0; i < tags.size(); i++) {
            tag_sums[i] = 0.0;
        }

        // calculate totals
        for(int i = 0; i < actions.size(); i++) {
            sum += actions.get(i).getAmount();

            // iterate through all the tags and compare to actions tags, and add to tag sums if match
            for(int j = 0; j < tags.size(); j++) {
               for(int k = 0; k < actions.get(i).getTags().size(); k++) {
                   if(tags.get(j).getId() == actions.get(i).getTags().get(k).getId()) {
                       tag_sums[j] += actions.get(i).getAmount();
                   }
               }
            }
        }

        // compile sum text
        TextView total_sum_title = new TextView(context);
        total_sum_title.setText(R.string.summary_sum_of_all);
        total_sum_title.setTypeface(Typeface.DEFAULT_BOLD);
        total_sum_title.setPadding(10,0,0,10);

        TextView total_sum = new TextView(context);
        if(sum >= 0) {
            total_sum.setText(String.format("+%.2f", sum));
            total_sum.setTextColor(context.getResources().getColor(R.color.green_number));
        }
        else {
            total_sum.setText(String.format("%.2f", sum));
            total_sum.setTextColor(context.getResources().getColor(R.color.red_number));
        }
        total_sum.setTextSize(20);
        total_sum.setTypeface(Typeface.DEFAULT_BOLD);
        total_sum.setPadding(10,0,0,0);

        TextView tags_sum_title = new TextView(context);
        tags_sum_title.setText(R.string.summary_tags_title);
        tags_sum_title.setTypeface(Typeface.DEFAULT_BOLD);
        tags_sum_title.setPadding(10,10,0,0);

        TextView tag_name;
        TextView tag_sum;
        LinearLayout tag_row;

        // add views to parent
        parent.addView(total_sum);
        parent.addView(total_sum_title);
        parent.addView(horiz_divider);
        parent.addView(tags_sum_title);

        // add tags
        for(int i = 0; i < tags.size(); i++) {
            tag_row = new LinearLayout(context);
            tag_row.setOrientation(LinearLayout.HORIZONTAL);
            tag_row.setPadding(10,0,10,0);

            tag_name = new TextView(context);
            tag_sum = new TextView(context);
            tag_sum.setPadding(10,0,0,0);

            tag_name.setText(tags.get(i).getName());
            if(tag_sums[i] >= 0) {
                tag_sum.setText(String.format("+%.2f", tag_sums[i]));
                tag_sum.setTextColor(context.getResources().getColor(R.color.green_number));
            }
            else {
                tag_sum.setText(String.format("%.2f", tag_sums[i]));
                tag_sum.setTextColor(context.getResources().getColor(R.color.red_number));
            }

            tag_row.addView(tag_name);
            tag_row.addView(tag_sum);
            parent.addView(tag_row);
        }

        return parent;
    }

    // Class to store sums for tags.
    // Contains also methods for sorting the lists
    // TODO: everything, now all is handled in getInflatedView() method
    private class TagSums {

        public TagSums() {};

    };


}
