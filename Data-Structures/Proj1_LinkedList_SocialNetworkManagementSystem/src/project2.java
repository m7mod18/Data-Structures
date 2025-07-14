package application;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class project2 extends Application {
    TableView<Post> userPostsTable = new TableView<>();

    TableView<User> UserTable = new TableView<>();
    TableView<User> FriendsTable = new TableView<>();
    private UserNode currentUserNode; // 
    TableView<ObservableList<String>> friendsTable;
    TableView<Post> postTable = new TableView<>();

    Network_Managment networkManagement = new Network_Managment(); 

    @Override
    public void start(Stage primaryStage) {
        Pane firstPage = new Pane();
        ImageView backgroundImage = new ImageView(new Image("p4.gif"));
        backgroundImage.setFitWidth(800);
        backgroundImage.setFitHeight(800);

        ImageView titleIcon = new ImageView(new Image("exit.png"));
        titleIcon.setFitHeight(50);
        titleIcon.setFitWidth(50);

        Label titleLabel = new Label("Social Network Management System", titleIcon);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setStyle("-fx-text-fill: white; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.5), 5, 0.0, 2, 2);");

        titleLabel.setOnMouseEntered(e -> {
            titleLabel.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #1E3A5F; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.7), 6, 0.0, 3, 3);");
            titleIcon.setFitHeight(55); 
            titleIcon.setFitWidth(55);
        });

        titleLabel.setOnMouseExited(e -> {
            titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.5), 5, 0.0, 2, 2);");
            titleIcon.setFitHeight(50);
            titleIcon.setFitWidth(50);
        });

        titleLabel.setLayoutX(200);
        titleLabel.setLayoutY(50);	  
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));

        Button readUserButton = createStyledButton("Open Users File", "open.png");
        Button readFriendsButton = createStyledButton("Open Friends File", "open.png");
        Button readPostsButton = createStyledButton("Open Posts File", "open.png");
        readFriendsButton.setDisable(true);
        readPostsButton.setDisable(true);
        Button saveUsersButton = createStyledButton("Save Users File", "print.png");
        Button saveFriendsButton = createStyledButton("Save Friends File", "print.png");
        Button savePostsButton = createStyledButton("Save Posts File", "print.png");
        saveUsersButton.setDisable(true);
        saveFriendsButton.setDisable(true);
        savePostsButton.setDisable(true);

        // ************************* Read For Files *****************************
        
        readUserButton.setOnAction(e -> {
            File userFile = fileChooser.showOpenDialog(primaryStage);
            if (userFile != null) {
                networkManagement.readUsers(userFile); 
                updateUserTable();
                showSuccessAlert(userFile.getName());
                readFriendsButton.setDisable(false);
            } else {
                showErrorAlert("No file selected.");
            }
        });
        
        
        readFriendsButton.setOnAction(e -> {
            File friendsFile = fileChooser.showOpenDialog(primaryStage);
            if (friendsFile != null) {
                networkManagement.readFriendships(friendsFile); 
                showSuccessAlert(friendsFile.getName());
                updateFriendshipTableDirect(friendsTable); 
                readPostsButton.setDisable(false);
            } else {
                showErrorAlert("No file selected.");
            }
            });

        readPostsButton.setOnAction(e -> {
            File postsFile = fileChooser.showOpenDialog(primaryStage);
            if (postsFile != null) {
                networkManagement.readPosts(postsFile);
                showSuccessAlert(postsFile.getName());
                updatePostTableFromUsers(postTable); 
                saveUsersButton.setDisable(false);
                saveFriendsButton.setDisable(false);
                savePostsButton.setDisable(false);
            } else {
                showErrorAlert("No file selected.");
            }
        });
        saveUsersButton.setOnAction(e -> {
        	
            File file = fileChooser.showSaveDialog(primaryStage);
            if (file != null) {
                networkManagement.saveUsersToFile(file);
                showSuccessAlert("Users saved to " + file.getName());
            }
        });

        saveFriendsButton.setOnAction(e -> {
            File file = fileChooser.showSaveDialog(primaryStage);
            if (file != null) {
                networkManagement.saveFriendshipsToFile(file);
                showSuccessAlert("Friendships saved to " + file.getName());
            }
        });

        savePostsButton.setOnAction(e -> {
            File file = fileChooser.showSaveDialog(primaryStage);
            if (file != null) {
                networkManagement.savePostsToFile(file);
                showSuccessAlert("Posts saved to " + file.getName());
            }
        });

        Button exitButton = createStyledButton("Exit", "exit.png");
        exitButton.setOnAction(e -> primaryStage.close());
        Button nextButton = createStyledButton("Next Page", "next.png");
        VBox firstPageButtons = new VBox(15,
        	    readUserButton, readFriendsButton, readPostsButton,
        	    saveUsersButton, saveFriendsButton, savePostsButton,
        	    exitButton, nextButton);
        firstPageButtons.setPadding(new Insets(20));
        firstPageButtons.setStyle("-fx-alignment: center;");
        firstPageButtons.setLayoutX(300);
        firstPageButtons.setLayoutY(150);

        firstPage.getChildren().addAll(backgroundImage, titleLabel, firstPageButtons);

        Scene firstScene = new Scene(firstPage, 800, 800);
         
        Scene secondScene = new Scene(createSecondPage(primaryStage, firstScene), 800, 800);
        nextButton.setOnAction(e -> {
            primaryStage.setScene(secondScene);
        }); 

        primaryStage.setTitle("Main Page");
        primaryStage.setScene(firstScene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    // ****************************** Second Page ***********************************************
    
    public TabPane createSecondPage(Stage primaryStage, Scene firstScene) {
        TabPane secondPage = new TabPane();

        // ------------ Users Tab ------------
        Tab userTab = new Tab("Users");
        Pane userPane = new Pane();
        secondPage.getTabs().add(userTab);

        Image userBackgroundImage = new Image("p1.gif");
        BackgroundImage userBackground = new BackgroundImage(
                userBackgroundImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(800, 800, false, false, false, false)
        );
        userPane.setBackground(new Background(userBackground));

        Label titleLabel = new Label("User Management");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        titleLabel.setStyle("-fx-text-fill: #FFDD00; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.5), 5, 0.0, 2, 2);");
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setPadding(new Insets(10));

        UserTable.setPlaceholder(new Label("No Users available"));
        UserTable.setPrefWidth(600);
        UserTable.setPrefHeight(220);

        TableColumn<User, String> IDColumn = new TableColumn<>("UserID");
        IDColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));

        TableColumn<User, String> NameColumn = new TableColumn<>("User Name");
        NameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<User, Integer> AgeColumn = new TableColumn<>("User Age");
        AgeColumn.setCellValueFactory(new PropertyValueFactory<>("age"));

        UserTable.getColumns().addAll(IDColumn, NameColumn, AgeColumn);
        UserTable.setStyle("-fx-text-fill: black;");

        FriendsTable.setPlaceholder(new Label("No Friends available"));
        FriendsTable.setPrefWidth(280);
        FriendsTable.setPrefHeight(180);
        TableColumn<User, String> FriendIdColumn = new TableColumn<>("User ID");
        FriendIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));

        TableColumn<User, String> FriendNameColumn = new TableColumn<>("Friend Name");
        FriendNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        FriendsTable.getColumns().addAll(FriendIdColumn, FriendNameColumn);
        FriendsTable.setStyle("-fx-text-fill: black;");

        // جدول بوستات المستخدم
        TableView<Post> userPostsTable = new TableView<>();
        userPostsTable.setPlaceholder(new Label("No Posts available"));
        userPostsTable.setPrefWidth(280);
        userPostsTable.setPrefHeight(180);

        TableColumn<Post, String> postIdCol = new TableColumn<>("Post ID");
        postIdCol.setCellValueFactory(new PropertyValueFactory<>("postId"));

        TableColumn<Post, String> postContentCol = new TableColumn<>("Content");
        postContentCol.setCellValueFactory(new PropertyValueFactory<>("content"));

        userPostsTable.getColumns().addAll(postIdCol, postContentCol);
        userPostsTable.setStyle("-fx-text-fill: black;");

        // ---------------- Buttons --------------
        Button AddreadButton = createStyledButton("Add Users", "add.png");
        Button DeleteUserButton = createStyledButton("Delete Users", "remove.png");
        Button UpdateUserButton = createStyledButton("Update Users", "update.png");
        Button SearchUserButton = createStyledButton("Search Users", "search.png");

        AddreadButton.setOnAction(e -> openAddUserStage());
        DeleteUserButton.setOnAction(e -> openDeleteUserStage());
        UpdateUserButton.setOnAction(e -> openUpdateUserStage());
        SearchUserButton.setOnAction(e -> openSearchUserStage());

        HBox Buttons_User_Managment = new HBox(10, AddreadButton, DeleteUserButton, UpdateUserButton, SearchUserButton);

        Button prevButton = createStyledButton("Previous Page", "prev.png");
        prevButton.setOnAction(e -> primaryStage.setScene(firstScene));

        Button nextButton = createStyledButton("Next", "next.png");
        nextButton.setOnAction(e -> {
            if (currentUserNode == null) {
                currentUserNode = networkManagement.getUsersHead();
            } else {
                currentUserNode = currentUserNode.getNext();
            }

            if (currentUserNode != null) {
                updateUserAndFriendsAndPostsTables(currentUserNode.getData(), userPostsTable);
            } else {
                showErrorAlert("No more users available.");
            }
        });

        Button reloadButton = createStyledButton("Reload", "open.png");
        reloadButton.setOnAction(e -> {
            FriendsTable.getItems().clear();
            List<User> users = networkManagement.getUsers();
            ObservableList<User> userData = FXCollections.observableArrayList(users);
            UserTable.setItems(userData);
        });

        ComboBox<String> sortComboBox = new ComboBox<>();
        sortComboBox.getItems().addAll("Sort Ascending", "Sort Descending");
        sortComboBox.setPromptText("Sort Users");
        sortComboBox.setOnAction(e -> {
            String selectedOption = sortComboBox.getValue();
            if (selectedOption != null) {
                boolean ascending = selectedOption.contains("Ascending");
                UserTable.setItems(getUsersDisplay(ascending));
            }
        });

        HBox navigationButtons = new HBox(20, prevButton, reloadButton, nextButton, sortComboBox);
        navigationButtons.setAlignment(Pos.CENTER);

        // الجداول جنب بعض
        HBox friendsAndPostsTables = new HBox(10, FriendsTable, userPostsTable);
        friendsAndPostsTables.setAlignment(Pos.CENTER);

        VBox userTableContainer = new VBox(10, titleLabel, UserTable, Buttons_User_Managment, friendsAndPostsTables, navigationButtons);
        userTableContainer.setLayoutX(50);
        userTableContainer.setLayoutY(30);
        userPane.getChildren().addAll(userTableContainer);
        userTab.setContent(userPane);

        // ******************************** Friends Tab *************************************
        Tab friendsTab = new Tab("Friends");
        Pane friendsPane = new Pane();
        secondPage.getTabs().add(friendsTab);

        Image friendsBackgroundImage = new Image("f4.gif"); 
        BackgroundImage friendsBackground = new BackgroundImage(
                friendsBackgroundImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(800, 800, false, false, false, false)
        );
        friendsPane.setBackground(new Background(friendsBackground));

        Label friendsTitleLabel = new Label("Friends Management");
        friendsTitleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        friendsTitleLabel.setStyle("-fx-text-fill: #FFDD00; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.5), 5, 0.0, 2, 2);");
        friendsTitleLabel.setAlignment(Pos.CENTER);
        friendsTitleLabel.setPadding(new Insets(10));

        friendsTable = new TableView<>();

        friendsTable.setPlaceholder(new Label("No Friends available"));
        friendsTable.setPrefWidth(600);
        friendsTable.setPrefHeight(220);

        TableColumn<ObservableList<String>, String> userIdCol = new TableColumn<>("User ID");
        userIdCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(0)));

        TableColumn<ObservableList<String>, String> friendsCol = new TableColumn<>("Friends");
        friendsCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(1)));

        friendsTable.getColumns().addAll(userIdCol, friendsCol);

        Button addFriendButton = createStyledButton("Add Friend", "add.png");
        Button removeFriendButton = createStyledButton("Remove Friend", "remove.png");
        addFriendButton.setOnAction(e -> openAddFriendStage());
        removeFriendButton.setOnAction(e -> openRemoveFriendStage());
        HBox friendsButtonBox = new HBox(10, addFriendButton, removeFriendButton);
        friendsButtonBox.setAlignment(Pos.CENTER);

        VBox friendsContainer = new VBox(10, friendsTitleLabel, friendsTable, friendsButtonBox);
        friendsContainer.setLayoutX(85);
        friendsContainer.setLayoutY(50);
        friendsPane.getChildren().add(friendsContainer);
        friendsTab.setContent(friendsPane);
      
        // ******************************** Posts Tab *************************************
        Tab postsTab = new Tab("Posts");
        Pane postsPane = new Pane();
        secondPage.getTabs().add(postsTab);
       
        Image postsBackgroundImage = new Image("p2.gif"); 
        BackgroundImage postsBackground = new BackgroundImage(
                postsBackgroundImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(800, 800, false, false, false, false)
        );
        postsPane.setBackground(new Background(postsBackground));

        Label postsTitleLabel = new Label("Posts Management");
        postsTitleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        postsTitleLabel.setStyle("-fx-text-fill: #FFDD00; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.5), 5, 0.0, 2, 2);");
        postsTitleLabel.setAlignment(Pos.CENTER);
        postsTitleLabel.setPadding(new Insets(10));

        postTable.setPlaceholder(new Label("No Posts available"));
        postTable.setPrefWidth(700);
        postTable.setPrefHeight(250);

        TableColumn<Post, String> postIdColumn = new TableColumn<>("Post ID");
        postIdColumn.setCellValueFactory(new PropertyValueFactory<>("postId"));

        TableColumn<Post, String> creatorIdColumn = new TableColumn<>("Creator ID");
        creatorIdColumn.setCellValueFactory(new PropertyValueFactory<>("creatorId"));

        TableColumn<Post, String> postContentColumn = new TableColumn<>("Content");
        postContentColumn.setCellValueFactory(new PropertyValueFactory<>("content"));

        TableColumn<Post, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
            new java.text.SimpleDateFormat("dd.MM.yyyy").format(data.getValue().getCreationDate()))
        );

        TableColumn<Post, String> sharedWithColumn = new TableColumn<>("Shared With");
        sharedWithColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
            data.getValue().getSharedWith().toStringList()
        ));

        postTable.getColumns().addAll(postIdColumn, creatorIdColumn, postContentColumn, dateColumn, sharedWithColumn);

        Button addPostButton = createStyledButton("Add Post", "add.png");
        Button deletePostButton = createStyledButton("Delete Post", "remove.png");
        Button viewSharedPostsButton = createStyledButton("View  Posts", "print.png");
        viewSharedPostsButton.setOnAction(e -> openSharedPostsStage());
        addPostButton.setOnAction(e -> openAddPostStage());
        deletePostButton.setOnAction(e -> openDeletePostStage());
        HBox postsButtonBox = new HBox(10, addPostButton, deletePostButton,viewSharedPostsButton);
        postsButtonBox.setAlignment(Pos.CENTER);
      

        // ******************************** Reports Tab *************************************
        Tab reportsTab = new Tab("Reports");
        Pane reportsPane = new Pane();

        Image reportsBackgroundImage = new Image("p3.gif"); 
        BackgroundImage reportsBackground = new BackgroundImage(
            reportsBackgroundImage,
            BackgroundRepeat.NO_REPEAT,
            BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.CENTER,
            new BackgroundSize(800, 800, false, false, false, false)
        );
        reportsPane.setBackground(new Background(reportsBackground));

        Label reportsTitle = new Label("Reporting & Statistics");
        reportsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        reportsTitle.setStyle("-fx-text-fill: #FFDD00; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 5, 0.0, 2, 2);");
        reportsTitle.setAlignment(Pos.CENTER);
        reportsTitle.setPadding(new Insets(10));

        TextArea reportOutput = new TextArea();
        reportOutput.setEditable(false);
        reportOutput.setPrefSize(600, 300);

        Button btnCreatedByUser = new Button("Posts Created by User");
        Button btnSharedWithUser = new Button("Posts Shared With User");
        Button btnMostActiveUsers = new Button("Most Active Users");
        Button btnEngagement = new Button("Engagement Metrics");

        btnCreatedByUser.setOnAction(e -> {
            StringBuilder result = new StringBuilder();
            for (User user : networkManagement.getUsers()) {
                result.append("User: ").append(user.getName()).append("\n");
                PostNode current = user.getPostsCreated().getHead();
                if (current != null) {
                    do {
                        Post p = current.getData();
                        result.append("  - ").append(p.getContent())
                              .append(" (").append(p.getCreationDate()).append(")\n");
                        current = current.getNext();
                    } while (current != user.getPostsCreated().getHead());
                } else {
                    result.append("  No posts created.\n");
                }
                result.append("\n");
            }
            reportOutput.setText(result.toString());
        });

        btnSharedWithUser.setOnAction(e -> {
            StringBuilder result = new StringBuilder();
            for (User user : networkManagement.getUsers()) {
                result.append("User: ").append(user.getName()).append("\n");
                PostNode current = user.getPostsSharedWith().getHead();
                if (current != null) {
                    do {
                        Post p = current.getData();
                        result.append("  - From ").append(p.getCreatorId())
                              .append(" | ").append(p.getContent())
                              .append(" (").append(p.getCreationDate()).append(")\n");
                        current = current.getNext();
                    } while (current != user.getPostsSharedWith().getHead());
                } else {
                    result.append("  No posts shared with this user.\n");
                }
                result.append("\n");
            }
            reportOutput.setText(result.toString());
        });

        btnMostActiveUsers.setOnAction(e -> {
            StringBuilder result = new StringBuilder();
            List<User> users = networkManagement.getUsers();
            users.sort((u1, u2) -> Integer.compare(
                u2.getPostsCreated().countPosts() + u2.getPostsSharedWith().countPosts(),
                u1.getPostsCreated().countPosts() + u1.getPostsSharedWith().countPosts()
            ));
            result.append("Top Active Users:\n");
            for (int i = 0; i < Math.min(5, users.size()); i++) {
                User u = users.get(i);
                int total = u.getPostsCreated().countPosts() + u.getPostsSharedWith().countPosts();
                result.append((i + 1)).append(") ").append(u.getName()).append(" - Total Posts: ").append(total).append("\n");
            }
            reportOutput.setText(result.toString());
        });

        btnEngagement.setOnAction(e -> {
            StringBuilder result = new StringBuilder();
            for (User user : networkManagement.getUsers()) {
                int created = user.getPostsCreated().countPosts();
                int shared = user.getPostsSharedWith().countPosts();
                result.append("User: ").append(user.getName())
                      .append(" | Created: ").append(created)
                      .append(" | Shared With: ").append(shared).append("\n");
            }
            reportOutput.setText(result.toString());
        });

        HBox buttons = new HBox(10, btnCreatedByUser, btnSharedWithUser, btnMostActiveUsers, btnEngagement);
        buttons.setAlignment(Pos.CENTER);

        VBox reportsContainer = new VBox(10, reportsTitle, buttons, reportOutput);
        reportsContainer.setLayoutX(85);
        reportsContainer.setLayoutY(50);
        reportsPane.getChildren().add(reportsContainer);

        reportsTab.setContent(reportsPane);
        secondPage.getTabs().add(reportsTab);

        VBox postsContainer = new VBox(10, postsTitleLabel, postTable, postsButtonBox);
        postsContainer.setLayoutX(85);
        postsContainer.setLayoutY(50);
        postsPane.getChildren().add(postsContainer);
        postsTab.setContent(postsPane);

        return secondPage;
    }
    
    // ***************************** Method For Styled Buttons **************************************
    
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
    
    // ***************** Alert error load ********
    public void showErrorAlert(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null); 
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    // ******************* Alert success load ****************
    public void showSuccessAlert(String fileName) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("File Load Successful");
        alert.setHeaderText(null); 
        alert.setContentText("The file \"" + fileName + "\" has been loaded successfully.");
        alert.showAndWait();
    }

    // ************************ Update User Table ***************************
    private void updateUserTable() {
        UserTable.getItems().clear(); 
        ObservableList<User> users = networkManagement.getUsers();

        if (users == null) {
            System.out.println("User list is null.");  
            return;
        }

        if (users.isEmpty()) {
            System.out.println("No users found to update."); 
        }
        else {
        UserTable.setItems(users);
    }
    }
    // ************************ Update User Table And Friends Table When Next Button Click  ***************************

    private void updateUserAndFriendsTables(User user) {
        UserTable.getItems().clear();
        FriendsTable.getItems().clear();

        UserTable.getItems().add(user); 

        ObservableList<User> friendsData = FXCollections.observableArrayList(user.getFriends().getUsers());
        FriendsTable.setItems(friendsData);
    }
    private void updateUserAndFriendsAndPostsTables(User user) {
        UserTable.getItems().clear();
        FriendsTable.getItems().clear();
        userPostsTable.getItems().clear();

        UserTable.getItems().add(user);

        ObservableList<User> friendsData = FXCollections.observableArrayList(user.getFriends().getUsers());
        FriendsTable.setItems(friendsData);

        ObservableList<Post> postsData = FXCollections.observableArrayList();
        PostNode current = user.getPostsCreated().getHead();
        if (current != null) {
            do {
                postsData.add(current.getData());
                current = current.getNext();
            } while (current != user.getPostsCreated().getHead());
        }
        userPostsTable.setItems(postsData);
    }


    // **************************** Add Users *********************************
    private void openAddUserStage() {
    	
        Stage stage = new Stage();
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter user name");
        
        TextField userIdField = new TextField();
        userIdField.setPromptText("Enter user ID");
        
        TextField userAgeField = new TextField();
        userAgeField.setPromptText("Enter user age");

        Label userNameLabel = new Label("User Name:");
        Label userIdLabel = new Label("User ID:");
        Label userAgeLabel = new Label("User Age:");

        Button addButton = new Button("Add");
        addButton.setOnAction(e -> {
            String userName = userNameField.getText();
            String userId = userIdField.getText();
            String userAgeInput = userAgeField.getText();

            if (!userName.isEmpty() && !userId.isEmpty() && !userAgeInput.isEmpty()) {
                try {
                    int userAge = Integer.parseInt(userAgeInput);
                    if (networkManagement.userExists(userId)) {
                        showAlert("Error", "User ID already exists.");
                    } else {
                        networkManagement.addUser(new User(userId, userName, userAge));
                        updateUserTable();
                        stage.close();
                    }
                } catch (NumberFormatException ex) {
                    showAlert("Error", "Age must be a valid number.");
                } catch (Exception ex) {
                    showAlert("Error", "An unexpected error occurred: " + ex.getMessage());
                }
            } else {
                showAlert("Error", "Please fill all fields correctly.");
            }
        });

        Button clearButton = new Button("Clear Fields");
        clearButton.setOnAction(e -> {
            userNameField.clear();
            userIdField.clear();
            userAgeField.clear();
        });

        vbox.getChildren().addAll(userNameLabel, userNameField, userIdLabel, userIdField, userAgeLabel, userAgeField, addButton, clearButton);
        Scene scene = new Scene(vbox, 300, 350);
        stage.setScene(scene);
        stage.setTitle("Add User");
        stage.show();
    }

    // *********************** Delete User Stage **********************************
    private void openDeleteUserStage() {
        Stage stage = new Stage();
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        TextField userIdField = new TextField();
        userIdField.setPromptText("Enter user ID to delete");

        Label userIdLabel = new Label("User ID:");

        Button deleteButton = new Button("Delete User");
        deleteButton.setOnAction(e -> {
            String userId = userIdField.getText().trim();

            if (!userId.isEmpty()) {
                if (networkManagement.userExists(userId)) {
                	
                	
                    Alert confirm = new Alert(AlertType.CONFIRMATION);
                    confirm.setTitle("Confirm Deletion");
                    confirm.setHeaderText("Are you sure you want to delete user ID: " + userId + "?");
                    confirm.showAndWait();
                      
                            networkManagement.deleteUser(userId);
                            updateUserTable();
                            updateFriendshipTableDirect(friendsTable); 
                            updatePostTableFromUsers(postTable);

                            stage.close();
                        
                 
                } else {
                    showAlert("Error", "User ID not found.");
                }
            } else {
                showAlert("Error", "Please enter a User ID.");
            }
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> stage.close());

        vbox.getChildren().addAll(userIdLabel, userIdField, deleteButton, cancelButton);
        Scene scene = new Scene(vbox, 300, 150);
        stage.setScene(scene);
        stage.setTitle("Delete User");
        stage.show();
    }

//************************ Update User Stage ***************************

private void openUpdateUserStage() {
  Stage stage = new Stage();
  VBox vbox = new VBox(10);
  vbox.setPadding(new Insets(10));

  TextField userIdField = new TextField();
  userIdField.setPromptText("Enter User ID");

  TextField newNameField = new TextField();
  newNameField.setPromptText("Enter New Name");

  TextField newAgeField = new TextField();
  newAgeField.setPromptText("Enter New Age");

  Button updateButton = new Button("Update User");
  updateButton.setOnAction(e -> {
      String userId = userIdField.getText().trim();
      String newName = newNameField.getText().trim();
      String newAgeStr = newAgeField.getText().trim();

      if (!userId.isEmpty() && !newName.isEmpty() && !newAgeStr.isEmpty()) {
          try {
              int newAge = Integer.parseInt(newAgeStr);
              if (newAge < 10 || newAge > 120) {
                  showAlert("Error", "Please enter a valid age between 10 and 120.");
                  return;
              }

              User user = networkManagement.findUserById(userId);
              if (user != null) {
                  user.setName(newName);
                  user.setAge(newAge);

                  showSuccessAlert("User updated successfully.");
                  updateUserTable();
                  updateFriendshipTableDirect(friendsTable);
                  stage.close();
              } else {
                  showAlert("Error", "User not found.");
              }
          } catch (NumberFormatException ex) {
              showAlert("Error", "Age must be a number.");
          }
      } else {
          showAlert("Error", "Please fill in all fields.");
      }
  });

  Button cancelButton = new Button("Cancel");
  cancelButton.setOnAction(e -> stage.close());

  vbox.getChildren().addAll(
      new Label("User ID:"), userIdField,
      new Label("New Name:"), newNameField,
      new Label("New Age:"), newAgeField,
      updateButton, cancelButton
  );

  Scene scene = new Scene(vbox, 300, 300);
  stage.setScene(scene);
  stage.setTitle("Update User");
  stage.show();
}
//************************ Search For User Stage ***************************

private void openSearchUserStage() {
Stage stage = new Stage();
VBox vbox = new VBox(10);
vbox.setPadding(new Insets(10));

TextField searchField = new TextField();
searchField.setPromptText("Enter User ID");

Label resultLabel = new Label();

Button searchButton = new Button("Search");
searchButton.setOnAction(e -> {
    String userId = searchField.getText().trim();
    if (!userId.isEmpty()) {
        User user = networkManagement.findUserById(userId);
        if (user != null) {
            ObservableList<User> found = FXCollections.observableArrayList();
            found.add(user);
            UserTable.setItems(found);
            updateFriendshipTableDirect(friendsTable); 
            resultLabel.setText("User Found: " + user.getName() + " - Age: " + user.getAge());
        } else {
            resultLabel.setText("User not found.");
        }
    } else {
        resultLabel.setText("Please enter a User ID.");
    }
});
Button closeButton = new Button("Close");
closeButton.setOnAction(e -> stage.close());

vbox.getChildren().addAll(new Label("User ID:"), searchField, searchButton, resultLabel, closeButton);
Scene scene = new Scene(vbox, 300, 250);
stage.setScene(scene);
stage.setTitle("Search User");
stage.show();
}
//***************************** Freinds Table in Freind Tab **********************************

private void updateFriendshipTableDirect(TableView<ObservableList<String>> table) {
 ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();

 for (User user : networkManagement.getUsers()) {
     ObservableList<String> row = FXCollections.observableArrayList();
     row.add(user.getUserId());

     List<String> friendIds = new ArrayList<>();
     for (User friend : user.getFriends().getUsers()) {
         friendIds.add(friend.getUserId()); // أو friend.getName()
     }

     row.add(String.join(", ", friendIds));
     data.add(row);
 }

 table.setItems(data);
}
//************************ Add Friend Stage ***************************
private void openAddFriendStage() {
 Stage stage = new Stage();
 VBox vbox = new VBox(10);
 vbox.setPadding(new Insets(10));

 TextField userIdField = new TextField();
 userIdField.setPromptText("Enter User ID");

 TextField friendIdField = new TextField();
 friendIdField.setPromptText("Enter Friend ID to Add");

 Button addButton = new Button("Add Friend");
 addButton.setOnAction(e -> {
     String userId = userIdField.getText().trim();
     String friendId = friendIdField.getText().trim();

     if (!userId.isEmpty() && !friendId.isEmpty()) {
         User user = networkManagement.findUserById(userId);
         User friend = networkManagement.findUserById(friendId);

         if (user != null && friend != null) {
             user.addFriend(friend);
             showSuccessAlert("Friend added successfully.");
             updateFriendshipTableDirect(friendsTable);
             stage.close();
         } else {
             showAlert("Error", "User or Friend not found.");
         }
     } else {
         showAlert("Error", "Please enter both User ID and Friend ID.");
     }
 });

 Button cancelButton = new Button("Cancel");
 cancelButton.setOnAction(e -> stage.close());

 vbox.getChildren().addAll(
     new Label("User ID:"), userIdField,
     new Label("Friend ID:"), friendIdField,
     addButton, cancelButton
 );

 Scene scene = new Scene(vbox, 300, 250);
 stage.setScene(scene);
 stage.setTitle("Add Friend");
 stage.show();
}
//************************ Remove Friend Stage ***************************
private void openRemoveFriendStage() {
    Stage stage = new Stage();
    VBox vbox = new VBox(10);
    vbox.setPadding(new Insets(10));

    TextField userIdField = new TextField();
    userIdField.setPromptText("Enter User ID");

    TextField friendIdField = new TextField();
    friendIdField.setPromptText("Enter Friend ID to Remove");

    Button removeButton = new Button("Remove Friend");
    removeButton.setOnAction(e -> {
        String userId = userIdField.getText().trim();
        String friendId = friendIdField.getText().trim();

        if (!userId.isEmpty() && !friendId.isEmpty()) {
            User user = networkManagement.findUserById(userId);
            if (user != null) {
                user.removeFriendById(friendId);
                showSuccessAlert("Friend removed successfully.");
                updateFriendshipTableDirect(friendsTable);
                stage.close();
            } else {
                showAlert("Error", "User not found.");
            }
        } else {
            showAlert("Error", "Please enter both User ID and Friend ID.");
        }
    });

    Button cancelButton = new Button("Cancel");
    cancelButton.setOnAction(e -> stage.close());

    vbox.getChildren().addAll(
        new Label("User ID:"), userIdField,
        new Label("Friend ID:"), friendIdField,
        removeButton, cancelButton
    );

    Scene scene = new Scene(vbox, 300, 250);
    stage.setScene(scene);
    stage.setTitle("Remove Friend");
    stage.show();
}
//************************ update Post Table Stage ***************************

private void updatePostTableFromUsers(TableView<Post> table) {
    ObservableList<Post> allPosts = FXCollections.observableArrayList();

    for (User user : networkManagement.getUsers()) {
        PostNode current = user.getPostsCreated().getHead();
        if (current != null) {
            do {
                allPosts.add(current.getData());
                current = current.getNext();
            } while (current != user.getPostsCreated().getHead());
        }
    }

    table.setItems(allPosts);
}
//************************ Add Posts Stage ***************************
private void openAddPostStage() {
    Stage stage = new Stage();
    VBox vbox = new VBox(10);
    vbox.setPadding(new Insets(10));

    TextField postIdField = new TextField();
    postIdField.setPromptText("Post ID");

    TextField creatorIdField = new TextField();
    creatorIdField.setPromptText("Creator ID");

    TextField contentField = new TextField();
    contentField.setPromptText("Content");

    ComboBox<String> shareOptionComboBox = new ComboBox<>();
    shareOptionComboBox.getItems().addAll("Share with All Friends", "Share with Specific Friends");
    shareOptionComboBox.setPromptText("Select Sharing Option");

    TextField specificFriendsField = new TextField();
    specificFriendsField.setPromptText("Enter Friends' IDs (comma-separated)");
    specificFriendsField.setVisible(false); 

    DatePicker datePicker = new DatePicker();
    datePicker.setPromptText("Select Date");

    shareOptionComboBox.setOnAction(e -> {
        String selected = shareOptionComboBox.getValue();
        if (selected.equals("Share with Specific Friends")) {
            specificFriendsField.setVisible(true);
        } else {
            specificFriendsField.setVisible(false);
        }
    });

    Button addButton = new Button("Add Post");
    addButton.setOnAction(e -> {
        String postId = postIdField.getText().trim();
        String creatorId = creatorIdField.getText().trim();
        String content = contentField.getText().trim();
        String selectedShareOption = shareOptionComboBox.getValue();
        LocalDate selectedDate = datePicker.getValue();

        if (postId.isEmpty() || creatorId.isEmpty() || content.isEmpty() || selectedDate == null || selectedShareOption == null) {
            showAlert("Error", "Please fill all fields and select sharing option.");
            return;
        }

        try {
            Date date = java.sql.Date.valueOf(selectedDate);
            User creator = networkManagement.findUserById(creatorId);
            if (creator != null) {
                Post post = new Post(postId, creatorId, content, date);
                creator.getPostsCreated().addPost(post);

                if (selectedShareOption.equals("Share with All Friends")) {
                    for (User friend : creator.getFriends().getUsers()) {
                        post.shareWith(friend.getUserId());
                        friend.getPostsSharedWith().addPost(post);
                    }
                } else if (selectedShareOption.equals("Share with Specific Friends")) {
                    String idsText = specificFriendsField.getText().trim();
                    if (idsText.isEmpty()) {
                        showAlert("Error", "Please enter at least one friend's ID.");
                        return;
                    }
                    String[] ids = idsText.split(",");
                    for (String id : ids) {
                        String userId = id.trim();
                        User friend = networkManagement.findUserById(userId);

                        if (friend != null && creator.getFriends().findUserById(userId) != null) {
                            post.shareWith(userId);
                            friend.getPostsSharedWith().addPost(post);
                        } else {
                            showAlert("Warning", "User ID " + userId + " is not a friend. Skipped.");
                        }
                    }
                }

                showSuccessAlert("Post added successfully.");
                updatePostTableFromUsers(postTable);
                stage.close();
            } else {
                showAlert("Error", "Creator ID not found.");
            }
        } catch (Exception ex) {
            showAlert("Error", "Unexpected error: " + ex.getMessage());
        }
    });
    Button cancelButton = new Button("Cancel");
    cancelButton.setOnAction(e -> stage.close());

    vbox.getChildren().addAll(
        new Label("Post ID:"), postIdField,
        new Label("Creator ID:"), creatorIdField,
        new Label("Content:"), contentField,
        new Label("Date:"), datePicker,
        new Label("Sharing Option:"), shareOptionComboBox,
        specificFriendsField,
        addButton, cancelButton
    );

    stage.setScene(new Scene(vbox, 420, 500));
    stage.setTitle("Add Post");
    stage.show();
}
//************************ Delete Posts Stage ***************************
private void openDeletePostStage() {
    Stage stage = new Stage();
    VBox vbox = new VBox(10);
    vbox.setPadding(new Insets(10));
    TextField userIdField = new TextField();
    userIdField.setPromptText("Creator ID");
    TextField postIdField = new TextField();
    postIdField.setPromptText("Post ID to Delete");
    Button deleteButton = new Button("Delete Post");
    deleteButton.setOnAction(e -> {
        String userId = userIdField.getText().trim();
        String postId = postIdField.getText().trim();
        User user = networkManagement.findUserById(userId);
        if (user != null) {
            PostNode current = user.getPostsCreated().getHead();
            if (current != null) {
                do {
                    Post p = current.getData();
                    if (p.getPostId().equals(postId)) {
                        user.getPostsCreated().deletePost(postId);

                        StringNode sharedCurrent = p.getSharedWith().getHead();
                        if (sharedCurrent != null) {
                            do {
                                User sharedUser = networkManagement.findUserById(sharedCurrent.getData());
                                if (sharedUser != null) {
                                    sharedUser.getPostsSharedWith().deletePost(postId);
                                }
                                sharedCurrent = sharedCurrent.getNext();
                            } while (sharedCurrent != p.getSharedWith().getHead());
                        }

                        showSuccessAlert("Post deleted successfully.");
                        updatePostTableFromUsers(postTable);
                        stage.close();
                        return;
                    }
                    current = current.getNext();
                } while (current != user.getPostsCreated().getHead());
            }

            showAlert("Error", "Post ID not found for this user.");
        } else {
            showAlert("Error", "User not found.");
        }
    });
    Button cancelButton = new Button("Cancel");
    cancelButton.setOnAction(e -> stage.close());

    vbox.getChildren().addAll(
        new Label("Creator ID:"), userIdField,
        new Label("Post ID to delete:"), postIdField,
        deleteButton, cancelButton
    );

    stage.setScene(new Scene(vbox, 300, 250));
    stage.setTitle("Delete Post");
    stage.show();
}
//************************ Shared Posts Stage ***************************

private void openSharedPostsStage() {
    Stage stage = new Stage();
    VBox vbox = new VBox(10);
    vbox.setPadding(new Insets(10));

    TextField userIdField = new TextField();
    userIdField.setPromptText("Enter User ID");

    ComboBox<String> viewOptionComboBox = new ComboBox<>();
    viewOptionComboBox.getItems().addAll("Show My Posts", "Show Shared Posts");
    viewOptionComboBox.setPromptText("Select View Option");

    TableView<Post> postsTable = new TableView<>();
    postsTable.setPrefHeight(300);

    TableColumn<Post, String> postIdCol = new TableColumn<>("Post ID");
    postIdCol.setCellValueFactory(new PropertyValueFactory<>("postId"));

    TableColumn<Post, String> contentCol = new TableColumn<>("Content");
    contentCol.setCellValueFactory(new PropertyValueFactory<>("content"));

    TableColumn<Post, String> dateCol = new TableColumn<>("Date");
    dateCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
        new java.text.SimpleDateFormat("yyyy-MM-dd").format(data.getValue().getCreationDate())
    ));

    TableColumn<Post, String> creatorCol = new TableColumn<>("Creator ID");
    creatorCol.setCellValueFactory(new PropertyValueFactory<>("creatorId"));

    postsTable.getColumns().addAll(postIdCol, contentCol, dateCol, creatorCol);

    Button viewButton = new Button("Show Posts");
    viewButton.setOnAction(e -> {
        String userId = userIdField.getText().trim();
        String selectedOption = viewOptionComboBox.getValue();
        User user = networkManagement.findUserById(userId);

        if (user != null && selectedOption != null) {
            ObservableList<Post> posts = FXCollections.observableArrayList();

            if (selectedOption.equals("Show My Posts")) {
                PostNode current = user.getPostsCreated().getHead();
                if (current != null) {
                    do {
                        posts.add(current.getData());
                        current = current.getNext();
                    } while (current != user.getPostsCreated().getHead());
                }
            } else if (selectedOption.equals("Show Shared Posts")) {
                PostNode current = user.getPostsSharedWith().getHead();
                if (current != null) {
                    do {
                        posts.add(current.getData());
                        current = current.getNext();
                    } while (current != user.getPostsSharedWith().getHead());
                }
            }

            postsTable.setItems(posts);
        } else {
            showAlert("Error", "Please enter User ID and select a view option.");
        }
    });

    Button closeButton = new Button("Close");
    closeButton.setOnAction(e -> stage.close());

    vbox.getChildren().addAll(
        new Label("User ID:"), userIdField,
        viewOptionComboBox,
        viewButton,
        postsTable,
        closeButton
    );

    stage.setScene(new Scene(vbox, 650, 550));
    stage.setTitle("User Posts Viewer");
    stage.show();
}

