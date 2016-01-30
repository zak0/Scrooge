package no.domain.zak0.scrooge.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;

import no.domain.zak0.R;
import no.domain.zak0.scrooge.dataclass.PoormanAccount;
import no.domain.zak0.scrooge.utils.DatabaseHelper;

public class AccountLongClickDialog {

	private static final String TAG = "AccountLongClickDialog";
	private PoormanAccount account;
	private Dialog dialog;
	private Context context;
	private DatabaseHelper db;
	
	public AccountLongClickDialog(Context ct, DatabaseHelper db, PoormanAccount acc) {
		this.account = acc;
		this.context = ct;
		this.db = db;
	}
	
	public Dialog getDialog() {
		dialog = new Dialog(context);
		//dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setTitle(account.getName());
		dialog.setContentView(R.layout.dialog_account_longclick);
		
		Button edit_button = (Button) dialog.findViewById(R.id.button_account_longclick_edit);
		Button delete_button = (Button) dialog.findViewById(R.id.button_account_longclick_delete);
		Button default_button = (Button) dialog.findViewById(R.id.button_account_longclick_set_as_default);
		
		edit_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AccountDialog dlg = new AccountDialog(context, db, account);
				dlg.getDialog().show();
				dialog.dismiss();
			}
		});
		
		delete_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

                // show a yes/no dialog
                AlertDialog.Builder dialog_builder = new AlertDialog.Builder(context);
                dialog_builder.setTitle(R.string.delete_account);
                dialog_builder.setMessage(R.string.delete_account_confirmation_msg);
                dialog_builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        db.deleteAccount(account);
                        dialog.dismiss();
                        ((PoormanActivity) context).refreshUi(true);
                    }
                });
                dialog_builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //nothing required to be done here
                    }
                });

                dialog_builder.create().show();

			}
		});
		
		default_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				db.setAccountAsDefault(account);
                ((PoormanActivity) context).refreshUi(true);
				dialog.dismiss();
			}
		});
		
		return dialog;
	}
}
