package illustratorScripts;


public class RasterItemRef extends  PlacedItemRef {

	/**when given a referance to an illustrator object with a pathitems collection, creates a script to 
	 att a new pathitem*/
	public String setToLastRaster(ArtLayerRef layer) {
		String output=getAssignment()+layer.refname+"rasterItems["+layer.refname+".rasterItems.length-1]";
		addScript(output);
		return output;
	}
}
