/*******************************************************************************
 * Copyright (c) 2021 Gregory Mazo
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *******************************************************************************/
/**
 * Author: Greg Mazo
 * Date Modified: Jan 7, 2021
 * Version: 2023.2
 */
package dialogs;

import java.util.ArrayList;

import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import plotParts.Core.PlotAreaRectangle;
import selectedItemMenus.BasicMultiSelectionOperator;
import standardDialog.StandardDialog;

/**A multi selection operator that displays a dialog for many 
 * one among a few types of objects*/
public class DataShapeSyncer extends BasicMultiSelectionOperator{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final int
		Data_Bar=0,
		Error_Bar=1,
		Scatter_Point=2,
		Boxplot=3,
		Colors_and_styles=4,
		Censor_Marks=5
		;
	
	
	public int type=Data_Bar;
	
	public DataShapeSyncer(int t) {type=t;}

	@Override
	public String getMenuCommand() {
		if (type==Data_Bar) return "Data Bar Options";
		if (type==Error_Bar) return "Error Bar Options";
		if (type==Scatter_Point) return "Scatter Points Options";
		if (type==Boxplot) return "Boxplot Options";
		if (type==Colors_and_styles) return "Colors and Styles";
		if (type==Censor_Marks) return "Censor Marks of Kaplan-Meier Plot";
		return "Set plot data shape Options";
	}
	

	@Override
	public void run() {
		StandardDialog mt=null;
		if (type==Data_Bar) mt = new MeanBarDialog(getArrayOfAllItems());
		if (type==Error_Bar) mt = new ErrorBarDialog(getArrayOfAllItems());
		if (type==Scatter_Point) mt = new ScatterPointsDialog(getArrayOfAllItems());
		if (type==Boxplot) mt = new BoxPlotDialog(getArrayOfAllItems());
		if (type==Colors_and_styles) mt=SeriesStyleDialog.createForPlotsInList(getArrayOfAllItems());
		if (type==Censor_Marks) mt=new CensorMarkDialog(getArrayOfAllItems());
		if (mt!=null&&mt.hasContent()) mt.showDialog();
	}

	/**Returns a list of items to check through and add to the dialog
	   */
	public ArrayList<ZoomableGraphic> getArrayOfAllItems() {
		ArrayList<ZoomableGraphic> list=new ArrayList<ZoomableGraphic>();
		list.addAll(getAllArray());
		for(ZoomableGraphic z: getAllArray()) {
			if (z instanceof PlotAreaRectangle) {
				GraphicLayer  r=((PlotAreaRectangle) z).getParentLayer();
				list.addAll(r.getAllGraphics());
			}
		}
		
		return list;
	}
	
	public String getMenuPath() {
		
		return "Plot Objects";
	}
}
