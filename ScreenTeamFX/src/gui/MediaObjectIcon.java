/**
 * 
 */
package gui;

import java.io.IOException;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import modules.*;

/**
 * @author Anders Lunde
 * This is the icon that is being displayed when the media object is being dragged
 */
public class MediaObjectIcon extends GridPane{
	@FXML GridPane grid_pane;
	
	//Drag&Drop
	private EventHandler <DragEvent> mContextDragOver;
	private EventHandler <DragEvent> mContextDragDropped;
	private Point2D mDragOffset = new Point2D (0.0, 0.0);
	private MediaSourceType mType = null;
	
	private MediaObject mediaObject;

	public MediaObjectIcon() {
		
		FXMLLoader fxmlLoader = new FXMLLoader(
				getClass().getResource("MediaObjectIcon.fxml")
				);
		
		fxmlLoader.setRoot(this); 
		fxmlLoader.setController(this);
		
		try { 
			fxmlLoader.load();
        
		} catch (IOException exception) {
		    throw new RuntimeException(exception);
		}

		
	}
	/*
	 * Method for setting the title of the Icon
	 */
	public void setTitle(String s){
		this.add(new Label(s.substring(0, s.length()-Math.min(s.length(),5))),0,0);
	}
	
	@FXML
	private void initialize() {
		buildNodeDragHandlers();
	}
	public void setMediaObject(MediaObject mediaObject){
		//Check validity
		this.mediaObject=mediaObject;
	}

	public void buildNodeDragHandlers() {
		
	mContextDragOver = new EventHandler <DragEvent>() {

		//dragover to handle node dragging inside the timeline
		@Override
		public void handle(DragEvent event) {		
	
			event.acceptTransferModes(TransferMode.ANY);				
			relocateToPoint(new Point2D( event.getSceneX(), event.getSceneY()));

			event.consume();
		}
	};
	
	//dragdrop for node dragging
	mContextDragDropped = new EventHandler <DragEvent> () {

		@Override
		public void handle(DragEvent event) {
		
			getParent().setOnDragOver(null);
			getParent().setOnDragDropped(null);
			
			event.setDropCompleted(true);
			
			event.consume();
		}
	};
	//close button click
//	close_button.setOnMouseClicked( new EventHandler <MouseEvent> () {
//
//		@Override
//		public void handle(MouseEvent event) {
//			AnchorPane parent  = (AnchorPane) self.getParent();
//			parent.getChildren().remove(self);
//		}
//		
//	});
	
	//drag detection for node dragging
	//TODO: use like a title bar for dragging ? not dragging the whole ting ?
//	title_bar.setOnDragDetected ( new EventHandler <MouseEvent> () {
	setOnDragDetected ( new EventHandler <MouseEvent> () {

		@Override
		public void handle(MouseEvent event) {
		
			getParent().setOnDragOver(null);
			getParent().setOnDragDropped(null);

			getParent().setOnDragOver (mContextDragOver);
			getParent().setOnDragDropped (mContextDragDropped);

            //begin drag ops
            mDragOffset = new Point2D(event.getX(), event.getY());
            
            relocateToPoint(
            		new Point2D(event.getSceneX(), event.getSceneY())
            		);
            
            ClipboardContent content = new ClipboardContent();
			MediaObjectContainer container = new MediaObjectContainer();
			
			container.addData ("type", mType.toString());
			content.put(MediaObjectContainer.AddNode, container);
			
            startDragAndDrop(TransferMode.ANY).setContent(content);                
            
            event.consume();					
		}
		
	});		
}	
	
	public void relocateToPoint (Point2D p) {

	
		//relocates the object to a point that has been converted to
		//scene coordinates
		Point2D localCoords = getParent().sceneToLocal(p);
		
		relocate ( 
				(int) (localCoords.getX() - (getBoundsInLocal().getWidth() / 2)),
				(int) (localCoords.getY() - (getBoundsInLocal().getHeight() / 2))
			);
	}
	
	public MediaSourceType getType () { return mType; }
	
	public void setType (MediaSourceType type) {
		
		mType = type;
		
		getStyleClass().clear();
		getStyleClass().add("dragicon");

			if(mType == MediaSourceType.AUDIO){
				getStyleClass().add("icon-sound");
			}else{
				getStyleClass().add("icon-video");
			}
			
	}
}
