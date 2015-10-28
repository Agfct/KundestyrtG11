package modules;


import com.sun.jna.platform.win32.WinDef.HWND;

public class WindowInfos
{
   String title, process, processFilePath;
   HWND hWnd;

   public WindowInfos(String title, String process, String processFilePath, HWND hWnd)
   {
       this.title = title;
       this.process = process;
       this.processFilePath = processFilePath;
       this.hWnd = hWnd;
 
   }

	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getProcess() {
		return process;
	}
	
	public void setProcess(String process) {
		this.process = process;
	}
	
	public String getProcessFilePath() {
		return processFilePath;
	}
	
	public void setProcessFilePath(String processFilePath) {
		this.processFilePath = processFilePath;
	}
	
	public HWND getThWnd() {
		return hWnd;
	}
	
	public void setThWnd(HWND hWnd) {
		this.hWnd = hWnd;
	}

   
}
