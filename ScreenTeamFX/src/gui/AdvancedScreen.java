/**
 * 
 */
package gui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import modules.*;


/**
 * @author Anders Lunde,  Magnus Gundersen
 * Singleton class
 *The AdvancedScreen class represents the view/Screen where you can create a new session that will be displayed on the screens.
 *This is the most important screen in the application and it will contain most of the applications functionallity.
 */
public class AdvancedScreen implements Screen{
		
		//Singleton:
		private static AdvancedScreen advancedScreen;
		

		private Scene screenScene;
		private AdvancedScreenController screenController;	
	

		private AdvancedScreen(){
			

			//Creating a new controller for use in the fxml
			screenController = new AdvancedScreenController();		
			
//			screenScene = new Scene(rootPane,1200,700); //TODO: Get size from global size ?
			screenScene = new Scene(screenController.getFXMLLoader().getRoot(),1200,700); //TODO: Get size from global size ?
		}
		
		public static AdvancedScreen getInstance() {
			if(advancedScreen == null){
				advancedScreen = new AdvancedScreen();
			}
			return advancedScreen;
		}
		
		/*
		 * (non-Javadoc)
		 * @see gui.Screen#getScene()
		 */
		@Override
		public Scene getScene() {
			return screenScene;
		}
		
		public AdvancedScreenController getScreenController() {
			return screenController;
		}
		
		
		
		
		/**
		 * 
		 * @author Anders Lunde, Magnus Gundersen
		 * The controller for the FXML of the advancedScreen.
		 * The MainScreenController listens to all the input from the objects (buttons, textFields, mouseClicks) in the fxml scene.
		 */
		public class AdvancedScreenController implements FXMLController, SessionListener {
			

			//List of all TimelineControllers within the advancedScreen
			private ArrayList<TimelineController> timelineControllers;
			private TimelineBarController timelineBarController;
			private HeaderController headerController;		
			private SessionModule currentSession;
			private double scrollBarPosition = 0;
			private Stage modalDialog;

			private FXMLLoader fxmlLoader;
			private AnchorPane rootPane;
			@FXML private GridPane rootGrid;
			@FXML private GridPane barGrid;
			@FXML private AnchorPane timelineBarContainer;
			
			//Drag&drop
			private MediaObjectIcon mDragOverIcon = null;
			private EventHandler<DragEvent> mIconDragOverRoot = null;
			private EventHandler<DragEvent> mIconDragDropped = null;
			private EventHandler<DragEvent> mIconDragDone = null;
			private EventHandler<DragEvent> mIconDragOverTimeline = null;

			// Pointers to the fx:id in the fxml
			@FXML private VBox timelineContainer;
			@FXML private Button testButton;
			@FXML private ListView fileListView;
			@FXML private ScrollBar timelineLineScrollBar;
			
			// Hashmap over the timelineIDs from the modules and the timelineControllers of the GUI
			private HashMap<TimelineController, Integer> idTimlineControllerMap;

			
			public AdvancedScreenController(){
				System.out.println("RUNNING CONSTRUCTOR ADVANCEDSCREEN CTRL");
				//Instantiating controller list
				timelineControllers = new ArrayList<TimelineController>();

				// The constructor will try to fetch the fxml 
				try {
					fxmlLoader = new FXMLLoader(getClass().getResource("AdvancedScreen.fxml"));
					fxmlLoader.setController(this);
					fxmlLoader.load();
					rootPane = fxmlLoader.getRoot();
				} catch (IOException e) {
					System.out.println("Failed to load AdvancedScreenController FXML");
					e.printStackTrace();
				}
				
				
				timelineBarController = new TimelineBarController(this);
				timelineBarContainer.getChildren().add(timelineBarController);
				
				//get current session
				currentSession=MainModuleController.getInstance().getSession();
				currentSession.addListener(this);
				
				//InitializeScrollBar values
				initializeScrollBar();
				
				//Drag&drop functionality
				initialize();
				
				// Initialize the header
				initHeader(this);
				
				// Instanciated the hashmap over controllers
				idTimlineControllerMap = new HashMap<TimelineController, Integer>();

			}
			


