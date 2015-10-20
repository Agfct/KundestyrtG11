package modules;

import java.io.Serializable;

/**
 * 
 * @author Baptiste Masselin, Eirik Zimmer Wold, Ole Steinar L. Skrede
 * This class contains information about a video or a stream.
 *
 *
 */

public class MediaObject implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4023233269937165070L;
	private String path;
	private String name;
	private long length;
	private MediaSourceType type;;
	
	/**
	 * 
	 * @param url 
	 * @param name
	 * @param startTime	
	 */
	public MediaObject(String path, String name, MediaSourceType type) {
		super();
		this.path = path;
		this.name = name;
		this.setType(type);
		if ( type == MediaSourceType.IMAGE){
			this.length = Long.MAX_VALUE;
		}
		else {
			this.length=(long) Math.max(3000,(Math.random()*100000)); //TODO: get proper length from VLC. Now it gets random value between 3000 and 100000
		}
	}
	
	public String getName() {
		return name;
	}
	
	

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public long getLength() {
		//TODO: we need help from VLC to get correct length of a video. 
		return length;
	}

	public void setLength(long length) {
		this.length = length;
	}

	public MediaSourceType getType() {
		return type;
	}

	public void setType(MediaSourceType type) {
		this.type = type;
	}
	
}
