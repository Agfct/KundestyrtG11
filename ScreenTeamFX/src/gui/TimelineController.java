package gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Optional;

import org.controlsfx.control.CheckComboBox;

import gui.AdvancedScreen.AdvancedScreenController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;
import modules.TimelineModel;


/**
 * 
 * @author Anders Lunde
 * The TimelineController is the controller of everything you see in a single timeline.
 * It contains fxml for the timeline info (left side) and the timeline with videos (right side)
 */
public class TimelineController implements FXMLController {
	
	private FXMLLoader fxmlLoader;
	private AdvancedScreenController parentController;
	private TimelineLineController childController;
	private GridPane rootPane;
	private Alert alert;
	
	@FXML GridPane timelineInfo;
	@FXML GridPane timelineContainer;
	@FXML AnchorPane timelineLineContainer;
	@FXML Button removeTimelineBtn;
	@FXML ComboBox<String> displaysComboBox;
	@FXML Label listOfScreens;
	@FXML TextField nameOfTimeLineField;
	
	@FXML Button moveUp;
	@FXML Button moveDown;
	
	@FXML Label titleLabel;
	@FXML Label muteLabel;
	@FXML Button muteVideo;
	@FXML Button muteSound;
	
	boolean timelineMuted;
	boolean timelineHidden;
	
	//The timelinemodel that corresponds to this controller
	TimelineModel timelineModel;
	
	ArrayList<Integer> assignedDisplays;
	protected boolean updatingDisplayList=false;  //variable to prevent the listener from firing when the model is updated
	
	String nameOfTimeLine = "";
	
	/**
	 *
	 *This construction initiates the TimelineInfo.fxml and controls the actions from it.
	 *It also creates an instance of the TimeLineLineController which contains the TimelineLine.fxml.
	 */
	public TimelineController (TimelineModel timelineModel){
		this.timelineModel=timelineModel;
			//TODO: Add clipping on anchorPane ??
//		timelineLineContainer.setClip(value);
		
		//Fetches the parent controller. In this case it is the controller in the advancedScreen class.'
		parentController = AdvancedScreen.getInstance().getScreenController();
		updateValuesFromModel();
		
		// The constructor will try to fetch the fxml 
		try {
			fxmlLoader = new FXMLLoader(getClass().getResource("TimelineInfo.fxml"));
			fxmlLoader.setController(this);
			fxmlLoader.load();
			rootPane = fxmlLoader.getRoot();
		} catch (IOException e) {
			System.out.println("Failed to load TimelineController FXML");
			e.printStackTrace();
		}

		//Creates a clipping mask to hide timeline outside of bounds
		createClip();
		//Creates the actual timeline line (right side of the timeline) using this controller as parent controller
		childController = new TimelineLineController(this);
		
		//TODO: Sets the timeline to the current scrollbar position
		getTimelineLineController().moveTimeline(parentController.getScrollBarPosition());
		
		initializeTimelineInfo();
		initializeAlerts();
		updateMutationValues();
		
		
		
	}
	
	
	/**
	 *Initializes the alert box that is displayed when the user right clicks the mediaObject and presses remove.
	 */
	private void initializeAlerts(){
		alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirmation Dialog");
		alert.setHeaderText("Delete Timeline");
		alert.setContentText("Do you really want to delete this Timeline?");
	}
	
	/**
	 * Creates a clip that decides the viewport of this Panes children
	 * The timeline that is outside of this clip will be hidden for the user.
	 */
	private void createClip(){
		Rectangle clipSize = new Rectangle(1000,150);
		clipSize.setLayoutX(0);
		clipSize.setLayoutY(0);
		timelineLineContainer.setClip(clipSize);
	}
	
	/**
	 * Initialises the info from the model on the left hand side of the timeline (timelineInfo)
	 */
	private void initializeTimelineInfo(){
		
		//Populates the list with the available screens
		ObservableList<String> obsList = FXCollections.observableArrayList();
		 for (int i=0;i<parentController.getAvailableDisplays().size();i++) {
			 obsList.add(parentController.getAvailableDisplays().get(i).toString());
		     
		 }
		 displaysComboBox.getItems().add("None");
		 displaysComboBox.getItems().addAll(obsList);
		 //selects none as no display is selected
		 displaysComboBox.getSelectionModel().select(0);
		 //adds the listener
		 initDisplayChooserListener();
		 
		 
		 //TextField for editing the name of a timeline
		 nameOfTimeLineField.setText(nameOfTimeLine);
		 nameOfTimeLineField.setFocusTraversable(false);
		 addListenerToNameOfTimelineTextField();
		
	}

	
	
