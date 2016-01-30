package no.domain.zak0.scrooge.dataclass;

import java.util.Vector;

/**
 * Created by jaakko on 8/2/13.
 *
 * Container of all data read from backup xml.
 * Used when restoring a backup xml.
 *
 * Contains sub-classes that define accounts, transactions and tags in a format that is easy to apply in an SQL script.
 */
public class PoormanBackup {

    private Vector<Account> accounts;
    private Vector<Action> actions;
    private Vector<Tag> tags;

    private long timestamp;

    public PoormanBackup() {
        this.accounts = new Vector<Account>();
        this.actions = new Vector<Action>();
        this.tags = new Vector<Tag>();
        this.timestamp = 0;
    }

    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public long getTimestamp() { return timestamp; }

    public void addAction(Action act) { actions.add(act); }
    public void addAccount(Account acc) { accounts.add(acc); }
    public void addTag(Tag tag) { tags.add(tag); }

    public Vector<Account> getAccounts() { return accounts; }
    public Vector<Action> getActions() { return actions; }
    public Vector<Tag> getTags() { return tags; }

    // Container classes
    public static class Action {
        public String id;
        public String amount;
        public String description;
        public String tofrom;
        public String timestamp;
        public Vector<String> tags; // list of tag IDs
        public String account;
        public String is_template;

        public Action() {}
    };



    public static class Account {
        public String id;
        public String initial_balance;
        public String balance;
        public String name;
        public String description;
        public String is_default;

        public Account() {}

    };

    public static class Tag {
        public String id;
        public String name;
        public String description;

        public Tag() {}
    }



}
