package gui;

import java.io.IOException;

import gui.AdvancedScreen.AdvancedScreenController;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

/**
 * 
 * @author Anders Lunde
 * The TimelineLineController is the controller of the line you see at the right side, 
 * containing all the media objects.
 */
public class TimelineLineController implements FXMLController{
	
	
	private FXMLLoader fxmlLoader;
	private TimelineController parentController;
	private Pane rootPane;
	
	/**
	 * 
	 * @param parentController
	 */
	public TimelineLineController(TimelineController parentController){
		
		//Fetches the parent controller. In this case it is the controller in the advancedScreen class.'
		this.parentController = parentController;
		
		// The constructor will try to fetch the fxml 
		try {
			fxmlLoader = new FXMLLoader(getClass().getResource("TimelineLine.fxml"));
			fxmlLoader.setController(this);
			fxmlLoader.load();
			rootPane = fxmlLoader.getRoot();
		} catch (IOException e) {
			System.out.println("Failed to load TimelineLine FXML");
			e.printStackTrace();
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
