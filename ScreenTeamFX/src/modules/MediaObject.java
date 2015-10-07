package modules;

import java.io.Serializable;

/**
 * 
 * @author BEO
 * This class contains information about a video or a stream.
 *
 *
 */
public abstract class MediaObject  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4948583258019192531L;
	private String path;
	private String name;
	private int length;
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

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public MediaSourceType getType() {
		return type;
	}

	public void setType(MediaSourceType type) {
		this.type = type;
	}
	
}
