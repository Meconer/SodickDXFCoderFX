package UtilPkg;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import sodickdxfcoderui.SodickDxfCoderFXPreferences;

/**
 *
 * @author Mats Andersson <mats.andersson@mecona.se>
 */
public class Util {

    public static final String ERROR_STRING = "*ERR*";
    
    public static void reportError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Fel!");
        alert.setContentText(message);
        alert.show();
    }

    public static String stripExtension(String fileName) {
        if (fileName.contains(".")) {
            return fileName.substring(0, fileName.lastIndexOf('.'));
        } else {
            return fileName;
        }
    }
    
    public static void saveToFile(String cncProgram, String fileName) {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("CNC-filer", "*.nc"));
        String initialDirectory = 
                SodickDxfCoderFXPreferences.getInstance()
                        .getDefaultDirectory();
        
        fc.setInitialFileName(fileName);
        fc.setInitialDirectory( new File(initialDirectory));
        File saveFile = fc.showSaveDialog( null );
        if ( saveFile != null ) {
            try {
                Path path = Paths.get(saveFile.getAbsolutePath());
                if ( Files.exists(path, LinkOption.NOFOLLOW_LINKS));
                               Files.write( path, cncProgram.getBytes() );
            } catch (IOException ex) {
                reportError("Kan inte spara filen");
            }
        }
    }

    public static String convertToDecimal(String textToConvert, String errorMessage ) {

        double value;
        String result = ERROR_STRING;
    
        try {
            value = Double.parseDouble(textToConvert);
            DecimalFormat df = new DecimalFormat("0.0###");
            df.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
            result = df.format(value);
        } catch (NumberFormatException e) {
            reportError(errorMessage);
        }

        return result;
    }
    
    public static Point2D.Double distanceBetweenPoints( Point2D.Double p1, Point2D.Double p2 ) {
        return new Point2D.Double(
                p1.getX() - p2.getX(),
                p1.getY() - p2.getY()
        );
    }
}
