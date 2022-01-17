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
 * Date Modified: Jan 4, 2021
 * Version: 2022.0
 */
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
	  It basically just creates a Layout
	  that is appropriate for insets and places it in 
	  the right location relative to the source panel*/
public 	class InsetLayout implements LayoutSpaces, Serializable{
	
	
	/**
		 * 
		 */
		private static final int DEFAULT_BORDER = 2;

	
		private static final long serialVersionUID = 1L;

	/** set to true if one needs to ignore panels that have been removed and arrange only the panels that are actually in the image will be layed out*/
	public boolean practicalSize=true;//

	
	public final static String[] arrangements=new String[] {"Lock to lateral outsides", "Normal Placement" , "Fill Side", "On Both Insides", "On Outer sides", "Dont arrange panels"};
	
	static final int 
			OUTSIDE_LEFT_RIGHT_ATTACHED_TO_PARENT=0, 
			DEFAULT_PLACEMENT=1,
			FILL_SPACE=2,
					ON_INNER_SIDES=3,
							ON_OUTER_SIDES=4,
			
			FREE_LOCATIONS=5;
	
	
		double border=DEFAULT_BORDER;
		int positiontype=DEFAULT_PLACEMENT;
		 boolean horizontal;
		 
		 /**The attachment position for the inset layout.
		  * This only acts as a recommendation */
		 AttachmentPosition position;

		 /**Creates an inset layout with the given parameters*/
		public InsetLayout(int borderWidth, int insetPositionType, boolean preferhorizontal, AttachmentPosition sb) {
			horizontal=preferhorizontal;
			this.position=sb;
			border=borderWidth;
			positiontype=insetPositionType;
		}
		public InsetLayout copy() {
			return new InsetLayout((int)border, positiontype, horizontal, position.copy());
		}
		
		
		/**returns true if the prefFered layout of the panels is horizontal*/
		private boolean horizontal() {
			if (horizontal) return true;
			return false;
		}
		
		/**returns true if the attachment position stored within this layout will be used*/
		boolean useAttachmentPosition() {
			if (positiontype==FILL_SPACE) return true;
			if (positiontype==ON_INNER_SIDES) return true;
			if (positiontype==ON_OUTER_SIDES) return true;
			return defaultPlacement();
		}
		
		
		
	
		/**returns true if inset panels will be attached to the parent panel*/
		boolean lockInsetPanelsToSourcePanel() {
			if (positiontype==OUTSIDE_LEFT_RIGHT_ATTACHED_TO_PARENT ) return true;
			return false;
		}
		
		/**returns true if the width of the layout containing inset panels should not exceed the width of the parent panel*/
		boolean limtedToWidthOfParentPanel() {
			if (position.isExternalSnap()&&defaultPlacement()) { return false;}
			
			if ((belowParentPanel()||this.aboveParentPanel())&&fillSide()) return true;
			if(useAttachmentPosition() &&position.isInternalSnap()) return true;
			
			return false;
		}
		
		/**returns true if the height of the layout containing inset panels should not exceed the height of the parent panel*/
		boolean limtedToHeightOfParentPanel() {
			if (position.isExternalSnap()&&defaultPlacement()) { return false;}
			if (rightOfParentPanel()&&fillSide()) return true;
			
			/**TODO: determine if this part is obsolete*/
			if (rightOfParentPanel() &&(useAttachmentPosition()&&position.isExternalRightSnap()&&defaultPlacement())) return true;
			
			if(useAttachmentPosition() &&position.isInternalSnap()) return true;
			return false;
		}
		/**
		 * @return
		 */
		private boolean defaultPlacement() {
			return positiontype==DEFAULT_PLACEMENT;
		}
		
		/**returns true if panels will be placed below the parent panel*/
		boolean belowParentPanel() {
			if (useAttachmentPosition()&&position.isInternalSnap()) return false;
			
			if (useAttachmentPosition()&&position.isExternalBottomEdgeSnap()) return true;
			
			return false;
		}
		
