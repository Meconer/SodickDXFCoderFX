package codingPkg;

import static UtilPkg.Util.reportError;
import geometryclasses.Chain;
import geometryclasses.SDCArc;
import geometryclasses.SDCGeometricEntity;
import geometryclasses.SDCGeometricEntity.GeometryType;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.stream.Collectors;

/**
 *
 * @author Mats Andersson <mats.andersson@mecona.se>
 */
public class StraightCoder {

    private double lastX;
    private double lastY;

    public enum CompensationType {
        g41, g42, g40
    };
    private final CompensationType compensationType;

    public enum NoOfCuts {
        oneCut, sixCuts
    };
    private final NoOfCuts noOfCuts;

    public enum MoveType {
        g00, g01, g02, g03
    }
    private MoveType lastMoveType;

    private boolean useM199;

    private Point2D.Double chainNextToLastPoint;
    private Point2D.Double chainSecondPoint;
    private Point2D.Double chainStartPoint;

    public StraightCoder(CompensationType compensationType, NoOfCuts noOfCuts, boolean useM199) {
        this.compensationType = compensationType;
        this.noOfCuts = noOfCuts;
        this.useM199 = useM199;
    }

    public String buildCode(Chain chainToCode) {
        StringBuilder cncProgramString = new StringBuilder();
        addStartCode(cncProgramString);
        setInitialPoints(chainToCode);
        addCutCode(cncProgramString);
        addSubSections(cncProgramString, chainToCode);
        return cncProgramString.toString();
    }

    private void addStartCode(StringBuilder cncProgramString) {
        String startCodeFileName = "straight1.txt";
        if (noOfCuts == NoOfCuts.sixCuts) {
            startCodeFileName = "straight6.txt";
        }
        cncProgramString.append(getResourceFileAsString(startCodeFileName));
        cncProgramString.append("\n");
    }

    // Finds start and end points of first and last element of chain. Has to be a line.
    private void setInitialPoints(Chain chainToCode) {
        chainStartPoint = chainToCode.getStartPoint();

        SDCGeometricEntity geoEntity = chainToCode.getEntity(0);
        if (geoEntity.getGeometryType() != GeometryType.LINE) {
            reportError("Måste starta med linje");
        }

        chainSecondPoint = chainToCode.getSecondPoint();

        chainNextToLastPoint = chainToCode.getNextToLastPoint();
    }

    private void addCutCode(StringBuilder cncProgramString) {
        // Next write start point info and G92

        cncProgramString.append("G92 ");
        cncProgramString.append( buildCoord(chainStartPoint, true) );
        cncProgramString.append(" Z0;\n");
        cncProgramString.append("G29\n");
        cncProgramString.append("T94\n");
        cncProgramString.append("T84\n");
        cncProgramString.append("C000\n");

        String compensationSideString = "G40";
        String revCompensationSideString = "G40";
        if (compensationType == CompensationType.g41) {
            compensationSideString = "G41";
            revCompensationSideString = "G42";
        }
        if (compensationType == CompensationType.g42) {
            compensationSideString = "G42";
            revCompensationSideString = "G41";
        }
        cncProgramString.append(compensationSideString + " H000 G01 " + buildCoord(chainSecondPoint, true) + ";\n");

        cncProgramString.append("H001 C001;\n");
        cncProgramString.append("M98 P0001;\n");
        cncProgramString.append("T85;\n");
        cncProgramString.append("G149 G249;\n");

        if (noOfCuts == NoOfCuts.sixCuts) {
            cncProgramString.append("C002;\n");

            cncProgramString.append(revCompensationSideString);
            cncProgramString.append(" H000 G01 ");
            cncProgramString.append(buildCoord(chainNextToLastPoint, true));
            cncProgramString.append(";\n");

            cncProgramString.append("H002;\n");
            cncProgramString.append("M98 P0002;\n");
            cncProgramString.append("C900;\n");

            cncProgramString.append(compensationSideString);
            cncProgramString.append(" H000 G01 ");
            cncProgramString.append(buildCoord(chainNextToLastPoint, true));
            cncProgramString.append(";\n");

            cncProgramString.append("H003;\n");
            cncProgramString.append("M98 P0001;\n");
            cncProgramString.append("C901;\n");

            cncProgramString.append(revCompensationSideString);
            cncProgramString.append(" H000 G01 ");
            cncProgramString.append(buildCoord(chainNextToLastPoint, true));
            cncProgramString.append(";\n");

            cncProgramString.append("H004;\n");
            cncProgramString.append("M98 P0002;\n");

            cncProgramString.append("C902;\n");

            cncProgramString.append(compensationSideString);
            cncProgramString.append(" H000 G01 ");
            cncProgramString.append(buildCoord(chainNextToLastPoint, true));
            cncProgramString.append(";\n");

            cncProgramString.append("H005;\n");
            cncProgramString.append("M98 P0001;\n");

            cncProgramString.append("C903;\n");

            cncProgramString.append(revCompensationSideString);
            cncProgramString.append(" H000 G01 ");
            cncProgramString.append(buildCoord(chainNextToLastPoint, true));
            cncProgramString.append(";\n");

            cncProgramString.append("H006;\n");
            cncProgramString.append("M98 P0002;\n");
        }

        if (useM199) {
            cncProgramString.append("M199;\n");
        } else {
            cncProgramString.append("M02;\n");
        }
    }