			/**
			 * initializes the Scroll bar and its listener
			 * The listener moves the timelineLines.
			 * TODO: Keep current scroll value to update newly added timlines, also make shure the scroll does not cover the info window.
			 */
			private void initializeScrollBar() {
				
				//TODO: atm this is the size of the clip minus the size of the timelineLine
				timelineLineScrollBar.setMax(currentSession.getSessionLength() - 1000);
				timelineLineScrollBar.valueProperty().addListener(new ChangeListener<Number>() {
		            public void changed(ObservableValue<? extends Number> ov,
		                Number old_val, Number new_val) {
//		            	System.out.println("Scrolling: Old value: "+ old_val.doubleValue()+" NewValue: "+ new_val.doubleValue());
		            	scrollBarPosition = -new_val.doubleValue();
		            	for (TimelineController timelineController : timelineControllers) {
		            		
		            		timelineController.getTimelineLineController().moveTimeline(scrollBarPosition);
						}
		            	timelineBarController.moveTimelineBar(scrollBarPosition);

		            }
		        });
				
			}



			/**
			 * Initializes a dummy icon that will be displayed when dragging
			 * accross panes.
			 *
			 */
			private void initialize(){
				
				mDragOverIcon = new MediaObjectIcon(null);
				mDragOverIcon.setVisible(false);
				mDragOverIcon.setOpacity(0.65);
				rootPane.getChildren().add(mDragOverIcon);
				
				buildDragHandlers();
			}
			
			/**
			 * adding drag detection to a MediaObjectIcon.
			 * @param dragIcon
			 */
			public void addDragDetection(MediaObjectIcon dragIcon) {
				
				dragIcon.setOnDragDetected (new EventHandler <MouseEvent> () {

					@Override
					public void handle(MouseEvent event) {
						System.out.println("[AdvancedScreen] Drag started");

						// Sets the drag handler for the rootPane.
						// Telling the root how to handle the dragIcon
						rootPane.setOnDragOver(mIconDragOverRoot);
						rootPane.setOnDragDone(mIconDragDone);
//						
						//Get timelines and add dragBehavior
//						for (FXMLController timelineController : timelineControllers) {
//							((TimelineController)timelineController).getTimelineLineController().setRootOnDragOver(mIconDragOverTimeline);
//						}
//						//Get timelines and add dropBehavior
						for (FXMLController timelineController : timelineControllers) {
							((TimelineController)timelineController).getTimelineLineController().setRootOnDropped(mIconDragDropped);
						}
						
						// get a reference to the clicked DragIcon object
						MediaObjectIcon icn = (MediaObjectIcon) event.getSource();
						
						
						//Sets the type of the drag icon based on the icon of the triggering MediaObject (Video/Sound)
						//Here one can alternativly set text that shoud be displayed on the icon while you drag it.
						mDragOverIcon.setType(icn.getType());

						
						//Moves the icon to the point where you start the drag event
						mDragOverIcon.relocateToPoint(new Point2D (event.getSceneX(), event.getSceneY()));

						//Creates a javafx clipboard and creates a new DragContainer for the mediaObject
						ClipboardContent content = new ClipboardContent();
						MediaObjectContainer container = new MediaObjectContainer();
//						
						//TODO: add all advanced information about the MediaObject is added here (seperate method ? )
						container.addData ("model", icn.getMediaObject());
						
						//Container is put onto the clipboard
						content.put(MediaObjectContainer.AddNode, container);

						mDragOverIcon.startDragAndDrop (TransferMode.ANY).setContent(content);
						mDragOverIcon.setVisible(true);
						mDragOverIcon.setMouseTransparent(true);
						event.consume();					
					}
				});
			}
			
