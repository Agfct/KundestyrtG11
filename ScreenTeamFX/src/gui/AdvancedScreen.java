/**
 *
 */
package gui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import modules.*;


/**
 * @author Anders Lunde,  Magnus Gundersen
 * Singleton class
 *The AdvancedScreen class represents the view/Screen where you can create a new session that will be displayed on the screens.
 *This is the most important screen in the application and its controller "AdvanceScreenController" (located within this class)
 *will contain most of the applications functionality. 
 *Read the explanation (comment) of the AdvanceScreen controller to get a short explanation of the hierarchy of the GUI Controllers.
 */
public class AdvancedScreen implements Screen{

	//Singleton:
	private static AdvancedScreen advancedScreen;


	private Scene screenScene;
	private AdvancedScreenController screenController;


	private AdvancedScreen(){


		//Creating a new controller for use in the fxml
		screenController = new AdvancedScreenController();
		screenScene = new Scene(screenController.getFXMLLoader().getRoot(),1200,700); //TODO: Make a global size instead of 1200,700
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
	 * @author Anders Lunde, Magnus Gundersen
	 * The controller for the FXML of the advancedScreen.
	 * The AdvancedScreenController is the main controller for all of the GUI, and almost all information flows through here.
	 * This controller has all the GUI controllers as its children and can access them all.
	 * 
	 * Controllers in order of appearance in the User Interface (from top to bottom):
	 * HeaderController			-	(Contains buttons for adding timelines, save, load, ...)
	 * TimelineBarController 	-	(Contains the timelineBar.fxml and the seekerController and stopPointController)
	 * TimelineControllers 		-	(An array of every Timeline, that is: Every TimelineController)
	 */
	public class AdvancedScreenController implements FXMLController, SessionListener {

		//Child Controllers of advanceScreenController:
		private HeaderController headerController;
		private TimelineBarController timelineBarController;
		private ArrayList<TimelineController> timelineControllers;
		
		//The current session from modules
		private SessionModule currentSession;
		
		private double scrollBarPosition = 0;
		
		//Stages that are shown in sessionBuilder (pop up windows):
		private Stage modalDialog;
		private Stage windowChooser; // stage for the windowChooser


		//Scaling
		private int scaleCoefficient = 1;
		private int maxScale = 10;
		private int minScale = 1;
		private double scrollBarDefaultValue = 0;

		//FXML Loader and RootPanes
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
		private SeekerPopupController seekerPopup = null;

		// Pointers to the fx:id in the FXML
		@FXML private VBox timelineContainer;
		@FXML private Button testButton;
		@FXML private ListView fileListView;
		@FXML private ScrollBar timelineLineScrollBar;
		@FXML private Label timelineBarGlobalTime;
		@FXML private ScrollPane timelineScrollPane;

		// Hashmap over the timelineIDs from the modules and the timelineControllers of the GUI
		private HashMap<TimelineController, Integer> idTimlineControllerMap;

		//The Canvas that the small timelineBar lines are drawn upon.
		//Its made in the advanceScreenController to draw it on top of the timelineBar
		private Canvas timelineBarCanvas = new Canvas(1000, 25);
		GraphicsContext gc = timelineBarCanvas.getGraphicsContext2D();

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


			//get current session
			currentSession=MainModuleController.getInstance().getSession();
			currentSession.addListener(this);

			//Creates the timelineBar above the timelines
			timelineBarController = new TimelineBarController(this);
			timelineBarContainer.getChildren().add(timelineBarCanvas);
			timelineBarCanvas.setLayoutX(0);
			timelineBarCanvas.setLayoutY(0);
			paintTimelineBarCanvas();
			timelineBarContainer.getChildren().add(timelineBarController);

			//Displays global time to the userinterface
			timelineBarGlobalTime.setText("Time: " + getTimeAsText(currentSession.getGlobalTime()));


			//initializeScrollPane
			initializeTimelineScrollPane();
			
			//InitializeScrollBar values
			initializeScrollBar();

			//Drag&drop functionality
			initializeDrag();

			//Seeker Popup
			initializeSeekerPopup();

			// Initialize the header
			initHeader(this);

			// Instanciated the hashmap over controllers
			idTimlineControllerMap = new HashMap<TimelineController, Integer>();

		}
		
		/**
		 * Initializes the ScrollPane that the timelines will be inside
		 */
		private void initializeTimelineScrollPane(){
			
			timelineScrollPane.addEventFilter(KeyEvent.ANY,new EventHandler<KeyEvent>() {
		        @Override
		        public void handle(KeyEvent event) {
		        	//Consumes the event if the arrowKeys are pressed. 
		        	//This is to prevent the user from manually scrolling the scrollPane left and right
		        	//TODO: This does not prevent touchPads from scrolling left and right, need a different solution.
		        	KeyCode keyCode = event.getCode();
		        	if(keyCode==KeyCode.UP || keyCode==KeyCode.DOWN || keyCode==KeyCode.LEFT || keyCode==KeyCode.RIGHT){
		        		event.consume();
		        	}

		        }
		    });
		}

		/**
		 * initializes the Scroll bar and its listener
		 * The listener moves the timelineLines.
		 */
		private void initializeScrollBar() {

			timelineLineScrollBar.setMax(currentSession.getSessionLength() - 1000);
			timelineLineScrollBar.valueProperty().addListener(new ChangeListener<Number>() {
				public void changed(ObservableValue<? extends Number> ov,
						Number old_val, Number new_val) {
					scrollBarDefaultValue = new_val.doubleValue()/scaleCoefficient;
					scrollBarPosition = -new_val.doubleValue();
					updateTimelinesPosition();
				}
			});
			timelineLineScrollBar.setUnitIncrement(20);
			timelineLineScrollBar.setBlockIncrement(20);

		}



		/**
		 * Initializes a dummy icon that will be displayed when dragging across panes.
		 */
		private void initializeDrag(){

			mDragOverIcon = new MediaObjectIcon(null);
			mDragOverIcon.setVisible(false);
			mDragOverIcon.setOpacity(0.65);
			rootPane.getChildren().add(mDragOverIcon);

			buildDragHandlers();
		}

		/**
		 * Initializes a popup that will be displayed when dragging the seeker.
		 * This popup shows the seekers current position.
		 */
		private void initializeSeekerPopup(){

			seekerPopup = new SeekerPopupController(timelineBarController.getSeeker());
			seekerPopup.setVisible(false);
			rootPane.getChildren().add(seekerPopup);

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
					
					for (FXMLController timelineController : timelineControllers) {
						((TimelineController)timelineController).getTimelineLineController().setRootOnDropped(mIconDragDropped);
					}

					// get a reference to the clicked DragIcon object
					MediaObjectIcon icn = (MediaObjectIcon) event.getSource();


					//Sets the type of the drag icon based on the icon of the triggering MediaObject (Video/Sound)
					//Here one can alternatively set text that should be displayed on the icon while you drag it.
					mDragOverIcon.setType(icn.getType());


					//Moves the icon to the point where you start the drag event
					mDragOverIcon.relocateToPoint(new Point2D (event.getSceneX(), event.getSceneY()));

					//Creates a javafx clipboard and creates a new DragContainer for the mediaObject
					ClipboardContent content = new ClipboardContent();
					MediaObjectContainer container = new MediaObjectContainer();

					//All advanced information about the MediaObject is added here
					//We simply add the entire mediaObject model
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
					//System.out.println("[AdvancedScreen] Dargging over root");

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
									String result=currentSession.addMediaObjectToTimeline(container.getValue("model"), timelineController.getTimelineModel() , ((int)p.getX()*1000)/scaleCoefficient);
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
		 * Adds a new timeline to the advancedScreen by running it through the sessionModule
		 */
		public void addTimeline(){
			int timelineInt = currentSession.addTimeline();
			System.out.println(timelineInt);
			System.out.println("Total number of timelines: "+ currentSession.getTimelines().size());
		}

		public void duplicateTimeline(TimelineModel tlm) {
			int timelineInt = currentSession.addTimeline();
			boolean success = currentSession.duplicateToTimeline(tlm, timelineInt);
			if(!success){
				System.out.println("[AdvancedScreen.duplicateTimeline] Could not duplicate the contents to the new timeline");
			}
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
		 * Removes the given timeline from the current session
		 * @param timelineController
		 */
		public void removeTimeline(TimelineController timelineController){
			currentSession.removeTimeline(idTimlineControllerMap.get(timelineController));
		}


		/**
		 * Initializes the HeaderController
		 * @param self
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
				tempTimeController.getTimelineLineController().getRoot().setPrefWidth(currentSession.getSessionLength()*scaleCoefficient);
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
				System.out.println("ORDERING");
				removeAllTimelineControllersFromScreen();
				ArrayList<Integer> orderedListOfTimelines = currentSession.getTimelineOrder();
				System.out.println("NEWORDER: " + orderedListOfTimelines);

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
			Scene dialogScene = new Scene(mediaObjectModal.getRoot(), 610, 230);
			modalDialog.setScene(dialogScene);
			modalDialog.show();
		}

		public void closeModal(){
			if(modalDialog != null){
				modalDialog.close();
			}
		}

		/**
		 * Shows the user a popup with the available windows that can be controlled
		 */
		public void showWindowChooser(){
			windowChooser = new Stage();
			windowChooser.setResizable(false);
			windowChooser.initModality(Modality.APPLICATION_MODAL);
			windowChooser.initOwner(MainGUIController.getInstance().primaryStage);
			WindowChooserController windowsChooser = new WindowChooserController(this);
			Scene dialogScene = new Scene(windowsChooser.getRoot(), 300, 200);
			windowChooser.setScene(dialogScene);
			windowChooser.show();
		}
		
		public void showWindowChooser(MediaObject mo){
			windowChooser = new Stage();
			windowChooser.setResizable(false);
			windowChooser.initModality(Modality.APPLICATION_MODAL);
			windowChooser.initOwner(MainGUIController.getInstance().primaryStage);
			WindowChooserController windowsChooser = new WindowChooserController(this, mo);
			Scene dialogScene = new Scene(windowsChooser.getRoot(), 300, 200);
			windowChooser.setScene(dialogScene);
			windowChooser.show();
		}
		
		public void closeWindowChooser(){
			if(windowChooser != null){
				windowChooser.close();
			}
		}
		public ArrayList<String> getWindows() {
			return currentSession.getAvailableWindows();
		}

		/**
		 * @return the scrollBarPosition
		 */
		public double getScrollBarPosition() {
			return scrollBarPosition;
		}

		/**
		 * Refreshes the scrollBar size based on the scale (usually ran when the scale has changed)
		 * It also moves the scrollparPosition to fit the new scale.
		 */
		public void refreshScrollBarSize() {
			//These three values are only used if one want to zoom without zooming inn on the seeker position.
			double oldScrollBarMax = timelineLineScrollBar.getMax();
			double oldScrollBarPos = timelineLineScrollBar.getValue();
			double scrollBarValueScaleCoeff = 0;

			timelineLineScrollBar.setUnitIncrement(20*scaleCoefficient);
			timelineLineScrollBar.setBlockIncrement(20*scaleCoefficient);

			timelineLineScrollBar.setMax((currentSession.getSessionLength()*scaleCoefficient) - 1000);
			scrollBarValueScaleCoeff = timelineLineScrollBar.getMax()/oldScrollBarMax;

			//timelineLineScrollBar.setValue(oldScrollBarPos*scrollBarValueScaleCoeff); //Zoom on timeline without seeker
			timelineLineScrollBar.setValue(timelineBarController.getSeeker().getSeekerPositionMiddle());
			System.out.println("[Advanced Screen] SEEKER POSITION: "+ timelineBarController.getSeeker().getSeekerPositionMiddle());
			updateTimelinesPosition();
		}

		private void updateTimelinesPosition(){
			for (TimelineController timelineController : timelineControllers) {

				timelineController.getTimelineLineController().moveTimeline(scrollBarPosition);
			}
			timelineBarController.moveTimelineBar(scrollBarPosition);
		}

		public void identifyDisplays(){
			MainModuleController.getInstance().getVLCController().identifyDisplays();
		}



		@Override
		public void fireGlobalTimeChanged(long newGlobalTime) {
			timelineBarController.getSeeker().placeSeeker(newGlobalTime);
			Platform.runLater(new Runnable() {
				@Override public void run() {
					timelineBarGlobalTime.setText("Time: " + getTimeAsText(newGlobalTime));                      
				}
			});

			//            System.out.println(newGlobalTime);

		}

		public long getGlobalTime(){
			return currentSession.getGlobalTime();
		}

		public void playAllTimelines(){
			currentSession.playAll();
		}

		public void pauseAllTimelines(){
			currentSession.pauseAll();
		}

		public int getScaleCoefficient(){
			return scaleCoefficient;
		}

		public void increaseScale(){
			if( scaleCoefficient < maxScale){
				scaleCoefficient *= 10;
				fireScaleChanged();
			}
		}

		public void decreaseScale(){
			if( scaleCoefficient > minScale){
				scaleCoefficient /= 10;
				fireScaleChanged();
			}
		}

		private void fireScaleChanged(){
			repaintTimelineBarCanvas();
			timelineBarController.scaleChanged();
			refreshScrollBarSize();
			for (TimelineController timelineController : timelineControllers) {
				timelineController.getTimelineLineController().getRoot().setPrefWidth(currentSession.getSessionLength()*scaleCoefficient);
				timelineController.getTimelineLineController().repaint();
			}
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



		public void assignRequest(String display, TimelineModel tlm) {

			//            if(checkedItems.size()>tlm.getAssignedDisplays().size()){
			//                System.out.println("New display!");
			//                //NB: only possible with one screen per timeline. Address problem with ghosts
			//                currentSession.assignTimeline(Integer.parseInt(checkedItems.get(0)), tlm);
			//            }
			//            else if(checkedItems.size()<tlm.getAssignedDisplays().size()){
			//                //TODO: implement the removal of screen if box is unchecked
			//                System.out.println("RemovedScreen!");
			//            }
			//            else if(checkedItems.size()==tlm.getAssignedDisplays().size()){
			//                for(String dispString:checkedItems){
			//                    if(!tlm.getAssignedDisplays().contains(Integer.parseInt(dispString))){
			//                        System.out.println("NEW DISPLAY!?");
			//                    }
			//                }
			//            }
			currentSession.assignTimeline(Integer.parseInt(display), tlm);

		}
		public void removeAssignRequest(TimelineModel tlm){
			currentSession.unassignTimeline(tlm);
		}


		/**
		 * Repaints the numbers on the canvas when you rescale.
		 */
		public void repaintTimelineBarCanvas(){
			//NB! Clearing only top rect of canvas to reduce lag
			gc.clearRect(0, 0, 200, 12);
			if(scaleCoefficient == 1){
				gc.fillText("1 min", 72+1, 12);
			}else if(scaleCoefficient == 10) {
				gc.fillText("6 sec", 72+1, 12);
			}
		}
		
		/**
		 * Paints the timelineBar lines and numbers according to scale
		 * NB! A canvas cannot be very large (max 4k)
		 * x = 12 because the timelinebar is 11 pixels wider than the timelines itself. So 12 is the first visible pixel of the timelinebar.
		 * and 0.5 is added to x1 before line is drawn (seeker center is 12.5 when the seeker is 25 pixels wide).
		 * So essensialy pixel nr 13 is where the line will be drawn, but software wise its 12.5.
		 */
		public void paintTimelineBarCanvas(){

			gc.setLineWidth(1.0);
			int k = 5;
			for (int x = 12; x < 1000; x+=10) {
				double x1 = 0;
				x1 = x;
				gc.setFont(new Font(8));
				if(k == 5){
					k = 0;
					if(x == 72){
						if(scaleCoefficient == 1){
							gc.fillText("1 min", x1+1, 12);
						}else if(scaleCoefficient == 10) {
							gc.fillText("6 sec", x1+1, 12);
						}
					}
					x1 = x + 0.5;
					gc.moveTo(x1, 25);
					gc.lineTo(x1, 15);
					gc.stroke();
				}else if(k == 2){
					k += 1;
					x1 = x + 0.5;
					gc.moveTo(x1, 25);
					gc.lineTo(x1, 16);
					gc.stroke();
				}else{
					k += 1;
					x1 = x + 0.5;
					gc.moveTo(x1, 25);
					gc.lineTo(x1, 18);
					gc.stroke();
				}
				//			            gc.strokeText("1", x1, 12);
			}

/**This is an attempt to redraw the lines and numbers (but its not complete), 
*so that when the user moves the scrollbar/seeker it would look like the timlinebar lines moves aswell.
*The issue was that drawing the canvas this many times over a short period was very resource consuming, and we had to let it go.
**/
			//				root.getChildren().removeAll(currentListOfCanvases);
			//				currentListOfCanvases.clear();
			//				//11 is the added length because the bar needs to be centered when starting and stopping
			//				long barScaledSessionLength = (parentController.getCurrentSession().getSessionLength()*parentController.getScaleCoefficient())+11;
			//				long widthBetweenLines = 10;
			//
			//				//Canvas are not scalable by default and can only have a length of 2-4k so we make 2k canvases and draws upon them.
			//				for (int i = 0; i < Math.ceil(barScaledSessionLength/2000); i++) {
			//					Canvas timelineBarCanvas = new Canvas(2000, 25);
			//					root.getChildren().add(timelineBarCanvas);
			//					currentListOfCanvases.add(timelineBarCanvas);
			//				}
			//				for (Canvas canvas : currentListOfCanvases) {
			//					GraphicsContext gc = canvas.getGraphicsContext2D();
			//					gc.setLineWidth(1.0);
			//					//x = 12 to start the first line ontop of the center of the seeker
			//				     for (int x = 12; x < barScaledSessionLength; x+=widthBetweenLines) {
			//				            double x1 ;
			//				            x1 = x + 0.5; //TODO: The 0.5 is to get a clean (not blurry) line, but it might mean that x width should be +1 more pixel
			//				            gc.moveTo(x1, 25);
			//				            gc.lineTo(x1, 15);
			//				            gc.stroke();
			//				            gc.setFont(new Font(8));
			//				            gc.fillText("1", x1+1, 12);
			////				            gc.strokeText("1", x1, 12);
			//				        }
			//				}



		}

		/* (non-Javadoc)
		 * @see gui.SessionListener#fireSessionLenghtChanged()
		 */
		@Override
		public void fireSessionLenghtChanged() {
			timelineBarController.getRoot().setPrefWidth((currentSession.getSessionLength()*scaleCoefficient)+22);
			refreshScrollBarSize();
			for (TimelineController timelineController : timelineControllers) {
				timelineController.getTimelineLineController().getRoot().setPrefWidth(currentSession.getSessionLength()*scaleCoefficient);
			}
		}

		/**
		 * Rebuilds the timelines. Used when changing the names of mediaobjects.
		 * TODO: Only go through the labels and update them. This implementation is highly unnecessary.
		 */
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


		private String getTimeAsText(long time){
			long timeInSeconds = time/1000;
			long hours = timeInSeconds/3600;
			long minutes = (timeInSeconds % 3600) / 60;
			long seconds = timeInSeconds % 60;
			long milliseconds = time%1000;
			return String.format("%02dh : %02dm : %02ds : %02dms", hours, minutes, seconds,milliseconds);
		}

		public void setSeekerPopoupVisibility(boolean visibility){
			seekerPopup.setVisible(visibility);
		}
		public void moveSeekerPopup(Point2D moveToThisPoint){
			seekerPopup.relocate(moveToThisPoint.getX(), moveToThisPoint.getY());
			seekerPopup.updateText();
		}

		public void ifSeekerIsOutsideThenScroll(Point2D checkThisPoint){
			int unitsMoved = 10;
			//If the seeker is going outside the left side
			if(checkThisPoint.getX() <= 199 && scrollBarPosition < 0){
				System.out.println("NR1");
				if(-scrollBarPosition - unitsMoved > 0){
					System.out.println("lOLnr1");
					scrollBarPosition += unitsMoved;
					timelineLineScrollBar.setValue(-scrollBarPosition);
				}else{ 
					//If pointer is below zero, then zero is set as the minimum value
					scrollBarPosition = 0;
					timelineLineScrollBar.setValue(-scrollBarPosition);
				}
				updateTimelinesPosition();

			}else if(checkThisPoint.getX() > 1188 && -scrollBarPosition < timelineLineScrollBar.getMax()){
				System.out.println("NR3");
				if(-scrollBarPosition + unitsMoved < timelineLineScrollBar.getMax()+200){
					scrollBarPosition -= unitsMoved;
					timelineLineScrollBar.setValue(-scrollBarPosition);
				}
				updateTimelinesPosition();

			}
		}


		public void moveTimeline(String direction, TimelineModel timelineModel) {
			currentSession.moveTimeline(direction,timelineModel);
			
		}

		public void fireTimelinebarChanged() {
			this.timelineBarController.removeAllBreakpoints();
			this.timelineBarController.repaint();
		}


	}//end AdvancedScreenController




}