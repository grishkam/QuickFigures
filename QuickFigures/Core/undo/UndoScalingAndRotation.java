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
package undo;

import java.awt.geom.Point2D;

import graphicalObjects.ImagePanelGraphic;
import graphicalObjects_BasicShapes.ArrowGraphic;
import graphicalObjects_BasicShapes.BarGraphic;
import graphicalObjects_BasicShapes.PathGraphic;
import graphicalObjects_BasicShapes.RectangularGraphic;
import graphicalObjects_BasicShapes.TextGraphic;
import graphicalObjects_FigureSpecific.PanelGraphicInsetDefiner;
import graphicalObjects_LayoutObjects.DefaultLayoutGraphic;
import layout.basicFigure.BasicLayout;
import utilityClassesForObjects.AttachmentPosition;


/**Written to undo scaling operations for a few different kinds of objects
  Also came to use this to undo mouse drags*/
public class UndoScalingAndRotation  extends AbstractUndoableEdit2 {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Object o;
	private ArrowGraphic iArrow;
	private ArrowGraphic fArrow;
	private RectangularGraphic fRect;
	private RectangularGraphic iRect;
	private PathGraphic fpath;
	private PathGraphic ipath;
	private PathEditUndo pathUndo;
	private BarGraphic fBar;
	private BarGraphic iBar;
	private double iScale;
	private Point2D iLoc;
	private double[] iFrame;
	private double fScale;
	private Point2D fLoc;
	private double[] fFrame;
	private BasicLayout iLayout;
	private BasicLayout fLayout;
	private UndoTextEdit tUndo1;
	private UndoMoveItems tUndo2;
	private AttachmentPosition iSnap;
	private AttachmentPosition fSnap;
	private double iAngle;
	private double fAngle;
	
	 public boolean isMyObject(Object ob) {
		  return ob==o;
	  }

	public UndoScalingAndRotation(Object o) {
		this.o=o;
		if (o instanceof ArrowGraphic) {
			iArrow = ((ArrowGraphic)o).copy();
		}
		
		if (o instanceof RectangularGraphic) {
			iRect = ((RectangularGraphic)o).copy();
		}
		
		if (o instanceof PathGraphic) {
			
			ipath=((PathGraphic)o).copy();
			pathUndo=new PathEditUndo((PathGraphic) o);
			
			
			}
		
		if (o instanceof TextGraphic) {
			
			TextGraphic ti=(TextGraphic) o;
			tUndo1=new UndoTextEdit(ti);
			tUndo2=new UndoMoveItems(ti);
			iAngle=ti.getAngle();
			}
		
	if (o instanceof BarGraphic) {
			
			iBar=((BarGraphic)o).copy();
			TextGraphic barText = ((BarGraphic) o).getBarText();
			tUndo1=new UndoTextEdit(barText);
			tUndo2=new UndoMoveItems(barText);
			iSnap=barText.getAttachmentPosition().copy();
			
			}
	
	if (o instanceof ImagePanelGraphic) {
		
		ImagePanelGraphic image = (ImagePanelGraphic)o;
		iScale=image.getScale();
		iLoc=image.getLocationUpperLeft();
		iFrame=new double[] {image.getFrameWidthH(), image.getFrameWidthV()};
		
		}
	
	if (o instanceof DefaultLayoutGraphic) {
		DefaultLayoutGraphic m=(DefaultLayoutGraphic) o;
		iLayout=new BasicLayout();
		iLayout.setToMatch(m.getPanelLayout());
		}
	}
	
	
	public void establishFinalState() {
		if (o instanceof ArrowGraphic) {
			fArrow = ((ArrowGraphic)o).copy();
		}
		
		if (o instanceof RectangularGraphic) {
			fRect = ((RectangularGraphic)o).copy();
		}
		
		if (o instanceof PathGraphic) {
			
			fpath=((PathGraphic)o).copy();
			
			}
		
		if (o instanceof BarGraphic) {
			
			fBar=((BarGraphic)o).copy();
			tUndo1.setUpFinalState();
			tUndo2.establishFinalLocations();
			fSnap=((BarGraphic)o).getBarText().getAttachmentPosition().copy();
			
			}
		
		if (o instanceof TextGraphic) {
			
			TextGraphic ti=(TextGraphic) o;
			tUndo1.setUpFinalState();
			tUndo2.establishFinalLocations();
			fAngle=ti.getAngle();
			}
		
		if (o instanceof ImagePanelGraphic) {
			
			ImagePanelGraphic image = (ImagePanelGraphic)o;
			fScale=image.getScale();
			fLoc=image.getLocationUpperLeft();
			fFrame=new double[] {image.getFrameWidthH(), image.getFrameWidthV()};
			
			}
		
		if (o instanceof DefaultLayoutGraphic) {
			DefaultLayoutGraphic m=(DefaultLayoutGraphic) o;
			fLayout=new BasicLayout();
			fLayout.setToMatch(m.getPanelLayout());
			}
		
	}
	
