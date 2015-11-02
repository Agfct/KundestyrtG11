/**
 * 
 */
package gui;

import java.io.IOException;

import gui.AdvancedScreen.AdvancedScreenController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

/**
 * @author Anders Lunde
 *
 */
public class SeekerPopupController extends BorderPane {
	

	private FXMLLoader fxmlLoader;
	private AdvancedScreenController parentController;
	private BorderPane root = this;
	private final SeekerController seeker;
	
	private @FXML Label timeLabel;

	public SeekerPopupController(SeekerController seeker){
		this.seeker = seeker;
		try {
			fxmlLoader = new FXMLLoader(getClass().getResource("SeekerPopup.fxml"));
			fxmlLoader.setController(this);
			fxmlLoader.setRoot(this);
			fxmlLoader.load();
		} catch (IOException e) {
			System.out.println("Failed to load SeekerPopup FXML");
			e.printStackTrace();
		}
	}
	
	public void updateText(){
		timeLabel.setText(getTimeAsText(seeker.getTempGlobalTime()));
	}
	
	private String getTimeAsText(long time){
		long timeInSeconds = time/1000;
		long hours = timeInSeconds/3600;
		long minutes = (timeInSeconds % 3600) / 60;
		long seconds = timeInSeconds % 60;
		return String.format("%02dh : %02dm : %02ds", hours, minutes, seconds);
	}

}
