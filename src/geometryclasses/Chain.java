package geometryclasses;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

public class Chain implements Iterable<SDCGeometricEntity>{

	private ArrayList<SDCGeometricEntity> entityList;
	private static final double CHAIN_LINK_DISTANCE = 0.002;
	private boolean selected = false;

	public Chain() {
		entityList = new ArrayList<SDCGeometricEntity>();
	}

	public Iterator<SDCGeometricEntity> iterator() {
		return entityList.iterator();
	}

	public boolean isSelected() {
		return selected;
	}


	public void setSelected(boolean selected) {
		this.selected = selected;
	}


	public Point2D.Double getStartPoint() {
		if (entityList.isEmpty()) return null;
		Point2D.Double startPoint = new Point2D.Double();
		startPoint.x = entityList.get(0).getX1();
		startPoint.y = entityList.get(0).getY1();
		return startPoint;
	}

	public Point2D.Double getEndPoint() {
		if (entityList.isEmpty()) return null;
		Point2D.Double startPoint = new Point2D.Double();
		startPoint.x = entityList.get(entityList.size()-1).getX2();
		startPoint.y = entityList.get(entityList.size()-1).getY2();
		return startPoint;
	}

	
	public boolean add(SDCGeometricEntity geo) {
		// Get end points of line
		double geoX1 = geo.getX1();
		double geoY1 = geo.getY1();
		double geoX2 = geo.getX2();
		double geoY2 = geo.getY2();

		if (entityList.isEmpty()) {
			// Chain is empty so the line can just be inserted.
			entityList.add(geo);
			return true;
		} else {
			// First check against chain start
			double chStartX = getStartPoint().getX();
			double chStartY = getStartPoint().getY();
			if (Point2D.Double.distance(chStartX, chStartY, geoX1, geoY1 ) < CHAIN_LINK_DISTANCE ) {
				// The chain start is close to the line start point. Reverse the line and add it at the chain start
				geo.reverse();
				entityList.add(0, geo);
				// Line was added. Return true for success
				return true;
			}
			if (Point2D.Double.distance(chStartX, chStartY, geoX2, geoY2) < CHAIN_LINK_DISTANCE ) {
				// The chain start is close to the line end point. Add the line at the chain start
				entityList.add(0, geo);
				// Line was added. Return true for success
				return true;
			}

			// If we get here the line wasn't added at the start. Now check the chain end.
			double chEndX = getEndPoint().getX();
			double chEndY = getEndPoint().getY();

			if (Point2D.Double.distance(chEndX, chEndY, geoX1, geoY1 ) < CHAIN_LINK_DISTANCE ) {
				// The chain end is close to the line start point. Add the line at the chain end
				entityList.add(geo);
				// Line was added. Return true for success
				return true;
			}
			if (Point2D.Double.distance(chEndX, chEndY, geoX2, geoY2) < CHAIN_LINK_DISTANCE ) {
				// The chain start is close to the line end point. Reverse the line and add it at the chain end
				geo.reverse();
				entityList.add(geo);
				// Line was added. Return true for success
				return true;
			}

			// If we get here the line couldn't be added. Return false for failure

			return false;
		}
		
	}

	// Tries to connect the chain with the supplied chain. Returns null if they can not connect.
	public Chain connect(Chain chainToConnect) {
		// Get chain start and end points
		double tStartX = getStartPoint().x;
		double tStartY = getStartPoint().y;
		double tEndX = getEndPoint().x;
		double tEndY = getEndPoint().y;
		double cStartX = chainToConnect.getStartPoint().getX();
		double cStartY = chainToConnect.getStartPoint().getY();
		double cEndX = chainToConnect.getEndPoint().getX();
		double cEndY = chainToConnect.getEndPoint().getY();

		if (Point2D.Double.distance(tStartX, tStartY, cStartX, cStartY) < CHAIN_LINK_DISTANCE) {
			// Chains can be connected by start points. Reverse the first chain and add the second one to the new end
			this.reverseChain();
			addChain(chainToConnect);
			return this;
		}
		
		if (Point2D.Double.distance(tStartY, tStartY, cEndX, cEndY) < CHAIN_LINK_DISTANCE) {
			// Chains can be connected at the end of the supplied chain and the start of this chain.
			// Reverse both chains and add the second to the first
			this.reverseChain();
			chainToConnect.reverseChain();
			addChain(chainToConnect);
			return this;
		}

		if (Point2D.Double.distance(tEndX, tEndY, cStartX, cStartY) < CHAIN_LINK_DISTANCE) {
			// Chains can be connected at the end of this chain and at the start of the second chain. Just add the supplied chain.
			addChain(chainToConnect);
			return this;
		}
		
		if (Point2D.Double.distance(tEndX, tEndY, cEndX, cEndY) < CHAIN_LINK_DISTANCE) {
			// Chains can be connected at the end points of the chains. Reverse the second one and add it.
			chainToConnect.reverseChain();
			addChain(chainToConnect);
			return this;
		}

		
		
		return null;
	}

	/**
	 * Adds supplied chain to this chain
	 */
	private void addChain(Chain chainToAdd) {
		Iterator<SDCGeometricEntity> chIter = chainToAdd.entityList.iterator();
		while (chIter.hasNext()) {
			SDCGeometricEntity geo = chIter.next();
			entityList.add(geo);
		}
	}

	public void reverseChain() {
		Chain newChain = new Chain();
		ListIterator<SDCGeometricEntity> geoIter = entityList.listIterator(entityList.size());
		while (geoIter.hasPrevious()) {
			SDCGeometricEntity geo = geoIter.previous(); // Get last entity
			geo.reverse();
			newChain.entityList.add(geo);
		}
		this.entityList = newChain.entityList;
	}

    public int getSize() {
        return entityList.size();
    }

    SDCGeometricEntity getEntity(int j) {
        return entityList.get(j);
    }


}