		/**returns true if panels will be placed above the parent panel*/
		boolean aboveParentPanel() {
			if (useAttachmentPosition()&&position.isInternalSnap()) return false;
			
			if (useAttachmentPosition()&&position.isExternalTopEdgeSnap()) return true;
			
			return false;
		}
		
		/**returns true if the group of small panels will be placed to the right
		 * of the parent panel*/
		boolean rightOfParentPanel() {
			if (belowParentPanel() ) return false;
			if (useAttachmentPosition()&&position.isInternalSnap()) return false;
			if (useAttachmentPosition()&&position.isExternalRightSnap()) return true;
			return true;
		}
		
		/**returns true if the group of small panels will be placed to the left
		 * of the parent panel*/
		boolean leftOfParentPanel() {
			if (belowParentPanel() ) return false;
			if (useAttachmentPosition()&position.isInternalSnap()) return false;
			if (useAttachmentPosition()&&position.isExternalLeftSnap()) return true;
			return false;
		}
	
		
		/**returns true if a row major panel arrangement is used as the starting arrangement*/
		private boolean rowmajorPanelArrangement() {
			
			if(innerSideBottomOrTop()) return true;
			
			if (positiontype==ON_OUTER_SIDES) {
				if (this.outerSideBottomOrTop())   return true; else return false;
			}
			
			
			return this.horizontal;
					
		}
		
		/**Creates a layout that fits the panel list and the inset*/
		public	DefaultLayoutGraphic createLayout(PanelList list, PanelGraphicInsetDefiner inset) {
			ImagePanelGraphic insetPanel = (ImagePanelGraphic) list.getPanels().get(0).getImageDisplayObject();
			
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
		
		/**reorders the panel list to match the order that the panels actually appear
		 * within the layout*/
		public static boolean fixorderOfPanels(PanelList output, DefaultLayoutGraphic g) {
			if (g==null) return false;
			
			ArrayList<PanelListElement> correctOrder = PanelManager.getPanelsInLayoutOrder(output.getPanels(), g.getPanelLayout().getPanels());
			output.getPanels().clear();
			output.addAll(correctOrder);
			
			return true;
		}
		
		/**returns true if the layout will be arranged such that the inset panels
		 * are on either side of the parent panel*/
		boolean onSides() {
			if(positiontype==ON_INNER_SIDES||positiontype==ON_OUTER_SIDES)  return true;
			
			return false;
		}
		
		/**Creates a layout in which panels of size insetPanelDim are fit around a panel with sourcePanelDim*/
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
							
							
							BasicLayout bml = new BasicLayout(ncol, nrow, (int)insetPanelDim.getWidth(), (int)insetPanelDim.getHeight(), (int) border,(int)border, rowmajorPanelArrangement());
				
							DefaultLayoutGraphic lg = new DefaultLayoutGraphic(bml);
							
							theSnap=prepareLayoutForAttachment(sourcePanelDim, lg);
							
							if(useAttachmentPosition()) {	
								 attachLayout(lg, sourcePanelDim, theSnap);
							}
							return lg;
			
		}
			
