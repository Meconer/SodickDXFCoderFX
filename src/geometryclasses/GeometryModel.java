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
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import sodickdxfcoderui.Utilities;

/**
 *
 * @author matsandersson
 */
public class GeometryModel {

    private ChainList chainList = null;
    private SDCTransform sdcTransform;

    public void changeZoomLevel(double zoom, Pane pane) {
        sdcTransform.setZoomLevel( zoom );
        plotOnPane(pane);
    }

    public void setZoomCenterInViewportCoords(double zoomCenterX, double zoomCenterY, Pane pane) {
        sdcTransform.setZoomCenterInViewportCoords( zoomCenterX, zoomCenterY, pane.getWidth(), pane.getHeight() );
    }

    private enum Action {
        REPLACE, ADD, CANCEL
    };

    public void setInitialScale(Pane pane) {
        GeoExtents geoExtents = new GeoExtents();
        geoExtents.calcGeoExtentsFromChainList(chainList);

        double viewPortWidth = pane.getWidth();
        double viewPortHeight = pane.getHeight();

        sdcTransform = SDCTransform.buildScaleTransform(geoExtents, viewPortWidth, viewPortHeight);
    }

    public void plotOnPane(Pane pane) {

        while (pane.getChildren().size() > 0) {
            pane.getChildren().remove(0);
        }
        Canvas canvas = new Canvas(pane.getWidth(), pane.getHeight());
        canvas.getGraphicsContext2D().setLineWidth(2.0);
        pane.getChildren().add(canvas);
        if (chainList != null) {
            for (Chain chain : chainList) {
                canvas.getGraphicsContext2D().setStroke(Color.RED);
                for (SDCGeometricEntity geoEntity : chain) {
                    geoEntity.drawOnCanvas(canvas, sdcTransform);
                    canvas.getGraphicsContext2D().setStroke(Color.GREEN);
                }
            }

            SDCLine xAxis = new SDCLine(-10, 0, 10, 0);
            SDCLine yAxis = new SDCLine(0, -10, 0, 10);
            canvas.getGraphicsContext2D().setStroke(Color.GRAY);
            canvas.getGraphicsContext2D().setLineDashes(3);
            canvas.getGraphicsContext2D().setLineWidth(1.0);
            xAxis.drawOnCanvas(canvas, sdcTransform);
            yAxis.drawOnCanvas(canvas, sdcTransform);
            canvas.getGraphicsContext2D().setLineDashes(0);
        }
    }

    public Boolean openDxfFile(File fileToOpen) {
        if (chainList != null) {
            Action action = askForAction();
            if (action == Action.CANCEL) {
                return false;
            }
            if (action == Action.REPLACE) {
                chainList = new ChainList();
            }
        } else {
            // Chainlist is empty. We need to make a new.
            chainList = new ChainList();
        }

        try {
            List<String> dxfLines = Files.readAllLines(fileToOpen.toPath(), Charset.defaultCharset());
            DxfFile dxfFile = new DxfFile();
            dxfFile.setDxfStringList(dxfLines);

            if (!chainList.addFromDxfFile(dxfFile)) {
                Utilities.showAlert("Kan inte öppna denna fil");
                return false;
            }
        } catch (IOException ex) {
            Utilities.showAlert("Kan inte läsa filen\n" + ex.getMessage());
            return false;
        }
        return true;
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
        if (result.get() == replaceButton) {
            return Action.REPLACE;
        }
        if (result.get() == addButton) {
            return Action.ADD;
        }
        if (result.get() == cancelButton) {
            return Action.CANCEL;
        }
        return Action.CANCEL;
    }
}
