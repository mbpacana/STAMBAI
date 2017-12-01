package dao;

public class RTData {

	private String time_in;
	private String time_out;
	private String duration;
	private String violation;
	private String rfid;
	private String loading_bay;

	public RTData(String loading_bay, String time_in, String time_out,
				  String RFID, String duration, String violation) {
		this.loading_bay = loading_bay;
		this.time_in = time_in;
		this.time_out = time_out;
		this.rfid = RFID;
		this.duration = duration;
		this.violation = violation;
	}

	public String getLoading_bay() {
		return loading_bay;
	}

	public void setLoading_bay(String loading_bay) {
		this.loading_bay = loading_bay;
	}

	public String getDuration() {
		return duration;
	}

	public String getViolation() {
		return violation;
	}

	public String getRFID() { return rfid; }

	public String getTime_in() {
		return time_in;
	}

	public String getTime_out() {
		return time_out;
	}

	public RTData() {}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public void setViolation(String violation) {
		this.violation = violation;
	}
}
