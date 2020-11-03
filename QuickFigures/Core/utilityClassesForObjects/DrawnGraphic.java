package utilityClassesForObjects;

public interface DrawnGraphic extends StrokedItem, Fillable, Named {
	
	public static int Rectangular=0, Text_Item=32, Shape=16, ImagePanel=8, Polygon=128, Line=256, Arrow=512;

	
	public int getTypeOfGraphic();


}
