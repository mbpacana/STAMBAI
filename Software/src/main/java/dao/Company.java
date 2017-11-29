package dao;

import java.util.ArrayList;

public class Company {

	private String contact_person;
	private ArrayList<String> RFID;
	
	public Company() {
		super();
	}

	public String getContact_person() {
		return contact_person;
	}

	public ArrayList<String> getRFID() {
		return RFID;
	}

	public Company(String contact_person, ArrayList<String> rFID) {
		super();
		this.contact_person = contact_person;
		RFID = rFID;
	}
	
	
}
