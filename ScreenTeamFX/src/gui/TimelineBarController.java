/**
 * 
 */
package gui;

import java.io.IOException;
import java.util.ArrayList;

import gui.AdvancedScreen.AdvancedScreenController;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

/**
 * @author Anders Lunde
 *
 */
public class TimelineBarController extends AnchorPane {
	
	private FXMLLoader fxmlLoader;
	private AdvancedScreenController parentController;
	
	
	public TimelineBarController(AdvancedScreenController parentController){
	//Fetches the parent controller. In this case it is the controller in the advancedScreen class.'
	this.parentController = parentController;
	
	this.setStyle("-fx-background-color:RED");
	
	// The constructor will try to fetch the fxml 
	try {
		fxmlLoader = new FXMLLoader(getClass().getResource("TimelineBar.fxml"));
		fxmlLoader.setController(this);
		fxmlLoader.setRoot(this);
		fxmlLoader.load();
	} catch (IOException e) {
		System.out.println("Failed to load TimelineBar FXML");
		e.printStackTrace();
	}
	
	}

}
