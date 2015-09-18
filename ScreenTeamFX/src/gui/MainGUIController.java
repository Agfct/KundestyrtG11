/**
 * 
 */
package gui;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * @author Anders Lunde, Magnus Gundersen
 *
 *This class controls the controllers for each fxml, 
 *and is the access point for everything that is needed from the GUI
 */
public class MainGUIController {
	
	//The primary stage which all screens are shown.
	Stage primaryStage;

	//The different screens
	MainScreen mainScreen;
	
	
	/**
	 * 
	 * @param primaryStage
	 * @throws IOException
	 * 
	 * The constructor of the MainGUIController starts by creating an instance of the mainScreen.
	 * The mainScreen is the first window the user sees when he starts the application.
	 */
	public MainGUIController(Stage primaryStage) throws IOException{
		this.primaryStage = primaryStage;
		
		mainScreen = new MainScreen();
		primaryStage.setScene(mainScreen.getScene());
		primaryStage.show();
	}


}
