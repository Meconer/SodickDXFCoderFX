package geometryclasses;

import java.awt.Color;
import java.awt.Graphics2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;

public class SDCArc extends SDCGeometricEntity {

    double xCenter;
    double yCenter;
    double radius;
    double stAng;
    double angExt;

    public SDCArc(double xCenter, double yCenter, double radius, double stAng,
            double angExt) {
        super();
        this.xCenter = xCenter;
        this.yCenter = yCenter;
        this.radius = radius;
        this.stAng = stAng;
        this.angExt = angExt;
        geometryType = GeometryType.ARC;
    }

    @Override
    public void draw(Graphics2D gr, float lineWidth, Color c) {
        // TODO Auto-generated method stub

    }

    @Override
    public double getX1() {
        return xCenter + radius * Math.cos(stAng * Math.PI / 180.0);
    }

    @Override
    public double getY1() {
        return yCenter + radius * Math.sin(stAng * Math.PI / 180.0);
    }

    @Override
    public double getX2() {
        double endAng = stAng + angExt;
        return xCenter + radius * Math.cos(endAng * Math.PI / 180.0);
    }

    @Override
    public double getY2() {
        double endAng = stAng + angExt;
        return yCenter + radius * Math.sin(endAng * Math.PI / 180.0);
    }

    @Override
    public void reverse() {
        stAng = stAng + angExt;
        angExt = -angExt;
    }

    @Override
    public void drawOnCanvas(Canvas canvas, SDCTransform sdct) {
        double x = sdct.scaleAndTranslateX(xCenter - radius );
        double w = sdct.scale(radius * 2);
        double h = w;
        double y = sdct.scaleAndTranslateY(yCenter + radius );
        canvas.getGraphicsContext2D().strokeArc(x, y, w, h, stAng, angExt, ArcType.OPEN);
    }

}