//*************** Alert error load ********
public void showErrorAlert1(String fileName) {
    Alert alert = new Alert(AlertType.ERROR);
    alert.setTitle("File Load Error");
    alert.setHeaderText(null); 
    alert.setContentText("Failed to load the " + fileName + " file. Please try again.");
    alert.showAndWait();
}
// ********************************** ************8**********8
private void showAlert(String title, String message) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
}
//**************** Alert succes lode ****************

public void showSuccessAlert1(String fileName) {
Alert alert = new Alert(AlertType.INFORMATION);
alert.setTitle("File Load Successful");
alert.setHeaderText(null); 
alert.setContentText("The file \"" + fileName + "\" has been loaded successfully.");
alert.showAndWait();
}
private ObservableList<User> getUsersDisplay(boolean ascending) {
    List<User> userList = networkManagement.getUsers(); 
    ObservableList<User> displayList = FXCollections.observableArrayList();

    if (ascending) {
        displayList.addAll(userList);
    } else {
        for (int i = userList.size() - 1; i >= 0; i--) {
            displayList.add(userList.get(i));
        }
    }
    return displayList;
}
private void updateUserAndFriendsAndPostsTables(User user, TableView<Post> userPostsTable) {
    UserTable.getItems().clear();
    FriendsTable.getItems().clear();
    userPostsTable.getItems().clear();

    UserTable.getItems().add(user);

    ObservableList<User> friendsData = FXCollections.observableArrayList(user.getFriends().getUsers());
    FriendsTable.setItems(friendsData);

    ObservableList<Post> postsData = FXCollections.observableArrayList();
    PostNode current = user.getPostsCreated().getHead();
    if (current != null) {
        do {
            postsData.add(current.getData());
            current = current.getNext();
        } while (current != user.getPostsCreated().getHead());
    }
    userPostsTable.setItems(postsData);
}
private void updatePostsAfterUserDeletion(String deletedUserId) {
    for (User user : networkManagement.getUsers()) {
        
        PostNode createdPostNode = user.getPostsCreated().getHead();
        if (createdPostNode != null) {
            PostNode temp = createdPostNode;
            boolean firstIteration = true;
            do {
                if (temp.getData().getCreatorId().equals(deletedUserId)) {
                    user.getPostsCreated().deletePost(temp.getData().getPostId());
                }
                temp = temp.getNext();
                firstIteration = false;
            } while (temp != createdPostNode || firstIteration);
        }

        PostNode sharedPostNode = user.getPostsSharedWith().getHead();
        if (sharedPostNode != null) {
            PostNode temp = sharedPostNode;
            boolean firstIteration = true;
            do {
                temp.getData().getSharedWith().removeUser(deletedUserId);
                temp = temp.getNext();
                firstIteration = false;
            } while (temp != sharedPostNode || firstIteration);
        }
    }

}
}