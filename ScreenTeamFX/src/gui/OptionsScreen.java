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
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import modules.MainModuleController;
import vlc.VLCController;

/**
 * @author Anders Lunde, Magnus Gundersen
 * Singleton  class
 * The Options screen is a screen where you can modify VLC options, make the background black and see the credits.
 */
public class OptionsScreen implements Screen {

	//Singleton:
	private static OptionsScreen optionScreen;
	private boolean blackBackground  = false;
	

	Scene screenScene;
	OptionsScreenController screenController;
	
	private OptionsScreen(){
		
		//Creating a new controller for use in the fxml
		screenController = new OptionsScreenController();
		
		//Setting the root of the controller to the scene
		screenScene = new Scene(screenController.getFXMLLoader().getRoot(),1200,700); //TODO: Make a global size instead of 1200,700
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
		private VLCController vlc_controller;
		private String temp_vlcCommands = "";
		
		private String[] vlcConfig ={};
		
		@FXML private GridPane rootGrid;
		@FXML private Label vlc_version;
		@FXML private Label java_version;
//		@FXML private TextField vlc_commandField;
		@FXML private CheckBox hwDecoding;
		@FXML private CheckBox hwOverlay;
		@FXML private CheckBox yuvToRGB;
		@FXML private ComboBox<String> vOutputComboBox;
		
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
			
//			vlc_commandField.textProperty().addListener((observable, oldValue, newValue) -> {
//			    System.out.println("[Options]TextField Text Changed (newValue: " + newValue + ")");
//			    if(newValue.length() > 0 && newValue.length() < 10) {
//			    	temp_vlcCommands = newValue;
//			    }
//			    
//			});
			setVLCConfigAlternatives();
			paintVLCConfigValues();
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
			else if(((Button)event.getSource()).getId().equals("applyVLCconfig")){
				
				//generating the new configArray
				String[] newVlcConfig;
				if(hwOverlay.isSelected()){
					if(vOutputComboBox.getSelectionModel().getSelectedItem().equals("DirectX (DirectDraw)")){
						newVlcConfig=new String[]{"--vout=directdraw"};
					}
					else{
						newVlcConfig=new String[]{};
					}
				}
				else{
					if(vOutputComboBox.getSelectionModel().getSelectedItem().equals("DirectX (DirectDraw)")){
						newVlcConfig=new String[]{"--vout=directdraw","--no-overlay"};
					}
					else{
						newVlcConfig=new String[]{"--no-overlay"};
					}
				}
				
				
				
				AdvancedScreen.getInstance().getScreenController().getCurrentSession().setVLCConfiguration(newVlcConfig);
				AdvancedScreen.getInstance().getScreenController().getCurrentSession().updateMediaPlayers();
				paintVLCConfigValues();
			}else if(((Button)event.getSource()).getId().equals("changeBackground")){
				//If the background is default (not black)
				if(!blackBackground){
					blackBackground = true;
					rootPane.getStyleClass().removeAll("background-main");
					rootPane.getStyleClass().add("background-main-black");
					MainScreen.getInstance().getScreenController().getRoot().getStyleClass().removeAll("background-main");
					MainScreen.getInstance().getScreenController().getRoot().getStyleClass().add("background-main-black");
					AdvancedScreen.getInstance().getScreenController().getRoot().getStyleClass().removeAll("background-main");
					AdvancedScreen.getInstance().getScreenController().getRoot().getStyleClass().add("background-main-black");
					
				}else{
					blackBackground = false;
					rootPane.getStyleClass().removeAll("background-main-black");
					rootPane.getStyleClass().add("background-main");
					MainScreen.getInstance().getScreenController().getRoot().getStyleClass().removeAll("background-main-black");
					MainScreen.getInstance().getScreenController().getRoot().getStyleClass().add("background-main");
					AdvancedScreen.getInstance().getScreenController().getRoot().getStyleClass().removeAll("background-main-black");
					AdvancedScreen.getInstance().getScreenController().getRoot().getStyleClass().add("background-main");
				}
				System.out.println(rootPane.getStyleClass());
			}
		}
		
		public void paintVLCConfigValues(){
			vlcConfig = AdvancedScreen.getInstance().getScreenController().getCurrentSession().getVLCConfiguration();
			System.out.println("VLCCONFIG: " + vlcConfig);
//			if(vOutputComboBox.getSelectionModel().getSelectedItem().equals("--vout=directdraw")){
//				System.out.println("Selected: " + "--vout=directdraw" );
//			}
			for(String s: vlcConfig){
				System.out.println("SELECTED config: " +s);
				if(s.equals("--vout=directdraw")){
					vOutputComboBox.getSelectionModel().select("DirectX (DirectDraw)");
					
				}
				else if(s.equals("--no-overlay")){
					hwOverlay.setSelected(false);
					
				}
			}				
		}
		
		public void setVLCConfigAlternatives(){
			vOutputComboBox.getItems().addAll(
					"Auto",
					"DirectX (DirectDraw)"
					);
			vOutputComboBox.getSelectionModel().select("Auto");
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