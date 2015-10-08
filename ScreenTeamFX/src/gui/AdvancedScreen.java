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
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;

/**
 * @author Anders Lunde,  Magnus Gunde
 *The AdvancedScreen class represents the view/Screen where you can create a new session that will be displayed on the screens.
 *This is the most important screen in the application and it will contain most of the applications functionallity.
 */
public class AdvancedScreen implements Screen{
		
		//Singleton:
		private static AdvancedScreen advancedScreen;
		

		private Scene screenScene;
		private AdvancedScreenController screenController;	
		
		//variable to keep track of the media files imported
		ObservableList<String> importedMediaFiles = FXCollections.observableArrayList();
		
		//Autoupdatable listproperty for use on the listview
		protected ListProperty<String> listProperty = new SimpleListProperty<>();

		private AdvancedScreen(){
			

			//Creating a new controller for use in the fxml
			screenController = new AdvancedScreenController();		
			
//			screenScene = new Scene(rootPane,1200,700); //TODO: Get size from global size ?
			screenScene = new Scene(screenController.getFXMLLoader().getRoot(),1200,700); //TODO: Get size from global size ?
//			screenScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
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
		
		
		// - change log: magnus 0110 - 
		/*
		 * (non-Javadoc)
		 * @ functionality for the file chooser
		 * The filechooser adds the file to the arraylist, and updates the on-screen listView
		 */
		public void fileChosen(File file){
			importedMediaFiles.add(file.toString());
			listProperty.set(importedMediaFiles);
			
		}
		
		
		/**
		 * 
		 * @author Anders Lunde
		 * The controller for the FXML of the advancedScreen.
		 * The MainScreenController listens to all the input from the objects (buttons, textFields, mouseClicks) in the fxml scene.
		 */
		public class AdvancedScreenController implements FXMLController {
			

			//List of all Children controllers
			private ArrayList<FXMLController> childControllers;

			private FXMLLoader fxmlLoader;
			private AnchorPane rootPane;
			
			//Drag&drop
			private MediaObjectIcon mDragOverIcon = null;
			private EventHandler<DragEvent> mIconDragOverRoot = null;
			private EventHandler<DragEvent> mIconDragDropped = null;
			private EventHandler<DragEvent> mIconDragOverTimeline = null;

			// Pointers to the fx:id in the fxml
			@FXML private GridPane timelineContainer;
			@FXML private Button testButton;
			@FXML private ListView fileListView;
			
			//TEST
			@FXML private GridPane topGrid;
			@FXML private GridPane rootGrid;
			
			public AdvancedScreenController(){

				//Instantiating controller list
				childControllers = new ArrayList<FXMLController>();

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
				
				//Drag&drop functionality
				initialize();

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
				
				//TODO: REMOVE TEST ONLY:
				//Creates a dummy icon
				MediaObjectIcon icn = new MediaObjectIcon();
				addDragDetection(icn);
				icn.setType(MediaObjectType.VIDEO);
				topGrid.add(icn,1,1);
				buildDragHandlers();
			}
			
			/**
			 * adding drag detection to a MediaObjectIcon
			 * 
			 * @param dragIcon
			 */
			private void addDragDetection(MediaObjectIcon dragIcon) {
				
				dragIcon.setOnDragDetected (new EventHandler <MouseEvent> () {

					@Override
					public void handle(MouseEvent event) {

						// Sets the drag handler for the rootPane.
						// Telling the root how to handle the dragIcon
						//TODO: One of these are handling the drag over wrong, should not be allowed to drop on root.
						rootPane.setOnDragOver(mIconDragOverRoot);
//						
//						//Get timelines and add dragBehavior
//						for (FXMLController timelineController : childControllers) {
//							((TimelineController)timelineController).getTimelineLineController().setRootOnDragOver(mIconDragOverTimeline);
//						}
//						//Get timelines and add dropBehavior
//						for (FXMLController timelineController : childControllers) {
//							((TimelineController)timelineController).getTimelineLineController().setRootOnDropped(mIconDragDropped);
//						}
						
						// get a reference to the clicked DragIcon object
						MediaObjectIcon icn = (MediaObjectIcon) event.getSource();
						
						//TODO: add all advanced information here
						//Sets the type of the drag icon based on the icon of the triggering MediaObject (Video/Sound)
						mDragOverIcon.setType(icn.getType());
						//Moves the icon to the point
						mDragOverIcon.relocateToPoint(new Point2D (event.getSceneX(), event.getSceneY()));
//						mDragOverIcon.relocateToPoint(new Point2D (80, 80));

						
						//Creates a javafx clipboard and creates a new DragContainer for the mediaObject
						ClipboardContent content = new ClipboardContent();
						MediaObjectContainer container = new MediaObjectContainer();
//						
						container.addData ("type", mDragOverIcon.getType().toString());
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
			 * Ex: how the rootpane should handle the drag icon
			 */
			private void buildDragHandlers() {
				
				//drag over transition to move widget from MediaObjectView
				mIconDragOverRoot = new EventHandler <DragEvent>() {

					@Override
					public void handle(DragEvent event) {
						
//						Point2D p = right_pane.sceneToLocal(event.getSceneX(), event.getSceneY());
						
						//Get timelines and add dragBehavior
//						for (FXMLController timelineController : childControllers) {
//							Pane timelineLinePane = ((TimelineController)timelineController).getTimelineLineController().getRoot();
//							Point2D p = timelineLinePane.sceneToLocal(event.getSceneX(), event.getSceneY());;
//						
//							/**
//							 *TODO: Redefine, what is this doing ?
//							 * Turns of the transfering of mediaObject if the user is inside timline?
//							 */
//							if (!timelineLinePane.boundsInLocalProperty().get().contains(p)) {
//								
//								event.acceptTransferModes(TransferMode.ANY);
//								mDragOverIcon.relocateToPoint(new Point2D(event.getSceneX(), event.getSceneY()));
//								return;
//							}
//						}
						
						event.acceptTransferModes(TransferMode.ANY);
						mDragOverIcon.relocateToPoint(new Point2D(event.getSceneX(), event.getSceneY()));
						return;
//						event.consume();
					}
				};
				
				mIconDragOverTimeline = new EventHandler <DragEvent> () {

					@Override
					public void handle(DragEvent event) {

						event.acceptTransferModes(TransferMode.ANY);
						
						//convert the mouse coordinates to scene coordinates,
						//then convert back to coordinates that are relative to 
						//the parent of mDragIcon.  Since mDragIcon is a child of the root
						//pane, coodinates must be in the root pane's coordinate system to work
						//properly.
						mDragOverIcon.relocateToPoint(
										new Point2D(event.getSceneX(), event.getSceneY())
						);
						event.consume();
					}
				};
						
				/**
				 * When the mediaObject is TODO:...
				 */
				mIconDragDropped = new EventHandler <DragEvent> () {

					@Override
					public void handle(DragEvent event) {
						
						MediaObjectContainer container = 
								(MediaObjectContainer) event.getDragboard().getContent(MediaObjectContainer.AddNode);
						
						container.addData("scene_coords", 
								new Point2D(event.getSceneX(), event.getSceneY()));
						
						ClipboardContent content = new ClipboardContent();
						content.put(MediaObjectContainer.AddNode, container);
						
						event.getDragboard().setContent(content);
						event.setDropCompleted(true);
					}
				};
				
				/**
				 * When the drag is complete, we clean up the drag operation. and add the node to the timeline
				 */
				rootPane.setOnDragDone (new EventHandler <DragEvent> (){
					
					@Override
					public void handle (DragEvent event) {
						


						rootPane.removeEventHandler(DragEvent.DRAG_OVER, mIconDragOverRoot);
										
						mDragOverIcon.setVisible(false);
						
						MediaObjectContainer container = 
								(MediaObjectContainer) event.getDragboard().getContent(MediaObjectContainer.AddNode);
						
						if (container != null) {
							//If the drop is inside the view
							if (container.getValue("scene_coords") != null) {
							
								MediaObjectController node = new MediaObjectController();
								
//								node.setType(MediaObjectType.valueOf(container.getValue("type")));
								node.initializeMediaObject(container);
								
								for (FXMLController timelineController : childControllers) {
									((TimelineController)timelineController).getTimelineLineController().getRoot().removeEventHandler(DragEvent.DRAG_OVER, mIconDragOverTimeline);
								}
								
								//We need to check which timeline the container is dropped upon
								for (FXMLController timelineController : childControllers) {
									Pane timelineLinePane = ((TimelineController)timelineController).getTimelineLineController().getRoot();
									Point2D p = timelineLinePane.sceneToLocal(event.getSceneX(), event.getSceneY());;
									if (timelineLinePane.boundsInLocalProperty().get().contains(p)) {
										((TimelineController)timelineController).addMediaObject(node,p);
//										timelineLinePane.getChildren().add(node);
									}
								}
								

//								Point2D cursorPoint = container.getValue("scene_coords");
//
//								node.relocateToPoint(
//										new Point2D(cursorPoint.getX() - 32, cursorPoint.getY() - 32)
//										);
							}
						}
						
						container = 
								(MediaObjectContainer) event.getDragboard().getContent(MediaObjectContainer.DragNode);
						
						if (container != null) {
							if (container.getValue("type") != null)
								System.out.println ("Moved node " + container.getValue("type"));
						}
						
						event.consume();
					}
				});		
			}
			
			/**
			 * This method is ran when you press a button in the advanced screen top layout (Not inside the timelines).
			 * It assumes that all buttons has in id. if they do not have an id this method gives a null pointer exception.
			 * @param event
			 */
			@FXML protected void buttonPressed(ActionEvent event) {
				System.out.println("AdvancedScreen:" + event.getSource().toString() + "has been pressed");
				
				if(((Button)event.getSource()).getId().equals("menuBtn") ){
					//If the menu screen button is pressed the MainGUIController changes the screen to be the menu screen
					MainGUIController.getInstance().changeScreen(SCREENTYPE.MAINMENU);
					
				}else if(((Button)event.getSource()).getId().equals("addTimeLineBtn")){
					System.out.println("Adding a TimeLine");
					addTimeline();
					
				}else if(((Button)event.getSource()).getId().equals("importMedia")){
					// If the user chooses the import media button, he will get a windows-file-chooser
					FileChooser fileChooser = new FileChooser();
					fileChooser.setTitle("Open media file");
					File file=fileChooser.showOpenDialog(MainGUIController.getInstance().primaryStage);
					
					//binds the items of the listView to the listProperty. This should probably be done somewhere else
					fileListView.itemsProperty().bind(listProperty);
					if(file != null){
						fileChosen(file);
					}
			        

				}
			}	
			
			/**
			 * Adds a new timeline to the advancedScreen
			 * TODO: add arguments and models
			 */
			private void addTimeline(){
				TimelineController tempTimeController = new TimelineController();
				childControllers.add(tempTimeController);
				addTimelineControllerToScreen(tempTimeController);
				
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
			
			/**
			 * Adds the given TimelineController to the timelineContainer (GridPane)
			 * First adding the info to the left, then adding the actual timeline to the right.
			 * @param newController
			 */
			private void addTimelineControllerToScreen(TimelineController tempTimeController){
				timelineContainer.add(tempTimeController.getFXMLLoader().getRoot(), 0, (childControllers.size()-1));
				timelineContainer.add(tempTimeController.getTimelineLineController().getFXMLLoader().getRoot(), 1, (childControllers.size()-1));
			}

		}//end AdvancedScreenController




}
