package geometryclasses;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import javafx.scene.canvas.Canvas;

public class SDCLine extends SDCGeometricEntity {

    double x1;
    double y1;
    double x2;
    double y2;

    public SDCLine(double x1, double y1, double x2, double y2) {
        super();
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        geometryType = GeometryType.LINE;
    }

    @Override
    public void draw(Graphics2D gr, float lineWidth, Color c) {
        // TODO Auto-generated method stub

    }

    @Override
    public double getX1() {
        return x1;
    }

    @Override
    public double getY1() {
        return y1;
    }

    @Override
    public double getX2() {
        return x2;
    }

    @Override
    public double getY2() {
        return y2;
    }

    public void reverse() {
        double x = x1;
        x1 = x2;
        x2 = x;
        double y = y1;
        y1 = y2;
        y2 = y;
    }

    @Override
    public void drawOnCanvas( Canvas canvas, SDCTransform sdct ) {
        Point2D.Double xStart = new Point2D.Double(x1, y1);
        Point2D.Double xEnd = new Point2D.Double(x2, y2);
        Point2D.Double viewportXStart = sdct.viewportCoordsFromModelCoords(xStart);
        Point2D.Double viewportXEnd = sdct.viewportCoordsFromModelCoords(xEnd);
        
        canvas.getGraphicsContext2D().strokeLine(
                viewportXStart.getX(),
                viewportXStart.getY(),
                viewportXEnd.getX(),
                viewportXEnd.getY() );
    }

}
