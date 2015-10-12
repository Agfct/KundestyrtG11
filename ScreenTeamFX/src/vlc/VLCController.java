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
	private int ID = 0;
	private VLCMediaPlayer prerunCheckPlayer;
	
	/**
	 * Creates a new VLC controller and opens a file chooser to select the 64bit VLC path.
	 * Searches for all displays available on the computer and adds them to an ArrayList.
	 */
	public VLCController(String vlcP){
		this.vlcPath = vlcP;
		NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), vlcPath); //Gives vlcj access to the VLC client on the PC
		prerunCheckPlayer = new VLCMediaPlayer();
		findDisplays();
	}
	
	private void findDisplays(){
	    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	    GraphicsDevice[] gs = ge.getScreenDevices();
	    for(int i = 0; i < gs.length; i++){
	    	availableDisplays.add(i);
	    }
	}
	
	/** * Create a VLC instance to be displayed on the specified display 
	 * @param display */
	public VLCMediaPlayer createMediaPlayer(int display){
		if(availableDisplays.contains((Integer)display)){
			VLCMediaPlayer mp = new VLCMediaPlayer(display, ID);
			mediaPlayerList.put(ID, mp);
			availableDisplays.remove((Integer)display);
			mediaPlayerDisplayConnections.put(mp.getID(), display);
			ID += 1;
			return mp;
		}
		return null;
	}
	
	public void deleteMediaPlayer(int mp){
		if(mediaPlayerList.containsKey((Integer)mp)){
			toPlayer(mp).close();
			mediaPlayerList.remove(mp);
			availableDisplays.add(mediaPlayerDisplayConnections.get(mp));
			mediaPlayerDisplayConnections.remove(mp);
		}
	}
	
	/**
	 * Display mp on the display only if display is not already in use 
	 * * @param mp 
	 * * @param display */
	public boolean setDisplay(int mp, int display){
		if(availableDisplays.contains((Integer)display)){
			availableDisplays.add(mediaPlayerDisplayConnections.get(mp));
			availableDisplays.remove((Integer)display);
			mediaPlayerDisplayConnections.remove(mp);
			mediaPlayerDisplayConnections.put(mp, display);
			toPlayer(mp).setDisplay(display);
			return true;
		}
		return false;
	}
	
	/**
	 * Returns the media player associated with ID.
	 * @param ID
	 * @return */
	public VLCMediaPlayer toPlayer(int ID){
		return mediaPlayerList.get(ID);
	}
	
	public void setMedia(int mp, String mediaPath){
		toPlayer(mp).setMedia(mediaPath);
	}
	
	
	/** * Plays one specific media player.
	 * @param mp */
	public void playOne(int mp, long time){
		if(time != 0){
			
		}
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
