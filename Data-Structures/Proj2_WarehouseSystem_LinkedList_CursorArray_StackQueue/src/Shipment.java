package application;

public class Shipment {
    private String shipmentID;
    private String productID;
    private int quantity;
    private String date; 
    public Shipment() {}  

    public Shipment(String shipmentID, String productID, int quantity, String date) {
        this.shipmentID = shipmentID;
        this.productID = productID;
        this.quantity = quantity;
        this.date = date;
    }

    public String getShipmentID() {
        return shipmentID;
    }

    public String getProductID() {
        return productID;
    }

    public void setShipmentID(String shipmentID) {
		this.shipmentID = shipmentID;
	}

	public void setProductID(String productID) {
		this.productID = productID;
	}

	public int getQuantity() {
        return quantity;
    }

    public String getDate() {
        return date;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return shipmentID + " (" + productID + ") → " + quantity + " units on " + date;
    }
}

