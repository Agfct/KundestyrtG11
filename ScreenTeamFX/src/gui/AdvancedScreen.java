/**
 * 
 */
package gui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;

/**
 * @author Anders Lunde,  Magnus Gunde
 *
 */
public class AdvancedScreen implements Screen{
		
		//Singleton:
		private static AdvancedScreen advancedScreen;
		
		GridPane rootPane;
		Scene screenScene;
		AdvancedScreenController screenController;
		
		
		//variable to keep track of the media files imported
		ObservableList<String> importedMediaFiles = FXCollections.observableArrayList();
		
		//Autoupdatable listproperty for use on the listview
		protected ListProperty<String> listProperty = new SimpleListProperty<>();
		

		
		

		private AdvancedScreen(){
			
			//Screen (this) configurations
			
			
			//Creating a new controller for use in the fxml
			screenController = new AdvancedScreenController();
			
			
			// The constructor will try to fetch the fxml 
			try {
				FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("AdvancedScreen.fxml"));
				fxmlLoader.setController(screenController);
				fxmlLoader.load();
				rootPane = fxmlLoader.getRoot();
			} catch (IOException e) {
				System.out.println("Failed to load AdvancedScreen FXML");
				e.printStackTrace();
			}
			
//			screenScene = new Scene(rootPane,1200,700); //TODO: Get size from global size ?
			screenScene = new Scene(rootPane,1200,700); //TODO: Get size from global size ?
//			screenScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		}
		
		public static AdvancedScreen getInstance() {
			if(advancedScreen == null){
				advancedScreen = new AdvancedScreen();
			}
			return advancedScreen;
		}
		
		/*
		 * (non-Javadoc)
		 * @see gui.Screen#getScene()
		 */
		@Override
		public Scene getScene() {
			return screenScene;
		}
		
		
		// - change log: magnus 0110 - 
		/*
		 * (non-Javadoc)
		 * @ functionality for the file chooser
		 * The filechooser adds the file to the arraylist, and updates the on-screen listView
		 */
		public void fileChosen(File file){
			importedMediaFiles.add(file.toString());
			listProperty.set(importedMediaFiles);
			
		}
		
		
		/**
		 * 
		 * @author Anders Lunde
		 * The controller for the FXML of the advancedScreen.
		 * The MainScreenController listens to all the input from the objects (buttons, textFields, mouseClicks) in the fxml scene.
		 */
		private class AdvancedScreenController {
			
			@FXML private Button testButton;
//			@FXML private GridPane timelineContainer;
			
			
			
			// A pointer to the fx:id in the fxml
			@FXML private ListView fileListView;
			

			
			
			
			/**
			 * This method is ran when you press a button in the advanced screen top layout (Not inside the timelines).
			 * It assumes that all buttons has in id. if they do not have an id this method gives a null pointer exception.
			 * @param event
			 */
			@FXML protected void buttonPressed(ActionEvent event) {
				System.out.println("Button has been pressed");
				
				if(((Button)event.getSource()).getId().equals("menuBtn") ){
					//If the menu screen button is pressed the MainGUIController changes the screen to be the menu screen
					MainGUIController.getInstance().changeScreen(SCREENTYPE.MAINMENU);
					
				}else if(((Button)event.getSource()).getId().equals("addTimeline")){
					//TODO: Create a new instance of a controller class.
					
				}
				else if(((Button)event.getSource()).getId().equals("importMedia")){
					// If the user chooses the import media button, he will get a windows-file-chooser
					FileChooser fileChooser = new FileChooser();
					fileChooser.setTitle("Open media file");
					File file=fileChooser.showOpenDialog(MainGUIController.getInstance().primaryStage);
					
					//binds the items of the listView to the listProperty. This should probably be done somewhere else
					fileListView.itemsProperty().bind(listProperty);
					fileChosen(file);
			        

				}
			}			

			
		}

}
