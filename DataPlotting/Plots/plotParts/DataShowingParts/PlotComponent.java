package plotParts.DataShowingParts;

public interface PlotComponent {
	
	/**returns the maximum values needed to plot the component*/
	double getMaxNeededValue();
	double getMaxNeededPosition();
	
	public void onAxisUpdate();

}
