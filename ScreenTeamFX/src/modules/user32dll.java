package modules;


import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef.*;
import com.sun.jna.ptr.PointerByReference;


	/**
	 * 
	 * @author Baptiste
	 *
	 */
//Create a "bridge" between the library user32.dll and the java program
//This library permit to control the user interface

public class user32dll{
		//Find the library
	    static { Native.register("user32");}
	      
	    //Re-declaration of the Windows function but in Java
	    public static native boolean ShowWindow(HWND hWnd,int  nCmdShow);
	    public static native boolean IsWindowVisible(HWND hWnd);
	    public static native HWND GetDesktopWindow();
	    public static native boolean MoveWindow(HWND hWnd, int x, int y, int nWidth, int nHeight, boolean bRepaint);
	    public static native boolean DestroyWindow(HWND hWnd);
	    // Monitor display		

	    public static native int GetWindowThreadProcessId(HWND hWnd, PointerByReference pref);
	    public static native int GetWindowTextW(HWND hWnd, char[] lpString, int nMaxCount);
	    public static native boolean GetWindowRect(HWND hWnd, int[] rect);
	    public static native boolean EnumWindows(WNDENUMPROC lpEnumFunc, Pointer arg);
	    public static interface WNDENUMPROC extends com.sun.jna.win32.StdCallLibrary.StdCallCallback  {  boolean callback(Pointer hWnd, Pointer arg); }
	    public static native boolean IsWindowVisible(Pointer hWnd);
	    public static native boolean SetForegroundWindow(HWND hWnd);
	    public static native int GetWindowLongW(Pointer hWnd, int nIndex);

	}