package undo;

import utilityClassesForObjects.LocatedObject2D;
import utilityClassesForObjects.AttachmentPosition;

public class UndoSnappingChange extends AbstractUndoableEdit2 {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private LocatedObject2D object;
	private AttachmentPosition iSnap;
	private AttachmentPosition fSnap;

	public UndoSnappingChange(LocatedObject2D object) {
		
		this.object=object;
		if (object==null ||object.getSnapPosition()==null) return;
		iSnap=object.getSnapPosition().copy();
	}
	
	public void establishFinalState() {
		fSnap=object.getSnapPosition().copy();
	}
	
	public boolean same() {
		return iSnap.same(fSnap);
	}
	
	public void redo() {
		if (object==null) return;
		if (fSnap==null) object.setSnapPosition(null); else
		fSnap.givePropertiesTo(object.getSnapPosition());
		
	}
	
	public void undo() {
		if (object==null) return;
		if (iSnap==null) object.setSnapPosition(null); else
		iSnap.givePropertiesTo(object.getSnapPosition());
		
	}

}
