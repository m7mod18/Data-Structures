package application;

public class LogEntry {
    private String timestamp;
    private String action;
    private String shipmentID;
    private String productID;
    private String quantityChange;

    public LogEntry(String timestamp, String action, String shipmentID, String productID, String quantityChange) {
        this.timestamp = timestamp;
        this.action = action;
        this.shipmentID = shipmentID;
        this.productID = productID;
        this.quantityChange = quantityChange;
    }

    public String getTimestamp() { return timestamp; }
    public String getAction() { return action; }
    public String getShipmentID() { return shipmentID; }
    public String getProductID() { return productID; }
    public String getQuantityChange() { return quantityChange; }
}
