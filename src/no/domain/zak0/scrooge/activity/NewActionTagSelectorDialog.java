package no.domain.zak0.scrooge.activity;

import java.util.Vector;

import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import no.domain.zak0.R;
import no.domain.zak0.scrooge.dataclass.PoormanAction;
import no.domain.zak0.scrooge.dataclass.PoormanTag;
import no.domain.zak0.scrooge.utils.DatabaseHelper;

public class NewActionTagSelectorDialog {
	
	private static final String TAG = "TagSelectorDialog";
	
	private Dialog dialog;
	private Context context;
	private DatabaseHelper db;
	private PoormanAction action;
	private LinearLayout tag_layout;
	private Vector<PoormanTag> selected_tags;
	
	public NewActionTagSelectorDialog(Context context, PoormanAction act, DatabaseHelper db, LinearLayout tag_layout) {
		this.context = context;
		this.db = db;
		this.action = act;
		this.tag_layout = tag_layout;
		
		dialog = new Dialog(context);
		selected_tags = new Vector<PoormanTag>();
	}
	
	public Dialog getDialog() {
		dialog.setTitle(R.string.add_tag);
		dialog.setContentView(R.layout.dialog_tag_selector);
		
		// Load all tags from the database
		refreshTags(db.getAllTags());
		
		// Stuff for adding a new tag
		final EditText new_tag_name = (EditText) dialog.findViewById(R.id.editText_tag_selector_tag);
		Button new_tag_button = (Button) dialog.findViewById(R.id.button_tag_selector_new_tag);
		
		new_tag_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String name = new_tag_name.getText().toString();
				
				PoormanTag new_tag = new PoormanTag();
				new_tag.setName(name);
				if(db.insertTag(new_tag)) {
					Toast.makeText(context, R.string.tag_created, Toast.LENGTH_LONG).show();
					// refresh tags list so that new tag is also visible
					refreshTags(db.getAllTagsThatStartWith(name));
				}
				else {
					Toast.makeText(context, R.string.tag_exists, Toast.LENGTH_LONG).show();
				}
			}
			
		});
		
		// "Dynamic search" for tags
		new_tag_name.addTextChangedListener(new TextWatcher() {
	
			@Override
			public void afterTextChanged(Editable arg0) {
				// Load all tags that begin with entered text from the db
				// and refresh tags list.
				refreshTags(db.getAllTagsThatStartWith(arg0.toString()));
				
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start,
					int count, int after) {}
			@Override
			public void onTextChanged(CharSequence s, int start,
					int before, int count) {}
			
		});
		
		// Save and cancel button listeners
		Button save_tags_button = (Button) dialog.findViewById(R.id.button_tag_selector_save);
		save_tags_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.d(TAG, Integer.toString(selected_tags.size()) + " tags selected");
				
				action.clearTags();
				
				// Clear the list of tags in add action activity tag list.
				tag_layout.removeAllViews();
				
				// Add tags to the transaction
				for(int i = 0; i < selected_tags.size(); i++) {
					//// this the edittext to be added to the new action activity tag list
					TextView new_tag_edittext = new TextView(context);
					new_tag_edittext.setText(selected_tags.get(i).getName());
					tag_layout.addView(new_tag_edittext);
					
					action.addTag(selected_tags.get(i));
				}
				
				dialog.dismiss();
			}
		});
		
		Button cancel_button = (Button) dialog.findViewById(R.id.button_tag_selector_cancel);
		cancel_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		
		return dialog;
	}
	
	// Refreshes the list of tags to contain tags given in parameter vector.
	private void refreshTags(Vector<PoormanTag> tags) {
		
		// Fill the scrollview with all existing tags
		LinearLayout tags_parent = (LinearLayout) dialog.findViewById(R.id.linearLayout_tag_selector_tags);
		CheckBox tag_name;
		
		tags_parent.removeAllViews();
		
		for(int i = 0; i < tags.size(); i++) {
			tag_name = new CheckBox(context);
			//tag_name.setPadding(10, 10, 10, 0);
			tag_name.setTextSize(16);
			tag_name.setText(tags.get(i).getName());
			
			// Check if tag is already selected for the transaction
			for(int j = 0; j < action.getTags().size(); j++) {
				if(action.getTags().get(j).getId() == tags.get(i).getId()) {
					tag_name.setChecked(true);
					selected_tags.add(tags.get(i));
					Log.d(TAG, "action tag selector - tag checked");	
				}
			}
			
			for(int k = 0; k < selected_tags.size(); k++) {
				if(selected_tags.get(k).getId() == tags.get(i).getId()) {
					tag_name.setChecked(true);
					//selected_tags.add(tags.get(i));
					Log.d(TAG, "selected tag selector - tag checked");
				}
			}
			
			tags_parent.addView(tag_name);					
			tag_name.setOnCheckedChangeListener(new TagSelectorOnCheckedChangeListener(tags.get(i), selected_tags));
		}
	}
}
