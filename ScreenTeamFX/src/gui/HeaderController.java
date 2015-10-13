/**
 * 
 */
package gui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import gui.AdvancedScreen.AdvancedScreenController;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import javafx.stage.FileChooser;


/**
 * @author Magnus Gundersen
 * This class is the controller of the GridPane that is the header of the advancedScreen
 * It includes the addTimeLineButton, the importMediaButton, the importedMediaView, prob and more. 
 */
public class HeaderController implements FXMLController{
	
	//list of all children controllers NB: May be empty, can possibly be removed
	private ArrayList<FXMLController> childControllers;
	
	// The standard FXML loader
	private FXMLLoader fxmlLoader;
	
	// The parent pane and controller (rootPane)
	private GridPane parentPane;  //might not need this?
	private AdvancedScreenController parentController;
	
	//Parent topGrid NB: Might not need this
//	GridPane topGrid;
	
	//Pointers to the fx:ids in the FXML
	@FXML private Button testButton;
	
	//The rootGrid that lies at the bottom of this FXML
	@FXML private GridPane rootPane;
	
	//The TilePane that has the mediaObjectIcons
	@FXML private TilePane mediaViewPane;
	
	
	
	
	//-----Graphical elements-------//
	//variable to keep track of the media files imported
	ObservableList<MediaObjectIcon> importedMediaObjects = FXCollections.observableArrayList();
	
	//Autoupdatable listproperty for use on the listview
	protected ListProperty<String> mediaObjectProperty = new SimpleListProperty<>();

	
	
	public HeaderController(AdvancedScreenController AdvParentController) {
		this.parentController=AdvParentController;
	
		
//		 Trying to fetch the FXML
		try {
			fxmlLoader = new FXMLLoader(getClass().getResource("Header.fxml"));
			fxmlLoader.setController(this);
			fxmlLoader.load();
			rootPane = fxmlLoader.getRoot();
		} catch (Exception e) {
			System.out.println("Exception in the headerController");
			e.printStackTrace();
		}
		mediaViewPane.getChildren().addAll(importedMediaObjects);
	}
	
	@Override
	public FXMLLoader getFXMLLoader() {
		return fxmlLoader;
	}
	// TODO: explain
	@FXML protected void buttonPressed(ActionEvent event) {
		System.out.println("Header:" + event.getSource().toString() + "has been pressed");
		
		if(((Button)event.getSource()).getId().equals("menuBtn") ){
			//If the menu screen button is pressed the MainGUIController changes the screen to be the menu screen
			MainGUIController.getInstance().changeScreen(SCREENTYPE.MAINMENU);
			
		}else if(((Button)event.getSource()).getId().equals("addTimeLineBtn")){
			System.out.println("Adding a TimeLine");
			System.out.println(parentController);
			System.out.println("PARENT?");
			this.parentController.addTimeline(); 
			
		}else if(((Button)event.getSource()).getId().equals("importMediaFromDisk")){
			// If the user clicks the import media button, he will get a windows file-chooser
			this.addMediaObjectFromDisk();
			
			
			
		}
	}
	
	public void addMediaObjectIconToView(MediaObjectIcon icon){
		parentController.addDragDetection(icon);
//		mediaViewPane.getChildren().add(icon);
		importedMediaObjects.add(icon);
		
	}
	
	public void updateMediaView(){
		mediaViewPane.getChildren().addAll(importedMediaObjects);
	}
	
	public void createNewMediaObject(){
		
	}
	
	/*
	 * TODO: get the currentSession from advancedScreen, and get the list of mediaObjects from the session. 
	 * 
	 */
	public void mediaObjectsChanged(){ // Consider rename to fireMediaObjectLstChanged
		//importedMediaObjects=currentSession.getMediaObjects()
		//updateMediaView()
		
	}
	
	//TODO: explain function
	public void addMediaObjectFromDisk(){
		//Lets the user choose a file from the disk
		FileChooser fileChooser;
		List<File> selectedFiles = null;
		try {
			fileChooser = new FileChooser();
			fileChooser.setTitle("Open media files");
			//TODO: consider making an extentionFilter: fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Video files", "*.avi"));
			selectedFiles = fileChooser.showOpenMultipleDialog(MainGUIController.getInstance().primaryStage);
		} catch(Exception e) {
			System.out.println("HeaderController: FileChooser caught an expection");
		}
		
		
		
		for (File file : selectedFiles){
			if(file != null){
				System.out.println("Do something with file: " + file.getName());
				//TODO: parentController.currentSession.createMediaObject(TYPE,PATH)
				
			}
			
		}
		
		
		MediaObjectIcon icn = new MediaObjectIcon();
		icn.setType(MediaObjectType.VIDEO);	
		this.addMediaObjectIconToView(icn);
		//Checks the validity of the files imported by checking the file extension
		String extension = "";
		String fileName = "test"; //file.getName();
		int i = fileName.lastIndexOf('.');
		int p = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));
		if (i > p) {
		    extension = fileName.substring(i+1);
		}
		if(extension.equals("avi")){ //TODO: Make a list over allowed fileextentions, and map this to different mediaTypes
			System.out.println("AVI");
		}
		
		
	}
	


	public GridPane getRoot() {
		return this.rootPane;
	}	
	
	
//	public void updateMediaObjectView(){
//		mediaObjectProperty.itemsProperty().bind(mediaObjectProperty);
//		if(file != null){
//			fileChosen(file);
//		}
//	}
	//binds the items of the listView to the listProperty. This should probably be done somewhere else
	

}
