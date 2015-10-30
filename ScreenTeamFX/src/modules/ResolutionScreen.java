package modules;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
/*
 *  Baptiste
 */
public class ResolutionScreen {

	private static GraphicsDevice[] gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
	private int width;
	private int height;
	
	public ResolutionScreen(){
		this.width = 0;
		this.height = 0;
	}
	 public void setResolutionScreen(int NumberScreen){
		this.width = gd[NumberScreen].getDisplayMode().getWidth();
		this.height = gd[NumberScreen].getDisplayMode().getHeight();
	 }
	
	public int getWidthResolutionScreen(){
		return this.width;
	}
	public int getHeightResolutionScreen(){
		return this.height;
	}
}
