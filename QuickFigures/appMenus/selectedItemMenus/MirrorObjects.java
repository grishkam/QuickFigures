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
 * Date Modified: Dec 3, 2022
 * Version: 2023.2
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
import logging.IssueLog;
import messages.ShowMessage;
import undo.CombinedEdit;
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
		
		ArrayList<ZoomableGraphic> objects = super.getSelector().getSelecteditems();
		
		ShapeGraphic s=null;
		CombinedEdit c=new CombinedEdit();
		
		for(int i=0; i<objects.size(); i++) try {
			ZoomableGraphic o = objects.get(i);
			if(o instanceof ShapeGraphic) {
				s=(ShapeGraphic) o;
				ArrayList<ZoomableGraphic> objects2=new ArrayList<ZoomableGraphic>(); objects2.addAll(objects);
				UndoAddItem createMirror = mirrowShapeToImagePanels(objects2, s);
				if (createMirror!=null)
					c.addEditToList(createMirror);
				
			}
		} catch (Throwable t) {
			IssueLog.logT(t);
		}
		
		
		
		
		super.addUndo(
				c
				);

	}

	/**
	 * @param objects
	 * @param s
	 * @return
	 */
	public synchronized UndoAddItem mirrowShapeToImagePanels(ArrayList<ZoomableGraphic> objects, ShapeGraphic s) {
		ImagePanelGraphic startPanel=null;
		ArrayList<ImagePanelGraphic> otherPanels=new ArrayList<ImagePanelGraphic>();
		
		
		
		if(s==null) {
			//ShowMessage.showOptionalMessage("A shape and multiple image panels must be selected");
			return null;
		}
		
		boolean proceed = ShowMessage.showOptionalMessage("mirror is a new experimental feature", false, "Do you wish to mirror the object "+s);
		if(!proceed)
			return null;
		
		ArraySorter.removeThoseNotOfClass(objects, ImagePanelGraphic.class);
		for(ZoomableGraphic o: objects) {
			if(o instanceof ImagePanelGraphic) {
				ImagePanelGraphic panel=(ImagePanelGraphic) o;
				if(panel.getOutline().contains(s.getOutline().getBounds()))
					startPanel=panel;
				else  otherPanels.add(panel);
			}
		}
		
		
		if( startPanel==null) {
			ShowMessage.showOptionalMessage("A shape over an image panel must be selected");
			return null;
		}
		
		objects.remove(startPanel);
		
		if(s==null|| otherPanels.size()<1) {
			ShowMessage.showOptionalMessage("A shape and multiple image panels must be selected");
		}
		
		UndoAddItem createMirror = createMirror(s, startPanel, otherPanels);
		return createMirror;
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
		return getMenuCommand();
	}
}

