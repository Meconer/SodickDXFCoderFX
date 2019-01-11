package codingPkg;

import UtilPkg.Util;
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
public class TBCoder {

    private Point2D.Double lastTopPoint;
    private Point2D.Double lastBottomPoint;


    public enum CompensationType {
        g141, g142, g140
    };
    private final CompensationType compensationType;

    public enum NoOfCuts {
        oneCut, sixCuts
    };
    private final NoOfCuts noOfCuts;
    
    public enum MoveType {
        g00, g01, g02, g03
    }
    private MoveType lastMoveTypeTop;
    private MoveType lastMoveTypeBottom;
    
    private String zLevelProgramString;
    private String zLevelLowerString;

    private boolean useM199;

    private Point2D.Double topChainNextToLastPoint;
    private Point2D.Double topChainSecondPoint;
    private Point2D.Double topChainStartPoint;
    
    private Point2D.Double bottomChainNextToLastPoint;
    private Point2D.Double bottomChainSecondPoint;
    private Point2D.Double bottomChainStartPoint;
    
    private Point2D.Double deltaTopToBottom;

    public TBCoder(CompensationType compensationType, NoOfCuts noOfCuts, String zLevelProgramString, String zLevelLowerString, boolean useM199) {
        this.compensationType = compensationType;
        this.noOfCuts = noOfCuts;
        this.zLevelProgramString = zLevelProgramString;
        this.zLevelLowerString = zLevelLowerString;
        this.useM199 = useM199;
    }

    public String buildCode(Chain topChain, Chain bottomChain) {
        StringBuilder cncProgramString = new StringBuilder();
        addStartCode(cncProgramString);
        setInitialPoints(topChain, bottomChain);
        addCutCode(cncProgramString);
        addSubSections(cncProgramString, topChain, bottomChain);
        return cncProgramString.toString();
    }

    private void addStartCode(StringBuilder cncProgramString) {
        String startCodeFileName = "tb1.txt";
        if (noOfCuts == NoOfCuts.sixCuts) {
            startCodeFileName = "tb6.txt";
        }
        cncProgramString.append(getResourceFileAsString(startCodeFileName));
        cncProgramString.append("\n");
    }

    // Finds start and end points of first and last element of chain. Has to be a line.
    private void setInitialPoints(Chain topChain, Chain bottomChain) {
        topChainStartPoint = topChain.getStartPoint();
        bottomChainStartPoint = bottomChain.getStartPoint();
        lastTopPoint = topChainStartPoint;
        lastBottomPoint = bottomChainStartPoint;

        SDCGeometricEntity geoEntityTop = topChain.getEntity(0);
        if (geoEntityTop.getGeometryType() != GeometryType.LINE) {
            reportError("Måste starta med linje. Fel på topplänken");
        }

        SDCGeometricEntity geoEntityBottom = bottomChain.getEntity(0);
        if (geoEntityBottom.getGeometryType() != GeometryType.LINE) {
            reportError("Måste starta med linje. Fel på bottenlänken");
        }

        topChainSecondPoint = topChain.getSecondPoint();
        bottomChainSecondPoint = bottomChain.getSecondPoint();

        topChainNextToLastPoint = topChain.getNextToLastPoint();
        bottomChainNextToLastPoint = bottomChain.getNextToLastPoint();
        
        deltaTopToBottom = Util.distanceBetweenPoints(topChainStartPoint, bottomChainStartPoint);
    }

