package dao;

public class Fees {
    private String company;
    private String count;
    private String fee;

    public Fees(String company, String count, String fee) {
        this.company = company;
        this.count = count;
        this.fee = fee;
    }


    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }
}
