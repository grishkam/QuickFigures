package undo;

public interface SimpleTraits<Type> {
	
	public SimpleTraits<Type> copy();
	public void giveTraitsTo(Type t);
	public Type self();

}