    private void addCutCode(StringBuilder cncProgramString) {
        // Next write start point info and G92
        cncProgramString.append( "TP" );
        addWithEndOfLine(cncProgramString, zLevelProgramString);
        cncProgramString.append( "TN"  );
        addWithEndOfLine(cncProgramString, zLevelLowerString );
        
        addWithEndOfLine(cncProgramString, "G55");
        addWithEndOfLine(cncProgramString, "G90");

        cncProgramString.append("G92 ");
        cncProgramString.append( buildCoord(bottomChainStartPoint, true, lastBottomPoint ) );
        cncProgramString.append(" U0 V0 Z0;\n");
        cncProgramString.append("G29\n");
        cncProgramString.append("T94\n");
        cncProgramString.append("T84\n");
        cncProgramString.append("C000\n");

        String compensationSideString = "G140";
        String revCompensationSideString = "G140";
        if (compensationType == CompensationType.g141) {
            compensationSideString = "G141";
            revCompensationSideString = "G142";
        }
        if (compensationType == CompensationType.g142) {
            compensationSideString = "G142";
            revCompensationSideString = "G141";
        }
        
        
        //cncProgramString.append(angularDirString );
        cncProgramString.append(compensationSideString);
        addWithEndOfLine(cncProgramString, " H000");
        
        cncProgramString.append(" G01 ");
        cncProgramString.append( buildCoord(bottomChainSecondPoint, true, lastBottomPoint ) );
        cncProgramString.append(" : G01 ");
        addWithEndOfLine(cncProgramString, 
                buildCoord( Util.distanceBetweenPoints(topChainSecondPoint, deltaTopToBottom) ,
                        true,
                        lastTopPoint));
        
        addWithEndOfLine( cncProgramString, "H001 C001");
        addWithEndOfLine( cncProgramString, "M98 P0001");
        addWithEndOfLine( cncProgramString, "T85");
        addWithEndOfLine( cncProgramString, "G149 G249");

        if (noOfCuts == NoOfCuts.sixCuts) {
            
            addWithEndOfLine( cncProgramString, "C002");

            cncProgramString.append(revCompensationSideString);
            addWithEndOfLine(cncProgramString, " H000");
            
            cncProgramString.append("G01 ");
            cncProgramString.append(buildCoord(bottomChainNextToLastPoint, true, lastBottomPoint));
            cncProgramString.append(" : G01 ");
            addWithEndOfLine(cncProgramString, 
                    buildCoord(topChainNextToLastPoint, true, lastTopPoint) );
            
            addWithEndOfLine( cncProgramString, "H002");
            addWithEndOfLine( cncProgramString, "M98 P0002");


            addWithEndOfLine( cncProgramString, "C900");
            cncProgramString.append(compensationSideString);
            addWithEndOfLine(cncProgramString, " H000");

            cncProgramString.append("G01 ");
            cncProgramString.append(buildCoord(bottomChainSecondPoint, true, lastBottomPoint));
            cncProgramString.append(" : G01 ");
            addWithEndOfLine(cncProgramString, 
                    buildCoord(topChainNextToLastPoint, true, lastTopPoint) );
            

            addWithEndOfLine( cncProgramString, "C901");

            cncProgramString.append(revCompensationSideString);
            addWithEndOfLine(cncProgramString, " H000");
            
            cncProgramString.append("G01 ");
            cncProgramString.append(buildCoord(bottomChainNextToLastPoint, true, lastBottomPoint));
            cncProgramString.append(" : G01 ");
            addWithEndOfLine(cncProgramString, 
                    buildCoord(topChainNextToLastPoint, true, lastTopPoint) );
            
            addWithEndOfLine( cncProgramString, "H002");
            addWithEndOfLine( cncProgramString, "M98 P0002");


            addWithEndOfLine( cncProgramString, "C902");
            cncProgramString.append(compensationSideString);
            addWithEndOfLine(cncProgramString, " H000");

            cncProgramString.append("G01 ");
            cncProgramString.append(buildCoord(bottomChainSecondPoint, true, lastBottomPoint));
            cncProgramString.append(" : G01 ");
            addWithEndOfLine(cncProgramString, 
                    buildCoord(topChainNextToLastPoint, true, lastTopPoint) );
            

            addWithEndOfLine( cncProgramString, "C903");

            cncProgramString.append(revCompensationSideString);
            addWithEndOfLine(cncProgramString, " H000");
            
            cncProgramString.append("G01 ");
            cncProgramString.append(buildCoord(bottomChainNextToLastPoint, true, lastBottomPoint));
            cncProgramString.append(" : G01 ");
            addWithEndOfLine(cncProgramString, 
                    buildCoord(topChainNextToLastPoint, true, lastTopPoint) );
            
            addWithEndOfLine( cncProgramString, "H002");
            addWithEndOfLine( cncProgramString, "M98 P0002");

        }

        if (useM199) {
            addWithEndOfLine( cncProgramString, "M199");
        } else {
            addWithEndOfLine( cncProgramString, "M02");
        }
    }