			/**Edits the layout to be appropriate for automatic positioning of the inset panels
			 * around the parent panel and returns the attachment position*/
			public AttachmentPosition prepareLayoutForAttachment(Rectangle parentPanelSize, DefaultLayoutGraphic lg) {
							BasicLayout bml = lg.getPanelLayout();
							AttachmentPosition theSnap = position.copy();
							double overallheight = parentPanelSize.getHeight();
							double overallwidth = parentPanelSize.getWidth();
							int ncol = bml.nColumns();
							int nrow=bml.nRows();
						
						/**Adjusts border to fit right*/
						if ((ncol==1&&fillSide())||(onSides()&&nrow>1)) {
							while(bml.getSelectedSpace(1, ALL_OF_THE+PANELS).getBounds().getHeight()<overallheight ) {
								bml.setVerticalBorder(bml.theBorderWidthBottomTop+1);
								;
							}
						} 
						
						/**Adjusts border to fit bottom*/
						if( (nrow==1&&fillSide())||(onSides()&&ncol>1)) {
							while(bml.getSelectedSpace(1, ALL_OF_THE+PANELS).getBounds().getWidth()<overallwidth ) {
								
								bml.setHorizontalBorder(bml.theBorderWidthLeftRight+1);
							
							}
						} 
						
						if (rightOfParentPanel() && !position.isExternalTopEdgeSnap()) {
							/**only need to move it to the right side if layout is at right*/
								bml.setLeftSpace((int) border);
								bml.move( parentPanelSize.getMaxX(),  parentPanelSize.getY());
								}
						
						if (leftOfParentPanel()) {
								bml.setRightSpace((int) border);
								//bml.move( sourcePanelDim.getMaxX(),  sourcePanelDim.getY());
								}
						if (belowParentPanel()) {
							
							bml.setTopSpace((int) border);
							bml.move( parentPanelSize.getX(),  parentPanelSize.getMaxY());
							}
						
							if (aboveParentPanel()) {
							
							bml.setBottomSpace((int) border);
							bml.move( parentPanelSize.getX(),  parentPanelSize.getMaxY());
							}
						
						
						
						if (positiontype==ON_INNER_SIDES) {
							theSnap=makeSideOfParentAttachmentPosition() ;
						}
						
						/**If the user wants to set the position and have the new layout placed inside and to the side of source panel*/
						if(useAttachmentPosition()) {	
							if (positiontype==ON_OUTER_SIDES &&(ncol==2||nrow==2)) {
								theSnap=makeSideOfParentAttachmentPosition() ;
								if(ncol==2 && !(outerSideBottomOrTop()&&nrow==2)) {
									bml.setHorizontalBorder((int) (parentPanelSize.width+border*2));
								
								} else 
								if(nrow==2) {
									bml.setVerticalBorder((int) (parentPanelSize.height+border*2));
									//bml.setVerticalBorder((int)(border+bml.BorderWidthBottomTop+insetPanelDim.getWidth()*2));
								}
							}
							
							
						
							
						}
						
							
							return theSnap;
			}
			
			/**returns the a an attachment position that will be used to for the layout
			 this may differ from the recommended position
			 */
			private AttachmentPosition makeSideOfParentAttachmentPosition() {
				AttachmentPosition theSpot = position.copy();
				
				
				if (this.positiontype==ON_OUTER_SIDES) {
						theSpot.setLocationCategory(AttachmentPosition.INTERNAL);
						
						if (position.getSnapLocationTypeInternal()==RectangleEdges.TOP||position.getSnapLocationTypeInternal()==RectangleEdges.BOTTOM) {
							theSpot.setLocationTypeInternal(RectangleEdges.LEFT);
						}else 	if (position.getSnapLocationTypeInternal()==RectangleEdges.LEFT||position.getSnapLocationTypeInternal()==RectangleEdges.RIGHT) {
							theSpot.setLocationTypeInternal(RectangleEdges.TOP);
						} else {
							theSpot.setLocationTypeInternal(RectangleEdges.CENTER);
						theSpot.setLocationTypeInternal(RectangleEdges.MIDDLE);
						}
				}
				

				if (this.positiontype==ON_INNER_SIDES) {
					theSpot.setLocationCategory(AttachmentPosition.INTERNAL);
					theSpot.setLocationTypeInternal(RectangleEdges.UPPER_LEFT);
				}
				
				return theSpot;
			}
			
			/**puts inset panels in preliminary locations*/
			void snapLayout(DefaultLayoutGraphic lg, Rectangle SourcePanelDim) {
				AttachmentPosition attachmentSite = makeSideOfParentAttachmentPosition();
				attachLayout(lg, SourcePanelDim, attachmentSite);
			}
			/**
			 * @param lg
			 * @param SourcePanelDim
			 * @param attachmentSite
			 */
			public void attachLayout(DefaultLayoutGraphic lg, Rectangle SourcePanelDim,
					AttachmentPosition attachmentSite) {
				attachmentSite.snapObjectToRectangle(lg,SourcePanelDim );
			}
			
			
			
