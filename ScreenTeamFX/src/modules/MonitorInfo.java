package modules;

import java.io.Serializable;

public class MonitorInfo implements Serializable{

		/**
	 * 
	 */
	private static final long serialVersionUID = 2321244859141548542L;
		int Num_Monitor;
		int Start_X, Start_Y;

		
		public int getNum_Monitor() {
			return Num_Monitor;
		}
		public void setNum_Monitor(int num_Monitor) {
			Num_Monitor = num_Monitor;
		}
		public int getStart_X() {
			return Start_X;
		}
		public void setStart_X(int start_X) {
			Start_X = start_X;
		}
		public int getStart_Y() {
			return Start_Y;
		}
		public void setStart_Y(int start_Y) {
			Start_Y = start_Y;
		}
		
}
