package no.domain.zak0.scrooge.dataclass;

import java.io.Serializable;

public class PoormanTag implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private int id;
	private String name;
	private String description;

    private double tag_total;

	public PoormanTag() {}
	
	public PoormanTag(int id, String name, String description) {
		this.id = id;
		this.name = name;
		this.description = description;
        this.tag_total = 0;
	}
	
	/* Setters and getters */
	public void setId(int id) {
		this.id = id;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

    public void setTagTotal(double total) {
        this.tag_total = total;
    }

	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}

    public double getTagTotal() {
        return tag_total;
    }
}
