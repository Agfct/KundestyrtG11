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
 *Singleton
 *This class controls the controllers for each fxml, 
 *and is the access point for everything that is needed from the GUI
 */
public class MainGUIController {
	
	private static MainGUIController mainGUIController;
	//The primary stage which all screens are shown.
	Stage primaryStage;

	//The different screens
	MainScreen mainScreen;
	MainScreen AdvancedScreen;
	
	
	/**
	 * 
	 * @param primaryStage
	 * @throws IOException
	 * 
	 * The constructor of the MainGUIController starts by creating an instance of the mainScreen.
	 * The mainScreen is the first window the user sees when he starts the application.
	 */
	private MainGUIController(Stage primaryStage) throws IOException{
		this.primaryStage = primaryStage;
		
		mainScreen = new MainScreen();
		primaryStage.setScene(mainScreen.getScene());
		primaryStage.show();
	}
	
	/**
	 * Returns the running instance of the mainGUIController, 
	 * if an instance does not exists it creates one.
	 * @param primaryStage
	 * @return
	 * @throws IOException
	 */
	public static MainGUIController getInstance(Stage primaryStage) throws IOException{
		if(mainGUIController == null){
			mainGUIController = new MainGUIController(primaryStage);
		}
		return mainGUIController;
	}
	
	/**
	 * Changes the screen based on class.
	 * @param newScreen
	 */
	public void changeScreen(Screen newScreen){
		//TODO: What shoud this method have as argument ? IDs ? Class ? screen?
		primaryStage.setScene(mainScreen.getScene());
		primaryStage.show();
	}


}
