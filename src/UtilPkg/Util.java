package UtilPkg;

import javafx.scene.control.Alert;

/**
 *
 * @author Mats Andersson <mats.andersson@mecona.se>
 */
public class Util {
    public static void reportError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Fel!");
        alert.setContentText(message);
        alert.show();
    }
}
