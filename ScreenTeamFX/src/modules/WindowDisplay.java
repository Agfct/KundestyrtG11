package modules;
import static modules.user32dll.*;
import static modules.kernel32dll.*;
import static modules.psapi32dll.*;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.swing.JFrame;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.ptr.PointerByReference;

import modules.user32dll.WNDENUMPROC;



/**
 * 
 * @author Baptiste
 *
 *
 */


public class WindowDisplay implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3407212486452142243L;
	private static final int MAX_TITLE_LENGTH = 1024;
	private HashMap <Integer,MonitorInfo> AllMonitor;
	public ArrayList<WindowInfos> AllWindow;
	
	public WindowDisplay(Integer nb_screen){
		InitialisationMonitor(nb_screen);
	}
	
	//Principal function	
	public void WindowManipulation(String NameWindow, boolean hide, Integer display) {
		
		WindowInfos WIs= new WindowInfos(null,null,null,null);
		
		WIs=getWindows(NameWindow);     
		if(WIs.getProcessFilePath()== null){	
			System.out.println("No Process File Path");
		}
		else{
					
			System.out.println("Monitor: " + AllMonitor.get(display).getNum_Monitor() +" x:"+ AllMonitor.get(display).getStart_X());
			
			if(hide==false){
				//Show the window
			
				//ShowWindow(WIs.getThWnd(),User32.SW_SHOWMAXIMIZED); 
				ShowWindow(WIs.getThWnd(),User32.SW_MAXIMIZE); 
				ResolutionScreen RS=new ResolutionScreen();
				RS.setResolutionScreen(display);
				MoveWindow(WIs.getThWnd(),AllMonitor.get(display).getStart_X(),AllMonitor.get(display).getStart_Y(),RS.getWidthResolutionScreen(),RS.getHeightResolutionScreen(),true);	
				System.out.println(AllMonitor.get(display).getStart_X());
				//ShowWindow(WIs.getThWnd(),User32.SW_SHOWMAXIMIZED); 
			}
			else if  (hide==true){
				//Hide the window
				ShowWindow(WIs.getThWnd(),User32.SW_SHOWMINIMIZED);
				MoveWindow(WIs.getThWnd(),AllMonitor.get(display).getStart_X(),AllMonitor.get(display).getStart_Y(),200,200,true);					
				System.out.println(AllMonitor.get(display).getStart_X());
				ShowWindow(WIs.getThWnd(),User32.SW_SHOWMINIMIZED);
			}
			else{
				System.out.println("boolean hide no assigned: "+hide);
			}
		}
			
	}

	public void InitialisationMonitor(int nb_screen){
		GraphicsDevice[] gs = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		Integer display;
		//Initialization of all Monitor
		AllMonitor = new HashMap();
		
		if( nb_screen > 0){
			for(display=0; display<nb_screen;display++){
				JFrame frame = new JFrame(gs[display].getDefaultConfiguration());
				MonitorInfo MI= new MonitorInfo();
				MI.setNum_Monitor(display);
				MI.setStart_X((int) frame.getLocation().getX());
				MI.setStart_Y((int) frame.getLocation().getY());
				AllMonitor.put(display, MI);
			}
		}
		
	}
		

	public void getAllWindows()
	   {
		   this.AllWindow = new ArrayList<WindowInfos>();	
	       final WindowInfos V = new WindowInfos(null,null,null,null);
	       EnumWindows(new WNDENUMPROC()
	       {
	           public boolean callback(Pointer hWndPointer, Pointer userData)
	           {
	               HWND hWnd = new HWND(hWndPointer);

              		
	               // Make sure the window is visible
	               if(IsWindowVisible(hWndPointer))
	               {
	                   int GWL_EXSTYLE = -20;
	                   long WS_EX_TOOLWINDOW = 0x00000080L;
	                   //------------------
	                   int [] rect = {0,0,0,0}; 
	                   
	                   //------------------
	                   // Make sure this is not a tool window
	                   if((GetWindowLongW(hWndPointer, GWL_EXSTYLE) & WS_EX_TOOLWINDOW) == 0)
	                   {
	                       // Get the title bar text for the window (and other info)
	                       WindowInfos info = getWindowTitleAndProcessDetails(hWnd);
	                       
	                       // Make sure the text is not null or blank
	                       if(!(info.title == null || info.title.trim().equals("")))
	                       {
	                    	                       	                      	   
	                    	   AllWindow.add(info);

	                       }
	                   }
	               }
	               
	               return true;
	           }
	       }, null);
	      
	   }

	private WindowInfos getWindows(String NameTitle)
	   {
	       final WindowInfos V = new WindowInfos(null,null,null,null);
	       EnumWindows(new WNDENUMPROC()
	       {
	           public boolean callback(Pointer hWndPointer, Pointer userData)
	           {
	               HWND hWnd = new HWND(hWndPointer);

           		
	               // Make sure the window is visible
	               if(IsWindowVisible(hWndPointer))
	               {
	                   int GWL_EXSTYLE = -20;
	                   long WS_EX_TOOLWINDOW = 0x00000080L;
	                   //------------------
	                   int [] rect = {0,0,0,0}; 
	                   
	                   //------------------
	                   // Make sure this is not a tool window
	                   if((GetWindowLongW(hWndPointer, GWL_EXSTYLE) & WS_EX_TOOLWINDOW) == 0)
	                   {
	                       // Get the title bar text for the window (and other info)
	                       WindowInfos info = getWindowTitleAndProcessDetails(hWnd);
	                       
	                       // Make sure the text is not null or blank
	                       if(!(info.title == null || info.title.trim().equals("")))
	                       {
	                    	   
	                    	   List<String> AllWindow = new ArrayList<String>();	                    	   
	                    	   AllWindow.add(info.getTitle());
	                    	   
		                    	if((info.title.equals(NameTitle))) {
		                    	   System.out.println(" Window " + NameTitle + " Find");
		                    	   GetWindowRect(hWnd, rect);
	                    		   V.setTitle(info.getTitle());
	                    		   V.setProcess(info.getProcess());
	                    		   V.setProcessFilePath(info.getProcessFilePath());
	                    		   V.setThWnd(info.getThWnd());	                    
		                       }
	                       }
	                   }
	               }
	               
	               return true;
	           }
	       }, null);
	       return V;
	   }

	
	

	private WindowInfos getWindowTitleAndProcessDetails(HWND hWnd) {
  
		if(hWnd == null){
			return null;
		}
	    char[] buffer = new char[MAX_TITLE_LENGTH * 2];
	    GetWindowTextW(hWnd, buffer, MAX_TITLE_LENGTH);
	    String title = Native.toString(buffer);
	
	    PointerByReference pointer = new PointerByReference();
	    GetWindowThreadProcessId(hWnd, pointer);    //GetForegroundWindow()
	    Pointer process = OpenProcess(PROCESS_QUERY_INFORMATION | PROCESS_VM_READ, false, pointer.getValue());
	    GetModuleBaseNameW(process, null, buffer, MAX_TITLE_LENGTH);
	    String Sprocess = Native.toString(buffer);
	    GetModuleFileNameExW(process, null, buffer, MAX_TITLE_LENGTH);
	    String SprocessFilePath = Native.toString(buffer);
	
	    return new WindowInfos(title, Sprocess, SprocessFilePath, hWnd);
	}

	


}