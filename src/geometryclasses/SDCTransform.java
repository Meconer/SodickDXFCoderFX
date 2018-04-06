/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geometryclasses;

/**
 *
 * @author matsandersson
 */
class SDCTransform {
    private double scale;
    private double translateX;
    private double translateY;

    public SDCTransform(double scale, double translateX, double translateY) {
        this.scale = scale;
        this.translateX = translateX;
        this.translateY = translateY;
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public double getTranslateX() {
        return translateX;
    }

    public void setTranslateX(double translateX) {
        this.translateX = translateX;
    }

    public double getTranslateY() {
        return translateY;
    }

    public void setTranslateY(double translateY) {
        this.translateY = translateY;
    }

    double scaleAndTranslateX(double x) {
        return ( x + translateX ) * scale;
    }
    
    double scaleAndTranslateY(double y) {
        return -(y + translateY )* scale;
    }

    double scale(double d) {
        return d * scale;
    }
    
}
