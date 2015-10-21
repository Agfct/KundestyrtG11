package vlc;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.SwingConstants;

import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;

import java.awt.Color;
import java.awt.Font;
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
		mp.setBackground(new Color(255, 255, 255));
		frame.setUndecorated(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(mp);
	}
	
	/**
	 * Constructor for prerunChecker. */	
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
			else if(getTime() > -1){
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
			pause();
			mp.getMediaPlayer().setTime(time);
		}
		else{
			System.out.println("No video attached");
		}
		while(isPlaying());
	}	
	
	public void stop(){
		mp.getMediaPlayer().stop();
	}
	
	public void setMedia(String mediaPath){
		mp.getMediaPlayer().stop();
		this.mediaPath = mediaPath;
		mediaChanged = true;
	}
	
	public void mute(){
		mp.getMediaPlayer().mute(true);
	}
	
	public void unmute(){
		mp.getMediaPlayer().mute(false);
	}
	
	public void maximize(){
		frame.setState(java.awt.Frame.NORMAL);
	}
	
	/**
	 * Creates a new Jframe on a new graphicsDevice. Must use setMedia before media can be played again. 
	 * @param display */
	public void setDisplay(int display){
		this.display = display;
		frame.getContentPane().remove(mp);
		frame.dispose();
		frame = new JFrame(gs[display].getDefaultConfiguration ());
		frame.setUndecorated(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(mp);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setVisible(true);
	}
	
	/**
	 * Preferred method for changing displays. Only works on Windows 8.
	 * @param display */
	public void setDisplayWin8(int display){
		this.display = display;
        gs[display].setFullScreenWindow(frame);
        frame.setVisible(true);
	}
	
	public void removeDisplay(){
		if(System.getProperty("os.name").startsWith("Windows 8")){
			gs[display].setFullScreenWindow(null);
		}
		else{
			frame.getContentPane().removeAll();
			frame.dispose();
		}
		display = -1;
	}
	
	public static void updateDisplays(){
		gs = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
	}
	
	public int getDisplay(){
		return this.display;
	}
	
	public boolean isPlaying(){
		return mp.getMediaPlayer().isPlaying();
	}
	
	public int getID(){
		return this.ID;
	}
	
	public String getMediaPath(){
		return mediaPath;
	}
	
	public long getTime(){
		return mp.getMediaPlayer().getTime();
	}
	
	public long isPlayable(String mediaPath){
		frame.setVisible(true);
		mp.getMediaPlayer().startMedia(mediaPath);
		return mp.getMediaPlayer().getLength();
	}
	
	public void close(){
		mp.getMediaPlayer().stop();
		frame.dispose();
	}
}