package application;

public class Category {
    private String categoryID;
    private String name;
    private String description;

    private ProductLinkedList productList; 

    public Category(String categoryID, String name, String description) {
        this.categoryID = categoryID;
        this.name = name;
        this.description = description;
        this.productList = new ProductLinkedList();
    }

    public String getCategoryID() {
    	return categoryID; }
    public String getName() { 
    	return name; }
    public String getDescription() {
    	return description; }

    public void setName(String name) { 
    	this.name = name; }
    public void setDescription(String description) {
    	this.description = description; }

    public ProductLinkedList getProductList() {
        return productList;
    }

    public void addProduct(Product product) {
        productList.addProduct(product);
    }

    public void removeProductByID(String productID) {
        productList.removeProductByID(productID);
    }

    public Product getProductByID(String productID) {
        return productList.getProductByID(productID);
    }

    public void printAllProducts() {
        productList.printAllProducts();
    }

    public boolean hasNoProducts() {
        return productList.isEmpty();
    }
}
