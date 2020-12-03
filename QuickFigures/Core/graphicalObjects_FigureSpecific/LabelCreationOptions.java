package graphicalObjects_FigureSpecific;

import gridLayout.RetrievableOption;

public class LabelCreationOptions {
	
	public static LabelCreationOptions current=new LabelCreationOptions() ;
	
	@RetrievableOption(key = "use Image anmes", label="Use Image Names To create labels")
	public boolean useImageNames=true;
	
	@RetrievableOption(key = "clip labels", label="Clip Labels Longer Than")
	public double clipLabels=50;

}
