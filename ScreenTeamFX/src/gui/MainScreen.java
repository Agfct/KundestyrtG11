package gui;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

/**
 * @author Anders Lunde, Magnus Gunde
 * Singleton  class
 * MainScreen is the handler for the main screen in the software.
 * Main screen is the second screen (loading screen is first) the user sees when he/she starts the software 
 * and its where you navigate yourself to the other screens.
 */
public class MainScreen implements Screen {
	
	//Singleton:
	private static MainScreen mainScreen;
	

	Scene screenScene;
	MainScreenController screenController;
	
	private MainScreen(){
		
		//Creating a new controller for use in the fxml
		screenController = new MainScreenController();
		
		//Setting the root of the controller to the scene
		screenScene = new Scene(screenController.getFXMLLoader().getRoot(),1200,700); //TODO: Make a global size instead of 1200,700
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
	
	public MainScreenController getScreenController(){
		return screenController;
	}
	
	/**
	 * 
	 * @author Anders Lunde
	 * The controller for the FXML of the mainScreen.
	 * The MainScreenController listens to all the input from the objects (buttons, textFields, mouseClicks) in the fxml scene.
	 */
	public class MainScreenController implements FXMLController {
		
		private FXMLLoader fxmlLoader;
		private AnchorPane rootPane;
		
//		@FXML private Button testButton;
		
		public MainScreenController(){
			
			// The constructor will try to fetch the fxml 
			try {
				fxmlLoader = new FXMLLoader(getClass().getResource("MainScreen.fxml"));
				fxmlLoader.setController(this);
				fxmlLoader.load();
				rootPane = fxmlLoader.getRoot();
			} catch (IOException e) {
				System.out.println("Failed to load MainScreen FXML");
				e.printStackTrace();
			}
		}
		
		/**
		 * This method is ran when you press a button in the main screen
		 * @param event
		 * @throws InterruptedException 
		 */
		@FXML protected void buttonPressed(ActionEvent event) throws InterruptedException {
			System.out.println("Button has been pressed");
			

			if(((Button)event.getSource()).getId().equals("advScrBtn")){
				System.out.println("Pressing advanced screen btn");
				//If the advanced screen button is pressed the MainGUIController changes the screen to be the advanced screen
				MainGUIController.getInstance().changeScreen(SCREENTYPE.ADVANCEDSCREEN);
				
			}else if(((Button)event.getSource()).getId().equals("optionsBtn")){
				System.out.println("Pressing options screen btn");
				MainGUIController.getInstance().changeScreen(SCREENTYPE.OPTIONS);
				
			}else if(((Button)event.getSource()).getId().equals("exitBtn")){
				System.exit(0);
			}
//			else if(((Button)event.getSource()).getId().equals("testBtn") ){
//			VLCController vlcc = MainModuleController.getInstance().getVLCController();
//			String[] options = {"--avcodec-hw=none", "--vout=directdraw", "--no-overlay"};
//			vlcc.updateOptions(options);
//			}
			
		}

		/* (non-Javadoc)
		 * @see gui.FXMLController#getFXMLLoader()
		 */
		@Override
		public FXMLLoader getFXMLLoader() {
			return fxmlLoader;
		}
		
		public AnchorPane getRoot(){
			return rootPane;
		}
		

		
	}// Main screen controller ends

}
