package modules;

import java.io.File;
import java.util.ArrayList;

/**
 * This badboy checks that there is files/windows in the path of MediaObjects. 
 * 
 * @author ole-s
 *
 */
public class PreRunChecker {
	
	/**
	 * Iterates through the list of MediaObjects from SessionModule and checks which ones does not exist.
	 * @return An ArrayList<MediaObject> which contains the MediaObjects that were not found.
	 */
	public static ArrayList<MediaObject> getNonExsitingMediaObjects(){
		ArrayList<MediaObject> oldModuleMediaObjects = MainModuleController.getInstance().getSession().getMediaObjects();
		ArrayList<String> currentOpenWindows = MainModuleController.getInstance().getSession().getAvailableWindows();
		
		ArrayList<MediaObject> nonExistingMediaObjects = new ArrayList<MediaObject>();
		
		for(MediaObject oldMediaObject : oldModuleMediaObjects){
			if( oldMediaObject.getType()==MediaSourceType.WINDOW ){
				if( !isExistingWindow(oldMediaObject, currentOpenWindows) ){
					nonExistingMediaObjects.add(oldMediaObject);
				}
			}
			else{
				if( !isExisting(oldMediaObject) ){
					nonExistingMediaObjects.add(oldMediaObject);
				}
			}
		}
		
		return nonExistingMediaObjects; 
	}
	
	/**
	 * Checks if the MediaObject parameter exists in the parameter list of current open windows.
	 * @param mo
	 * @param currentWindows
	 * @return
	 */
	public static boolean isExistingWindow(MediaObject mo, ArrayList<String> currentWindows){
		// Go through the list of currently open windows and check if the MediaObject is there
		for(String win : currentWindows){
			if( mo.getPath().equals(win) ){
				// It exists
				return true;
			}
		}
		// Could not find it
		return false;
	}
	
	/**
	 * Checks if the parameter MediaObject exist on the disk by checking its path. The parameter MediaObject
	 * can not be of the MediaSourceType.WINDOW
	 * @param mo
	 * @return
	 */
	public static boolean isExisting(MediaObject mo){
		if( mo.getType()==MediaSourceType.WINDOW ){
			System.out.println("[PreRunChecker.java].isExisting tried to check if a MediaObject of type WINDOW exists on disk, "
					+ "but this method should not take in a MediaObject of type WINDOW.");
			return false;
		}
		
		String path = mo.getPath();
		boolean fileExists = new File(path).isFile();
		
		return fileExists;
	}
	
}
