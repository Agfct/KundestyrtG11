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
	protected static final String[] DEFAULT_FACTORY_ARGUMENTS_2 = {
	        "--video-title=vlcj video output",
	        "--no-snapshot-preview",
	        "--quiet-synchro",
	        "--sub-filter=logo:marq",
	        "--intf=dummy"
	    }; 
	
	/**
	 * Creates a new media player and adds the default factory arguments plus an optional list of options to it.
	 * 
	 * @param ID
	 * @param options
	 */
	public VLCMediaPlayer(int ID, String[] options){
		this.ID = ID;
		mediaPlayerComponent = new EmbeddedMediaPlayerComponent() {
			private static final long serialVersionUID = 1L;

			protected String[] onGetMediaPlayerFactoryArgs() {
				String[] mergedOptions = concat(DEFAULT_FACTORY_ARGUMENTS, options);
				System.out.println("vlc options:" + Arrays.toString(mergedOptions));
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
	 * Constructor for prerunChecker. Size is set to 0*0 because the frame must be displayed to start any media.	
	 */
	public VLCMediaPlayer(){
		mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
		frame.setUndecorated(true);
		frame.getContentPane().add(mediaPlayerComponent);
		frame.setSize(0, 0);
	}
	
	/**
	 * If new media has been sent to the media player, it will play the new media from the beginning.
	 * If some media is already in the media player and is paused it will play from where it is paused.
	 */
	public void play(){
		System.out.println("[PLAY]");
		if(display > -1 && !isPlaying()){
			if(mediaChanged){
				System.out.println("[mediaChanged!]");
				mediaPlayerComponent.getMediaPlayer().startMedia(mediaPath);
				mediaChanged = false;
			}
			else if(getTime() > -1){
				System.out.println("[getTime>-1]");
				mediaPlayerComponent.getMediaPlayer().play();
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
	
	/**
	 * If new media has been sent to the media player, 
	 * the media player will first start the new media and then seek to the point time and pause it.
	 * If some media is already in the media player,
	 * the media player will seek to the point time and pause it.
	 * @param time
	 */
	public void seek(long time){
		System.out.println("[SEEK]");
		if(mediaChanged){
			System.out.println("[mediaChanged!]");
			mediaPlayerComponent.getMediaPlayer().startMedia(mediaPath);
			mediaPlayerComponent.getMediaPlayer().pause();
			mediaPlayerComponent.getMediaPlayer().setTime(time);
			mediaChanged = false;
		}
		else if(getTime() > -1){
			System.out.println("[getTime>-1]");
			pause();
			mediaPlayerComponent.getMediaPlayer().setTime(time);
		}
		else{
			System.out.println("No video attached");
		}
		while(isPlaying());
	}	
	
	/**
	 * Removes any media from the media player, turning the media player black.
	 * Some times the frame would become white afterwards and in order to fix this 
	 * the frame is simply minimized and then maximized again.
	 */
	public void stop(){
		mediaPath = "";
		mediaPlayerComponent.getMediaPlayer().stop();
		frame.setState(java.awt.Frame.ICONIFIED);
		frame.setState(java.awt.Frame.NORMAL);
	}
	
	/**
	 * Sends some new media to the media player.
	 * The old media will continue to play until play or seek it called.
	 * @param mediaPath
	 */
	public void setMedia(String mediaPath){
		mediaPlayerComponent.getMediaPlayer().stop();
		mediaPlayerComponent.getMediaPlayer().prepareMedia(mediaPath);
		this.mediaPath = mediaPath;
		mediaChanged = true;
	}
	
	/**
	 * Mute the sound from the media. A video will still show.
	 */
	public void mute(){
		mediaPlayerComponent.getMediaPlayer().mute(true);
	}
	
	public void unmute(){
		mediaPlayerComponent.getMediaPlayer().mute(false);
	}
	
	public void maximize(){
		frame.setState(java.awt.Frame.NORMAL);
	}
	
	/**
	 * Hide the video. Sound will still play.
	 */
	public void hide(){
		mediaPlayerComponent.setVisible(false);
		frame.setState(java.awt.Frame.ICONIFIED);
		frame.setState(java.awt.Frame.NORMAL);
	}
	
	/**
	 * Show the video again after hiding it.
	 */
	public void show(){
		mediaPlayerComponent.setVisible(true);
		frame.setState(java.awt.Frame.ICONIFIED);
		frame.setState(java.awt.Frame.NORMAL);
	}
	
	/**
	 * 
	 * @param show
	 */
	public void showhide(boolean show){
		frame.setVisible(show);
	}
	
	/**
	 * Binds a GraphicsDevice (representing a display connected to your computer) to a JFrame.
	 * This will bring up the media player on the display, initially showing just the black background.
	 * In order to find out which display corresponds to which number, call the identify function in VLCController.
	 * @param display
	 */
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
	 * This method does the same as the one above, but is more sophisticated. 
	 * It allows for media players to change display almost instantaneously during runtime.
	 * Sadly it will not work on Windows 7 or 10. So far it is only confirmed to work on Windows 8.
	 * @param display */
	public void setDisplayWin8(int display){
		this.display = display;
        gs[display].setFullScreenWindow(frame);
        frame.setVisible(true);
	}
	
	/**
	 * Frees the display from the media player.
	 */
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
	
	/**
	 * Returns true if a video or audio is currently playing on the media player.
	 * @return
	 */
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
	
	/**
	 * Used to check if a media file is valid and returns its length.
	 * @param mediaPath
	 * @return
	 */
	public long isPlayable(String mediaPath){
		frame.setVisible(true);
		mediaPlayerComponent.getMediaPlayer().startMedia(mediaPath);
		return mediaPlayerComponent.getMediaPlayer().getLength();
	}
	
	/**
	 * Disposes the JFrame that the media player is attached to, deleting it in the process.
	 */
	public void close(){
		mediaPlayerComponent.getMediaPlayer().stop();
		frame.dispose();
	}
	
	/**
	 * Used in the constructor to merge the default options with those passed as argument.
	 * @param arrays
	 * @return
	 */
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