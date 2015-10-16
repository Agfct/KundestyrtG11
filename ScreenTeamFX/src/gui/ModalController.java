package gui;

import java.io.IOException;
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
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import modules.MediaSourceType;

/**
 * @author Anders
 * TODO: Maby make the modal hold temp values and only apply them after you press an ok button ?
 */
public class ModalController implements FXMLController {

		private FXMLLoader fxmlLoader;
		private MediaObjectController currentMediaObject;
		
		//Temp variables for keeping MediaObject information (new start points and such)
		private long temp_start;
		private long temp_startPoint = 0;
		private long temp_duration;
		private long endTime;

		private GridPane rootGrid;
		
		@FXML Label nameLabel;
		@FXML NumberTextField startTimeField;
		@FXML NumberTextField durationField;
		@FXML Label endTimeLabel;
		@FXML Button applyBtn;
		@FXML Button okBtn;
		
		
		public ModalController(MediaObjectController mediaObject){

			currentMediaObject = mediaObject;
			// The constructor will try to fetch the fxml 
			try {
				fxmlLoader = new FXMLLoader(getClass().getResource("MediaObjectModal.fxml"));
				fxmlLoader.setController(this);
				fxmlLoader.load();
				rootGrid = fxmlLoader.getRoot();
			} catch (IOException e) {
				System.out.println("Failed to load Media Object modal FXML");
				e.printStackTrace();
			}

			initializeMediaObjectToModal();
		}
		
		/**
		 * Maps the info in the media object to the modal for editing.
		 */
		private void initializeMediaObjectToModal(){
			// Handle TextField text changes.
			//TODO: Set int not text
//			startTimeField.setText(mediaObject.getStartTime());
			startTimeField.textProperty().addListener((observable, oldValue, newValue) -> {
			    System.out.println("TextField Text Changed (newValue: " + newValue + ")");
			    temp_start = Integer.parseInt(newValue);
			    
			});
//			durationField.setText(mediaObject.getStartTime());
			durationField.textProperty().addListener((observable, oldValue, newValue) -> {
			    System.out.println("TextField Text Changed (newValue: " + newValue + ")");
//			    if(Integer.parseInt(newValue) > 0 && Integer.parseInt(newValue) < currentMediaObject.getLength()){
				    temp_duration = Integer.parseInt(newValue);
//			    }

			    
			});
			
//			typeChoiseBox.setItems(FXCollections.observableArrayList(MediaSourceType.values()));
//			typeChoiseBox.getSelectionModel().select(currentMediaObject.getType());
//			typeChoiseBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<MediaSourceType>() {
//		        @Override public void changed(ObservableValue ov, MediaSourceType oldType, MediaSourceType newType) {
//		        	currentMediaObject.setType(newType);
//		          }    
//		      });
//			typeChoiseBox.getSelectionModel().selectedIndexProperty().addListener(new
//					ChangeListener<Number>() {
//				@Override
//				public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
//					name.setText(newValue.toString());
//				}
//			});
		}
		
		/*
		 * Button-listener for the modal. 
		 */
		@FXML protected void buttonPressed(ActionEvent event) {
			System.out.println("Modal:" + event.getSource().toString() + "has been pressed");
			
			if(((Button)event.getSource()).getId().equals("applyBtn") ){
				System.out.println("APPLY CHANGES");
				
			}else if(((Button)event.getSource()).getId().equals("okBtn")){
				System.out.println("OK AND APPLY CHANGES");
				AdvancedScreen.getInstance().getScreenController().closeModal();
				
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