			/**
			 * Defines what the EventHandlers are supposed to do.
			 * For example: how the rootpane should handle the drag icon
			 */
			private void buildDragHandlers() {
				
				//drag over transition to move widget from MediaObjectView over the AnchorPane
				mIconDragOverRoot = new EventHandler <DragEvent>() {

					@Override
					public void handle(DragEvent event) {
//						System.out.println("[AdvancedScreen] Dargging over root");
						
						//We only want the drop icon to display "ok to drop here" when you are hovering over a timeline.
						for (FXMLController timelineController : timelineControllers) {
							Pane timelineLinePane = ((TimelineController)timelineController).getTimelineLineController().getRoot();
							Point2D p = timelineLinePane.sceneToLocal(event.getSceneX(), event.getSceneY());
						
							//If the drag event goes inside the boundaries of the Timeline you are ok to transfer
							if (timelineLinePane.boundsInLocalProperty().get().contains(p)) {
								event.acceptTransferModes(TransferMode.ANY);
							}
						}
						
						mDragOverIcon.relocateToPoint(new Point2D(event.getSceneX(), event.getSceneY()));
						event.consume();
					}
				};
				
				//TODO: NOT USED ? REVISIT
//				mIconDragOverTimeline = new EventHandler <DragEvent> () {
//
//					@Override
//					public void handle(DragEvent event) {
//
//						event.acceptTransferModes(TransferMode.ANY);
//						
//						//convert the mouse coordinates to scene coordinates,
//						//then convert back to coordinates that are relative to 
//						//the parent of mDragIcon.  Since mDragIcon is a child of the root
//						//pane, coodinates must be in the root pane's coordinate system to work
//						//properly.
//						mDragOverIcon.relocateToPoint(
//										new Point2D(event.getSceneX(), event.getSceneY())
//						);
//						event.consume();
//					}
//				};
						
				/**
				 * When the mediaObjectIcon is dropped onto a timeline this handler
				 * gets the drop point and adds it to a new mediaObjectContainer which is set as the
				 * content on the final clipboard and then it sets the 
				 */
				mIconDragDropped = new EventHandler <DragEvent> () {

					@Override
					public void handle(DragEvent event) {
						System.out.println("[AdvancedScreen] DragDropped");
						
						MediaObjectContainer container = 
								(MediaObjectContainer) event.getDragboard().getContent(MediaObjectContainer.AddNode);
						
						//TODO: TESTING REMOVE THE pk point and sysout
						Point2D pk = new Point2D(event.getSceneX(),event.getSceneY());
						System.out.println("Dropped scene coords: X:" +pk.getX() + "Y:"+ pk.getY());
						
						//Adds the coordinates where the icon was dropped to the MediaObjectContainer
						container.addData("scene_coords", 
								new Point2D(event.getSceneX(), event.getSceneY()));
						
						ClipboardContent content = new ClipboardContent();
						content.put(MediaObjectContainer.AddNode, container);
						
						event.getDragboard().setContent(content);
						
						//Setting drop Completed to true indicates that the Drag-Drop was successful.
						event.setDropCompleted(true);
					}
				};
				
				/**
				 * When the drag is complete we clean up the drag operation, 
				 * and add the node to the timeline that was dropped upon.
				 */
				mIconDragDone = new EventHandler <DragEvent> () {
					
					@Override
					public void handle (DragEvent event) {
						System.out.println("[AdvancedScreen] Drag DONE");

						//Cleaning up the DragEvents
						rootPane.removeEventHandler(DragEvent.DRAG_OVER, mIconDragOverRoot);
						rootPane.setOnDragOver(null);
						rootPane.setOnDragDone(null);
//						for (FXMLController timelineController : timelineControllers) {
//							((TimelineController)timelineController).getTimelineLineController().getRoot().removeEventHandler(DragEvent.DRAG_OVER, mIconDragOverTimeline);
//						}
						for (FXMLController timelineController : timelineControllers) {
							((TimelineController)timelineController).getTimelineLineController().getRoot().removeEventHandler(DragEvent.DRAG_DROPPED, mIconDragDropped);
							((TimelineController)timelineController).getTimelineLineController().getRoot().setOnDragDropped(null);
						}
										
						mDragOverIcon.setVisible(false);
						
						MediaObjectContainer container = 
								(MediaObjectContainer) event.getDragboard().getContent(MediaObjectContainer.AddNode);
						
						if (container != null) {
							//If the drop is inside the view
							if (container.getValue("scene_coords") != null) {
//								System.out.println("Not EMPTY");
							
//								MediaObjectController node = new MediaObjectController();
								
								//Sends the container containing all the information about the mediaObject
								//To the newly created MediaObject for initialization
//								node.initializeMediaObject(container);
								
				
								//We need to check which timeline the container is dropped upon
								for (TimelineController timelineController : timelineControllers) {
									TimelineLineController currentTimelineLineController = ((TimelineController)timelineController).getTimelineLineController();
									Pane timelineLinePane = currentTimelineLineController.getRoot();
									Point2D containerPoints = (Point2D)container.getValue("scene_coords");
									Point2D p = timelineLinePane.sceneToLocal(containerPoints);
									System.out.println("Pane:" + timelineLinePane);
									System.out.println("Point: X: " + p.getX() + " Y: " + p.getY());
									System.out.println(" Container Point: X: " + ((Point2D) container.getValue("scene_coords")).getX() + " Y: " + ((Point2D) container.getValue("scene_coords")).getY());
									if (timelineLinePane.boundsInLocalProperty().get().contains(p)) {
										String result=currentSession.addMediaObjectToTimeline(container.getValue("model"), timelineController.getTimelineModel() , (int)p.getX()*1000);
										System.out.println("Result of the drop of mediaObject: " + result);
										break;
									}
								}
								
							}
						}
						
						
						event.consume();
					}
				};		
			}
			
			
			
