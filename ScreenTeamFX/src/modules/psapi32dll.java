package modules;

import com.sun.jna.Native;
import com.sun.jna.Pointer;

/**
 * 
 * @author Baptiste
 *
 */

public class psapi32dll {
       static {   Native.register("psapi");   }
       public static native int GetModuleBaseNameW(Pointer hProcess, Pointer hmodule, char[] lpBaseName, int size);
       public static native int GetModuleFileNameExW(Pointer hProcess, Pointer hmodule, char[] lpBaseName, int size);
       
   }
