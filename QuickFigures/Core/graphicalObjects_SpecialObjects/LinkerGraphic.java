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
 * Date Created: Mar 7, 2023
 * Date Modified: Mar 7, 2023
 * Version: 2023.1
 */
package graphicalObjects_SpecialObjects;

import java.awt.Graphics2D;

import graphicalObjects.CordinateConverter;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_Shapes.PathGraphic;
import locatedObject.Named;
import locatedObject.PathPoint;
import logging.IssueLog;

/**
 A link between two paths. 
 */
public class LinkerGraphic implements ZoomableGraphic, Named{

	private GraphicLayer parent=null;
	
	PathGraphic path1;
	PathGraphic path2;

	private String name="Linker between paths";
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public GraphicLayer getParentLayer() {
		return parent;
	}

	@Override
	public void setParentLayer(GraphicLayer parent) {
		this.parent=parent;

	}

	@Override
	public void draw(Graphics2D graphics, CordinateConverter cords) {
		if(parent==null||path1==null||path2==null)
			return;
		PathPoint lastp1 = path1.getPoints().getLastPoint();
		PathPoint lastp2 = path2.getPoints().get(0);
		
		
		PathPoint p = path2.convertPointToExternalCrdinates(lastp2);
		p=path1.convertPointToInternalCrdinates(p);
		p.givePointLocationsTo(lastp1);
		
		
		path1.updatePathFromPoints();
		//lastp2.convertPointToInternalCrdinates(null);
	}

	/**
	 * @param i
	 */
	public void setLinkedItem(ZoomableGraphic i) {
		if(i instanceof PathGraphic) {
			if (path2==null) {
				path2= (PathGraphic) i;
			} else if (path1==null) {
				path1= (PathGraphic) i;
			} 
		}
		
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String st) {
		// TODO Auto-generated method stub
		
	}
	
	public String toString() {return getName();}


}
