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

	private StorageController storageController;
	private SessionModule sessionModule;
	private IOModule ioModule;
	private VLCController vlc;
	private WindowDisplay wdi;
	// might need io if that is made

	private static MainModuleController mainModuleController;
	
	
	private MainModuleController(){
		this.storageController = StorageController.getInstance();
		//TODO check if storage loads last session without problem, if so set timelinemodule to last session
		//this.timelinemodule = this.storage.gettimelineModule
		this.ioModule = new IOModule();
		this.wdi = new WindowDisplay(ioModule.getDisplays().size());
		this.vlc = new VLCController(ioModule.getDisplays());
		this.sessionModule= new SessionModule(vlc,wdi);
		this.sessionModule.updateDisplays(ioModule.getDisplays());

	}
	
	public static MainModuleController getInstance() {
		if(mainModuleController == null){
			mainModuleController = new MainModuleController();
			
			//This tells the MainGUIController that the initialization is done
			MainGUIController.getInstance().finishedLoadingModules();
		}
		return mainModuleController;
	}
	
	public IOModule getIOModule(){
		return ioModule;
	}
	
	public VLCController getVLCController(){
		return vlc;
	}
	
	public SessionModule getSession(){
		System.out.println(sessionModule);
		return sessionModule;
	}
	
}
