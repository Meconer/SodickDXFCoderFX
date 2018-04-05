/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geometryclasses;

import java.awt.geom.Point2D;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author mats
 */
class DxfFile {

    private List<String> dxfStringList = new LinkedList();

    public List<String> getDxfStringList() {
        return dxfStringList;
    }

    public void setDxfStringList(List<String> dxfStringList) {
        this.dxfStringList = dxfStringList;
        
        
    }
    private Path path;
    private BufferedWriter bufferedWriter;

    private boolean createFile() {
        JFileChooser jfc = new JFileChooser();
        boolean okToCreate = false;
        int returnVal = jfc.showSaveDialog(null);
        if (returnVal == JFileChooser.CANCEL_OPTION) {
            return false;
        } else {
            path = Paths.get(jfc.getSelectedFile().toURI());
            if (Files.exists(path)) {
                int result = JOptionPane.showConfirmDialog(null, "Filen finns. Skriva över?", "Filen finns", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    okToCreate = true;
                }
            } else {
                okToCreate = true;
            }
            if (okToCreate) {
                try {
                    bufferedWriter = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
                    return true;
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, ex.getLocalizedMessage());
                    return false;
                }

            }
        }
        return false;
    }


    void addArc(Point2D.Double center, double arcStart, double arcEnd, double radie) {
        dxfStringList.add("ARC");
        dxfStringList.add("  8");
        dxfStringList.add("0");
        dxfStringList.add(" 10");
        dxfStringList.add(String.format( Locale.US, " %f", center.x));
        dxfStringList.add(" 20");
        dxfStringList.add(String.format( Locale.US, " %f", center.y));
        dxfStringList.add(" 40");
        dxfStringList.add(String.format( Locale.US, " %f", radie));
        dxfStringList.add(" 50");
        dxfStringList.add(String.format( Locale.US, " %f", arcStart));
        dxfStringList.add(" 51");
        dxfStringList.add(String.format( Locale.US, " %f", arcEnd));
        dxfStringList.add(" 0");
    }

    void addLine(Point2D.Double startPoint, Point2D.Double endPoint) {
        dxfStringList.add("LINE");
        dxfStringList.add("  8");
        dxfStringList.add("0");
        dxfStringList.add(" 10");
        dxfStringList.add(String.format( Locale.US, " %f", startPoint.x));
        dxfStringList.add(" 20");
        dxfStringList.add(String.format( Locale.US, " %f", startPoint.y));
        dxfStringList.add(" 11");
        dxfStringList.add(String.format( Locale.US, " %f", endPoint.x));
        dxfStringList.add(" 21");
        dxfStringList.add(String.format( Locale.US, " %f", endPoint.y));
        dxfStringList.add(" 0");
    }

    void addHeader() {
        dxfStringList.add("  0");
        dxfStringList.add("SECTION");
        dxfStringList.add("  2");
        dxfStringList.add("ENTITIES");
        dxfStringList.add("  0");
    }

    void saveFile() {
        if (createFile()) {
            try {
                Iterator<String> i = dxfStringList.iterator();
                while (i.hasNext()) {
                    bufferedWriter.write(i.next() + "\r\n");
                }
            } catch (Exception e) {

            } finally {
                //Close the BufferedWriter
                try {
                    if (bufferedWriter != null) {
                        bufferedWriter.flush();
                        bufferedWriter.close();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    void addEnd() {
        dxfStringList.add("ENDSEC");
        dxfStringList.add("  0");
        dxfStringList.add("EOF");
    }

    Iterator<String> dxfStringListIterator;
    public SDCGeometricEntity getFirstGeometricEntity() {
        dxfStringListIterator = dxfStringList.iterator();
        
        // Search to ENTITIES section in dxf lines.
        String result = findInDxfStringList("ENTITIES");
        if ( result == null ) return null; // Didn't find entity section
        
        // Now in ENTITIES section, search for the first entity of ARC or LINE
        // type. 
        return getNextGeometricEntity();
    }

    public SDCGeometricEntity getNextGeometricEntity() {
        String result = findInDxfStringList("ARC|LINE");
        if ( result == null ) return null;
        if (result.matches("LINE")) {
            SDCLine line = readLineFromDxf( );
            return line;
        }
        if (result.matches("ARC")) {
            SDCArc arc = readArcFromDxf( );
            return arc;
        }
        return null;
    }
    
    private String findInDxfStringList(String regexToMatch) {
        while( dxfStringListIterator.hasNext()) {
            String textLine = dxfStringListIterator.next();
            // Search string is found. Return true;
            if ( textLine.matches(regexToMatch) ) return textLine;
        }
        return null; // Not found
    }

    private SDCLine readLineFromDxf() {
        String inLine1;
        String inLine2;
        double x1 = 0, x2 = 0, y1 = 0, y2 = 0;

        do {
            // Read 2 lines from dxf file
            inLine1 = dxfStringListIterator.next();
            inLine2 = dxfStringListIterator.next();
            if (inLine1.matches(" 10")) {
                x1 = Double.parseDouble(inLine2);
            }
            if (inLine1.matches(" 20")) {
                y1 = Double.parseDouble(inLine2);
            }
            if (inLine1.matches(" 11")) {
                x2 = Double.parseDouble(inLine2);
            }
            if (inLine1.matches(" 21")) {
                y2 = Double.parseDouble(inLine2);
            }
        } while (!inLine1.matches(" 21"));
        SDCLine l = new SDCLine(x1, y1, x2, y2);
        NumberFormat nf = new DecimalFormat("#.###");
        System.out.println("Line " + nf.format(l.getX1()) + ":"
                + nf.format(l.getY1()) + " ; "
                + nf.format(l.getX2()) + ":"
                + nf.format(l.getY2()));
        return l;
    }

    private SDCArc readArcFromDxf() {
        String inLine1;
        String inLine2;

        double xC = 0,
                yC = 0,
                r = 0,
                stA = 0,
                endA = 0;
        do {
            // Read 2 lines from dxf file
            inLine1 = dxfStringListIterator.next();
            inLine2 = dxfStringListIterator.next();
            if (inLine1.matches(" 10")) {
                xC = Double.parseDouble(inLine2);
            }
            if (inLine1.matches(" 20")) {
                yC = Double.parseDouble(inLine2);
            }
            if (inLine1.matches(" 40")) {
                r = Double.parseDouble(inLine2);
            }
            if (inLine1.matches(" 50")) {
                stA = Double.parseDouble(inLine2);
            }
            if (inLine1.matches(" 51")) {
                endA = Double.parseDouble(inLine2);
            }
        } while (!inLine1.matches(" 51"));
        if (endA < stA) {
            endA += 360.0;
        }
        SDCArc a = new SDCArc(xC, yC, r, stA, endA - stA);
        NumberFormat nf = new DecimalFormat("#.###");
        System.out.println("Arc " + nf.format(a.getX1()) + ":"
                + nf.format(a.getY1()) + " ; "
                + nf.format(a.getX2()) + ":"
                + nf.format(a.getY2()) + " - "
                + nf.format(a.stAng) + " . "
                + nf.format(a.angExt));

        return a;
    }
}

