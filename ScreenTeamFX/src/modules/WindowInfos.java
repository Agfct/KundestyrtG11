package modules;

import com.sun.jna.platform.win32.WinDef.HWND;

class WindowInfos
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
   public String getTitle(){
	   return title;
   }
   public String getProcess(){
	   return process;
   }
   public String getProcessFilePath(){
	   return processFilePath;
   }
   public HWND getThWnd(){
	   return hWnd;
   }
   
   public void setTitle(String nametitle){
	   this.title=nametitle;
   }
   public void setProcess(String nameprocess){
	   this.process=nameprocess;
   }
   public void setProcessFilePath(String nameprocessfilepath){
	   this.processFilePath=nameprocessfilepath;
   }
   public void setThWnd(HWND namehWnd){
	   this.hWnd=namehWnd;
   }

}
