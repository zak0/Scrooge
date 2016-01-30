package no.domain.zak0.scrooge.activity;

import no.domain.zak0.scrooge.dataclass.PoormanAction;
import no.domain.zak0.scrooge.utils.DatabaseHelper;

import android.content.Context;
import android.view.View;
import android.view.View.OnLongClickListener;

public class ActionOnLongClickListener implements OnLongClickListener {

	private DatabaseHelper db;
	private Context context;
	private PoormanAction action;
	
	public ActionOnLongClickListener(Context con, DatabaseHelper db_helper, PoormanAction act) {
		this.db = db_helper;
		this.context = con;
		this.action = act;
	}
	
	@Override
	public boolean onLongClick(View arg0) {
		
		ActionLongClickDialog dlg = new ActionLongClickDialog(context, action, db);
		dlg.getDialog().show();
		
		return false;
	}

}
