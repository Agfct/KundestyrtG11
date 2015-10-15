/**
 * 
 */
package gui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

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
import javafx.stage.FileChooser;

import modules.*;
/**
 * @author Anders Lunde,  Magnus Gunde
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

			private FXMLLoader fxmlLoader;
			private AnchorPane rootPane;
			@FXML private GridPane rootGrid;
			@FXML private GridPane barGrid;
			
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
				barGrid.add(timelineBarController,1,0);
				
				//InitializeScrollBar values
				initializeScrollBar();
				
				//Drag&drop functionality
				initialize();
				
				// Initialize the header
				initHeader(this);
				
				//get current session
				currentSession=MainModuleController.getInstance().getSession();
				currentSession.addListener(this);

			}
			


			/**
			 * initializes the Scroll bar and its listener
			 * The listener moves the timelineLines.
			 * TODO: Keep current scroll value to update newly added timlines, also make shure the scroll does not cover the info window.
			 */
			private void initializeScrollBar() {
				timelineLineScrollBar.valueProperty().addListener(new ChangeListener<Number>() {
		            public void changed(ObservableValue<? extends Number> ov,
		                Number old_val, Number new_val) {
		            	System.out.println("Scrolling: Old value: "+ old_val.doubleValue()+" NewValue: "+ new_val.doubleValue());
		            	for (TimelineController timelineController : timelineControllers) {
		            		timelineController.getTimelineLineController().moveTimeline(-new_val.doubleValue());;
						}

		            }
		        });
				
			}



			/**
			 * Initializes a dummy icon that will be displayed when dragging
			 * accross panes.
			 *
			 */
			private void initialize(){
				
				mDragOverIcon = new MediaObjectIcon();
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
						container.addData ("type", mDragOverIcon.getType().toString());
						
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
								System.out.println("Not EMPTY");
							
								MediaObjectController node = new MediaObjectController();
								
								//Sends the container containing all the information about the mediaObject
								//To the newly created MediaObject for initialization
								node.initializeMediaObject(container);
								
				
								//We need to check which timeline the container is dropped upon
								for (FXMLController timelineController : timelineControllers) {
									TimelineLineController currentTimelineLineController = ((TimelineController)timelineController).getTimelineLineController();
									Pane timelineLinePane = currentTimelineLineController.getRoot();
									Point2D containerPoints = (Point2D)container.getValue("scene_coords");
									Point2D p = timelineLinePane.sceneToLocal(containerPoints);
									System.out.println("Pane:" + timelineLinePane);
									System.out.println("Point: X: " + p.getX() + " Y: " + p.getY());
									System.out.println(" Container Point: X: " + ((Point2D) container.getValue("scene_coords")).getX() + " Y: " + ((Point2D) container.getValue("scene_coords")).getY());
									if (timelineLinePane.boundsInLocalProperty().get().contains(p)) {
										currentTimelineLineController.addMediaObject(node,p);
										System.out.println(" WE ARE DONE !!!");
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
				TimelineController tempTimeController = new TimelineController();
				timelineControllers.add(tempTimeController);
				addTimelineControllerToScreen(tempTimeController);
			}
			
			/**
			 * Adds the given TimelineController to the timelineContainer (VBox).
			 * @param newController
			 */
			private void addTimelineControllerToScreen(TimelineController tempTimeController){
				timelineContainer.getChildren().add(tempTimeController.getRoot());
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
			public void removeTimeline(TimelineController timeline){
				timelineContainer.getChildren().remove(timeline.getRoot());
				timelineControllers.remove(timeline);
			}
			
			
			/*
			 * Initializes the header
			 */
			public void initHeader(AdvancedScreenController self){
				headerController = new HeaderController(self);
				System.out.println("INITING THE HEADER: ");
				rootGrid.getChildren().add(headerController.getRoot());

			}



			@Override
			public void fireTimelinesChanged() {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void fireMediaObjectListChanged() {
				headerController.mediaObjectsChanged();
				
			}
			
			public SessionModule getCurrentSession(){
				return currentSession;
			}
			
	

		}//end AdvancedScreenController




}
