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
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import modules.MainModuleController;
import vlc.VLCController;

/**
 * @author Anders
 * Singleton  class
 */
public class OptionsScreen implements Screen {

	//Singleton:
	private static OptionsScreen optionScreen;
	

	Scene screenScene;
	OptionsScreenController screenController;
	
	private OptionsScreen(){
		
		//Creating a new controller for use in the fxml
		screenController = new OptionsScreenController();
		
		//Setting the root of the controller to the scene
		screenScene = new Scene(screenController.getFXMLLoader().getRoot(),1200,700); //TODO: Get size from global size ?
	}
	
	public static OptionsScreen getInstance() {
		if(optionScreen == null){
			optionScreen = new OptionsScreen();
		}
		return optionScreen;
	}
	
	/* (non-Javadoc)
	 * @see gui.Screen#getScene()
	 */
	@Override
	public Scene getScene() {
		return screenScene;
	}
	
	/**
	 * @author Anders Lunde
	 * The controller for the FXML of the optionsScrenn.
	 * The OptionsScreenController listens to all the input from the objects 
	 * (buttons, textFields, mouseClicks) in the OptionsScreen.fxml scene.
	 */
	private class OptionsScreenController implements FXMLController {
		
		private FXMLLoader fxmlLoader;
		private AnchorPane rootPane;
		private AnchorPane creditsRootPane;
		private boolean credits = false;
		private VLCController vlc_controller;
		
		@FXML private GridPane rootGrid;
		@FXML private Label vlc_version;
		@FXML private Label java_version;
		
		public OptionsScreenController(){
			
			// The constructor will try to fetch the fxml 
			try {
				fxmlLoader = new FXMLLoader(getClass().getResource("OptionsScreen.fxml"));
				fxmlLoader.setController(this);
				fxmlLoader.load();
				rootPane = fxmlLoader.getRoot();
			} catch (IOException e) {
				System.out.println("Failed to load OptionsScreen FXML");
				e.printStackTrace();
			}
			
			initializeOptions();
			
		}
		
		/**
		 * Gets correct version values from VLC and labels them
		 */
		private void initializeOptions(){
			vlc_controller = MainModuleController.getInstance().getVLCController();
			java_version.setText(vlc_controller.getJavaVersion());
			vlc_version.setText(vlc_controller.getVLCVersion());
		}
		
		/**
		 * This method is ran when you press a button in the Options screen
		 * @param event
		 */
		@FXML protected void buttonPressed(ActionEvent event) {
			System.out.println("Button has been pressed");
			
			if(((Button)event.getSource()).getId().equals("mainMenuBtn") ){
				System.out.println("Pressing mainMenu screen btn");
				//If the mainMenu screen button is pressed the MainGUIController changes the screen to be the mainMenu screen
				MainGUIController.getInstance().changeScreen(SCREENTYPE.MAINMENU);
				
			}
		}

		/* (non-Javadoc)
		 * @see gui.FXMLController#getFXMLLoader()
		 */
		@Override
		public FXMLLoader getFXMLLoader() {
			return fxmlLoader;
		}
		

		
	}

}
