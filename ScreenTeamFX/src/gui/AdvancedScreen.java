/**
 * 
 */
package gui;

import java.io.IOException;
import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

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
			private GridPane rootPane;

			
			@FXML private GridPane timelineContainer;
			
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

		public AdvancedScreenController getScreenController() {
			return screenController;
		}
		


}
