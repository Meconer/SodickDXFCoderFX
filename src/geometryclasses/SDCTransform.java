/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geometryclasses;

import javafx.geometry.Point2D;

/**
 *
 * @author matsandersson
 */
class SDCTransform {

    private double scale;
    private Point2D translate;
    private double zoomLevel;
    private Point2D zoomCenter;

    public SDCTransform(double scale, Point2D translate) {
        this.scale = scale;
        this.translate = translate;
        zoomCenter = new Point2D(0, 0);
        zoomLevel = 1.0;
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public Point2D getTranslate() {
        return translate;
    }

    public void setTranslate( Point2D translate ) {
        this.translate = translate;
    }

    public Point2D viewportCoordsFromModelCoords(Point2D modelCoords) {
        Point2D viewportCoords = new Point2D(
                scaleModelToViewport( modelCoords.getX() + translate.getX() ),
                -scaleModelToViewport( modelCoords.getY() + translate.getY()));
                
        return viewportCoords;
    }
    
    public Point2D modelCoordsFromViewportCoords( Point2D viewportPoint ) {
        Point2D modelPoint = new Point2D(
                scaleViewportToModel(viewportPoint.getX()) - translate.getX(),
                -scaleViewportToModel(viewportPoint.getY()) - translate.getY());
                
        return modelPoint;
    }
    
    double scaleAndTranslateViewportToModelY( double y ) {
        return -scaleViewportToModel( y ) - translate.getY();
    }
    
    double scaleModelToViewport(double d) {
        return d * scale * zoomLevel;
    }
    
    double scaleViewportToModel( double d ) {
        return  d / scale / zoomLevel;
    }
    
    static SDCTransform buildScaleTransform(GeoExtents geoExtents, double viewPortWidth, double viewPortHeight) {
        double yScale = viewPortHeight / geoExtents.getHeightWithOriginIncluded();
        double xScale = viewPortWidth / geoExtents.getWidthWithOriginIncluded();

        double extraSpaceInViewport = sodickdxfcoderui.SodickDxfCoderPreferences.getInstance().getExtraSpaceInViewport();
        double scale = Math.min(xScale, yScale) / extraSpaceInViewport;

        double translateX = -geoExtents.getMidpointWithOriginIncluded().getX() + viewPortWidth / scale / 2;
        double translateY = -geoExtents.getMidpointWithOriginIncluded().getY() - viewPortHeight / scale / 2;

        return new SDCTransform(scale, new Point2D(translateX, translateY));
    }

    public void setZoomLevel(double zoomLevel) {
        if (zoomLevel < 0 ) {
            this.zoomLevel /= Math.abs(zoomLevel);
        } else {
            this.zoomLevel *= Math.abs(zoomLevel);
        }
        System.out.println("zoomLevel " + this.zoomLevel);
    }

    void setZoomCenterInViewportCoords(double zoomCenterX, double zoomCenterY ) {
        Point2D viewportCoords = new Point2D(zoomCenterX, zoomCenterY);
        Point2D modelZoomCenter = modelCoordsFromViewportCoords(viewportCoords);
        
        this.zoomCenter =  modelZoomCenter;
        System.out.println("ZoomCenter : " + this.zoomCenter.getX() + ":" + this.zoomCenter.getY());
    }
    

}
