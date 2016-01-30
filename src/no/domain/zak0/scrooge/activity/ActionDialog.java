package no.domain.zak0.scrooge.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import no.domain.zak0.R;
import no.domain.zak0.scrooge.dataclass.PoormanAccount;
import no.domain.zak0.scrooge.dataclass.PoormanAction;
import no.domain.zak0.scrooge.utils.DatabaseHelper;

import java.sql.Date;
import java.util.Calendar;
import java.util.Vector;

/**
 * Created by jaakko on 8/10/13.
 */
public class ActionDialog {

    private Context context;
    private Dialog dlg;
    private DatabaseHelper db;
    private PoormanAction action;
    private PoormanAction action_before_edit;
    private static final String TAG = "ActionDialog";
    private boolean edit_flag;

    private RadioButton expense_radio;
    private RadioButton income_radio;
    private EditText tofrom_edittext;


    public ActionDialog(Context ct, DatabaseHelper db, PoormanAction act) {
        this.context = ct;
        this.db = db;
        this.action = act;
    }


    public Dialog getDialog() {
        dlg = new Dialog(context);
        dlg.setContentView(R.layout.dialog_action);

        if(action == null) {
            edit_flag = false;
            Log.d(TAG, "not in edit mode, creating a new action...");
            action = new PoormanAction();
            dlg.setTitle(R.string.add_transaction);
        }
        else {
            edit_flag = true;
            action_before_edit = new PoormanAction(action);
            dlg.setTitle(R.string.edit_transaction);
        }

        tofrom_edittext = (EditText) dlg.findViewById(R.id.editText_new_action_tofrom);

        Date curr_time = new Date(System.currentTimeMillis());
        if(edit_flag) curr_time = new Date(action.getTime());
        Calendar cal = Calendar.getInstance();

        // Get the UI elements
        final EditText new_amount = (EditText) dlg.findViewById(R.id.editText_new_action_amount);
        final EditText new_description = (EditText) dlg.findViewById(R.id.editText_new_action_descr);
        EditText new_date = (EditText) dlg.findViewById(R.id.editText_new_action_date);
        EditText new_time = (EditText) dlg.findViewById(R.id.editText_new_action_time);

        expense_radio = (RadioButton) dlg.findViewById(R.id.radioButton_new_action_expense);
        income_radio = (RadioButton) dlg.findViewById(R.id.radioButton_new_action_income);

        Button save_button = (Button) dlg.findViewById(R.id.button_save_action);
        if(!edit_flag) save_button.setText(R.string.add);

        Button cancel_button = (Button) dlg.findViewById(R.id.button_cancel_new_action);

        final DatePickerDialog date_dialog = new DatePickerDialog(context, 0, new NewActionOnDateSetListener(new_date, action, context), cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        final TimePickerDialog time_dialog = new TimePickerDialog(context, 0, new NewActionOnTimeSetListener(new_time, action, context), 0, 0, DateFormat.is24HourFormat(context));

        // Date and time fields default to current time and day in the system locale formats
        new_date.setText(DateFormat.getDateFormat(context).format(curr_time));
        new_time.setText(DateFormat.getTimeFormat(context).format(curr_time));

        // Populate accounts spinner
        populateAccountsSpinner();

        // Set current date and time to action
        if(!edit_flag) action.setTime(System.currentTimeMillis());

        // Fill fields with info from action to edit if in edit mode.
        if(edit_flag) {
            double amount = action.getAmount();

            if(action.getAmount() < 0) {
                expense_radio.setChecked(true);
                income_radio.setChecked(false);
                amount *= -1;
                //is_expense = true;
            }
            else {
                expense_radio.setChecked(false);
                income_radio.setChecked(true);
                //is_expense = false;
            }

            String amount_string = "";
            amount_string = String.format("%.2f", amount);
            new_amount.setText(amount_string);
            new_description.setText(action.getDescription());
            tofrom_edittext.setText(action.getToFrom());

            // Read and add tags from action to edit
            for(int i = 0; i < action.getTags().size(); i++) {
                LinearLayout tags_list = (LinearLayout) dlg.findViewById(R.id.linearLayout_new_action_tags);
                TextView tag_textview = new TextView(context);
                tag_textview.setText(action.getTags().get(i).getName());
                tags_list.addView(tag_textview);
            }
        }

        // Save button action is defined here
        save_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                double amount = 0;

                try {
                    amount = Double.parseDouble(new_amount.getText().toString());
                }
                catch (NumberFormatException ex) {
                    Toast.makeText(context, R.string.action_amount_not_set, Toast.LENGTH_LONG).show();
                    return;
                }

                if(expense_radio.isChecked()) amount = amount * -1.00;

                // Set data to PoorManAction object
                action.setAmount((float) amount);
                action.setDescription(new_description.getText().toString());
                action.setToFrom(tofrom_edittext.getText().toString());

                Log.d(TAG, "onCreate(): Save-button onClick(): expense: "+Boolean.toString(expense_radio.isChecked()));
                Log.d(TAG, "onCreate(): Save-button onClick(): amount="+Double.toString(amount));

                // Insert action into the database.
                if(!edit_flag) db.insertAction(action);
                else db.updateAction(action); // ...or update it if in edit mode.

                // Make changes to selected account
                if(edit_flag) {
                    if(action_before_edit.getAccount().getId() == action.getAccount().getId()) {
                        action.getAccount().removeAction(action_before_edit);
                    }
                    else {
                        action_before_edit.getAccount().removeAction(action_before_edit);
                    }
                }
                action.getAccount().addAction(action);
                db.updateAccount(action.getAccount());
                if(edit_flag) {
                    if(action_before_edit.getAccount().getId() != action.getAccount().getId()) {
                        db.updateAccount(action_before_edit.getAccount());
                    }
                }

                // refresh main actitivy UI
                ((PoormanActivity) context).refreshUi(true);

                dlg.dismiss();
            }

        });

        // Cancel button action
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
            }
        });


        // RadioButton actions are defined here
        expense_radio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
            boolean isChecked) {
                if(isChecked) {
                    income_radio.setChecked(false);
                    tofrom_edittext.setHint(R.string.to);
                }
            }
        });
        income_radio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
            boolean isChecked) {
                if(isChecked) {
                    expense_radio.setChecked(false);
                    tofrom_edittext.setHint(R.string.from);
                }
            }
        });

        // Date field actions
        new_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!date_dialog.isShowing()) date_dialog.show();
            }

        });
        new_date.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus && !date_dialog.isShowing()) date_dialog.show();
            }

        });

        // Time field actions
        new_time.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(!time_dialog.isShowing()) time_dialog.show();
            }
        });
        new_time.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus && !time_dialog.isShowing()) time_dialog.show();
            }
        });

        // Add tags -button
        Button add_tag_button = (Button) dlg.findViewById(R.id.button_new_action_add_tag);
        add_tag_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                LinearLayout tag_layout = (LinearLayout) dlg.findViewById(R.id.linearLayout_new_action_tags);
                NewActionTagSelectorDialog tag_dialog = new NewActionTagSelectorDialog(context, action, db, tag_layout);
                tag_dialog.getDialog().show();
            }
        });


        // Browse targets button action
        Button browse_targets_button = (Button) dlg.findViewById(R.id.button_browse_targets);
        browse_targets_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ToFromSelectorDialog dlg = new ToFromSelectorDialog(context, action, db, expense_radio.isChecked(), ActionDialog.this);
                dlg.getDialog().show();
            }
        });

        return dlg;
    }


    // Populates account selector spinner with all the accounts in the database.
    private void populateAccountsSpinner() {
        Spinner accounts_spinner = (Spinner) dlg.findViewById(R.id.spinner_new_action_account);
        final Vector<PoormanAccount> accounts = db.getAllAccounts(DatabaseHelper.SORT_BY_IS_DEFAULT);

        String names[] = new String[accounts.size()];
        for(int i = 0; i < accounts.size(); i++) {
            names[i] = accounts.get(i).getName();
        }

        accounts_spinner.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, names));

        accounts_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                //AddActionActivity.this.selected_account = accounts.get(arg2);
                action.setAccount(accounts.get(arg2));
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        // If in edit mode, set account from action as selected
        if(edit_flag && action.getAccount() != null) {
            for(int i = 0; i < accounts.size(); i++) {
                if(accounts.get(i).getId() == action.getAccount().getId()) {
                    accounts_spinner.setSelection(i);
                    action.setAccount(accounts.get(i));
                    break;
                }
            }
        }
    }


    public void setTarget(String target) {
        EditText target_edit = (EditText) dlg.findViewById(R.id.editText_new_action_tofrom);
        target_edit.setText(target);
    }

}
