package no.domain.zak0.scrooge.activity;

import java.util.Vector;

import no.domain.zak0.R;
import no.domain.zak0.scrooge.dataclass.PoormanAction;
import no.domain.zak0.scrooge.utils.DatabaseHelper;

//import android.R;
import android.content.Context;
import android.graphics.Typeface;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class ActionListInflater {

	private Context context;
	private DatabaseHelper db;
	
	private int divider_height = 2;
	//private int divider_color = 0xff0099CC;
	private int COLOR_GRAY = 0x44000000;
	private int COLOR_HOLO_GREEN = 0xff669900;
	private int COLOR_HOLO_RED = 0xffCC0000;
	
	private String TAG = "ActionListInflater";
	
	public ActionListInflater(Context con, DatabaseHelper db) {
		this.context = con;
		this.db = db;
	}

	
	/* Returns a View containing all the actions in the parameter vector */
	public View getInflatedView(Vector<PoormanAction> actions) {
		int i = 0;
		
		// UI element initializations
		LinearLayout parent = new LinearLayout(context);
		LinearLayout top_row;
		LinearLayout left_cell;
		LinearLayout right_cell;
		LinearLayout bottom_row;
		TextView act_amount;
		TextView act_tofrom;
		TextView act_description;
		TextView act_datetime;
		TextView act_tags;
		TextView act_accountname;
		View horiz_divider;
		
		String tags;
		
		parent.setOrientation(LinearLayout.VERTICAL);
		parent.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		
		// iterate through all the actions in the parameter vector
		for(i=0; i < actions.size(); i++) {
			
			// Build date and time strings according to current system locale
			String datetime;
			datetime = DateFormat.getDateFormat(context).format(actions.get(i).getTime()) + " ";
			datetime = datetime + DateFormat.getTimeFormat(context).format(actions.get(i).getTime());
			
			left_cell = new LinearLayout(context);
			left_cell.setOrientation(LinearLayout.VERTICAL);
			left_cell.setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));
			left_cell.setPadding(10, 5, 0, 0);
			
			right_cell = new LinearLayout(context);
			right_cell.setOrientation(LinearLayout.VERTICAL);
			right_cell.setGravity(Gravity.RIGHT);
			right_cell.setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));
			right_cell.setPadding(0, 5, 10, 0);
			
			top_row = new LinearLayout(context);
			top_row.setOrientation(LinearLayout.HORIZONTAL);
			top_row.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			
			bottom_row = new LinearLayout(context);
			bottom_row.setOrientation(LinearLayout.VERTICAL);
			bottom_row.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			bottom_row.setPadding(10, 0, 10, 5);
			
			horiz_divider = new View(context);
			horiz_divider.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,divider_height));
			horiz_divider.setBackgroundColor(COLOR_GRAY);
			
			act_amount = new TextView(context);
			act_tofrom = new TextView(context);
			act_description = new TextView(context);
			act_datetime = new TextView(context);
			
			act_amount.setTextSize(20);
			act_amount.setTypeface(Typeface.DEFAULT_BOLD);
			act_amount.setText(actions.get(i).getAmountString());
			if(actions.get(i).getAmount() >= 0)	act_amount.setTextColor(COLOR_HOLO_GREEN);
			else act_amount.setTextColor(COLOR_HOLO_RED);
			
			act_tofrom.setTypeface(Typeface.DEFAULT_BOLD);
			act_tofrom.setText(actions.get(i).getToFrom());
			
			act_description.setText(actions.get(i).getDescription());
			
			act_accountname = new TextView(context);
			act_accountname.setText(actions.get(i).getAccount().getName());
			act_accountname.setGravity(Gravity.RIGHT);
			
			//act_datetime.setText(actions.get(i).getTimeString());
			act_datetime.setText(datetime);
			act_datetime.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1));
			act_datetime.setGravity(Gravity.RIGHT);
			
			// Compile tags string
			tags = "";
			for(int j = 0; j < actions.get(i).getTags().size(); j++) {
				if(!tags.equals("")) tags += ", ";
				tags += actions.get(i).getTags().get(j).getName();
			}
			act_tags = new TextView(context);
			act_tags.setText(tags);
			act_tags.setBackgroundResource(R.color.new_action_tags_bg);
			
			left_cell.addView(act_amount);
			left_cell.addView(act_tofrom);
			//bottom_row.addView(act_tofrom);
			bottom_row.addView(act_description);
			bottom_row.addView(act_tags);
						
			right_cell.addView(act_datetime);
			right_cell.addView(act_accountname);
			
			top_row.addView(left_cell);
			top_row.addView(right_cell);
			
			top_row.setOnLongClickListener(new ActionOnLongClickListener(context, db, actions.get(i)));
			bottom_row.setOnLongClickListener(new ActionOnLongClickListener(context, db, actions.get(i)));
			
			parent.addView(top_row);
			parent.addView(bottom_row);
			parent.addView(horiz_divider);
		}
		
		return parent;
	}
	
	
	
	
}
