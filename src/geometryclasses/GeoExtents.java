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
class GeoExtents {

    Point2D upperLeft;
    Point2D lowerRight;

    GeoExtents() {
    }

    public void calcGeoExtentsFromChainList(ChainList chainList) {
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double maxY = Double.MIN_VALUE;

        if (chainList != null) {
            // Find smallest and largest X and Y values
            for (Chain chain : chainList) {
                for (SDCGeometricEntity geo : chain) {
                    minX = Math.min(minX, geo.getX1());
                    minX = Math.min(minX, geo.getX2());
                    minY = Math.min(minY, geo.getY1());
                    minY = Math.min(minY, geo.getY2());
                    maxX = Math.max(maxX, geo.getX1());
                    maxX = Math.max(maxX, geo.getX2());
                    maxY = Math.max(maxY, geo.getY1());
                    maxY = Math.max(maxY, geo.getY2());
                }
            }
        }

        upperLeft = new Point2D(minX, maxY);
        lowerRight = new Point2D(maxX, minY);
    }

    public Point2D getUpperLeft() {
        return upperLeft;
    }

    public Point2D getLowerRight() {
        return lowerRight;
    }

    public double getHeight() {
        return upperLeft.getY() - lowerRight.getY();
    }

    public double getWidth() {
        return lowerRight.getX() - upperLeft.getX();
    }

    public Point2D getMidpoint() {
        double midX = (upperLeft.getX() + lowerRight.getX()) / 2;
        double midY = (upperLeft.getY() + lowerRight.getY()) / 2;
        return new Point2D(midX, midY);
    }

    public double getWidthWithOriginIncluded() {
        double xLeft = Math.min(0, upperLeft.getX());
        double xRight = Math.max(0, lowerRight.getX());
        return xRight - xLeft;
    }

    public double getHeightWithOriginIncluded() {
        double yUpper = Math.max(0, upperLeft.getY());
        double yLower = Math.min(0, lowerRight.getY());
        return yUpper - yLower;
    }

    public Point2D getMidpointWithOriginIncluded() {
        double xLeft = Math.min(0, upperLeft.getX());
        double xRight = Math.max(0, lowerRight.getX());
        double yUpper = Math.max(0, upperLeft.getY());
        double yLower = Math.min(0, lowerRight.getY());
        return new Point2D((xRight + xLeft) / 2, (yUpper + yLower) / 2);
    }

}
