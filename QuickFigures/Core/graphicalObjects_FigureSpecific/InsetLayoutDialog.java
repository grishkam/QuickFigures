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
package graphicalObjects_FigureSpecific;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.Rectangle;
import java.util.ArrayList;

import genericMontageKit.PanelList;
import graphicalObjects_LayoutObjects.MontageLayoutGraphic;
import gridLayout.BasicMontageLayout;
import standardDialog.BooleanInputPanel;
import standardDialog.ComboBoxPanel;
import standardDialog.NumberInputPanel;
import standardDialog.SnappingPanel;
import standardDialog.StandardDialog;
import utilityClassesForObjects.AttachmentPosition;


	
	public class InsetLayoutDialog extends StandardDialog {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private InsetLayout currentLayout;
		private SnappingPanel snappanel;
		private PanelGraphicInsetDefiner currentInset;
	
		public final static String[] arrangements=new String[] {"Lock to lateral outsides", "Normal Placement" , "Fill Side", "On Both Insides", "On outer sides"};
		
		
		public InsetLayoutDialog(InsetLayout mover) {
			//setModal(true);
			
			this.currentLayout=mover;
			
		
			add("snaptypeClass", new ComboBoxPanel("Select snaptype", arrangements, mover.snaptype));
			
		
			add("border", new NumberInputPanel("Border Width", mover.border, 3));
		
			add("horizon", new BooleanInputPanel("Prefer Horizontal Panels", mover.horizontal));
			
			
			this.snappanel=new SnappingPanel(mover.sb , "placement of internal; Montage");
			snappanel.addObjectEditListener(this);
			
			GridBagConstraints gc = new GridBagConstraints();
			gc.gridwidth=4;
			gc.gridheight=2;
			gc.gridx=0;
			gc.anchor=GridBagConstraints.WEST;
			
			gc.gridy=7;
			gy+=2;
			// snappanel.getSnapBox().setToMontageMode();
			add( snappanel.getSnapBox(), gc);
			
			
			
		}
		
		
		protected void afterEachItemChange() {
			int snaptype=getInsetLayout().snaptype;
			
			getInsetLayout().border=(int) this.getNumber("border");
			getInsetLayout().snaptype=this.getChoiceIndex("snaptypeClass");
	
			getInsetLayout().horizontal=this.getBoolean("horizon");
			currentLayout.sb= snappanel.getSnappingBehaviour();
		
			int npanels=3;
			if (getInsetLayout().onSides()) npanels=5;
			MontageLayoutGraphic previewLayout = getInsetLayout().createLayout(npanels,  new Rectangle(0,0, 28,21), snappanel.getSnapBox().getReferenceObject().getBounds(), 1);
			BasicMontageLayout lg = previewLayout.getPanelLayout();
			previewLayout.setFilledPanels(true);
			previewLayout.setAlwaysShow(true);
		
			getInsetLayout();
			if (snaptype== InsetLayout.useSnapping) {
				lg.setVerticalBorder(8);
				lg.setBottomSpace(lg.labelSpaceWidthBottom-8);
				lg.setHorizontalBorder(8);
				lg.setRightSpace(lg.labelSpaceWidthRight-8);
				lg.resetPtsPanels();
			}
			
			snappanel.getSnapBox().setOverrideObject(previewLayout);
			snappanel.getSnapBox().getReferenceObject().setFillColor(Color.blue.darker());
			snappanel.getSnapBox().getReferenceObject().setStrokeWidth(0);
			snappanel.getSnapBox().repaint();
			redoInsetPositions();
		}
		
		void redoInsetPositions() {
			if (this.currentInset==null) return ;
			
			PanelList extralist=null;
			/**preparation if many panels are shared*/
			  if (currentInset.sharesPersonalLayer()) {
				  
				  ArrayList<PanelGraphicInsetDefiner> list = currentInset.getInsetDefinersThatShareLayout();
				  extralist = currentInset.getPanelManager().getPanelList().createDouble();
				  extralist.getPanels().clear();
					
					for(PanelGraphicInsetDefiner inset: list) {
						
							extralist.addAll(inset.getPanelManager().getPanelList().getPanels());
							InsetLayout.removeAbsentPanels(extralist, inset.getPanelManager().getPanelList(), inset);
							
					}
					
					//insetLayout.fixorderOfPanels(extralist, currentInset.personalGraphic);
				  
				  
			  }
			
			
			
			PanelList panelStack = currentInset.getPanelManager().getPanelList();
			MontageLayoutGraphic graphicRem = currentInset.personalGraphic;
			
			if (currentInset.sharesPersonalLayer()&&extralist!=null) {
				panelStack =extralist;
			 }
			
			
			currentLayout.applyInsetLayout(panelStack, currentInset);
			
			
			/**removes the old layout graphic*/
			currentInset.personalLayer.swapItemPositions(graphicRem, currentInset.personalGraphic);
			currentInset.personalLayer.remove(graphicRem);
			
			/**buggy need to fix many isues at once*/
			 if (currentInset.sharesPersonalLayer()&&extralist!=null) {
			
				//InsetTool.addExtraInsetPanelsToLayout(extralist, currentLayout.border, currentInset.getPanelManager(), false);//buggymust fix at soe point
				
				/**  ArrayList<PanelGraphicInsetDef> list2 = PanelGraphicInsetDef. getInsetDefinersFromLayer(currentInset.getParentLayer());
					for(PanelGraphicInsetDef l :list2) {
						l.personalGraphic=currentInset.personalGraphic;
						
						l.resizeMontageLayoutPanels();
						currentInset.previosInsetLayout.snapLayout(currentInset.personalGraphic, currentInset.getSourcePanel().getBounds());
					}*/
			 
			 }
			
			
			currentInset.updateDisplay();
			

			currentInset.personalGraphic.resizeLayoutToFitContents();
			
			Rectangle sourcePanelDim = currentInset.getSourcePanel().getBounds();
			InsetLayout inlayout = currentInset.previosInsetLayout;
			inlayout.prepareForSnapping(sourcePanelDim, currentInset.personalGraphic);
			if (inlayout.useSnapping()) {inlayout.snapLayout(currentInset.personalGraphic, sourcePanelDim);}
			
			currentInset.updateDisplay();
			
		}
		
		@Override
		public void onOK() {
			afterEachItemChange();
		}
		
		
		public InsetLayout getInsetLayout() {
			return currentLayout;
		}
		
		public void setTargetInset(PanelGraphicInsetDefiner inset) {
			this.currentInset=inset;
		}
		
		
		public static void main(String[] args) {
			
			InsetLayout inlay=new InsetLayout(2, InsetLayout.fill, true,AttachmentPosition.defaultInternal());
			new InsetLayoutDialog(inlay).showDialog();
		}
		
		
		
	}


