package codingPkg;

import UtilPkg.Util;
import static UtilPkg.Util.reportError;
import codingPkg.StraightCoder.CompensationType;
import codingPkg.StraightCoder.NoOfCuts;
import geometryclasses.Chain;
import geometryclasses.GeometryModel;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sodickdxfcoderui.SodickDxfCoderFXPreferences;

/**
 *
 * @author Mats Andersson <mats.andersson@mecona.se>
 */
public class CodeAngleDialogController implements Initializable {

    @FXML
    private RadioButton g41RadioButton;
    @FXML
    private RadioButton g42RadioButton;
    @FXML
    private RadioButton g40RadioButton;

    @FXML
    private RadioButton oneCutRadioButton;
    @FXML
    private RadioButton sixCutsRadioButton;
    
    @FXML
    private CheckBox m199CheckBox;

    @FXML
    private Button codeStraightButton;
    @FXML
    private Button closeButton;
    private GeometryModel geoModel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ToggleGroup compensationToggleGroup = new ToggleGroup();
        g41RadioButton.setToggleGroup(compensationToggleGroup);
        g41RadioButton.setSelected(true);
        g42RadioButton.setToggleGroup(compensationToggleGroup);
        g40RadioButton.setToggleGroup(compensationToggleGroup);

        ToggleGroup numberOfCutsToggleGroup = new ToggleGroup();
        oneCutRadioButton.setToggleGroup(numberOfCutsToggleGroup);
        sixCutsRadioButton.setToggleGroup(numberOfCutsToggleGroup);
        sixCutsRadioButton.setSelected(true);
    }

    @FXML
    private void codeStraightAction(ActionEvent event) {
        if ( geoModel == null ) {
            Util.reportError("Ingen geometri");
            return;
        }

        if ( geoModel.getNumberOfSelectedLinks() == 0 ) {
            Util.reportError("Ingen kedja vald");
            return;
        }
        
        System.out.println("Code Straight Action");
        CompensationType compensationType = CompensationType.g40;
        if (g41RadioButton.isSelected()) {
            compensationType = CompensationType.g41;
        }
        if (g42RadioButton.isSelected()) {
            compensationType = CompensationType.g42;
        }

        NoOfCuts noOfCuts = NoOfCuts.oneCut;
        if (sixCutsRadioButton.isSelected()) {
            noOfCuts = NoOfCuts.sixCuts;
        }


            
        StraightCoder straightCoder = new StraightCoder(compensationType, noOfCuts, m199CheckBox.isSelected() );
       
        if (geoModel.getNumberOfSelectedLinks() != 1) {
            Util.reportError("Välj en kedja");
        } else {
            Chain chainToCode = geoModel.getSelectedLinks().get(0);
            String cncProgram = straightCoder.buildCode( chainToCode );
            String fileName = SodickDxfCoderFXPreferences.getInstance().getCurrentFileName() + ".nc";
            
            saveToFile(cncProgram, fileName );
            closeAction(event);
        }
    }

    @FXML
    private void closeAction(ActionEvent event) {
        System.out.println("Close Action");
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    public void initModel(GeometryModel geoModel) {
        if (this.geoModel != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }
        this.geoModel = geoModel;
    }

    private void saveToFile(String cncProgram, String fileName) {
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

}
