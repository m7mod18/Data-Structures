package application;

public class ActionRecord {
    private String actionType; 
    private Shipment shipment;
    private String timestamp;

    public ActionRecord(String actionType, Shipment shipment, String timestamp) {
        this.actionType = actionType;
        this.shipment = shipment;
        this.timestamp = timestamp;
    }

    public String getActionType() {
    	return actionType; }
    public Shipment getShipment() { 
    	return shipment; }
    public String getTimestamp() {
    	return timestamp; }
}
