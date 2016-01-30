package no.domain.zak0.scrooge.activity;

import java.util.Vector;

import no.domain.zak0.scrooge.dataclass.PoormanTag;

import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class TagSelectorOnCheckedChangeListener implements OnCheckedChangeListener{

	private PoormanTag tag;
	private Vector<PoormanTag> selected_tags;
	
	public TagSelectorOnCheckedChangeListener(PoormanTag tag, Vector<PoormanTag> selected_tags) {
		this.tag = tag;
		this.selected_tags = selected_tags;
	}
	
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if(isChecked) {
			selected_tags.add(tag);
		}
		else {
			for(int i = 0; i < selected_tags.size(); i++) {
				if(selected_tags.get(i).getId() == tag.getId()) {
					selected_tags.remove(selected_tags.get(i));
				}
			}			
		}
	}
}
