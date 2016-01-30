package no.domain.zak0.scrooge.utils;

import java.io.Serializable;
import java.util.Vector;

import no.domain.zak0.scrooge.dataclass.PoormanAccount;
import no.domain.zak0.scrooge.dataclass.PoormanAction;
import no.domain.zak0.scrooge.dataclass.PoormanBackup;
import no.domain.zak0.scrooge.dataclass.PoormanFilter;
import no.domain.zak0.scrooge.dataclass.PoormanTag;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper implements Serializable {

	/* This class contains the interface between the SQLite database
	 * and other app components.
	 * 
	 * All the SQL-stuff goes here. This is the only place where the database is
	 * directly accessed.
	 */

    // TODO: Save as template -field for action table must be included in action table methods.
    // TODO: replace ' with something or horrible things will happen in the future

	private static final long serialVersionUID = -872880668063880320L;

	private SQLiteDatabase database;
	
	/* Name constants */
	public static final String TABLE_ACCOUNT = "account";
	public static final String TABLE_ACTION = "action";
	public static final String TABLE_TAG = "tag";
	public static final String TABLE_TAGLINK = "taglink";
	
	public static final String COL_ACCOUNTID = "accountid";
	public static final String COL_ACCOUNTINITIALSALDO = "accinitsaldo";
	public static final String COL_ACCOUNTSALDO = "accsaldo";
	public static final String COL_ACCOUNTNAME = "accname";
	public static final String COL_ACCOUNTDESCRIPTION = "accdescription";
	public static final String COL_ACCOUNTISDEFAULT = "accisdefault";
		
	public static final String COL_ACTIONID = "actionid";
	public static final String COL_ACTIONAMOUNT = "actamount";
	public static final String COL_ACTIONTIME ="acttime";
	public static final String COL_ACTIONDESCRIPTION = "actdescription";
	public static final String COL_ACTIONTOFROM = "acttofrom";
	public static final String COL_ACTIONISTEMPLATE = "actistemplate";
	
	public static final String COL_TAGID = "tagid";
	public static final String COL_TAGNAME = "tagname";
	public static final String COL_TAGDESCRIPTION = "tagdescription";
	
	public static final String COL_TAGLINKID = "taglinkid";

	public static final String DB_NAME = "scrooge_database.db";
	public static final int DB_VERSION = 1;
	
	private static String TAG = "DatabaseHelper";
	
	/* Numeric constants */
	public static final int SORT_BY_NAME = 1;
	public static final int SORT_BY_IS_DEFAULT = 2;
	
	// Auto-generated constructor
	public DatabaseHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	// Database creation. This is called if database does not exist.
	@Override
	public void onCreate(SQLiteDatabase db) {
		
		// SQL for creating action table
		String create_account = "create table " + TABLE_ACCOUNT + "(" + COL_ACCOUNTID + 
				" integer primary key autoincrement, " + COL_ACCOUNTINITIALSALDO + " float not null, "
				+ COL_ACCOUNTSALDO + " float not null, " + COL_ACCOUNTNAME + " text, "
				+ COL_ACCOUNTDESCRIPTION + " text, " + COL_ACCOUNTISDEFAULT + " integer)";
		
		// SQL for creating action table
		String create_action = "create table " + TABLE_ACTION + "(" + COL_ACTIONID + 
				" integer primary key autoincrement, " + COL_ACTIONAMOUNT + " float not null, "
				+ COL_ACTIONTIME + " integer, " + COL_ACTIONDESCRIPTION + " text, "
				+ COL_ACTIONTOFROM + " text, " + COL_ACCOUNTID + " integer, "
				+ COL_ACTIONISTEMPLATE + " integer)";
		
		// SQL for craeting tag table
		String create_tag = "create table " + TABLE_TAG + "(" + COL_TAGID +
				" integer primary key autoincrement, " + COL_TAGNAME + " text not null, "
				+ COL_TAGDESCRIPTION + " text)";
		
		// SQL for creating taglink table
		String create_taglink = "create table " + TABLE_TAGLINK + "(" + COL_TAGLINKID +
				" integer primary key autoincrement, " + COL_ACTIONID + " integer, "
				+ COL_TAGID + " integer)";
		
		db.execSQL(create_account);
		db.execSQL(create_action);
		db.execSQL(create_tag);
		db.execSQL(create_taglink);
		
		Log.d("DatabaseHelper", "onCreate() finished");
	}

	// Called when DB is upgraded.
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	    Log.d("DatabaseHelper", "Upgrading database from version " + oldVersion + " to "
	                + newVersion + ", which will destroy all old data");
	        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACTION);
	        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TAG);
	        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TAGLINK);
	        onCreate(db);
	}

	// Opens the database and stores pointer to database.
	public void openDb() throws SQLException {
		Log.d("DatabaseHelper", "openDb() called");
		database = this.getWritableDatabase();
	}
	
	// Closes the database
	public void closeDb() {
		Log.d("DatabaseHelper", "closeDb() called");
		database.close();
	}
	
	
	
	public void insertAccount(PoormanAccount acc) {
		int is_default = 0;
		String sql = "";
		if(acc.getIsDefault()) {
			// Remove default account flag from all other accounts
			sql = "update "+TABLE_ACCOUNT+" set "
					+COL_ACCOUNTISDEFAULT+" = 0";
			database.execSQL(sql);
			is_default = 1;
		}
		
		sql = "insert into "+TABLE_ACCOUNT+"("+COL_ACCOUNTNAME+","+COL_ACCOUNTDESCRIPTION+","
				+COL_ACCOUNTINITIALSALDO+","+COL_ACCOUNTSALDO+","+COL_ACCOUNTISDEFAULT+") values('"
				+acc.getName()+"', '"+acc.getDescription()+"', "+Double.toString(acc.getInitialSaldo())
				+", "+Double.toString(acc.getSaldo())+", "+Integer.toString(is_default)+")";
		
		database.execSQL(sql);
		Log.d(TAG, "insertAccount() - account was added");
		Log.d(TAG, "insertAccount() - value of (int) is_default: "+Integer.toString(is_default));
	}
	
	public void updateAccount(PoormanAccount acc) {
		int is_default = 0;
		String sql = "";
		if(acc.getIsDefault()) {
			// Remove default account flag from all other accounts
			is_default = 1;
			sql = "update "+TABLE_ACCOUNT+" set "
					+COL_ACCOUNTISDEFAULT+" = 0 where "
					+COL_ACCOUNTID+" != "+Integer.toString(acc.getId());
			Log.d(TAG, "TADAA!!!!");
			database.execSQL(sql);
		}
		
		sql = "update "+TABLE_ACCOUNT+" set "
				+COL_ACCOUNTNAME+" = '"+acc.getName()+"', "
				+COL_ACCOUNTDESCRIPTION+" = '"+acc.getDescription()+"', "
				+COL_ACCOUNTSALDO+" = "+Double.toString(acc.getSaldo())+", "
				+COL_ACCOUNTINITIALSALDO+" = "+Double.toString(acc.getInitialSaldo())+", "
				+COL_ACCOUNTISDEFAULT+" = "+Integer.toString(is_default)
				+" where "+COL_ACCOUNTID+" = "+Integer.toString(acc.getId());
				
		database.execSQL(sql);
		
		Log.d(TAG, "updateAccount() - value of (int) is_default: "+Integer.toString(is_default));
	}
	
	public void deleteAccount(PoormanAccount acc) {
		String sql = "delete from "+TABLE_ACCOUNT+" where "+COL_ACCOUNTID+" = "+Integer.toString(acc.getId());
		database.execSQL(sql);
		
		// Delete also actions on that account
		sql = "delete from "+TABLE_ACTION+" where "+COL_ACCOUNTID+" = "+Integer.toString(acc.getId());
		database.execSQL(sql);
		
		Log.d(TAG, "deleted account: "+acc.getName());
		
	}
	
	public void setAccountAsDefault(PoormanAccount acc) {
		String sql = "update "+TABLE_ACCOUNT+" set "
				+COL_ACCOUNTISDEFAULT+" = 0 where "
				+COL_ACCOUNTID+" != "+Integer.toString(acc.getId());
		
		database.execSQL(sql);
		
		sql = "update "+TABLE_ACCOUNT+" set "
				+COL_ACCOUNTISDEFAULT+" = 1 where "
				+COL_ACCOUNTID+" = "+Integer.toString(acc.getId());
		
		database.execSQL(sql);
		
	}

    // reads actions to the account for last 7 and 30 days and sets them to acc.
    public void getAccountRecentChanges(PoormanAccount acc) {
        long week_ago = System.currentTimeMillis() - (7L*24L*60L*60L*1000L);
        long month_ago = System.currentTimeMillis() - (30L*24L*60L*60L*1000L);
        String sql = "select sum("+COL_ACTIONAMOUNT+") from "+TABLE_ACTION+" where "
                +COL_ACCOUNTID+"="+Integer.toString(acc.getId())+" and "
                +COL_ACTIONTIME+">="+Long.toString(week_ago)+" and "
                +COL_ACTIONTIME+"<="+Long.toString(System.currentTimeMillis());
        Cursor cur = database.rawQuery(sql, null);
        cur.moveToFirst();
        if(!cur.isAfterLast()) {
          acc.setChange7Days(cur.getDouble(0));
        }
        cur.close();

        sql = "select sum("+COL_ACTIONAMOUNT+") from "+TABLE_ACTION+" where "
                +COL_ACCOUNTID+"="+Integer.toString(acc.getId())+" and "
                +COL_ACTIONTIME+">="+Long.toString(month_ago)+" and "
                +COL_ACTIONTIME+"<="+Long.toString(System.currentTimeMillis());
        cur = database.rawQuery(sql, null);
        cur.moveToFirst();
        if(!cur.isAfterLast()) {
            acc.setChange30Days(cur.getDouble(0));
        }
        cur.close();
    }
	
	public Vector<PoormanAccount> getAllAccounts() { return getAllAccounts(DatabaseHelper.SORT_BY_NAME); }
	public Vector<PoormanAccount> getAllAccounts(int sort_by) {
		Vector<PoormanAccount> ret = new Vector<PoormanAccount>();
		
		String sql = "select * from "+TABLE_ACCOUNT+" order by ";
		
		if(sort_by == DatabaseHelper.SORT_BY_IS_DEFAULT) {
			sql += COL_ACCOUNTISDEFAULT+" desc, ";
		}
		
		// always order by account name
		sql += COL_ACCOUNTNAME;
	
		Cursor cur = database.rawQuery(sql, null);
		cur.moveToFirst();
		while(!cur.isAfterLast()) {
			ret.add(cursorToAccount(cur));
			cur.moveToNext();
		}
        cur.close();
		
		return ret;
	}
	
	
	private PoormanAccount cursorToAccount(Cursor cur) {
		PoormanAccount acc = new PoormanAccount();
		
		acc.setId(cur.getInt(0));
		acc.setInitialSaldo(cur.getDouble(1));
		acc.setSaldo(cur.getDouble(2));
		acc.setName(cur.getString(3));
		acc.setDescription(cur.getString(4));
		if(cur.getInt(5) == 1) acc.setIsDefault(true);
		else acc.setIsDefault(false);
		
		return acc;
	}
	
	
	
	/* Database actions for ACTION table.
	 * 
	 * 
	 * */
	
	// Inserts a transaction into the database.
	// Returns id of the new transaction
	public void insertAction(PoormanAction act) {
		String sql = "insert into "+TABLE_ACTION+"("+COL_ACTIONAMOUNT+","+COL_ACTIONTIME+","+COL_ACTIONDESCRIPTION+
				","+COL_ACTIONTOFROM+","+COL_ACCOUNTID+","+COL_ACTIONISTEMPLATE+
				") values("+Double.toString(act.getAmount())+", "+Long.toString(act.getTime())+", '"+act.getDescription()
				+"', '" +act.getToFrom()+ "', "+Integer.toString(act.getAccount().getId())+", 0)";
		
		if(database == null) Log.e(TAG, "insertAction(): database was null");
		database.execSQL(sql);
		
		// Get id of the new transaction
		// The id is used when adding taglinks
		sql = "select "+COL_ACTIONID+" from "+TABLE_ACTION+" order by "+COL_ACTIONID+" desc limit 1";
		Cursor result = database.rawQuery(sql, null);
		result.moveToFirst();
		if(!result.isAfterLast()) {
			act.setId(result.getInt(0));
			// Add tag links
			for(int i = 0; i < act.getTags().size(); i++) {
				insertTaglink(act, act.getTags().get(i));
			}
		}
        result.close();
		
	}
	
	public void deleteAction(PoormanAction action) {
		Log.d("DatabaseHelper", "deleteAction() called for action id "+Integer.toString(action.getId()));
		String sql = "delete from "+TABLE_ACTION+" where "+COL_ACTIONID+"="+Integer.toString(action.getId());
		database.execSQL(sql);

        // Delete all taglinks to this action
        for(int i = 0; i < action.getTags().size(); i++) {
            deleteTaglink(action, action.getTags().get(i));
        }
	}
	
	
	/* Returns all actions as a vector. 
	 * Parameterized version returns a given amount of rows
	 */
	public Vector<PoormanAction> getAllActions() { return getAllActions(-1); }
	public Vector<PoormanAction> getAllActions(int num_of_rows) {
		Vector<PoormanAction> all_actions = new Vector<PoormanAction>();
		Cursor result;
		String sql = "select * from " +TABLE_ACTION+" order by "+COL_ACTIONTIME+" desc";
		
		if(num_of_rows > 0) sql += " limit "+Integer.toString(num_of_rows);
		
		result = database.rawQuery(sql, null);
		
		result.moveToFirst();
		Log.d(TAG, "selectAllActions(): before iterating all the results");
		while(result.isAfterLast() == false) {
			all_actions.add(cursorToAction(result));
			result.moveToNext();
		}
		Log.d(TAG, "selectAllActions(): after iterating all the results");
		result.close();
		
		Log.d(TAG, "selectAllActions() found "+Integer.toString(all_actions.size())+" actions");		
		return all_actions;
	}
	
	// Returns all the actions between before and after parameters.
	public Vector<PoormanAction> getActionsInTimeRange(long after, long before) {
		Vector<PoormanAction> ret = new Vector<PoormanAction>();
		
		String sql = "select * from "+TABLE_ACTION+" where "
				+COL_ACTIONTIME+" <= "+Long.toString(before)+" and "
				+COL_ACTIONTIME+" >= "+Long.toString(after)
				+" order by "+COL_ACTIONTIME+" desc";
		
		Cursor cur = database.rawQuery(sql, null);
		cur.moveToFirst();
		while(!cur.isAfterLast()) {
			ret.add(cursorToAction(cur));
			cur.moveToNext();
		}
        cur.close();
		
		return ret;
	}
	
	// Returns all actions that fulfill filter preferences.
	public Vector<PoormanAction> getFilteredActions(PoormanFilter filter) {
		Vector<PoormanAction> ret = new Vector<PoormanAction>();
		
		// Compile sql
		String sql = "select * from "+TABLE_ACTION+" where 1 = 1";
		
		// Iterate through all the possible filters
		for(int i = 0; i < filter.getActiveFilters().size(); i++) {
			// Add where clauses of corresponding filter settings
			switch(filter.getActiveFilters().get(i)) {
			case PoormanFilter.FILTER_TIME_SPAN:
				sql += " and "+COL_ACTIONTIME+" <= "+Long.toString(filter.getFilterTimeSpanBefore())
					+ " and "+COL_ACTIONTIME+" >= "+Long.toString(filter.getFilterTimeSpanAfter());
				break;

            case PoormanFilter.FILTER_ACCOUNTS:
                for(int j = 0; j < filter.getFilterAccounts().size(); j++) {
                    sql += " and "+COL_ACCOUNTID+" = "+Integer.toString(filter.getFilterAccounts().get(j).getId());
                }
                break;

			}
		}

        sql += " order by "+COL_ACTIONTIME+" desc";

		Cursor cur = database.rawQuery(sql, null);
		cur.moveToFirst();
		while(!cur.isAfterLast()) {
			ret.add(cursorToAction(cur));
			cur.moveToNext();
		}
		cur.close();
		
		return ret;
	}
	
	private PoormanAction cursorToAction(Cursor cur) {
		PoormanAction act = new PoormanAction();
				
		act.setId(cur.getInt(0));
		act.setAmount(cur.getFloat(1));
		act.setTime(cur.getLong(2));
		act.setDescription(cur.getString(3));
		act.setToFrom(cur.getString(4));
		
		// Get tags for action
		getTagsForAction(act);
		
		// Get account for action
		getAccountForAction(act);
		
		return act;
	}
	
	/* Returns all tags as a vector. Sorted in alphabetical order.
	 * 
	 */
	public Vector<PoormanTag> getAllTags() {
		Vector<PoormanTag> all_tags = new Vector<PoormanTag>();
		Cursor result;
		String sql = "select * from "+TABLE_TAG+" order by "+COL_TAGNAME;
		result = database.rawQuery(sql, null);
		
		result.moveToFirst();
		while(result.isAfterLast() == false) {
			all_tags.add(cursorToTag(result));
			result.moveToNext();
		}
		result.close();		
		
		return all_tags;
	}
	
	/* Returns all tags that begin with <String begin>.
	 * 
	 */
	public Vector<PoormanTag> getAllTagsThatStartWith(String begin) {
		Vector<PoormanTag> ret = new Vector<PoormanTag>();
		Cursor result;
		String sql = "select * from "+TABLE_TAG+" where "+COL_TAGNAME+ " like '"
				+begin+"%' order by "+COL_TAGNAME;
		result = database.rawQuery(sql, null);
		
		result.moveToFirst();
		while(result.isAfterLast() == false) {
			ret.add(cursorToTag(result));
			result.moveToNext();
		}
		result.close();		
		
		return ret;
	}

	private PoormanTag cursorToTag(Cursor cur) {
		PoormanTag tag = new PoormanTag();
		
		tag.setId(cur.getInt(0));
		tag.setName(cur.getString(1));
		tag.setDescription(cur.getString(2));
		
		return tag;
	}
	
	
	// Inserts a new tag into database
	// Checks for duplicate tag names, returns true if new tag was added, false otherwise.
	public boolean insertTag(PoormanTag new_tag) {
		String sql = "select * from "+TABLE_TAG+" where "+COL_TAGNAME+" = '"+new_tag.getName()+"'";
		Cursor result = database.rawQuery(sql, null);
		if(result.getCount() > 0) {
			result.close();
			return false;
		}
        result.close();
		
		
		sql = "insert into "+TABLE_TAG+"("+COL_TAGNAME+", "+COL_TAGDESCRIPTION+") values('"
				+new_tag.getName()+"', '"+new_tag.getDescription()+"')";
		
		database.execSQL(sql);
		return true;
	}

    // Deletes a tag from the database
    public void deleteTag(PoormanTag tag) {
        // Check that tag is not used in any taglinks (it sure should not be...)

        String sql = "select * from "+TABLE_TAGLINK+" where "+COL_TAGID+" = "+Integer.toString(tag.getId());
        Cursor cur = database.rawQuery(sql, null);
        if(cur.getCount() > 0) {
            // Tag was still used in one or more taglinks, so it's not safe to delete it...
            Log.d(TAG, "deleteTag() : tag still used in taglinks, not deleted");
            return;
        }
        cur.close();

        // Execution goes here if no taglinks with tag existed.
        sql = "delete from "+TABLE_TAG+" where "+COL_TAGID+" = "+Integer.toString(tag.getId());
        database.execSQL(sql);
    }


	// Creates a new taglink row
	public void insertTaglink(PoormanAction act, PoormanTag tag) {
		
		// See if taglink already exists. And don't add a same link again.
		String sql = "select * from " + TABLE_TAGLINK + " where "+COL_ACTIONID+" = "
		+ Integer.toString(act.getId()) + " and " + COL_TAGID + " = " + Integer.toString(tag.getId());
		Cursor result = database.rawQuery(sql, null);
		if(result.getCount() > 0) {
			Log.d(TAG, "taglink already exists");
			return;
		}
		result.close();

		// Add a new taglink if it didn't already exist.
		sql = "insert into "+TABLE_TAGLINK+"("+COL_ACTIONID+", "+COL_TAGID+") values ("
				+Integer.toString(act.getId())+", "+Integer.toString(tag.getId())+")";
		database.execSQL(sql);
		
		Log.d(TAG, "taglink added, action id: "+Integer.toBinaryString(act.getId())+", tag id: "
				+Integer.toString(tag.getId()));
	}


    /* Deletes a taglink from the database
     * Also deletes the tag if it's no longer used by any other taglink
     */
    public void deleteTaglink(PoormanAction act, PoormanTag tag) {
        String sql = "delete from "+TABLE_TAGLINK+" where "+COL_ACTIONID+" = "+Integer.toString(act.getId())
                +" and "+COL_TAGID+" = "+Integer.toString(tag.getId());

        database.execSQL(sql);

        // See if tag is used in any other taglink.
        sql = "select * from "+TABLE_TAGLINK+" where "+COL_TAGID+" = "+Integer.toString(tag.getId());

        Cursor cur = database.rawQuery(sql, null);
        if(cur.getCount() == 0) {
            // tag was not used in any other taglink, so its safe to delete
            deleteTag(tag);
        }
        cur.close();

    }


	/* Selects (from the database) and sets all the tags for a transaction.
	 * 
	 */
	public void getTagsForAction(PoormanAction act) {
		String sql = "select "+TABLE_TAG+"."+COL_TAGID+", "
				+TABLE_TAG+"."+COL_TAGNAME+", "+TABLE_TAG+"."
				+COL_TAGDESCRIPTION+" from "+TABLE_TAG+", "+TABLE_TAGLINK
				+" where "+TABLE_TAG+"."+COL_TAGID+" = "+TABLE_TAGLINK+"."
				+COL_TAGID+" and "+TABLE_TAGLINK+"."+COL_ACTIONID
				+" = "+Integer.toString(act.getId())+" order by "+COL_TAGNAME;
		Cursor result = database.rawQuery(sql, null);
		result.moveToFirst();
		while(result.isAfterLast() == false) {
			act.addTag(cursorToTag(result));
			result.moveToNext();
		}
        result.close();
	}
	
	
	/* Updates an existing transaction in the database.
	 * 
	 */
	public void updateAction(PoormanAction act) {
		String sql = "update "+TABLE_ACTION+" set "
				+COL_ACTIONAMOUNT+" = "+Double.toString(act.getAmount())+", "
				+COL_ACTIONDESCRIPTION+" = '"+act.getDescription()+"', "
				+COL_ACTIONTIME+" = "+ Long.toString(act.getTime()) + ", "
				+COL_ACTIONTOFROM+" = '"+act.getToFrom() + "', "
				+COL_ACCOUNTID+" = "+Integer.toString(act.getAccount().getId())
				+" where "+COL_ACTIONID+" = "+Integer.toString(act.getId());
		database.execSQL(sql);
		
		// Rebuild tag links
		sql = "delete from "+TABLE_TAGLINK+" where "+COL_ACTIONID+" = "+Integer.toString(act.getId());
		database.execSQL(sql);
		
		// Method insertTaglink checks for duplicates, so checking here is not necessary.
		for(int i = 0; i < act.getTags().size(); i++) {
			insertTaglink(act, act.getTags().get(i));
		}
		
	}
	
	/* Gets account for action from the database.
	 * Sets the account to action object.
	 * 
	 */
	public void getAccountForAction(PoormanAction act) {
		String sql = "select "+TABLE_ACCOUNT+".* from "+TABLE_ACTION+", "+TABLE_ACCOUNT+" where "
				+TABLE_ACTION+"."+COL_ACTIONID+" = "+Integer.toString(act.getId())+" and "
				+TABLE_ACTION+"."+COL_ACCOUNTID+" = "+TABLE_ACCOUNT+"."+COL_ACCOUNTID;
		Cursor cur = database.rawQuery(sql, null);
		cur.moveToFirst();
		while(!cur.isAfterLast()) {
			act.setAccount(cursorToAccount(cur));
			cur.moveToNext();
		}
		cur.close();
	}
	
	/* Returns all used targets (to/from) that begin with <String begin>.
	 * 
	 */
	public Vector<String> getTargets(String that_begin_with, boolean is_expense) {
		Vector<String> ret = new Vector<String>();
		String sql = "select distinct "+COL_ACTIONTOFROM+" from "+TABLE_ACTION
				+" where "+COL_ACTIONTOFROM+" like '"+that_begin_with+"%'";
		
		if(is_expense) sql += " and "+COL_ACTIONAMOUNT+" <= 0";
		else sql += " and "+COL_ACTIONAMOUNT+" >= 0";
		
		sql += " order by "+COL_ACTIONTOFROM;
			
		Cursor cur = database.rawQuery(sql, null);
		cur.moveToFirst();
		while(cur.isAfterLast() == false) {
			ret.add(cur.getString(0));
			cur.moveToNext();
		}
		cur.close();
		
		return ret;
	}

    /* Returns all targets after timelimit
     *
     */
    public Vector<String> getTargetsAfter(long after, boolean is_expense) {
        Vector<String> ret = new Vector<String>();
        String sql = "select distinct "+COL_ACTIONTOFROM+" from "+TABLE_ACTION
                +" where "+COL_ACTIONTIME+" >= "+Long.toString(after);

        if(is_expense) sql += " and "+COL_ACTIONAMOUNT+" <= 0";
        else sql += " and "+COL_ACTIONAMOUNT+" >= 0";

        sql += " order by "+COL_ACTIONTOFROM;

        Cursor cur = database.rawQuery(sql, null);
        cur.moveToFirst();
        while(cur.isAfterLast() == false) {
            ret.add(cur.getString(0));
            cur.moveToNext();
        }
        cur.close();

        return ret;
    }

    /* Restores a PoormanBackup into a database.

    First cleans up the database (i.e. delete * from all the tables),
    then adds the data from PoormanBackup.
     */
    public void restoreBackup(PoormanBackup bu) {
        // delete everything.
        String sql = "delete from "+TABLE_TAGLINK;
        database.execSQL(sql);

        sql = "delete from "+TABLE_TAG;
        database.execSQL(sql);

        sql = "delete from "+TABLE_ACTION;
        database.execSQL(sql);

        sql = "delete from "+TABLE_ACCOUNT;
        database.execSQL(sql);

        // restore accounts
        for(int i = 0; i < bu.getAccounts().size(); i++) {
            PoormanBackup.Account acc = bu.getAccounts().get(i);
            sql = "insert into "+TABLE_ACCOUNT+"("
                    +COL_ACCOUNTID+","
                    +COL_ACCOUNTINITIALSALDO+","
                    +COL_ACCOUNTSALDO+","
                    +COL_ACCOUNTNAME+","
                    +COL_ACCOUNTDESCRIPTION+","
                    +COL_ACCOUNTISDEFAULT+") values ("
                    +acc.id+","
                    +acc.initial_balance+","
                    +acc.balance+",'"
                    +acc.name+"','"
                    +acc.description+"',"
                    +acc.is_default+")";

            database.execSQL(sql);

        }

        // restore tags
        for(int i = 0; i < bu.getTags().size(); i++) {
            PoormanBackup.Tag tag = bu.getTags().get(i);
            sql = "insert into "+TABLE_TAG+"("
                    +COL_TAGID+","
                    +COL_TAGNAME+","
                    +COL_TAGDESCRIPTION+") values ("
                    +tag.id+",'"
                    +tag.name+"','"
                    +tag.description+"')";

            database.execSQL(sql);

        }


        // restore actions
        for(int i = 0; i < bu.getActions().size(); i++) {
            PoormanBackup.Action act = bu.getActions().get(i);
            sql = "insert into "+TABLE_ACTION+"("
                    +COL_ACTIONID+","
                    +COL_ACTIONAMOUNT+","
                    +COL_ACTIONTIME+","
                    +COL_ACTIONDESCRIPTION+","
                    +COL_ACTIONTOFROM+","
                    +COL_ACTIONISTEMPLATE+","
                    +COL_ACCOUNTID+") values ("
                    +act.id+","
                    +act.amount+","
                    +act.timestamp+",'"
                    +act.description+"','"
                    +act.tofrom+"',"
                    +act.is_template+","
                    +act.account+")";

            database.execSQL(sql);

            // create taglinks
            for(int j = 0; j < act.tags.size(); j++) {
                sql = "insert into "+TABLE_TAGLINK+"("
                        +COL_TAGID+","
                        +COL_ACTIONID+") values ("
                        +act.tags.get(j)+","
                        +act.id+")";

                database.execSQL(sql);
            }

        }

    }


}
