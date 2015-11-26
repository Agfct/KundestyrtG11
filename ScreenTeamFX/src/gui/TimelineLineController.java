package gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import gui.AdvancedScreen.AdvancedScreenController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import modules.MainModuleController;
import modules.TimelineMediaObject;
import modules.TimelineModel;

/**
 *
 * @author Anders Lunde, Magnus Gundersen
 * The TimelineLineController is the controller of the line you see at the right side, 
 * containing all the media objects.
 * It handles the repainting of all the MediaObjects when a mediaObject changes
 */
public class TimelineLineController implements FXMLController{


    private FXMLLoader fxmlLoader;
    private TimelineController parentController;
    private AnchorPane rootPane;
    private final ContextMenu contextMenu = new ContextMenu();

    private ArrayList<MediaObjectController> mediaObjectControllers;
    private ArrayList<TimelineMediaObject> timelineMediaObjectModels;
    private HashMap<TimelineMediaObject,MediaObjectController> mediaObjectToControllerMap;

    /**
     *
     * @param parentController
     */
    public TimelineLineController(TimelineController parentController){

        //Fetches the parent controller. In this case it is the timelineController who has this timelinelinecontroller
        this.parentController = parentController;

        //initializes empty mediaObjectList
        mediaObjectControllers = new ArrayList<>();
        mediaObjectToControllerMap = new HashMap<TimelineMediaObject, MediaObjectController>();

        // The constructor will try to fetch the fxml
        try {
            fxmlLoader = new FXMLLoader(getClass().getResource("TimelineLine.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.load();
            rootPane = fxmlLoader.getRoot();
        } catch (IOException e) {
            System.out.println("Failed to load TimelineLine FXML");
            e.printStackTrace();
        }


        rootPane.setPrefWidth(MainModuleController.getInstance().getSession().getSessionLength());

        //Mouse Functionallity
        initializeMouse();

        //Puts this TimelineLine onto the info (parent) controller
//		parentController.timelineContainer.add(this.rootPane, 1, 0);
        parentController.timelineLineContainer.getChildren().add(this.rootPane);

        timelineMediaObjectModels= new ArrayList<TimelineMediaObject>();


    }

    /**
     * Initializes all the mouse gesture controls, and also initializes the right click pop up menu.
     */
    private void initializeMouse(){
        //Adds different right click options to the ContextMenu that pops up on mouse click.
        MenuItem duplicate = new MenuItem("Duplicate");
        contextMenu.getItems().addAll(duplicate);
        duplicate.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                AdvancedScreenController asc = AdvancedScreen.getInstance().getScreenController();
                TimelineModel tlm = parentController.getTimelineModel();
                asc.duplicateTimeline(tlm);
            }
        });
        rootPane.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                contextMenu.hide();
                MouseButton button = event.getButton();
                if(button==MouseButton.PRIMARY){
//                    System.out.println("PRIMARY button clicked");
                }else if(button==MouseButton.SECONDARY){
                    contextMenu.show(rootPane, event.getScreenX(), event.getScreenY()); // this brings up the "duplicate" option
                }else if(button==MouseButton.MIDDLE){
//                    System.out.println("MIDDLE button clicked");
                }
                event.consume(); //Consumes the event so it wont go deeper down into the hierarchy 
            }
        });

    }


    /* (non-Javadoc)
     * @see gui.FXMLController#getFXMLLoader()
     */
    @Override
    public FXMLLoader getFXMLLoader() {
        return fxmlLoader;
    }

    public void setRootOnDragOver( EventHandler<DragEvent> event){
        rootPane.setOnDragOver(event);
    }

    public void setRootOnDropped( EventHandler<DragEvent> event){
        rootPane.setOnDragDropped(event);
    }

    public AnchorPane getRoot(){
        return rootPane;
    }

    public ContextMenu getContextMenu(){
        return contextMenu;
    }

    public TimelineController getParentController(){
        return parentController;
    }

    /**
     * Receives and add/places a mediaObject to the timelineLine based on the coordinates p
     * @param node
     * @param p
     */
    public void addMediaObject(MediaObjectController node, Point2D p) {
        rootPane.getChildren().add(node);
        mediaObjectControllers.add(node);
        node.setParentController(this);
        node.relocateToPoint(p);
        mediaObjectToControllerMap.put(node.getTimelineMediaObject(), node);


    }

    public void removeMediaObject(MediaObjectController node) {
        rootPane.getChildren().remove(node);
        mediaObjectControllers.remove(node);
        mediaObjectToControllerMap.remove(node);

    }

    protected void moveTimeline(Double newPosition){
        rootPane.setLayoutX(newPosition);
    }
    /**
     * This method repaints the timelineMediaObjects that is currently on the timelineLine
     * 
     * This method has other posible implementations:
     * 1. In the TimelineChanged changeType Enum we can add a parameter that tell the timelineLine what the change was. T
     * 	  This must be propagated all the way from the timelineModel in the modules. 
     * 2. Repaint the whole thing each time. This will cause problems with the drag and drop
     */
    public void repaint() {
    	//Gets the new list of timelineMediaObjects from the model
        TimelineModel model=parentController.getTimelineModel();
        ArrayList<TimelineMediaObject> newListOfTimelineMediaObject=model.getTimelineMediaObjects();
        
        //Checks if the number of timelineMediaOBject is the same
        //This means that they have only been changed, which again means that we have to repaint the existing mediaObjectControllers
        if(newListOfTimelineMediaObject.size()==timelineMediaObjectModels.size()){
        	//removes and adds the mediaObjectControllers currently on the timeline
            for(MediaObjectController mediaObjectController:mediaObjectControllers){
                mediaObjectController.updateValuesFromModel();
                rootPane.getChildren().remove(mediaObjectController);
                rootPane.getChildren().add(mediaObjectController);
                mediaObjectController.relocateToPoint(new Point2D((mediaObjectController.getTimelineMediaObject().getStart()*AdvancedScreen.getInstance().getScreenController().getScaleCoefficient())/1000,0));
            }

        }
        // Checks if the new size is bigger. This means that an element has been added
        else if(newListOfTimelineMediaObject.size()>timelineMediaObjectModels.size()){
        	//gets the difference, namely the new object
            ArrayList<TimelineMediaObject> difference= new ArrayList<>();  
            difference.addAll(newListOfTimelineMediaObject);
            difference.removeAll(timelineMediaObjectModels);
            
            
            //Paints the new one
            for(TimelineMediaObject tlmo:difference){
                MediaObjectController mediaObjectController = new MediaObjectController(tlmo);
                mediaObjectController.initializeMediaObject();
                addMediaObject(mediaObjectController, new Point2D((tlmo.getStart()*AdvancedScreen.getInstance().getScreenController().getScaleCoefficient())/1000, 0));

            }
            
            //clear all and adds all
            timelineMediaObjectModels.clear();
            timelineMediaObjectModels.addAll(newListOfTimelineMediaObject);

        }
        
        
        //This means an object has been removed. 
        else if(newListOfTimelineMediaObject.size()<timelineMediaObjectModels.size()){
        	//finds the removed object
            ArrayList<TimelineMediaObject> difference= new ArrayList<>();
            difference.addAll(timelineMediaObjectModels);
            difference.removeAll(newListOfTimelineMediaObject);
            
            //removes it from the timeline
            for(TimelineMediaObject tlmo:difference){
                MediaObjectController mediaObjectController = mediaObjectToControllerMap.get(tlmo);
                removeMediaObject(mediaObjectController);

            }
            timelineMediaObjectModels.clear();
            timelineMediaObjectModels.addAll(newListOfTimelineMediaObject);

        }
    }
}