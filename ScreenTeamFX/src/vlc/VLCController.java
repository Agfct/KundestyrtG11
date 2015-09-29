package vlc;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import com.sun.jna.NativeLibrary;

import uk.co.caprica.vlcj.runtime.RuntimeUtil;

public class VLCController {
	private ArrayList<VLCMediaPlayer> mediaPlayerList = new ArrayList<VLCMediaPlayer>();
	private ArrayList<Integer> availableDisplays = new ArrayList<Integer>();
	private Map<VLCMediaPlayer, Integer> mediaPlayerDisplayConnections = new HashMap<VLCMediaPlayer, Integer>();
	private GraphicsDevice[] displays;
	private String vlcPath;
	private VLCMediaPlayer prerunCheckPlayer = new VLCMediaPlayer();
	
	/**
	 * Creates a new VLC controller and opens a file chooser to select the 64bit VLC path.
	 * Searches for all displays available on the computer and adds them to an ArratList.
	 */
	public VLCController(){
		vlcPath = "C:\\Program Files\\VideoLAN\\VLC64";
		//vlcPath = new FileChooser().selectVLCPath();
		NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), vlcPath);
		findAvailableDisplays();
	}
	
	public void findAvailableDisplays(){
	    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	    GraphicsDevice[] gs = ge.getScreenDevices();
	    for(int i = 0; i < gs.length; i++){
	    	availableDisplays.add(i);
	    }
	    displays = gs;
	}
	
	/** * Create a VLC instance to be displayed on the specified display * @param display */
	public VLCMediaPlayer createMediaPlayer(int display){
		if(availableDisplays.contains((Integer)display)){
			VLCMediaPlayer mp = new VLCMediaPlayer(display);
			mediaPlayerList.add(mp);
			availableDisplays.remove((Integer)display);
			mediaPlayerDisplayConnections.put(mp, display);
			return mp;
		}
		return null;
	}
	
	public void setMedia(int mp, String path){
		mediaPlayerList.get(mp).setMedia(path);
	}
	
	/**
	 * Display mp on the display only if display is not already in use * @param mp * @param display */
	public boolean setDisplay(int mp, int display){
		if(availableDisplays.contains((Integer)display)){
			availableDisplays.add(mediaPlayerDisplayConnections.get(mediaPlayerList.get(mp)));
			mediaPlayerList.get(mp).setDisplay(display);
			availableDisplays.remove(display);
			mediaPlayerDisplayConnections.remove(mediaPlayerList.get(mp));
			mediaPlayerDisplayConnections.put(mediaPlayerList.get(mp), display);
			return true;
		}
		return false;
	}
	
	/** * Plays one specific media player. mp corresponds to a time line * @param mp */
	public void playOne(int mp){
		mediaPlayerList.get(mp).play();
	}
	
	/** * Pauses one specific media player. mp corresponds to a time line * @param mp */
	public void pauseOne(int mp){
		mediaPlayerList.get(mp).pause();
	}
	
	/** * Play all the media of all media players from their current time 
	 * @throws BrokenBarrierException 
	 * @throws InterruptedException */	
	public void playAll(){
		final CyclicBarrier gate = new CyclicBarrier(mediaPlayerList.size() + 1);
		for(VLCMediaPlayer mp: mediaPlayerList){
			Thread t = new Thread(){
				public void run(){
					try {
						gate.await();
					} catch (InterruptedException | BrokenBarrierException e) {e.printStackTrace();}
					mp.play();
				}
			};
			t.start();
		}
		try {
			gate.await();
		} catch (InterruptedException | BrokenBarrierException e) {e.printStackTrace();}
	}
	
	/** * Pause all the media players. 
	 * @throws BrokenBarrierException 
	 * @throws InterruptedException */
	public void pauseAll(){
		final CyclicBarrier gate = new CyclicBarrier(mediaPlayerList.size() + 1);
		for(VLCMediaPlayer mp: mediaPlayerList){
			Thread t = new Thread(){
				public void run(){
					try {
						gate.await();
					} catch (InterruptedException | BrokenBarrierException e) {e.printStackTrace();}
					mp.play();
				}
			};
			t.start();
		}
		try {
			gate.await();
		} catch (InterruptedException | BrokenBarrierException e) {e.printStackTrace();}
	}
	
	public void prerunCheck(){
		
	}
	
	public ArrayList<Integer> getAvailableDisplays(){
		return availableDisplays;
	}
	
	public ArrayList<VLCMediaPlayer> getMediaPlayers(){
		return mediaPlayerList;
	}
}
