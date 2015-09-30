/**
 * This software is made by (Screen Team):
 * Anders Lunde, Mangus Gundersen, Kristian Midtgård, 
 * Baptiste Masselin, Ole Steinar Lillestøl Skrede and Eirik Zimmer Wold.
 */
package application;
	
import gui.MainGUIController;
import javafx.application.Application;
import javafx.stage.Stage;
import modules.MainModuleController;

/**
 * 
 * @author Anders Lunde, Magnus Gundersen
 * The Main class is simply the starting point of the software and initializes the
 * two main controllers; mainGUIController and mainModuleController.
 */

public class Main extends Application {
	MainGUIController mainGUIController;
	MainModuleController mainModuleController;
	
	/**
	 * Start initialises the main GUI controller for the GUI, 
	 * and the main java controller "MainJavaController" for the java parts of the application. 
	 */
	@Override
	public void start(Stage primaryStage) {
		try {
			
			//Initializes the GUI part of the software, initializing the mainScene onto the stage and adding the fxml.
			mainGUIController = MainGUIController.initialize(primaryStage);
			
			//Initializes the Modules and base models, running the default setup phase.
			mainModuleController = MainModuleController.getInstance();
			

			

		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}

}
