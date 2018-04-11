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
    private double zoomLevel = 1.0;
    private double zoomCenterX = 0.0;
    private double zoomCenterY = 0.0;

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
        double zoomTranslationX = (x - zoomCenterX)*zoomLevel;
        return ( x + translateX + zoomTranslationX ) * scale;
    }
    
    double scaleAndTranslateY(double y) {
        double zoomTranslationY = (y - zoomCenterY)*zoomLevel;
        return -(y + translateY + zoomTranslationY)* scale ;
    }

    double scale(double d) {
        return d * scale * zoomLevel;
    }
    
    static SDCTransform buildScaleTransform(GeoExtents geoExtents, double viewPortWidth, double viewPortHeight) {
        double yScale = viewPortHeight / geoExtents.getHeightWithOriginIncluded();
        double xScale = viewPortWidth / geoExtents.getWidthWithOriginIncluded();

        double extraSpaceInViewport = sodickdxfcoderui.SodickDxfCoderPreferences.getInstance().getExtraSpaceInViewport();
        double scale = Math.min(xScale, yScale) / extraSpaceInViewport;

        double translateX = -geoExtents.getMidpointWithOriginIncluded().getX() + viewPortWidth / scale / 2;
        double translateY = -geoExtents.getMidpointWithOriginIncluded().getY() - viewPortHeight / scale / 2;

        return new SDCTransform(scale, translateX, translateY);
    }

    public void setZoomLevel(double zoomLevel) {
        if (zoomLevel < 0 ) {
            this.zoomLevel /= Math.abs(zoomLevel);
        } else {
            this.zoomLevel *= Math.abs(zoomLevel);
        }
        System.out.println("zoomLevel " + zoomLevel);
    }

    void setZoomCenterInViewportCoords(double zoomCenterX, double zoomCenterY, double viewportWidth, double viewportHeight ) {
        this.zoomCenterX = zoomCenterX / scale + translateX;
        this.zoomCenterY = zoomCenterY / scale + translateY;
    }
    
}
