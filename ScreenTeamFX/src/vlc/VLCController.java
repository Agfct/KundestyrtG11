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
	private Map<Integer, VLCMediaPlayer> mediaPlayerList = new HashMap<Integer, VLCMediaPlayer>();
	private Map<Integer, Integer> mediaPlayerDisplayConnections = new HashMap<Integer, Integer>();
	private ArrayList<Integer> availableDisplays = new ArrayList<Integer>();
	private String vlcPath;
	private boolean vlcPathSet = false;
	private VLCMediaPlayer prerunCheckPlayer;
	
	/**
	 * Creates a new VLC controller. vlcPath is the path to the VLC 64-bit client on your computer.
	 * @param vlcPath */
	public VLCController(String vlcPath){
		this.vlcPath = vlcPath;
		try{
			NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), vlcPath);
			vlcPathSet = true;
			prerunCheckPlayer = new VLCMediaPlayer();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		findDisplays();
	}
	
	private void findDisplays(){
	    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	    GraphicsDevice[] gs = ge.getScreenDevices();
	    for(int i = 0; i < gs.length; i++){
	    	availableDisplays.add(i);
	    }
	}
	
	/** 
	 * * Create a VLC media player. ID is linked to a timeline. 
	 * @param ID */
	public VLCMediaPlayer createMediaPlayer(int ID){
		if(vlcPathSet){
			VLCMediaPlayer mp = new VLCMediaPlayer(ID);
			mediaPlayerList.put(ID, mp);
			return mp;
		}
		else{
			System.out.println("Can't create a VLC media player. VLC64 path not set");
		}
		return null;
	}
	
	/**
	 * Deletes a media player and frees all its resources. 
	 * @param mp */
	public void deleteMediaPlayer(int mp){
		if(mediaPlayerList.containsKey((Integer)mp)){
			toPlayer(mp).close();
			mediaPlayerList.remove(mp);
			if(mediaPlayerDisplayConnections.containsKey(mp)){
				availableDisplays.add(mediaPlayerDisplayConnections.get(mp));
				mediaPlayerDisplayConnections.remove(mp);
			}
		}
	}
	
	/**
	 * Display mp on the display only if display is not already in use.
	 * Returns true if a display was set
	 * @param mp 
	 * @param display */
	public boolean setDisplay(int mp, int display){
		if(availableDisplays.contains((Integer)display)){
			if(mediaPlayerDisplayConnections.containsKey(mp)){
				availableDisplays.add(mediaPlayerDisplayConnections.get(mp));
				mediaPlayerDisplayConnections.remove(mp);
			}
			availableDisplays.remove((Integer)display);
			mediaPlayerDisplayConnections.put(mp, display);
			VLCMediaPlayer vlcmp = toPlayer(mp);
			if(System.getProperty("os.name").startsWith("Windows 8")){
				vlcmp.setDisplayWin8(display);
			}
			else{
				vlcmp.setDisplay(display);
				if(vlcmp.getTime() > 0){
					long time = vlcmp.getTime();
					vlcmp.setMedia(vlcmp.getMediaPath());
					vlcmp.seek(time);
				}	
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Returns the media player associated with ID.
	 * @param ID
	 * @return */
	private VLCMediaPlayer toPlayer(int ID){
		return mediaPlayerList.get(ID);
	}
	
	public void setMedia(int mp, String mediaPath){
		toPlayer(mp).setMedia(mediaPath);
	}
	
	
	/** * Seek to time and then play the media player. Seeks only if time not equal 0.
	 * @param mp
	 * @param time */
	public void playOne(int mp, long time){
		if(time != 0){
			toPlayer(mp).seek(time);
		}
		while(toPlayer(mp).isSeeking());
		toPlayer(mp).play();
	}

	/** * Pauses one specific media player. 
	 * * @param mp */
	public void pauseOne(int mp){
		toPlayer(mp).pause();
	}
	
	/**
	 * Seeks to time in the specified media player.
	 * @param mp
	 * @param time
	 */
	public void seekOne(int mp, long time){
		toPlayer(mp).seek(time);
	}
	
	public void stopOne(int mp){
		toPlayer(mp).stop();
	}
	
	/** * Play all the media of all media players at the exact same time.
	 * @throws BrokenBarrierException 
	 * @throws InterruptedException */	
	public void playAll() throws InterruptedException{
		ArrayList<Thread> threads = new ArrayList<Thread>();
		final CyclicBarrier gate = new CyclicBarrier(mediaPlayerList.size() + 1);
		for(int mp : mediaPlayerList.keySet()){
			Thread t = new Thread(){
				public void run(){
					try {
						gate.await();
					} catch (InterruptedException | BrokenBarrierException e) {e.printStackTrace();}
					toPlayer(mp).play();
				}
			};
			t.start();
			threads.add(t);
		}
		try {
			gate.await();
		} catch (InterruptedException | BrokenBarrierException e) {e.printStackTrace();}
		for(int i= 0; i < threads.size(); i++){
			threads.get(i).join();
		}
	}
	
	/** * Pause all the media players at the exact same time. 
	 * @throws BrokenBarrierException 
	 * @throws InterruptedException */
	public void pauseAll() throws InterruptedException{
		ArrayList<Thread> threads = new ArrayList<Thread>();
		final CyclicBarrier gate = new CyclicBarrier(mediaPlayerList.size() + 1);
		for(int mp : mediaPlayerList.keySet()){
			Thread t = new Thread(){
				public void run(){
					try {
						gate.await();
					} catch (InterruptedException | BrokenBarrierException e) {e.printStackTrace();}
					toPlayer(mp).pause();
				}
			};
			t.start();
			threads.add(t);
		}
		try {
			gate.await();
		} catch (InterruptedException | BrokenBarrierException e) {e.printStackTrace();}
		for(int i= 0; i < threads.size(); i++){
			threads.get(i).join();
		}
	}
	
	/**
	 * Takes a map of media players and times. 
	 * Seeks to that time for each media player at the exact same time
	 * @param map
	 * @throws InterruptedException */
	public void SeekMultiple(Map<Integer, Long> map) throws InterruptedException{
		ArrayList<Thread> threads = new ArrayList<Thread>();
		final CyclicBarrier gate = new CyclicBarrier(map.size() + 1);
		for(int mp : map.keySet()){
			Thread t = new Thread(){
				public void run(){
					try {
						gate.await();
					} catch (InterruptedException | BrokenBarrierException e) {e.printStackTrace();}
					toPlayer(mp).seek(map.get(mp));
				}
			};
			t.start();
			threads.add(t);
		}
		try {
			gate.await();
		} catch (InterruptedException | BrokenBarrierException e) {e.printStackTrace();}
		for(int i= 0; i < threads.size(); i++){
			threads.get(i).join();
		}
	}
	
	/**
	 * Returns true if mediaPath is a valid path and is playable.
	 * @param mediaPath
	 * @return */
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
}
