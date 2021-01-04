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
package figureOrganizer.insetPanels;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.Serializable;
import java.util.ArrayList;

import figureOrganizer.PanelList;
import figureOrganizer.PanelListElement;
import figureOrganizer.PanelManager;
import figureOrganizer.PanelSetter;
import graphicalObjects_LayoutObjects.DefaultLayoutGraphic;
import graphicalObjects_SpecialObjects.ImagePanelGraphic;
import layout.basicFigure.BasicLayout;
import layout.basicFigure.LayoutSpaces;
import locatedObject.AttachmentPosition;
import locatedObject.RectangleEdges;


	/**Class used to position the inset's image panels
	  It basically just creates a Layout Graphic
	  that is appropriate for insets and places it in 
	  the right location relative to the source panel*/
public 	class InsetLayout implements LayoutSpaces, Serializable{
	
	
	/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

	public boolean practicalSize=true;// set to true if one want the number of panels to be treated as the ones actually in the image and not just in the panel stack

	static final int free=5, outsideLR=0, 
			useSnapping=1,
			fill=2,
					onSides=3,
							onOuterSides=4,
			
			fillRight=11, 
			fillbottom=12,
					insideLR=13, 
			
			verticalRight=7,
			rightTop=8,
			rightBottom=9, 
			montageBelowPanel=10;
	
	
		double border=2;
		int positiontype=0;
		 boolean horizontal;
		 
		 AttachmentPosition position;


		public InsetLayout(int b, int type, boolean preferhorizontal, AttachmentPosition sb) {
			horizontal=preferhorizontal;
			this.position=sb;
			border=b;
			positiontype=type;
		}
		public InsetLayout copy() {
			return new InsetLayout((int)border, positiontype, horizontal, position.copy());
		}
		
		
		/**returns true if the prefered layout of the montage is horizontal*/
		private boolean horizontal() {
			if (verticalRight==positiontype) return false;
			if (rightTop==positiontype) return true;
			if (rightBottom==positiontype) return true;
			
			if (horizontal) return true;
			return false;
		}
		
		boolean useSnapping() {
			if (positiontype==fill) return true;
			if (positiontype==onSides) return true;
			if (positiontype==onOuterSides) return true;
			return positiontype==useSnapping;
		}
		
		
		
		boolean atRight() {
			if (positiontype==verticalRight) return true;
			if (positiontype==fillRight) return true;
			if (positiontype==rightTop) return true;
			
		
			return false;
		}
		
		boolean lockToSourcePanel() {
			if (positiontype==insideLR ) return true;
			if (positiontype==outsideLR ) return true;
			
			return false;
		}
		
		boolean limtedToWidthOfParentPanel() {
			if (position.isExternalSnap()&&positiontype==useSnapping) { return false;}//if a nonfilling but snapping optin is used
			
			if ((belowParentPanel()||this.aboveParentPanel())&&fillSide()) return true;
			if(useSnapping() &&position.isInternalSnap()) return true;
			
			return false;
		}
		
		boolean limtedToHeightOfParentPanel() {
			if (position.isExternalSnap()&&positiontype==useSnapping) { return false;}
			if (rightOfParentPanel()&&fillSide()) return true;
			if (rightOfParentPanel() &&(useSnapping()&&position.isExternalRightSnap()&&useSnapping==positiontype)) return true;
			if(useSnapping() &&position.isInternalSnap()) return true;
			return false;
		}
		
		boolean belowParentPanel() {
			if (useSnapping()&&position.isInternalSnap()) return false;
			if (positiontype==montageBelowPanel ) {
				return true;
			}
			if (positiontype==fillbottom ) {
				return true;
			}
			if (useSnapping()&&position.isExternalBottomEdgeSnap()) return true;
			
			return false;
		}
		
		boolean aboveParentPanel() {
			if (useSnapping()&&position.isInternalSnap()) return false;
			
			if (useSnapping()&&position.isExternalTopEdgeSnap()) return true;
			
			return false;
		}
		
