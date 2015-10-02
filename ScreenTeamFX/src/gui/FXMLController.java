package gui;

import javafx.fxml.FXMLLoader;

/**
 * @author Anders Lunde
 *
 *All FXMLController classes inherits from the FXMLController interface, 
 *making the methods for the FXML Controllers the same for every controller.
 */
public interface FXMLController {
	
	/**
	 * Returns the FXMLLoader for the Controller
	 * @return
	 */
	public FXMLLoader getFXMLLoader();
	

}
