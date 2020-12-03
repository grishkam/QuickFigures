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
package graphicalObjectHandles;

import java.awt.Graphics2D;
import java.util.ArrayList;

import graphicalObjects.CordinateConverter;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import utilityClassesForObjects.LocatedObject2D;

/**draws multiple objects that can be seen by user but not clicked on or otherwise used.
  meant only for showing previews, messages and indicators to the user*/
public class GraphicList implements ZoomableGraphic {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<LocatedObject2D> items;

	public GraphicList(ArrayList<LocatedObject2D> o2) {
		this.items=o2;
	}

	@Override
	public GraphicLayer getParentLayer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setParentLayer(GraphicLayer parent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void draw(Graphics2D graphics, CordinateConverter<?> cords) {
		for(LocatedObject2D z: items) try {
			if (z instanceof ZoomableGraphic) {((ZoomableGraphic) z).draw(graphics, cords);}
		} catch (Throwable r) {}
		
	}

}