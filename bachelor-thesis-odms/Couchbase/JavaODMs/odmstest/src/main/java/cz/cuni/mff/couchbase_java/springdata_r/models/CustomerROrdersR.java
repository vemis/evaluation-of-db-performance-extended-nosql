package cz.cuni.mff.couchbase_java.springdata_r.models;

public class CustomerROrdersR {

    private String cName;
    private String oOrderDate;
    private double oTotalPrice;
    //private String id;

    public CustomerROrdersR() {
    }

    public String getCName() {
        return cName;
    }

    public void setCName(String cName) {
        this.cName = cName;
    }

    public String getOOrderDate() {
        return oOrderDate;
    }

    public void setOOrderDate(String oOrderDate) {
        this.oOrderDate = oOrderDate;
    }

    public double getOTotalPrice() {
        return oTotalPrice;
    }

    public void setOTotalPrice(double oTotalPrice) {
        this.oTotalPrice = oTotalPrice;
    }

    /*public String getId() {
        return id;
    }*/

    /*public void setId(String id) {
        this.id = id;
    }*/

    @Override
    public String toString() {
        String result =     "cName " + cName +
                            "oOrderDate " + oOrderDate +
                            "oTotalPrice " + oTotalPrice;
        return result;
    }
}