		/**returns true if the group of small panels will be placed to the right
		 * of the parent panel*/
		boolean rightOfParentPanel() {
			if (belowParentPanel() ) return false;
			if (useSnapping()&&position.isInternalSnap()) return false;
			if (useSnapping()&&position.isExternalRightSnap()) return true;
			return true;
		}
		
		/**returns true if the group of small panels will be placed to the left
		 * of the parent panel*/
		boolean leftOfParentPanel() {
			if (belowParentPanel() ) return false;
			if (useSnapping()&position.isInternalSnap()) return false;
			if (useSnapping()&&position.isExternalLeftSnap()) return true;
			return false;
		}
	
		
		boolean horizontalPanelArrangement() {
			
			if(innerSideBottomOrTop()) return true;
			
			if (positiontype==onOuterSides) {
				if (this.outerSideBottomOrTop())   return true; else return false;
			}
			
			
			if (positiontype==fillRight) return true;
			if (positiontype==rightTop) return true;
			if (positiontype==rightBottom) return true;
			return this.horizontal;
			//return false;
					
		}
		
		/**Creates a layout that fit the panel list and the inset*/
		public	DefaultLayoutGraphic createLayout(PanelList list, PanelGraphicInsetDefiner inset) {
			ImagePanelGraphic insetPanel = (ImagePanelGraphic) list.getPanels().get(0).getLocatedImageDisplayObject();
			
			Rectangle insetPanelDim = insetPanel.getBounds();
			Rectangle sourcePanelDim = inset.getSourcePanel().getBounds();
			
			
			int npanel=list.getPanels().size();
			if (practicalSize) {
				npanel=getPanelsPresentInImage(list, inset).getPanels().size();
			}
			
			return createLayout(npanel, insetPanelDim, sourcePanelDim, inset.totalThatSharesPersonalLayer());
		}
		
		
		/**returns only the panels in the image that are included in the layer. Reorders */
		PanelList getPanelsPresentInImage(PanelList list, PanelGraphicInsetDefiner inset) {
			PanelList output = list.createDouble();
			output.getPanels().clear();
			output.add(list);//the same exact panels not copies need to be included in the new list
			
			removeAbsentPanels(output, list, inset);
			
			fixorderOfPanels(output, inset.personalLayout);
			
			
			
			//IssueLog.log("Cut list fown from "+list.getPanels().size()+ " to "+output.getPanels().size());
			return output;
			
		}
		
		/**if some of the panels graphics are not in the personal layer their list elements are removed from output*/
		public static boolean removeAbsentPanels(PanelList output, PanelList input, PanelGraphicInsetDefiner inset) {
			
			for(PanelListElement g: input.getPanels() ) {
				ImagePanelGraphic panelGraphic = g.getPanelGraphic();
				if (!inset.personalLayer.hasItem(panelGraphic) &&!inset.getParentLayer().hasItem(panelGraphic))  output.remove(g);
			}
			
			return true;
		}
		
		public static boolean fixorderOfPanels(PanelList output, DefaultLayoutGraphic g) {
			if (g==null) return false;
			
			ArrayList<PanelListElement> correctOrder = PanelManager.getPanelsInLayoutOrder(output.getPanels(), g.getPanelLayout().getPanels());
			output.getPanels().clear();
			output.addAll(correctOrder);
			
			return true;
		}
		
		boolean onSides() {
			if(positiontype==onSides||positiontype==onOuterSides)  return true;
			
			return false;
		}
		
