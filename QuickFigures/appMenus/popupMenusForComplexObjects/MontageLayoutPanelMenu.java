package popupMenusForComplexObjects;

import java.awt.event.ActionEvent;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import fLexibleUIKit.ObjectAction;
import genericMontageUIKitMenuItems.MontageEditCommandMenu;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_LayoutObjects.MontageLayoutGraphic;
import graphicalObjects_LayoutObjects.PanelLayoutGraphic;
import menuUtil.SmartPopupJMenu;
import menuUtil.HasUniquePopupMenu;
import undo.UndoManagerPlus;

public class MontageLayoutPanelMenu extends LockedItemMenu {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private MontageEditCommandMenu editmenu;

	public MontageLayoutPanelMenu(MontageLayoutGraphic c) {
		
		
		
		/**
		add(new ObjectAction<MontageLayoutGraphic>(c) {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (item.getBounds().width<500) {
					double factor=650.00/(item.getBounds().width+item.getBounds().x);
					
					double [] factors= new double []{ 300.0/200, 300.0/150, 300.0/100, 300.0/72, 300.0/50};
					for (int i=0; i<factors.length; i++) {
					    if (factor<factors[i]&&i>0)  {
					    	factor=factors[i-1];
					    	break;}
					}
					
					ArrayList<ZoomableGraphic> ii = item.getParentLayer().getAllGraphics();
					
					for(ZoomableGraphic xg: ii) {
						if (xg instanceof Scales) {
							Scales s=(Scales) xg;
							s.scaleAbout(new Point(0,0), factor);
						}
					}
				}
			}	
	}.createJMenuItem("Scale to Slide Size"));*/
		
	
		c.generateCurrentImageWrapper();
		  editmenu = new MontageEditCommandMenu(c.getPanelLayout());
	
		add(editmenu.getInclusiveList());
		
		super.setLockedItem(c);
		super.addLockedItemMenus();
		
		//add(createPanelSizeDefSubMenu(c));
		GraphicLayer par = c.getParentLayer();
		if (par instanceof HasUniquePopupMenu) {
			JPopupMenu jp = ((HasUniquePopupMenu) par).getMenuSupplier().getJPopup();
			if (jp instanceof SmartPopupJMenu) {
				JMenu menuadded = ((SmartPopupJMenu) jp).extractToMenu("Figure");
				add(menuadded, 0);
				
			}
		}
		
		add(new ObjectAction<MontageLayoutGraphic>(c) {
			@Override
			public void actionPerformed(ActionEvent arg0) {item.showOptionsDialog();}	
	}.createJMenuItem("Other Options"));
		
	//if (c.getParentLayer()instanceof FigureOrganizingLayerPane)	this.add(new FigureScalerMenu(c));
	}
	
	

	
	
	JMenu createPanelSizeDefSubMenu(PanelLayoutGraphic c) {
		JMenu psize = new JMenu("Panel Size Definers");
		
		
		AddPanelSizeDefiningItemMenu def = new AddPanelSizeDefiningItemMenu(c, c.getPanelSizeDefiningItems());
		def.setRemove(true);
		psize.add(new AddPanelSizeDefiningItemMenu(c, c.getLockedItems()));
		psize.add(def);
		
		return psize;
	}
	
	
	public void setUndoManager(UndoManagerPlus u) {
		super.setUndoManager(u);
		if (editmenu!=null) editmenu.setUndoManager(u);
	}
	
	
}
