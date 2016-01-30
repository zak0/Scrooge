package no.domain.zak0.scrooge.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import no.domain.zak0.R;
import no.domain.zak0.scrooge.dataclass.PoormanAccount;
import no.domain.zak0.scrooge.dataclass.PoormanAction;
import no.domain.zak0.scrooge.dataclass.PoormanBackup;
import no.domain.zak0.scrooge.dataclass.PoormanTag;
import no.domain.zak0.scrooge.utils.DatabaseHelper;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Created by jaakko on 8/2/13.
 *
 * Implements the backup / restore dialog and backup and restore functionality
 */


public class BackupRestoreDialog {

    public static final String ROOTNODE = "Backup";
    public static final String TIMESTAMP = "Timestamp";

    public static final String ACCOUNTSROOT = "Accounts";
    public static final String ACCOUNTROOT = "Account";
    public static final String ACCOUNTID = "Id";
    public static final String ACCOUNTINITIALSALDO = "InitialBalance";
    public static final String ACCOUNTSALDO = "Balance";
    public static final String ACCOUNTNAME = "Name";
    public static final String ACCOUNTDESCRIPTION = "Description";
    public static final String ACCOUNTISDEFAULT = "IsDefault";

    public static final String TAGSROOT = "Tags";
    public static final String TAGROOT = "Tag";
    public static final String TAGID = "Id";
    public static final String TAGNAME = "Name";
    public static final String TAGDESCRIPTION = "Description";

    public static final String ACTIONSROOT = "Transactions";
    public static final String ACTIONROOT = "Transaction";
    public static final String ACTIONID = "Id";
    public static final String ACTIONAMOUNT = "Amount";
    public static final String ACTIONTIME ="Timestamp";
    public static final String ACTIONDESCRIPTION = "Description";
    public static final String ACTIONTOFROM = "ToFrom";
    public static final String ACTIONISTEMPLATE = "IsTemplate";
    public static final String ACTIONACCOUNTID = "AccountId";
    public static final String ACTIONTAGS = "Tags";
    public static final String ACTIONTAGID = "TagId";

    private static final String TAG = "BackupRestoreDialog";
    private Context context;
    private DatabaseHelper db;
    private Dialog dlg;
    private PoormanBackup backup;

    private static final String backup_filename = "backup.xml";
    private static final String backup_directory = "/Scrooge";

    public BackupRestoreDialog(Context ct, DatabaseHelper db) {
        this.context = ct;
        this.db = db;
        backup = new PoormanBackup();

        dlg = new Dialog(context);
    }

