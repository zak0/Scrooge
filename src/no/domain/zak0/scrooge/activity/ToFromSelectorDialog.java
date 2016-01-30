package no.domain.zak0.scrooge.activity;

import java.util.Vector;

import no.domain.zak0.R;
import no.domain.zak0.scrooge.dataclass.PoormanAction;
import no.domain.zak0.scrooge.utils.DatabaseHelper;

import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ToFromSelectorDialog {

	private Dialog dialog;
	private Context context;
	private PoormanAction action;
    private ActionDialog parent_dlg;
	private DatabaseHelper db;
	private TextView target_textview;
	private boolean is_expense; // flag for telling whether transaction is income or expense
	
	public ToFromSelectorDialog(Context context, PoormanAction action, DatabaseHelper db, boolean is_expense, ActionDialog parent_dlg) {
		this.context = context;
		this.action = action;
		this.db = db;
		this.is_expense = is_expense;
		this.parent_dlg = parent_dlg;
	}
	
	public Dialog getDialog() {
		dialog = new Dialog(context);
		
		dialog.setTitle(R.string.previous_targets);
		dialog.setContentView(R.layout.dialog_tofrom_selector);
		
		
		//refreshList(db.getTargets("", is_expense));
        refreshList(db.getTargetsAfter(System.currentTimeMillis() - (30L*24L*60L*60L*1000L), is_expense));
		
		// "Dynamic search" implementation
		EditText name_edittext = (EditText) dialog.findViewById(R.id.editText_tofrom_selector_name);
		name_edittext.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				refreshList(db.getTargets(s.toString(), is_expense));	
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {	
			}
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {	
			}
		});

		return dialog;
	}
	
	private void refreshList(Vector<String> vect) {
		LinearLayout tofrom_parent = (LinearLayout) dialog.findViewById(R.id.linearLayout_tofrom_selector_list_parent);
		
		tofrom_parent.removeAllViews();
		
		for(int i = 0; i < vect.size(); i++) {
			target_textview = new TextView(context);
			target_textview.setText(vect.get(i));
			target_textview.setTextSize(24);
			target_textview.setPadding(10, 25, 25, 0);
			target_textview.setOnClickListener(new ToFromOnClickListener(vect.get(i), parent_dlg, dialog));
			
			tofrom_parent.addView(target_textview);
		}
	}
	
}
