/**
 * 
 */
package modules;

import java.io.File;

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
	// might need io if that is made

	private static MainModuleController mainModuleController;
	
	
	private MainModuleController(){
		this.storageController = StorageController.getInstance();
		//TODO check if storage loads last session without problem, if so set timelinemodule to last session
		//this.timelinemodule = this.storage.gettimelineModule
		this.ioModule = new IOModule();
		
		this.vlc = new VLCController(ioModule.getDisplays());
		this.sessionModule= new SessionModule(vlc);
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
	
	public boolean saveSession(){
		return storageController.storeSession(sessionModule);
	}

	public boolean saveSession(File saveFile) {
		SessionModule saveSession = new SessionModule(null);
		// Need to remove some stuff, but keep it and put it back after the save
		sessionModule.getListeners();
		sessionModule.getVLCController();
		sessionModule.getT1();
		sessionModule.getTAll();
		sessionModule.getGlobalTimeTicker();
		sessionModule.getDisplays();
		
		boolean result = storageController.storeSession(saveSession, saveFile);
		
		// Put back stuff
		sessionModule.setListeners();
		sessionModule.setVLCController();
		sessionModule.setT1();
		sessionModule.setTAll();
		sessionModule.setGlobalTimeTicker();
		sessionModule.setDisplays();
		
		return result;
	}

	public String getSaveFiletype() {
		return storageController.getFileType();
	}

	public SessionModule loadSession(File loadFile) {
		return storageController.loadSession(loadFile);
	}

	public void updateSession(SessionModule sm) {
		int loadedNumberOfAvailableDisplays = sm.getNumberOfAbailableDisplays();
		int currentNumberOfAvailableDisplays = sessionModule.getNumberOfAbailableDisplays();
		
		// If we have at least as many displays as last time, we can keep the arrangement (displays->timlines), if not we have to scrap it
		boolean keepDisplays = false;//currentNumberOfAvailableDisplays >= loadedNumberOfAvailableDisplays;
		
		sessionModule = sm;
		
		sessionModule.reinitialize(vlc, keepDisplays);
		sessionModule.updateDisplays(ioModule.getDisplays());
		
	}
	
}
