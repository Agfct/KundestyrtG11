/**
 * 
 */
package gui;

import java.io.IOException;

import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;


/**
 * @author Anders Lunde
 * The seeker controller = the seeker, and it controls the seeker.fxml and handles the drag and drop of the seeker.
 * The size of the seeker is based on the seeker.fxml sizes and is currently 25x25 pixels.
 */
public class SeekerController extends Pane{
	private FXMLLoader fxmlLoader;
	private TimelineBarController parentController;
	private Pane root = this;
	
	//scaling
	private int scale = 1;
	
	/*Hardcoded values
	Why hardcoded ? Because the values are not set until after the fxml controller is created, 
	so to enable us to do things with the width and size of the seeker straight away we presume (as set in the seeker.fxml) that the size is 25x25
	We set the half size to 12 in order to make the position of the line that goes downwards the middle at 12.5
	12.5 does not go well with pixels since we cannot make something that is 12.5 pixels show.*/
	private final int HALFSIZE = 12;
	private final double FULLSIZE = 25;
	
	//The time in the modules is in miliseconds (int value) so we need to divide and multiply by 1000 to scale things right.
	private final int milisecs = 1000;
	
	private Canvas seekerLine;
	private GraphicsContext seekergc;
	private int seekerLineHeight = 1000;

	//Dragging:
	private EventHandler <DragEvent> mContextDragOver;
	private EventHandler <DragEvent> mContextDragDone;
	private EventHandler <DragEvent> mContextDragDropped;
	private Point2D mDragOffset = new Point2D (0.0, 0.0);

	public SeekerController(TimelineBarController parent){
		parentController = parent;
		
		try {
			fxmlLoader = new FXMLLoader(getClass().getResource("Seeker.fxml"));
			fxmlLoader.setController(this);
			fxmlLoader.setRoot(this);
			fxmlLoader.load();
		} catch (IOException e) {
			System.out.println("Failed to load Seeker FXML");
			e.printStackTrace();
		}

		initialize();
	}


	/**
	 * Initializes the drag and drop functionality, runs the initializer for creating the seeker line and then draws the line.
	 */
	public void initialize() {
		buildNodeDragHandlers();
		initializeSeeker();
		drawSeekerLine();
	}

	/**
	 * Adds the seeker line to the root node of this controller. 
	 * The seekerLine is the black line covering the timelines, to pinpoint the middle of the seeker.
	 */
	public void initializeSeeker(){
		seekerLine = new Canvas(2,0);
		root.getChildren().add(seekerLine);
		seekerLine.toFront();
		//Centers the line (the line is 2 pixels long so it starts at 11 and ends at 12)
		seekerLine.relocate(11,0);
	}
	public void drawSeekerLine(){
		seekerLine.setHeight(seekerLineHeight);
		seekergc = seekerLine.getGraphicsContext2D() ;
		seekergc.setLineWidth(1.0);
		//We draw the line at 1+0.5 which is essentially 12.5)
		seekergc.moveTo(1+0.5, FULLSIZE);
		seekergc.lineTo(1+0.5, seekerLineHeight);
		seekergc.stroke();
	}

	public void relocateToPoint (Point2D p) {

		//relocates the object to a point that has been converted to
		//scene coordinates
		relocate ( 
				(int) (p.getX()),
				(int) (p.getY())
				);
	}

	/**
	 * Used by TimelineBarController when rightClicked and the user chooses to "move seeker to the right clicked position".
	 * @param pointX
	 */
	public void moveTo(Point2D pointX){
		System.out.println("Point: " + pointX);
		//Moves by -HALFSIZE to set the pointer to the middle
		relocateToPoint(new Point2D(pointX.getX()- HALFSIZE,0));
		seekerChanged();
	}
	
	public double getSeekerPosition(){
		return localToParent(0,FULLSIZE).getX();
	}
	
	/**
	 * This methods is used by the scrollbar to get the position of the seeker when zooming in and out.
	 * If the position of the seeker is less than 500 (close to the start) it simply returns 0 putting the scrollbar to zero.
	 * If the seeker is at the end of the session (the width of the timelinebar -500) it sets it to the max -500.
	 * Why 500 ? Because the visible clip size is currently 1000 (the part of the timline and timelinebar that the user can see) 
	 * and -500 is the center of this.
	 * @return
	 */
	public double getSeekerPositionMiddle(){
		double tempPos = getSeekerPosition();
		if(tempPos < 500){
			return 0;
		}else if (tempPos > 500 && tempPos < parentController.getRoot().getPrefWidth()-500){
			return tempPos - 500;
		}
		return parentController.getRoot().getPrefWidth()-1022;
	}
	
