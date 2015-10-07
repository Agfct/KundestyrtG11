package modules;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * 
 * @author ole-s
 * Class responsible for saving and loading objects to disk.
 */
public class StorageController {
	
	File storageFile;
	ArrayList<MediaObject> mediaObjects;
	TimelineModule timeLineModule;
	
	private static StorageController instance = null;
	
	protected StorageController() {
		storageFile = new File("default_session_save.data");
		mediaObjects = new ArrayList<MediaObject>();
		timeLineModule = null;
	}
	
	public static StorageController getInstance() {
		if (instance == null) {
			instance = new StorageController();
		}
		return instance;
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
	
	/**
	 * Example for class parameter: String.class, MediaObject.class, int.class
	 * 
	 * @return Object
	 */
	public boolean loadSession(String file){
		// Check that the requested file exists
		if(!new File(file).exists()){
			System.out.println("File " + file + " not found.");
			return false;
		}
		
		boolean loadSuccess = false;
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
			loadSuccess = true;
		} catch (IOException e) {
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
		
		if ( temp_tlm instanceof TimelineModule ) {
			this.timeLineModule = (TimelineModule)temp_tlm;
			
			if
		}
		
		// Cast the retrieved objects
		if (return_object == null){
			System.out.println("Could not load object from " + file);
			return null;
		} 
		else if (return_object instanceof TimelineModule){
			return (TimelineModule)return_object;
		} 
		else if (return_object instanceof ArrayList<?>) {
			if ( ((ArrayList<?>)return_object).size() > 0 ){
				if ( ((ArrayList<?>)return_object).get(0) instanceof MediaObject ) {
					return (ArrayList<MediaObject>)return_object;
				}
				else {
					System.out.println("The ArrayList loaded from " + file 
										+ " contains some other type of object than MediaObjects");
					return null;
				}
			}
			else {
				// System.out.println("The ArrayList loaded from " + file + " is empty.");
				return (ArrayList<MediaObject>)return_object;
			}
		}
		else {
			System.out.println("Tried to load an object from " + file
								+", but the retrieved object was not an instance of ArrayList or TimelineModule.");
			return null;
		}
	}

}
