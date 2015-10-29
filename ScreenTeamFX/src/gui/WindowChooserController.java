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
		@FXML ListView windowListView;
		
		ArrayList<String> windowsList;
		
		public WindowChooserController(AdvancedScreenController parentController){
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
		
		

		private void initList(){
			windowsList= parentController.getWindows();
			windowListView.getItems().addAll(windowsList);
//			windowListView.
			

		}

		
		/*
		 * Button-listener for the  
		 */
		@FXML protected void buttonPressed(ActionEvent event) {
			System.out.println("Import windows: " + event.getSource().toString() + "has been pressed");
			
			if(((Button)event.getSource()).getId().equals("importWindow") ){
				String selectedWindow = (String)windowListView.getSelectionModel().getSelectedItem();
				if(selectedWindow!=null){
					String result = parentController.getCurrentSession().createNewMediaObject(MediaSourceType.WINDOW, selectedWindow);
					System.out.println(result);
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