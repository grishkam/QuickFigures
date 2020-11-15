package basicAppAdapters;

import java.awt.Color;
import appContext.ToolbarColorContext;

public class ToolColors implements ToolbarColorContext {

	static Color foregroundCol=Color.black;
	static Color backgroundCol=Color.black;


	@Override
	public Color getForeGroundColor() {
		return foregroundCol;
	}

	@Override
	public Color getBackGroundColor() {
		return backgroundCol;
	}


}
