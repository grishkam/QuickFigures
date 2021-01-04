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
package addObjectMenus;


import appContext.CurrentAppContext;
import figureOrganizer.MultichannelDisplayLayer;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;

public class ImagePlusAdder extends BasicGraphicAdder{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	boolean layonGrid=false;
	
	@Override
	public ZoomableGraphic add(GraphicLayer gc) {
		// TODO Auto-generated method stub
		;
		MultichannelDisplayLayer display = CurrentAppContext.getMultichannelContext().createMultichannelDisplay().creatMultiChannelDisplayFromUserSelectedImage(false, null);
		if (!gc.canAccept(display)) return null;
		display.setLaygeneratedPanelsOnGrid(layonGrid);
		display.getSlot().setImageDialog();
		gc.add(display);
		return display;
	}

	
	@Override
	public String getCommand() {
		return "Add Multichannel Image";
	}

	@Override
	public String getMenuCommand() {
		return "Add Multichannel Image";
	}

}
