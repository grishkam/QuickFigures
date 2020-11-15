package plotCreation;

import java.util.ArrayList;

import applicationAdapters.DisplayedImage;
import dataSeries.DataSeries;

public interface PlotCreator<Type extends DataSeries>  {
	
	public void createPlot(String name, ArrayList<Type> items, DisplayedImage diw);
	public String getNameText();
	
}
