/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sodickdxfcoderui;

import geometryclasses.GeometryModel;
import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.DepthTest;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;

/**
 *
 * @author matsandersson
 */
public class FXMLDocumentController implements Initializable {

    GeometryModel geoModel = new GeometryModel();

    @FXML
    private Label statusLabel;

    @FXML
    private Pane graphicsPane;

    @FXML
    private void menuOpenAction(ActionEvent event) {
        System.out.println("Menu Open");
        userOpenDxfFile();
    }

    @FXML
    private void menuExitAction(ActionEvent event) {
        System.out.println("Menu Exit");
    }

    @FXML
    private void menuReverseLinkAction(ActionEvent event) {
        System.out.println("Menu Reverse Link");
    }

    @FXML
    private void menuRedraw(ActionEvent event) {
        System.out.println("Menu Reverse Link");
        redraw();
    }

    @FXML
    private void menuCodeStraightAction(ActionEvent event) {
        System.out.println("Menu Code Straight");
    }

    @FXML
    private void menuCodeAngleAction(ActionEvent event) {
        System.out.println("Menu Code Angle");
    }

    @FXML
    private void menuCodeTBAction(ActionEvent event) {
        System.out.println("Menu CodeTB");
    }
    
    @FXML
    private void zoomPositive(ActionEvent event) {
        geoModel.changeZoomLevel(1.1, graphicsPane);
    }

    @FXML
    private void zoomNegative(ActionEvent event) {
        geoModel.changeZoomLevel(-1.1, graphicsPane);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        statusLabel.setText("");

        setupGraphicPaneDragDrop();

        setupResizeHandler();
        setupZoomHandler();
    }

    private void setupGraphicPaneDragDrop() {
        graphicsPane.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                if (event.getDragboard().hasFiles()) {
                    event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                }
            }
        });

        graphicsPane.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard dragboard = event.getDragboard();
                if (dragboard.hasFiles()) {
                    List<File> droppedFileList = dragboard.getFiles();
                    File fileToDrop = droppedFileList.get(0);
                    System.out.println("fileToDrop " + fileToDrop.getAbsolutePath());
                    Path pathToDropFile = Paths.get(fileToDrop.getAbsolutePath());
                    openDXFFile(fileToDrop);
                }
            }
        });
    }

    private void userOpenDxfFile() {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("DXF-filer", "*.dxf"));
        File initialDirectory = new File(SodickDxfCoderPreferences.getInstance().getDefaultDirectory());
        fc.setInitialDirectory(initialDirectory);
        File fileToOpen = fc.showOpenDialog(null);
        if (fileToOpen != null) {
            openDXFFile(fileToOpen);
        }
    }

    private void openDXFFile(File fileToOpen) {
        // If a file is chosen, store the directory in preferences so the
        // same directory is used for next fileOpen

        Path chosenDirectory = fileToOpen.toPath().getParent();
        String chosenDirectoryString = chosenDirectory.toString();
        System.out.println("chosenDirectoryString: " + chosenDirectoryString);
        SodickDxfCoderPreferences.getInstance().setDefaultDirectory(chosenDirectory.toString());

        // Open the file
        if (geoModel.openDxfFile(fileToOpen)) {
            geoModel.setInitialScale(graphicsPane);
            geoModel.plotOnPane(graphicsPane);
        }
    }

    private void redraw() {
        geoModel.plotOnPane(graphicsPane);
    }

    private void setupResizeHandler() {
        graphicsPane.widthProperty().addListener(resizeListener());

        graphicsPane.heightProperty().addListener(resizeListener());
    }

    private void setupZoomHandler() {
        graphicsPane.setOnScroll(new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        double deltaY = event.getDeltaY();
                        double zoomCenterX = event.getX();
                        double zoomCenterY = event.getX();
                        
                        double zoom = 1.1;
                        if (deltaY < 0) {
                            zoom = -zoom;
                        }
                        geoModel.setZoomCenterInViewportCoords( zoomCenterX, zoomCenterY, graphicsPane );
                        geoModel.changeZoomLevel(zoom, graphicsPane);
                    }
                });
            }
        });
    }

    private InvalidationListener resizeListener() {
        return (Observable observable) -> {
            Platform.runLater(() -> {
                geoModel.setInitialScale(graphicsPane);
                geoModel.plotOnPane(graphicsPane);
            });
        };
    }

}
