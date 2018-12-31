package geometryclasses;

import java.awt.Color;
import java.awt.Graphics2D;
import javafx.scene.canvas.Canvas;

public abstract class SDCGeometricEntity {

    public enum GeometryType {
        LINE, ARC, POINT
    }
    private boolean selected = false;

    GeometryType geometryType;

    public abstract void draw(Graphics2D gr, float lineWidth, Color c);

    public abstract void drawOnCanvas(Canvas canvas, SDCTransform sDCTransform);

    public abstract double getX1();

    public abstract double getY1();

    public abstract double getX2();

    public abstract double getY2();

    public abstract void reverse();

    public GeometryType getGeometryType() {
        return geometryType;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }
}
