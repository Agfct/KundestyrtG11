/**
 * 
 */
package gui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.input.DataFormat;
import javafx.util.Pair;

/**
 * @author Anders Lunde
 * Contains all the information a MediaObject needs when being dragged
 * By using a container we can drag the media from the fileImport
 */
public class MediaObjectContainer implements Serializable {

	/**
	 * Generated value
	 */
	private static final long serialVersionUID = 3142167186469138520L;

	//If one is creating a new mediaObject the container will only contain creation info
	public static final DataFormat AddNode = 
			new DataFormat("gui.MediaObjectIcon.add");
	
	/*If one is dragging an existing MediaObject the container will 
	contain all the information already stored inside the mediaObject*/
	public static final DataFormat DragNode = 
			new DataFormat("gui.MediaObject.drag");
	
	//The values of the data are stored as a pair of a string and an object
	private final List <Pair<String, Object> > mDataPairs = new ArrayList <Pair<String, Object> > ();
	
	/**
	 * Adds a new data pair to the container. using a key(string) and
	 * a value (object)
	 * @param key
	 * @param value
	 */
	public void addData (String key, Object value) {
		mDataPairs.add(new Pair<String, Object>(key, value));		
	}
	
	/**
	 * Returns an object based on the key(string)
	 * @param key
	 * @return
	 */
	public <T> T getValue (String key) {
		
		for (Pair<String, Object> data: mDataPairs) {
			
			if (data.getKey().equals(key))
				return (T) data.getValue();
				
		}
		
		return null;
	}
	
	/**
	 * Returns the entire list of data pairs
	 * @return
	 */
	public List <Pair<String, Object> > getData () { return mDataPairs; }	
}
