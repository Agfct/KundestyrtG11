/*
Cribrum. A program for playing video, audio, and images, and showing windows on multiple displays.
Copyright (C) 2015  Anders Lunde, Baptiste Masselin, Kristian Midtgård, Magnus Gundersen, Eirik Zimmer Wold, and Ole Steinar L. Skrede

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package vlc;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import javax.swing.JFrame;
import javax.swing.JLabel;

import com.sun.jna.NativeLibrary;

import uk.co.caprica.vlcj.runtime.RuntimeUtil;

public class VLCController {
	private Map<Integer, VLCMediaPlayer> mediaPlayerList = new HashMap<Integer, VLCMediaPlayer>();
	private Map<Integer, Integer> mediaPlayerDisplayConnections = new HashMap<Integer, Integer>();
	private ArrayList<Integer> displays = new ArrayList<Integer>();
	private ArrayList<Integer> availableDisplays = new ArrayList<Integer>();
	private boolean vlcPathSet = false;
	private VLCMediaPlayer prerunCheckPlayer;
	
	/**
	 * Creates a VLC controller.
	 * Java version is checked and the corresponding version on VLC is loaded.
	 */
	public VLCController(ArrayList<Integer> displays){
		if(Integer.parseInt(System.getProperty("sun.arch.data.model")) == 32){
			NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), System.getProperty("user.dir") + "\\VLC\\VLC32");
		}
		else if(Integer.parseInt(System.getProperty("sun.arch.data.model")) == 64){
			NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), System.getProperty("user.dir") + "\\VLC\\VLC64");
		}
		try{
			prerunCheckPlayer = new VLCMediaPlayer();
			vlcPathSet = true;
		}
		catch(Exception e){
			System.out.println("Can't find VLC native library. You need VLC " + System.getProperty("sun.arch.data.model") + "-bit.");
	
		}
		this.displays = displays;
		availableDisplays = displays;
	}
	
	/**
	 * Update the list of displays.
	 * Currently this method is not called from anywhere.
	 * @param displays
	 */
	public void updateDisplays(ArrayList<Integer> displays){
		for(Integer d : this.displays){
			if(!displays.contains(d)){
				if(availableDisplays.contains(d)){
					availableDisplays.remove(d);
				}

			}
		}
		for(Integer i : displays){
			if(!this.displays.contains(i)){
				availableDisplays.add(i);
			}
		}
		this.displays = displays;
		VLCMediaPlayer.updateDisplays();
	}
	
	/**
	 * Creates a media player and passes the options to it.
	 * The ID is the same as a timeline. 
	 * @param ID
	 * @param options
	 * @return
	 */
	public VLCMediaPlayer createMediaPlayer(int ID, String[] options){
		if(vlcPathSet){
			VLCMediaPlayer mp = new VLCMediaPlayer(ID, options);
			mediaPlayerList.put(ID, mp);
			return mp;
		}
		else{
			System.out.println("Can't create a VLC media player. VLC path not set");
		}
		return null;
	}
	
	/**
	 * Deletes a media player and frees the display from it.
	 * @param mp
	 */
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
	 * Used by updateOptions.
	 * @param mp
	 * @param options
	 */
	public void updateMediaPlayer(int mp, String[] options){
		int ID = toPlayer(mp).getID();
		int display = toPlayer(mp).getDisplay();
		deleteMediaPlayer(mp);
		createMediaPlayer(ID, options);
		if(display > -1){
			assignDisplay(ID, display);
		}
	}
	
	/**
	 * Update all the media players with some new options.
	 * The media players need to be paused before they can display something again.
	 * This is because new media players are created as some options can only be added at the creation of the media player.
	 * @param options
	 */
	public void updateOptions(String[] options){
		ArrayList<Integer> mps = new ArrayList<Integer>();
		for(int mp : mediaPlayerList.keySet()){
			mps.add(mp);
		}
		for(int mp : mps){
			updateMediaPlayer(mp, options);
		}
	}
	
	/**
	 * Assigns a display to media player.
	 * @param mp
	 * @param display
	 * @return
	 */
	public boolean assignDisplay(int mp, int display){
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
	 * Unassigns a display from a media player.
	 * @param mp
	 */
	public void unassignDisplay(int mp){
		if(mediaPlayerDisplayConnections.containsKey(mp)){
			availableDisplays.add(mediaPlayerDisplayConnections.remove(mp));
			toPlayer(mp).removeDisplay();
		}
	}
	
	/**
	 * Returns the media player associated with ID.
	 * @param ID
	 * @return */
	private VLCMediaPlayer toPlayer(int ID){
		return mediaPlayerList.get(ID);
	}
	
	/**
	 * Sends new media to a media player.
	 * Old media will continue to play until the media player plays or seeks again.
	 * @param mp
	 * @param mediaPath
	 */
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
		toPlayer(mp).play();
		while(!toPlayer(mp).isPlaying()){
			toPlayer(mp).play();
		}
	}

	/** * Pauses one specific media player. 
	 * * @param mp */
	public void pauseOne(int mp){
		toPlayer(mp).pause();
	}
	
	/**
	 * Seeks to point time in the specified media player.
	 * @param mp
	 * @param time
	 */
	public void seekOne(int mp, long time){
		toPlayer(mp).seek(time);
	}
	
	/**
	 * Stops a media player, removing any media from it and turns it black.
	 * @param mp
	 */
	public void stopOne(int mp){
		toPlayer(mp).stop();
	}
	
	/**
	 * Plays all media players at the exact same time.
	 * @throws InterruptedException
	 */
	public synchronized void playAll() throws InterruptedException{
		if(mediaPlayerDisplayConnections.size()>0){
			ArrayList<Thread> threads = new ArrayList<Thread>();
			final CyclicBarrier gate = new CyclicBarrier(mediaPlayerDisplayConnections.size());
			for(int mp : mediaPlayerDisplayConnections.keySet()){
				Thread t = new Thread(){
					public void run(){
						try {
							gate.await();
							toPlayer(mp).play();
						} catch (InterruptedException | BrokenBarrierException e) {e.printStackTrace();}
					}
				};
				t.start();
				threads.add(t);
			}
	//		try {
	//			gate.await();
	//		} catch (InterruptedException | BrokenBarrierException e) {e.printStackTrace();}
			for(int i= 0; i < threads.size(); i++){
				threads.get(i).join();
			}
		}
	}
	
	/**
	 * Pauses all media players at the exact same time
	 * @throws InterruptedException
	 */
	public synchronized void pauseAll() throws InterruptedException{
		if(mediaPlayerList.size()>0){
			ArrayList<Thread> threads1 = new ArrayList<Thread>();
			final CyclicBarrier gate = new CyclicBarrier(mediaPlayerList.size());
			for(int mp : mediaPlayerList.keySet()){
				Thread t1 = new Thread(){
					public void run(){
						try {
							gate.await();
							toPlayer(mp).pause();
						} catch (InterruptedException | BrokenBarrierException e) {e.printStackTrace();}
						
					}
				};
				t1.start();
				threads1.add(t1);
			}
	//		try {
	//			gate.await();
	//		} catch (InterruptedException | BrokenBarrierException e) {e.printStackTrace();}
			for(int i= 0; i < threads1.size(); i++){
				threads1.get(i).join();
			}
		}
	}
	
	/**
	 * Takes a map of media players and times. 
	 * Seeks to that time for each media player at the exact same time.
	 * @param map
	 * @throws InterruptedException */
	public synchronized void SeekMultiple(Map<Integer, Long> map) throws InterruptedException{
		if(mediaPlayerDisplayConnections.size()>0){
			ArrayList<Thread> threads = new ArrayList<Thread>();
			final CyclicBarrier gate = new CyclicBarrier(map.size());
			for(int mp : map.keySet()){
				Thread t = new Thread(){
					public void run(){
						try {
							gate.await();
							toPlayer(mp).seek(map.get(mp));
						} catch (InterruptedException | BrokenBarrierException e) {e.printStackTrace();}
					}
				};
				t.start();
				threads.add(t);
			}
	//		try {
	//			gate.await();
	//		} catch (InterruptedException | BrokenBarrierException e) {e.printStackTrace();}
			for(int i= 0; i < threads.size(); i++){
				threads.get(i).join();
			}
		}
	}
	
	/**
	 * Mutes or unmutes a media player.
	 * @param mp
	 * @param mute
	 */
	public void mute(int mp, boolean mute){
		if(mute){
			toPlayer(mp).mute();
		}
		else{
			toPlayer(mp).unmute();
		}
	}
	
	/**
	 * Shows or ides a media player.
	 * @param mp
	 * @param hide
	 */
	public void hide(int mp, boolean hide){
		if(hide){
			toPlayer(mp).hide();
		}
		else{
			toPlayer(mp).show();
		}
	}
	
	public void maximize(int mp){
		toPlayer(mp).maximize();
	}
		
	
	public void showmp(int mp,boolean show){
		toPlayer(mp).showhide(show);
	}
	
	/**
	 * Shows a huge number on each display for 5 seconds. Similar to the Windows identify function.
	 */
	public void identifyDisplays(){
		GraphicsDevice[] gs = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		
		Thread t = new Thread(){
			public void run(){
				ArrayList<JFrame> frames = new ArrayList<JFrame>();
				for(int i = 0; i < gs.length; i++){
					int textSize = gs[i].getDisplayMode().getHeight()/2;
					JLabel label = new JLabel("" + i);
					label.setForeground(Color.WHITE);
					label.setBackground(Color.BLACK);
					label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, textSize));
					label.setOpaque(true);
					label.setHorizontalAlignment(JLabel.CENTER);
				    label.setVerticalAlignment(JLabel.CENTER);
					JFrame frame = new JFrame(gs[i].getDefaultConfiguration());
					frame.setUndecorated(true);
					frame.getContentPane().add(label);
					frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
					frame.setVisible(true);
					frames.add(frame);
				}
				try {
					sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				for(int j = 0; j < gs.length; j++){
					frames.get(j).dispose();
				}
				for(Integer mp : mediaPlayerList.keySet()){
					if(mediaPlayerDisplayConnections.containsKey(mp)){
						toPlayer(mp).maximize();
					}
				}
			}		
		};
		t.start();
	}
	
	
	/**
	 * Returns true if mediaPath is a valid path and is playable.
	 * @param mediaPath
	 * @return */
	public long prerunCheck(String mediaPath){
		try{
			long length = prerunCheckPlayer.isPlayable(mediaPath);
			if(length != 0){
				return length;
			}
			return 0;
		}
		catch(Exception e) {
			return 0;
		}
		finally {
			prerunCheckPlayer.close();
		}
	}
	
	public String getJavaVersion(){
		return System.getProperty("sun.arch.data.model");
	}
	
	public String getVLCVersion(){
		return RuntimeUtil.getLibVlcLibraryName();
	}

	public Map<Integer, VLCMediaPlayer> getMediaPlayerList() {
		return mediaPlayerList;
	}
	
	
}
