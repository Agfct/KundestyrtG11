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
 * @author Anders Lunde, Magnus Gundersen
 */
public class ModalController implements FXMLController {

	private FXMLLoader fxmlLoader;
	private MediaObjectController currentMediaObjectController;

	//Temp variables for keeping MediaObject information (new start points and such)
	private long temp_start;
	private long temp_startPoint;
	private long temp_duration;
	private long endTime;

	private GridPane rootGrid;

	@FXML Label nameLabel;
	//		@FXML NumberTextField startTimeField;
	@FXML NumberTextField startTimeHours;
	@FXML NumberTextField startTimeMinutes;
	@FXML NumberTextField startTimeSeconds;
	@FXML NumberTextField startTimeMiliSeconds;
	@FXML NumberTextField offsetField;

	@FXML NumberTextField durationField;
	@FXML Label endTimeLabel;
	@FXML Button applyBtn;
	@FXML Button okBtn;

	@FXML Label visualStartTime;
	@FXML Label visualOffset;
	@FXML Label visualDuration;
	@FXML Label visualEndTime;


	public ModalController(MediaObjectController mediaObject){

		currentMediaObjectController = mediaObject;
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

		setInfo();
		initializeMediaObjectToModal();

	}


	/**
	 * This function gets the info from the model, and sets the fields. It is run every time the user does some changes to the values that are displayed. 
	 */
	private void setInfo(){
		nameLabel.setText(currentMediaObjectController.getTimelineMediaObject().getParent().getName());
		setStartTimeField(currentMediaObjectController.getTimelineMediaObject().getStart());
		//			startTimeField.setText(Long.toString(currentMediaObjectController.getTimelineMediaObject().getStart()));
		durationField.setText(Long.toString(currentMediaObjectController.getTimelineMediaObject().getDuration()));
		endTimeLabel.setText(Long.toString(currentMediaObjectController.getTimelineMediaObject().getEnd()));			

		//This info will be updated on apply
		visualStartTime.setText(getTimeAsText(currentMediaObjectController.getTimelineMediaObject().getStart()));
		//			visualOffset.setText(value);
		visualDuration.setText((currentMediaObjectController.getTimelineMediaObject().getDuration()/1000)+"s");
		visualEndTime.setText(getTimeAsText(currentMediaObjectController.getTimelineMediaObject().getEnd()));
		offsetField.setText(Long.toString(currentMediaObjectController.getTimelineMediaObject().getStartPoint()));
	}

	private void setStartTimeField(long time){
		long timeInSeconds = time/1000;
		System.out.println("Time%1000" + time%1000);
		startTimeHours.setText(Long.toString(timeInSeconds/3600));
		startTimeMinutes.setText(Long.toString((timeInSeconds % 3600) / 60));
		startTimeSeconds.setText(Long.toString(timeInSeconds % 60));
		startTimeMiliSeconds.setText(Long.toString(time%1000));
	}

	private String getTimeAsText(long time){
		long timeInSeconds = time/1000;
		long hours = timeInSeconds/3600;
		long minutes = (timeInSeconds % 3600) / 60;
		long seconds = timeInSeconds % 60;
		long milliseconds = time%1000;
		return String.format("%02dh : %02dm : %02ds : %02dms", hours, minutes, seconds,milliseconds);
	}

