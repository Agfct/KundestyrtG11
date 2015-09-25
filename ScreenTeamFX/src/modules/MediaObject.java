package modules;
/**
 * 
 * @author BEO
 * This class contains information about a video or a stream.
 *
 *
 */
public abstract class MediaObject {

	protected String url;
	protected String name;
	
	/**
	 * 
	 * @param url
	 * @param name
	 */
	public MediaObject(String url, String name) {
		super();
		this.url = url;
		this.name = name;
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
