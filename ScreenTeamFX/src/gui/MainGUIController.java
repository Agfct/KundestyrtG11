/**
 * 
 */
package gui;

import java.io.IOException;

import javafx.geometry.Rectangle2D;
import javafx.stage.Stage;
import modules.SessionModule;

/**
 * @author Anders Lunde, Magnus Gundersen.
 *Singleton.
 *This class controls which Screen that is shown. It fetches the scene inside every Screen class and puts in onto the primaryStage.
 */
public class MainGUIController {
	
	private static boolean initialized = false;
	private static Rectangle2D primScreenBounds = javafx.stage.Screen.getPrimary().getVisualBounds();
	private static MainGUIController mainGUIController;
	//The primary stage which all screens are shown.
	Stage primaryStage;

	//The different screens
	LoadingScreen loadingScreen;
	MainScreen mainScreen;
	AdvancedScreen advancedScreen;
	OptionsScreen optionsScreen;
	
	//Screen size
	//TODO: make a global screen size?
	
	//Stylesheet
	String stylesheet = "gui/application.css";

	
	/**
	 * 
	 * @param primaryStage
	 * @throws IOException
	 * 
	 * The constructor of the MainGUIController starts by creating an instance of the loadingScreen.
	 * The loadingScreen is the first window the user sees when he starts the application.
	 */
	private MainGUIController(Stage primaryStage) throws IOException{
		this.primaryStage = primaryStage;
		
		loadingScreen = new LoadingScreen();
		primaryStage.setScene(loadingScreen.getScene());
		primaryStage.show();
	}
	
	/**
	 * Returns the running instance of the mainGUIController, 
	 * if an instance does not exists it creates one.
	 * @param primaryStage
	 * @return
	 * @throws IOException
	 */
	public static MainGUIController getInstance() {
		return mainGUIController;
	}
	
	public static MainGUIController initialize(Stage primaryStage){
		if(mainGUIController == null){
			try {
				mainGUIController = new MainGUIController(primaryStage);
			} catch (IOException e) {
				System.out.println("Failed to initialize MainGUIController");
				e.printStackTrace();
			}
		}
		return mainGUIController;
	}
	
	/**
	 * Changes the screen based on SCREENTYPE.
	 * @param newScreen
	 */
	public void changeScreen(SCREENTYPE screenType){
		
		if(screenType == SCREENTYPE.MAINMENU){
			primaryStage.setScene(mainScreen.getScene());
			primaryStage.show();

		}else if(screenType == SCREENTYPE.ADVANCEDSCREEN){
			primaryStage.setScene(advancedScreen.getScene());
			primaryStage.show();
			
		}else if(screenType == SCREENTYPE.OPTIONS){
			primaryStage.setScene(optionsScreen.getScene());
			primaryStage.show();
		}
		
//		centerScreen(); //Centers the screen every time it changes
	}

	/**
	 * This method will start and initialize all program screens.
	 * MainMenu, AdvancedScreen, OptionsMenu.
	 */
	public void finishedLoadingModules() {
		if(initialized == false){
			
			mainScreen = MainScreen.getInstance();
			advancedScreen = AdvancedScreen.getInstance();
			optionsScreen = OptionsScreen.getInstance();
			setStylesheets();
			primaryStage.hide();
			primaryStage.setScene(mainScreen.getScene());
			primaryStage.show();
			centerScreen();
			
			initialized = true;
	
		}
		//If initialized is true nothing happens
		
	}
	
	public void centerScreen(){
        primaryStage.setX((primScreenBounds.getWidth() - primaryStage.getWidth()) / 2);
        primaryStage.setY((primScreenBounds.getHeight() - primaryStage.getHeight()) / 2);
	}
	
	public void changeStylesheet(){
		if(stylesheet.equals("gui/application_black.css")){
			stylesheet = "gui/application.css";
		}else{
			stylesheet = "gui/application_black.css";
		}
		setStylesheets();
	}
	
	private void setStylesheets(){
		mainScreen.getScene().getStylesheets().clear();
		advancedScreen.getScene().getStylesheets().clear();
		optionsScreen.getScene().getStylesheets().clear();
		mainScreen.getScene().getStylesheets().add(stylesheet);
		advancedScreen.getScene().getStylesheets().add(stylesheet);
		optionsScreen.getScene().getStylesheets().add(stylesheet);
	}
	
	// Needs to rebuild the timelines, since updating the title of the media object should update
	// title of TimelineMediaObjects.  
	// TODO: There might be a way to just update the labels?
//	public void updateMediaObjects(){
//		AdvancedScreen.getInstance().getScreenController().fireMediaObjectListChanged();
//		AdvancedScreen.getInstance().getScreenController().rebuildTimelines();
//	}


}
