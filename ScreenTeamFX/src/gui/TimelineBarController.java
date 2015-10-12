/**
 * 
 */
package gui;

import java.io.IOException;
import java.util.ArrayList;

import gui.AdvancedScreen.AdvancedScreenController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

/**
 * @author Anders Lunde
 *
 */
public class TimelineBarController extends AnchorPane {
	
	private FXMLLoader fxmlLoader;
	private AdvancedScreenController parentController;
	
	@FXML Image barImage;
	
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
	
	initializeTimeBar();
	
	}
	
	/**
	 * Starts the numbering on the bar with global time
	 */
	public void initializeTimeBar(){
		int currentLength = (int) this.getPrefWidth();
		System.out.println("[TimelineBarController: length" + currentLength);
		int large = currentLength /10;
		System.out.println("[TimelineBarController: large" + large);
		int smal = currentLength / 100;
		System.out.println("[TimelineBarController: large" + smal);
		PixelReader pixelReader = barImage.getPixelReader();
		
	}

}
