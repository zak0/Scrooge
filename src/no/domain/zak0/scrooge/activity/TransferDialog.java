package no.domain.zak0.scrooge.activity;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import no.domain.zak0.R;
import no.domain.zak0.scrooge.dataclass.PoormanAccount;
import no.domain.zak0.scrooge.dataclass.PoormanAction;
import no.domain.zak0.scrooge.utils.DatabaseHelper;

import java.util.Vector;

/**
 * Created by jaakko on 8/2/13.
 */
public class TransferDialog {

    private static final String TAG = "TransferDialog";

    private Context context;
    private DatabaseHelper db;
    private final Dialog dlg;
    private PoormanAccount from_account;
    private PoormanAccount to_account;

    public TransferDialog(Context ct, DatabaseHelper db) {
        this.context = ct;
        this.db = db;
        from_account = null;
        to_account = null;
        dlg = new Dialog(context);
    }

    public Dialog getDialog() {
        //final Dialog dlg = new Dialog(context);
        dlg.setTitle(R.string.transfer);
        dlg.setContentView(R.layout.dialog_transfer);

        populateSpinners();

        Button cancel_button = (Button) dlg.findViewById(R.id.button_transfer_cancel);
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dlg.dismiss();
            }
        });

        Button save_button = (Button) dlg.findViewById(R.id.button_transfer_save);
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox save_as_action_checkbox = (CheckBox) dlg.findViewById(R.id.checkBox_transger_save_as_action);
                EditText amount_edittext = (EditText) dlg.findViewById(R.id.editText_transfer_amount);
                double amount = 0.0;

                // Checks for proper and required values
                if(to_account == null || from_account == null || to_account.getId() == from_account.getId()) {
                    Toast.makeText(context, R.string.invalid_transfer_accounts, Toast.LENGTH_LONG).show();
                    return;
                }

                try {
                    amount = Double.parseDouble(amount_edittext.getText().toString());
                }
                catch(NumberFormatException e) {
                    Toast.makeText(context, R.string.invalid_transfer_amount, Toast.LENGTH_LONG).show();
                    return;
                }

                // Entered values should be fine if we got this far.
                // Create transactions for transfer
                PoormanAction from_action = new PoormanAction();
                from_action.setAccount(from_account);
                from_action.setAmount((float) (amount * (-1.0)));
                from_action.setDescription(context.getResources().getString(R.string.transfer_action_description_to_account)+" "+to_account.getName());
                from_action.setTime(System.currentTimeMillis());
                from_action.setToFrom(to_account.getName());

                PoormanAction to_action = new PoormanAction();
                to_action.setAccount(to_account);
                to_action.setAmount((float) amount);
                to_action.setDescription(context.getResources().getString(R.string.transfer_action_description_from_account)+" "+from_account.getName());
                to_action.setTime(System.currentTimeMillis());
                to_action.setToFrom(from_account.getName());

                from_account.addAction(from_action);
                to_account.addAction(to_action);

                // update accounts
                db.updateAccount(from_account);
                db.updateAccount(to_account);

                // add transactions, if so desired
                if(save_as_action_checkbox.isChecked()) {
                    db.insertAction(from_action);
                    db.insertAction(to_action);
                }

                // refresh list in main activity
                ((PoormanActivity) context).refreshUi(true);
                dlg.dismiss();

            }
        });

        return dlg;
    }

    // Populates account selector spinner with all the accounts in the database.
    private void populateSpinners() {
        Spinner from_spinner = (Spinner) dlg.findViewById(R.id.spinner_transfer_from);
        Spinner to_spinner = (Spinner) dlg.findViewById(R.id.spinner_transfer_to);

        final Vector<PoormanAccount> accounts = db.getAllAccounts(DatabaseHelper.SORT_BY_IS_DEFAULT);

        String names[] = new String[accounts.size()];
        for(int i = 0; i < accounts.size(); i++) {
            names[i] = accounts.get(i).getName();
        }

        from_spinner.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, names));
        to_spinner.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, names));

        from_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                from_account = accounts.get(arg2);
                Log.d(TAG, "populateSpinners(): from_account set to "+accounts.get(arg2).getName());
                //AddActionActivity.this.action.setAccount(accounts.get(arg2));
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        to_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                to_account = accounts.get(i);
                Log.d(TAG, "populateSpinners(): to_account set to "+accounts.get(i).getName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }
}
