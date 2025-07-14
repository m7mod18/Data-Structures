package application;
	
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class project4 extends Application {
	 TableView<Movie> movieTable = new TableView<>();
	 MovieCatalog catalog = new MovieCatalog();
     ComboBox<String> sortOrderSelector = new ComboBox<>();
     private static int currentIndex = 0;
     String mode = "TopAndLeast";
     HBox statsBox = new HBox(5); 
	   
     Label numbermov = new Label();
      Label indexLabel = new Label();
      Label heightLabel = new Label();
      Label countLabel = new Label();
      Label moviesLabel = new Label();
      Label tableSizeLabel = new Label();
      Label rehashCountLabel = new Label();
      
	   @Override
	    public void start(Stage primaryStage) {
	        Pane firstPage = new Pane();
	        movieTable.setStyle("-fx-text-fill: black; -fx-background-color: white;");

	        ImageView backgroundImage = new ImageView(new Image("mov.gif"));
	        backgroundImage.setFitWidth(800);
	        backgroundImage.setFitHeight(800);

	        ImageView titleIcon = new ImageView(new Image("movtit.png"));
	        titleIcon.setFitHeight(50);
	        titleIcon.setFitWidth(50);

	        Label titleLabel = new Label("Movie Catalog Management System", titleIcon);
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
	        Button readFileButton = createStyledButton("Open File", "open.png");
	        Button saveFileButton = createStyledButton("Save File", "print.png");
	        Button exitButton = createStyledButton("Exit", "exit.png");
	        Button nextButton = createStyledButton("Next Page", "next.png");
	        
	        // ************************************************ File Choser ********************************88
	        FileChooser fileChooser = new FileChooser();
			fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));

	        readFileButton.setOnAction(e -> {
	            try {
	                File MovieFile = fileChooser.showOpenDialog(primaryStage);

	                if (MovieFile != null) {
	                	readMoviesFromFile(MovieFile , catalog);
	                	
	        	        movieTable.setItems(catalog.getAllMovies());
	                  	 showSuccessAlert(MovieFile.getName());
	                  
	   
	                  	printHashTableStats(catalog);
	                } else {
	                    showErrorAlert("No  file selected. Please select a valid passenger file.");
	                    
	                }
	            } catch (Exception ex) {
	                showErrorAlert("Error loading passengers: " + ex.getMessage());
	            }
	        });
	        
	        saveFileButton.setOnAction(e -> {
	            FileChooser fileChooserSave = new FileChooser();
	            fileChooserSave.setTitle("Save Catalog");
	            fileChooserSave.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));

	            File file = fileChooserSave.showSaveDialog(primaryStage); 
	            if (file != null) {
	                saveCatalogToFile(file, catalog); 
	            }
	        });
	        
	        VBox firstPageButtons = new VBox(20, readFileButton, saveFileButton, exitButton, nextButton);
	        firstPageButtons.setPadding(new Insets(20));
	        firstPageButtons.setStyle("-fx-alignment: center;");
	        firstPageButtons.setLayoutX(300);
	        firstPageButtons.setLayoutY(150);

	        firstPage.getChildren().addAll(backgroundImage, titleLabel, firstPageButtons);

	        
	        Scene firstScene = new Scene(firstPage, 800, 800);
	        Scene secondScene = new Scene(createSecondPage(primaryStage, firstScene), 800, 800);
	        movieTable.setRowFactory(tv -> {
	            TableRow<Movie> row = new TableRow<>();
	            row.setOnMouseEntered(e -> row.setStyle("-fx-background-color: #f0f0f0;"));
	            row.setOnMouseExited(e -> row.setStyle("-fx-background-color: transparent;"));
	            return row;
	        });  

	        nextButton.setOnAction(e -> primaryStage.setScene(secondScene));
	        exitButton.setOnAction(e -> primaryStage.close());

	        primaryStage.setTitle("Movie Catalog Management System");
	        primaryStage.setScene(firstScene);
	        primaryStage.show();
	    }  
	   
	   // 777777777777777777777777777777777777777777777777777777777777777777
	   public void printTopAndLeastRankedMovies(MovieCatalog catalog) {
		    ObservableList<Movie> topAndLeastMovies = FXCollections.observableArrayList();

		    for (int i = 0; i < catalog.getHashTable().length; i++) {
		        AVLTree tree = catalog.getHashTable()[i];

		        if (tree != null && !tree.isEmpty()) {
		            Movie topRankedMovie = tree.getMax();
		            if (topRankedMovie != null) {
		                topAndLeastMovies.add(topRankedMovie); 
		            }

		            Movie leastRankedMovie = tree.getMinByRating();
		            if (leastRankedMovie != null && !leastRankedMovie.equals(topRankedMovie)) {
		                topAndLeastMovies.add(leastRankedMovie); 
		            }
		            
		        }
		    }

		    movieTable.setItems(topAndLeastMovies);
		}


	               
	   //77777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777
	   
	   public void readMoviesFromFile(File file, MovieCatalog catalog) {
		    if (file != null) {
		        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
		            String line;
		            String title = null, description = null;
		            int releaseYear = 0;
		            double rating = 0.0;

		            while ((line = br.readLine()) != null) {
		                line = line.trim(); // Remove leading and trailing whitespaces
		                if (line.isEmpty()) {
		                    // End of a movie's data; create the movie and reset variables
		                    if (title != null && description != null) {
		                        Movie movie = new Movie(title, description, releaseYear, rating);
		                        catalog.add(movie); // Add the movie to the catalog
		                    }
		                    // Reset for the next movie
		                    title = null;
		                    description = null;
		                    releaseYear = 0;
		                    rating = 0.0;
		                } else if (line.startsWith("Title:")) {
		                    title = line.substring(6).trim();
		                } else if (line.startsWith("Description:")) {
		                    description = line.substring(12).trim();
		                } else if (line.startsWith("Release Year:")) {
		                    releaseYear = Integer.parseInt(line.substring(13).trim());
		                } else if (line.startsWith("Rating:")) {
		                    rating = Double.parseDouble(line.substring(7).trim());
		                }
		            }

		            if (title != null && description != null) {
		                Movie movie = new Movie(title, description, releaseYear, rating);
		                catalog.add(movie);
		            }

		        } catch (Exception e) {
		            e.printStackTrace();
		        }
		    } else {
		        System.out.println("No file was selected.");
		    }
		}


	   // *************************************************************  " Second page "***************************************************************

	   public Pane createSecondPage(Stage primaryStage, Scene firstScene) {
		   
	        Pane secondPage = new Pane();
	        Image backgroundImage = new Image("secpage.jpg"); 
	        BackgroundImage background = new BackgroundImage(
	                backgroundImage,
	                BackgroundRepeat.NO_REPEAT,
	                BackgroundRepeat.NO_REPEAT,
	                BackgroundPosition.CENTER,
	                new BackgroundSize(800, 800, false, false, false, false)
	        );
	        secondPage.setBackground(new Background(background));

	        movieTable.setPlaceholder(new Label("No movies available"));
	        movieTable.setPrefWidth(630);
	        movieTable.setPrefHeight(265);
	        movieTable.setLayoutY(100);
	        
	        TableColumn<Movie, String> titleColumn = new TableColumn<>("Title");
	        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));

	        TableColumn<Movie, String> descriptionColumn = new TableColumn<>("Description");
	        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

	        TableColumn<Movie, Integer> yearColumn = new TableColumn<>("Release Year");
	        yearColumn.setCellValueFactory(new PropertyValueFactory<>("releaseYear"));

	        
	        TableColumn<Movie, Double> ratingColumn = new TableColumn<>("Rating");
	        ratingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));


	        movieTable.getColumns().addAll(titleColumn, descriptionColumn, yearColumn, ratingColumn);
	        movieTable.setStyle("-fx-text-fill: black;");
	       
	        statsBox.setAlignment(Pos.TOP_LEFT);  


	        statsBox.getChildren().addAll(indexLabel, heightLabel, countLabel, moviesLabel,numbermov);

	        VBox tableContainer = new VBox(movieTable);
	        tableContainer.setLayoutX(85); 
	        tableContainer.setLayoutY(100);

	        Label titleLabel = new Label("Movies List");
	        titleLabel.setTextFill(Color.WHITE);
	        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.5), 5, 0.0, 2, 2);"); // إضافة ظلال للنص

	        ImageView icon = new ImageView(new Image("movtit.png"));
	        icon.setFitHeight(30);
	        icon.setFitWidth(30);

	        titleLabel.setOnMouseEntered(e -> titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #1E3A5F; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.7), 6, 0.0, 3, 3);"));
	        titleLabel.setOnMouseExited(e -> titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.5), 5, 0.0, 2, 2);"));

	        icon.setOnMouseEntered(e -> icon.setFitHeight(35)); 
	        icon.setOnMouseExited(e -> icon.setFitHeight(30)); 

	        HBox titleContainer = new HBox(10, icon, titleLabel);
	        
	        titleContainer.setLayoutX(325); 
	        titleContainer.setLayoutY(50);
	        tableSizeLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");
	        rehashCountLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");
	        
	        Button reloadButton = createStyledButton("Reload Catalog", "update.png");
	       
	        reloadButton.setOnMousePressed(e -> reloadButton.setScaleX(0.95));
	        reloadButton.setOnMouseReleased(e -> reloadButton.setScaleX(1));
	        Button nextButton = createStyledButton("Next", "next.png");
	        Button prev2Button = createStyledButton("Previous", "prev.png");
	        Button addButton = createStyledButton("Add Movie", "add.png");
	        addButton.setOnAction(e -> showAddMovieWindow());

	        Button updateButton = createStyledButton("Update Movie", "update.png");
	        updateButton.setOnAction(e ->  showUpdateMovieWindow());
	        
	       
	        Button deleteButton =createStyledButton("Delete Movie", "remove.png");
	        deleteButton.setOnAction(e -> showDeleteMovieWindow(catalog,movieTable));

	        Button printSortedButton = createStyledButton("print Movie's sorted", "print.png");  
	        Button topLeastButton = createStyledButton("Top Ranked", "top.png");
	        Button searchButton =createStyledButton("Search Movie", "search.png");
	        searchButton.setOnAction(e ->  showSearchMovieWindow(movieTable,catalog));
	        
	        topLeastButton.setOnAction(e -> printTopAndLeastRankedMovies(catalog));
	        Button prevButton = createStyledButton("Previous Page", "prev.png");
	        prevButton.setOnMousePressed(e -> prevButton.setScaleX(0.95));
	        prevButton.setOnMouseReleased(e -> prevButton.setScaleX(1));
	        // *****************************************************************************************888
	        
	       
	        

	        sortOrderSelector.getItems().addAll("Ascending", "Descending");
	        sortOrderSelector.setValue("Ascending");
	        sortOrderSelector.setDisable(true); // 
	       
	        Button switchToTopAndLeastButton =  createStyledButton("Switch to Top & Least Mode", "print.png");

	        switchToTopAndLeastButton.setOnAction(e -> {
	            AVLTree tree = catalog.getHashTable()[currentIndex];

	            if (tree != null && !tree.isEmpty()) {
	                ObservableList<Movie> topAndLeastMovies = FXCollections.observableArrayList();

	                Movie topRankedMovie = tree.getMaxByRating(); 
	                Movie leastRankedMovie = tree.getMinByRating(); 

	                if (topRankedMovie != null) {
	                    topAndLeastMovies.add(topRankedMovie);
	                }

	                if (leastRankedMovie != null && !leastRankedMovie.equals(topRankedMovie)) {
	                    topAndLeastMovies.add(leastRankedMovie);
	                }

	                movieTable.setItems(topAndLeastMovies);
	            } else {
	                movieTable.setItems(FXCollections.observableArrayList());
	                System.out.println("Tree at index " + currentIndex + " is empty or does not exist.");
	            }
	        });

	       

	        sortOrderSelector.setOnAction(e -> {
	            updateTableForCurrentMode(catalog);
	        });


	        nextButton.setOnAction(e -> {
	            if (currentIndex < catalog.getHashTable().length - 1) {
	                currentIndex++;
	                mode = "Sorted by Title"; 
	                sortOrderSelector.setDisable(false); 
	                
	                updateTableForCurrentMode(catalog); 
	                displayHashTableStats(catalog); 
	            } else {
	                showAlert("You are already at the last index.");
	            }
	        });

	        prev2Button.setOnAction(e -> {
	            if (currentIndex > 0) {
	                currentIndex--;
	                mode = "Sorted by Title";
	                sortOrderSelector.setDisable(false); 
	               
	                updateTableForCurrentMode(catalog); 
	                displayHashTableStats(catalog); 
	            } else {
	                showAlert("You are already at the first index.");
	         }
	        });
	        reloadButton.setOnAction(e -> {
	            ObservableList<Movie> allMovies = catalog.getAllMovies();
	            movieTable.setItems(allMovies);  
              	printHashTableStats(catalog);
              	currentIndex =0 ;
              	displayHashTableStats(catalog);
              
	        });
	        String defaultStyle = "-fx-font-size: 14px; -fx-background-color: #555; -fx-text-fill: white; -fx-padding: 5px 10px; -fx-background-radius: 10;";

	        String hoverStyle = "-fx-font-size: 14px; -fx-background-color: #777; -fx-text-fill: white; -fx-padding: 5px 10px; -fx-background-radius: 10;";

	        indexLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white;"); 
	        heightLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");
	        countLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");

	        sortOrderSelector.setStyle(defaultStyle);
	        sortOrderSelector.setOnMouseEntered(e -> sortOrderSelector.setStyle(hoverStyle));
	        sortOrderSelector.setOnMouseExited(e -> sortOrderSelector.setStyle(defaultStyle));
	        Label operationsListLabel = new Label(" 'Operations List' ");
	        Label dataViewMethodLabel = new Label(" 'Data View Method' ");
	         
	        
	        String labelStyle = "-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.5), 5, 0.0, 2, 2);";

	        operationsListLabel.setStyle(labelStyle);
	        dataViewMethodLabel.setStyle(labelStyle);
	     
	        operationsListLabel.setOnMouseEntered(e -> operationsListLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1E3A5F; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.7), 6, 0.0, 3, 3);"));
	        operationsListLabel.setOnMouseExited(e -> operationsListLabel.setStyle(labelStyle));

	        dataViewMethodLabel.setOnMouseEntered(e -> dataViewMethodLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1E3A5F; -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.7), 6, 0.0, 3, 3);"));
	        dataViewMethodLabel.setOnMouseExited(e -> dataViewMethodLabel.setStyle(labelStyle));

	        //*********************************************************************************************
	        
	        HBox HeadButtons = new HBox(80, prev2Button,reloadButton, nextButton);
	        

	        HeadButtons.setLayoutX(120); 
	        HeadButtons.setLayoutY(420);
	       
	        VBox secondPageButtons1 = new VBox(10,operationsListLabel,addButton, updateButton, deleteButton,searchButton);
	        
	        VBox secondPageButtons2 = new VBox(10,dataViewMethodLabel,switchToTopAndLeastButton,sortOrderSelector
	        		,prevButton);
	        
	        secondPageButtons1.setLayoutX(220); 
	        secondPageButtons1.setLayoutY(480);
	        
	        statsBox.setLayoutX(120); 
	        statsBox.setLayoutY(370);
	       
	        numbermov.setLayoutX(220); 
	        numbermov.setLayoutY(370);
	        secondPageButtons2.setLayoutX(420); 
	        secondPageButtons2.setLayoutY(480);
	       
	        statsBox.getChildren().addAll(tableSizeLabel, rehashCountLabel);
	        secondPage.getChildren().addAll(titleContainer, tableContainer,statsBox, HeadButtons,secondPageButtons1,secondPageButtons2,numbermov);
	        
	        prevButton.setOnAction(e -> primaryStage.setScene(firstScene));

	        return secondPage;
	   
	   }
	   
	    // ******************************************************************************************

	   public void displayHashTableStats(MovieCatalog catalog) {
		    AVLTree tree = catalog.getHashTable()[currentIndex];

		    indexLabel.setText("Index " + currentIndex + ":");

		    if (tree != null && !tree.isEmpty()) {
		        heightLabel.setText("  - Height: " + tree.getHeight());
		        countLabel.setText("  - Number of Movies: " + tree.countNodes());
		    } else {
		        heightLabel.setText("  - Height: N/A");
		        countLabel.setText("  - Number of Movies: 0");
		    }

		    tableSizeLabel.setText(" - Table Size: " + catalog.getTableSize());
		    rehashCountLabel.setText(" - Rehashes: " + catalog.getRehashCount());
		}

	   //*******************************************************************************************8
	   private void showAlert(String message) {
	       Alert alert = new Alert(Alert.AlertType.WARNING);
	       alert.setTitle("Selection Required");
	       alert.setHeaderText(null);
	       alert.setContentText(message);
	       alert.showAndWait();
	   }
	    //  ************************************************************** Top RAted ?//////////////////////////
	   public void updateTableForCurrentMode(MovieCatalog catalog) {
		    AVLTree tree = catalog.getHashTable()[currentIndex];

		    if (tree != null && !tree.isEmpty()) {
		        List<Movie> list = tree.inOrderList();

		        for (int i = 0; i < list.size() - 1; i++) {
		            for (int j = 0; j < list.size() - i - 1; j++) {
		                Movie m1 = list.get(j);
		                Movie m2 = list.get(j + 1);
		                boolean ascending = "Ascending".equals(sortOrderSelector.getValue());

		                int compare = m1.getTitle().compareToIgnoreCase(m2.getTitle());

		                if ((ascending && compare > 0) || (!ascending && compare < 0)) {
		                    
		                    list.set(j, m2);
		                    list.set(j + 1, m1);
		                }
		            }
		        }

		        ObservableList<Movie> sortedMovies = FXCollections.observableArrayList(list);
		        movieTable.setItems(sortedMovies);
		        System.out.println("Displaying sorted movies by title at index " + currentIndex + " (Bubble Sorted)");

		    } else {
		        movieTable.setItems(FXCollections.observableArrayList());
		        System.out.println("Tree at index " + currentIndex + " is empty or does not exist.");
		    }
		}



	   
  // ********************************************************************   "Button style' *******************************************************************
	   private Button createStyledButton(String text, String iconPath) {
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


	    public static void main(String[] args) {
	        launch(args);
	    }
	    // ************************************************************************************************
	    public void printHashTableStats(MovieCatalog catalog) {
	        for (int i = 0; i < catalog.getHashTable().length; i++) {
	            AVLTree tree = catalog.getHashTable()[i];
	            if (tree != null && !tree.isEmpty()) {
	                System.out.println("Index " + i + ": ");
	                System.out.println("  - Height: " + tree.getHeight());
	                System.out.println("  - Number of Movies: " + tree.countNodes());
	                System.out.println("  - Movies:");

	                List<Movie> movies = tree.inOrderList();  // ← استخدام الطريقة الجديدة
	                for (Movie m : movies) {
	                    System.out.println("    * " + m);
	                }

	            } else {
	                System.out.println("Index " + i + ": Empty");
	            }
	        }
	    }


	  // ***************************** Save to File ****************************************
	    public void saveCatalogToFile(File file, MovieCatalog catalog) {
	        if (file != null) {
	            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
	                for (Movie movie : catalog.getAllMovies()) {
	                    writer.write("Title: " + movie.getTitle());
	                    writer.newLine();
	                    writer.write("Description: " + movie.getDescription());
	                    writer.newLine();
	                    writer.write("Release Year: " + movie.getReleaseYear());
	                    writer.newLine();
	                    writer.write("Rating: " + movie.getRating());
	                    writer.newLine();
	                    writer.newLine();
	                }
	                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Catalog saved successfully!");
	                alert.showAndWait();
	            } catch (IOException e) {
	                Alert alert = new Alert(Alert.AlertType.ERROR, "Error saving catalog: " + e.getMessage());
	                alert.showAndWait();
	            }
	        } else {
	            Alert alert = new Alert(Alert.AlertType.ERROR, "File not selected!");
	            alert.showAndWait();
	        }
	    }
	    
