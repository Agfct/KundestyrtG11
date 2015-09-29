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
	private boolean isPlaying = false;
	
	public VLCMediaPlayer(int display){
		this.display = display;
		mp = new EmbeddedMediaPlayerComponent();
		frame.setUndecorated(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(mp);
		frame.setVisible(true);
		showOnDisplay(display, this.frame);
	}
	
	public VLCMediaPlayer(){
		
	}
	
//	public void identifyScreen(){
//		label.setText("" + (display + 1));
//		label.setForeground(Color.WHITE);
//		label.setBackground(Color.BLACK);
//		label.setOpaque(true);
//		label.setFont(new Font("test", Font.BOLD, 800));
//		frame.getContentPane().removeAll();
//		frame.getContentPane().add(label);
//		frame.setVisible(true);
//	}
	
	public void play(){
		if(getTime() > 0){
			mp.getMediaPlayer().play();
			isPlaying = true;
		}
		else if(mediaPath != ""){
			mp.getMediaPlayer().playMedia(mediaPath);
			isPlaying = true;
		}
	}
	
	public void play(String mediaPath){
		this.mediaPath = mediaPath;
		mp.getMediaPlayer().playMedia(mediaPath);
		isPlaying = true;
	}
	
	public void pause(){
		isPlaying = false;
		mp.getMediaPlayer().pause();
	}
	
	public void fastForward(int speed){
		if(getTime() < 0){
			play();
		}
		pause();
		while(!isPlaying){
			seek(getTime() + speed*200);
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
			}
			if(getTime() > 1400000){
				break;
			}
		}
	}
	
	public void rewind(int speed){
		pause();
		while(!isPlaying){
			seek(getTime() - speed*200);
			System.out.println(getTime());
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
			}
			if(getTime() == 0){
				break;
			}
		}
	}
	
	public void seek(long time){
		System.out.println(isPlaying);
		if(getTime() < 0){
			play();
			pause();
		}
		mp.getMediaPlayer().setTime(time);
		System.out.println(isPlaying);
		if(!isPlaying){
			pause();
		}
	}
	
	public void restart(){
		if(getTime() < 0){
			play();
		}
		else{
			seek(0);
		}
	}
	
	public void close(){
		pause();
		frame.dispose();
	}
	
	public boolean isPlaying(){
		return isPlaying;
	}
	
	public long getTime(){
		return mp.getMediaPlayer().getTime();
	}
	
	public void changeVideo(String mediaPath, boolean play){
		play(mediaPath);		
		if(!play){
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
			}
			pause();	
		}
	}
	
	
	public void mute(){
		if(!mp.getMediaPlayer().isMute()){
			mp.getMediaPlayer().mute(true);
		}
	}
	
	public void unmute(){
		if(mp.getMediaPlayer().isMute()){
			mp.getMediaPlayer().mute(false);
		}
	}
	
	public int getDisplays(){
		return this.display;
	}
	
	public void setDisplay(int display){
		this.display = display;
		showOnDisplay(display, this.frame);
	}
	
	
	public void setMedia(String path){
		mediaPath = path;
	}
	
	public void showOnDisplay(int display, JFrame frame){
	    if(display > -1 && display < gs.length){
	        gs[display].setFullScreenWindow((Window)frame);
	    }
	    else{
	        throw new RuntimeException( "No Displays Found" );
	    }
	}
}

