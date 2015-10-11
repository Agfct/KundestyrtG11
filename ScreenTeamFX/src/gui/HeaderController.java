/**
 * 
 */
package gui;

import java.io.File;
import java.util.ArrayList;

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
	
	
	
	
	//-----Graphical elements-------//
	//variable to keep track of the media files imported
	ObservableList<String> importedMediaObjects = FXCollections.observableArrayList();
	
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
			
		}else if(((Button)event.getSource()).getId().equals("importMedia")){
			// If the user clicks the import media button, he will get a windows file-chooser
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Open media file");
			File file=fileChooser.showOpenDialog(MainGUIController.getInstance().primaryStage);
			
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
