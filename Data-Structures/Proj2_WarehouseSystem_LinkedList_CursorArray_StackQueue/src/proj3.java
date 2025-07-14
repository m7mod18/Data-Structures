package application;
	
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;


public class proj3 extends Application {
    WarehouseSystem warehouseSystem = new WarehouseSystem();
    TableView<Category> categoryTable = new TableView<>();

    TableView<Product> productTable = new TableView<>();
    TableView<Shipment> pendingTable = new TableView<>();
    TableView<Shipment> inventoryTable = new TableView<>();
    TableView<Shipment> canceledTable = new TableView<>();
    ObservableList<LogEntry> logList = FXCollections.observableArrayList();
    TableView<LogEntry> logTable = new TableView<>();
    ActionStack undoStack = new ActionStack();
    ActionStack redoStack = new ActionStack();
    TableView<Shipment> canceledShipmentsTable = new TableView<>();
    TableView<Shipment> inventoryShipmentsTable = new TableView<>();
     TableView<Product> categoryProductTable = new TableView<>();
     TableView<Product> categoryProductTablenext = new TableView<>();
     private CatNode currentCatNode;
    TableView<Shipment> pendingShipmentsTable = new TableView<>();
    private void displayCategoryDetails(CatNode node) {
        if (node == null) return;

        ObservableList<Product> products = FXCollections.observableArrayList();
        ProductLinkedList productList = node.getCategory().getProductList();
        PNode pNode = productList.getHead();

        while (pNode != null) {
            products.add(pNode.getProduct());
            pNode = pNode.getNext();
        }
        categoryProductTablenext.setItems(products);

        ObservableList<Category> currentCategory = FXCollections.observableArrayList();
        currentCategory.add(node.getCategory());
        categoryTable.setItems(currentCategory);
    }

