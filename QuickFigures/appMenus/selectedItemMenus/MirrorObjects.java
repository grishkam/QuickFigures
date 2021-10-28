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
 * Date Modified: May 1, 2021
 * Version: 2021.2
 */
package selectedItemMenus;

import java.util.ArrayList;

import addObjectMenus.BasicGraphicAdder;
import figureOrganizer.insetPanels.PanelGraphicInsetDefiner;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_LayerTypes.PanelMirror;
import graphicalObjects_Shapes.ShapeGraphic;
import graphicalObjects_SpecialObjects.ImagePanelGraphic;
import locatedObject.LocatedObject2D;
import messages.ShowMessage;
import undo.UndoAddItem;
import utilityClasses1.ArraySorter;

/**A menu options for showing a scale bar dialog for selected scale bars*/
public class MirrorObjects extends BasicGraphicAdder {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String getMenuCommand() {
		return "Mirror Selected items";
	}

	@Override
	public void run() {
		
		ArrayList<LocatedObject2D> objects = super.getAllObjects();
		ShapeGraphic s=null;
		ImagePanelGraphic startPanel=null;
		ArrayList<ImagePanelGraphic> otherPanels=new ArrayList<ImagePanelGraphic>();
		
		for(LocatedObject2D o: objects) {
			if(o instanceof ShapeGraphic) {
				s=(ShapeGraphic) o;
			}
		}
		
		if(s==null) {
			ShowMessage.showOptionalMessage("A shape and multiple image panels must be selected");
			return;
		}
		
		boolean proceed = ShowMessage.showOptionalMessage("mirror is a new experimental feature", true, "Do you wish to mirror the object "+s);
		if(!proceed)
			return;
		
		ArraySorter.removeThoseNotOfClass(objects, ImagePanelGraphic.class);
		for(LocatedObject2D o: objects) {
			if(o instanceof ImagePanelGraphic) {
				ImagePanelGraphic panel=(ImagePanelGraphic) o;
				if(panel.getOutline().contains(s.getOutline().getBounds()))
					startPanel=panel;
				else  otherPanels.add(panel);
			}
		}
		
		
		if( startPanel==null) {
			ShowMessage.showOptionalMessage("A shape over an image panel must be selected");
			return;
		}
		
		objects.remove(startPanel);
		
		if(s==null|| otherPanels.size()<1) {
			ShowMessage.showOptionalMessage("A shape and multiple image panels must be selected");
		}
		
		super.addUndo(
				createMirror(s, startPanel, otherPanels)
				);

	}

	/**
	 * @param s
	 * @param startPanel
	 * @param otherPanels
	 * @return 
	 */
	public UndoAddItem createMirror(ShapeGraphic s, ImagePanelGraphic startPanel, ArrayList<ImagePanelGraphic> otherPanels) {
		PanelMirror output = new PanelMirror(s, startPanel, otherPanels);
		GraphicLayer parentLayer = s.getParentLayer();
		if(s instanceof PanelGraphicInsetDefiner) {
			PanelGraphicInsetDefiner instDef=(PanelGraphicInsetDefiner) s;
			parentLayer =instDef.personalLayer;
		}
		parentLayer.add(output);
		
		
		return new UndoAddItem(parentLayer, output);
		
	}

	
	@Override
	public String getMenuPath() {
		String mainMenu = "To selected panels";
		
		return mainMenu;
	}

	@Override
	public ZoomableGraphic add(GraphicLayer gc) {
		this.run();
		return null;
	}

	@Override
	public String getCommand() {
		// TODO Auto-generated method stub
		return getMenuCommand();
	}
}

