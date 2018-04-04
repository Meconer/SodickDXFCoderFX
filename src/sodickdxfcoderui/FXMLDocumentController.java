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
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
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
    private void menuOpenAction(ActionEvent event) {
        System.out.println("Menu Open");
        openDxfFile();
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
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        statusLabel.setText("");
    }    

    private void openDxfFile() {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("DXF-filer", "*.dxf"));
        File initialDirectory = new File(SodickDxfCoderPreferences.getInstance().getDefaultDirectory());
        fc.setInitialDirectory(initialDirectory);
        File fileToOpen = fc.showOpenDialog(null);
        if ( fileToOpen != null ) {
            // If a file is chosen, store the directory in preferences so the
            // same directory is used for next fileOpen
        
            Path chosenDirectory = fileToOpen.toPath().getParent();
            String chosenDirectoryString = chosenDirectory.toString();
            System.out.println("chosenDirectoryString: " + chosenDirectoryString);
            SodickDxfCoderPreferences.getInstance().setDefaultDirectory(chosenDirectory.toString());
            
            // Open the file
            geoModel.openDxfFile(fileToOpen);
        }
    }
    
}
