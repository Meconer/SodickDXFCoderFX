package geometryclasses;

import geometryclasses.SDCGeometricEntity.GeometryType;
import java.awt.geom.Line2D;
import java.awt.geom.Arc2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;


public class ChainList implements Iterable<Chain>{


	ArrayList<Chain> listOfChains;

	
	// Constructor. Create a new list of chains
	public ChainList() {
		listOfChains = new ArrayList<>();
	}

	
	// Add a line to first chain that has start or endpoint at the same place as the start or end of the new line.
	// If it doesn't fit any chain in the list, start a new chain and put the line in that chain. If the line fits in a
	// chain then check if any chains can be connected and do that if that's the case.
	public void addGeo(SDCGeometricEntity geo) {

		// First go through existing chains and see if the new line can be added.
		Iterator<Chain> clIter = listOfChains.iterator();
		boolean ready = false;
		while (clIter.hasNext() && !ready){
			Chain chain = clIter.next();
			if (chain.add(geo)) {
				// if addLine returns true the line was added to the chain
				ready = true;
			}
		}
		if (ready) {
			tryToConnectChains();
		} else {
			// The line couldn't be added. Create a new chain and add the line to it.
			Chain newChain = new Chain();
			newChain.add(geo);
			listOfChains.add(newChain);
		}
			
	}


	private void tryToConnectChains() {
		// The line was added to the chain. Now check if some of the chains could be connected.
		Chain newChain = null;
		for (int i = 0; i < listOfChains.size()-1 ; i++ ) {
			for (int j = i+1 ; j < listOfChains.size(); j++) {
				// Try to connect the chains
				newChain = listOfChains.get(i).connect(listOfChains.get(j));
				if (newChain != null) {
					// The chains was connected. Remove chain j from the list and then break the loop
					listOfChains.remove(j);
					break;
				}
			}
			if (newChain != null ) break;
		}
	}


	public void clear() {
		listOfChains.clear();
		
	}


	public boolean isEmpty() {
		return listOfChains.isEmpty();
	}


	public Iterator<Chain> iterator() {
		return listOfChains.iterator();
	}


	public void selectElement(Rectangle2D.Double selRect) {
		ArrayList<SDCGeometricEntity> candidateList = new ArrayList<>();
		for (int i = 0 ; i < listOfChains.size() ; i++ ) {
			Chain ch = listOfChains.get(i);
			for (int j = 0 ; j < ch.entityList.size() ; j++) {
				SDCGeometricEntity geo = ch.entityList.get(j);
				geo.setSelected(false);
				if (geo.geometryType == GeometryType.ARC) {
					SDCArc a = (SDCArc) geo;
					Arc2D.Double a2 = new Arc2D.Double();
					a2.setArcByCenter(a.xCenter, a.yCenter, a.radius, a.stAng, a.angExt, Arc2D.OPEN);
					if (a2.intersects(selRect)) {
						candidateList.add(geo);
						geo.setSelected(true);
					}
				}
				if (geo.geometryType == GeometryType.LINE) {
					SDCLine l = (SDCLine) geo;
					Line2D.Double l2 = new Line2D.Double(l.x1,l.y1,l.x2,l.y2);
					if (l2.intersects(selRect)) {
						candidateList.add(geo);
						geo.setSelected(true);
					}
				}
			}
		}
	}

    public void addFromDxfFile(DxfFile dxfFile) {
        SDCGeometricEntity geoEntity = dxfFile.getFirstGeometricEntity();
        if ( geoEntity != null ) addGeo(geoEntity);
        
        do {
             geoEntity = dxfFile.getNextGeometricEntity();
             if ( geoEntity != null ) addGeo(geoEntity);
        } while ( geoEntity != null );
    }
}