//**************** Alert succes lode ****************
public void showSuccessAlert(String fileName) {
    Alert alert = new Alert(AlertType.INFORMATION);
    alert.setTitle("File Load Successful");
    alert.setHeaderText(null); 
    alert.setContentText("The file \"" + fileName + "\" has been loaded successfully.");
    alert.showAndWait();
}
//*************** Alert error load ********
public void showErrorAlert(String fileName) {
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
//********************************** Add movie ******************************************
public void showAddMovieWindow() {
    Stage addMovieStage = new Stage();
    addMovieStage.setTitle("Add Movie");

    Label titleLabel = new Label("Title:");
    titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
    TextField titleField = new TextField();
    titleField.setStyle("-fx-background-color: #444; -fx-text-fill: white; -fx-prompt-text-fill: gray;");

    Label descriptionLabel = new Label("Description:");
    descriptionLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
    TextField descriptionField = new TextField();
    descriptionField.setStyle("-fx-background-color: #444; -fx-text-fill: white; -fx-prompt-text-fill: gray;");

    Label yearLabel = new Label("Release Year:");
    yearLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
    DatePicker yearPicker = new DatePicker();
    yearPicker.setStyle("-fx-background-color: #444; -fx-text-fill: white; -fx-prompt-text-fill: gray;");

    Label ratingLabel = new Label("Rating:");
    ratingLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
    TextField ratingField = new TextField();
    ratingField.setStyle("-fx-background-color: #444; -fx-text-fill: white; -fx-prompt-text-fill: gray;");

    Button saveButton = new Button("Save");
    saveButton.setStyle("-fx-background-color: #5cb85c; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");

    saveButton.setOnAction(e -> {
        String title = titleField.getText().trim();
        String description = descriptionField.getText().trim();
        int releaseYear;
        double rating;

        if (title.isEmpty() || description.isEmpty()) {
            showAlert("Invalid Input", "Title and Description cannot be empty.");
            return;
        }

        if (catalog.get(title) != null) {
            showAlert("Duplicate Movie", "A movie with the title \"" + title + "\" already exists in the catalog.");
            return;
        }

        if (yearPicker.getValue() == null) {
            showAlert("Invalid Input", "Please select a valid release year.");
            return;
        }

        releaseYear = yearPicker.getValue().getYear();
        int currentYear = java.time.Year.now().getValue();
        if (releaseYear < 1888 || releaseYear > currentYear) {
            showAlert("Invalid Input", "Please enter a realistic release year (1888 - " + currentYear + ").");
            return;
        }

        try {
            rating = Double.parseDouble(ratingField.getText());
            if (rating < 0.0 || rating > 10.0) {
                showAlert("Invalid Input", "Rating must be between 0.0 and 10.0.");
                return;
            }
        } catch (NumberFormatException ex) {
            showAlert("Invalid Input", "Rating must be a valid number.");
            return;
        }

        Movie newMovie = new Movie(title, description, releaseYear, rating);
        catalog.add(newMovie);

        int index = catalog.hashFunction(title);
        movieTable.setItems(catalog.getAllMovies());
        movieTable.refresh();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText("Movie Added Successfully");
        alert.setContentText("The movie \"" + title + "\" was added at index " + index + " in the hash table.");
        alert.showAndWait();

        addMovieStage.close();
    });

    Button cancelButton = new Button("Cancel");
    cancelButton.setStyle("-fx-background-color: #d9534f; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
    cancelButton.setOnAction(e -> addMovieStage.close());

    HBox buttonBox = new HBox(10, saveButton, cancelButton);
    buttonBox.setPadding(new Insets(10, 0, 0, 0));
    buttonBox.setStyle("-fx-alignment: center;");

    VBox gridPane = new VBox(10);
    gridPane.getChildren().addAll(titleLabel, titleField, descriptionLabel, descriptionField, yearLabel, yearPicker, ratingLabel, ratingField, buttonBox);
    gridPane.setPadding(new Insets(20));
    gridPane.setStyle("-fx-background-color: #222; -fx-font-family: Arial; -fx-border-color: #555; -fx-border-radius: 10px; -fx-border-width: 2px;");

    Scene scene = new Scene(gridPane, 500, 400);
    addMovieStage.setScene(scene);
    addMovieStage.show();
}


// ******************************************** Update Movie ************************************************

public void showUpdateMovieWindow() {
    Stage updateStage = new Stage();
    updateStage.setTitle("Update Movie");

    VBox root = new VBox(15);
    root.setPadding(new Insets(20));
    root.setStyle("-fx-background-color: #222; -fx-font-family: Arial;");

    Label headerLabel = new Label("Update Movie");
    headerLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
    headerLabel.setPadding(new Insets(0, 0, 20, 0));

    Label titleSearchLabel = new Label("Enter Movie Title to Search:");
    titleSearchLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

    TextField titleSearchField = new TextField();
    titleSearchField.setPromptText("Enter Movie Title");
    titleSearchField.setStyle("-fx-background-color: #444; -fx-text-fill: white; -fx-prompt-text-fill: gray;");

    Button searchButton = new Button("Search", new ImageView(new Image("file:search_icon.png", 20, 20, true, true)));
    searchButton.setStyle("-fx-background-color: #5bc0de; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");

    Label titleLabel = new Label("New Title:");
    titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
    TextField titleField = new TextField();
    titleField.setStyle("-fx-background-color: #444; -fx-text-fill: white; -fx-prompt-text-fill: gray;");

    Label descriptionLabel = new Label("New Description:");
    descriptionLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
    TextField descriptionField = new TextField();
    descriptionField.setStyle("-fx-background-color: #444; -fx-text-fill: white; -fx-prompt-text-fill: gray;");

    Label releaseYearLabel = new Label("New Release Year:");
    releaseYearLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
    DatePicker yearPicker = new DatePicker();
    yearPicker.setStyle("-fx-background-color: #444; -fx-text-fill: white; -fx-prompt-text-fill: gray;");

    Label ratingLabel = new Label("New Rating:");
    ratingLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
    TextField ratingField = new TextField();
    ratingField.setStyle("-fx-background-color: #444; -fx-text-fill: white; -fx-prompt-text-fill: gray;");

    Button updateButton = new Button("Update", new ImageView(new Image("file:update_icon.png", 20, 20, true, true)));
    updateButton.setStyle("-fx-background-color: #5bc0de; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");

    searchButton.setOnAction(e -> {
        String movieTitle = titleSearchField.getText().trim();
        Movie movie = catalog.get(movieTitle);
        if (movie != null) {
            titleField.setText(movie.getTitle());
            descriptionField.setText(movie.getDescription());
            yearPicker.setValue(java.time.LocalDate.of(movie.getReleaseYear(), 1, 1));
            ratingField.setText(String.valueOf(movie.getRating()));
        } else {
            showAlert("Not Found", "Movie not found!");
        }
    });

    updateButton.setOnAction(e -> {
        String newTitle = titleField.getText().trim();
        String newDescription = descriptionField.getText().trim();
        String ratingText = ratingField.getText().trim();
        Movie movie = catalog.get(titleSearchField.getText().trim());

        if (movie == null) {
            showAlert("Not Found", "Movie not found!");
            return;
        }

        if (newTitle.isEmpty() || newDescription.isEmpty()) {
            showAlert("Invalid Input", "Title and Description cannot be empty.");
            return;
        }

        if (yearPicker.getValue() == null) {
            showAlert("Invalid Input", "Please select a valid release year.");
            return;
        }

        int releaseYear = yearPicker.getValue().getYear();
        int currentYear = java.time.Year.now().getValue();
        if (releaseYear < 1888 || releaseYear > currentYear) {
            showAlert("Invalid Input", "Release year must be between 1888 and " + currentYear + ".");
            return;
        }

        double rating;
        try {
            rating = Double.parseDouble(ratingText);
            if (rating < 0.0 || rating > 10.0) {
                showAlert("Invalid Input", "Rating must be between 0.0 and 10.0.");
                return;
            }
        } catch (NumberFormatException ex) {
            showAlert("Invalid Input", "Rating must be a valid number.");
            return;
        }

        movie.setTitle(newTitle);
        movie.setDescription(newDescription);
        movie.setReleaseYear(releaseYear);
        movie.setRating(rating);

        movieTable.setItems(catalog.getAllMovies());
        movieTable.refresh();

        showAlert("Success", "Movie updated successfully!");
        updateStage.close();
    });

    root.getChildren().addAll(
        headerLabel, titleSearchLabel, titleSearchField, searchButton,
        titleLabel, titleField,
        descriptionLabel, descriptionField,
        releaseYearLabel, yearPicker,
        ratingLabel, ratingField,
        updateButton
    );

    Scene scene = new Scene(root, 400, 600);
    updateStage.setScene(scene);
    updateStage.show();
}


// ************************************************** Delete Movie **********************************************
public void showDeleteMovieWindow(MovieCatalog catalog, TableView<Movie> mainTableView) {
    Stage deleteStage = new Stage();
    deleteStage.setTitle("Delete Movie");

    VBox root = new VBox(15);
    root.setPadding(new Insets(20));
    root.setStyle("-fx-background-color: #222; -fx-font-family: Arial;");

    Label headerLabel = new Label("Delete Movie");
    headerLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
    headerLabel.setPadding(new Insets(0, 0, 20, 0));

    Label titleLabel = new Label("Enter Movie Title to Delete:");
    titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

    TextField titleField = new TextField();
    titleField.setPromptText("Enter Movie Title");
    titleField.setStyle("-fx-background-color: #444; -fx-text-fill: white; -fx-prompt-text-fill: gray;");

    ImageView deleteIcon = new ImageView(new Image("file:delete_icon.png")); 
    deleteIcon.setFitWidth(20);
    deleteIcon.setFitHeight(20);

    Button deleteButton = new Button("Delete", deleteIcon);
    deleteButton.setStyle("-fx-background-color: #d9534f; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");

    deleteButton.setOnAction(e -> {
        String movieTitle = titleField.getText().trim();

        if (movieTitle.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please enter a movie title.");
            alert.showAndWait();
            return;
        }

        Movie movie = catalog.get(movieTitle);
        if (movie != null) {
            catalog.erase(movieTitle);
            mainTableView.setItems(catalog.getAllMovies());
            mainTableView.refresh();

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Movie \"" + movieTitle + "\" was deleted successfully.");
            alert.showAndWait();
            deleteStage.close();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Movie \"" + movieTitle + "\" not found.");
            alert.showAndWait();
        }
    });

    root.getChildren().addAll(headerLabel, titleLabel, titleField, deleteButton);

    Scene scene = new Scene(root, 400, 300);
    deleteStage.setScene(scene);
    deleteStage.show();
}


