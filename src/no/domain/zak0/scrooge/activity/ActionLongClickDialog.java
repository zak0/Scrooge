package no.domain.zak0.scrooge.activity;

import no.domain.zak0.R;
import no.domain.zak0.scrooge.dataclass.PoormanAction;
import no.domain.zak0.scrooge.utils.DatabaseHelper;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;


/* Class implements functionality for simpler dialogs used in the app.
 * 
 * 
 */
public class ActionLongClickDialog {
	
	private static final String TAG = "ActionLongClickDialog";
	private PoormanAction action;
	private Dialog dialog;
	private Context context;
	private DatabaseHelper db;
	
	public ActionLongClickDialog(Context context, PoormanAction action, DatabaseHelper db) {
		
		this.context = context;
		
		this.action = action;
		this.db = db;

	}
	
	public Dialog getDialog() {
		dialog = new Dialog(context);
		
		//dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        String title_string = "";
        if(action.getAmount() > 0)
            title_string = "+";
        title_string += String.format("%.2f", action.getAmount());
        title_string += " "+action.getToFrom();

        dialog.setTitle(title_string);

		dialog.setContentView(R.layout.dialog_action_longclick);
		Button edit_button = (Button) dialog.findViewById(R.id.button_action_longclick_edit);
		Button delete_button = (Button) dialog.findViewById(R.id.button_action_longclick_delete);
		
		delete_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

                // show a yes/no dialog
                AlertDialog.Builder dialog_builder = new AlertDialog.Builder(context);
                dialog_builder.setTitle(R.string.are_you_sure);
                dialog_builder.setMessage(R.string.delete_action_confirmation_msg);
                dialog_builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Remove action from account
                        action.getAccount().removeAction(action);

                        // Update account and remove action:
                        db.deleteAction(action);
                        db.updateAccount(action.getAccount());

                        ((PoormanActivity) context).refreshUi(true);
                        dialog.dismiss();
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
		
		edit_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Intent intent = new Intent(context, AddActionActivity.class);
				//intent.putExtra("action", action);
				//context.startActivity(intent);
                new ActionDialog(context, db, action).getDialog().show();
				dialog.dismiss();
			}
		});
		

		
		return dialog;
	}

}
