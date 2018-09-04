/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sodickdxfcoderui;

import geometryclasses.Chain;
import geometryclasses.ChainList;
import geometryclasses.GeometryModel;
import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
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
    private boolean mousePanningGoingOn;
    private Point2D panStartPoint;

    @FXML
    private ListView<String> chainListView;
    
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
        System.exit(0);
    }

    @FXML
    private void menuReverseLinkAction(ActionEvent event) {
        userReverseLink();
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
        geoModel.zoomOnOrigo(1/1.1, graphicsPane);
    }

    @FXML
    private void zoomNegative(ActionEvent event) {
        geoModel.zoomOnOrigo(1.1, graphicsPane );
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        statusLabel.setText("");

        setupGraphicPaneDragDrop();

        setupResizeHandler();
        setupZoomHandler();
        setupMouseClickHandler();
        setupChainListHandler();
    }

    private void setupGraphicPaneDragDrop() {
        graphicsPane.setOnDragOver((DragEvent event) -> {
            if (event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
        });

        graphicsPane.setOnDragDropped((DragEvent event) -> {
            Dragboard dragboard = event.getDragboard();
            if (dragboard.hasFiles()) {
                List<File> droppedFileList = dragboard.getFiles();
                File fileToDrop = droppedFileList.get(0);
                System.out.println("fileToDrop " + fileToDrop.getAbsolutePath());
                Path pathToDropFile = Paths.get(fileToDrop.getAbsolutePath());
                openDXFFile(fileToDrop);
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
            showInfo();
        }
    }
    
    private void showInfo() {
        int noOfChains = geoModel.getNoOfChains();
        chainListView.getItems().clear();
        for ( int i = 0 ; i < noOfChains ; i++ ) {
            chainListView.getItems().add(new String( "Kedja " + (i+1)));
        }
    }

    private void userReverseLink() {
        List<Integer> selectedChainIndices = chainListView.getSelectionModel().getSelectedIndices();
        
        for ( int index : selectedChainIndices ) {
            geoModel.getChain(index).reverseChain();
        }
        
        redraw();
    }

    
    
    private void redraw() {
        geoModel.plotOnPane(graphicsPane);
    }

    private void setupChainListHandler() {
        chainListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        
        chainListView.getSelectionModel().selectedItemProperty().addListener((observable) -> {
//            geoModel.setSelectedLinks( chainListView.getSelectionModel().getSelectedIndices());
              List<Integer> selectedChainIndices = chainListView.getSelectionModel().getSelectedIndices();
              for ( int chainIndex = 0 ; chainIndex < chainListView.getItems().size() ; chainIndex++ ) {
                  geoModel.getChain(chainIndex).setSelected(selectedChainIndices.contains(chainIndex));
              }
              geoModel.plotOnPane(graphicsPane);
        });
    }


    private void setupResizeHandler() {
        graphicsPane.widthProperty().addListener(resizeListener());

        graphicsPane.heightProperty().addListener(resizeListener());
    }

    private void setupZoomHandler() {
        graphicsPane.setOnScroll((ScrollEvent event) -> {
            Platform.runLater(() -> {
                double deltaY = event.getDeltaY();
                double zoomCenterX = event.getX();
                double zoomCenterY = event.getY();
                
                double zoomFactor = 1.1;
                if (deltaY > 0) {
                    zoomFactor = 1/zoomFactor;
                }
                geoModel.zoom(zoomFactor, graphicsPane, zoomCenterX, zoomCenterY);
            });
        });
    }

    private void setupMouseClickHandler() {
        graphicsPane.setOnMouseClicked((MouseEvent event) -> {
            Platform.runLater(() -> {
                Point2D viewportPoint = new Point2D(event.getX(), event.getY());
                
                Point2D modelPoint = geoModel.getModelCoordsFromViewpointCoords( viewportPoint );
                System.out.println("Mouse clicked at : " + modelPoint.getX() + " : " + modelPoint.getY());
            });
        });
        
        graphicsPane.setOnMousePressed((event) -> {
            mousePanningGoingOn = true;
            panStartPoint = new Point2D(event.getX(), event.getY());
            Platform.runLater(() -> {
                Point2D viewportPoint = new Point2D(event.getX(), event.getY());
                Point2D modelPoint = geoModel.getModelCoordsFromViewpointCoords( viewportPoint );
                System.out.println("Mouse pressed at : " + viewportPoint.getX() + " : " + viewportPoint.getY());
            });
        });
        
        graphicsPane.setOnMouseDragged((event) -> {
            if ( mousePanningGoingOn ) {
                graphicsPane.setCursor(Cursor.CLOSED_HAND);
                Point2D currentPanPoint = new Point2D(event.getX(), event.getY());
                Platform.runLater(() -> {
                    geoModel.pan( panStartPoint, currentPanPoint );
                    geoModel.plotOnPane(graphicsPane);
                    panStartPoint = currentPanPoint;
                    Point2D viewportPoint = new Point2D(event.getX(), event.getY());
                    System.out.println("Mouse moved to : " + viewportPoint.getX() + " : " + viewportPoint.getY());
                });
            }
        });
        
        graphicsPane.setOnMouseReleased((event) -> {
            if ( mousePanningGoingOn ) {
                Point2D currentPanPoint = new Point2D(event.getX(), event.getY());
                mousePanningGoingOn = false;
                graphicsPane.setCursor(Cursor.DEFAULT);
                Platform.runLater(() -> {
                    geoModel.pan( panStartPoint, currentPanPoint );
                    geoModel.plotOnPane(graphicsPane);
                    Point2D viewportPoint = new Point2D(event.getX(), event.getY());
                    Point2D modelPoint = geoModel.getModelCoordsFromViewpointCoords( viewportPoint );
                    System.out.println("Mouse released at : " + viewportPoint.getX() + " : " + viewportPoint.getY());
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