			/**
			 * Adds a new timeline to the advancedScreen
			 * First adding the TimelineController to the advanceScreen controller list
			 * then adding the GridPane of the TimelineController to the VBox container.
			 * TODO: add arguments and java models
			 */
			public void addTimeline(){
				int timelineInt = currentSession.addTimeline();
				System.out.println(timelineInt);
				System.out.println("Total number of timelines: "+ currentSession.getTimelines().size());
//				TimelineController tempTimeController = new TimelineController();
//				timelineControllers.add(tempTimeController);
//				addTimelineControllerToScreen(tempTimeController);
			}
			
			/**
			 * Adds the given TimelineController to the timelineContainer (VBox).
			 * @param newController
			 */
			private void addTimelineControllerToScreen(TimelineController tempTimeController){
				timelineContainer.getChildren().add(tempTimeController.getRoot());
				tempTimeController.getRoot().toBack();
			}
			
			/**
			 * Removes the given timelineController from the timelinecontainer(Vbox)
			 * @param timeLineControllerToBeRemoved
			 */
			private void removeTimelineControllerFromScreen(TimelineController timelineController){
				timelineContainer.getChildren().remove(timelineController.getRoot());

			}
			
			private void removeAllTimelineControllersFromScreen(){
				timelineContainer.getChildren().clear();
			}
			
			/* (non-Javadoc)
			 * @see gui.FXMLController#getFXMLLoader()
			 */
			public FXMLLoader getFXMLLoader(){
				return fxmlLoader;
			}
			public GridPane getRoot(){
				return rootGrid;
			}
			public AnchorPane getMasterRoot(){
				return rootPane;
			}
	
			/**
			 * Removes the TimelineControllers GridPane from the VBox container
			 * and the TimelineController from the advanceScreens controller list.
			 * @param timeline
			 */
			public void removeTimeline(TimelineController timelineController){
//				timelineContainer.getChildren().remove(timeline.getRoot());
//				timelineControllers.remove(timeline);
				currentSession.removeTimeline(idTimlineControllerMap.get(timelineController));
			}
			
			
			/*
			 * Initializes the header
			 */
			public void initHeader(AdvancedScreenController self){
				headerController = new HeaderController(self);
				System.out.println("INITING THE HEADER: ");
				rootGrid.getChildren().add(headerController.getRoot());

			}


