/**
 * 
 */
package gui;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

/**
 * @author Anders Lunde, Magnus Gunde
 * MainScreen is the handler for the main screen in the software.
 * Main screen is the first screen the user sees when he/she starts the software.
 */
public class MainScreen implements Screen {

	GridPane rootPane;
	Scene screenScene;
	MainScreenController screenController;
	
	public MainScreen(){
		
		//Creating a new controller for use in the fxml
		screenController = new MainScreenController();
		
		// The constructor will try to fetch the fxml 
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("mainScreen.fxml"));
			fxmlLoader.setController(screenController);
			fxmlLoader.load();
			rootPane = fxmlLoader.getRoot();
		} catch (IOException e) {
			System.out.println("Failed to load MainScreen FXML");
			e.printStackTrace();
		}
		
		screenScene = new Scene(rootPane,1200,700);
//		screenScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
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
		
		@FXML protected void testButtonPressed(ActionEvent event) {
			System.out.println("Button has been pressed");
		}
		
	}

}
