package modules;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * 
 * @author ole-s
 * Class responsible for saving and loading sessions to disk.
 * Use storeSession() and loadSession() to save and load. After calling loadSession() successfully, you can retrieve 
 * ArrayList<MediaObject> mediaObjects and TimelineModule timelineModule by using the getters for these.
 */
public class StorageController {
	
	String defaultFile = "default_save";
	String filetype = "stdata";
	
	// storageFile is the savefile used if no file is specified. 
	File storageFile;
	
	// StorageController is singleton
	private static StorageController instance = null;
	
	protected StorageController() {
		storageFile = new File(defaultFile + "." + filetype);
	}
	
	public static StorageController getInstance() {
		if (instance == null) {
			instance = new StorageController();
		}
		return instance;
	}

	
	public boolean storeSession(SessionModule sm){
		return storeSession(sm, this.storageFile);
	}
	
	public boolean storeSession(SessionModule sm, String fileString){
		File saveFile = new File(fileString);
		return storeSession(sm, saveFile);
	}
	
	public boolean storeSession(SessionModule sm, File file){
		
		// Ensure that the save file has the correct filetype
		file = setCorrectFileype(file);
		
		FileOutputStream f_out_stream = null;
		ObjectOutputStream obj_out_stream = null;
		boolean storageSuccess = false;
		
		// Try to open the storage file
		try {
			f_out_stream = new FileOutputStream(file);
			
			// Try to store the object
			obj_out_stream = new ObjectOutputStream(f_out_stream);
			obj_out_stream.writeObject(sm);
			storageSuccess = true;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				f_out_stream.close();
				obj_out_stream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return storageSuccess;
	}
	
//	public SessionModule loadSession(){
//		return loadSession(this.storageFile);
//	}
	
	public SessionModule loadSession(String fileString){
		File saveFile = new File(fileString);
		return loadSession(saveFile);
	}
	
	/**
	 * 
	 * @param file
	 * @return
	 */
	public SessionModule loadSession(File file){
		// Check that the requested file exists
		if(!file.exists()){
			System.out.println("File: " + file + ", not found.");
			return null;
		}
		
		FileInputStream f_in_stream = null;	
		ObjectInputStream obj_in_stream = null;
		
		// Store the loaded objects in these.
		Object loadedSessionModule = null;
		try{
			f_in_stream = new FileInputStream(file);
			obj_in_stream = new ObjectInputStream(f_in_stream);
			
			loadedSessionModule = obj_in_stream.readObject();
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				f_in_stream.close();
				obj_in_stream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		// Check that we have retrieved the correct objects
		if ( loadedSessionModule instanceof SessionModule ) {
			loadedSessionModule = ((SessionModule)loadedSessionModule);
			return this.synchronizeMediaObjects((SessionModule)loadedSessionModule);
		}
		System.out.println("The loaded object was not an instance of SessionMOdule");
		return null;
	}
	
	/**
	 * 
	 */
	private SessionModule synchronizeMediaObjects(SessionModule sm){
		// Go through all timelines in TimelineModule
		HashMap<Integer,TimelineModel> timelines = sm.getTimelines(); 
		for (Integer i : timelines.keySet()) {
			
			// Go through all TimelineMediaObjects in each timeline
			ArrayList<TimelineMediaObject> mo = timelines.get(i).getTimelineMediaObjects();
			for (int j=0; j<mo.size(); j++){
				
				// Find the correct MediaObject parent for for the TimelineMediaObject
				MediaObject parent = mo.get(j).getParent();
				for (int k=0; k<sm.getMediaObjects().size(); k++){
					
					// Replace the parent with the MediaObject with the same path.
					if ( (parent.getPath()).equals(sm.getMediaObjects().get(k).getPath()) ) {
						mo.get(j).setParent(sm.getMediaObjects().get(k));
					}
					
					if ( k==sm.getMediaObjects().size()-1 ) {
						// Have found no parent!
						// TODO: handle this. maybe add the parent to the media objects?
					}
				}
			}
		}
		return sm;
	}
	
	public File getStorageFile() {
		return storageFile;
	}

	public void setStorageFile(File storageFile) {
		this.storageFile = storageFile;
	}
	
	public String getFileType(){
		String out = ""+filetype;
		return out;
	}

	private File setCorrectFileype(File file) {
		String path = file.getPath();
		int dotIndex = path.lastIndexOf('.');
		if (dotIndex==-1){
			// There were no '.' in the filepath. So just add the filetype
			path += "." + filetype;
			return new File(path);
		}
		
		// Here the path contains '.' so check if the filetype is correct
		String ending = path.substring(dotIndex+1);
		if (ending.equals(filetype)){
			// all good
			return file;
		}
		
		// The filetype of file is not correct. Append the correct filetype
		path += "." + filetype;
		return new File(path);
	}
	
}
