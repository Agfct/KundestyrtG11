package modules;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * 
 * @author ole-s
 * Class responsible for saving and loading objects to disk.
 */
public class StorageController {
	
	File outputfile = null;
	ArrayList<MediaObject> mediaobjects = null;
	
	private static StorageController instance = null;
	
	protected StorageController() {
		outputfile = new File("object.data");
		mediaobjects = new ArrayList<MediaObject>();
	}
	
	public static StorageController getInstance() {
		if (instance == null) {
			instance = new StorageController();
		}
		return instance;
	}
	
	/**
	 * 
	 * @param object
	 * @return boolean
	 * Tries to store the given object to disk. Returns true if it succeeds.
	 * This method calls a protected method that actually performs the saving, while this method
	 * catches exceptions and handles them.
	 */
	public boolean storeObject(Object object){
		boolean storageSucces = false;
		try{
			storageSucces = this.protectedStoreObject(object);
		} catch (Exception e){
			StackTraceElement elements[] = e.getStackTrace();
		    for (int i = 0, n = elements.length; i < n; i++) {       
		        System.err.println(elements[i].getFileName()
		            + ":" + elements[i].getLineNumber() 
		            + ">> "
		            + elements[i].getMethodName() + "()");
		    }
		}
		return storageSucces;
	}
	
	protected boolean protectedStoreObject(Object object) throws Exception{
		FileOutputStream f_out_stream = null;
		ObjectOutputStream obj_out_stream = null;
		boolean storageSuccess = false;
		
		// Try to open the storage file
		try {
			f_out_stream = new FileOutputStream(outputfile);
			
			// Try to store the object
			obj_out_stream = new ObjectOutputStream(f_out_stream);
			obj_out_stream.writeObject(object);
			storageSuccess = true;
		} catch (Exception e) {
			throw new Exception("Could not store object to "+outputfile+" in storeObject() in StorageController.java.", e);
		} finally {
			f_out_stream.close();
			obj_out_stream.close();
		}
		
		return storageSuccess;
		
	}
	
	public Object loadObject(){
		
	}

}
