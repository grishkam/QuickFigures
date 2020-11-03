package genericMontageLayoutToolKit;

import java.awt.Rectangle;
import java.awt.Shape;

import genericMontageKit.SelectionManager;
import gridLayout.BasicMontageLayout;
import gridLayout.GenericMontageEditor;
import gridLayout.MontageEditorDialogs;
import logging.IssueLog;
import standardDialog.ComboBoxPanel;
import standardDialog.StandardDialog;
import utilityClassesForObjects.LocatedObject2D;

/**The panel selector tool allows one to select regions of interest based on the the MontageLayout available*/
public class Panel_Selector2 extends GeneralLayoutEditorTool {
	public int panelSelectionOption=0;
	public  final int SELECT_PANEL=0, SWAP_TWO_PANEL=1, MOVE_PANEL=2;
	public  int panelSelectionMod=0;
	public int swapper=0;
	public LocatedObject2D lastRoi=null;
	
	public void setpanelSelectionOption(int i) {
		panelSelectionOption=i;
	};
	
	public void showPanelSelectorOptions() {
		StandardDialog gd=new StandardDialog("Panel Selector Options", true);
		gd.add("Select what type of space",new ComboBoxPanel("Select what type of space", stringDescriptors, panelSelectionOption%100));
		gd.add("Mod", new ComboBoxPanel(" ", stringDescriptorsOfModifyers, panelSelectionMod));
		String[] option2=new String[] {"Select", "Swap", "Move"};
		gd.add("What to do ",new ComboBoxPanel("What to do ", option2,swapper));

		gd.showDialog();
		if (gd.wasOKed()) {	
			panelSelectionOption=gd.getChoiceIndex("Select what type of space");
			panelSelectionMod=gd.getChoiceIndex("Mod");
			panelSelectionOption+=panelSelectionMod*100;
			swapper=gd.getChoiceIndex("What to do ");

		}
	}
	

	 public void swapMontagePanels(BasicMontageLayout ml,  GenericMontageEditor me, int xc1, int yc1, int xc2, int yc2, int type){
		if (ml==null) {IssueLog.log("one is attempting to swap panels in a null layout"); return;}
		 Rectangle r1=(ml.getSelectedSpace(xc1, yc1, panelSelectionOption)).getBounds();
		Rectangle r2=(ml.getSelectedSpace( xc2, yc2, panelSelectionOption)).getBounds();
		me.swapMontagePanels(ml.getWrapper(), r1, r2);
	}

		public void checkExpansion(BasicMontageLayout basicMontageLayout, GenericMontageEditor me, int xc1, int yc1, int xc2, int yc2) {
			try{
			if (yc1>basicMontageLayout.montageHeight-basicMontageLayout.specialSpaceWidthBottom || yc2>basicMontageLayout.montageHeight-basicMontageLayout.specialSpaceWidthBottom ) {getEditor().addRows(basicMontageLayout, 1); return;}
			if (xc1>basicMontageLayout.montageWidth-basicMontageLayout.specialSpaceWidthRight || xc2>basicMontageLayout.montageWidth-basicMontageLayout.specialSpaceWidthRight) {getEditor().addCols(basicMontageLayout, 1); return;}
			if (xc1<0 || xc2<0) {
				me.addLeftLabelSpace(basicMontageLayout, basicMontageLayout.getPanelWidthOfColumn(1)/8); 
				return;}
			if (yc1<0 || yc2<0) {
				me.addTopLabelSpace(basicMontageLayout,basicMontageLayout.getPanelHeightOfRow(1)/8); 
				return;
				}
			} catch (Exception e) {IssueLog.log(e );}	
		}

		

		public void moveMontagePanel(BasicMontageLayout basicMontageLayout, GenericMontageEditor me, int xc1, int yc1, int xc2, int yc2, int type){
			checkExpansion(basicMontageLayout, me, xc1, yc1, xc2, yc2);
			basicMontageLayout=basicMontageLayout.makeAltered(type);
			me.moveMontagePanels(basicMontageLayout, basicMontageLayout.getPanelIndex(xc1, yc1), basicMontageLayout.getPanelIndex(xc2, yc2), type);
			//ml.getImage().updateAndDraw();
		}
		

