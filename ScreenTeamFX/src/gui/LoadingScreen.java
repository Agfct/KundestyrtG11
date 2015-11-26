/**
 * 
 */
package gui;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

/**
 * @author Anders Lunde, Magnus Gunde
 * LoadingScreen is to have a screen where you can load resources and other components.
 * Currently there is no threads involved and the software only loads the main scene's that are used, and no data.
 * LoadingScreen is the first screen the user sees when he/she starts the software.
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
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("LoadScreen.fxml"));
			fxmlLoader.setController(screenController);
			fxmlLoader.load();
			rootPane = fxmlLoader.getRoot();
		} catch (IOException e) {
			System.out.println("Failed to load LoadScreen FXML");
			e.printStackTrace();
		}
		
		screenScene = new Scene(rootPane,600,400); //TODO: Make a global size instead of 600,400
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
	 * @author Anders Lunde
	 * The controller for the FXML of the loadScreen
	 * Currently no need for this.
	 */
	private class LoadScreenController {
		
	}

}