    @Override
    public void start(Stage primaryStage) {
        Pane firstPage = new Pane();

        ImageView backgroundImage = new ImageView(new Image("s8.gif"));
        backgroundImage.setFitWidth(800);
        backgroundImage.setFitHeight(800);

        ImageView titleIcon = new ImageView(new Image("open.png"));
        titleIcon.setFitHeight(50);
        titleIcon.setFitWidth(50);

        Label titleLabel = new Label("Smart Warehouse Management System", titleIcon);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setStyle("-fx-text-fill: white;");
        titleLabel.setLayoutX(150);
        titleLabel.setLayoutY(50);

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));

        Button loadProductsBtn = createStyledButton("Open Products File", "open.png");
        Button loadShipmentsBtn = createStyledButton("Open Shipments File", "open.png");
        loadShipmentsBtn.setDisable(true);

        loadProductsBtn.setOnAction(e -> {
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
    	        readProductsFromFile(file, warehouseSystem, categoryTable);
    	        updateCategoryTable(warehouseSystem, categoryTable);
    	        updateProductTableFromSystem(); 
            	showSuccessAlert("Loaded: " + file.getName());
                loadShipmentsBtn.setDisable(false);
            }
        });

        loadShipmentsBtn.setOnAction(e -> {
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
            	readShipmentsFromFile(file);
            	updatePendingTable(); 

                showSuccessAlert("Loaded: " + file.getName());
            }
        });

        VBox loadButtons = new VBox(15, loadProductsBtn, loadShipmentsBtn);
        loadButtons.setLayoutX(300);
        loadButtons.setLayoutY(150);

        MenuBar menuBar = new MenuBar();
        menuBar.setPrefWidth(800);
        Menu fileMenu = new Menu("File");

        MenuItem saveProducts = new MenuItem("Save Products");
        MenuItem saveShipments = new MenuItem("Save Shipments");

        saveProducts.setOnAction(e -> {
            File file = fileChooser.showSaveDialog(primaryStage);
            if (file != null) {
                try (PrintWriter writer = new PrintWriter(file)) {
                    writer.println("ProductID,Name,CategoryName,Status");

                    CatNode currentCat = warehouseSystem.getCategories().getHeadNode();
                    while (currentCat != null) {
                        String catName = currentCat.getCategory().getName();
                        ProductLinkedList productList = currentCat.getCategory().getProductList();
                        PNode p = productList.getHead();

                        while (p != null) {
                            Product prod = p.getProduct();
                            writer.println(prod.getProductID() + "," + prod.getName() + "," + catName + "," + prod.getStatus());
                            p = p.getNext();
                        }

                        currentCat = currentCat.getNext();
                    }

                    showSuccessAlert("Products saved successfully to " + file.getName());
                } catch (IOException ex) {
                    showErrorAlert("Failed to save products: " + ex.getMessage());
                }
            }
        });


        saveShipments.setOnAction(e -> {
            File file = fileChooser.showSaveDialog(primaryStage);
            if (file != null) {
                try (PrintWriter writer = new PrintWriter(file)) {
                    writer.println("ShipmentID,ProductID,Quantity,Date");

                    CatNode currentCat = warehouseSystem.getCategories().getHeadNode();
                    while (currentCat != null) {
                        ProductLinkedList products = currentCat.getCategory().getProductList();
                        PNode p = products.getHead();

                        while (p != null) {
                            Product product = p.getProduct();
                            QNode q = product.getPendingShipments().getFront();
                            while (q != null) {
                                Shipment s = q.getShipment();
                                writer.println(s.getShipmentID() + "," + s.getProductID() + "," + s.getQuantity() + "," + s.getDate());
                                q = q.getNext();
                            }

                            p = p.getNext();
                        }

                        currentCat = currentCat.getNext();
                    }

                    showSuccessAlert("Shipments saved successfully to " + file.getName());
                } catch (IOException ex) {
                    showErrorAlert("Failed to save shipments: " + ex.getMessage());
                }
            }
        });

        fileMenu.getItems().addAll(saveProducts, saveShipments);
        menuBar.getMenus().add(fileMenu);

        Button nextPageBtn = createStyledButton("Next Page", "next.png");
        nextPageBtn.setLayoutX(350);
        nextPageBtn.setLayoutY(400);

        firstPage.getChildren().addAll(backgroundImage, menuBar, titleLabel, loadButtons, nextPageBtn);

        Scene firstScene = new Scene(firstPage, 800, 800);
        Scene secondScene = new Scene(createSecondPage(primaryStage, firstScene), 800, 800);
        nextPageBtn.setOnAction(e -> {
            primaryStage.setScene(secondScene);
        }); 
        primaryStage.setTitle("Smart Warehouse System");
        primaryStage.setScene(firstScene);
        primaryStage.show();
    }
    private Button createStyledButton(String text,
    		String iconPath) {
        ImageView icon = new ImageView(new Image(iconPath));
        icon.setFitHeight(20);
        icon.setFitWidth(20);
        Button button = new Button(text, icon);
        String defaultStyle = "-fx-font-size: 14px; -fx-background-color: #555; -fx-text-fill: white; -fx-padding: 10px 20px; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.5), 5, 0.0, 2, 2);";
        String hoverStyle = "-fx-font-size: 14px; -fx-background-color: #777; -fx-text-fill: white; -fx-padding: 10px 20px; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.7), 8, 0.0, 3, 3);";

        button.setStyle(defaultStyle);

        button.setOnMouseEntered(e -> {
            button.setStyle(hoverStyle);  
            button.setScaleX(1.05);      
            button.setScaleY(1.05);       
        });

        button.setOnMouseExited(e -> {
            button.setStyle(defaultStyle);  
            button.setScaleX(1);            
            button.setScaleY(1);           
        });

        button.setOnMousePressed(e -> {
            button.setScaleX(0.95);  
            button.setScaleY(0.95);  
        });

        button.setOnMouseReleased(e -> {
            button.setScaleX(1);     
            button.setScaleY(1);     
        });

        return button;
    }

    private void showSuccessAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setContentText(msg);
        alert.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
    public TabPane createSecondPage(Stage primaryStage, Scene firstScene) {
    	  TabPane tabPane = new TabPane();

    	    // ================= Tab 1: Categories =================
    	    Tab categoriesTab = new Tab("Categories");
    	    Pane categoriesPane = new Pane();
    	    categoriesTab.setContent(categoriesPane);

    	    Image catBackgroundImage = new Image("s2.gif");
    	    BackgroundImage catBackground = new BackgroundImage(
    	        catBackgroundImage,
    	        BackgroundRepeat.NO_REPEAT,
    	        BackgroundRepeat.NO_REPEAT,
    	        BackgroundPosition.CENTER,
    	        new BackgroundSize(800, 800, false, false, false, false)
    	    );
    	    categoriesPane.setBackground(new Background(catBackground));

    	    Label categoriesTitle = new Label("Category Management");
    	    categoriesTitle.setFont(Font.font("Arial", FontWeight.BOLD, 28));
    	    categoriesTitle.setStyle("-fx-text-fill: #FFDD00;");
    	    categoriesTitle.setLayoutX(250);
    	    categoriesTitle.setLayoutY(10);

    	    categoryTable.setLayoutX(100);
    	    categoryTable.setLayoutY(60);
    	    categoryTable.setPrefSize(600, 250);
    	    categoryTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
    	        if (newSelection != null) {
    	            displayProductsOfCategory(newSelection);
    	        }
    	    });

    	    TableColumn<Category, String> catIdCol = new TableColumn<>("Category ID");
    	    catIdCol.setCellValueFactory(new PropertyValueFactory<>("categoryID"));

    	    TableColumn<Category, String> catNameCol = new TableColumn<>("Name");
    	    catNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

    	    TableColumn<Category, String> catDescCol = new TableColumn<>("Description");
    	    catDescCol.setCellValueFactory(new PropertyValueFactory<>("description"));

    	    categoryTable.getColumns().addAll(catIdCol, catNameCol, catDescCol);

    	    Button addCategoryBtn = createStyledButton("Add Category", "add.png");
    	    addCategoryBtn.setOnAction(e -> openAddCategoryStage());

    	    Button updateCategoryBtn = createStyledButton("Update Category", "update.png");
    	    updateCategoryBtn.setOnAction(e -> {
    	        Category selected = categoryTable.getSelectionModel().getSelectedItem();
    	        openUpdateCategoryStage(selected);
    	    });
    	    
    	    Button deleteCategoryBtn = createStyledButton("Delete Category", "remove.png");
    	    deleteCategoryBtn.setOnAction(e -> openDeleteCategoryStage());
    	    
    	    Button searchCategoryBtn = createStyledButton("Search Category", "search.png");
    	    searchCategoryBtn.setOnAction(e -> openSearchCategoryStage());

    	    Button listCategoriesBtn = createStyledButton("List Categories", "print.png");
    	    listCategoriesBtn.setOnAction(e -> listAllCategories());
    	    Button sortCategoriesBtn = createStyledButton("Sort A-Z", "print.png");

    	    HBox operationButtons = new HBox(10,
    	        addCategoryBtn, updateCategoryBtn, deleteCategoryBtn,
    	        searchCategoryBtn, listCategoriesBtn
    	    );
    	    
    	    operationButtons.setAlignment(Pos.CENTER);
    	    sortCategoriesBtn.setOnAction(e -> sortCategoriesByNameAscending());

    	    Button prevBtn = createStyledButton("Previous", "prev.png");
    	    Button nextBtn = createStyledButton("Next", "next.png");
    	    Button backBtn = createStyledButton("Previous Page", "prev.png");
    	    nextBtn.setOnAction(e -> {
    	        if (currentCatNode == null) {
    	            currentCatNode = warehouseSystem.getCategories().getHeadNode();
    	        } else {
    	            currentCatNode = currentCatNode.getNext();
    	        }

    	        if (currentCatNode != null) {
    	            displayCategoryDetails(currentCatNode); 
    	        } else {
    	            showErrorAlert("No more categories available.");
    	        }
    	    });



    	    prevBtn.setOnAction(e -> {
    	        if (currentCatNode == null) {
    	            currentCatNode = warehouseSystem.getCategories().getHeadNode();
    	            while (currentCatNode != null && currentCatNode.getNext() != null) {
    	                currentCatNode = currentCatNode.getNext();
    	            }
    	        } else {
    	            currentCatNode = currentCatNode.getPrev();
    	        }

    	        if (currentCatNode != null) {
    	            displayCategoryDetails(currentCatNode);
    	        } else {
    	            showErrorAlert("No previous categories available.");
    	        }
    	    });


    	    backBtn.setOnAction(e -> primaryStage.setScene(firstScene));
    	    HBox navButtons = new HBox(15, prevBtn, nextBtn, backBtn);
    	    navButtons.setAlignment(Pos.CENTER);

    	    VBox buttonBox = new VBox(15, operationButtons, navButtons);
    	    buttonBox.setLayoutX(100);
    	    buttonBox.setLayoutY(330);
    	    buttonBox.setPrefWidth(600);
    	    buttonBox.setAlignment(Pos.CENTER);
    	   

    	    categoryProductTablenext.setLayoutX(100);
    	    categoryProductTablenext.setLayoutY(450);
    	    categoryProductTablenext.setPrefSize(600, 100);

    	    TableColumn<Product, String> prodIdCol = new TableColumn<>("Product ID");
    	    prodIdCol.setCellValueFactory(new PropertyValueFactory<>("productID"));

    	    TableColumn<Product, String> prodNameCol = new TableColumn<>("Name");
    	    prodNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

    	    TableColumn<Product, String> prodStatusCol = new TableColumn<>("Status");
    	    prodStatusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

    	    categoryProductTablenext.getColumns().addAll(prodIdCol, prodNameCol, prodStatusCol);

    	    sortCategoriesBtn.setLayoutX(340);
    	    sortCategoriesBtn.setLayoutY(600);

    	    categoriesPane.getChildren().addAll(
    	    	    categoriesTitle,
    	    	    categoryTable,
    	    	    buttonBox,
    	    	    
    	    	    categoryProductTablenext, 
    	    	    sortCategoriesBtn
    	    	);
        // ================= Tab 2: Products =================
        Tab productsTab = new Tab("Products");
        Pane productsPane = new Pane();
        productsTab.setContent(productsPane);

        Image prodBackgroundImage = new Image("s3.gif");
        BackgroundImage prodBackground = new BackgroundImage(
            prodBackgroundImage,
            BackgroundRepeat.NO_REPEAT,
            BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.CENTER,
            new BackgroundSize(800, 800, false, false, false, false)
        );
        productsPane.setBackground(new Background(prodBackground));

        Label productsTitle = new Label("Product Management");
        productsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        productsTitle.setStyle("-fx-text-fill: #FFDD00;");
        productsTitle.setLayoutX(270);
        productsTitle.setLayoutY(10);

        productTable.setLayoutX(100);
        productTable.setLayoutY(60);
        productTable.setPrefSize(600, 300);

        TableColumn<Product, String> prodIdCol1 = new TableColumn<>("Product ID");
        prodIdCol1.setCellValueFactory(new PropertyValueFactory<>("productID"));

        TableColumn<Product, String> prodNameCol1 = new TableColumn<>("Name");
        prodNameCol1.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Product, String> prodStatusCol1 = new TableColumn<>("Status");
        prodStatusCol1.setCellValueFactory(new PropertyValueFactory<>("status"));

        productTable.getColumns().addAll(prodIdCol1, prodNameCol1, prodStatusCol1);

        Button addProdBtn = createStyledButton("Add Product", "add.png");
        Button delProdBtn = createStyledButton("Delete Product", "remove.png");
        Button updateProdBtn = createStyledButton("Update Product", "update.png");
        Button searchProdBtn = createStyledButton("Search Product", "search.png");
        Button refreshProdBtn = createStyledButton("Refresh", "print.png");
        Button sortProdBtn = createStyledButton("Sort A-Z", "print.png");

        addProdBtn.setOnAction(e -> openAddProductStage());
        delProdBtn.setOnAction(e -> openDeleteProductStage());
        updateProdBtn.setOnAction(e -> openUpdateProductStage());
        searchProdBtn.setOnAction(e -> openSearchProductStage());
        refreshProdBtn.setOnAction(e -> refreshProductTable());
        sortProdBtn.setOnAction(e -> sortProductsByNameAscending());
        ComboBox<String> filterBox = new ComboBox<>();
        filterBox.getItems().addAll("Active", "Inactive");
        filterBox.setPromptText("Filter by status");

        filterBox.setOnAction(e -> {
            String selected = filterBox.getValue();
            if (selected != null) {
                filterProductsByStatus(selected);
            }
        });

        HBox mainProdButtons = new HBox(10, addProdBtn, delProdBtn, updateProdBtn, searchProdBtn);
        HBox lowerProdButtons = new HBox(10, refreshProdBtn, sortProdBtn);

        mainProdButtons.setLayoutX(150);
        mainProdButtons.setLayoutY(380);

        lowerProdButtons.setLayoutX(250);
        lowerProdButtons.setLayoutY(430);
        filterBox.setLayoutX(550);
        filterBox.setLayoutY(430);


        productsPane.getChildren().addAll(
            productsTitle,
            productTable,
            mainProdButtons,
            lowerProdButtons,
            filterBox
        );

     // ================= Tab 3: Shipments =================
        Tab shipmentsTab = new Tab("Shipments");
        Pane shipmentsPane = new Pane();
        shipmentsTab.setContent(shipmentsPane);

        Image shipBackgroundImage = new Image("s5.gif");
        BackgroundImage shipBackground = new BackgroundImage(
            shipBackgroundImage,
            BackgroundRepeat.NO_REPEAT,
            BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.CENTER,
            new BackgroundSize(800, 800, false, false, false, false)
        );
        shipmentsPane.setBackground(new Background(shipBackground));

        Label shipmentsTitle = new Label("Shipment Management");
        shipmentsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        shipmentsTitle.setStyle("-fx-text-fill: #FFDD00;");
        shipmentsTitle.setLayoutX(220);
        shipmentsTitle.setLayoutY(10);

        Label pendingLabel = new Label("Pending Shipments");
        pendingLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        pendingLabel.setStyle("-fx-text-fill: white;");
        pendingLabel.setLayoutX(310);
        pendingLabel.setLayoutY(40);

        Label inventoryLabel1 = new Label("Inventory Stock");
        inventoryLabel1.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        inventoryLabel1.setStyle("-fx-text-fill: white;");
        inventoryLabel1.setLayoutX(120);
        inventoryLabel1.setLayoutY(270);

        Label canceledLabel1 = new Label("Canceled Shipments");
        canceledLabel1.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        canceledLabel1.setStyle("-fx-text-fill: white;");
        canceledLabel1.setLayoutX(530);
        canceledLabel1.setLayoutY(270);

        pendingTable.setLayoutX(200);
        pendingTable.setLayoutY(60);
        pendingTable.setPrefSize(400, 200);

        inventoryTable.setLayoutX(60);
        inventoryTable.setLayoutY(290);
        inventoryTable.setPrefSize(340, 200);

        canceledTable.setLayoutX(430);
        canceledTable.setLayoutY(290);
        canceledTable.setPrefSize(340, 200);

        TableColumn<Shipment, String> shipIdCol1 = new TableColumn<>("Shipment ID");
        shipIdCol1.setCellValueFactory(new PropertyValueFactory<>("shipmentID"));

        TableColumn<Shipment, String> prodIdCol11 = new TableColumn<>("Product ID");
        prodIdCol11.setCellValueFactory(new PropertyValueFactory<>("productID"));

        TableColumn<Shipment, Integer> qtyCol1 = new TableColumn<>("Quantity");
        qtyCol1.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<Shipment, String> dateCol1 = new TableColumn<>("Date");
        dateCol1.setCellValueFactory(new PropertyValueFactory<>("date"));

        pendingTable.getColumns().addAll(shipIdCol1, prodIdCol11, qtyCol1, dateCol1);

        TableColumn<Shipment, String> shipIdCol2 = new TableColumn<>("Shipment ID");
        shipIdCol2.setCellValueFactory(new PropertyValueFactory<>("shipmentID"));

        TableColumn<Shipment, String> prodIdCol2 = new TableColumn<>("Product ID");
        prodIdCol2.setCellValueFactory(new PropertyValueFactory<>("productID"));

        TableColumn<Shipment, Integer> qtyCol2 = new TableColumn<>("Quantity");
        qtyCol2.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<Shipment, String> dateCol2 = new TableColumn<>("Date");
        dateCol2.setCellValueFactory(new PropertyValueFactory<>("date"));

        inventoryTable.getColumns().addAll(shipIdCol2, prodIdCol2, qtyCol2, dateCol2);

        TableColumn<Shipment, String> shipIdCol3 = new TableColumn<>("Shipment ID");
        shipIdCol3.setCellValueFactory(new PropertyValueFactory<>("shipmentID"));

        TableColumn<Shipment, String> prodIdCol3 = new TableColumn<>("Product ID");
        prodIdCol3.setCellValueFactory(new PropertyValueFactory<>("productID"));

        TableColumn<Shipment, Integer> qtyCol3 = new TableColumn<>("Quantity");
        qtyCol3.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<Shipment, String> dateCol3 = new TableColumn<>("Date");
        dateCol3.setCellValueFactory(new PropertyValueFactory<>("date"));

        canceledTable.getColumns().addAll(shipIdCol3, prodIdCol3, qtyCol3, dateCol3);

        Button addShipmentBtn = createStyledButton("Add Shipment", "add.png");
        
        addShipmentBtn.setOnAction(e -> openAddShipmentStage());

        Button approveBtn = createStyledButton("Approve", "next.png");
        approveBtn.setOnAction(e -> openApproveShipmentStage());

        Button cancelBtn = createStyledButton("Cancel", "remove.png");
        cancelBtn.setOnAction(e -> openCancelShipmentStage());


        Button undoBtn = createStyledButton("Undo", "undo.png");
        undoBtn.setOnAction(e -> {
            if (undoStack.isEmpty()) {
                showErrorAlert("No actions to undo.");
                return;
            }

            ActionRecord last = undoStack.pop();
            Shipment s = last.getShipment();
            String type = last.getActionType();

            Product p = warehouseSystem.searchProduct(s.getProductID());
            if (p == null) {
                showErrorAlert("Product not found.");
                return;
            }

            if (type.equals("Approve")) {
                p.getInventoryStock().removeByShipmentID(s.getShipmentID());
                p.getPendingShipments().enqueue(s);
            } else if (type.equals("Cancel")) {
                p.getCanceledShipments().removeByShipmentID(s.getShipmentID());
                p.getPendingShipments().enqueue(s);
            }

            String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            redoStack.push(new ActionRecord(type, s, now));

            updatePendingTable();
            updateInventoryTable();
            updateCanceledTable();

            logList.add(new LogEntry(now, "Undo " + type, s.getShipmentID(), s.getProductID(), "↺ " + s.getQuantity()));
            logTable.setItems(logList);
        });

        Button redoBtn = createStyledButton("Redo", "redo.png");
        redoBtn.setOnAction(e -> {
            if (redoStack.isEmpty()) {
                showErrorAlert("No actions to redo.");
                return;
            }

            ActionRecord last = redoStack.pop();
            Shipment s = last.getShipment();
            String type = last.getActionType();

            Product p = warehouseSystem.searchProduct(s.getProductID());
            if (p == null) {
                showErrorAlert("Product not found.");
                return;
            }

            if (type.equals("Approve")) {
                p.getPendingShipments().removeByID(s.getShipmentID());
                p.getInventoryStock().insertAtEnd(s);
            } else if (type.equals("Cancel")) {
                p.getPendingShipments().removeByID(s.getShipmentID());
                p.getCanceledShipments().insertAtEnd(s);
            }
            String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            undoStack.push(new ActionRecord(type, s, now));

            updatePendingTable();
            updateInventoryTable();
            updateCanceledTable();

            logList.add(new LogEntry(now, "Redo " + type, s.getShipmentID(), s.getProductID(), "↻ " + s.getQuantity()));
            logTable.setItems(logList);
        });

        HBox shipmentButtons = new HBox(10, addShipmentBtn,approveBtn, cancelBtn, undoBtn, redoBtn);
        shipmentButtons.setLayoutX(130);
        shipmentButtons.setLayoutY(510);

        shipmentsPane.getChildren().addAll(
            shipmentsTitle,
            pendingLabel,
            inventoryLabel1,
            canceledLabel1,
            pendingTable,
            inventoryTable,
            canceledTable,
            shipmentButtons
        );
     // ================= Tab 4: Shipment Log =================
        Tab logTab = new Tab("Shipment Log");
        Pane logPane = new Pane();
        logTab.setContent(logPane);

        Image logBackgroundImage = new Image("s7.gif");
        BackgroundImage logBackground = new BackgroundImage(
            logBackgroundImage,
            BackgroundRepeat.NO_REPEAT,
            BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.CENTER,
            new BackgroundSize(800, 800, false, false, false, false)
        );
        logPane.setBackground(new Background(logBackground));

        Label logTitle = new Label("Shipment Log Table");
        logTitle.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        logTitle.setStyle("-fx-text-fill: #FFDD00;");
        logTitle.setLayoutX(250);
        logTitle.setLayoutY(10);

        logTable.setLayoutX(60);
        logTable.setLayoutY(60);
        logTable.setPrefSize(680, 400);

        TableColumn<LogEntry, String> timeCol = new TableColumn<>("Timestamp");
        timeCol.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        timeCol.setPrefWidth(160);

        TableColumn<LogEntry, String> actionCol = new TableColumn<>("Action");
        actionCol.setCellValueFactory(new PropertyValueFactory<>("action"));
        actionCol.setPrefWidth(140);

        TableColumn<LogEntry, String> shipIdCol11 = new TableColumn<>("Shipment ID");
        shipIdCol11.setCellValueFactory(new PropertyValueFactory<>("shipmentID"));
        shipIdCol11.setPrefWidth(110);

        TableColumn<LogEntry, String> prodIdCol111 = new TableColumn<>("Product ID");
        prodIdCol111.setCellValueFactory(new PropertyValueFactory<>("productID"));
        prodIdCol111.setPrefWidth(110);

        TableColumn<LogEntry, String> qtyCol = new TableColumn<>("Qty Change");
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantityChange"));
        qtyCol.setPrefWidth(100);

        logTable.getColumns().addAll(timeCol, actionCol, shipIdCol11, prodIdCol111, qtyCol);

        ObservableList<LogEntry> logList = FXCollections.observableArrayList();
        logTable.setItems(logList);

        Button refreshLogBtn = createStyledButton("Refresh Log", "open.png");
        Button exportLogBtn = createStyledButton("Export Log", "print.png");

        refreshLogBtn.setOnAction(e -> {
            logTable.refresh();
            showSuccessAlert("Log table refreshed.");
        });

        exportLogBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialFileName("shipment_log.txt");
            File file = fileChooser.showSaveDialog(primaryStage);
            if (file != null) {
                try (FileWriter fw = new FileWriter(file)) {
                    for (LogEntry entry : logList) {
                        fw.write(entry.getTimestamp() + " | " +
                                 entry.getAction() + " | " +
                                 entry.getShipmentID() + " | " +
                                 entry.getProductID() + " | " +
                                 entry.getQuantityChange() + "\n");
                    }
                    showSuccessAlert("Log exported to " + file.getName());
                } catch (IOException ex) {
                    showErrorAlert("Failed to export log: " + ex.getMessage());
                }
            }
        });

        HBox logButtons = new HBox(15, refreshLogBtn, exportLogBtn);
        logButtons.setLayoutX(290);
        logButtons.setLayoutY(480);

        logPane.getChildren().addAll(logTitle, logTable, logButtons);
        
        
         // ********************************************** Tab 5 Reports **************************8 
        Tab reportTab = new Tab("Reports");
        Pane reportPane = new Pane();
        reportTab.setContent(reportPane);

        Image reportBackground = new Image("s6.gif");
        BackgroundImage bg = new BackgroundImage(
            reportBackground,
            BackgroundRepeat.NO_REPEAT,
            BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.CENTER,
            new BackgroundSize(800, 800, false, false, false, false)
        );
        reportPane.setBackground(new Background(bg));

        Label reportTitle = new Label("Warehouse Statistics");
        reportTitle.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        reportTitle.setStyle("-fx-text-fill: #FFDD00;");
        reportTitle.setLayoutX(220);
        reportTitle.setLayoutY(10);

        TextArea reportArea = new TextArea();
        reportArea.setLayoutX(100);
        reportArea.setLayoutY(60);
        reportArea.setPrefSize(600, 300);
        reportArea.setEditable(false);

        Button generateBtn = createStyledButton("Generate Report", "print.png");
        generateBtn.setLayoutX(300);
        generateBtn.setLayoutY(380);
        generateBtn.setOnAction(e -> {
            StringBuilder report = new StringBuilder();
            report.append("=========== WAREHOUSE REPORT ===========\n\n");

            int totalProducts = 0;
            int totalCanceled = 0;
            int totalIncoming = 0;
            int activeCount = 0;
            int inactiveCount = 0;

            Shipment mostRecent = null;
            Shipment maxQuantityShipment = null;
            int maxQty = -1;

            ArrayList<String> productNames = new ArrayList<>();
            ArrayList<Integer> productVolumes = new ArrayList<>();

            ArrayList<String> categoryNames = new ArrayList<>();
            ArrayList<Integer> categoryCanceled = new ArrayList<>();
            ArrayList<Integer> categoryTotal = new ArrayList<>();

            CatNode catNode = warehouseSystem.getCategories().getHeadNode();
            while (catNode != null) {
                String categoryName = catNode.getCategory().getName();

                if (!categoryNames.contains(categoryName)) {
                    categoryNames.add(categoryName);
                    categoryCanceled.add(0);
                    categoryTotal.add(0);
                }
                int catIndex = categoryNames.indexOf(categoryName);

                ProductLinkedList productList = catNode.getCategory().getProductList();
                PNode prodNode = productList.getHead();

                while (prodNode != null) {
                    Product product = prodNode.getProduct();
                    totalProducts++;

                    if (product.getStatus().equalsIgnoreCase("Active")) activeCount++;
                    else inactiveCount++;

                    int pending = product.getPendingShipments().count();

                    int inventory = product.getInventoryStock().count();
                    totalIncoming += inventory;

                    int canceled = product.getCanceledShipments().count();
                    totalCanceled += canceled;

                    int totalForThisProduct = pending + inventory + canceled;
                    categoryCanceled.set(catIndex, categoryCanceled.get(catIndex) + canceled);
                    categoryTotal.set(catIndex, categoryTotal.get(catIndex) + totalForThisProduct);

                    productNames.add(product.getName());
                    productVolumes.add(inventory);

                    CNode[] invNodes = product.getInventoryStock().getNodeArray();
                    int idx = product.getInventoryStock().getHeadIndex();
                    while (idx != -1) {
                        Shipment s = invNodes[idx].getData();
                        if (s != null) {
                            try {
                                LocalDateTime shipmentDate = LocalDate.parse(s.getDate()).atStartOfDay();
                                if (shipmentDate.isAfter(LocalDateTime.now().minusDays(30))) {
                                    if (mostRecent == null || shipmentDate.isAfter(LocalDate.parse(mostRecent.getDate()).atStartOfDay())) {
                                        mostRecent = s;
                                    }
                                }

                                if (s.getQuantity() > maxQty) {
                                    maxQty = s.getQuantity();
                                    maxQuantityShipment = s;
                                }
                            } catch (Exception ex) {
                                                            }
                        }
                        idx = invNodes[idx].getNext();
                    }

                    prodNode = prodNode.getNext();
                }

                catNode = catNode.getNext();
            }

            report.append("Total Products: ").append(totalProducts).append("\n");
            report.append("Total Canceled Shipments: ").append(totalCanceled).append("\n");
            report.append("Total Incoming Shipments: ").append(totalIncoming).append("\n\n");

            report.append("Most Recently Added Shipment:\n");
            if (mostRecent != null) {
                report.append(" - ").append(mostRecent.getShipmentID()).append(" (")
                      .append(mostRecent.getProductID()).append(") → ")
                      .append(mostRecent.getDate()).append("\n");
            } else {
                report.append(" - No shipments in last 30 days\n");
            }

            report.append("\nShipment with Max Quantity:\n");
            if (maxQuantityShipment != null) {
                report.append(" - ").append(maxQuantityShipment.getShipmentID()).append(" (")
                      .append(maxQuantityShipment.getProductID()).append(") → ")
                      .append(maxQuantityShipment.getQuantity()).append(" units\n");
            }

            report.append("\nCancel Rate Per Category:\n");
            for (int i = 0; i < categoryNames.size(); i++) {
                int cancelled = categoryCanceled.get(i);
                int total = categoryTotal.get(i);
                int percent = total > 0 ? (int) ((cancelled * 100.0) / total) : 0;
                report.append(" - ").append(categoryNames.get(i)).append(": ")
                      .append(cancelled).append("/").append(total)
                      .append(" (").append(percent).append("%)\n");
            }

            report.append("\nStatus Summary:\n");
            report.append(" - Active Products: ").append(activeCount).append("\n");
            report.append(" - Inactive Products: ").append(inactiveCount).append("\n");

            report.append("\nInventory Summary:\n");
            for (int i = 0; i < productNames.size(); i++) {
                report.append(" - ").append(productNames.get(i)).append(": ")
                      .append(productVolumes.get(i)).append(" units\n");
            }

            reportArea.setText(report.toString());
        });

        reportPane.getChildren().addAll(reportTitle, reportArea, generateBtn);
        
        tabPane.getTabs().addAll(categoriesTab, productsTab, shipmentsTab, logTab,reportTab);
        return tabPane;
    }
    private void updateCanceledTable() {
        ObservableList<Shipment> canceledList = FXCollections.observableArrayList();

        CatNode currentCat = warehouseSystem.getCategories().getHeadNode();
        while (currentCat != null) {
            ProductLinkedList prodList = currentCat.getCategory().getProductList();
            PNode currentProd = prodList.getHead();

            while (currentProd != null) {
                Product product = currentProd.getProduct();
                CursorBasedLinkedList canceled = product.getCanceledShipments();

                int currentIndex = canceled.getHeadIndex();
                CNode[] nodes = canceled.getNodeArray();

                while (currentIndex != -1) {
                    Shipment s = nodes[currentIndex].getData();
                    if (s != null) {
                        canceledList.add(s);
                    }
                    currentIndex = nodes[currentIndex].getNext();
                }

                currentProd = currentProd.getNext();
            }
            currentCat = currentCat.getNext();
        }

        canceledTable.setItems(canceledList);
        canceledTable.refresh();
    } 

    private void updateInventoryTable() {
        ObservableList<Shipment> inventoryList = FXCollections.observableArrayList();

        CatNode currentCat = warehouseSystem.getCategories().getHeadNode();
        while (currentCat != null) {
            ProductLinkedList prodList = currentCat.getCategory().getProductList();
            PNode currentProd = prodList.getHead();

            while (currentProd != null) {
                Product product = currentProd.getProduct();
                CursorBasedLinkedList inventory = product.getInventoryStock();

                int currentIndex = inventory.getHeadIndex();
                CNode[] nodes = inventory.getNodeArray();

                while (currentIndex != -1) {
                    Shipment s = nodes[currentIndex].getData();
                    if (s != null) {
                        inventoryList.add(s);
                    }
                    currentIndex = nodes[currentIndex].getNext();
                }

                currentProd = currentProd.getNext();
            }
            currentCat = currentCat.getNext();
        }

        inventoryTable.setItems(inventoryList);
        inventoryTable.refresh();
    } 
    public void readProductsFromFile(File file, WarehouseSystem warehouseSystem, TableView<Category> categoryTable) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine(); // skip header
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 4) continue;

                String productID = parts[0].trim();
                String name = parts[1].trim();
                String categoryName = parts[2].trim();
                String status = parts[3].trim();

                Category existingCategory = warehouseSystem.searchCategory(categoryName);
                if (existingCategory == null) {
                    String catID = "C" + (int) (Math.random() * 1000); 
                    warehouseSystem.addCategory(catID, categoryName, "Imported");
                }

                warehouseSystem.addProduct(productID, name, status, categoryName);
            }

            updateCategoryTable(warehouseSystem, categoryTable);


        } catch (IOException e) {
            showErrorAlert("Error reading file: " + e.getMessage());
        }
    }
    private void updateCategoryTable(WarehouseSystem warehouseSystem, TableView<Category> categoryTable) {
        ObservableList<Category> refreshedList = FXCollections.observableArrayList();
        CatNode current = warehouseSystem.getCategories().getHeadNode();

        while (current != null) {
            refreshedList.add(current.getCategory());
            current = current.getNext();
        }

        categoryTable.setItems(refreshedList);
        categoryTable.refresh();
    }

    private void showErrorAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("An error occurred");
        alert.setContentText(msg);
        alert.showAndWait();
    }
    private void updateProductTableFromSystem() {
        ObservableList<Product> productList = FXCollections.observableArrayList();

        CatNode currentCategory = warehouseSystem.getCategories().getHeadNode();
        while (currentCategory != null) {
            PNode currentProduct = currentCategory.category.getProductList().getHead();
            while (currentProduct != null) {
                productList.add(currentProduct.getProduct());
                currentProduct = currentProduct.getNext();
            }
            currentCategory = currentCategory.getNext();
        }

        productTable.setItems(productList);
    }

    private void displayProductsOfCategory(Category category) {
        ObservableList<Product> products = FXCollections.observableArrayList();

        ProductLinkedList productList = category.getProductList();
        PNode current = productList.getHead(); 

        while (current != null) {
            products.add(current.getProduct());
            current = current.getNext();
        }

        productTable.setItems(products);
    }
   // *********************************** Cat Tap Managment *********************************
    private void openAddCategoryStage() {
        Stage addStage = new Stage();
        addStage.setTitle("Add New Category");

        Label idLabel = new Label("Category ID:");
        TextField idField = new TextField();

        Label nameLabel = new Label("Category Name:");
        TextField nameField = new TextField();

        Label descLabel = new Label("Description:");
        TextField descField = new TextField();

        Button saveButton = new Button("Save");
        Button cancelButton = new Button("Cancel");

        saveButton.setOnAction(e -> {
            String id = idField.getText().trim();
            String name = nameField.getText().trim();
            String desc = descField.getText().trim();

            if (id.isEmpty() || name.isEmpty() || desc.isEmpty()) {
                showErrorAlert("All fields are required.");
                return;
            }

            if (warehouseSystem.searchCategory(name) != null) {
                showErrorAlert("Category name already exists.");
                return;
            }
            if (warehouseSystem.searchByID(id) != null) {
                showErrorAlert("Category ID already exists.");
                return;
            }

            warehouseSystem.addCategory(id, name, desc);
            updateCategoryTable(warehouseSystem, categoryTable); 
            showSuccessAlert("Category added successfully.");
            addStage.close();
        });

        cancelButton.setOnAction(e -> addStage.close());

        VBox layout = new VBox(10,
            idLabel, idField,
            nameLabel, nameField,
            descLabel, descField,
            new HBox(10, saveButton, cancelButton)
        );

        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-padding: 20px;");

        Scene scene = new Scene(layout, 350, 300);
        addStage.setScene(scene);
        addStage.show();
    }

    private void openUpdateCategoryStage(Category category) {
        if (category == null) {
            showErrorAlert("Please select a category to update.");
            return;
        }

        Stage updateStage = new Stage();
        updateStage.setTitle("Update Category");

        Label idLabel = new Label("Category ID:");
        TextField idField = new TextField(category.getCategoryID());
        idField.setDisable(true);

        Label nameLabel = new Label("Category Name:");
        TextField nameField = new TextField(category.getName());

        Label descLabel = new Label("Description:");
        TextField descField = new TextField(category.getDescription());

        Button updateButton = new Button("Update");
        Button cancelButton = new Button("Cancel");

        updateButton.setOnAction(e -> {
            String newName = nameField.getText().trim();
            String newDesc = descField.getText().trim();

            if (newName.isEmpty() || newDesc.isEmpty()) {
                showErrorAlert("All fields are required.");
                return;
            }

            Category existing = warehouseSystem.searchCategory(newName);
            if (existing != null && !existing.getCategoryID().equals(category.getCategoryID())) {
                showErrorAlert("Another category with this name already exists.");
                return;
            }

            category.setName(newName);
            category.setDescription(newDesc);
            updateCategoryTable(warehouseSystem, categoryTable); 
            showSuccessAlert("Category updated successfully.");
            updateStage.close();
        });

        cancelButton.setOnAction(e -> updateStage.close());

        VBox layout = new VBox(10,
            idLabel, idField,
            nameLabel, nameField,
            descLabel, descField,
            new HBox(10, updateButton, cancelButton)
        );

        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-padding: 20px;");

        Scene scene = new Scene(layout, 350, 300);
        updateStage.setScene(scene);
        updateStage.show();
    }
    private void openDeleteCategoryStage() {
        Stage deleteStage = new Stage();
        deleteStage.setTitle("Delete Category");

        Label idLabel = new Label("Enter Category ID to delete:");
        TextField idField = new TextField();

        Button nextButton = new Button("Next");
        Button cancelButton = new Button("Cancel");

        nextButton.setOnAction(e -> {
            String id = idField.getText().trim();
            if (id.isEmpty()) {
                showErrorAlert("Please enter a category ID.");
                return;
            }

            Category categoryToDelete = warehouseSystem.searchByID(id);
            if (categoryToDelete == null) {
                showErrorAlert("Category not found.");
                return;
            }

            if (categoryToDelete.hasNoProducts()) {
                warehouseSystem.deleteCategoryByID(id);
                updateCategoryTable(warehouseSystem, categoryTable);
                productTable.getItems().clear();
                showSuccessAlert("Category deleted (no products inside).");
                deleteStage.close();
                return;
            }

            Alert optionDialog = new Alert(Alert.AlertType.CONFIRMATION);
            optionDialog.setTitle("Category has products");
            optionDialog.setHeaderText("This category has products. Choose an action:");
            ButtonType reassign = new ButtonType("Reassign products");
            ButtonType forceDelete = new ButtonType("Force delete");
            ButtonType cancel = new ButtonType("Cancel");

            optionDialog.getButtonTypes().setAll(reassign, forceDelete, cancel);

            optionDialog.showAndWait().ifPresent(response -> {
                if (response == reassign) {
                    deleteStage.close();
                    openReassignStage(categoryToDelete); 
                } else if (response == forceDelete) {
                    categoryToDelete.getProductList().clear(); 
                    warehouseSystem.deleteCategoryByID(id);
                    updateCategoryTable(warehouseSystem, categoryTable);
                    productTable.getItems().clear();
                    showSuccessAlert("Category and all its products deleted.");
                    deleteStage.close();
                } else {
                    showSuccessAlert("Category deletion cancelled.");
                }
            });
        });

        cancelButton.setOnAction(e -> deleteStage.close());

        VBox layout = new VBox(10, idLabel, idField, new HBox(10, nextButton, cancelButton));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-padding: 20px;");
        deleteStage.setScene(new Scene(layout, 350, 200));
        deleteStage.show();
    }
    private void openReassignStage(Category fromCategory) {
        Stage reassignStage = new Stage();
        reassignStage.setTitle("Reassign Products");

        Label targetLabel = new Label("Select target category:");
        ComboBox<String> categoryBox = new ComboBox<>();

        CatNode current = warehouseSystem.getCategories().getHeadNode();
        while (current != null) {
            if (!current.getCategory().getCategoryID().equals(fromCategory.getCategoryID())) {
                categoryBox.getItems().add(current.getCategory().getName());
            }
            current = current.getNext();
        }

        Button confirm = new Button("Reassign and Delete");
        Button cancel = new Button("Cancel");

        confirm.setOnAction(e -> {
            String selectedCategoryName = categoryBox.getValue();
            if (selectedCategoryName == null) {
                showErrorAlert("Please select a target category.");
                return;
            }

            Category toCategory = warehouseSystem.searchCategory(selectedCategoryName);
            if (toCategory == null) {
                showErrorAlert("Target category not found.");
                return;
            }

            PNode currentProduct = fromCategory.getProductList().getHead();
            while (currentProduct != null) {
                toCategory.addProduct(currentProduct.getProduct());
                currentProduct = currentProduct.getNext();
            }

            fromCategory.getProductList().clear();
            warehouseSystem.deleteCategoryByID(fromCategory.getCategoryID());
            updateCategoryTable(warehouseSystem, categoryTable);
            updateProductTableFromSystem();
            showSuccessAlert("Products reassigned. Category deleted.");
            reassignStage.close();
        });

        cancel.setOnAction(e -> reassignStage.close());

        VBox layout = new VBox(10, targetLabel, categoryBox, new HBox(10, confirm, cancel));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-padding: 20px;");
        reassignStage.setScene(new Scene(layout, 400, 200));
        reassignStage.show();
    }


    private void openSearchCategoryStage() {
        Stage searchStage = new Stage();
        searchStage.setTitle("Search Category");

        Label searchLabel = new Label("Enter Category ID or Name:");
        TextField inputField = new TextField();

        Button searchButton = new Button("Search");
        Button cancelButton = new Button("Cancel");

        searchButton.setOnAction(e -> {
            String input = inputField.getText().trim();

            if (input.isEmpty()) {
                showErrorAlert("Please enter category ID or name.");
                return;
            }

            Category found = warehouseSystem.searchCategory(input); 
            if (found == null) {
                found = warehouseSystem.searchByID(input);
            }

            if (found == null) {
                showErrorAlert("Category not found.");
            } else {
                showSuccessAlert("Found Category:\nID: " + found.getCategoryID() +
                                 "\nName: " + found.getName() +
                                 "\nDescription: " + found.getDescription());

                ObservableList<Category> result = FXCollections.observableArrayList();
                result.add(found);
                categoryTable.setItems(result);
            }

            searchStage.close();
        });

        cancelButton.setOnAction(e -> searchStage.close());

        VBox layout = new VBox(10, searchLabel, inputField, new HBox(10, searchButton, cancelButton));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-padding: 20px;");

        Scene scene = new Scene(layout, 350, 200);
        searchStage.setScene(scene);
        searchStage.show();
    }

    private void listAllCategories() {
        updateCategoryTable(warehouseSystem, categoryTable);
        showSuccessAlert("All categories listed.");
    }
    
    // *********************************** Product Tap Managment *********************************

    private void openAddProductStage() {
        Stage addStage = new Stage();
        addStage.setTitle("Add New Product");

        Label idLabel = new Label("Product ID:");
        TextField idField = new TextField();

        Label nameLabel = new Label("Product Name:");
        TextField nameField = new TextField();

        Label statusLabel = new Label("Status:");
        ComboBox<String> statusBox = new ComboBox<>();
        statusBox.getItems().addAll("Active", "Inactive");

        Label categoryLabel = new Label("Category:");
        ComboBox<String> categoryBox = new ComboBox<>();
        // تعبئة ComboBox بالفئات
        CatNode current = warehouseSystem.getCategories().getHeadNode();
        while (current != null) {
            categoryBox.getItems().add(current.getCategory().getName());
            current = current.getNext();
        }

        Button saveButton = new Button("Save");
        Button cancelButton = new Button("Cancel");

        saveButton.setOnAction(e -> {
            String id = idField.getText().trim();
            String name = nameField.getText().trim();
            String status = statusBox.getValue();
            String categoryName = categoryBox.getValue();

            // تحقق من الإدخالات
            if (id.isEmpty() || name.isEmpty() || status == null || categoryName == null) {
                showErrorAlert("All fields are required.");
                return;
            }

            if (warehouseSystem.searchProduct(id) != null) {
                showErrorAlert("Product ID already exists.");
                return;
            }

            warehouseSystem.addProduct(id, name, status, categoryName);
            updateProductTableFromSystem();
            showSuccessAlert("Product added successfully.");
            addStage.close();
        });

        cancelButton.setOnAction(e -> addStage.close());

        VBox layout = new VBox(10,
            idLabel, idField,
            nameLabel, nameField,
            statusLabel, statusBox,
            categoryLabel, categoryBox,
            new HBox(10, saveButton, cancelButton)
        );

        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-padding: 20px;");
        Scene scene = new Scene(layout, 400, 400);
        addStage.setScene(scene);
        addStage.show();
    }
    private void openUpdateProductStage() {
        Stage updateStage = new Stage();
        updateStage.setTitle("Update Product");

        Label idLabel = new Label("Enter Product ID:");
        TextField idField = new TextField();

        Button nextButton = new Button("Next");
        Button cancelButton = new Button("Cancel");

        nextButton.setOnAction(e -> {
            String id = idField.getText().trim();
            if (id.isEmpty()) {
                showErrorAlert("Please enter product ID.");
                return;
            }

            Product productToUpdate = warehouseSystem.searchProduct(id);
            if (productToUpdate == null) {
                showErrorAlert("Product not found.");
                return;
            }

            updateStage.close(); 
            openEditProductDetailsStage(productToUpdate);
        });

        cancelButton.setOnAction(e -> updateStage.close());

        VBox layout = new VBox(10, idLabel, idField, new HBox(10, nextButton, cancelButton));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-padding: 20px;");
        updateStage.setScene(new Scene(layout, 350, 200));
        updateStage.show();
    }
    private void openEditProductDetailsStage(Product product) {
        Stage editStage = new Stage();
        editStage.setTitle("Edit Product");

        Label idLabel = new Label("Product ID:");
        TextField idField = new TextField(product.getProductID());
        idField.setDisable(true);

        Label nameLabel = new Label("Product Name:");
        TextField nameField = new TextField(product.getName());

        Label statusLabel = new Label("Status:");
        ComboBox<String> statusBox = new ComboBox<>();
        statusBox.getItems().addAll("Active", "Inactive");
        statusBox.setValue(product.getStatus());

        Button updateButton = new Button("Update");
        Button cancelButton = new Button("Cancel");

        updateButton.setOnAction(e -> {
            String newName = nameField.getText().trim();
            String newStatus = statusBox.getValue();

            if (newName.isEmpty() || newStatus == null) {
                showErrorAlert("All fields are required.");
                return;
            }

            product.setName(newName);
            product.setStatus(newStatus);
            
            updateProductTableFromSystem();
            productTable.refresh();
            showSuccessAlert("Product updated.");
            editStage.close();
        });

        cancelButton.setOnAction(e -> editStage.close());

        VBox layout = new VBox(10,
            idLabel, idField,
            nameLabel, nameField,
            statusLabel, statusBox,
            new HBox(10, updateButton, cancelButton)
        );
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-padding: 20px;");
        editStage.setScene(new Scene(layout, 350, 300));
        editStage.show();
    }
    private void openSearchProductStage() {
        Stage searchStage = new Stage();
        searchStage.setTitle("Search Product");

        Label searchLabel = new Label("Enter Product ID or Name:");
        TextField inputField = new TextField();

        Button searchButton = new Button("Search");
        Button cancelButton = new Button("Cancel");

        searchButton.setOnAction(e -> {
            String input = inputField.getText().trim();

            if (input.isEmpty()) {
                showErrorAlert("Please enter product ID or name.");
                return;
            }

            CatNode currentCat = warehouseSystem.getCategories().getHeadNode();
            Product found = null;
            String foundCategory = "";

            while (currentCat != null) {
                ProductLinkedList list = currentCat.getCategory().getProductList();
                PNode currentProd = list.getHead();
                while (currentProd != null) {
                    Product p = currentProd.getProduct();
                    if (p.getProductID().equalsIgnoreCase(input) || p.getName().equalsIgnoreCase(input)) {
                        found = p;
                        foundCategory = currentCat.getCategory().getName();
                        break;
                    }
                    currentProd = currentProd.getNext();
                }
                if (found != null) break;
                currentCat = currentCat.getNext();
            }

            if (found == null) {
                showErrorAlert("Product not found.");
            } else {
                showSuccessAlert("Found Product:\nID: " + found.getProductID() +
                                 "\nName: " + found.getName() +
                                 "\nStatus: " + found.getStatus() +
                                 "\nCategory: " + foundCategory);

                ObservableList<Product> result = FXCollections.observableArrayList();
                result.add(found);
                productTable.setItems(result);
            }

            searchStage.close();
        });

        cancelButton.setOnAction(e -> searchStage.close());

        VBox layout = new VBox(10, searchLabel, inputField, new HBox(10, searchButton, cancelButton));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-padding: 20px;");
        Scene scene = new Scene(layout, 350, 200);
        searchStage.setScene(scene);
        searchStage.show();
    }
    private void openDeleteProductStage() {
        Stage deleteStage = new Stage();
        deleteStage.setTitle("Delete Product");

        Label idLabel = new Label("Enter Product ID to delete:");
        TextField idField = new TextField();

        Button deleteButton = new Button("Delete");
        Button cancelButton = new Button("Cancel");

        deleteButton.setOnAction(e -> {
            String id = idField.getText().trim();
            if (id.isEmpty()) {
                showErrorAlert("Please enter a product ID.");
                return;
            }

            boolean found = false;

            CatNode currentCat = warehouseSystem.getCategories().getHeadNode();
            while (currentCat != null) {
                ProductLinkedList list = currentCat.getCategory().getProductList();
                if (list.getProductByID(id) != null) {
                    list.removeProductByID(id);
                    found = true;
                    break;
                }
                currentCat = currentCat.getNext();
            }

            if (found) {
                updateProductTableFromSystem();
                productTable.refresh();
                showSuccessAlert("Product deleted successfully.");
                deleteStage.close();
            } else {
                showErrorAlert("Product not found.");
            }
        });

        cancelButton.setOnAction(e -> deleteStage.close());

        VBox layout = new VBox(10, idLabel, idField, new HBox(10, deleteButton, cancelButton));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-padding: 20px;");
        deleteStage.setScene(new Scene(layout, 350, 200));
        deleteStage.show();
    }
    private void refreshProductTable() {
        updateProductTableFromSystem();
        productTable.refresh();
        showSuccessAlert("Product list refreshed.");
    }
    private void sortProductsByNameAscending() {
        ObservableList<Product> sortedList = FXCollections.observableArrayList();

        CatNode currentCat = warehouseSystem.getCategories().getHeadNode();
        while (currentCat != null) {
            PNode currentProd = currentCat.getCategory().getProductList().getHead();
            while (currentProd != null) {
                sortedList.add(currentProd.getProduct());
                currentProd = currentProd.getNext();
            }
            currentCat = currentCat.getNext();
        }

        for (int i = 0; i < sortedList.size() - 1; i++) {
            for (int j = 0; j < sortedList.size() - 1 - i; j++) {
                String name1 = sortedList.get(j).getName().toLowerCase();
                String name2 = sortedList.get(j + 1).getName().toLowerCase();
                if (name1.compareTo(name2) > 0) {
                    // تبديل
                    Product temp = sortedList.get(j);
                    sortedList.set(j, sortedList.get(j + 1));
                    sortedList.set(j + 1, temp);
                }
            }
        }

        productTable.setItems(sortedList);
        productTable.refresh();
        showSuccessAlert("Products sorted manually A → Z by name.");
    }

    private void filterProductsByStatus(String status) {
        ObservableList<Product> filteredList = FXCollections.observableArrayList();

        CatNode currentCat = warehouseSystem.getCategories().getHeadNode();
        while (currentCat != null) {
            PNode currentProd = currentCat.getCategory().getProductList().getHead();
            while (currentProd != null) {
                Product p = currentProd.getProduct();
                if (p.getStatus().equalsIgnoreCase(status)) {
                    filteredList.add(p);
                }
                currentProd = currentProd.getNext();
            }
            currentCat = currentCat.getNext();
        }

        productTable.setItems(filteredList);
        productTable.refresh();
        showSuccessAlert("Filtered by status: " + status);
    }
    private void readShipmentsFromFile(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine(); // skip header
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 4) continue;

                String shipmentID = parts[0].trim();
                String productID = parts[1].trim();
                int quantity = Integer.parseInt(parts[2].trim());
                String date = parts[3].trim();

                Shipment shipment = new Shipment(shipmentID, productID, quantity, date);
                Product product = warehouseSystem.searchProduct(productID);
                System.out.println("Added shipment: " + shipmentID + " to product: " + productID);

                if (product != null) {
                    product.getPendingShipments().enqueue(shipment);
                } else {
                    System.out.println("Product ID not found: " + productID);
                }
            }            
        } catch (Exception e) {
            showErrorAlert("Error reading shipments: " + e.getMessage());
        }
    }
 
    private void updatePendingTable() {
        ObservableList<Shipment> list = FXCollections.observableArrayList();

        CatNode currentCat = warehouseSystem.getCategories().getHeadNode();
        while (currentCat != null) {
            ProductLinkedList prodList = currentCat.getCategory().getProductList();
            PNode currentProd = prodList.getHead();

            while (currentProd != null) {
                Product product = currentProd.getProduct();
                ShipmentQueue queue = product.getPendingShipments();

                QNode qNode = queue.getFront();
                while (qNode != null) {
                    list.add(qNode.getShipment());
                    qNode = qNode.getNext();
                }

                currentProd = currentProd.getNext();
            }

            currentCat = currentCat.getNext();
        }
        System.out.println("=== All Shipments ===");
        for (Shipment s : list) {
            System.out.println(s.getShipmentID() + " | " + s.getProductID());
        }

        pendingTable.setItems(list);
        pendingTable.refresh();
    }

    private void openApproveShipmentStage() {
        Stage stage = new Stage();
        stage.setTitle("Approve Shipment");

        // ======= ComboBox for Categories =======
        Label catLabel = new Label("Select Category:");
        ComboBox<String> categoryBox = new ComboBox<>();
        ObservableList<String> categoryNames = FXCollections.observableArrayList();

        CatNode currentCat = warehouseSystem.getCategories().getHeadNode();
        while (currentCat != null) {
            categoryNames.add(currentCat.getCategory().getName());
            currentCat = currentCat.getNext();
        }
        categoryBox.setItems(categoryNames);

        Label prodLabel = new Label("Select Product:");
        ComboBox<String> productBox = new ComboBox<>();

        categoryBox.setOnAction(e -> {
            String selectedCat = categoryBox.getValue();
            Category cat = warehouseSystem.searchCategory(selectedCat);
            if (cat != null) {
                ObservableList<String> productIDs = FXCollections.observableArrayList();
                PNode p = cat.getProductList().getHead();
                while (p != null) {
                    productIDs.add(p.getProduct().getProductID());
                    p = p.getNext();
                }
                productBox.setItems(productIDs);
            }
        });

        // ======= Label to Show First Shipment =======
        Label shipmentInfo = new Label("Shipment info will appear here...");

        productBox.setOnAction(e -> {
            String prodID = productBox.getValue();
            Product prod = warehouseSystem.searchProduct(prodID);
            if (prod != null && !prod.getPendingShipments().isEmpty()) {
                Shipment first = prod.getPendingShipments().getFront().getShipment();
                shipmentInfo.setText("ID: " + first.getShipmentID() +
                        "\nQuantity: " + first.getQuantity() +
                        "\nDate: " + first.getDate());
            } else {
                shipmentInfo.setText("No pending shipments.");
            }
        });

        // ======= Approve Button =======
        Button approveBtn = new Button("Approve Shipment");
        approveBtn.setOnAction(e -> {
            String prodID = productBox.getValue();
            if (prodID == null) {
                showErrorAlert("Please select a product.");
                return;
            }

            Product prod = warehouseSystem.searchProduct(prodID);
            if (prod == null || prod.getPendingShipments().isEmpty()) {
                showErrorAlert("No shipment to approve.");
                return;
            }

            Shipment first = prod.getPendingShipments().getFront().getShipment();

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Approval");
            confirm.setHeaderText("Approve this shipment?");
            confirm.setContentText("Shipment ID: " + first.getShipmentID() +
                    "\nProduct ID: " + first.getProductID() +
                    "\nQuantity: " + first.getQuantity());

            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                prod.getPendingShipments().dequeue();
                prod.getInventoryStock().insertAtEnd(first);

                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                logList.add(new LogEntry(timestamp, "Approve", first.getShipmentID(), first.getProductID(), "+" + first.getQuantity()));
                logTable.setItems(logList);
                undoStack.push(new ActionRecord("Approve", first, timestamp));

                updatePendingTable();
                updateInventoryTable();

                showSuccessAlert("Shipment approved successfully.");
                stage.close();
            }
        });

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setOnAction(e -> stage.close());

        VBox layout = new VBox(12, catLabel, categoryBox, prodLabel, productBox, shipmentInfo, new HBox(10, approveBtn, cancelBtn));
        layout.setStyle("-fx-padding: 20;");
        layout.setAlignment(Pos.CENTER);

        stage.setScene(new Scene(layout, 350, 400));
        stage.show();
    }
    private void openCancelShipmentStage() {
        Stage stage = new Stage();
        stage.setTitle("Cancel Shipment");

        // === اختيار الكاتيجوري ===
        Label catLabel = new Label("Select Category:");
        ComboBox<String> categoryBox = new ComboBox<>();
        ObservableList<String> categoryNames = FXCollections.observableArrayList();

        CatNode currentCat = warehouseSystem.getCategories().getHeadNode();
        while (currentCat != null) {
            categoryNames.add(currentCat.getCategory().getName());
            currentCat = currentCat.getNext();
        }
        categoryBox.setItems(categoryNames);

        Label prodLabel = new Label("Select Product:");
        ComboBox<String> productBox = new ComboBox<>();

        categoryBox.setOnAction(e -> {
            String selectedCat = categoryBox.getValue();
            Category cat = warehouseSystem.searchCategory(selectedCat);
            if (cat != null) {
                ObservableList<String> productIDs = FXCollections.observableArrayList();
                PNode p = cat.getProductList().getHead();
                while (p != null) {
                    productIDs.add(p.getProduct().getProductID());
                    p = p.getNext();
                }
                productBox.setItems(productIDs);
            }
        });

        // === عرض أول شحنة في الكيو ===
        Label shipmentInfo = new Label("Shipment info will appear here...");

        productBox.setOnAction(e -> {
            String prodID = productBox.getValue();
            Product prod = warehouseSystem.searchProduct(prodID);
            if (prod != null && !prod.getPendingShipments().isEmpty()) {
                Shipment first = prod.getPendingShipments().getFront().getShipment();
                shipmentInfo.setText("ID: " + first.getShipmentID() +
                        "\nQuantity: " + first.getQuantity() +
                        "\nDate: " + first.getDate());
            } else {
                shipmentInfo.setText("No pending shipments.");
            }
        });

        Button cancelNow = new Button("Cancel Shipment");
        cancelNow.setOnAction(e -> {
            String prodID = productBox.getValue();
            if (prodID == null) {
                showErrorAlert("Please select a product.");
                return;
            }

            Product prod = warehouseSystem.searchProduct(prodID);
            if (prod == null || prod.getPendingShipments().isEmpty()) {
                showErrorAlert("No shipment to cancel.");
                return;
            }

            Shipment first = prod.getPendingShipments().getFront().getShipment();

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Cancellation");
            confirm.setHeaderText("Cancel this shipment?");
            confirm.setContentText("Shipment ID: " + first.getShipmentID() +
                    "\nProduct ID: " + first.getProductID() +
                    "\nQuantity: " + first.getQuantity());

            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                prod.getPendingShipments().dequeue();
                prod.getCanceledShipments().insertAtEnd(first);

                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                logList.add(new LogEntry(timestamp, "Cancel", first.getShipmentID(), first.getProductID(), "-" + first.getQuantity()));
                logTable.setItems(logList);
                undoStack.push(new ActionRecord("Cancel", first, timestamp));

                updatePendingTable();
                updateCanceledTable();

                showSuccessAlert("Shipment canceled successfully.");
                stage.close();
            }
        });

        Button closeBtn = new Button("Cancel");
        closeBtn.setOnAction(e -> stage.close());

        VBox layout = new VBox(12, catLabel, categoryBox, prodLabel, productBox, shipmentInfo, new HBox(10, cancelNow, closeBtn));
        layout.setStyle("-fx-padding: 20;");
        layout.setAlignment(Pos.CENTER);

        stage.setScene(new Scene(layout, 350, 400));
        stage.show();
    }
    private void openAddShipmentStage() {
        Stage stage = new Stage();
        stage.setTitle("Add New Shipment");

        Label catLabel = new Label("Select Category:");
        ComboBox<String> categoryBox = new ComboBox<>();
        ObservableList<String> categoryNames = FXCollections.observableArrayList();

        CatNode currentCat = warehouseSystem.getCategories().getHeadNode();
        while (currentCat != null) {
            categoryNames.add(currentCat.getCategory().getName());
            currentCat = currentCat.getNext();
        }
        categoryBox.setItems(categoryNames);

        Label prodLabel = new Label("Select Product:");
        ComboBox<String> productBox = new ComboBox<>();

        categoryBox.setOnAction(e -> {
            String selectedCat = categoryBox.getValue();
            Category cat = warehouseSystem.searchCategory(selectedCat);
            if (cat != null) {
                ObservableList<String> productIDs = FXCollections.observableArrayList();
                PNode p = cat.getProductList().getHead();
                while (p != null) {
                    productIDs.add(p.getProduct().getProductID());
                    p = p.getNext();
                }
                productBox.setItems(productIDs);
            }
        });
        Label idLabel = new Label("Shipment ID:");
        TextField idField = new TextField();

        Label qtyLabel = new Label("Quantity:");
        TextField qtyField = new TextField();

        Label dateLabel = new Label("Select Date:");
        DatePicker datePicker = new DatePicker();

        Button addBtn = new Button("Add Shipment");
        addBtn.setOnAction(e -> {
            String cat = categoryBox.getValue();
            String prodID = productBox.getValue();
            String shipmentID = idField.getText().trim();
            String qtyText = qtyField.getText().trim();
            LocalDate selectedDate = datePicker.getValue();
            if (selectedDate == null) {
                showErrorAlert("Please select a date.");
                return;
            }
            String date = selectedDate.toString(); 

            if (cat == null || prodID == null || shipmentID.isEmpty() || qtyText.isEmpty() || date.isEmpty()) {
                showErrorAlert("All fields must be filled.");
                return;
            }

            int quantity;
            try {
                quantity = Integer.parseInt(qtyText);
                if (quantity <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                showErrorAlert("Quantity must be a positive integer.");
                return;
            }

            Product product = warehouseSystem.searchProduct(prodID);
            if (product == null) {
                showErrorAlert("Product not found.");
                return;
            }

            QNode temp = product.getPendingShipments().getFront();
            while (temp != null) {
                if (temp.getShipment().getShipmentID().equalsIgnoreCase(shipmentID)) {
                    showErrorAlert("Shipment ID already exists.");
                    return;
                }
                temp = temp.getNext();
            }

            Shipment newShipment = new Shipment(shipmentID, prodID, quantity, date);
            product.getPendingShipments().enqueue(newShipment);

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            logList.add(new LogEntry(timestamp, "Add", shipmentID, prodID, "+" + quantity));
            logTable.setItems(logList);
            undoStack.push(new ActionRecord("Add", newShipment, timestamp));

            updatePendingTable();
            showSuccessAlert("Shipment added successfully.");
            stage.close();
        });

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setOnAction(e -> stage.close());

        VBox layout = new VBox(12,
            catLabel, categoryBox,
            prodLabel, productBox,
            idLabel, idField,
            qtyLabel, qtyField,
            dateLabel, datePicker,
            new HBox(10, addBtn, cancelBtn)
        );
        layout.setStyle("-fx-padding: 20;");
        layout.setAlignment(Pos.CENTER);

        stage.setScene(new Scene(layout, 350, 480));
        stage.show();
    }
    private void sortCategoriesByNameAscending() {
        ObservableList<Category> list = FXCollections.observableArrayList();

        CatNode current = warehouseSystem.getCategories().getHeadNode();
        while (current != null) {
            list.add(current.getCategory());
            current = current.getNext();
        }

        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = 0; j < list.size() - i - 1; j++) {
                String name1 = list.get(j).getName().toLowerCase();
                String name2 = list.get(j + 1).getName().toLowerCase();

                if (name1.compareTo(name2) > 0) {
                    Category temp = list.get(j);
                    list.set(j, list.get(j + 1));
                    list.set(j + 1, temp);
                }
            }
        }

        categoryTable.setItems(list);
        categoryTable.refresh();
        showSuccessAlert("Categories sorted A → Z by name.");
    }


}
