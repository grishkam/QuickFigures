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
 * Date Modified: Jan 6, 2021
 * Version: 2022.2
 */
package selectedItemMenus;

import java.util.ArrayList;

import applicationAdapters.ImageWorkSheet;
import graphicalObjects.FigureDisplayWorksheet;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;

/**An interface for any ui that allows the user to selected items and layers*/
public interface LayerSelectionSystem {
	/**The primary selected layer*/
	public GraphicLayer getSelectedLayer();
	
	/**the list of selected items*/
	public ArrayList<ZoomableGraphic> getSelecteditems();
	
	public FigureDisplayWorksheet getWorksheet();
	public ImageWorkSheet getImageWrapper();
}