    public Dialog getDialog() {
        dlg.setContentView(R.layout.dialog_backup_restore);
        dlg.setTitle(R.string.backup_restore);

        TextView last_backup_textview = (TextView) dlg.findViewById(R.id.textView_backup_last_date);

        // Backup button action
        Button backup_button = (Button) dlg.findViewById(R.id.button_backup_backup);
        backup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backup();
            }
        });

        // Restore button action
        Button restore_button = (Button) dlg.findViewById(R.id.button_backup_restore);
        restore_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // show a yes/no dialog
                AlertDialog.Builder dialog_builder = new AlertDialog.Builder(context);
                dialog_builder.setTitle(R.string.are_you_sure);
                dialog_builder.setMessage(R.string.restore_confirmation_msg);
                dialog_builder.setPositiveButton(R.string.restore, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        restore();
                        dlg.dismiss();
                        ((PoormanActivity) context).refreshUi(true);
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

        // Cancel button action
        Button cancel_button = (Button) dlg.findViewById(R.id.button_backup_cancel);
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dlg.dismiss();
            }
        });


        checkLastBackup();
        return dlg;
    }


    // Checks if a file exists and opens it. Returns Document object of the backup XML file.
    // Returns null if failed.
    private Document openBackupFile() {
        File backup_file;
        backup_file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + backup_directory, backup_filename);
        if(!backup_file.exists()) {
            Log.d(TAG, "openBackupFile(): no backup file found");
            return null;
        }

        DocumentBuilderFactory doc_factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder doc_builder;
        try {
            doc_builder = doc_factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            Log.d(TAG, "openBackupFile(): unable to create doc builder: "+e.getMessage());
            return null;
        }

        Document doc;
        try {
            doc = doc_builder.parse(backup_file);
        } catch (SAXException e) {
            e.printStackTrace();
            Log.d(TAG, "openBackupFile(): unable to open doc: "+e.getMessage());
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "openBackupFile(): unable to open doc: "+e.getMessage());
            return null;
        }

        doc.getDocumentElement().normalize();

        return doc;
    }


    // Checks if a backup file already exists and reads its' timestamp.
    private void checkLastBackup() {
        Document doc = openBackupFile();

        TextView last_backup = (TextView) dlg.findViewById(R.id.textView_backup_last_date);
        if(doc == null) {
            last_backup.setText(R.string.never);
        }
        else {

            long timestamp = Long.parseLong(doc.getElementsByTagName(TIMESTAMP).item(0).getChildNodes().item(0).getNodeValue());

            String date_and_time = DateFormat.getDateFormat(context).format(new Date(timestamp));
            date_and_time += " ";
            date_and_time += DateFormat.getTimeFormat(context).format(new Date(timestamp));
            last_backup.setText(date_and_time);
        }
    }

    // Backs up all accounts, transactions and tags into an XML file.
    private void backup() {
        DocumentBuilderFactory doc_factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder doc_builder;
        try {
            doc_builder = doc_factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            Log.d(TAG, "backup(): unable to create doc builder");
            Toast.makeText(context, R.string.backup_failed, Toast.LENGTH_LONG).show();
            return;
        }

        Document doc = doc_builder.newDocument();

        Element root = doc.createElement(ROOTNODE);
        doc.appendChild(root);

        // add timestamp
        Element timestamp = doc.createElement(TIMESTAMP);
        timestamp.appendChild(doc.createTextNode(Long.toString(System.currentTimeMillis())));
        root.appendChild(timestamp);

        // add accounts
        Element accounts_root = doc.createElement(ACCOUNTSROOT);
        root.appendChild(accounts_root);

        Vector<PoormanAccount> accs = db.getAllAccounts();
        for(int i = 0; i < accs.size(); i++) {
            Element account_root = doc.createElement(ACCOUNTROOT);
            accounts_root.appendChild(account_root);

            Element account_id = doc.createElement(ACCOUNTID);
            account_id.appendChild(doc.createTextNode(Integer.toString(accs.get(i).getId())));
            account_root.appendChild(account_id);

            Element account_initbal = doc.createElement(ACCOUNTINITIALSALDO);
            account_initbal.appendChild(doc.createTextNode(String.format("%.2f", accs.get(i).getInitialSaldo())));
            account_root.appendChild(account_initbal);

            Element account_balance = doc.createElement(ACCOUNTSALDO);
            account_balance.appendChild(doc.createTextNode(String.format("%.2f", accs.get(i).getSaldo())));
            account_root.appendChild(account_balance);

            Element account_name = doc.createElement(ACCOUNTNAME);
            account_name.appendChild(doc.createTextNode(accs.get(i).getName()));
            account_root.appendChild(account_name);

            Element account_description = doc.createElement(ACCOUNTDESCRIPTION);
            account_description.appendChild(doc.createTextNode(accs.get(i).getDescription()));
            account_root.appendChild(account_description);

            Element account_isdefault = doc.createElement(ACCOUNTISDEFAULT);
            int is_default = 0;
            if(accs.get(i).getIsDefault()) is_default = 1;
            account_isdefault.appendChild(doc.createTextNode(Integer.toString(is_default)));
            account_root.appendChild(account_isdefault);

        }


        // add tags
        Element tags_root = doc.createElement(TAGSROOT);
        root.appendChild(tags_root);

        Vector<PoormanTag> tags = db.getAllTags();
        for(int i = 0; i < tags.size(); i++) {
            Element tag_root = doc.createElement(TAGROOT);
            tags_root.appendChild(tag_root);

            Element tag_id = doc.createElement(TAGID);
            tag_id.appendChild(doc.createTextNode(Integer.toString(tags.get(i).getId())));
            tag_root.appendChild(tag_id);

            Element tag_name = doc.createElement(TAGNAME);
            tag_name.appendChild(doc.createTextNode(tags.get(i).getName()));
            tag_root.appendChild(tag_name);

            Element tag_descr = doc.createElement(TAGDESCRIPTION);
            tag_descr.appendChild(doc.createTextNode(tags.get(i).getDescription()));
            tag_root.appendChild(tag_descr);

        }

        // add transactions
        Element actions_root = doc.createElement(ACTIONSROOT);
        root.appendChild(actions_root);

        Vector<PoormanAction> actions = db.getAllActions();
        for(int i = 0; i < actions.size(); i++) {
            Element action_root = doc.createElement(ACTIONROOT);
            actions_root.appendChild(action_root);

            Element action_id = doc.createElement(ACTIONID);
            action_id.appendChild(doc.createTextNode(Integer.toString(actions.get(i).getId())));
            action_root.appendChild(action_id);

            Element action_amount = doc.createElement(ACTIONAMOUNT);
            action_amount.appendChild(doc.createTextNode(String.format("%.2f", actions.get(i).getAmount())));
            action_root.appendChild(action_amount);

            Element action_time = doc.createElement(ACTIONTIME);
            action_time.appendChild(doc.createTextNode(Long.toString(actions.get(i).getTime())));
            action_root.appendChild(action_time);

            Element action_descr = doc.createElement(ACTIONDESCRIPTION);
            action_descr.appendChild(doc.createTextNode(actions.get(i).getDescription()));
            action_root.appendChild(action_descr);

            Element action_tofrom = doc.createElement(ACTIONTOFROM);
            action_tofrom.appendChild(doc.createTextNode(actions.get(i).getToFrom()));
            action_root.appendChild(action_tofrom);

            Element action_istempl = doc.createElement(ACTIONISTEMPLATE);
            int is_template = 0;
            if(actions.get(i).getIsTemplate()) is_template = 1;
            action_istempl.appendChild(doc.createTextNode(Integer.toString(is_template)));
            action_root.appendChild(action_istempl);

            Element action_account = doc.createElement(ACTIONACCOUNTID);
            action_account.appendChild(doc.createTextNode(Integer.toString(actions.get(i).getAccount().getId())));
            action_root.appendChild(action_account);

            Element action_tags = doc.createElement(ACTIONTAGS);
            action_root.appendChild(action_tags);

            // also add tag links here
            for(int j = 0; j < actions.get(i).getTags().size(); j++) {
                Element action_tag = doc.createElement(ACTIONTAGID);
                action_tag.appendChild(doc.createTextNode(Integer.toString(actions.get(i).getTags().get(j).getId())));
                action_tags.appendChild(action_tag);
            }
        }


        // Write XML to file
        TransformerFactory tr_factory = TransformerFactory.newInstance();
        Transformer transformer;
        try {
            transformer = tr_factory.newTransformer();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
            Log.d(TAG, "backup(): unable to create transformer");
            Toast.makeText(context, R.string.backup_failed, Toast.LENGTH_LONG).show();
            return;
        }

        // make the output file look prettier
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        DOMSource source = new DOMSource(doc);
        // check if backup dir exists
        File backup_dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + backup_directory);
        if(!backup_dir.exists()) {
            if(!backup_dir.mkdir()) {
                Toast.makeText(context, R.string.backup_failed, Toast.LENGTH_LONG).show();
                Log.d(TAG, "backup(): unable to create directory");
                return;
            }
        }

        File backup_file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + backup_directory, backup_filename);
        StreamResult result = new StreamResult(backup_file);
        try {
            transformer.transform(source, result);
        } catch (TransformerException e) {
            e.printStackTrace();
            Log.d(TAG, "backup(): unable to transform: '"+e.getMessage()+"'");
            Toast.makeText(context, R.string.backup_failed, Toast.LENGTH_LONG).show();
            return;
        }

        Log.d(TAG, "backup(): backup finished");

        Toast.makeText(context, R.string.backup_finished, Toast.LENGTH_LONG).show();
        dlg.dismiss();
    }


    // Reads a backup XML file and calls DatabaseHelper method for restoring the data.
    // Restoring a backup replaces the current database with data from the backup.
    private void restore() {

        int accounts_restored = 0;
        int actions_restored = 0;
        int tags_restored = 0;

        Document doc = openBackupFile();

        if(doc == null) {
            Toast.makeText(context, R.string.restore_failed, Toast.LENGTH_LONG).show();
            return;
        }

        // read account nodes
        NodeList account_nodes = doc.getElementsByTagName(ACCOUNTROOT);
        // iterate through all the accounts
        for(int i = 0; i < account_nodes.getLength(); i++) {
            Node account_node = account_nodes.item(i);
            if(account_node.getNodeType() == Node.ELEMENT_NODE) {
                Element node = (Element) account_node;
                PoormanBackup.Account account = new PoormanBackup.Account();
                account.id = getValue(ACCOUNTID, node);
                account.is_default = getValue(ACCOUNTISDEFAULT, node);
                account.description = getValue(ACCOUNTDESCRIPTION, node);
                account.name = getValue(ACCOUNTNAME, node);
                account.balance = getValue(ACCOUNTSALDO, node);
                account.initial_balance = getValue(ACCOUNTINITIALSALDO, node);

                Log.d(TAG, "restore(): account from xml:");
                Log.d(TAG, account.id);
                Log.d(TAG, account.is_default);
                Log.d(TAG, account.name);
                Log.d(TAG, account.description);
                Log.d(TAG, account.balance);
                Log.d(TAG, account.initial_balance);
                Log.d(TAG, "**********************************");

                backup.addAccount(account);
                accounts_restored++;

            }
        }

        // read action nodes
        NodeList action_nodes = doc.getElementsByTagName(ACTIONROOT);
        // iterate through all the actions
        for(int i = 0; i < action_nodes.getLength(); i++) {
            Node action_node = action_nodes.item(i);
            if(action_node.getNodeType() == Node.ELEMENT_NODE) {
                Element node = (Element) action_node;
                PoormanBackup.Action action = new PoormanBackup.Action();
                action.id = getValue(ACTIONID, node);
                action.amount = getValue(ACTIONAMOUNT, node);
                action.tofrom = getValue(ACTIONTOFROM, node);
                action.is_template = getValue(ACTIONISTEMPLATE, node);
                action.account = getValue(ACTIONACCOUNTID, node);
                action.timestamp = getValue(ACTIONTIME, node);
                action.description = getValue(ACTIONDESCRIPTION, node);

                Log.d(TAG, "restore(): action from xml:");
                Log.d(TAG, "action id: "+action.id);
                Log.d(TAG, action.tofrom);
                Log.d(TAG, action.amount);
                Log.d(TAG, action.description);
                Log.d(TAG, action.timestamp);
                Log.d(TAG, "account: "+action.account);
                Log.d(TAG, "is_template: "+action.is_template);

                // read tags (tag IDs)
                Vector<String> tags_vect = new Vector<String>();
                Node tags_parent = node.getElementsByTagName(ACTIONTAGS).item(0);
                NodeList tags_nodelist = tags_parent.getChildNodes();
                for(int j = 0; j < tags_nodelist.getLength(); j++) {
                    if(tags_nodelist.item(j).getNodeType() == Node.ELEMENT_NODE) {
                        Element tag_node = (Element) tags_nodelist.item(j);
                        String tag_id = tag_node.getChildNodes().item(0).getNodeValue();
                        tags_vect.add(tag_id);

                        Log.d(TAG, "tag: "+tag_id);
                    }
                }
                action.tags = tags_vect;

                Log.d(TAG, "**********************************");

                backup.addAction(action);
                actions_restored++;
            }
        }

        // read tag nodes
        NodeList tag_nodes = doc.getElementsByTagName(TAGROOT);
        // iterate through all the tags
        for(int i = 0; i < tag_nodes.getLength(); i++) {
            if(tag_nodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element node = (Element) tag_nodes.item(i);
                PoormanBackup.Tag tag = new PoormanBackup.Tag();

                tag.id = getValue(TAGID, node);
                tag.name = getValue(TAGNAME, node);
                tag.description = getValue(TAGDESCRIPTION, node);

                Log.d(TAG, "restore(): tag from xml:");
                Log.d(TAG, tag.id);
                Log.d(TAG, tag.name);
                Log.d(TAG, tag.description);
                Log.d(TAG, "**********************************");

                backup.addTag(tag);
                tags_restored++;
            }
        }


        Log.d(TAG, "restore(), XML file read successfully. Starting to handle the database.");

        db.restoreBackup(backup);

        Log.d(TAG, "restore(): restoring data finished: restored "+Integer.toString(accounts_restored)
                +" accounts, "+Integer.toString(actions_restored)+" transactions, "+Integer.toString(tags_restored)
                +" tags");

        ((PoormanActivity) context).refreshUi(true);

        Toast.makeText(context, R.string.restore_finished, Toast.LENGTH_LONG).show();

    }

    // returns value of a <tag> node under <parent>.
    private String getValue(String tag, Element parent) {
        /*
        NodeList nodes = parent.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = nodes.item(0);
        return node.getNodeValue();
        */

        // 20130824 null checks
        if(parent == null) return "";
        else if(parent.getElementsByTagName(tag) == null) return "";
        else if(parent.getElementsByTagName(tag).item(0) == null) return "";
        else if(parent.getElementsByTagName(tag).item(0).getChildNodes() == null) return "";
        else if(parent.getElementsByTagName(tag).item(0).getChildNodes().item(0) == null) return "";
        else if(parent.getElementsByTagName(tag).item(0).getChildNodes().item(0).getNodeValue() == null) return "";
        else return parent.getElementsByTagName(tag).item(0).getChildNodes().item(0).getNodeValue();

    }


}
