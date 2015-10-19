package modules;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;

public class IOModule {
	private ArrayList<Integer> displays;
	
	public IOModule(){
		displays = new ArrayList<Integer>();
		findConnectedDisplays();
	}
	
	public boolean findConnectedDisplays(){
		ArrayList<Integer> temp = new ArrayList<Integer>();
		for(int d: displays){
			temp.add(d);
		}
		displays.clear();
	    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	    GraphicsDevice[] gs = ge.getScreenDevices();
	    for(int i = 0; i < gs.length; i++){
	    	displays.add(i);
	    }
	    if(temp.equals(displays)){
	    	return false;
	    }
	    return true;
	}
	
	
	public ArrayList<Integer> getDisplays(){
		return displays;
	}
}