			/**returns true if the placement will be on the outer sides of the parent panel
			  above or below*/
			boolean outerSideBottomOrTop() {
				if (positiontype==ON_OUTER_SIDES &&position.getSnapLocationTypeInternal()==RectangleEdges.TOP) return true;
				if (positiontype==ON_OUTER_SIDES &&position.getSnapLocationTypeInternal()==RectangleEdges.BOTTOM) return true;
				return false;
				
			}
			/**returns true if the placement will be on the inner sides of the parent panel
			  above or below*/
			boolean innerSideBottomOrTop() {
				if (positiontype==ON_INNER_SIDES &&position.getSnapLocationTypeInternal()==RectangleEdges.TOP) return true;
				if (positiontype==ON_INNER_SIDES &&position.getSnapLocationTypeInternal()==RectangleEdges.BOTTOM) return true;
				return false;
				
			}
		
		/**returns true if the borders of the inset layout should be adjusted to fill the side of the parent panel where it appears*/
		boolean fillSide() {
			if(positiontype==FILL_SPACE) return true;
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
			 
			/**If the user chooses not to have the panels locked, sorted nor layout out
			  then the tool will just put the panels near the inset*/
			if(positiontype==FREE_LOCATIONS) {
				ArrayList<ImagePanelGraphic> graphics = listOfPanels;
				for(ImagePanelGraphic g: graphics) {
					g.setLocation(new Point((int)inset.getBounds().getMaxX()+(int)border+5, (int)inset.getBounds().getMinY()+(int)border-5));
				
				}
			}
			
			
			/**Attached individual inset panels to the source panel*/
			if (lockInsetPanelsToSourcePanel())   {
				
								for(int i=0; i<list.getSize(); i++) {
									
									ImagePanelGraphic insetPanel = (ImagePanelGraphic) listOfPanels.get(i);
									insetPanel.setAttachmentPosition(getParentAttachmentForPanel(i));
									inset.getSourcePanel().addLockedItem( insetPanel);
									setFrameSize(insetPanel);
								}
								return;
								}
			else {
				//if panels do not need to be attached to their parent, this detaches them
				for(int i=0; i<list.getSize(); i++) {
					ImagePanelGraphic insetPanel = (ImagePanelGraphic) listOfPanels.get(i);
					inset.getSourcePanel().removeLockedItem( insetPanel);
					
				}
			}
			
			
			if (OUTSIDE_LEFT_RIGHT_ATTACHED_TO_PARENT!=positiontype&&positiontype!=FREE_LOCATIONS) {
				forMontageLayoutAppliestoInset(list, inset);
			}
			
			
		}
		
		/**Sets the frame size for a panel*/
		public void setFrameSize(ImagePanelGraphic g) {
			if (!needsFrame()) return;
			g.setFrameWidthH(border);
			g.setFrameWidthV(border);
		}
		
	
		
		/**returns true if the panels should be created with frames*/
		private boolean needsFrame() {
			if (useAttachmentPosition() &&position.isExternalSnap()) {
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
				
			
				
				
				setter.layDisplayPanelsOfStackOnLayout( list, lg.getPanelLayout(), false);
				//lg.resizeLayoutToFitContents();//??//TODO: determine if resize of layout is helpful to user at this point and delete commented line
		}
		
		
		
		void setBorders(ImagePanelGraphic insetPanel) {
			 setFrameSize(insetPanel);
			 
		}
		
		/**returns the attachment position for the individual panel */
		AttachmentPosition getParentAttachmentForPanel(int i) {
			ArrayList<AttachmentPosition> snaps =getSnappings();
			if(i>=snaps.size()) return null;
			AttachmentPosition snap = snaps.get(i);
			if (positiontype==OUTSIDE_LEFT_RIGHT_ATTACHED_TO_PARENT)snap.setHorizontalOffset((int)border);
			return snap;
		}

		/**returns a list of attachment positions around the edges of a parent panel*/
		ArrayList<AttachmentPosition> getSnappings() {
			if (positiontype==OUTSIDE_LEFT_RIGHT_ATTACHED_TO_PARENT)return AttachmentPosition.externalRightLeft();
			if (defaultPlacement()) return AttachmentPosition.internalSpread(); 
			return AttachmentPosition.externalRightLeft();
		}
		
	}

