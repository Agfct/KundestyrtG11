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
	private SessionModule timelinemodule;
	private VLCController vlc;
	// might need io if that is made

	private static MainModuleController mainModuleController;
	
	
	private MainModuleController(){
		this.storage = StorageController.getInstance();
		//TODO check if storage loads last session without problem, if so set timelinemodule to last session
		//this.timelinemodule = this.storage.gettimelineModule
		this.vlc = new VLCController("C:\\Program Files\\VideoLAN\\VLC64");
		this.timelinemodule = new SessionModule(vlc);
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
