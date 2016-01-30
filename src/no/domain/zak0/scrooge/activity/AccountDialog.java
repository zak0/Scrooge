package no.domain.zak0.scrooge.activity;

import no.domain.zak0.R;
import no.domain.zak0.scrooge.dataclass.PoormanAccount;
import no.domain.zak0.scrooge.utils.DatabaseHelper;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

// TODO edit current balance

public class AccountDialog {
	
	private Context context;
	private Dialog dialog;
	private PoormanAccount account;
	private DatabaseHelper db;
	
	private EditText acc_name;
	private EditText acc_descr;
	private EditText acc_initial_saldo;
	private CheckBox acc_is_default;
	
	private boolean edit_mode;
	
	public AccountDialog(Context context, DatabaseHelper db, PoormanAccount account) {
		this.context = context;
		this.account = account;
		this.db = db;
		
		if(account == null) edit_mode = false;
		else edit_mode = true;
	}
	
	public Dialog getDialog() {
		dialog = new Dialog(context);
		if(edit_mode) dialog.setTitle(R.string.edit_account);
		else dialog.setTitle(R.string.new_account);
		
		dialog.setContentView(R.layout.dialog_account);
		
		Button save_button = (Button) dialog.findViewById(R.id.button_new_account_save);
		Button cancel_button = (Button) dialog.findViewById(R.id.button_new_account_cancel);
		
		acc_name = (EditText) dialog.findViewById(R.id.editText_new_account_name);
		acc_descr = (EditText) dialog.findViewById(R.id.editText_new_account_description);
		acc_initial_saldo = (EditText) dialog.findViewById(R.id.editText_new_account_saldo);
		acc_is_default = (CheckBox) dialog.findViewById(R.id.checkBox_new_account_is_default);
			
		// If account was provided in constructor (e.g. dialog is in edit mode),
		// fill fields with info from this account.
		if(edit_mode) {
			acc_name.setText(account.getName());
			acc_descr.setText(account.getDescription());

			// in edit mode initial saldo field is used to change current account balance
            acc_initial_saldo.setText(String.format("%.2f", account.getSaldo()));
			if(account.getIsDefault()) acc_is_default.setChecked(true);
		}
		
		save_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				if(acc_name.getText().toString().equals("")) {
					Toast.makeText(context, R.string.account_must_have_name, Toast.LENGTH_LONG).show();
					return;
				}
				else if(!edit_mode && acc_initial_saldo.getText().toString().equals("")
						|| acc_initial_saldo.getText().toString().equals(".")) {
					Toast.makeText(context, R.string.account_must_have_saldo, Toast.LENGTH_LONG).show();
					return;
				}
				
				// Compile new PoormanAccount
				if(!edit_mode) account = new PoormanAccount();
				account.setName(acc_name.getText().toString());
				account.setDescription(acc_descr.getText().toString());
				if(!edit_mode) {
                    account.setInitialSaldo(Double.parseDouble(acc_initial_saldo.getText().toString()));
				}
                account.setSaldo(Double.parseDouble(acc_initial_saldo.getText().toString()));
				account.setIsDefault(acc_is_default.isChecked());
				
				if(edit_mode) db.updateAccount(account);
				else db.insertAccount(account);

                ((PoormanActivity) context).refreshUi(true);
				dialog.dismiss();
			}
			
		});
		
		
		cancel_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
			
		});
		
		
		return dialog;
	}
}
