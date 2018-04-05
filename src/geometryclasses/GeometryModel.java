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
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.transform.Scale;
import sodickdxfcoderui.Utilities;

/**
 *
 * @author matsandersson
 */
public class GeometryModel {
    
    private ChainList chainList = null;

    
    private enum Action { REPLACE, ADD, CANCEL};

    public void plotOnCanvas(Canvas canvas) {
        GeoExtents geoExtents = new GeoExtents();
        geoExtents.calcGeoExtentsFromChainList(chainList);
        
        System.out.println("upperLeft : " + geoExtents.getUpperLeft());
        System.out.println("lowerRight : " + geoExtents.getLowerRight());
        double viewPortWidth = canvas.getWidth();
        double viewPortHeight = canvas.getHeight();
        double yScale = viewPortHeight / geoExtents.getHeight();
        double xScale = viewPortWidth / geoExtents.getWidth();
        
        double scale = 0.5 * Math.min(xScale, yScale);
        
        /*canvas.getGraphicsContext2D().clearRect(
                geoExtents.getLowerRight().getX(),
                geoExtents.getLowerRight().getY(),
                geoExtents.getWidth(),
                geoExtents.getHeight());*/
        
        canvas.getGraphicsContext2D().scale(scale, -scale);
        canvas.getGraphicsContext2D().translate(-geoExtents.getUpperLeft().getX()*1.5, -geoExtents.getUpperLeft().getY()*1.5);
        canvas.getGraphicsContext2D().setLineWidth( 2/scale);
        
        for ( Chain chain : chainList ) {
            for (SDCGeometricEntity geoEntity : chain ) {
                geoEntity.drawOnCanvas( canvas );
            }
        }
        
    }

    public void openDxfFile(File fileToOpen) {
        if ( chainList != null ) {
            Action action = askForAction();
            if ( action == Action.CANCEL ) return;
            if ( action == Action.REPLACE ) chainList = new ChainList();
        } else {
            // Chainlist is empty. We need to make a new.
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
