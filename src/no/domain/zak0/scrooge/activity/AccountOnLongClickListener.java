package no.domain.zak0.scrooge.activity;

import no.domain.zak0.scrooge.dataclass.PoormanAccount;
import no.domain.zak0.scrooge.utils.DatabaseHelper;

import android.content.Context;
import android.view.View;
import android.view.View.OnLongClickListener;

public class AccountOnLongClickListener implements OnLongClickListener {

	private DatabaseHelper db;
	private Context context;
	private PoormanAccount account;
	
	public AccountOnLongClickListener(DatabaseHelper db, Context ct, PoormanAccount acc) {
		this.db = db;
		this.context = ct;
		this.account = acc;
	}
	
	@Override
	public boolean onLongClick(View arg0) {
		AccountLongClickDialog dlg = new AccountLongClickDialog(context, db, account);
		dlg.getDialog().show();
		return false;
	}

}
