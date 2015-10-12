package vlc;

import javax.swing.JFrame;

import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;


public class VLCMediaPlayer {
	private JFrame frame = new JFrame();
	private EmbeddedMediaPlayerComponent mp;
	private String mediaPath = "";
	private static GraphicsDevice[] gs = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
	private int display = -1;
	private int ID;
	private boolean mediaChanged = false;
	
	public VLCMediaPlayer(int ID){
		this.ID = ID;
		mp = new EmbeddedMediaPlayerComponent();
		frame.setUndecorated(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(mp);
	}
	
	/**
	 * Constructor for prerunChecker. Does not display the media player.
	 */	
	public VLCMediaPlayer(){
		mp = new EmbeddedMediaPlayerComponent();
		frame.setUndecorated(true);
		frame.getContentPane().add(mp);
		frame.setSize(0, 0);
	}
	
	public void play(){
		if(display > -1){
			if(mediaChanged){
				mp.getMediaPlayer().startMedia(mediaPath);
				mediaChanged = false;
			}
			else if(getTime() > 0){
				mp.getMediaPlayer().start();
			}
			else{
				System.out.println("No video attached");
			}
		}
	}
	
	public void pause(){
		if(isPlaying()){
			mp.getMediaPlayer().pause();
		}	
	}
	
	public void seek(long time){
		if(mediaChanged){
			mp.getMediaPlayer().startMedia(mediaPath);
			mp.getMediaPlayer().pause();
			mp.getMediaPlayer().setTime(time);
			mediaChanged = false;
		}
		else if(getTime() > -1){
			mp.getMediaPlayer().pause();
			mp.getMediaPlayer().setTime(time);
		}
		else{
			System.out.println("No video attached");
		}
	}	
	
	public void stop(){
		mp.getMediaPlayer().stop();
	}
	
	public void setMedia(String mediaPath){
		mp.getMediaPlayer().stop();
		this.mediaPath = mediaPath;
		mediaChanged = true;
	}
	
	public boolean isPlaying(){
		return mp.getMediaPlayer().isPlaying();
	}
	
	public void setDisplay(int display){
		this.display = display;
		showOnDisplay(display);
	}
	
	
	public int getDisplay(){
		return this.display;
	}
	
	public int getID(){
		return this.ID;
	}
	
	public long getTime(){
		return mp.getMediaPlayer().getTime();
	}
	
	public void showOnDisplay(int display){
	    if(display > -1 && display < gs.length){
			frame.setVisible(true);
	        gs[display].setFullScreenWindow(frame);
	    }
	    else{
	        System.out.println("Display not found");
	    }
	}
	
	public boolean isPlayable(String mediaPath){
		frame.setVisible(true);
		mp.getMediaPlayer().playMedia(mediaPath);
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
		}
		return isPlaying();
	}
	
	public void close(){
		mp.getMediaPlayer().stop();
		frame.dispose();
	}
}