//*******************************************************************************************************************************
public void showSearchMovieWindow(TableView<Movie> mainTableView, MovieCatalog catalog) {
    Stage searchStage = new Stage();
    searchStage.setTitle("Search Movie");

    VBox root = new VBox(15);
    root.setPadding(new Insets(20));
    root.setStyle("-fx-background-color: #222; -fx-font-family: Arial;");

    Label headerLabel = new Label("Search Movie");
    headerLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
    headerLabel.setPadding(new Insets(0, 0, 20, 0));

    Label titleLabel = new Label("Enter Movie Title (optional):");
    titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
    TextField titleField = new TextField();
    titleField.setPromptText("e.g., Titanic");
    titleField.setStyle("-fx-background-color: #444; -fx-text-fill: white; -fx-prompt-text-fill: gray;");

    Label yearLabel = new Label("Enter Release Year (optional):");
    yearLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
    TextField yearField = new TextField();
    yearField.setPromptText("e.g., 1997");
    yearField.setStyle("-fx-background-color: #444; -fx-text-fill: white; -fx-prompt-text-fill: gray;");

    ImageView searchIcon = new ImageView(new Image("file:search_icon.png"));
    searchIcon.setFitWidth(20);
    searchIcon.setFitHeight(20);

    Button searchButton = new Button("Search", searchIcon);
    searchButton.setStyle("-fx-background-color: #5bc0de; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");

    searchButton.setOnAction(e -> {
        String inputTitle = titleField.getText().trim().toLowerCase();
        String yearText = yearField.getText().trim();

        ObservableList<Movie> result = FXCollections.observableArrayList();

        for (AVLTree tree : catalog.getHashTable()) {
            if (tree != null && !tree.isEmpty()) {
                for (Movie movie : tree.inOrderList()) {
                    boolean matchTitle = inputTitle.isEmpty() || movie.getTitle().toLowerCase().contains(inputTitle);
                    boolean matchYear = yearText.isEmpty() || String.valueOf(movie.getReleaseYear()).equals(yearText);

                    if (matchTitle && matchYear) {
                        result.add(movie);
                    }
                }
            }
        }

        mainTableView.setItems(result);

        if (result.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "No movies found matching the criteria.");
            alert.showAndWait();
        }

        searchStage.close();
    });

    root.getChildren().addAll(headerLabel, titleLabel, titleField, yearLabel, yearField, searchButton);

    Scene scene = new Scene(root, 400, 300);
    searchStage.setScene(scene);
    searchStage.show();
}



}

