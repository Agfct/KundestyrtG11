/**
 * 
 */
package gui;

import java.io.IOException;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;


/**
 * @author Anders Lunde
 *
 */
public class SeekerController extends Pane{
	private FXMLLoader fxmlLoader;
	private TimelineBarController parentController;
	private Pane root = this;

	private Canvas seekerLine;
	private GraphicsContext seekergc;

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

	}


	/**
	 * This method is ran when the object is initialized (created) i the FXML
	 */
	@FXML
	private void initialize() {
		buildNodeDragHandlers();
		initializeSeeker();
	}

	/**
	 * Adds the seeker line to the root node of the advancedScreeen
	 */
	public void initializeSeeker(){
		seekerLine = new Canvas();
		parentController.getAdvancedScreenController().getMasterRoot().getChildren().add(seekerLine);
		seekerLine.toFront();
	}

	public void relocateToPoint (Point2D p) {

		//relocates the object to a point that has been converted to
		//scene coordinates
		relocate ( 
				(int) (p.getX()),
				(int) (p.getY())
				);
	}

	public void buildNodeDragHandlers() {


		/**
		 * This is the method for handling dragging the Seeker
		 */
		mContextDragOver = new EventHandler <DragEvent>() {

			@Override
			public void handle(DragEvent event) {		
				System.out.println("[Seeker] Dragging over root");

				Pane timelineBarController = parentController.getRoot();
				Point2D p = timelineBarController.sceneToLocal(event.getSceneX(), event.getSceneY());
				//				System.out.println("AnchorPane sceneToLocal: "+ p);

				//				System.out.println("[MediaObject] sceneX: "+event.getSceneX()+" LocalX: "+ p.getX());
				//				System.out.println("[MediaObject] LocalX: "+p.getX()+" LocalX: "+ p.getY());
				//				System.out.println("[MediaObjectController] AnchtorPane Bounds: "+timelineLinePane.boundsInLocalProperty().get());

				//Prevents you from dragging outside timeline boundaries
				//				Bounds boundsInParent = getBoundsInParent();
				//				Bounds newBounds = new BoundingBox(p.getX() - mDragOffset.getX(),p.getY() - mDragOffset.getY(),
				//						(p.getX() - mDragOffset.getX()) + boundsInParent.getWidth(), (p.getY() - mDragOffset.getY()) + boundsInParent.getHeight());

				//p.getX() - mDragOffset.getX() is left corner of mediaObject in AnchorPane coordinates
				//So pX-dragX, pY-dragY is the top left corner of the mediaObject 
				//(at the current dragged position, we later check if it can be placed there)
				//				Bounds mediaControllerRect = new BoundingBox(p.getX() - mDragOffset.getX(),p.getY() - mDragOffset.getY(),
				//						getMediaObjectWidth(), getMediaObjectHeigth());
				Bounds mediaControllerRect;
				if(p.getX() - mDragOffset.getX() >= 0){
					mediaControllerRect = new BoundingBox(p.getX() - mDragOffset.getX(),0,
							25, 25);
				}else{
					mediaControllerRect = new BoundingBox(0,0,
							25, 25);
				}

				//				System.out.println("[MediaObjectController] NewBounds for MediaObject: " +mediaControllerRect);
				//				System.out.println("TimelineLinePane.getBoundsInLocal(): "+ timelineLinePane.getBoundsInLocal());
				if (timelineBarController.getBoundsInLocal().contains(mediaControllerRect)) {
					//					if (timelineLinePane.boundsInLocalProperty().get().intersects(root.getLayoutX(), root.getLayoutY(), root.getLayoutX() + root.getWidth(), root.getLayoutY()+ root.getHeight())) {
					event.acceptTransferModes(TransferMode.MOVE);
					//					relocateToPoint(new Point2D(event.getSceneX(), event.getSceneY()));
					relocateToPoint(new Point2D(mediaControllerRect.getMinX(),mediaControllerRect.getMinY()));
					//					relocateToPoint(new Point2D(event.getSceneX(), 0));
				}
				//				event.acceptTransferModes(TransferMode.ANY);				
				//				relocateToPoint(new Point2D( event.getSceneX(), event.getSceneY()));

				event.consume();
			}
		};

		/**
		 * This is the method for handling dropping of the MediaObject
		 */
		mContextDragDropped = new EventHandler <DragEvent> () {

			@Override
			public void handle(DragEvent event) {

				//				parentController.getRoot().setOnDragOver(null);
				//				parentController.getRoot().setOnDragDropped(null);
				//				
				event.setDropCompleted(true);

				event.consume();
			}
		};

		mContextDragDone = new EventHandler <DragEvent> () {

			@Override
			public void handle (DragEvent event) {
				System.out.println("[MediaObjectController] Drag DONE");

				parentController.getRoot().removeEventHandler(DragEvent.DRAG_OVER, mContextDragOver);
				parentController.getRoot().setOnDragOver(null);
				AdvancedScreen.getInstance().getScreenController().getMasterRoot().removeEventHandler(DragEvent.DRAG_OVER, mContextDragOver);
				AdvancedScreen.getInstance().getScreenController().getMasterRoot().setOnDragOver(null);
				parentController.getRoot().setOnDragDropped(null);
				parentController.getRoot().setOnDragDone(null);


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
				System.out.println("[MediaObjectController] Drag event started");

				AdvancedScreen.getInstance().getScreenController().getMasterRoot().setOnDragOver (mContextDragOver);

				parentController.getRoot().setOnDragOver (mContextDragOver);
				parentController.getRoot().setOnDragDropped (mContextDragDropped);
				parentController.getRoot().setOnDragDone(mContextDragDone);

				//begin drag ops
				mDragOffset = new Point2D(event.getX(), event.getY());
				System.out.println("dragOffset with getX: " + mDragOffset);

				//                relocateToPoint(
				//                		new Point2D(event.getSceneX(), event.getSceneY())
				//                		);

				//TODO: TEST STUFF:
				System.out.println("relocate to point with sceneX with getX: " + (new Point2D(event.getSceneX(), event.getSceneY())));
				Pane timelineLinePane = parentController.getRoot();
				System.out.println("AnchorPane: getBoundsInLocal " + timelineLinePane.getBoundsInLocal());
				System.out.println("Media Object: getBounds in parent" + getBoundsInParent());

				//The clipboard contains all content that are to be transfered in the drag
				ClipboardContent content = new ClipboardContent();

				//creating a container with all the data of the media object
				MediaObjectContainer container = new MediaObjectContainer();			
				//				container.addData ("type", timelineMediaObject.getParent().getType().toString());

				//Putting the data container onto the content
				content.put(MediaObjectContainer.DragNode, container); //TODO: AddNode ??

				startDragAndDrop (TransferMode.MOVE).setContent(content);      

				event.consume();					
			}

		});

	}
}
