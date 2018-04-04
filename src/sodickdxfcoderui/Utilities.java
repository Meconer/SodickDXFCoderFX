/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sodickdxfcoderui;

import javafx.scene.control.Alert;

/**
 *
 * @author matsandersson
 */
public class Utilities {
    
    public static void showAlert(String alertText) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Fel!");
        alert.setContentText(alertText);
        alert.showAndWait();
    }

    static void showAboutBox() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Om");
        alert.setContentText("Om SodickDXFCoderFX\nVersion 0.01 2018-04-04");
        alert.showAndWait();
    }
    
}
