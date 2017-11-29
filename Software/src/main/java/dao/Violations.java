package dao;

/**
 * Created by next on 11/22/2017.
 */
public class Violations {

    private String count;
    private String company;

    public Violations(String company, String count) {
        this.count = count;
        this.company = company;
    }


    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }
}
