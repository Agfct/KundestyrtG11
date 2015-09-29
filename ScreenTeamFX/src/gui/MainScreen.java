package gui;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import modules.MainModuleController;

/**
 * @author Anders Lunde, Magnus Gunde
 * Singelton  class
 * MainScreen is the handler for the main screen in the software.
 * Main screen is the first screen the user sees when he/she starts the software.
 */
public class MainScreen implements Screen {
	
	//Singleton:
	private static MainScreen mainScreen;
	
	GridPane rootPane;
	Scene screenScene;
	MainScreenController screenController;
	
	private MainScreen(){
		
		//Creating a new controller for use in the fxml
		screenController = new MainScreenController();
		
		// The constructor will try to fetch the fxml 
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainScreen.fxml"));
			fxmlLoader.setController(screenController);
			fxmlLoader.load();
			rootPane = fxmlLoader.getRoot();
		} catch (IOException e) {
			System.out.println("Failed to load MainScreen FXML");
			e.printStackTrace();
		}
		
		screenScene = new Scene(rootPane,1200,700); //TODO: Get size from global size ?
//		screenScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
	}
	
	public static MainScreen getInstance() {
		if(mainScreen == null){
			mainScreen = new MainScreen();
		}
		return mainScreen;
	}
	
	/*
	 * (non-Javadoc)
	 * @see gui.Screen#getScene()
	 */
	@Override
	public Scene getScene() {
		return screenScene;
	}
	
	/**
	 * 
	 * @author Anders Lunde
	 * The controller for the FXML of the mainScreen.
	 * The MainScreenController listens to all the input from the objects (buttons, textFields, mouseClicks) in the fxml scene.
	 */
	private class MainScreenController {
		
		@FXML private Button testButton;
		
		/**
		 * This method is ran when you press a button in the main screen
		 * @param event
		 */
		@FXML protected void buttonPressed(ActionEvent event) {
			System.out.println("Button has been pressed");
			
			if(((Button)event.getSource()).getId().equals("testBtn") ){
				//If the test button is pressed
				
			}else if(((Button)event.getSource()).getId().equals("advScrBtn")){
				System.out.println("Pressing advanced screen btn");
				//If the advanced screen button is pressed the MainGUIController changes the screen to be the advanced screen
				MainGUIController.getInstance().changeScreen(SCREENTYPE.ADVANCEDSCREEN);
				
			}
		}
		

		
	}

}
