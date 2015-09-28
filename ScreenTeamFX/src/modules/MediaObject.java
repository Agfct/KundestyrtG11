package modules;
/**
 * 
 * @author BEO
 * This class contains information about a video or a stream.
 *
 *
 */
public abstract class MediaObject {

	private String url;
	private String name;
	private int startTime;
	
	/**
	 * 
	 * @param url
	 * @param name
	 * @param startTime	
	 */
	public MediaObject(String url, String name, int startTime) {
		super();
		this.url = url;
		this.name = name;
		this.startTime = startTime;
	}

	public int getStartTime() {
		return startTime;
	}

	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
}
