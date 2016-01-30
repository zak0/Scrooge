package no.domain.zak0.scrooge.activity;

import java.util.Calendar;
import java.util.Locale;
import java.util.Vector;

import no.domain.zak0.R;
import no.domain.zak0.scrooge.dataclass.PoormanAccount;
import no.domain.zak0.scrooge.dataclass.PoormanAction;
import no.domain.zak0.scrooge.dataclass.PoormanFilter;
import no.domain.zak0.scrooge.dataclass.PoormanTag;
import no.domain.zak0.scrooge.utils.DatabaseHelper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.content.Context;
import android.widget.Toast;

// Todos here are general todos for the entire project
// DONE: detaulf filter (e.g. last 7 or 30 days)
// PARTIALLY DONE: load data from DB only when filter changes and read from memory to smoothen the app up...
// TODO: addition to above: summary page still to do (tags and total, variables are created to PoormanActivity...)
// TODO: recurring transactions
// TODO: statistics (weekly, monthly, annual, ...)
// DONE: transaction addition from activity to dialog
// DONE: lock orientation to portrait
// DONE: fix bug where default filter is not set to the end of today

public class PoormanActivity extends FragmentActivity {

	SectionsPagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;

	private DatabaseHelper db; // database handler
    private PoormanFilter filter; // filter for transaction and by-tag views

    private static final String TAG = "PoormanActivity";

    // a bit ugly using public statics, but it works...
    public static View account_list_parent;
    public static View action_list_parent;
    public static View summary_list_parent;

    // 20130824 next few variables added in attempt to reduce UI lag
    // data to display in the UI
    private double total_of_active_actions;
    private Vector<PoormanAction> active_actions; // actions tab
    private Vector<PoormanAccount> active_accounts; // accounts tab
    private Vector<PoormanTag> active_tags; // summary tab