		public void mousePressed() {
			setupClickedLayout();
			
			if (!hasALayoutBeenClicked()) {getSelManOfClcikedImage().removeSelections();return;}
		
			
				performPressEdit();
			
		}
		
		protected SelectionManager getSelManOfClcikedImage() {
			return getImageWrapperClick().getSelectionManagger();
		}
		
		protected void performPressEdit() {
			
			if (this.getCurrentLayout()==null
					||!getCurrentLayout().getBoundry().contains(this.getClickedCordinateX(), this.getClickedCordinateY())
					) {
				getImageWrapperClick().getSelectionManagger().removeSelections();return;
			}
			getSelManOfClcikedImage().select(getSelectedRoi(getCurrentLayout(), this.getClickedCordinateX(), this.getClickedCordinateY(), panelSelectionOption), 4, 0);
			
			
		}
		
		public void mouseDragged() {
			if (this.getCurrentLayout()==null)
				return;
			
			/**selects several panels if one has dragged from one to another*/
				if (this.swapper==this.SELECT_PANEL){
				
					this.getSelManOfClcikedImage().select(getCurrentLayout().rangeRoi(getClickedCordinateX(), getClickedCordinateY(), getDragCordinateX(), getDragCordinateY(), panelSelectionOption), 2, 0);
				return;
				}
				
			SelectionManager man = this.getImageWrapperClick().getSelectionManagger();
			man.select(getSelectedRoi(getCurrentLayout(), this.getDragCordinateX(), this.getDragCordinateY(), panelSelectionOption), 4, 1);
		}
		

		public Shape getSelectedRoi(BasicMontageLayout ml, int x, int y, int type) {
			// Roi newroi=new Roi(0, 0, 0, 0);
			 ml=ml.makeAltered(type);//Makes a copy of this MontageLayout with alterations depending on the type.
				  int index=ml.getPanelIndex(x,y);
			  //if (type==POINTS) return drawPointMarkers( ml, x, y);;
				  
			  Shape s=ml.getSelectedSpace(index, type); 
			  if (s instanceof Rectangle) return (Rectangle)s;
			return s;
		
		}

		
		protected void performDragEdit() {
			// TODO Auto-generated method stub
			
		}
		
		public void mouseReleased() {
			if (swapper==this.SELECT_PANEL) return;
			
			this.getImageWrapperClick().getSelectionManagger().removeSelections();
			if ((swapper==SWAP_TWO_PANEL || ( (shiftDown() && swapper==0)) ) ) {
				swapMontagePanels(this.getCurrentLayout(), getEditor(), getClickedCordinateX(), getClickedCordinateY(), getDragCordinateX(), getDragCordinateY(), panelSelectionOption );
				
			}
			if ((swapper==MOVE_PANEL || ( (altKeyDown() && swapper==0)) ) ) {
				moveMontagePanel(this.getCurrentLayout(),  getEditor(), getClickedCordinateX(), getClickedCordinateY(), getDragCordinateX(), getDragCordinateY(), panelSelectionOption);
				
			}
			updateClickedDisplay();
			
		}
		
		public void mouseClicked() {
			if (this.clickCount()>1)
			 new MontageEditorDialogs().showMontageLayoutEditingDialog(getCurrentLayout());
			
		}
		
		public void mouseMoved() {}
		
		public void mouseExited() {}
		{createIconSet("icons/PanelSelectorIcon.jpg","icons/PanelSelectorIconPressed.jpg","icons/PanelSelectorRolloverIcon.jpg");}



		public void showOptionsDialog() {
			showPanelSelectorOptions();
		}
		
		@Override
		public String getToolTip() {
				
				return "Select Panels";
			}
		@Override
		public String getToolName() {
				
				return "Select Panels";
			}

}