			/*
			 * (non-Javadoc)
			 * @see gui.SessionListener#fireTimelinesChanged(modules.TimeLineChanges, modules.TimelineModel)
			 * 
			 */
			@Override
			public void fireTimelinesChanged(TimeLineChanges changeType, TimelineModel timeLineModel) {
			
				
				switch (changeType) {
				case ADDED:{
					TimelineController tempTimeController = new TimelineController(timeLineModel);
					timelineControllers.add(tempTimeController);
					addTimelineControllerToScreen(tempTimeController);
					idTimlineControllerMap.put(tempTimeController, timeLineModel.getID());
					break;
				}
				case REMOVED:{
					TimelineController timelineControllerToBeRemoved=null;
					for(TimelineController timelineController: idTimlineControllerMap.keySet()){
						if(idTimlineControllerMap.get(timelineController).equals(timeLineModel.getID())){
							timelineControllerToBeRemoved=timelineController;
							break;
						}
					}
					idTimlineControllerMap.remove(timelineControllerToBeRemoved);
					timelineControllers.remove(timelineControllerToBeRemoved);
					removeTimelineControllerFromScreen(timelineControllerToBeRemoved);
					break;
				}
				case MODIFIED:{
					//TODO: find out what the modification was: addedMediaObject, removedMediaOBject or replaced mediaobject. 
					TimelineController timelineControllerToBeModified=null;
					for(TimelineController timelineController: idTimlineControllerMap.keySet()){
						if(idTimlineControllerMap.get(timelineController).equals(timeLineModel.getID())){
							timelineControllerToBeModified=timelineController;
							break;
						}
					}
					timelineControllerToBeModified.modelChanged();
					
					break;
				}
				case ORDER:{
					removeAllTimelineControllersFromScreen();
					ArrayList<Integer> orderedListOfTimelines = currentSession.getTimelineOrder();
					
					//We need to reverse the ordering because we always bring the newest timeline to the top of the view. 
					for(int i=orderedListOfTimelines.size()-1;i>=0;i--){
						TimelineController timelineControllerToPaint=null;
						for(TimelineController timelineController: idTimlineControllerMap.keySet()){
							if(idTimlineControllerMap.get(timelineController).equals(orderedListOfTimelines.get(i))){
								timelineControllerToPaint=timelineController;
								break;
							}
						}
						addTimelineControllerToScreen(timelineControllerToPaint);
						
					}					
					break;
				}
				
				default:
					System.out.println("bitte nicht die enum engenehmen, Zimmer...");
					break;
				}
				//Remove all timelines from view
//				timelineControllers.clear();
//				timelineContainer.getChildren().clear();
//				idTimlineControllerMap.clear();
				
//				for(TimelineModel tlm:timelineModelList){
//					TimelineController tempTimeController = new TimelineController();
//					timelineControllers.add(tempTimeController);
//					addTimelineControllerToScreen(tempTimeController);
//					idTimlineControllerMap.put(tempTimeController, tlm.getID());
//					
//				}
				
			}

			public TimelineBarController getTimelineBarController(){
				return timelineBarController;
			}

			@Override
			public void fireMediaObjectListChanged() {
				headerController.mediaObjectsChanged();
				
			}
			
			public SessionModule getCurrentSession(){
				return currentSession;
			}
			
			public void showModal(MediaObjectController mediaObject){
//                final Stage modalDialog = new Stage();
                modalDialog = new Stage();
                modalDialog.setResizable(false);
                modalDialog.initModality(Modality.APPLICATION_MODAL);
                modalDialog.initOwner(MainGUIController.getInstance().primaryStage);
                ModalController mediaObjectModal = new ModalController(mediaObject);
                Scene dialogScene = new Scene(mediaObjectModal.getRoot(), 300, 200);
                modalDialog.setScene(dialogScene);
                modalDialog.show();
			}
			
			public void closeModal(){
				if(modalDialog != null){
					modalDialog.close();
				}
			}



			/**
			 * @return the scrollBarPosition
			 */
			public double getScrollBarPosition() {
				return scrollBarPosition;
			}
			
			public void refreshScrollBarSize() {
				timelineLineScrollBar.setMax(currentSession.getSessionLength() - 1000);
			}



