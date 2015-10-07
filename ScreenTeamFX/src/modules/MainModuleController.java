/**
 * 
 */
package modules;

import gui.MainGUIController;
import vlc.VLCController;

/**
 * @author Anders Lunde
 *Singleton
 */
public class MainModuleController {
	private StorageController storage;
	private TimelineModule timelinemodule;
	private VLCController vlc;
	// might need io if that is made

	private static MainModuleController mainModuleController;
	
	
	private MainModuleController(){
		//TODO create storage, load previous session and save it as timelinemodule, or create new
		//timelinemodule. also add vlcController
		
		//This tells the MainGUIController that the initialization is done
		MainGUIController.getInstance().finishedLoadingModules();
	}
	
	public static MainModuleController getInstance() {
		if(mainModuleController == null){
			mainModuleController = new MainModuleController();
		}
		return mainModuleController;
	}
	
}
