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
    private Point2D translation;
    private double zoomLevel;
    private Point2D zoomCenter;

    public SDCTransform(double scale, Point2D translation) {
        this.scale = scale;
        this.translation = translation;
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
        return translation;
    }

    public void setTranslate( Point2D translation ) {
        this.translation = translation;
    }

    public Point2D viewportCoordsFromModelCoords(Point2D modelCoords) {
        
        double distanceToZoomCenterX = modelCoords.getX() - zoomCenter.getX();
        double distanceToZoomCenterY = modelCoords.getY() - zoomCenter.getY();
        double zoomedModelX = zoomLevel * distanceToZoomCenterX;
        double zoomedModelY = zoomLevel * distanceToZoomCenterY;
        double viewportX = scaleModelToViewport(zoomedModelX + translation.getX());
        double viewportY = -scaleModelToViewport( zoomedModelY + translation.getY());
        
        Point2D viewportCoords = new Point2D( viewportX, viewportY ) ;

        return viewportCoords;
    }
    
    public Point2D modelCoordsFromViewportCoords( Point2D viewportPoint ) {
        double zoomedModelX = scaleViewportToModel(viewportPoint.getX() - translation.getX());
        double zoomedModelY = -scaleViewportToModel(viewportPoint.getY() - translation.getY());
        
        double zoomedDistanceToZoomCenterX = zoomedModelX - zoomCenter.getX();
        double zoomedDistanceToZoomCenterY = zoomedModelY - zoomCenter.getY();
        
        double distanceToZoomCenterX = zoomedDistanceToZoomCenterX / zoomLevel;
        double distanceToZoomCenterY = zoomedDistanceToZoomCenterY / zoomLevel;

        Point2D modelPoint = new Point2D( 
                distanceToZoomCenterX + zoomCenter.getX(),
                distanceToZoomCenterY + zoomCenter.getY());

        return modelPoint;
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
