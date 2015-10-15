package gui;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

/**
 * @author Anders
 */
public class ModalController implements FXMLController {

		private FXMLLoader fxmlLoader;
		private MediaObjectController currentMediaObject;

		private GridPane rootGrid;
		
		@FXML Label name;
		
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
			name.setText(currentMediaObject.getType().toString());
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