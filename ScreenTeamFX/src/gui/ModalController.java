package gui;

import java.io.IOException;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableObjectValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

/**
 * @author Anders
 * TODO: Maby make the modal hold temp values and only apply them after you press an ok button ?
 */
public class ModalController implements FXMLController {

		private FXMLLoader fxmlLoader;
		private MediaObjectController currentMediaObject;

		private GridPane rootGrid;
		
		@FXML Label name;
		@FXML ChoiceBox<MediaObjectType> typeChoiseBox;
		
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
			typeChoiseBox.setItems(FXCollections.observableArrayList(MediaObjectType.values()));
			typeChoiseBox.getSelectionModel().select(currentMediaObject.getType());
			typeChoiseBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<MediaObjectType>() {
		        @Override public void changed(ObservableValue ov, MediaObjectType oldType, MediaObjectType newType) {
		        	currentMediaObject.setType(newType);
		          }    
		      });
//			typeChoiseBox.getSelectionModel().selectedIndexProperty().addListener(new
//					ChangeListener<Number>() {
//				@Override
//				public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
//					name.setText(newValue.toString());
//				}
//			});
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