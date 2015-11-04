package gui;

import java.io.IOException;
import java.util.ArrayList;

import gui.AdvancedScreen.AdvancedScreenController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableObjectValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import modules.MediaObject;
import modules.MediaSourceType;

/**
 * @author Magnus Gundersen
 */
public class WindowChooserController implements FXMLController {

		private FXMLLoader fxmlLoader;
		private AdvancedScreenController parentController;
		
		//Grid that contains the labels and the listView
		private GridPane rootGrid;
		
		@FXML Label nameLabel;
		@FXML NumberTextField startTimeField;
		@FXML NumberTextField durationField;
		@FXML Label endTimeLabel;
		@FXML Button applyBtn;
		@FXML ListView<String> windowListView;
		
		ArrayList<String> windowsList;
		private MediaObject mediaObject;
		
		public WindowChooserController(AdvancedScreenController parentController){
			mediaObject = null;
			this.parentController = parentController;
			try {
				fxmlLoader = new FXMLLoader(getClass().getResource("WindowChooser.fxml"));
				fxmlLoader.setController(this);
				fxmlLoader.load();
				rootGrid = fxmlLoader.getRoot();
			} catch (IOException e) {
				System.out.println("Failed to load windowChooser FXML");
				e.printStackTrace();
			}
			initList();

		}
		
		public WindowChooserController(AdvancedScreenController parentController, MediaObject mo){
			mediaObject = mo;
			this.parentController = parentController;
			try {
				fxmlLoader = new FXMLLoader(getClass().getResource("WindowChooser.fxml"));
				fxmlLoader.setController(this);
				fxmlLoader.load();
				rootGrid = fxmlLoader.getRoot();
			} catch (IOException e) {
				System.out.println("Failed to load windowChooser FXML");
				e.printStackTrace();
			}
			initList();

		}
		
		/**
		 * This method populates the listview
		 */
		private void initList(){
			windowsList= parentController.getWindows();
			windowListView.getItems().addAll(windowsList);
			windowListView.setMaxWidth(200); // to center the listview

		}

		
		/*
		 * Button-listener for the  
		 */
		@FXML protected void buttonPressed(ActionEvent event) {
			System.out.println("Import windows: " + event.getSource().toString() + "has been pressed");
			
			if(((Button)event.getSource()).getId().equals("importWindowChooser") ){
				System.out.println("INSIDE BUTTONEVENT");
				String selectedWindow = (String)windowListView.getSelectionModel().getSelectedItem();
				System.out.println(selectedWindow);
				if(selectedWindow!=null){
					if(mediaObject == null){
						String result = parentController.getCurrentSession().createNewMediaObject(MediaSourceType.WINDOW, selectedWindow);
						System.out.println("[WindowChooserController] "+result);
					}
					else{
						parentController.getCurrentSession().changeMediaObject(mediaObject, selectedWindow);
					}
				}
				parentController.closeWindowChooser();
			}
			
		}

		/* (non-Javadoc)
		 * @see gui.FXMLController#getFXMLLoader()
		 */
		@Override
		public FXMLLoader getFXMLLoader() {
			return fxmlLoader;
		}
		
		public GridPane getRoot(){
			return rootGrid;
		}
		
	}