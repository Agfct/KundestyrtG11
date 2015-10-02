package vlc;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import com.sun.jna.NativeLibrary;

import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Window;
import java.util.ArrayList;


public class VLCMediaPlayer {
	private JFrame frame = new JFrame();
	private EmbeddedMediaPlayerComponent mp;
	private String mediaPath = "";
	private static GraphicsDevice[] gs = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
	private int display;
	
	public VLCMediaPlayer(int display){
		this.display = display;
		mp = new EmbeddedMediaPlayerComponent();
		frame.setUndecorated(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(mp);
		frame.setVisible(true);
		showOnDisplay(display);
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
		if(getTime() > 0){
			mp.getMediaPlayer().play();
		}
		else if(mediaPath != ""){
			mp.getMediaPlayer().playMedia(mediaPath);
		}
	}
	
	public void pause(){
		mp.getMediaPlayer().pause();
	}
	
	public void seek(long time){
		if(getTime() < 0){
			mp.getMediaPlayer().playMedia(mediaPath);
		}
		mp.getMediaPlayer().setTime(time);
	}	
	
	public boolean isPlaying(){
		return mp.getMediaPlayer().isPlaying();
	}
	
	public void setDisplay(int display){
		this.display = display;
		showOnDisplay(display);
	}
	
	public void setMedia(String mediaPath){
		this.mediaPath = mediaPath;
		mp.getMediaPlayer().playMedia(mediaPath);
	}
	
	public int getDisplay(){
		return this.display;
	}
	
	public long getTime(){
		return mp.getMediaPlayer().getTime();
	}
	
	public void showOnDisplay(int display){
	    if(display > -1 && display < gs.length){
	        gs[display].setFullScreenWindow((Window)frame);
	    }
	    else{
	        throw new RuntimeException( "No Displays Found" );
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
		pause();
		frame.dispose();
	}
}