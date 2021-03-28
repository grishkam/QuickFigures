/**
 * Author: Greg Mazo
 * Date Modified: Mar 27, 2021
 * DateCreated: Mar 27, 2021
 * Version: 2021.1
 */
package fileread;

/**
A list of the major types of plot
 */
public enum PlotType {
	
	DEFAULT_PLOT_TYPE_COLS("exampleCols.xlsx"), 
	XY_PLOT_TYPE("ExampleXY.xlsx"), 
	GROUP_PLOT_TYPE("ExampleGrouped.xlsx"),  
	COLUMN_PLOT_TYPE("exampleCols.xlsx") , 
	KAPLAN_MEIER_PLOT_TYPE("Kaplan Example.xlsx");
	
	
	/**The file that can be used as an example*/
	private String example;

	PlotType(String exampleData) {
		this.example=exampleData;
	}
	
	public String getExampleFileName() {return example;}
}
