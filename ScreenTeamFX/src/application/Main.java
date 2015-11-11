/*
Cribrum. A program for playing video, audio, and images, and showing windows on multiple displays.
Copyright (C) 2015  Anders Lunde, Baptiste Masselin, Kristian Midtgård, Magnus Gundersen, Eirik Zimmer Wold, and Ole Steinar L. Skrede

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

/**
 * This software is made by (Screen Team):
 * Anders Lunde, Mangus Gundersen, Kristian Midtgård, 
 * Baptiste Masselin, Ole Steinar Lillestøl Skrede and Eirik Zimmer Wold.
 * Fall 2015
 * 
 * 
 */
package application;
	
import gui.MainGUIController;
import gui.MainScreen;
import javafx.application.Application;
import javafx.stage.Stage;
import modules.MainModuleController;
import vlc.VLCController;

/**
 * 
 * @author Anders Lunde, Magnus Gundersen
 * The Main class is simply the starting point of the software and initializes the
 * two main controllers; mainGUIController and mainModuleController.
 */

public class Main extends Application {
	MainGUIController mainGUIController;
	MainModuleController mainModuleController;
	
	/**
	 * Start initializes the main GUI controller for the GUI, 
	 * and the main module controller "MainModuleController" for the java parts of the application. 
	 */
	@Override
	public void start(Stage primaryStage) {
		try {
			
			//Initializes the GUI part of the software, initializing the mainScene onto the stage and adding the fxml.
			mainGUIController = MainGUIController.initialize(primaryStage);
			
			//Initializes the Modules and base models, running the default setup phase.
			mainModuleController = MainModuleController.getInstance();
			

			

		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void stop(){
		System.out.println("APPLICATION TERMINATED");
		System.exit(0);

	}


}
