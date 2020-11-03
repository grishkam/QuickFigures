package utilityClassesForObjects;

public interface LocationChangeListener extends Mortal{
	
	
	public void objectMoved(LocatedObject2D object);
	public void objectSizeChanged(LocatedObject2D object);
	public void objectEliminated(LocatedObject2D object);
	public void userMoved(LocatedObject2D object);
	public void userSizeChanged(LocatedObject2D object);
	
}
