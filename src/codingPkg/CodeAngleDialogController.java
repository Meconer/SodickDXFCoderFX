package codingPkg;

import UtilPkg.Util;
import codingPkg.AngleCoder.LeanSide;
import codingPkg.AngleCoder.CompensationType;
import codingPkg.AngleCoder.NoOfCuts;
import geometryclasses.Chain;
import geometryclasses.GeometryModel;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import UtilPkg.SodickDxfCoderFXPreferences;

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
    private TextField zProgramLevelTextField;
    @FXML
    private TextField zLowerLevelTextField;
    @FXML
    private TextField angleTextField;
    
    @FXML
    private RadioButton leanLeftRadioButton;
    @FXML
    private RadioButton leanRightRadioButton;
    

    @FXML
    private Button codeAngleButton;
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
        
        ToggleGroup leanSideToggleGroup = new ToggleGroup();
        leanLeftRadioButton.setToggleGroup(leanSideToggleGroup);
        leanRightRadioButton.setToggleGroup(leanSideToggleGroup);
        leanLeftRadioButton.setSelected(true);
    }

    @FXML
    private void codeAngleAction(ActionEvent event) {
        if ( geoModel == null ) {
            Util.reportError("Ingen geometri");
            return;
        }

        if ( geoModel.getNumberOfSelectedLinks() == 0 ) {
            Util.reportError("Ingen kedja vald");
            return;
        }
        
        System.out.println("Code Angle Action");
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
        
        LeanSide leanSide = LeanSide.leanLeft;
        if ( leanRightRadioButton.isSelected() ) leanSide = LeanSide.leanRight;

        String zLevelProgramString = Util.convertToDecimal( zProgramLevelTextField.getText(), "Z-nivå program felaktig" );
        if (zLevelProgramString.contains(Util.ERROR_STRING)) return;
        
        String zLevelLowerString = Util.convertToDecimal( zLowerLevelTextField.getText(), "Z-nivå nedre felaktig" );
        if (zLevelLowerString.contains(Util.ERROR_STRING)) return;
        
        String leanAngleString = Util.convertToDecimal( angleTextField.getText(), "Lutningsvinkel felaktig" );
        if (leanAngleString.contains(Util.ERROR_STRING)) return;
        
        AngleCoder angleCoder = new AngleCoder(
                compensationType,
                noOfCuts,
                leanSide,
                zLevelProgramString,
                zLevelLowerString,
                leanAngleString,
                m199CheckBox.isSelected() );
       
        if (geoModel.getNumberOfSelectedLinks() != 1) {
            Util.reportError("Välj en kedja");
        } else {
            Chain chainToCode = geoModel.getSelectedLinks().get(0);
            String cncProgram = angleCoder.buildCode( chainToCode );
            String fileName = SodickDxfCoderFXPreferences.getInstance().getCurrentFileName() + ".nc";
            
            Util.saveToFile(cncProgram, fileName );
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


}