	//This method is needed because when we set the value of hours, the value of mintues and seconds ect are not set yet.
	//But the lisner is still tirgered
	private long checkValue(NumberTextField field){
		long tempValue = 0;
		try {
			if(field.textProperty().getValue().length() > 0 && field.textProperty().getValue().length() < 4) {
				tempValue = Integer.parseInt(field.textProperty().getValue());
			}
		} catch (Exception e) {
			System.out.println("[Modal] Value Failed returning 0");
		}
		return tempValue;
	}
	/**
	 * Maps the info in the media object to the modal for editing.
	 */
	private void initializeMediaObjectToModal(){
		// Handle TextField text changes.

		startTimeHours.textProperty().addListener((observable, oldValue, newValue) -> {
			System.out.println("[Hours] TextField Text Changed (newValue: " + newValue + ")");
			if(newValue.length() > 0 && newValue.length() < 10) {
				//When one of the fields have changed we add the milliseconds from each together before its sent to the modules:
				// New hours value * 3600000 = hours in milliseconds
				System.out.println("Total temp_Start:" + (checkValue(startTimeMinutes)*60000));
				//					temp_start = (Integer.parseInt(newValue)*3600000) + (Integer.parseInt(startTimeMinutes.textProperty().get())*60000) + (Integer.parseInt(startTimeSeconds.textProperty().get())*1000) + (Integer.parseInt(startTimeMiliSeconds.textProperty().get()));
				if(newValue.length() > 0 && newValue.length() < 3) {
					temp_start = (Integer.parseInt(newValue)*3600000) + (checkValue(startTimeMinutes)*60000) + (checkValue(startTimeSeconds)*1000) + (checkValue(startTimeMiliSeconds));
				}
			}

		});
		startTimeMinutes.textProperty().addListener((observable, oldValue, newValue) -> {
			System.out.println("[Min] TextField Text Changed (newValue: " + newValue + ")");
			if(newValue.length() > 0 && newValue.length() < 3) {
				temp_start = (checkValue(startTimeHours)*3600000) + (Integer.parseInt(newValue)*60000) + (checkValue(startTimeSeconds)*1000) + (checkValue(startTimeMiliSeconds));
			}

		});
		startTimeSeconds.textProperty().addListener((observable, oldValue, newValue) -> {
			System.out.println("[Sec] TextField Text Changed (newValue: " + newValue + ")");
			if(newValue.length() > 0 && newValue.length() < 3) {
				temp_start = (checkValue(startTimeHours)*3600000) + (checkValue(startTimeMinutes)*60000) + (Integer.parseInt(newValue)*1000) + (checkValue(startTimeMiliSeconds));
			}

		});
		startTimeMiliSeconds.textProperty().addListener((observable, oldValue, newValue) -> {
			System.out.println("[Sec] TextField Text Changed (newValue: " + newValue + ")");
			if(newValue.length() > 0 && newValue.length() < 4) {
				temp_start = (checkValue(startTimeHours)*3600000) + (checkValue(startTimeMinutes)*60000) + (checkValue(startTimeSeconds)*1000) + (Integer.parseInt(newValue));
			}

		});

		//			durationField.setText(mediaObject.getStartTime());
		durationField.textProperty().addListener((observable, oldValue, newValue) -> {
			System.out.println("[Dur] TextField Text Changed (newValue: " + newValue + ")");
			//			    if(Integer.parseInt(newValue) > 0 && Integer.parseInt(newValue) < currentMediaObject.getLength()){
			if(newValue.length() > 0 && newValue.length() < 10) {
				temp_duration = Integer.parseInt(newValue);
			}
			//			    }

		});

		offsetField.textProperty().addListener((observable, oldValue, newValue) -> {
			System.out.println("[Offset] Offset Text Changed (newValue: " + newValue + ")");
			//Checks that the string is not bigger than long size, checks that the newValue is not larger or equal to duration, 
			if(newValue.length() > 0 && newValue.length() < 10 ){
				int intNewValue = Integer.parseInt(newValue);
				if( intNewValue < currentMediaObjectController.getTimelineMediaObject().getParent().getLength()) {
					temp_startPoint = intNewValue;
				}
			}
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
			//TODO: a new object is made each time something is changed. See the TODO on line 172 in timelineModel.java This means the function only works one time.
			//This line get the currentSession, and sends a request to change the value of the timelinemediaObject
			String result = AdvancedScreen.getInstance().getScreenController().getCurrentSession().timelineMediaObjectChanged(currentMediaObjectController.getParentController().getParentController().getTimelineModel(),currentMediaObjectController.getTimelineMediaObject(), (int)temp_start, (int)temp_startPoint, (int)temp_duration);
			//TODO: it would be nice to display this result somewhere. Maybe another popup? or some field on the screen that can be used. 		
			System.out.println("timelinemediaObject changed: " + result);
			this.setInfo(); // Update the info from the model. It is possible that the values inputted was rejected. 
		}else if(((Button)event.getSource()).getId().equals("okBtn")){
			String result = AdvancedScreen.getInstance().getScreenController().getCurrentSession().timelineMediaObjectChanged(currentMediaObjectController.getParentController().getParentController().getTimelineModel(),currentMediaObjectController.getTimelineMediaObject(), (int)temp_start, (int)temp_startPoint, (int)temp_duration);
			System.out.println("OK AND APPLY: " + result);
			AdvancedScreen.getInstance().getScreenController().closeModal();

		}else if(((Button)event.getSource()).getId().equals("maxDurBtn")){
			durationField.setText(Long.toString(currentMediaObjectController.getTimelineMediaObject().getParent().getLength()));
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