	/**
	 * This method is ran when you press a button in the TimelineInfo screen (Left side of timeline).
	 * It assumes that all buttons has in id. if they do not have an id this method gives a null pointer exception.
	 * @param event
	 */
	@FXML protected void buttonPressed(ActionEvent event) {
		System.out.println("TIMELINECONTROLLER:" + event.getSource().toString() + "has been pressed");
		
		if(((Button)event.getSource()).getId().equals("removeTimelineBtn") ){
			//Removes the timeline from the AdvanceScreen Children list
			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK){
			    // ... user chose OK
				parentController.removeTimeline(this);
			} else {
			    // ... user chose CANCEL or closed the dialog
			}
			
			
		}else if(((Button)event.getSource()).getId().equals("muteVideo") ){
			parentController.getCurrentSession().hideTimeline(timelineModel);
			
		}else if(((Button)event.getSource()).getId().equals("muteSound") ){
			parentController.getCurrentSession().muteTimeline(timelineModel);
			
		}
		else if(((Button)event.getSource()).getId().equals("moveUp") ){
			parentController.moveTimeline("up",timelineModel);
		}
		else if(((Button)event.getSource()).getId().equals("moveDown") ){
			parentController.moveTimeline("down",timelineModel);
		}
		
		
	}
	/* (non-Javadoc)
	 * @see gui.FXMLController#getFXMLLoader()
	 */
	@Override
	public FXMLLoader getFXMLLoader() {
		return fxmlLoader;
	}
	
	public TimelineLineController getTimelineLineController(){
		return childController;
	}
	
	public GridPane getRoot(){
		return rootPane;
	}
	
	public GridPane getTimelineInfo(){
		return timelineInfo;
	}
	
	public TimelineModel getTimelineModel(){
		return timelineModel;
	}
	/**
	 * This method is run by advancedScreen when fireTimelineChanges is ran 
	 */
	public void modelChanged() {
		childController.repaint();// repaints the timelineline
		updateValuesFromModel(); //Gets the updated values from the model
		updateDisplayList(); // Use the updated values to update the list of the selected screens.
		updateMutationValues();
		
	}
	
	public void updateMutationValues(){
		//Mutes all the sound on this timeline
		if(!timelineMuted){
			muteSound.getStyleClass().removeAll(Collections.singleton("icon-soundmuteon"));
			muteSound.getStyleClass().add("icon-soundmuteoff");
			//UnMute
		}
		if(timelineMuted){
			muteSound.getStyleClass().removeAll(Collections.singleton("icon-soundmuteoff"));
			muteSound.getStyleClass().add("icon-soundmuteon");
			//Mutes
		}
		if(!timelineHidden){
			muteVideo.getStyleClass().removeAll(Collections.singleton("icon-videomuteon"));
			muteVideo.getStyleClass().add("icon-videomuteoff");
			//UnMute
		}
		if(timelineHidden){
			muteVideo.getStyleClass().removeAll(Collections.singleton("icon-videomuteoff"));
			muteVideo.getStyleClass().add("icon-videomuteon");
			//Mutes
		}
		
	}
	/**
	 * This method adds the available displays to the list, and selects the one that is displayed to this timeline
	 */
	private void updateDisplayList() {
		updatingDisplayList=true; // set this variable to true in order for the listener not to fire
		if(!assignedDisplays.isEmpty()){//make sure a display is actually assigned	
			displaysComboBox.getSelectionModel().select(assignedDisplays.get(0)+1);
		}
		else{
			displaysComboBox.getSelectionModel().select(0);
		}
		updatingDisplayList=false;
	}
	
	/**
	 * This method starts the listener for the displayChooserCombobox
	 */
	private void initDisplayChooserListener(){
		displaysComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>(){
			@Override
			public void changed(ObservableValue<? extends String> selected, String oldDisplay, String newDisplay) {
				if(updatingDisplayList==false){
					if(newDisplay!=null){
						if(newDisplay.equals("None")){
							parentController.removeAssignRequest(timelineModel);		
						}
						else{
							parentController.assignRequest(newDisplay, timelineModel);	

						}
					}
				}
			}
		 });
		
	}
	/**
	 * Update the saved values from the model
	 * 
	 */
	private void updateValuesFromModel() {
		assignedDisplays=timelineModel.getAssignedDisplays(); //gets the assigned display.
		nameOfTimeLine=timelineModel.getNameOfTimeline();
		timelineMuted=timelineModel.getMuted();
		timelineHidden=timelineModel.getHidden();
		
	}
	
	/**
	 * Adds listeners to the name of timeline-textField. 
	 */
	private void addListenerToNameOfTimelineTextField(){

			/*
			 * Fires when the user clicks the field. This method can be used if the appearance of the field shall change when clicked on
			 */
			nameOfTimeLineField.setOnMouseClicked(new EventHandler<MouseEvent>() {  
			  @Override  
			  public void handle(MouseEvent event) {
//				  nameOfTimeLineField.setStyle(" -fx-focus-color: black; -fx-accent: transparent; -fx-background-color: white;");
				  nameOfTimeLineField.requestFocus();
				  nameOfTimeLineField.setEditable(true); 
			  }  
			});
			
			/*
			 * OnAction listener. Currently only firing when the ENTER-key is pressed. 
			 */
			nameOfTimeLineField.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					//Sends the info to the modules when pressing enter
					timelineModel.setNameOfTimeline(nameOfTimeLineField.getText());
					
				}
				
			});
			/*
			 * Send the new text if the user defocuses the textField
			 */
			nameOfTimeLineField.focusedProperty().addListener(new ChangeListener<Boolean>()
			{
			    @Override
			    public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue)
			    {
			        if (newPropertyValue)
			        {
			            //TextField is not in focus
			        }
			        else
			        {
			            //TextField is not out of focus
			            timelineModel.setNameOfTimeline(nameOfTimeLineField.getText());
			            
			        }
			    }
			});
	}
	
	


	
	

}
