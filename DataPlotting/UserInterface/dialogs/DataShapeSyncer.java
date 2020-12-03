/*******************************************************************************
 * Copyright (c) 2020 Gregory Mazo
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
package dialogs;

import java.util.ArrayList;

import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import plotParts.Core.PlotAreaRectangle;
import selectedItemMenus.BasicMultiSelectionOperator;
import standardDialog.StandardDialog;

public class DataShapeSyncer extends BasicMultiSelectionOperator{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int type=0;
	
	public DataShapeSyncer(int t) {type=t;}

	@Override
	public String getMenuCommand() {
		if (type==0) return "Data Bar Options";
		if (type==1) return "Error Bar Options";
		if (type==2) return "Scatter Points Options";
		if (type==3) return "Boxplot Options";
		if (type==4) return "Colors and Styles";
		if (type==5) return "Censor Marks of Kaplan-Meier Plot";
		return "Set plot data shape Options";
	}
	

	@Override
	public void run() {
		StandardDialog mt=null;
		if (type==0) mt = new MeanBarDialog(getArrayOfAllItems());
		if (type==1) mt = new ErrorBarDialog(getArrayOfAllItems());
		if (type==2) mt = new ScatterPointsDialog(getArrayOfAllItems());
		if (type==3) mt = new BoxPlotDialog(getArrayOfAllItems());
		if (type==4) mt=SeriesStyleDialog.createForPlotsInList(getArrayOfAllItems());
		if (type==5) mt=new CensorMarkDialog(getArrayOfAllItems());
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
