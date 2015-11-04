package modules;

import java.io.File;
import java.util.ArrayList;

public class FileController {
	
	public static final ArrayList<String> acceptedVideoFormats = new ArrayList<String>(); 
	public static final ArrayList<String> acceptedAudioFormats = new ArrayList<String>();
	public static final ArrayList<String> acceptedImageFormats = new ArrayList<String>();
	
	/**
	 * This must be called at the start of the program, or else the class won't work as it is supposed to.
	 */
	public static void initialize(){
		// Can change this in the future to read from a config file 
		
		acceptedVideoFormats.add("avi");
		acceptedVideoFormats.add("mpg");
		acceptedVideoFormats.add("mkv");
		acceptedVideoFormats.add("wmv");
		acceptedVideoFormats.add("mp4");
		acceptedVideoFormats.add("mov");
		
		acceptedAudioFormats.add("mp3");
		acceptedAudioFormats.add("flac");
		acceptedAudioFormats.add("wma");
		acceptedAudioFormats.add("waw");
		
		acceptedImageFormats.add("img");
		acceptedImageFormats.add("jpg");
		acceptedImageFormats.add("jpeg");
		acceptedImageFormats.add("png");
	}
	
	public static MediaSourceType getMediaSourceType(String filePath){
		String extension = getFileExtension(filePath);
		if(acceptedVideoFormats.contains(extension)){
			return MediaSourceType.VIDEO;
		}
		else if(acceptedAudioFormats.contains(extension)){
			return MediaSourceType.AUDIO;
		}
		else if(acceptedImageFormats.contains(extension)){
			return MediaSourceType.IMAGE;
		}
		
		return null;
	}
	
	/**
	 * Returns the file extension of the parameter file. Returns null if no extension is found.
	 * @param file
	 * @return
	 */
	public static String getFileExtension(String fileName){
		String extension = "";
		int i = fileName.lastIndexOf('.');
		int p = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));
		if (i > p) {
		    extension = fileName.substring(i+1).toLowerCase();
		    return extension;
		}
		else{
			return null;
		}
	}
	
	public static String getFileExtension(File file){
		String fileName = file.getName();
		return getFileExtension(fileName);
	}

	public static String getTitle(String path) {
		String title = path.substring(path.lastIndexOf('\\')+1);
		return title;
	}

}
