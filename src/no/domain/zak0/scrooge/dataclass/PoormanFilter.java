package no.domain.zak0.scrooge.dataclass;

import java.io.Serializable;
import java.util.Vector;

/* Container class for filter settings 
 * 
 * 
 */
public class PoormanFilter implements Serializable {

	// filter type constants
	public static final int FILTER_TIME_SPAN = 1;
	public static final int FILTER_TAGS = 2;
	public static final int FILTER_ACCOUNTS = 3;
	public static final int FILTER_MAX_COUNT = 4;
	
	private Vector<Integer> active_filters;
	private long time_span_before;
	private long time_span_after;
	private Vector<PoormanTag> tags;
	private Vector<PoormanAccount> accounts;
	private int max_count;
	
	public PoormanFilter() {
		active_filters = new Vector<Integer>();
		tags = new Vector<PoormanTag>();
		accounts = new Vector<PoormanAccount>();
		time_span_after = 0L;
		time_span_before = 0L;
		max_count = 0;
	}
	
	public Vector<Integer> getActiveFilters() { return active_filters; }
	public void setActiveFilters(Vector<Integer> filters) { this.active_filters = filters; }
	
	public void enableFilter(int filter) {
		for(int i = 0; i < active_filters.size(); i++) {
			// check if filter already is active
			if(active_filters.get(i) == filter) return;
		}
		
		// goes here if filter not already active
		active_filters.add(filter);
	}
	
	public void disableFilter(int filter) {
		for(int i = 0; i < active_filters.size(); i++) {
			if(active_filters.get(i) == filter) active_filters.removeElementAt(i);
		}
	}

    // returns true/false according to if filter is enabled or not
    public boolean isFilterEnabled(int filter) {
        for(int i = 0; i < active_filters.size(); i++) {
            if(active_filters.get(i) == filter) return true;
        }
        return false;
    }

	public long getFilterTimeSpanAfter() { return time_span_after; }
	public long getFilterTimeSpanBefore() { return time_span_before; }
	
	public void setFilterTimeSpan(long after, long before) {
		this.time_span_after = after;
		this.time_span_before = before;
	}
	
	public Vector<PoormanTag> getFilterTags() { return tags; }
	public void setFilterTags(Vector<PoormanTag> tags) { this.tags = tags;	}
	
	public Vector<PoormanAccount> getFilterAccounts() { return accounts; }
	public void setFilterAccounts(Vector<PoormanAccount> accs) { this.accounts = accs; }
    public void addFilterAccount(PoormanAccount acc) { this.accounts.add(acc); }
	
	public int getFilterMaxCount() { return max_count; }
	public void setFilterMaxCount(int count) { this.max_count = count; }
	
}
