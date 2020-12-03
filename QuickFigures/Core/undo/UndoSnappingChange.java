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
		if (object==null ||object.getAttachmentPosition()==null) return;
		iSnap=object.getAttachmentPosition().copy();
	}
	
	public void establishFinalState() {
		fSnap=object.getAttachmentPosition().copy();
	}
	
	public boolean same() {
		return iSnap.same(fSnap);
	}
	
	public void redo() {
		if (object==null) return;
		if (fSnap==null) object.setAttachmentPosition(null); else
		fSnap.givePropertiesTo(object.getAttachmentPosition());
		
	}
	
	public void undo() {
		if (object==null) return;
		if (iSnap==null) object.setAttachmentPosition(null); else
		iSnap.givePropertiesTo(object.getAttachmentPosition());
		
	}

}
