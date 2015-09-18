/**
 * 
 */
package modules;

/**
 * @author Anders Lunde
 *Singleton
 */
public class MainModuleController {

	private static MainModuleController mainModuleController;
	private MainModuleController(){}
	
	public static MainModuleController getInstance() {
		if(mainModuleController == null){
			mainModuleController = new MainModuleController();
		}
		return mainModuleController;
	}
	
}
