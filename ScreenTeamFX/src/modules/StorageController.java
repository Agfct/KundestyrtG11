package modules;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * 
 * @author ole-s
 * Class responsible for saving and loading sessions to disk.
 * Use storeSession() and loadSession() to save and load. After calling loadSession() successfully, you can retrieve 
 * ArrayList<MediaObject> mediaObjects and TimelineModule timelineModule by using the getters for these.
 */
public class StorageController {
	
	// storageFile is the savefile used if no file is specified. 
	File storageFile;
	// The list of MediaObjects. Every class that uses mediaObjects should use the same list, so they are always in sync
	ArrayList<MediaObject> mediaObjects;
	// Set after it is loaded, can then be retrieved.
	TimelineModule timelineModule;
	
	private static StorageController instance = null;
	
	protected StorageController() {
		storageFile = new File("default_save.data");
		mediaObjects = new ArrayList<MediaObject>();
		timelineModule = null;
	}
	
	public boolean storeSession(TimelineModule tlm){
		return storeSession(tlm, this.storageFile);
	}
	
	public boolean storeSession(TimelineModule tlm, String fileString){
		File saveFile = new File(fileString);
		return storeSession(tlm, saveFile);
	}
	
	public boolean storeSession(TimelineModule tlm, File file){
		FileOutputStream f_out_stream = null;
		ObjectOutputStream obj_out_stream = null;
		boolean storageSuccess = false;
		
		// Try to open the storage file
		try {
			f_out_stream = new FileOutputStream(file);
			
			// Try to store the object
			obj_out_stream = new ObjectOutputStream(f_out_stream);
			obj_out_stream.writeObject(tlm);
			obj_out_stream.writeObject(mediaObjects);
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
	
	public boolean loadSession(){
		return loadSession(this.storageFile);
	}
	
	public boolean loadSession(String fileString){
		File saveFile = new File(fileString);
		return loadSession(saveFile);
	}
	
	/**
	 * Tries to load ArrayList<MediaObject> and TimelineModule from the specified file. If it succeeds, the MediaObjects that
	 * exist in TimelineModule->TimelineModel->TimelineMediaObject.parent->MediaObject are synchronized with the MediaObjects
	 * in ArrayList<MediaObject> to ensure that there is only one MediaObject instance per actual MediaObject (each file that is).
	 * The timelinemodule and the mediaobjects are then set as object members in this StorageController and can be retrieved 
	 * with their getters and setters.
	 * @param file
	 * @return
	 */
	public boolean loadSession(File file){
		// Check that the requested file exists
		if(!file.exists()){
			System.out.println("File: " + file + ", not found.");
			return false;
		}
		
		FileInputStream f_in_stream = null;	
		ObjectInputStream obj_in_stream = null;
		
		// Store the loaded objects in these.
		Object temp_tlm = null;
		Object temp_mo = null;
		try{
			f_in_stream = new FileInputStream(file);
			obj_in_stream = new ObjectInputStream(f_in_stream);
			
			temp_tlm = obj_in_stream.readObject();
			temp_mo = obj_in_stream.readObject();
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
		if ( temp_tlm instanceof TimelineModule ) {
			this.timelineModule = (TimelineModule)temp_tlm;
			
			if (temp_mo instanceof ArrayList<?>){
				if ( ((ArrayList<?>)temp_mo).size() > 0 ){
					if ( ((ArrayList<?>)temp_mo).get(0) instanceof MediaObject ) {
						this.mediaObjects = (ArrayList<MediaObject>)temp_mo;
						this.synchronizeMediaObjects();
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Ensures that the loaded MediaObjects in TimlineModule and ArrayList<MediaObject> are the same after loading them.
	 * Goes through all the TimelineMediaObjects in the TimelineModule, and updates the parents to the ones in
	 * ArrayList<MediaObject> (this.mediaObjects)
	 */
	private void synchronizeMediaObjects(){
		// Go through all timelines in TimelineModule
		ArrayList<TimelineModel> timelines = timelineModule.getTimelines(); 
		for (int i=0; i<timelines.size(); i++) {
			
			// Go through all TimelineMediaObjects in each timeline
			ArrayList<TimelineMediaObject> mo = timelines.get(i).getTimelineMediaObjects();
			for (int j=0; j<mo.size(); j++){
				
				// Find the correct MediaObject parent for for the TimelineMediaObject
				MediaObject parent = mo.get(j).getParent();
				for (int k=0; k<this.mediaObjects.size(); k++){
					
					// Replace the parent with the MediaObject with the same path.
					if ( (parent.getPath()).equals(this.mediaObjects.get(k).getPath()) ) {
						mo.get(j).setParent(this.mediaObjects.get(k));
					}
					
					if ( k==this.mediaObjects.size()-1 ) {
						// Have found no parent!
						// TODO: handle this. maybe add the parent to the media objects?
					}
				}
			}
		}
	}
	
	public File getStorageFile() {
		return storageFile;
	}

	public void setStorageFile(File storageFile) {
		this.storageFile = storageFile;
	}

	public ArrayList<MediaObject> getMediaObjects() {
		return mediaObjects;
	}

	public void setMediaObjects(ArrayList<MediaObject> mediaObjects) {
		this.mediaObjects = mediaObjects;
	}

	public TimelineModule getTimelineModule() {
		return timelineModule;
	}

	public void setTimelineModule(TimelineModule timelineModule) {
		this.timelineModule = timelineModule;
	}

	public static StorageController getInstance() {
		if (instance == null) {
			instance = new StorageController();
		}
		return instance;
	}
}
