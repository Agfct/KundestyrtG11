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

import modules.*;



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
	
	//Pointers to the fx:ids in the FXML
	@FXML private Button testButton;
	
	//The rootGrid that lies at the bottom of this FXML
	@FXML private GridPane rootPane;
	
	//The TilePane that has the mediaObjectIcons
	@FXML private TilePane mediaViewPane;
	

	//variable to keep track of the media files imported
	ArrayList<MediaObjectIcon> importedMediaObjects = new ArrayList<MediaObjectIcon>(); //NB: Should perhaps be a normal ArrayList

	
	
	public HeaderController(AdvancedScreenController AdvParentController) {
		this.parentController=AdvParentController;
		System.out.println("HEADERCONST");
	
		
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
	
	/*
	 * Button-listener for the header. 
	 */
	@FXML protected void buttonPressed(ActionEvent event) {
		System.out.println("Header:" + event.getSource().toString() + "has been pressed");
		
		if(((Button)event.getSource()).getId().equals("menuBtn") ){
			//If the menu screen button is pressed the MainGUIController changes the screen to be the menu screen
			MainGUIController.getInstance().changeScreen(SCREENTYPE.MAINMENU);
			
		}else if(((Button)event.getSource()).getId().equals("addTimeLineBtn")){
			this.parentController.addTimeline(); 
			
		}else if(((Button)event.getSource()).getId().equals("importMediaFromDisk")){
			// If the user clicks the import media button, he will get a windows file-chooser
			this.addMediaObjectFromDisk();
			
			
			
		}
	}
	
	
	/*
	 * TODO: possibly get all mediaObjects from the currentSession first?
	 * This method updates the tilePane in the header. This should happen every time the model is changed. 
	 */
	public void updateMediaView(){
		mediaViewPane.getChildren().clear();
		mediaViewPane.getChildren().addAll(importedMediaObjects);
		for(MediaObjectIcon icn:importedMediaObjects){
			parentController.addDragDetection(icn);
		}		
	}
	
	/*
	 * TODO: get the currentSession, and run the method currentSession.
	 * This function sends the mediaType and Path to the session.CreateNewMediaObject()
	 */
	public void createNewMediaObjectFromFile(File file){
		String path=file.getAbsolutePath();
		
		//Checks if the file is a video file, an audio file or a not accepted file
		ArrayList<String> acceptedVideoFormats = new ArrayList<String>();
		acceptedVideoFormats.add("avi");
		acceptedVideoFormats.add("mpg");
		acceptedVideoFormats.add("mkv");
		acceptedVideoFormats.add("wmv");
		ArrayList<String> acceptedAudioFormats = new ArrayList<String>();
		acceptedAudioFormats.add("mp3");
		acceptedAudioFormats.add("flac");
		acceptedAudioFormats.add("wma");
		acceptedAudioFormats.add("waw");
		
		//Checks the validity of the files imported by checking the file extension
		String extension = "";
		String fileName = file.getName();
		int i = fileName.lastIndexOf('.');
		int p = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));
		if (i > p) {
		    extension = fileName.substring(i+1);
		}
		
		
		for(String format:acceptedVideoFormats){
			if(format.equals(extension)){
				System.out.println(fileName + " is a video! of type:  "+ format);
				parentController.getCurrentSession().createNewMediaObject(MediaSourceType.VIDEO,path);
				//___________DELETE: NOT MVC___________
//				MediaObjectIcon icn = new MediaObjectIcon();
//				icn.setType(MediaObjectType.VIDEO);
//				icn.setTitle(fileName);
//				importedMediaObjects.add(icn);
//				updateMediaView();
				//--------DELETE FINISHED--------------
				return;
			}
		}
		for(String format:acceptedAudioFormats){
			if(format.equals(extension)){
				System.out.println(fileName + " is a sound! of type:  "+ format);
				//TODO: currentSession.createMediaObject(MediaSourceType.AUDIO,path);
				return;
			}
		}
		System.out.println("The file was neither a video nor a sound: " + extension);
	}
	
	/*
	 * TODO: get the currentSession from advancedScreen, and get the list of mediaObjects from the session. 
	 *  This method is run by the currentSession when a mediaObject is changed. 
	 */
	public void mediaObjectsChanged(){ // is Run by the advScreen when the function fireMediaObjectCahnges 
		ArrayList<MediaObject> newListOfMediaObjects=parentController.getCurrentSession().getMediaObjects();
		importedMediaObjects.clear();
		for(MediaObject m:newListOfMediaObjects){
			MediaObjectIcon icn = new MediaObjectIcon(m);
			icn.setMediaObject(m);
			icn.setType(MediaSourceType.VIDEO);
			icn.setTitle(m.getName());
			importedMediaObjects.add(icn);
		}
		
		//Updates the view for the user
		updateMediaView();
		
	}
	
	/*
	 * This method is run each time the user presses the "import Media From disk"-button
	 * It creates a fileChooser, and lets the user pick one or more files
	 * 
	 */
	public void addMediaObjectFromDisk(){
		//Lets the user choose a file from the disk
		FileChooser fileChooser;
		List<File> selectedFiles = null;
		try {
			fileChooser = new FileChooser();
			fileChooser.setTitle("Open media files");
			//TODO: consider making an extentionFilter: fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Video files", "*.avi"));
			selectedFiles = fileChooser.showOpenMultipleDialog(MainGUIController.getInstance().primaryStage); //Possibilty to select more files at the time
		} catch(Exception e) {
			System.out.println("HeaderController: FileChooser caught an expection");
		}
		//checks for aborted file import
		if(selectedFiles==null){
			return;
		}
		
		//Runs through the files imported, and creates a mediaObject from the file
		
		for (File file : selectedFiles){
			if(file != null){
				this.createNewMediaObjectFromFile(file);
			}
		}		
	}
	

	
	public GridPane getRoot() {
		return this.rootPane;
	}	


}