		/**Creates a layout inwhich npanels of size insetPanelDim are fit around a panel with sourcePanelDim*/
			public DefaultLayoutGraphic createLayout(int npanel, Rectangle insetPanelDim, 	Rectangle sourcePanelDim, int nimages ) {
			AttachmentPosition theSnap = position.copy();
			double overallheight = sourcePanelDim.getHeight();
			double overallwidth = sourcePanelDim.getWidth();
			int ncol=1;
			int nrow=1;
			
			boolean horizontal=horizontal();
			if(onSides())  {
				horizontal=false;
				if ( theSnap.isInternalSnap() &&theSnap.getSnapLocationTypeInternal()==RectangleEdges.TOP) horizontal=true;
				if ( theSnap.isInternalSnap() &&theSnap.getSnapLocationTypeInternal()==RectangleEdges.BOTTOM) horizontal=true;
				
			}
			if(fillSide() &&theSnap.isExternalRightSnap())  horizontal=false;
			if(fillSide() &&theSnap. isExternalLeftSnap())  horizontal=false;
			if(fillSide() &&theSnap.isExternalTopEdgeSnap())  horizontal=true;
			if(fillSide() &&theSnap. isExternalBottomEdgeSnap())  horizontal=true;
			if(fillSide() && theSnap.isInternalSnap() &&theSnap.getSnapLocationTypeInternal()==RectangleEdges.LEFT) horizontal=false;
			if(fillSide() && theSnap.isInternalSnap() &&theSnap.getSnapLocationTypeInternal()==RectangleEdges.RIGHT) horizontal=false;
			
			
			
			
			if(fillSide() &&theSnap.isExternalBottomEdgeSnap())  horizontal=true;
		
			if (horizontal)
				 {ncol=npanel/ nimages; nrow= nimages;} 
			else {nrow=npanel/ nimages; ncol= nimages;}
			
			if (limtedToHeightOfParentPanel()&&!horizontal&&npanel>1) {
			
					double rows = overallheight/(insetPanelDim.getHeight()+border);
					nrow=(int) Math.floor(rows);//sets the row number to the max that will fit
					if (nrow>npanel) nrow=npanel;
					ncol=nimages;
					while(ncol*nrow<npanel) {ncol++;}//handles the too few column problem
					while (ncol*nrow-ncol>=npanel&&!(onSides() )) {nrow--;}//handles too many row problem
			}
			
			/**if rows go below the layout, sets things up*/
			if ( limtedToWidthOfParentPanel() &&horizontal&&npanel>1) {
				
				double cols = overallwidth/(insetPanelDim.getWidth()+border);
				ncol=(int) Math.floor(cols);//sets col number to maximum
				if (ncol>npanel) ncol=npanel;
				nrow=nimages ;
				while(ncol*nrow<npanel) {nrow++;}//handles too few row problem
				if (ncol*nrow-nrow>=npanel&&!(onSides() )) {ncol--;}//handles too many column problem
				
				
			}
			
			
			BasicLayout bml = new BasicLayout(ncol, nrow, (int)insetPanelDim.getWidth(), (int)insetPanelDim.getHeight(), (int) border,(int)border, horizontalPanelArrangement());

			DefaultLayoutGraphic lg = new DefaultLayoutGraphic(bml);
			
			theSnap=prepareForSnapping(sourcePanelDim, lg);
			
			if(useSnapping()) {	
				 theSnap.snapObjectToRectangle(lg, sourcePanelDim);
			}
			return lg;
			
		}
			
