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
public class SDCTransform {
    private Point2D viewportBottomLeftInModelSpace;
    private Point2D viewportTopRightInModelSpace;
    private double viewportWidth;
    private double viewportHeight;

    public SDCTransform( 
            Point2D viewportLowerLeftInModelSpace,
            Point2D viewportUpperRightInModelSpace,
            double viewportWidth,
            double viewportHeight ) {
        this.viewportBottomLeftInModelSpace = viewportLowerLeftInModelSpace;
        this.viewportTopRightInModelSpace = viewportUpperRightInModelSpace;
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;
    }

    private double viewportWidthInModelSpace() {
        return viewportTopRightInModelSpace.getX() - viewportBottomLeftInModelSpace.getX();
    }
    
    private double viewportHeightInModelSpace() {
        return viewportTopRightInModelSpace.getY() - viewportBottomLeftInModelSpace.getY();
    }
    
    public Point2D viewportCoordsFromModelCoords(Point2D modelCoords) {
        double modelX = modelCoords.getX();
        double viewportLeftInModelSpace = viewportBottomLeftInModelSpace.getX();
        double viewportX = ( modelX - viewportLeftInModelSpace ) / viewportWidthInModelSpace() * viewportWidth;
        
        double modelY = modelCoords.getY();
        double viewportTopInModelSpace = viewportTopRightInModelSpace.getY();
        double viewportY = ( modelY - viewportTopInModelSpace ) / viewportHeightInModelSpace() * viewportHeight;
        
        Point2D viewportCoords = new Point2D( viewportX, viewportY ) ;

        return viewportCoords;
    }
    
    public Point2D modelCoordsFromViewportCoords( Point2D viewportPoint ) {
        double modelX = viewportWidthInModelSpace() * viewportPoint.getX() / viewportWidth + viewportBottomLeftInModelSpace.getX();
        double modelY = -viewportHeightInModelSpace() * viewportPoint.getY() / viewportWidth +  viewportBottomLeftInModelSpace.getY();
        return new Point2D(modelX, modelY);
    }
    
    private double getScale() {
        return viewportWidthInModelSpace() / viewportWidth;
    }
    
    double scaleModelToViewport(double d) {
        return d * getScale();
    }
    
    double scaleViewportToModel( double d ) {
        return  d / getScale();
    }
    
    static SDCTransform buildScaleTransform(GeoExtents geoExtents, double viewPortWidth, double viewPortHeight) {
        // Get extra space from preferences
        double extraSpaceInViewport = sodickdxfcoderui.SodickDxfCoderPreferences.getInstance().getExtraSpaceInViewport();
        
        // Find out how much space needed in x and y
        double viewPortHeightInModelSpace = geoExtents.getHeightWithOriginIncluded() * extraSpaceInViewport;
        double viewPortWidthInModelSpace = geoExtents.getWidthWithOriginIncluded() * extraSpaceInViewport;
        
        // Check out the scale needed in x and y
        double scaleX = viewPortWidth / viewPortWidthInModelSpace;
        double scaleY = viewPortHeight / viewPortHeightInModelSpace;
        double scale = Math.min(scaleX, scaleY);
        
        double midPointX = geoExtents.getMidpoint().getX();
        double midPointY = geoExtents.getMidpoint().getY();
        
        double viewportLeft =  midPointX - viewPortWidthInModelSpace * scaleX / scale / 2;
        double viewportRight = viewportLeft + viewPortWidthInModelSpace * scaleX / scale ;
        
        double viewportBottom = midPointY - viewPortHeightInModelSpace * scaleY / scale / 2;
        double viewportTop = viewportBottom + viewPortHeightInModelSpace * scaleY/ scale;

        Point2D viewportBottomLeft = new Point2D( viewportLeft, viewportBottom);
                
        Point2D viewportTopRight = new Point2D( viewportRight, viewportTop );

        return new SDCTransform(viewportBottomLeft, viewportTopRight, viewPortWidth, viewPortHeight );
    }

    public void setZoomLevel(double zoomLevel) {

        System.out.println("zoomLevel " );
    }

    void setZoomCenterInViewportCoords(double zoomCenterX, double zoomCenterY ) {
        System.out.println("ZoomCenter : " );
    }
    

}
