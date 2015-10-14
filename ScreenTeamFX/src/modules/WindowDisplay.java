package modules;
import static modules.user32dll.*;
import static modules.kernel32dll.*;
import static modules.psapi32dll.*;

import java.util.ArrayList;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.ptr.PointerByReference;

/**
 * 
 * @author Baptiste
 *
 */

public class WindowDisplay {
	
	//Declaration variable global
	private static final int MAX_TITLE_LENGTH = 1024;
	private ArrayList<WindowInfos> AllWIs;
	
	//Principal function	
	public WindowDisplay(String NameWindow, boolean hide) {
		WindowInfos WIs= new WindowInfos(null,null,null,null);
		
		WIs=getWindows(NameWindow);		
		if(WIs.getProcessFilePath()== null){	
			System.out.println("No Process File Path");
		}
		else{
			if(hide==false){
				//Show the window
				ShowWindow(WIs.getThWnd(),1); 
			}
			else if  (hide==true){
				//Hide the window
				ShowWindow(WIs.getThWnd(),2);
			}
			else{
				System.out.println("boolean hide no assigned: "+hide);
			}
					
			
	//		if(!(SetForegroundWindow(WIs.getThWnd()))){
	
	//		}
	
		}
 		
	}
	public WindowDisplay(String NameWindow,int hide) {
		WindowInfos WIs= new WindowInfos(null,null,null,null);	
		WIs=getWindows(NameWindow);		
		if(WIs.getProcessFilePath()== null){	
			System.out.println("No Process File Path");
		}
		else{
			//Hide the window
			ShowWindow(WIs.getThWnd(),2);
		}
		
	}
	
	public void addWindowInfos(HWND hWnd){
		
		AllWIs.add(getWindowTitleAndProcessDetails(hWnd));
	}
	
	// TODO: We are not sure which removeTimeline to use per now.
	public void removeWindow(String title){
		// Find the timeline in the timelines list and remove it
		for(int i=0; i<AllWIs.size();i++){
			if(title==AllWIs.get(i).getTitle()){
				unassignWindow(AllWIs.get(i).getTitle());
				AllWIs.remove(i);
			}
			
		}
	}
	
	public void unassignWindow(String title){
		//TODO: Go through all Windows
		int i;
		//Test to know is there is a windows
		if(AllWIs.isEmpty()){
		}
		else{
			//Check every displays
			for(i=0; i<AllWIs.size(); i++){
				if(AllWIs.get(i).getTitle()==title){
					AllWIs.remove(i);
				}
			}		
		}
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

	                   // Make sure this is not a tool window
	                   if((GetWindowLongW(hWndPointer, GWL_EXSTYLE) & WS_EX_TOOLWINDOW) == 0)
	                   {
	                       // Get the title bar text for the window (and other info)
	                       WindowInfos info = getWindowTitleAndProcessDetails(hWnd);

	                       // Make sure the text is not null or blank
	                       if(!(info.title == null || info.title.trim().equals("")))
	                       {
		                    	//System.out.println("title: "+info.getTitle());
		                		//System.out.println("NameTitle: "+ NameTitle);
		                   		//System.out.println("Process: "+info.getProcess());
		                   		//System.out.println("Path: "+info.getProcessFilePath());
		                   		//System.out.println("hWn: "+ info.getThWnd());
		                   		
		                    	if((info.title.equals(NameTitle))) {
		                    		System.out.println("Find");
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