			/**Edits the layout to be appropriate for automatic positioning and returns the attachment positino*/
			public AttachmentPosition prepareForSnapping(Rectangle sourcePanelDim, DefaultLayoutGraphic lg) {
				BasicLayout bml = lg.getPanelLayout();
				AttachmentPosition theSnap = position.copy();
				double overallheight = sourcePanelDim.getHeight();
				double overallwidth = sourcePanelDim.getWidth();
				int ncol = bml.nColumns();
				int nrow=bml.nRows();
			
			/**Adjusts border to fit right*/
			if ((ncol==1&&fillSide())||(onSides()&&nrow>1)) {
				while(bml.getSelectedSpace(1, ALL_OF_THE+PANELS).getBounds().getHeight()<overallheight ) {
					bml.setVerticalBorder(bml.BorderWidthBottomTop+1);
					;
				}
			} 
			
			/**Adjusts border to fit bottom*/
			if( (nrow==1&&fillSide())||(onSides()&&ncol>1)) {
				while(bml.getSelectedSpace(1, ALL_OF_THE+PANELS).getBounds().getWidth()<overallwidth ) {
					
					bml.setHorizontalBorder(bml.BorderWidthLeftRight+1);
				
				}
			} 
			
			if (rightOfParentPanel() && !position.isExternalTopEdgeSnap()) {
				/**only need to move it to the right side if montage is at right*/
					bml.setLeftSpace((int) border);
					bml.move( sourcePanelDim.getMaxX(),  sourcePanelDim.getY());
					}
			
			if (leftOfParentPanel()) {
				
					bml.setRightSpace((int) border);
					//bml.move( sourcePanelDim.getMaxX(),  sourcePanelDim.getY());
					}
			if (belowParentPanel()) {
				
				bml.setTopSpace((int) border);
				bml.move( sourcePanelDim.getX(),  sourcePanelDim.getMaxY());
				}
			
				if (aboveParentPanel()) {
				
				bml.setBottomSpace((int) border);
				bml.move( sourcePanelDim.getX(),  sourcePanelDim.getMaxY());
				}
			
			
			
			if (positiontype==onSides) {
				theSnap=makeSnapping() ;
			}
			
			/**If the user wants to set the position and have the new montage placed inside and to the side of source panel*/
			if(useSnapping()) {	
				if (positiontype==onOuterSides &&(ncol==2||nrow==2)) {
					theSnap=makeSnapping() ;
					if(ncol==2 && !(outerSideBottomOrTop()&&nrow==2)) {
						bml.setHorizontalBorder((int) (sourcePanelDim.width+border*2));
					
					} else 
					if(nrow==2) {
						bml.setVerticalBorder((int) (sourcePanelDim.height+border*2));
						//bml.setVerticalBorder((int)(border+bml.BorderWidthBottomTop+insetPanelDim.getWidth()*2));
					}
				}
				
				
				 //theSnap.snapObjectToRectangle(lg, sourcePanelDim);
				
			}
			
				
				return theSnap;
			}
			
			public void snapLayout(DefaultLayoutGraphic lg, Rectangle SourcePanelDim) {
				makeSnapping().snapObjectToRectangle(lg,SourcePanelDim );
			}
			
			/**returns the snapping behavior that will actually be used to snap the rectangle. this differs
			  from the internally saved snapping for some sets of options
			 */
			private AttachmentPosition makeSnapping() {
				AttachmentPosition theSnap = position.copy();
				
				
				if (this.positiontype==onOuterSides) {
						theSnap.setLocationCategory(AttachmentPosition.INTERNAL);
						
						if (position.getSnapLocationTypeInternal()==RectangleEdges.TOP||position.getSnapLocationTypeInternal()==RectangleEdges.BOTTOM) {
							theSnap.setLocationTypeInternal(RectangleEdges.LEFT);
						}else 	if (position.getSnapLocationTypeInternal()==RectangleEdges.LEFT||position.getSnapLocationTypeInternal()==RectangleEdges.RIGHT) {
							theSnap.setLocationTypeInternal(RectangleEdges.TOP);
						} else {
							theSnap.setLocationTypeInternal(RectangleEdges.CENTER);
						theSnap.setLocationTypeInternal(RectangleEdges.MIDDLE);
						}
				}
				

				if (this.positiontype==onSides) {
					theSnap.setLocationCategory(AttachmentPosition.INTERNAL);
					theSnap.setLocationTypeInternal(RectangleEdges.UPPER_LEFT);
				}
				
				return theSnap;
			}
			
			boolean outerSideBottomOrTop() {
				if (positiontype==onOuterSides &&position.getSnapLocationTypeInternal()==RectangleEdges.TOP) return true;
				if (positiontype==onOuterSides &&position.getSnapLocationTypeInternal()==RectangleEdges.BOTTOM) return true;
				return false;
				
			}
			
			boolean innerSideBottomOrTop() {
				if (positiontype==onSides &&position.getSnapLocationTypeInternal()==RectangleEdges.TOP) return true;
				if (positiontype==onSides &&position.getSnapLocationTypeInternal()==RectangleEdges.BOTTOM) return true;
				return false;
				
			}
		
		boolean fillSide() {
			if (positiontype==fillRight) return true;
			if(positiontype==fillbottom) return true;
			if(positiontype==fill) return true;
			return false;
		}
		