    private void addSubSections(StringBuilder cncProgramString, Chain chainToCode) {
        addSubSection(cncProgramString, "N0001", chainToCode);
        chainToCode.reverseChain();
        addSubSection(cncProgramString, "N0002", chainToCode);
    }

    private void addSubSection(StringBuilder cncProgramString, String subName, Chain chainToCode) {
        SDCGeometricEntity geo;
        lastMoveType = MoveType.g01;
        lastX = -99999.88;
        lastY = -99999.88;
        addWithEndOfLine(cncProgramString, "");
        addWithEndOfLine(cncProgramString, subName);
        for (int i = 1; i <= chainToCode.getSize() - 2; i++) {
            geo = chainToCode.getEntity(i);
            if (geo.getGeometryType() == GeometryType.LINE) {
                cncProgramString.append(buildMove(MoveType.g01));
                addWithEndOfLine(cncProgramString, buildCoord(geo.getSecondPoint(), false));
            }
            if (geo.getGeometryType() == GeometryType.ARC) {
                SDCArc sdcArc = (SDCArc) geo;
                if (sdcArc.getAngExt() < 0) { // cw arc
                    cncProgramString.append(buildMove(MoveType.g02));
                } else {
                    cncProgramString.append(buildMove(MoveType.g03));
                }
                cncProgramString.append(buildCoord(sdcArc.getSecondPoint(), false));
                cncProgramString.append(" ");
                addWithEndOfLine(cncProgramString, buildIJ(sdcArc));
            }
        }
        geo = chainToCode.getEntity(chainToCode.getSize() - 1);
        if (geo.getGeometryType() != GeometryType.LINE) {
            reportError("Måste avslutas med linje");
        }
        cncProgramString.append(buildMove(MoveType.g01));
        cncProgramString.append("G40 H000 ");
        addWithEndOfLine(cncProgramString, buildCoord(geo.getSecondPoint(), false));
        addWithEndOfLine(cncProgramString, "M99");
    }

    private void addWithEndOfLine(StringBuilder cncProgramString, String stringToAdd) {
        cncProgramString.append(stringToAdd);
        cncProgramString.append(";\n");
    }

    private String buildCoord(Point2D.Double coords, Boolean forceOut) {
        String s = "";
        double x = coords.getX();
        double y = coords.getY();

        if ((Math.abs(x - lastX) > 0.00001d) || forceOut) { // X changed, output X
            s = "X" + nForm(x);
        }

        if ((Math.abs(y - lastY)) > 0.00001d || forceOut) { // Y changed, output Y
            if (!s.isEmpty()) {
                s = s + " ";
            }
            s = s + "Y" + nForm(y);
        }

        lastX = x;
        lastY = y;
        return s;
    }

    // Calculate I and J numbers for arc
    private String buildIJ(SDCArc sdcArc) {
        double xDiff = sdcArc.getCenterPoint().getX() - sdcArc.getX1();
        double yDiff = sdcArc.getCenterPoint().getY() - sdcArc.getY1();
        return "I" + nForm(xDiff) + " J" + nForm(yDiff);
    }

    private String buildMove(MoveType moveType) {
        String s = "";
        if (moveType != lastMoveType) {
            switch (moveType) {
                case g01:
                    s = "G01 ";
                    break;

                case g02:
                    s = "G02 ";
                    break;

                case g03:
                    s = "G03 ";
                    break;

                case g00:
                    s = "G00 ";
                    break;

                default:
                    break;
            }
            lastMoveType = moveType;
        }
        return s;
    }

    // Number formatting
    private String nForm(double number) {
        String s;
        DecimalFormat df = new DecimalFormat("0.0###");
        s = df.format(number);
        return s.replaceAll(",", ".");

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
