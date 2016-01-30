package no.domain.zak0.scrooge.activity;

import android.app.Dialog;
import android.view.View;
import android.view.View.OnClickListener;

public class ToFromOnClickListener implements OnClickListener {

	private String target;
	private ActionDialog parent_dlg;
	private Dialog dlg;
	
	public ToFromOnClickListener(String target, ActionDialog action_dlg, Dialog dlg) {
		this.target = target;
		this.parent_dlg = action_dlg;
		this.dlg = dlg;
	}
	
	@Override
	public void onClick(View v) {
		parent_dlg.setTarget(target);
		dlg.dismiss();
	}

}
