package plotCreation;

import java.util.ArrayList;

import applicationAdapters.DisplayedImageWrapper;
import dataSeries.DataSeries;

public interface PlotCreator<Type extends DataSeries>  {
	
	public void createPlot(String name, ArrayList<Type> items, DisplayedImageWrapper diw);
	public String getNameText();
	
}
