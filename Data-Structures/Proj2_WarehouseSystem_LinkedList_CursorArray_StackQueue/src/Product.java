package application;

public class Product {
    private String productID;
    private String name;
    private String status; 

    private ShipmentQueue pendingShipments;         
    private CursorBasedLinkedList inventoryStock;   
    private CursorBasedLinkedList canceledShipments; 
    private ActionStack undoStack; 
    private ActionStack redoStack;

    public Product(String productID, String name, String status) {
        this.productID = productID;
        this.name = name;
        this.status = status;

        this.pendingShipments = new ShipmentQueue();
        this.inventoryStock = new CursorBasedLinkedList();
        this.canceledShipments = new CursorBasedLinkedList();
        this.undoStack = new ActionStack();
        this.redoStack = new ActionStack();
    }

    public CursorBasedLinkedList getInventoryStock() {
		return inventoryStock;
	}

	public void setInventoryStock(CursorBasedLinkedList inventoryStock) {
		this.inventoryStock = inventoryStock;
	}

	public CursorBasedLinkedList getCanceledShipments() {
		return canceledShipments;
	}

	public void setCanceledShipments(CursorBasedLinkedList canceledShipments) {
		this.canceledShipments = canceledShipments;
	}

	public void setProductID(String productID) {
		this.productID = productID;
	}

	public void setPendingShipments(ShipmentQueue pendingShipments) {
		this.pendingShipments = pendingShipments;
	}

	public void setUndoStack(ActionStack undoStack) {
		this.undoStack = undoStack;
	}

	public void setRedoStack(ActionStack redoStack) {
		this.redoStack = redoStack;
	}

	public void setName(String name) {
		this.name = name;
	}
    public String getProductID() { 
    	return productID; }
    public String getName() { 
    	return name; }
    public String getStatus() { 
    	return status; }

    public void setStatus(String status) { 
    	this.status = status; }

    public ShipmentQueue getPendingShipments() {
    	return pendingShipments; }
    public CursorBasedLinkedList getInventoryList() { 
    	return inventoryStock; }
    public CursorBasedLinkedList getCanceledList() {
    	return canceledShipments; }
    public ActionStack getUndoStack() {
    	return undoStack; }
    public ActionStack getRedoStack() { 
    	return redoStack; }

   
}
