package dao;

/*
	POJO Class
	Container for a Bus Information
 */
public class Bus_Info {
	private String company;
	private String plate_num;
	private String destination;
	private String type;
	private String BusID;
	private String seat_cap;//maguba ba ang database kung i string ni nako?
	private int index;

	public Bus_Info() {}

	/*
		Constructor used for firebase purposes
	 */
	public Bus_Info(String plate_num, String company, String destination, String seat_cap, String type) {
		this.company = company;
		this.plate_num = plate_num;
		this.destination = destination;
		this.seat_cap = seat_cap;
		this.type = type;
	}

	/*
		Constructor used for tableview purposes
	 */
	public Bus_Info( String rfid,String plate_num, String company,String destination, String seat_cap, String type) {
		this.BusID = rfid;
		this.company = company;
		this.plate_num = plate_num;
		this.destination = destination;
		this.seat_cap = seat_cap;
		this.type = type;
	}


	public String toString(){
		return index+","+BusID + ","+plate_num +","+ company +"," +destination +"," +seat_cap +","+ type;
	}
	public String getCompany() {
		return company;
	}

	public String getType() {
		return type;
	}

	public String getPlate_num() {
		return plate_num;
	}

	public String getDestination() {
		return destination;
	}

	public String getSeat_cap() {
		return seat_cap;
	}

	public String getBusID() {
		return BusID;
	}

	public void setIndex(int index){this.index = index;}
	public int getIndex(){return this.index;}
	public void setBusID(String BusID){ this.BusID = BusID;}
	public void setCompany(String company) {
		this.company = company;
	}

	public void setPlate_num(String plate_num) {
		this.plate_num = plate_num;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public void setSeat_cap(String seat_cap) {
		this.seat_cap = seat_cap;
	}

	public void setType(String type) {
		this.type = type;
	}

}