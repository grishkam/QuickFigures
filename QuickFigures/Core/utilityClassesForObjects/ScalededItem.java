package utilityClassesForObjects;

/**for any item in which pixels have a width, height and depth corresponsing to a unit*/
public interface ScalededItem {
	/**setters and getters for scale info*/
	public ScaleInfo getScaleInfo();
	public void setScaleInfo(ScaleInfo s);
	
	/**If the item is drawn onto the graphics with a scaling,
	   gets the corrected scale into
	 * */
	public ScaleInfo getDisplayScaleInfo();
	
	/**returns the sixe of the item in units by units.
	  for example, it may be 2micron*2micron*/
	public double[] getDimensionsInUnits();
}
