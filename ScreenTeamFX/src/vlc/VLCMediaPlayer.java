package vlc;

import javax.swing.JFrame;

import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;

import java.awt.Color;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.Arrays;


public class VLCMediaPlayer{
	private JFrame frame = new JFrame();
	private EmbeddedMediaPlayerComponent mediaPlayerComponent;
	private String mediaPath = "";
	private static GraphicsDevice[] gs = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
	private int display = -1;
	private int ID;
	private String[] libvlcOptions;
	private boolean mediaChanged = false;
	protected static final String[] DEFAULT_FACTORY_ARGUMENTS = {
	        "--video-title=vlcj video output",
	        "--no-snapshot-preview",
	        "--quiet-synchro",
	        "--sub-filter=logo:marq",
	        "--intf=dummy"
	    }; 
	
	public VLCMediaPlayer(int ID, String[] options){
		this.ID = ID;
		mediaPlayerComponent = new EmbeddedMediaPlayerComponent() {
			private static final long serialVersionUID = 1L;

			protected String[] onGetMediaPlayerFactoryArgs() {
				String[] mergedOptions = concat(DEFAULT_FACTORY_ARGUMENTS, options);
				return mergedOptions;
			}
		};
		libvlcOptions = options;
		frame.getContentPane().setBackground(Color.BLACK);
		frame.setUndecorated(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(mediaPlayerComponent);
		frame.setTitle("scr " + ID);
		mute();
		show();
	}
	
	/**
	 * Constructor for prerunChecker. */	
	public VLCMediaPlayer(){
		mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
		frame.setUndecorated(true);
		frame.getContentPane().add(mediaPlayerComponent);
		frame.setSize(0, 0);
	}
	
	public void play(){
		if(display > -1){
			if(mediaChanged){
				mediaPlayerComponent.getMediaPlayer().startMedia(mediaPath);
				mediaChanged = false;
			}
			else if(getTime() > -1){
				mediaPlayerComponent.getMediaPlayer().start();
			}
			else{
				System.out.println("No video attached");
			}
		}
	}
	
	public void pause(){
		if(isPlaying()){
			mediaPlayerComponent.getMediaPlayer().pause();
		}
	}
	
	public void seek(long time){
		if(mediaChanged){
			mediaPlayerComponent.getMediaPlayer().startMedia(mediaPath);
			mediaPlayerComponent.getMediaPlayer().pause();
			mediaPlayerComponent.getMediaPlayer().setTime(time);
			mediaChanged = false;
		}
		else if(getTime() > -1){
			pause();
			mediaPlayerComponent.getMediaPlayer().setTime(time);
		}
		else{
			System.out.println("No video attached");
		}
		while(isPlaying());
	}	
	
	public void stop(){
		mediaPath = "";
		mediaPlayerComponent.getMediaPlayer().stop();
		frame.setState(java.awt.Frame.ICONIFIED);
		frame.setState(java.awt.Frame.NORMAL);
	}
	
	public void setMedia(String mediaPath){
		mediaPlayerComponent.getMediaPlayer().stop();
		mediaPlayerComponent.getMediaPlayer().prepareMedia(mediaPath);
		this.mediaPath = mediaPath;
		mediaChanged = true;
	}
	
	public void mute(){
		mediaPlayerComponent.getMediaPlayer().mute(true);
	}
	
	public void unmute(){
		mediaPlayerComponent.getMediaPlayer().mute(false);
	}
	
	public void maximize(){
		frame.setState(java.awt.Frame.NORMAL);
	}
	
	public void hide(){
		mediaPlayerComponent.setVisible(false);
		frame.setState(java.awt.Frame.ICONIFIED);
		frame.setState(java.awt.Frame.NORMAL);
	}
	
	public void show(){
		mediaPlayerComponent.setVisible(true);
		frame.setState(java.awt.Frame.ICONIFIED);
		frame.setState(java.awt.Frame.NORMAL);
	}
	
	public void showhide(boolean show){
		frame.setVisible(show);
	}
	
	/**
	 * Creates a new Jframe on a new graphicsDevice. Must use setMedia before media can be played again. 
	 * @param display */
	public void setDisplay(int display){
		this.display = display;
		frame.getContentPane().remove(mediaPlayerComponent);
		frame.dispose();
		frame = new JFrame(gs[display].getDefaultConfiguration ());
		frame.getContentPane().setBackground(Color.BLACK);
		frame.setTitle("scr " + ID);
		frame.setUndecorated(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.getContentPane().add(mediaPlayerComponent);
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
		return mediaPlayerComponent.getMediaPlayer().isPlaying();
	}
	
	public int getID(){
		return this.ID;
	}
	
	public String getMediaPath(){
		return mediaPath;
	}
	
	public String[] getOptions(){
		return libvlcOptions;
	}
	
	public long getTime(){
		return mediaPlayerComponent.getMediaPlayer().getTime();
	}
	
	public long isPlayable(String mediaPath){
		frame.setVisible(true);
		mediaPlayerComponent.getMediaPlayer().startMedia(mediaPath);
		return mediaPlayerComponent.getMediaPlayer().getLength();
	}
	
	public void close(){
		mediaPlayerComponent.getMediaPlayer().stop();
		frame.dispose();
	}
	
	static String[] concat(String[]... arrays) {
	    int length = 0;
	    for (String[] array : arrays) {
	        length += array.length;
	    }
	    String[] result = new String[length];
	    int pos = 0;
	    for (String[] array : arrays) {
	        for (String element : array) {
	            result[pos] = element;
	            pos++;
	        }
	    }
	    return result;
	}
}