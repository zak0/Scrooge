package no.domain.zak0.scrooge.dataclass;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;


public class PoormanAction implements Serializable {

	private static final long serialVersionUID = 1L;

	private final static String TAG = "PoormanAction";
	
	private int id;
	private double amount;
	private long time; //System.currenttimemillis() of the time of transaction
	private int hours; // hour part of the time
	private int minutes; // minute part of the time
	private String description;
	private String tofrom; // recipient of the transaction
	private Vector<PoormanTag> tags;
	private PoormanAccount account; // account the transaction belongs to
	private boolean is_template; // flag for telling if the transaction is to be used as a template
	
	public PoormanAction() {
		tags = new Vector<PoormanTag>();
	}
	
	// Copy constructor
	public PoormanAction(PoormanAction act) {
		this.id = act.id;
		this.amount = act.amount;
		this.time = act.time;
		this.hours = act.hours;
		this.minutes = act.minutes;
		this.description = act.description;
		this.tofrom = act.tofrom;
		this.tags = act.tags;
		this.account = act.account;
		this.is_template = act.is_template;
	}
	
	public PoormanAction(int id, float amount, long time, String description, String tofrom) {
		this.id = id;
		this.amount = amount;
		this.time = time;
		this.description = description;
		this.tofrom = tofrom;
		
		this.tags = new Vector<PoormanTag>();
	}
	
	// Adds a tag into tags vector
	public void addTag(PoormanTag tag) {
		
		// Don't add if tag already exists
		if(!tags.contains(tag)) tags.add(tag);
	}
	
	// Clears all the tags.
	// This is called before saving changes to tags
	public void clearTags() {
		tags = new Vector<PoormanTag>();
	}
	
	
	/* Setters and getters */
	public void setId(int id) {
		this.id = id;
	}
	
	public void setAmount(float amount) {
		this.amount = amount;
	}
	
	public void setTime(long l) {
		this.time = l;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public void setHoursAndMinutes(int hrs, int mins) {
		this.minutes = mins;
		this.hours = hrs;
	}
	
	public void setToFrom(String tofrom) {
		this.tofrom = tofrom;
	}
	
	public void setAccount(PoormanAccount account) {
		this.account = account;
	}
	
	public void setIsTemplate(boolean is_template) {
		this.is_template = is_template;
	}
	
	public int getId() {
		return id;
	}

	public double getAmount() {
		return amount;
	}
	
	public String getAmountString() {
		String ret = "";
		if(amount >= 0) ret = ret + "+";
		ret = ret + String.format("%.2f", amount);
		return ret;
	}
	
	
	public long getTime() {
		return time;
	}
	
	public String getTimeString() {
		String ret = "";
		
		SimpleDateFormat sdf = new SimpleDateFormat("MMMdd yyyy, HH:mm");

        Date resultdate = new Date(time);
        ret = sdf.format(resultdate);
		
		
		return ret;
	}
	
	public String getDescription() {
		return description;
	}
	
	public int getHours() {
		return hours;
	}
	
	public int getMinutes() {
		return minutes;
	}
	
	public String getToFrom() {
		return tofrom;
	}
	
	public Vector<PoormanTag> getTags() {
		return tags;
	}
	
	public PoormanAccount getAccount() {
		return account;
	}
	
	public boolean getIsTemplate() {
		return is_template;
	}
}
