package UtilPkg;

import java.io.File;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import sodickdxfcoderui.SodickDxfCoderFXPreferences;

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

    public static void saveToFile( String cncProgram, String fileName ) {

    }

    public static String stripExtension(String fileName) {
        if (fileName.contains(".")) {
            return fileName.substring(0, fileName.lastIndexOf('.'));
        } else {
            return fileName;
        }
    }
}
