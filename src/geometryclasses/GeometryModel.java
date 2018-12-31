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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import javafx.geometry.Point2D;
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
    private List<Integer> selectedIndices = new LinkedList<>();

    public void zoom(double zoomFactor, Pane pane, double zoomCenterX, double zoomCenterY ) {
        sdcTransform.zoom( new Point2D(zoomCenterX, zoomCenterY), zoomFactor);
        plotOnPane(pane);
    }
    
    public void zoomOnOrigo( double zoomFactor, Pane pane ) {
        sdcTransform.zoomOnOrigo(zoomFactor);
        plotOnPane(pane);
    }

    public Point2D getModelCoordsFromViewpointCoords(Point2D viewportPoint) {
        return sdcTransform.modelCoordsFromViewportCoords(viewportPoint);
    }

    public void pan(Point2D panStartPoint, Point2D currentPanPoint) {
        sdcTransform.pan( panStartPoint, currentPanPoint );
    }

    public int getNoOfChains() {
        return chainList.getSize();
    }

    public void setSelectedLinks(List<Integer> selectedIndices) {
        this.selectedIndices = selectedIndices;
        for ( Chain chain : chainList ) {
            chain.setSelected(false);
        }
        selectedIndices.forEach((index) -> {
            chainList.getChain(index).setSelected(true);
        });
    }
    
    public ArrayList<Chain> getSelectedLinks() {
        ArrayList<Chain> selectedChains = new ArrayList<>();
        for ( Chain chain : chainList ) {
            if (chain.isSelected() ) selectedChains.add(chain);
        }
        return selectedChains;
    }
    
    public int getNumberOfSelectedLinks() {
        int counter = 0 ;
        if ( chainList == null ) return 0;
        if ( chainList.isEmpty())  return 0;
        for ( Chain chain : chainList ) {
            if ( chain.isSelected() ) counter++;
        }
        return counter;
    }

    public Chain getChain(int chainIndex) {
        if (chainIndex < getNoOfChains() ) return chainList.getChain(chainIndex);
        return null;
    };

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
            for (int chainIndex = 0 ; chainIndex < chainList.getSize() ; chainIndex++ ) {
                Chain chain = chainList.getChain( chainIndex );
                if ( chain.isSelected()) {
                    canvas.getGraphicsContext2D().setStroke(SELECTED_CHAIN_START_COLOR);
                } else {
                    canvas.getGraphicsContext2D().setStroke(UNSELECTED_CHAIN_START_COLOR);
                }
                    for (SDCGeometricEntity geoEntity : chain) {
                        geoEntity.drawOnCanvas(canvas, sdcTransform);
                        if ( chain.isSelected() ) {
                            canvas.getGraphicsContext2D().setStroke(SELECTED_CHAIN_COLOR);
                        } else {
                            canvas.getGraphicsContext2D().setStroke(UNSELECTED_CHAIN_COLOR);
                        }
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
    private static final Color UNSELECTED_CHAIN_COLOR = Color.LIGHTBLUE;
    private static final Color UNSELECTED_CHAIN_START_COLOR = Color.ORANGE;
    private static final Color SELECTED_CHAIN_COLOR = Color.BLUE;
    private static final Color SELECTED_CHAIN_START_COLOR = Color.RED;

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
