package modules;

import com.sun.jna.Native;
import com.sun.jna.Pointer;

/**
 * 
 * @author Baptiste
 *
 */

//Create a "bridge" between the library kernel.dll and the java program
//This library give access to the fundamental ressources for Windows
// Gestion de fichier, input/output, processus, exeption, ..

public class kernel32dll{	
    static   {   Native.register("kernel32");  }
    public static int PROCESS_QUERY_INFORMATION = 0x0400;
    public static int PROCESS_VM_READ = 0x0010;
    public static native Pointer OpenProcess(int dwDesiredAccess, boolean bInheritHandle, Pointer pointer);
}