			@Override
			public void fireGlobalTimeChanged(long newGlobalTime) {
				// TODO Update timelineBar to new global time
				System.out.println(newGlobalTime);
				
			}
			
			
			public void playAllTimelines(){
				currentSession.playAll();
			}
			
			public void pauseAllTimelines(){
				currentSession.pauseAll();
			}



			public void changeGlobalTime(long i) {
				currentSession.changeGlobalTime(i);
				
			}
			
			public AnchorPane getTimelineBarContainer(){
				return timelineBarContainer;
			}



			public ArrayList<Integer> getAvailableDisplays() {
				return currentSession.getAvailableDisplays();
			}
			
			public void assignDisplay(Integer displayID,TimelineModel tlm){
				currentSession.assignTimeline(displayID, tlm);
			}



			public void assignRequest(List<String> checkedItems, TimelineModel tlm) {
				System.out.println("CHECKEDITEMS: " +checkedItems);		
				System.out.println("ACTUAL ASSIGNED SCREENS: "+ tlm.getAssignedDisplays() );
				
				if(checkedItems.size()>tlm.getAssignedDisplays().size()){
					System.out.println("New display!");
					//NB: only possible with one screen per timeline. Address problem with ghosts
					currentSession.assignTimeline(Integer.parseInt(checkedItems.get(0)), tlm);
				}
				else if(checkedItems.size()<tlm.getAssignedDisplays().size()){
					//TODO: implement the removal of screen if box is unchecked
					System.out.println("RemovedScreen!");
				}
				else if(checkedItems.size()==tlm.getAssignedDisplays().size()){
					for(String dispString:checkedItems){
						if(!tlm.getAssignedDisplays().contains(Integer.parseInt(dispString))){
							System.out.println("NEW DISPLAY!?");
						}
					}
				}
				
			}

			/* (non-Javadoc)
			 * @see gui.SessionListener#fireSessionLenghtChanged()
			 */
			@Override
			public void fireSessionLenghtChanged() {
				refreshScrollBarSize();
				for (TimelineController timelineController : timelineControllers) {
					timelineController.getTimelineLineController().getRoot().setPrefWidth(currentSession.getSessionLength());
				}
			}
				
			public boolean saveSession() {
				return MainModuleController.getInstance().saveSession();
			}
			
			public boolean saveSession(File saveFile) {
				return MainModuleController.getInstance().saveSession(saveFile);
			}
			
			public void loadSession(File loadFile) {
				SessionModule sm = MainModuleController.getInstance().loadSession(loadFile);
				MainModuleController.getInstance().updateSession(sm);
				currentSession = sm;
				MainGUIController.getInstance().updateSession(sm);
				MainModuleController.getInstance().getSession().addListener(this);
			}



			public void rebuildTimelines() {
				removeAllTimelineControllersFromScreen();
				idTimlineControllerMap = new HashMap<TimelineController, Integer>();
				
				for(Integer i: currentSession.getTimelines().keySet()){
					TimelineModel timeLineModel = currentSession.getTimelines().get(i);
					TimelineController tempTimeController = new TimelineController(timeLineModel);
					timelineControllers.add(tempTimeController);
					idTimlineControllerMap.put(tempTimeController, timeLineModel.getID());
				}
				
				ArrayList<Integer> orderedListOfTimelines = currentSession.getTimelineOrder();
				
				
				//Repaint
				//We need to reverse the ordering because we always bring the newest timeline to the top of the view. 
				for(int i=orderedListOfTimelines.size()-1;i>=0;i--){
					TimelineController timelineControllerToPaint=null;
					for(TimelineController timelineController: idTimlineControllerMap.keySet()){
						if(idTimlineControllerMap.get(timelineController).equals(orderedListOfTimelines.get(i))){
							timelineControllerToPaint=timelineController;
							break;
						}
					}
					addTimelineControllerToScreen(timelineControllerToPaint);
					timelineControllerToPaint.getTimelineLineController().repaint();
					
				}		
			}

		}//end AdvancedScreenController




}
