package no.domain.zak0.scrooge.dataclass;

import java.io.Serializable;

import android.util.Log;

public class PoormanAccount implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private static final String TAG = "PoormanAccount";
	
	private int id;
	private double initial_saldo;
	private double saldo;
	private String name;
	private String description;
	private boolean is_default;

    //20130824 added in attempt to reduce UI lag
    private double change_7_days;
    private double change_30_days;
	
	public PoormanAccount() {
        change_7_days = 0.0;
        change_30_days = 0.0;
    };
	
	// Changes account balace according to a new transaction
	public void addAction(PoormanAction act) {
		saldo += act.getAmount();
		Log.d(TAG, String.format("%.2f", act.getAmount())+" added to account "+name);
	}
	
	public void removeAction(PoormanAction act) {
		saldo -= act.getAmount();
		Log.d(TAG, String.format("%.2f", act.getAmount())+" reduced from account "+name);
	}
	
	// setters and getters:
	public void setId(int id) { this.id = id; }
	public void setInitialSaldo(double saldo) { this.initial_saldo = saldo; }
	public void setSaldo(double saldo) { this.saldo = saldo; }
	public void setName(String name) { this.name = name; }
	public void setDescription(String description) { this.description = description; }
	public void setIsDefault(boolean is_default) { this.is_default = is_default; }
    public void setChange7Days(double change_7) { this.change_7_days = change_7; }
    public void setChange30Days(double change_30) { this.change_30_days = change_30; }
	
	public int getId() { return id; }
	public double getInitialSaldo() { return initial_saldo; }
	public double getSaldo() { return saldo; }
	public String getName() { return name; }
	public String getDescription() { return description; }
	public boolean getIsDefault() { return is_default; }
    public double getChange7Days() { return change_7_days; }
    public double getChange30Days() { return change_30_days; }
	
	// ...and more complex methods:
}
