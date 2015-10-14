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
 * Class responsible for saving and loading objects to disk.
 */
public class StorageController {
	

	File storageFile;
	ArrayList<MediaObject> mediaObjects;
	SessionModule timelineModule;

	
	private static StorageController instance = null;
	
	protected StorageController() {
		storageFile = new File("default_session_save.data");
		mediaObjects = new ArrayList<MediaObject>();
		timelineModule = null;
	}
	
	public static StorageController getInstance() {
		if (instance == null) {
			instance = new StorageController();
		}
		return instance;
	}
	
	public boolean storeSession(SessionModule tlm, File file){
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
	
	public boolean loadSession(String file){
		// Check that the requested file exists
		if(!new File(file).exists()){
			System.out.println("File " + file + " not found.");
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
		if ( temp_tlm instanceof SessionModule ) {
			this.timelineModule = (SessionModule)temp_tlm;
			
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
}