	public void redo() {
		
		if (o instanceof ArrowGraphic) {
			ArrowGraphic a = (ArrowGraphic)o;
			a.copyArrowAtributesFrom(fArrow);
			a.copyPositionFrom(fArrow);
			
			
		}
		
		
		if (o instanceof RectangularGraphic) {
			RectangularGraphic r = (RectangularGraphic)o;
			r.copyAttributesFrom(fRect);
			r.setRectangle(fRect.getBounds());
			r.setAngle(fRect.getAngle());
		}
		
		if (o instanceof PathGraphic) {
			PathGraphic p=(PathGraphic) o;
			pathUndo.redo();
			p.copyAttributesFrom(fpath);
			p.setLocation(fpath.getLocation());
		}
		
		if (o instanceof BarGraphic) {
			BarGraphic b=(BarGraphic) o;
			b.copyLocationFrom(fBar);
			b.copySizeAngleFrom(fBar);

			tUndo1.redo();
			tUndo2.redo();
			b.getBarText().setAttachmentPosition(fSnap);
		}
		
		if (o instanceof ImagePanelGraphic) {
			
			ImagePanelGraphic image = (ImagePanelGraphic)o;
			image.setRelativeScale(fScale);
			image.setLocationUpperLeft(fLoc);
			image.setFrameWidthH(fFrame[0]);image.setFrameWidthV(fFrame[1]);
		
			}
		
		if (o instanceof DefaultLayoutGraphic) {
			DefaultLayoutGraphic m=(DefaultLayoutGraphic) o;
			m.getPanelLayout().setToMatch(fLayout);
			m.getPanelLayout().resetPtsPanels();
			}
		
		if (o instanceof TextGraphic) {
			TextGraphic ti=(TextGraphic) o;
			
			ti.setAngle(fAngle);
			
			tUndo1.redo();
			tUndo2.redo();
			
			}
	
		if (o instanceof PanelGraphicInsetDefiner) {
			((PanelGraphicInsetDefiner) o).updateImagePanels();
		}
		
	}
	
	public void undo() {
		
		if (o instanceof ArrowGraphic) {
			ArrowGraphic a = (ArrowGraphic)o;
			a.copyPositionFrom(iArrow);
			a.copyArrowAtributesFrom(iArrow);
		}
		
		if (o instanceof RectangularGraphic) {
			RectangularGraphic r = (RectangularGraphic)o;
			r.copyAttributesFrom(iRect);
			r.setRectangle(iRect.getBounds());
			r.setAngle(iRect.getAngle());
		}
		
		if (o instanceof PathGraphic) {
			PathGraphic p=(PathGraphic) o;
			pathUndo.undo();
			p.copyAttributesFrom(ipath);
			p.setLocation(ipath.getLocation());
		}
		
		if (o instanceof BarGraphic) {
			BarGraphic b=(BarGraphic) o;
			b.copyLocationFrom(iBar);
			b.copySizeAngleFrom(iBar);

			tUndo1.undo();
			tUndo2.undo();
			b.getBarText().setAttachmentPosition(iSnap);
		}
		
		if (o instanceof TextGraphic) {
			TextGraphic ti=(TextGraphic) o;
			ti.setAngle(iAngle);
			tUndo1.undo();
			tUndo2.undo();
			
			}
		
		if (o instanceof ImagePanelGraphic) {
			
			ImagePanelGraphic image = (ImagePanelGraphic)o;
			image.setRelativeScale(iScale);
			image.setLocationUpperLeft(iLoc);
			image.setFrameWidthH(iFrame[0]);image.setFrameWidthV(iFrame[1]);
	
			}
		
		
		if (o instanceof DefaultLayoutGraphic) {
			DefaultLayoutGraphic m=(DefaultLayoutGraphic) o;
			m.getPanelLayout().setToMatch(iLayout);
			m.getPanelLayout().resetPtsPanels();
			}
		
		if (o instanceof PanelGraphicInsetDefiner) {
			((PanelGraphicInsetDefiner) o).updateImagePanels();
		}
		
		
	}

}
