package codingPkg;

import UtilPkg.Util;
import static UtilPkg.Util.reportError;
import codingPkg.TBCoder.CompensationType;
import codingPkg.TBCoder.NoOfCuts;
import geometryclasses.Chain;
import geometryclasses.GeometryModel;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import UtilPkg.SodickDxfCoderFXPreferences;

/**
 *
 * @author Mats Andersson <mats.andersson@mecona.se>
 */
public class CodeTBDialogController implements Initializable {

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
    private ChoiceBox<String> topChainChoiceBox;

    @FXML
    private RadioButton leanLeftRadioButton;
    @FXML
    private RadioButton leanRightRadioButton;

    @FXML
    private Button codeTBButton;
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
    private void codeTBAction(ActionEvent event) {
        if (geoModel == null) {
            Util.reportError("Ingen geometri");
            return;
        }

        if (geoModel.getNumberOfSelectedLinks() != 2) {
            Util.reportError("Du måste välja två länkar");
            return;
        }

        System.out.println("Code TB Action");
        CompensationType compensationType = CompensationType.g140;
        if (g41RadioButton.isSelected()) {
            compensationType = CompensationType.g141;
        }
        if (g42RadioButton.isSelected()) {
            compensationType = CompensationType.g142;
        }

        NoOfCuts noOfCuts = NoOfCuts.oneCut;
        if (sixCutsRadioButton.isSelected()) {
            noOfCuts = NoOfCuts.sixCuts;
        }

        String zLevelProgramString = Util.convertToDecimal(zProgramLevelTextField.getText(), "Z-nivå program felaktig");
        if (zLevelProgramString.contains(Util.ERROR_STRING)) {
            return;
        }

        String zLevelLowerString = Util.convertToDecimal(zLowerLevelTextField.getText(), "Z-nivå nedre felaktig");
        if (zLevelLowerString.contains(Util.ERROR_STRING)) {
            return;
        }

        String topChainString = topChainChoiceBox.getValue();

        TBCoder tbCoder = new TBCoder(
                compensationType,
                noOfCuts,
                zLevelProgramString,
                zLevelLowerString,
                m199CheckBox.isSelected());
        
        

        if (geoModel.getNumberOfSelectedLinks() != 2) {
            Util.reportError("Välj två länkar");
        } else {
            Chain topChain = null, bottomChain = null;
            int topLinkIndex = Integer.parseInt(topChainString.substring("Länk".length()+1)) - 1;
            for (int i = 0; i < geoModel.getNoOfChains(); i++) {
                if (geoModel.getChain(i).isSelected()) {
                    if (i == topLinkIndex) {
                        topChain = geoModel.getChain(i);
                    } else {
                        bottomChain = geoModel.getChain(i);
                    }
                }
            }
            if ( topChain == null || bottomChain == null) {
                Util.reportError( "Någon av länkarna är null!");
                return;
            }
            String cncProgram = tbCoder.buildCode(topChain, bottomChain);
            String fileName = SodickDxfCoderFXPreferences.getInstance().getCurrentFileName() + ".nc";

            Util.saveToFile(cncProgram, fileName);
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

        ObservableList list = topChainChoiceBox.getItems();
        for (int i = 0; i < geoModel.getNoOfChains(); i++) {
            if (geoModel.getChain(i).isSelected()) {
                list.add("Länk " + (i + 1));
            }
        }
        topChainChoiceBox.getSelectionModel().select(0);

    }

}