    private static Context ct;
    private static int COLOR_GRAY = 0x44000000;
    private static int COLOR_HOLO_GREEN = 0xff669900;
    private static int COLOR_HOLO_RED = 0xffCC0000;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);

        ct = this;

		db = new DatabaseHelper(this, DatabaseHelper.DB_NAME, null, DatabaseHelper.DB_VERSION);
		db.openDb();

        // restore filter from Intent if provided
        Intent intent = getIntent();
        filter = (PoormanFilter) intent.getSerializableExtra("filter");
        if(filter == null) {
            Log.d(TAG, "onCreate() : creating a new filter");
            filter = new PoormanFilter();

            // set default filter to last 30 days
            filter.enableFilter(PoormanFilter.FILTER_TIME_SPAN);
            Calendar end_cal = Calendar.getInstance();
            end_cal.set(Calendar.HOUR_OF_DAY, 23);
            end_cal.set(Calendar.MINUTE, 59);
            end_cal.set(Calendar.SECOND, 59);
            end_cal.set(Calendar.MILLISECOND, 999);

            Calendar start_cal = Calendar.getInstance();
            start_cal.setTimeInMillis(System.currentTimeMillis() - (30L*24L*60L*60L*1000L));
            start_cal.set(Calendar.HOUR_OF_DAY, 0);
            start_cal.set(Calendar.MINUTE, 0);
            start_cal.set(Calendar.SECOND, 0);
            start_cal.set(Calendar.MILLISECOND, 0);

            filter.setFilterTimeSpan(start_cal.getTimeInMillis(), end_cal.getTimeInMillis());
        }
		
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), db, filter);

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
    	mViewPager.setAdapter(mSectionsPagerAdapter);

        // call refreshUi(). This updates views with data.
        refreshUi(true);

	}

    protected void onResume() {
		super.onResume();
		//mViewPager.invalidate();
        if(db == null) {
            db = new DatabaseHelper(this, DatabaseHelper.DB_NAME, null, DatabaseHelper.DB_VERSION);
        }
		db.openDb();
        refreshUi(false);
	}

    protected void onPause() {
    	super.onPause();
    	db.closeDb();
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
            case R.id.menu_new_action:
                new ActionDialog(this, db, null).getDialog().show();

                //Intent add_action_intent = new Intent(PoormanActivity.this, AddActionActivity.class);
                //add_action_intent.putExtra("filter", filter);
                //PoormanActivity.this.startActivity(add_action_intent);
                break;
            case R.id.menu_new_account:
                new AccountDialog(this, db, null).getDialog().show();
                break;
            case R.id.menu_filter:
                new FilterDialog(this, db, filter).getDialog().show();
                break;
            case R.id.menu_backup_restore:
                new BackupRestoreDialog(this, db).getDialog().show();
                break;
            case R.id.menu_export:
                Toast.makeText(this, "Feature not yet implemented.", Toast.LENGTH_LONG).show();
                break;
		}
		
		return super.onOptionsItemSelected(item);
	}


    // 20130810: change to include parameter for which fragment to refresh, param -1 refreshes all
    // 20130810: when no parameter, actions are refreshed from the database
    // 20130824: added parameter for refreshing data from database
    public void refreshUi(boolean refreshData) {
        if(refreshData) {
            Log.d(TAG, "refreshUI() - refreshing data");
            active_actions = db.getFilteredActions(filter);
            active_accounts = db.getAllAccounts(DatabaseHelper.SORT_BY_IS_DEFAULT);
            // get accounta balance chance in last 7 and 30 days
            for(int i = 0; i < active_accounts.size(); i++) {
                db.getAccountRecentChanges(active_accounts.get(i));
            }
        }
        refreshUi(-1);
    }
    public void refreshUi(int fragment) {
        Log.d(TAG, "refreshUi(), fragment parameter: "+Integer.toString(fragment));

        if(account_list_parent != null && (fragment == PoormanSectionFragment.FRAGMENT_ACCOUNTS || fragment == -1)) {
            Log.d(TAG, "refreshUi(), refreshing fragment "+Integer.toString(PoormanSectionFragment.FRAGMENT_ACCOUNTS));
            ((LinearLayout) account_list_parent).removeAllViews();
            ((LinearLayout) account_list_parent).addView(new AccountListInflater(this, db).getInflatedView(active_accounts));
        }

        if(action_list_parent != null && (fragment == PoormanSectionFragment.FRAGMENT_ACTIONS || fragment == -1)) {
            Log.d(TAG, "refreshUi(), refreshing fragment "+Integer.toString(PoormanSectionFragment.FRAGMENT_ACTIONS));
            ((LinearLayout) action_list_parent).removeAllViews();
            ((LinearLayout) action_list_parent).addView(new FilterInfoInflater(this, filter).getInflatedView());
            //((LinearLayout) action_list_parent).addView(new ActionListInflater(this, db).getInflatedView(db.getFilteredActions(filter)));
            ((LinearLayout) action_list_parent).addView(new ActionListInflater(this, db).getInflatedView(active_actions));
        }

        if(summary_list_parent != null && (fragment == PoormanSectionFragment.FRAGMENT_SUMMARY || fragment == -1)) {
            Log.d(TAG, "refreshUi(), refreshing fragment "+Integer.toString(PoormanSectionFragment.FRAGMENT_SUMMARY));
            ((LinearLayout) summary_list_parent).removeAllViews();
            ((LinearLayout) summary_list_parent).addView(new FilterInfoInflater(this, filter).getInflateView(R.string.filter_info_title_summary));
            ((LinearLayout) summary_list_parent).addView(new SummaryListInflater(this, filter, db).getInflatedView(active_actions));
        }
    }

    /**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		private DatabaseHelper db;
        private PoormanFilter filter;
		
		public SectionsPagerAdapter(FragmentManager fm, DatabaseHelper db, PoormanFilter filter) {
			super(fm);
			this.db = db;
            this.filter = filter;
		}
		
		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			Fragment fragment = new PoormanSectionFragment();
			((PoormanSectionFragment) fragment).setDatabase(db); // set link to database
            ((PoormanSectionFragment) fragment).setFilter(filter); // set link to filter
			Bundle args = new Bundle();
			args.putInt(PoormanSectionFragment.ARG_SECTION_NUMBER, position + 1);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
			}
			return null;
		}
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class PoormanSectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

        public static final int FRAGMENT_ACCOUNTS = 0;
        public static final int FRAGMENT_ACTIONS = 1;
        public static final int FRAGMENT_SUMMARY = 2;

		private DatabaseHelper db;
        private PoormanFilter filter;
        private int section;
        private View rootView;

		public PoormanSectionFragment() {
		}

		public void setDatabase(DatabaseHelper db) { this.db = db; }
        public void setFilter(PoormanFilter filter) { this.filter = filter; }

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			//rootView = new View(getActivity());
            rootView = new View(ct);

            section = getArguments().getInt(ARG_SECTION_NUMBER);

			switch(section) {
                case 1: // Accounts
                    rootView = inflater.inflate(R.layout.tab_poorman_overview, container, false);
                    // Refresh account information
                    //Vector<PoormanAccount> accounts = db.getAllAccounts(DatabaseHelper.SORT_BY_IS_DEFAULT);

                    LinearLayout accounts_parent = (LinearLayout) rootView.findViewById(R.id.linearLayout_poorman_overview_accounts_list);
                    PoormanActivity.account_list_parent = accounts_parent;

                    Button transfer_button = (Button) rootView.findViewById(R.id.button_accounts_transfer);
                    Button stats_button = (Button) rootView.findViewById(R.id.button_accounts_statistics);

                    transfer_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            (new TransferDialog(getActivity(), db)).getDialog().show();
                        }
                    });

                    stats_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(getActivity(), "Feature not yet implemented.", Toast.LENGTH_LONG).show();
                        }
                    });

                    ((PoormanActivity) getActivity()).refreshUi(FRAGMENT_ACCOUNTS);
                    break;

                case 2: // Transactions
                    rootView = inflater.inflate(R.layout.tab_poorman_transactions, container, false);
                    LinearLayout actions_parent = (LinearLayout) rootView.findViewById(R.id.linearLayout_transactions_tab_parent);
                    PoormanActivity.action_list_parent = actions_parent;
                    ((PoormanActivity) getActivity()).refreshUi(FRAGMENT_ACTIONS);
                    break;

                case 3: // Summary
                    rootView = inflater.inflate(R.layout.tab_poorman_summary, container, false);
                    LinearLayout summary_parent = (LinearLayout) rootView.findViewById(R.id.linearLayout_summary_parent);
                    PoormanActivity.summary_list_parent = summary_parent;
                    ((PoormanActivity) getActivity()).refreshUi(FRAGMENT_SUMMARY);
                    break;
            }


			return rootView;
		}

    }

}
