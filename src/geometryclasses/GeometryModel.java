/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geometryclasses;

import java.awt.Choice;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import sodickdxfcoderui.Utilities;

/**
 *
 * @author matsandersson
 */
public class GeometryModel {
    
    private ChainList chainList = null;
    private enum Action { REPLACE, ADD, CANCEL};

    public void openDxfFile(File fileToOpen) {
        if ( chainList != null ) {
            Action action = askForAction();
            if ( action == Action.CANCEL ) return;
            if ( action == Action.REPLACE ) chainList = new ChainList();
        } else {
            chainList = new ChainList();
        }
        
        try {
            List<String> dxfLines = Files.readAllLines(fileToOpen.toPath(), Charset.defaultCharset());
            DxfFile dxfFile = new DxfFile();
            dxfFile.setDxfStringList(dxfLines);
            chainList.addFromDxfFile( dxfFile );
        } catch (IOException ex) {
            Utilities.showAlert("Kan inte läsa filen\n" + ex.getMessage());
        }
    }

    private Action askForAction() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Geometri existerar redan. Ersätt eller lägg till?");
        alert.setTitle("Geometri existerar");
        alert.setContentText("Välj");
        ButtonType replaceButton = new ButtonType("Ersätt");
        ButtonType addButton = new ButtonType("Lägg till");
        ButtonType cancelButton = new ButtonType("Avbryt");
        alert.getButtonTypes().setAll(replaceButton, addButton, cancelButton);
        Optional<ButtonType> result = alert.showAndWait();
        if ( result.get() == replaceButton ) return Action.REPLACE;
        if ( result.get() == addButton ) return Action.ADD;
        if ( result.get() == cancelButton ) return Action.CANCEL;
        return Action.CANCEL;
    }
    
}
