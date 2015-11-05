package modules;

import java.awt.EventQueue;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JOptionPane;

import gui.MainGUIController;
import gui.SessionListener;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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
		FileController.initialize();
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
	
	public boolean saveSession(){
		return storageController.storeSession(sessionModule);
	}
	
	public SessionModule createNewSession(){
		return new SessionModule(vlc,wdi);
	}

	public boolean saveSession(File saveFile) {
		// Need to remove some stuff, but keep it and put it back after the save
		ArrayList<SessionListener> listeners = sessionModule.removeListeners();
		VLCController vlcc = sessionModule.removeAndGetVLCController();
		Thread t1 = sessionModule.removeT1();
		Thread tAll = sessionModule.removeTAll();
		Thread gtt = sessionModule.removeGlobalTimeTicker();
		HashMap<Integer, TimelineModel> disp = sessionModule.removeDisplays();
		WindowDisplay wd = sessionModule.removeWindowDisplay();
		
		boolean result = storageController.storeSession(sessionModule, saveFile);
		
		// Put back stuff
		sessionModule.setListeners(listeners);
		sessionModule.setVLCController(vlcc);
		sessionModule.setT1(t1);
		sessionModule.setTAll(tAll);
		sessionModule.setGlobalTimeTicker(gtt);
		sessionModule.setDisplays(disp);
		sessionModule.setWindowDisplay(wd);
		
		return result;
	}

	public String getSaveFiletype() {
		return storageController.getFileType();
	}

	public SessionModule loadSession(File loadFile) {
		return storageController.loadSession(loadFile);
	}

	/**
	 * After loding a session, this method is called to set the loaded session as the current one.
	 * @param sm
	 */
	public void updateSession(SessionModule sm) {
		
		// Remove the old stuff. This removes old VLCMediaPlayers
		sessionModule.removeListeners();
		sessionModule.removeAllTimlines();
		
		sessionModule = sm;
		
		sessionModule.removeAllTimlineDisplayAssignments();
		
		this.vlc = new VLCController(ioModule.getDisplays());
		this.wdi = new WindowDisplay(ioModule.getDisplays().size());
		sessionModule.reinitialize(vlc, wdi);
		sessionModule.updateDisplays(ioModule.getDisplays());
		
		// Run the PreRunChecker to see if any MediaObjects are not found. Give a warning to the user.
		ArrayList<MediaObject> nonExistingMediaObject = PreRunChecker.getNonExsitingMediaObjects();
		if( 0 < nonExistingMediaObject.size() ){
			String message = "";
			message += "The following media files are missing (not found on the old path):\n\n";
			for(MediaObject mo : nonExistingMediaObject){
				mo.setValidPath(false);
				
				if(mo.getType()==MediaSourceType.WINDOW){
					message += "- Window:   "+ mo.getPath() +"\n";
				}
				else{
					message += "- Media:       "+ mo.getPath() +"\n";
				}
			}
			message += "\nPlease right-click on these imported windows/medias and choose \"Set path\" to update the paths.\n"
					+ "If, for some reason, you can not set the correct path on some of them, then please right-click and remove them. \n"
					+ "If you try to press \"Play\", whith the wrong path, the program will not work correctly.";			
			
			// JavaFX Information Dialog
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Warning Dialog");
			alert.setHeaderText("Warning: Media/window not found");
			alert.setContentText(message);
			alert.setResizable(true);
			alert.getDialogPane().setPrefWidth(650);
			alert.showAndWait();
		}
	}
	
}