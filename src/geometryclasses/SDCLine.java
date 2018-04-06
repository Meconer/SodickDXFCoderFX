package geometryclasses;

import java.awt.Color;
import java.awt.Graphics2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Line;

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
        double xS = sdct.scaleAndTranslateX( x1 );
        double yS = sdct.scaleAndTranslateY( y1 );
        double xE = sdct.scaleAndTranslateX( x2 );
        double yE = sdct.scaleAndTranslateY( y2 );
        canvas.getGraphicsContext2D().strokeLine(xS, yS, xE, yE );
    }

}
