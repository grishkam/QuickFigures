package popupMenusForComplexObjects;

import java.awt.Color;
import java.awt.event.ActionEvent;

import javax.swing.JMenu;

import fLexibleUIKit.ObjectAction;
import graphicTools.BarGraphicTool;
import graphicTools.Text_GraphicTool;
import graphicalObjects.ImagePanelGraphic;
import graphicalObjects_BasicShapes.ComplexTextGraphic;
import graphicalObjects_FigureSpecific.FigureOrganizingLayerPane;
import graphicalObjects_FigureSpecific.MultichannelDisplayLayer;
import graphicalObjects_LayerTypes.GraphicLayer;
import iconGraphicalObjects.CropIconGraphic;
import menuUtil.SmartJMenu;
import multiChannelFigureUI.ChannelSwapperToolBit2;
import standardDialog.GraphicObjectDisplayBasic;
import undo.CompoundEdit2;
import undo.Edit;
import undo.UndoAddItem;
import undo.UndoTakeLockedItem;

public class ImagePanelMenu extends LockedItemMenu {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ImagePanelGraphic imagePanel;


	
	public ImagePanelMenu(ImagePanelGraphic c) {
		this.imagePanel=c;
		FigureOrganizingLayerPane folp = FigureOrganizingLayerPane.findFigureOrganizer(c);
		if (folp!=null) {
			
			JMenu channelMenu=new SmartJMenu("Channels"); 
			this.add(channelMenu);
			new ChannelSwapperToolBit2(c). addChannelRelevantMenuItems(channelMenu);
			
			JMenu extendFigure=new SmartJMenu("Figure");
			new FigureOrganizingSuplierForPopup(folp).addMenus(extendFigure);
			this.add(extendFigure);
			
		}
		
		
		
		if (c.getParentLayer() instanceof MultichannelDisplayLayer) {
			MultichannelDisplayLayer m=(MultichannelDisplayLayer) c.getParentLayer();
			JMenu mc=new SmartJMenu("This Image");
			MultiChannelImageDisplayPopup everyMenu = new MultiChannelImageDisplayPopup(m, m.getStack(), imagePanel);
			everyMenu.addMenus(mc);
			
			this.add(mc);
			
		}
		
	
		
		add(new ObjectAction<ImagePanelGraphic>(c) {
			public void actionPerformed(ActionEvent arg0) {addScaleBar();}	
	}.createJMenuItem("Add Scale Bar"));
		
		add(new ObjectAction<ImagePanelGraphic>(c) {
			public void actionPerformed(ActionEvent arg0) {addText();}	
	}.createJMenuItem("Add Text"));
		
		add(new ObjectAction<ImagePanelGraphic>(c) {
			@Override
			public void actionPerformed(ActionEvent arg0) {item.showCroppingDialog();}	
	}.createJMenuItem("Crop Only This Panel"));

/**
add(new ObjectAction<ImagePanelGraphic>(c) {
	@Override
	public void actionPerformed(ActionEvent arg0) {item.createInset();item.updateDisplay();}	
	}.createJMenuItem("Create Inset"));
*/

super.setLockedItem(c);
super.addLockedItemMenus();
	}



	protected void addText() {
		CompoundEdit2 undo=new CompoundEdit2();
		String name=null;
		//name= imagePanel.getName();
		//if(name.length()>15) name=name.substring(0, 14);
		//if(name.length()==0) 
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
		CompoundEdit2 undo = new CompoundEdit2();
		BarGraphicTool.getCurrentBarTool().addBarGraphic(imagePanel, imagePanel.getParentLayer(), undo, (int)imagePanel.getBounds().getMaxX(),(int)imagePanel.getBounds().getMaxY());
		imagePanel.getUndoManager().addEdit(undo);
	}
	
}
