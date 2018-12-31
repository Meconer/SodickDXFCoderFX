package codingPkg;

import static UtilPkg.Util.reportError;
import geometryclasses.Chain;
import geometryclasses.SDCGeometricEntity;
import geometryclasses.SDCGeometricEntity.GeometryType;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

/**
 *
 * @author Mats Andersson <mats.andersson@mecona.se>
 */
public class StraightCoder {

    public enum CompensationType {
        g41, g42, g40
    };

    public enum NoOfCuts {
        oneCut, sixCuts
    };

    private final CompensationType compensationType;
    private final NoOfCuts noOfCuts;

    public StraightCoder(CompensationType compensationType, NoOfCuts noOfCuts) {
        this.compensationType = compensationType;
        this.noOfCuts = noOfCuts;
    }

    public void buildCode(Chain chainToCode) {
        StringBuilder cncProgramString = new StringBuilder();
        addStartCode(cncProgramString);
        setInitialPoints(chainToCode);
    }

    private void addStartCode(StringBuilder cncProgramString) {
        String startCodeFileName = "straight1.txt";
        if (noOfCuts == NoOfCuts.sixCuts) {
            startCodeFileName = "straight6.txt";
        }
        cncProgramString.append(getResourceFileAsString(startCodeFileName));
        System.out.println(cncProgramString);
    }

    // Finds start and end points of first and last element of chain. Has to be a line.
    private void setInitialPoints( Chain chainToCode ) {
        Point2D.Double chainStartPoint = chainToCode.getStartPoint();
                

        SDCGeometricEntity geoEntity = chainToCode.getEntity(0);
        if (geoEntity.getGeometryType() != GeometryType.LINE) {
            reportError("Måste starta med linje");
        }
//        chain2ndPointx = geoEntity.getX2();
//        chain2ndPointy = geoEntity.getY2();
//
//        geoEntity = chainToCode.entityList.get(chainToCode.entityList.size() - 1);
//        chainNLPointx = geoEntity.getX1();
//        chainNLPointy = geoEntity.getY1();
    }

    
    /**
     * Reads given resource file as a string.
     *
     * @param fileName the path to the resource file
     * @return the file's contents or null if the file could not be opened
     */
    public String getResourceFileAsString(String fileName) {
        InputStream is = StraightCoder.class.getResourceAsStream(fileName);
        if (is != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            return reader.lines().collect(Collectors.joining(System.lineSeparator()));
        }
        return null;
    }

}
