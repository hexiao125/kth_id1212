package se.kth.id1212.restful.webstore.integration;

public class OrderEntity{
    
    private String orderId;
    private int quanOne;
    private int quanTwo;
    private int amount;
    private String username;
    private String bank;
    private String bankAcc;
    private String phone;
    private String address;
                    
    public OrderEntity() {
    }

    public OrderEntity(String orderId, int quanOne, int quanTwo, int amount, String username, String bank, 
            String bankAcc, String phone, String address) {
        this.orderId = orderId;
        this.quanOne = quanOne;
        this.quanTwo = quanTwo;
        this.amount = amount;
        this.username = username;
        this.bank = bank;
        this.bankAcc = bankAcc;
        this.phone = phone;
        this.address = address;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getUsername() {
        return username;
    }

    public int getQuanOne() {
        return quanOne;
    }

    public int getQuanTwo() {
        return quanTwo;
    }

    public int getAmount() {
        return amount;
    }

    public String getBank() {
        return bank;
    }

    public String getBankAcc() {
        return bankAcc;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String setOrderId(String orderId) {
        return this.orderId = orderId;
    }
  
    public void setQuanOne(int quanOne) {
        this.quanOne = quanOne;
    }

    public void setQuanTwo(int quanTwo) {
        this.quanTwo = quanTwo;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setUsername(String user) {
        this.username = user;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public void setBankAcc(String bankAcc) {
        this.bankAcc = bankAcc;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAddress(String address) {
        this.address = address;
    }
  
}