		/**When given a panel list and inset, sets up a layout according to the current options*/
		public void applyInsetLayout(PanelList list, PanelGraphicInsetDefiner  inset) {
			 inset.previosInsetLayout=this.copy();
			 
			 if (practicalSize) {
				 list =getPanelsPresentInImage(list, inset);//Accounts for removed, moved panels. ensures so the list order, content matches the x,y positions of the panels
			}
			 
			 ArrayList<ImagePanelGraphic> listOfPanels = list.getPanelGraphics();

			 /**updates the frames around each panel*/
				for(ImagePanelGraphic g: listOfPanels) {
					setFrameSize(g);
				}
			 
			/**If the user chooses not to have the panels locked, sorted and layout out
			  then the tool will just put the panels near the inset*/
			if(positiontype==free) {
				ArrayList<ImagePanelGraphic> graphics = listOfPanels;
				for(ImagePanelGraphic g: graphics) {
					g.setLocation(new Point((int)inset.getBounds().getMaxX()+(int)border+5, (int)inset.getBounds().getMinY()+(int)border-5));
				
				}
			}
			
			
			/**simply locks to the source panel*/
			if (lockToSourcePanel())   {
				
								for(int i=0; i<list.getSize(); i++) {
									
									ImagePanelGraphic insetPanel = (ImagePanelGraphic) listOfPanels.get(i);
									insetPanel.setAttachmentPosition(getSnapping(i));
									inset.getSourcePanel().addLockedItem( insetPanel);
									setFrameSize(insetPanel);
								}
								return;
								}
			
			
			if (outsideLR!=positiontype&& insideLR!=positiontype&&positiontype!=free) {
				forMontageLayoutAppliestoInset(list, inset);
			}
			
			
		}
		
		public void setFrameSize(ImagePanelGraphic g) {
			if (!needsFrame()) return;
			g.setFrameWidthH(border);
			g.setFrameWidthV(border);
		}
		
	
		
		/**returns true if the panels should be created with frames*/
		private boolean needsFrame() {
			if (useSnapping() &&position.isExternalSnap()) {
				return false;
			}
			return true;
		}
		
		private void forMontageLayoutAppliestoInset(PanelList list, PanelGraphicInsetDefiner  inset) {

			
					DefaultLayoutGraphic lg = createLayout(list, inset);
			
			 		inset.personalLayer.add(lg);
			 		inset.personalLayer.swapmoveObjectPositionsInArray(lg, inset.personalLayer.getItemArray().get(0));
					inset.personalLayout=lg;
								
								
				
				PanelSetter setter = new PanelSetter();
				setter.layDisplayPanelsOfStackOnLayout( list, lg.getPanelLayout(), false);
			
				
				ArrayList<ImagePanelGraphic> imagelist=new ArrayList<ImagePanelGraphic>();
				for(int i=0; i<list.getSize(); i++) {
					
					ImagePanelGraphic ob = (ImagePanelGraphic) list.getPanels().get(i).getImageDisplayObject();
					imagelist.add(ob);
					//lg.addLockedItem(ob);
					ob.setAttachmentPosition(AttachmentPosition.defaultInternalPanel());
					setBorders(ob);
					
				}
				
				
				if (positiontype==rightBottom) {
					double heightDiff = inset.getSourcePanel().getBounds().getHeight()-lg.getBounds().getHeight();
					lg.moveLocation(0, heightDiff);
				}
				
				
				setter.layDisplayPanelsOfStackOnLayout( list, lg.getPanelLayout(), false);
				//lg.resizeLayoutToFitContents();//??//TODO: determine if resize of layout is helpful to user at this point and delete commented line
		}
		
		
		
		void setBorders(ImagePanelGraphic insetPanel) {
			 setFrameSize(insetPanel);
			 
		}
		
		/**returns the attachment position for the */
		AttachmentPosition getSnapping(int i) {
			ArrayList<AttachmentPosition> snaps =getSnappings();
			AttachmentPosition snap = snaps.get(i);
			if (positiontype==0)snap.setHorizontalOffset((int)border);
			return snap;
		}

		/***/
		ArrayList<AttachmentPosition> getSnappings() {
			if (positiontype==0)return AttachmentPosition.externalRightLeft();
			if (positiontype==1) return AttachmentPosition.internalSpread(); 
			return AttachmentPosition.externalRightLeft();
		}
		
	}

