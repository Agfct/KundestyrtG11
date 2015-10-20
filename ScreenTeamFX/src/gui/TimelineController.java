package gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.controlsfx.control.CheckComboBox;

import gui.AdvancedScreen.AdvancedScreenController;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;
import modules.TimelineModel;
import sun.misc.Cleaner;

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
	
	@FXML GridPane timelineInfo;
	@FXML GridPane timelineContainer;
	@FXML AnchorPane timelineLineContainer;
	@FXML Button removeTimelineBtn;
	@FXML CheckComboBox<String> checkComboBox;
	@FXML Label listOfScreens;
	
	//The timelinemodel that corresponds to this controller
	TimelineModel timelineModel;
	
	ArrayList<Integer> assignedDisplays;
	
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
	
	private void initializeTimelineInfo(){
		
		
		checkComboBox.getItems().clear();
		System.out.println("UPDATING TIMELINEMODEL CTRL: "+ timelineModel.getID());
		
		ObservableList<String> obsList = FXCollections.observableArrayList();
		 for (int i=0;i<parentController.getAvailableDisplays().size();i++) {
//		     obsDisplays.add(i.toString());
//		     checkComboBox.getItems().add(parentController.getAvailableDisplays().get(i).toString());
			 obsList.add(parentController.getAvailableDisplays().get(i).toString());
		     
		 }
		 checkComboBox.getItems().addAll(obsList);
		 checkComboBox.getCheckModel().clearChecks();
		 if(assignedDisplays.size()!=0){
			 for(int i=0;i<checkComboBox.getItems().size();i++){
			 if(assignedDisplays.contains(Integer.parseInt(checkComboBox.getItems().get(i)))){
				 checkComboBox.getCheckModel().checkIndices(i);
			 }
			 }
		 }
		 
		 
//		 checkComboBox.getCheckModel().checkIndices(0);
		 //Checks who is suppose to be checked:
		 
//		 checkComboBox = new CheckComboBox<String>(obsDisplays);
//		 checkComboBox.getItems().addAll(obsDisplays);
//		 checkComboBox.getCheckModel().getCheckedItems().clear();
//		 if(assignedDisplays.size()>0){
//			 checkComboBox.getCheckModel().getCheckedItems().addAll(assignedDisplays.get(0).toString());
//		 }
		 System.out.println("CHECKED ITEMS; "+checkComboBox.getCheckModel().getCheckedItems());
		 
//		 checkComboBox.setCheckModel(value);
		     

		 checkComboBox.getCheckModel().getCheckedItems().addListener(new ListChangeListener<String>() {
		     public void onChanged(ListChangeListener.Change<? extends String> c) {
		    	 System.out.println("CheckedItems: " +checkComboBox.getCheckModel().getCheckedItems());
		    	 parentController.assignRequest(checkComboBox.getCheckModel().getCheckedItems(),timelineModel);
		     }
		     
		 });
		 
		 
//		List<String> supplierNames = Arrays.asList("sup1", "sup2", "sup3");
//		screenChoiceBox.setItems(FXCollections.observableArrayList(supplierNames));
//		typeChoiseBox.getSelectionModel().select(currentMediaObject.getType());
//		screenChoiceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
//	        @Override public void changed(ObservableValue<? extends String> ov, String oldType, String newType) {
//	        	currentMediaObject.setType(newType);
//	          }    
//	      });
//		typeChoiseBox.getSelectionModel().selectedIndexProperty().addListener(new
//				ChangeListener<Number>() {
//			@Override
//			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
//				name.setText(newValue.toString());
//			}
//		});
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
			parentController.removeTimeline(this);
			
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

	public void modelChanged() {
		childController.repaint();
		updateValuesFromModel();
		initializeTimelineInfo();
		
	}

	private void updateValuesFromModel() {
		assignedDisplays=timelineModel.getAssignedDisplays();
		
	}
	
	


	
	

}
