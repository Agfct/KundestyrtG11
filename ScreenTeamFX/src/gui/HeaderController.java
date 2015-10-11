/**
 * 
 */
package gui;

import java.io.File;
import java.util.ArrayList;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
	private GridPane rootPane;
	private FXMLController parentController;
	//Parent topGrid
	GridPane topGrid;
	
	//Pointers to the fx:ids in the FXML
	@FXML private Button testButton;
	
	
	
	
	//-----Graphical elements-------//
	//variable to keep track of the media files imported
	ObservableList<String> importedMediaObjects = FXCollections.observableArrayList();
	
	//Autoupdatable listproperty for use on the listview
	protected ListProperty<String> mediaObjectProperty = new SimpleListProperty<>();

	
	
	public HeaderController() {
		
		//Fetches the parent controller. In this case it is the controller in the advancedScreen class.'
		parentController = AdvancedScreen.getInstance().getScreenController();
		
		//Fetches the top grid from the parent
		
				
		
		// Trying to fetch the FXML
		try {
			fxmlLoader = new FXMLLoader(getClass().getResource("Header.fxml"));
			fxmlLoader.setController(this);
			//fxmlLoader.load();
			rootPane = fxmlLoader.getRoot();
			//Fetches the parent controller. In this case it is the controller in the advancedScreen class.'
			parentController = AdvancedScreen.getInstance().getScreenController();
		} catch (Exception e) {
			System.out.println("Exception in the headerController");
			e.printStackTrace();
		}
	}
	
	@Override
	public FXMLLoader getFXMLLoader() {
		// TODO Auto-generated method stub
		return null;
	}
	// TODO: explain
	@FXML protected void buttonPressed(ActionEvent event) {
		System.out.println("Header:" + event.getSource().toString() + "has been pressed");
		
		if(((Button)event.getSource()).getId().equals("menuBtn") ){
			//If the menu screen button is pressed the MainGUIController changes the screen to be the menu screen
			MainGUIController.getInstance().changeScreen(SCREENTYPE.MAINMENU);
			
		}else if(((Button)event.getSource()).getId().equals("addTimeLineBtn")){
			System.out.println("Adding a TimeLine");
			//this.parentController.action(); TODO: Implement this? 
			
		}else if(((Button)event.getSource()).getId().equals("importMedia")){
			// If the user clicks the import media button, he will get a windows file-chooser
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Open media file");
			File file=fileChooser.showOpenDialog(MainGUIController.getInstance().primaryStage);
			
		}
	}	
	
	
//	public void updateMediaObjectView(){
//		mediaObjectProperty.itemsProperty().bind(mediaObjectProperty);
//		if(file != null){
//			fileChosen(file);
//		}
//	}
	//binds the items of the listView to the listProperty. This should probably be done somewhere else
	
}
