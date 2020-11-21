package popupMenusForComplexObjects;

import java.awt.Color;
import java.awt.event.ActionEvent;

import javax.swing.JMenu;

import fLexibleUIKit.ObjectAction;
import graphicTools.ArrowGraphicTool;
import graphicTools.BarGraphicTool;
import graphicTools.CircleGraphicTool;
import graphicTools.RectGraphicTool;
import graphicTools.Text_GraphicTool;
import graphicalObjects.ImagePanelGraphic;
import graphicalObjects_BasicShapes.ComplexTextGraphic;
import graphicalObjects_FigureSpecific.FigureOrganizingLayerPane;
import graphicalObjects_FigureSpecific.MultichannelDisplayLayer;
import graphicalObjects_LayerTypes.GraphicLayer;
import menuUtil.SmartJMenu;
import multiChannelFigureUI.ChannelPanelEditingMenu;
import undo.CombinedEdit;
import undo.Edit;
import undo.UndoTakeLockedItem;

/**Menu for Image Panels. Displayed  when user right clicks on a panel*/
public class ImagePanelMenu extends LockedItemMenu {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ImagePanelGraphic imagePanel;

	
	public ImagePanelMenu(ImagePanelGraphic c) {
		this.imagePanel=c;
		
		/**If the panel is inside of a figure, adds a few submenus*/
		FigureOrganizingLayerPane folp = FigureOrganizingLayerPane.findFigureOrganizer(c);
		if (folp!=null) {
			
			JMenu channelMenu=new SmartJMenu("Channels"); 
			this.add(channelMenu);
			new ChannelPanelEditingMenu(c). addChannelRelevantMenuItems(channelMenu);
			
			JMenu extendFigure=new SmartJMenu("Figure");
			new FigureOrganizingSuplierForPopup(folp).addMenus(extendFigure);
			this.add(extendFigure);
			
		}
		
		
		/**If the panel is part of a MultichannelDisplayLayer, adds another submenu*/
		if (c.getParentLayer() instanceof MultichannelDisplayLayer) {
			MultichannelDisplayLayer m=(MultichannelDisplayLayer) c.getParentLayer();
			JMenu mc=new SmartJMenu("This Image");
			MultiChannelImageDisplayPopup everyMenu = new MultiChannelImageDisplayPopup(m, m.getPanelList(), imagePanel);
			everyMenu.addMenus(mc);
			this.add(mc);
		}
		
	
		/**Creates a menu option for adding a scale bar*/
		add(new ObjectAction<ImagePanelGraphic>(c) {
			public void actionPerformed(ActionEvent arg0) {addScaleBar();}	
	}.createJMenuItem("Add Scale Bar"));
		
		/**Creates a menu option for add a label*/
		add(new ObjectAction<ImagePanelGraphic>(c) {
			public void actionPerformed(ActionEvent arg0) {addText();}	
	}.createJMenuItem("Add Text"));
		
		
		
	addShapeSubmenu();
	
	add(new ObjectAction<ImagePanelGraphic>(c) {
		@Override
		public void actionPerformed(ActionEvent arg0) {item.showCroppingDialog();}	
}.createJMenuItem("Crop Only This Panel"));


super.setLockedItem(c);
super.addLockedItemMenus();
	}



	/**Creates a submenu for adding shapes, user should be able to add*/
	public void addShapeSubmenu() {
		SmartJMenu s = new SmartJMenu("Add Shape ");
		s.add(new PanelShapeAdder(new ArrowGraphicTool(), imagePanel, imagePanel.getParentLayer()));
		s.add(new PanelShapeAdder(new RectGraphicTool(), imagePanel, imagePanel.getParentLayer()));
		s.add(new PanelShapeAdder(new CircleGraphicTool(CircleGraphicTool.SIMPLE_CIRCLE), imagePanel, imagePanel.getParentLayer()));
		this.add(s);
	}



	protected void addText() {
		CombinedEdit undo=new CombinedEdit();
		String name=null;
	
			name="Panel";
		
		ComplexTextGraphic text = new ComplexTextGraphic(name);
		text.setTextColor(Color.white);
		Text_GraphicTool.lockAndSnap(imagePanel, text, (int)imagePanel.getLocationUpperLeft().getX(), (int) imagePanel.getLocationUpperLeft().getY());
		
		undo.addEditToList(new UndoTakeLockedItem(imagePanel, text, false));
		GraphicLayer parentLayer = imagePanel.getParentLayer();
		undo.addEditToList(Edit.addItem(parentLayer, text));
		
		
		imagePanel.getUndoManager().addEdit(undo);
	}



	protected void addScaleBar() {
		CombinedEdit undo = new CombinedEdit();
		BarGraphicTool.getCurrentBarTool().addBarGraphic(imagePanel, imagePanel.getParentLayer(), undo, (int)imagePanel.getBounds().getMaxX(),(int)imagePanel.getBounds().getMaxY());
		imagePanel.getUndoManager().addEdit(undo);
	}
	
}
