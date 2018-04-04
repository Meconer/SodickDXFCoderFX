/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geometryclasses;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;
import javafx.geometry.Point2D;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import sodickdxfcoderui.Utilities;

/**
 *
 * @author matsandersson
 */
public class GeometryModel {
    
    private ChainList chainList = null;

    private class GeoExtents {
        Point2D upperLeft;
        Point2D lowerRight;
    }
    
    private enum Action { REPLACE, ADD, CANCEL};

    private GeoExtents getGeoExtents() {
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double maxY = Double.MIN_VALUE;
        
        // Find smallest and largest X and Y values
        for ( Chain chain : chainList ) {
            for ( GeometricEntity geo : chain ) {
                minX = Math.min(minX, geo.getX1());
                minX = Math.min(minX, geo.getX2());
                minY = Math.min(minY, geo.getY1());
                minY = Math.min(minY, geo.getY2());
                maxX = Math.max(maxX, geo.getX1());
                maxX = Math.max(maxX, geo.getX2());
                maxY = Math.max(maxY, geo.getY1());
                maxY = Math.max(maxY, geo.getY2());
            }
        }
        
        GeoExtents geoExtents = new GeoExtents();
        geoExtents.upperLeft = new Point2D(minX, maxY);
        geoExtents.lowerRight = new Point2D(maxX, minY);
        return geoExtents;
    }
    
    public void plotOnCanvas() {
        GeoExtents geoExtents = getGeoExtents();
    }

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
