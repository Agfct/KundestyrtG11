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
 * @author Anders Lunde,  Magnus Gunde
 *
 */
public class AdvancedScreen implements Screen{
		
		//Singleton:
		private static AdvancedScreen advancedScreen;
		
		Pane rootPane;
		Scene screenScene;
		AdvancedScreenController screenController;
		
		private AdvancedScreen(){
			
			//Creating a new controller for use in the fxml
			screenController = new AdvancedScreenController();
			
			// The constructor will try to fetch the fxml 
			try {
				FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("loadScreen.fxml"));
				fxmlLoader.setController(screenController);
				fxmlLoader.load();
				rootPane = fxmlLoader.getRoot();
			} catch (IOException e) {
				System.out.println("Failed to load AdvancedScreen FXML");
				e.printStackTrace();
			}
			
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
		
		/**
		 * 
		 * @author Anders Lunde
		 * The controller for the FXML of the advancedScreen.
		 * The MainScreenController listens to all the input from the objects (buttons, textFields, mouseClicks) in the fxml scene.
		 */
		private class AdvancedScreenController {
			
			@FXML private Button testButton;
			
			/**
			 * This method is ran when you press a button in the main screen
			 * @param event
			 */
			@FXML protected void buttonPressed(ActionEvent event) {
				System.out.println("Button has been pressed");
				
				if(((Button)event.getSource()).getId().equals("testBtn") ){
					//If the test button is pressed
					
				}else if(((Button)event.getSource()).getId().equals("advSrcBtn")){
					//If the advanced screen button is pressed the MainGUIController changes the screen to be the advanced screen
					MainGUIController.getInstance().changeScreen(SCREENTYPE.ADVANCEDSCREEN);
					
				}
			}
			

			
		}

}
