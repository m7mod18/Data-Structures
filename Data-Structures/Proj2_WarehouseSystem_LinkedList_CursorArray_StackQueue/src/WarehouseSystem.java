package application;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class WarehouseSystem {
    private CategoryDoublyLinkedList categories;
    private ShipmentQueue pendingShipments = new ShipmentQueue();

    public WarehouseSystem() {
        categories = new CategoryDoublyLinkedList();
    }

    // ========== Category Operations ==========
    public ShipmentQueue getPendingShipments() {
        return pendingShipments;
    }
    public void addPendingShipment(Shipment shipment) {
        pendingShipments.enqueue(shipment);
    }


    public void addCategory(String id, String name, String desc) {
        if (categories.searchByID(id) != null) {
            System.out.println("Category ID already exists.");
            return;
        }

        Category newCat = new Category(id, name, desc);
        categories.addCategory(newCat);
        System.out.println("Category added: " + name);
    }

    public void listAllCategories() {
        categories.printAllCategories();
    }

    public Category searchCategory(String name) {
        return categories.searchByName(name);
    }

    public Category searchByID(String id) {
        return categories.searchByID(id);
    }


    public void deleteCategoryByID(String id) {
        categories.removeByID(id);
    }

    // ========== Product Operations ==========

    public void addProduct(String prodID, String name, String status, String categoryName) {
        Category cat = categories.searchByName(categoryName);
        if (cat == null) {
            System.out.println("Category not found.");
            return;
        }

        if (cat.getProductByID(prodID) != null) {
            System.out.println("Product ID already exists in this category.");
            return;
        }

        Product newProd = new Product(prodID, name, status);
        cat.addProduct(newProd);
        System.out.println("Product added to category: " + categoryName);
    }

    public Product searchProduct(String productID) {
    	CatNode current = categories.getHeadNode();
        while (current != null) {
            Product found = current.category.getProductByID(productID);
            if (found != null)
                return found;
            current = current.getNext();
        }
        return null;
    }

    // ========== Shipment Operations ==========

    public void addShipment(String shipmentID, String productID, int quantity, String date) {
        Product prod = searchProduct(productID);
        if (prod == null) {
            System.out.println("Product not found.");
            return;
        }

        Shipment shipment = new Shipment(shipmentID, productID, quantity, date);
        prod.getPendingShipments().enqueue(shipment);

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        ActionRecord record = new ActionRecord("Add Shipment", shipment, timestamp);
        prod.getUndoStack().push(record);
    }

    public void approveShipment(String productID) {
        Product prod = searchProduct(productID);
        if (prod == null || prod.getPendingShipments().isEmpty()) {
            System.out.println("No pending shipments to approve.");
            return;
        }

        Shipment shipment = prod.getPendingShipments().dequeue();
        prod.getInventoryStock().insertAtEnd(shipment);

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        ActionRecord record = new ActionRecord("Approve", shipment, timestamp);
        prod.getUndoStack().push(record);
    }

    public void cancelShipment(String productID) {
        Product prod = searchProduct(productID);
        if (prod == null || prod.getPendingShipments().isEmpty()) {
            System.out.println("No pending shipments to cancel.");
            return;
        }

        Shipment shipment = prod.getPendingShipments().dequeue();
        prod.getCanceledShipments().insertAtEnd(shipment);

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        ActionRecord record = new ActionRecord("Cancel", shipment, timestamp);
        prod.getUndoStack().push(record);
    }
    // ========== Display ==========

    public void displayAllProducts() {
    	CatNode current = categories.getHeadNode();
        while (current != null) {
            System.out.println("Category: " + current.category.getName());
            current.category.printAllProducts();
            current = current.getNext();
        }
    }
    public CategoryDoublyLinkedList getCategories() {
        return categories;
    }

}
