/**
 * 
 */
package modules;

import gui.MainGUIController;

/**
 * @author Anders Lunde
 *Singleton
 */
public class MainModuleController {

	private static MainModuleController mainModuleController;
	private MainModuleController(){
		
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
