package geometryclasses;

import java.awt.Color;
import java.awt.Graphics2D;

public abstract class GeometricEntity {
	public enum GeometryType { LINE, ARC }
	private boolean selected = false;
	
	GeometryType geometryType;

	public abstract void draw(Graphics2D gr, float lineWidth, Color c);
	public abstract double getX1();
	public abstract double getY1();
	public abstract double getX2();
	public abstract double getY2();
	public abstract void reverse();
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	public boolean isSelected() {
		return selected;
	}
}
