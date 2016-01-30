package no.domain.zak0.scrooge.activity;


import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import no.domain.zak0.R;
import no.domain.zak0.scrooge.dataclass.PoormanAccount;
import no.domain.zak0.scrooge.dataclass.PoormanFilter;
import no.domain.zak0.scrooge.utils.DatabaseHelper;

import java.util.Vector;

public class AccountListInflater {

    private Context context;
    private DatabaseHelper db;

    private static int COLOR_GRAY = 0x44000000;
    private static int COLOR_HOLO_GREEN = 0xff669900;
    private static int COLOR_HOLO_RED = 0xffCC0000;

    public AccountListInflater(Context con, DatabaseHelper db) {

        this.context = con;
        this.db = db;
    }

    public View getInflatedView(Vector<PoormanAccount> accounts) {
        long time_now = System.currentTimeMillis();
        long week_ago = time_now - (7L * 24L * 60L * 60L * 1000L);
        long month_ago = time_now - (30L * 24L * 60L * 60L * 1000L);

        LinearLayout parent = new LinearLayout(context);
        parent.setOrientation(LinearLayout.VERTICAL);

        TextView account_name;
        TextView account_balance;
        TextView current_balance_title;
        TextView change_7;
        TextView change_7_title;
        TextView change_30;
        TextView change_30_title;
        TextView default_account;
        LinearLayout title_row;
        LinearLayout title_row_left;
        LinearLayout title_row_right;
        LinearLayout current_balance_row;
        LinearLayout change_7_row;
        LinearLayout change_30_row;
        View horiz_divider;
        PoormanFilter week_filter;
        PoormanFilter month_filter;
        double week_change;
        double month_change;

        // Display add account button, if not accounts are present in the database.
        if(accounts.size() <= 0) {
            Button new_account_button = new Button(context);
            TextView no_accounts_help = new TextView(context);
            no_accounts_help.setText(R.string.no_accounts_help_text);
            no_accounts_help.setPadding(10,10,10,10);

            float density = context.getResources().getDisplayMetrics().density;

            LinearLayout.LayoutParams button_params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            button_params.setMargins((int) (30 * density) ,0, (int) (30 * density) ,0);
            new_account_button.setText(R.string.new_account);
            new_account_button.setLayoutParams(button_params);
            new_account_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    (new AccountDialog(context, db, null)).getDialog().show();
                }
            });

            parent.addView(no_accounts_help);
            parent.addView(new_account_button);

            return parent;
        }


        for(int i = 0; i < accounts.size(); i++) {
            week_change = 0.0;
            month_change = 0.0;

            /*
            week_filter = new PoormanFilter();
            week_filter.enableFilter(PoormanFilter.FILTER_ACCOUNTS);
            week_filter.enableFilter(PoormanFilter.FILTER_TIME_SPAN);
            week_filter.addFilterAccount(accounts.get(i));
            week_filter.setFilterTimeSpan(week_ago, time_now);

            month_filter = new PoormanFilter();
            month_filter.enableFilter(PoormanFilter.FILTER_ACCOUNTS);
            month_filter.enableFilter(PoormanFilter.FILTER_TIME_SPAN);
            month_filter.addFilterAccount(accounts.get(i));
            month_filter.setFilterTimeSpan(month_ago, time_now);
            */

            horiz_divider = new View(context);
            horiz_divider.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,2));
            horiz_divider.setBackgroundColor(COLOR_GRAY);

            title_row = new LinearLayout(context);
            title_row.setOrientation(LinearLayout.HORIZONTAL);
            title_row_left = new LinearLayout(context);
            title_row_left.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            title_row_left.setGravity(Gravity.LEFT);
            title_row_right = new LinearLayout(context);
            title_row_right.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            title_row_right.setGravity(Gravity.RIGHT);

            current_balance_row = new LinearLayout(context);
            current_balance_row.setOrientation(LinearLayout.HORIZONTAL);
            current_balance_row.setPadding(10,0,0,0);
            change_7_row = new LinearLayout(context);
            change_7_row.setOrientation(LinearLayout.HORIZONTAL);
            change_7_row.setPadding(10,0,0,0);
            change_30_row = new LinearLayout(context);
            change_30_row.setOrientation(LinearLayout.HORIZONTAL);
            change_30_row.setPadding(10,0,0,10);

            account_name = new TextView(context);
            account_balance = new TextView(context);
            account_balance.setPadding(10, 0, 0, 0);
            current_balance_title = new TextView(context);
            change_7_title = new TextView(context);
            change_30_title = new TextView(context);
            change_7 = new TextView(context);
            change_30 = new TextView(context);
            default_account = new TextView(context);

            current_balance_title.setText(R.string.current_balance);
            change_7_title.setText(R.string.change_7_days);
            change_30_title.setText(R.string.change_30_days);


            if(accounts.get(i).getIsDefault()) {
                default_account.setText("Default account");
                default_account.setPadding(0,5,10,0);
            }

            // Calculate balance changes for last 7 and 30 days
            /*
            for(int j = 0; j < db.getFilteredActions(week_filter).size(); j++) {
                week_change += db.getFilteredActions(week_filter).get(j).getAmount();
            }
            for(int j = 0; j < db.getFilteredActions(month_filter).size(); j++) {
                month_change += db.getFilteredActions(month_filter).get(j).getAmount();
            }*/
            week_change = accounts.get(i).getChange7Days();
            month_change = accounts.get(i).getChange30Days();

            if(week_change < 0) {
                change_7.setText(String.format("%.2f", week_change));
                change_7.setTextColor(COLOR_HOLO_RED);
            }
            else {
                change_7.setText(String.format("+%.2f", week_change));
                change_7.setTextColor(COLOR_HOLO_GREEN);
            }

            if(month_change < 0) {
                change_30.setText(String.format("%.2f", month_change));
                change_30.setTextColor(COLOR_HOLO_RED);
            }
            else {
                change_30.setText(String.format("+%.2f", month_change));
                change_30.setTextColor(COLOR_HOLO_GREEN);
            }
            change_7.setPadding(10, 0, 0, 0);
            change_30.setPadding(10, 0, 0, 0);

            current_balance_row.setOrientation(LinearLayout.HORIZONTAL);
            account_name.setText(accounts.get(i).getName());
            account_name.setTypeface(Typeface.DEFAULT_BOLD);
            account_name.setTextSize(20);
            account_name.setPadding(10, 5, 0, 0);

            if(accounts.get(i).getSaldo() < 0) {
                account_balance.setTextColor(COLOR_HOLO_RED);
                account_balance.setText(String.format("%.2f", accounts.get(i).getSaldo()));
            }
            else {
                account_balance.setTextColor(COLOR_HOLO_GREEN);
                account_balance.setText(String.format("+%.2f", accounts.get(i).getSaldo()));
            }


            // Compile rows
            title_row_left.addView(account_name);
            title_row_right.addView(default_account);
            title_row.addView(title_row_left);
            title_row.addView(title_row_right);
            current_balance_row.addView(current_balance_title);
            current_balance_row.addView(account_balance);
            change_7_row.addView(change_7_title);
            change_7_row.addView(change_7);
            change_30_row.addView(change_30_title);
            change_30_row.addView(change_30);

            // Add rows to parent
            parent.addView(title_row);
            parent.addView(current_balance_row);
            parent.addView(change_7_row);
            parent.addView(change_30_row);

            parent.addView(horiz_divider);

            title_row.setOnLongClickListener(new AccountOnLongClickListener(db, context, accounts.get(i)));

        }

        return parent;
    }


}