	/**
	 * Builds all the different drag handlers.
	 */
	public void buildNodeDragHandlers() {



		//This is the handler for handling dragging the Seeker
		mContextDragOver = new EventHandler <DragEvent>() {

			@Override
			public void handle(DragEvent event) {		
//				System.out.println("[Seeker] Dragging over root");
				Pane timelineBarController = parentController.getRoot();
				Point2D p = timelineBarController.sceneToLocal(event.getSceneX(), event.getSceneY());
				Bounds mediaControllerRect;
				if(p.getX() - mDragOffset.getX() >= 0){
					mediaControllerRect = new BoundingBox(p.getX() - mDragOffset.getX(),0,
							FULLSIZE, FULLSIZE);
				}else{
					mediaControllerRect = new BoundingBox(0,0,
							FULLSIZE, FULLSIZE);
				}

				if (timelineBarController.getBoundsInLocal().contains(mediaControllerRect)) {
					event.acceptTransferModes(TransferMode.MOVE);
					relocateToPoint(new Point2D(mediaControllerRect.getMinX(),mediaControllerRect.getMinY()));
					AdvancedScreen.getInstance().getScreenController().moveSeekerPopup(localToScene(0,FULLSIZE));
					AdvancedScreen.getInstance().getScreenController().ifSeekerIsOutsideThenScroll(localToScene(0,FULLSIZE));
				}
				event.consume();
			}
		};

		
		//This is the handler for handling dropping of the seeker
		mContextDragDropped = new EventHandler <DragEvent> () {

			@Override
			public void handle(DragEvent event) {

				event.setDropCompleted(true);

				event.consume();
			}
		};

		//This method handles the end of the drag event (after the seeker is dropped and you want things to happen)
		mContextDragDone = new EventHandler <DragEvent> () {

			@Override
			public void handle (DragEvent event) {
				System.out.println("[SeekerController] Drag DONE X is: " + localToParent(0,FULLSIZE).getX()/scale);

				parentController.getRoot().removeEventHandler(DragEvent.DRAG_OVER, mContextDragOver);
				parentController.getRoot().setOnDragOver(null);
				AdvancedScreen.getInstance().getScreenController().getMasterRoot().removeEventHandler(DragEvent.DRAG_OVER, mContextDragOver);
				AdvancedScreen.getInstance().getScreenController().getMasterRoot().setOnDragOver(null);
				parentController.getRoot().setOnDragDropped(null);
				parentController.getRoot().setOnDragDone(null);
				
				//Hiding the seekerPopup
				AdvancedScreen.getInstance().getScreenController().setSeekerPopoupVisibility(false);

				seekerChanged();

			}
		};

		/**
		 * When you drag the MediaObject it triggers setOnDragDetected
		 */
		setOnDragDetected ( new EventHandler <MouseEvent> () {

			@Override
			public void handle(MouseEvent event) {

				/* Drag was detected, start a drag-and-drop gesture */
				/* allow any transfer mode */
				System.out.println("[SeekerController] Drag event started");

				AdvancedScreen.getInstance().getScreenController().getMasterRoot().setOnDragOver (mContextDragOver);

				parentController.getRoot().setOnDragOver (mContextDragOver);
				parentController.getRoot().setOnDragDropped (mContextDragDropped);
				parentController.getRoot().setOnDragDone(mContextDragDone);

				//begin drag ops
				mDragOffset = new Point2D(event.getX(), event.getY());

				//The clipboard contains all content that are to be transfered in the drag
				ClipboardContent content = new ClipboardContent();

				//creating a container with all the data of the media object
				MediaObjectContainer container = new MediaObjectContainer();			

				//Putting the data container onto the content
				content.put(MediaObjectContainer.DragNode, container);
				
				//Showing the seekerPopup to display temp globaltime
				AdvancedScreen.getInstance().getScreenController().setSeekerPopoupVisibility(true);
				
				//Pauses all timelines when you start to drag
				AdvancedScreen.getInstance().getScreenController().pauseAllTimelines();
				
				startDragAndDrop (TransferMode.MOVE).setContent(content);      

				event.consume();					
			}

		});

	}
	
	private void seekerChanged(){
		AdvancedScreen.getInstance().getScreenController().changeGlobalTime(getTempGlobalTime());
	}
	
	public long getTempGlobalTime(){
		return (long)((localToParent(0,FULLSIZE).getX()/scale)*milisecs);
	}
	public void placeSeeker( long newGlobalTime){
		root.setLayoutX((newGlobalTime*scale)/milisecs);
	}
	
	/**
	 * When the scale changes we move the seeker to the new scaled location
	 * @param newScale
	 */
	public void scaleChanged(int newScale){
		System.out.println("seeker: scalechanged");
		this.scale = newScale;
		root.setLayoutX((AdvancedScreen.getInstance().getScreenController().getGlobalTime()*scale)/milisecs);
	}
}