    private void addSubSections(StringBuilder cncProgramString, Chain topChain, Chain bottomChain ) {
        addSubSection(cncProgramString, "N0001", topChain, bottomChain );
        topChain.reverseChain();
        bottomChain.reverseChain();
        addSubSection(cncProgramString, "N0002", topChain, bottomChain);
    }

    private void addSubSection(StringBuilder cncProgramString, String subName, Chain topChain, Chain bottomChain) {
        SDCGeometricEntity geo;
        lastMoveTypeTop = MoveType.g01;
        lastMoveTypeBottom = MoveType.g01;
        lastBottomPoint.setLocation( -99999.88, -99999.88);
        lastTopPoint.setLocation( -99999.88, -99999.88);

        addWithEndOfLine(cncProgramString, "");
        addWithEndOfLine(cncProgramString, subName);
        
        for (int i = 1; i <= topChain.getSize() - 2; i++) {
            geo = bottomChain.getEntity(i);
            if (geo.getGeometryType() == GeometryType.LINE) {
                cncProgramString.append(buildMove(MoveType.g01));
                addWithEndOfLine(cncProgramString, buildCoord(geo.getSecondPoint(), false, lastBottomPoint));
            }
            if (geo.getGeometryType() == GeometryType.ARC) {
                SDCArc sdcArc = (SDCArc) geo;
                if (sdcArc.getAngExt() < 0) { // cw arc
                    cncProgramString.append(buildMove(MoveType.g02));
                } else {
                    cncProgramString.append(buildMove(MoveType.g03));
                }
                cncProgramString.append(buildCoord(sdcArc.getSecondPoint(), false, lastBottomPoint));
                cncProgramString.append(" ");
                cncProgramString.append( buildIJ(sdcArc));
                cncProgramString.append(" : ");
            }
            geo = topChain.getEntity(i);
            if (geo.getGeometryType() == GeometryType.LINE) {
                cncProgramString.append(buildMove(MoveType.g01));
                addWithEndOfLine(cncProgramString,
                        buildCoord( 
                                Util.distanceBetweenPoints(geo.getSecondPoint(), deltaTopToBottom),
                                false,
                                lastTopPoint));
            }
            if (geo.getGeometryType() == GeometryType.ARC) {
                SDCArc sdcArc = (SDCArc) geo;
                if (sdcArc.getAngExt() < 0) { // cw arc
                    cncProgramString.append(buildMove(MoveType.g02));
                } else {
                    cncProgramString.append(buildMove(MoveType.g03));
                }
                cncProgramString.append(buildCoord(sdcArc.getSecondPoint(), false, lastTopPoint));
                cncProgramString.append(" ");
                addWithEndOfLine(cncProgramString, buildIJ(sdcArc));
            }
        }
        geo = topChain.getEntity(topChain.getSize() - 1);
        if (geo.getGeometryType() != GeometryType.LINE) {
            reportError("Måste avslutas med linje");
        }
        addWithEndOfLine(cncProgramString, "G140");
        addWithEndOfLine(cncProgramString,
                buildCoord(geo.getSecondPoint(), false, lastBottomPoint));
        addWithEndOfLine(cncProgramString, "M99");
    }

    private void addWithEndOfLine(StringBuilder cncProgramString, String stringToAdd) {
        cncProgramString.append(stringToAdd);
        cncProgramString.append(";\n");
    }

    private String buildCoord(Point2D.Double coords, Boolean forceOut, Point2D.Double lastPoint) {
        String s = "";
        double x = coords.getX();
        double y = coords.getY();

        if ((Math.abs(x - lastPoint.getX()) > 0.00001d) || forceOut) { // X changed, output X
            s = "X" + nForm(x);
        }

        if ((Math.abs(y - lastPoint.getY())) > 0.00001d || forceOut) { // Y changed, output Y
            if (!s.isEmpty()) {
                s = s + " ";
            }
            s = s + "Y" + nForm(y);
        }

        lastPoint.setLocation(coords);
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
        if (moveType != lastMoveTypeTop) {
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
            lastMoveTypeTop = moveType;
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
        InputStream is = TBCoder.class.getResourceAsStream(fileName);
        if (is != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            return reader.lines().collect(Collectors.joining(System.lineSeparator()));
        }
        return null;
    }

}
