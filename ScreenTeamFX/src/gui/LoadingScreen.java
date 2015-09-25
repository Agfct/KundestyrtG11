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
public class LoadingScreen implements Screen {

	Pane rootPane;
	Scene screenScene;
	LoadScreenController screenController;
	
	public LoadingScreen(){
		
		//Creating a new controller for use in the fxml
		screenController = new LoadScreenController();
		
		// The constructor will try to fetch the fxml 
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("loadScreen.fxml"));
			fxmlLoader.setController(screenController);
			fxmlLoader.load();
			rootPane = fxmlLoader.getRoot();
		} catch (IOException e) {
			System.out.println("Failed to load LoadScreen FXML");
			e.printStackTrace();
		}
		
		screenScene = new Scene(rootPane,600,400); //TODO: Get size from global size ?
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
	private class LoadScreenController {
		
	}

}
