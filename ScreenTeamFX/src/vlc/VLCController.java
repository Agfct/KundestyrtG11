package vlc;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.nio.file.InvalidPathException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import com.sun.jna.NativeLibrary;
import com.sun.media.sound.InvalidFormatException;

import uk.co.caprica.vlcj.runtime.RuntimeUtil;

public class VLCController {
	private ArrayList<VLCMediaPlayer> mediaPlayerList = new ArrayList<VLCMediaPlayer>();
	private ArrayList<Integer> availableDisplays = new ArrayList<Integer>();
	private Map<VLCMediaPlayer, Integer> mediaPlayerDisplayConnections = new HashMap<VLCMediaPlayer, Integer>();
	private GraphicsDevice[] displays;
	private String vlcPath;
	private VLCMediaPlayer prerunCheckPlayer;
	
	/**
	 * Creates a new VLC controller and opens a file chooser to select the 64bit VLC path.
	 * Searches for all displays available on the computer and adds them to an ArratList.
	 */
	public VLCController(){
		vlcPath = "C:\\Program Files\\VideoLAN\\VLC64";
		//vlcPath = new FileChooser().selectVLCPath();
		NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), vlcPath);
		prerunCheckPlayer = new VLCMediaPlayer();
		findDisplays();
	}
	
	private void findDisplays(){
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
	
	
	/**
	 * Display mp on the display only if display is not already in use * @param mp * @param display */
	public boolean setDisplay(VLCMediaPlayer mp, int display){
		if(availableDisplays.contains((Integer)display)){
			availableDisplays.add(mediaPlayerDisplayConnections.get(mp));
			availableDisplays.remove(display);
			mediaPlayerDisplayConnections.remove(mp);
			mediaPlayerDisplayConnections.put(mp, display);
			mp.setDisplay(display);
			return true;
		}
		return false;
	}
	
	public void setMedia(VLCMediaPlayer mp, String mediaPath){
		mp.setMedia(mediaPath);
	}
	
	public void seek(VLCMediaPlayer mp, long time){
		mp.seek(time);
	}
	
	/** * Plays one specific media player. mp corresponds to a time line * @param mp */
	public void playOne(VLCMediaPlayer mp){
		mp.play();
	}
	
	/** * Pauses one specific media player. mp corresponds to a time line * @param mp */
	public void pauseOne(VLCMediaPlayer mp){
		mp.pause();
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
					mp.pause();
				}
			};
			t.start();
		}
		try {
			gate.await();
		} catch (InterruptedException | BrokenBarrierException e) {e.printStackTrace();}
	}
	
	public boolean prerunCheck(String mediaPath){
		try{
			if(prerunCheckPlayer.isPlayable(mediaPath)){
				return true;
			}
			return false;
		}
		catch(Exception e) {
			return false;
		}
		finally {
			prerunCheckPlayer.close();
		}
	}
	
	public ArrayList<Integer> getAvailableDisplays(){
		return availableDisplays;
	}
	
	public ArrayList<VLCMediaPlayer> getMediaPlayers(){
		return mediaPlayerList;
	}
}
