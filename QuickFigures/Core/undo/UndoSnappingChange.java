package undo;

import utilityClassesForObjects.LocatedObject2D;
import utilityClassesForObjects.SnappingPosition;

public class UndoSnappingChange extends AbstractUndoableEdit2 {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private LocatedObject2D object;
	private SnappingPosition iSnap;
	private SnappingPosition fSnap;

	public UndoSnappingChange(LocatedObject2D object) {
		
		this.object=object;
		if (object==null ||object.getSnappingBehaviour()==null) return;
		iSnap=object.getSnappingBehaviour().copy();
	}
	
	public void establishFinalState() {
		fSnap=object.getSnappingBehaviour().copy();
	}
	
	public boolean same() {
		return iSnap.same(fSnap);
	}
	
	public void redo() {
		if (object==null) return;
		if (fSnap==null) object.setSnappingBehaviour(null); else
		fSnap.givePropertiesTo(object.getSnappingBehaviour());
		
	}
	
	public void undo() {
		if (object==null) return;
		if (iSnap==null) object.setSnappingBehaviour(null); else
		iSnap.givePropertiesTo(object.getSnappingBehaviour());
		
	}

}
