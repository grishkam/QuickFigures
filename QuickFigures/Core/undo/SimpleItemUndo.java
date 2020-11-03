package undo;

public class SimpleItemUndo<Type> extends AbstractUndoableEdit2  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SimpleTraits<Type> parameter;
	private SimpleTraits<Type> starting;
	private SimpleTraits<Type> ending;
	private Type item;
	
	public SimpleItemUndo(SimpleTraits<Type> p) {
		this.item=p.self();
		this.parameter=p;
		
		starting= parameter.copy();
	}
	
	public void extablishFinalState() {
		ending= parameter.copy();
	}
	
	
	public void redo() {
		ending.giveTraitsTo(item);
	}
	
	public void undo() {
		starting.giveTraitsTo(item);
	}

}
