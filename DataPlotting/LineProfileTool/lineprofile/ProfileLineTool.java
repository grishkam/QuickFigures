package lineprofile;
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
 * Date Created: Jan 29 2022
 * Date Modified: Jan 29 2022
 * Version: 2023.2
 */


import java.awt.geom.Point2D;
import java.util.ArrayList;

import applicationAdapters.ImageWorkSheet;
import figureOrganizer.PanelListElement;
import graphicTools.GraphicTool;
import graphicalObjects_SpecialObjects.ImagePanelGraphic;
import icons.AcronymIcon;
import icons.TreeIconWrappingToolIcon;
import layout.basicFigure.LayoutSpaces;
import locatedObject.LocatedObject2D;
import locatedObject.ObjectContainer;
import logging.IssueLog;
import undo.CombinedEdit;
import undo.UndoScalingAndRotation;


/**Tool used for drawing regions of interest to define the area displayed in inset panels
  */
public class ProfileLineTool extends GraphicTool implements LayoutSpaces {
	
	/**
	 * 
	 */
	public static final String INSET_TOOL_NAME = "Profile Line Tool";
	{{super.iconSet=TreeIconWrappingToolIcon.createIconSet(new AcronymIcon("P", 0));}
		
		}


			
	ArrayList<Integer> channelChoices=new ArrayList<Integer>();
	
	
	
	
	ImagePanelGraphic sourceImageforProfileLine=null;
	
	ProfileLine currentProfileLine=null;
	ProfileLine preExisting=null;
	
	
	ObjectContainer imageTargetted ;
	





	private CombinedEdit undo=new CombinedEdit();









	private boolean sizeDefiningMouseDrag;



	
	public ProfileLineTool() {
		super.temporaryTool=true;
		
	}
	
	/**returns the most recently edited profile line object*/
	public ProfileLine getMostRecentDrawnProfileLine() {return currentProfileLine;};
	
public void onPress(ImageWorkSheet gmp, LocatedObject2D roi2) {
	undo=new CombinedEdit();
	
	imageTargetted = this.getImageClicked();
	if (roi2 instanceof ProfileLine) {
		
		setupToolToEditExistingInset(roi2);
		return;
	} else 
		setupToolForImagePanel(roi2);
	}

/**
 * @param roi2
 */
public void setupToolToEditExistingInset(LocatedObject2D roi2) {
	currentProfileLine= (ProfileLine) roi2;
	sourceImageforProfileLine=currentProfileLine.getSourcePanel();
	sizeDefiningMouseDrag=false;
}

/**
 * @param roi2
 */
public void setupToolForImagePanel(LocatedObject2D roi2) {
	currentProfileLine=null;
	sizeDefiningMouseDrag=true;
		if (roi2 instanceof ImagePanelGraphic) {
			
			sourceImageforProfileLine=(ImagePanelGraphic) roi2;
			
		}
}
	
	public void onRelease(ImageWorkSheet imageWrapper, LocatedObject2D roi2) {
		
		if (currentProfileLine==null) return;
		if (!currentProfileLine.isValid()) {
			currentProfileLine.removeLineAndPlot();
			
		}
		
		addPlot(currentProfileLine);
		
		resizeCanvas();
		
	}
	





	
	/**
	 * @param currentProfileLine2
	 */
	private void addPlot(ProfileLine currentProfileLine2) {
		currentProfileLine2. createLineProfile();
		
	}

	public void mouseDragged() {
		try {
			refreshLineOnMouseDrag();
		} catch (Exception e) {
			IssueLog.logT(e);
		}
	}



	/**
	 * 
	 */
	void refreshLineOnMouseDrag() {
		if (!getImageDisplayWrapperClick().getUndoManager().hasUndo(undo)){
				this.getImageDisplayWrapperClick().getUndoManager().addEdit(undo);
		}
	
		createOrEditLine(this.clickedCord(), this.draggedCord());
	}

	
	/**when given an image panel and a bounds location (relative to the origin of the image panel),
	 * creates a line profile*/
	private ProfileLine createLineOnImagePanel(ObjectContainer ob,
			ImagePanelGraphic g, Point2D p1, Point2D p2) {
		this.setupToolForImagePanel(g);
		imageTargetted=ob;
		Point2D p = g.getLocationUpperLeft();
		
		p1.setLocation((int)p.getX()+p1.getX(), (int)p.getY()+p1.getY());
		p1.setLocation((int)p.getX()+p2.getX(), (int)p.getY()+p2.getY());
		this.createOrEditLine(p1, p2);
		return currentProfileLine;
	}
	
	/**creates a line for the drawn positions
	 * @param r
	 * @return 
	 */
	public ProfileLine createOrEditLine(Point2D p1, Point2D p2) {
		UndoScalingAndRotation scalingUndo = new UndoScalingAndRotation(currentProfileLine);
		undo.addEditToList(scalingUndo);
		
		boolean isRectValid=validPoints(p1, p2);
		
		if (sizeDefiningMouseDrag==true) {
							if (currentProfileLine==null) {
								currentProfileLine=createProfileLine(sourceImageforProfileLine, p1, p2);
							} else  {
								if (isRectValid) {
									currentProfileLine.setPoints(p1, p2);
								}
								}
			} else {
				super.mouseDragged();
				undo.addEditToList(super.currentUndo);
				}
		
		if (sourceImageforProfileLine==null||!isRectValid) return null;
		
		
		
		if(currentProfileLine==null) return null;
		
		
		
		
		return currentProfileLine;
	}

	/**
	 * @param p1
	 * @param p2
	 * @return 
	 */
	public ProfileLine createProfileLine(ImagePanelGraphic sourceImageforProfileLine, Point2D p1, Point2D p2) {
		
		if (sourceImageforProfileLine==null||!validPoints(p1, p2)) return null;
		
		currentProfileLine = new ProfileLine(sourceImageforProfileLine);
		currentProfileLine.setChannelChoices( sourceImageforProfileLine.getSourcePanel().getChannelEntries());
		
		
		sourceImageforProfileLine.getParentLayer().add(currentProfileLine);
		
		
		currentProfileLine.setPoints(p1, p2);
		currentProfileLine.moveLocation(sourceImageforProfileLine.getLocationUpperLeft().getX(),sourceImageforProfileLine.getLocationUpperLeft().getY());
		return currentProfileLine;
	}
	
	

	/**
	 * @param p1
	 * @param p2
	 * @return
	 */
	private boolean validPoints(Point2D p1, Point2D p2) {
		
		return true;
	}

	
	

	public static void setPanelFrames(double border, PanelListElement panel) {
		
		panel.getPanelGraphic().setFrameWidthH(border);
		panel.getPanelGraphic().setFrameWidthV(border);
	}
	
	
	


	
	
	
	@Override
	public String getToolTip() {
			
			return "Create Line profile";
		}
	@Override
	public String getToolName() {
			
			return INSET_TOOL_NAME;
		}

}
