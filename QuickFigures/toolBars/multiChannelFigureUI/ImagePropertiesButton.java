package multiChannelFigureUI;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Arc2D;
import java.awt.geom.Arc2D.Double;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.Icon;

import channelMerging.MultiChannelWrapper;
import graphicalObjects.ImagePanelGraphic;
import graphicalObjects_FigureSpecific.MultichannelDisplayLayer;
import iconGraphicalObjects.CropIconGraphic;
import iconGraphicalObjects.IconUtil;
import logging.IssueLog;
import popupMenusForComplexObjects.FigureOrganizingSuplierForPopup;
import selectedItemMenus.BasicMultiSelectionOperator;
import standardDialog.GraphicObjectDisplayBasic;
import undo.ChannelDisplayUndo;
import undo.CompoundEdit2;
import utilityClassesForObjects.LocatedObject2D;
import utilityClassesForObjects.RectangleEdges;

public class ImagePropertiesButton extends BasicMultiSelectionOperator {

	

	public static final int COLOR_MODE = 9, CROP_IMAGE = 3;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ImagePanelGraphic firstImage;
	int brightcontrast=0;
	
	public ImagePropertiesButton() {}
	public ImagePropertiesButton(ImagePanelGraphic i, int bc) {
		firstImage=i;
		brightcontrast=bc;
	}

	@Override
	public String getMenuCommand() {
		if(brightcontrast==WindowLevelDialog.ALL) return "Set Display Range";
		if(brightcontrast==WindowLevelDialog.WINDOW_LEVEL) return "Set Window/Level";
		if(brightcontrast==WindowLevelDialog.MIN_MAX) return "Set Brightness/Contrast";
		if(brightcontrast==CROP_IMAGE) return "Recrop Image";
		if(brightcontrast==8) return "PPI";
		if(brightcontrast==9) return "Change Color Modes";
		
		return "Set Window/Level";
	}

	@Override
	public void run() {
		ArrayList<LocatedObject2D> items = super.getAllObjects();
		CompoundEdit2 undo=null;
		
		
		firstImage=null;
		
		ArrayList<MultichannelDisplayLayer> foundDisplays=new ArrayList<MultichannelDisplayLayer>();
		ArrayList<MultiChannelWrapper> foundImages=new ArrayList<MultiChannelWrapper>();
		
		 
		for(LocatedObject2D i: items) {
			if(i instanceof ImagePanelGraphic)  {
				if(firstImage==null)firstImage=(ImagePanelGraphic) i;
				MultichannelDisplayLayer nextone = new ChannelSwapperToolBit2((ImagePanelGraphic) i).getPrincipalDisplay();
				
				if (nextone!=null&&!foundDisplays.contains(nextone))
					{
					foundDisplays.add(nextone);
					foundImages.add(nextone.getMultichanalWrapper());
					}
			}
				
		}
		
		ChannelSwapperToolBit2 context = new ChannelSwapperToolBit2(firstImage);
		context.workOn=0;
		context.extraWrappers= foundImages;
		context.extraDisplays=foundDisplays;
		//if (this.brightcontrast<2) undo = ChannelDisplayUndo.createMany(foundImages, context);//
	
		if (this.brightcontrast<3)
			context.showDisplayRangeDialog(brightcontrast);
	
			
		if(this.brightcontrast==3) {
			if(foundDisplays.size()<1) return;
			foundDisplays.remove(context.getPrincipalDisplay());
		
			undo = FigureOrganizingSuplierForPopup.recropManyImages(context.getPrincipalDisplay(),foundDisplays);;
		}
		
		if(this.brightcontrast==9) {
			context.workOn=1;
			undo=context.changeColorModes();
		}
		
		if(undo!=null&&this.getUndoManager()!=null) this.getUndoManager().addEdit(undo);
	}
	
	public Icon getIcon() {
		if(brightcontrast==3) return new GraphicObjectDisplayBasic<CropIconGraphic>(new 	CropIconGraphic());
		if(brightcontrast==COLOR_MODE) return new ColorIcon();
		return IconUtil.createBrightnessIcon(0, Color.black);
	}
	
	public class ColorIcon implements Icon {
		
		

		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
			java.awt.geom.Rectangle2D.Double ra = new Rectangle2D.Double(x+3, y+3, 18, 18);
			
			GradientPaint gp = new GradientPaint(RectangleEdges.getLocation(RectangleEdges.TOP, ra), Color.white, RectangleEdges.getLocation(RectangleEdges.BOTTOM, ra), Color.black);
			GradientPaint gp2 = gp;
			Color iColor = this.getImageColor();
		if (iColor!=null) gp2=new GradientPaint(RectangleEdges.getLocation(RectangleEdges.TOP, ra), iColor, RectangleEdges.getLocation(RectangleEdges.BOTTOM, ra), Color.black);
			
			Double a = new Arc2D.Double(ra, 90, 180, Arc2D.CHORD);
		Double a2 = new Arc2D.Double(ra, -90, 180, Arc2D.CHORD);
			
		
			if(g instanceof Graphics2D) {
			Graphics2D g2=(Graphics2D) g;
			g2.setPaint(gp);
			g2.fill(a);
			g2.setPaint(gp2);
			g2.fill(a2);
			g2.setStroke(new BasicStroke());
			g2.setColor(Color.red.darker());
			if(this.getImageColorMode()) {g2.draw(a);} else {g2.draw(a2);}
		}
		}

		@Override
		public int getIconWidth() {
			// TODO Auto-generated method stub
			return 25;
		}

		@Override
		public int getIconHeight() {
			// TODO Auto-generated method stub
			return 25;
		}
		
		public Color getImageColor() {
			try {
				ChannelSwapperToolBit2 cc = new ChannelSwapperToolBit2(firstImage);
				
				return cc.getChannelEntryList().get(0).getColor();
			} catch (Exception e) {
			}
			
			return Color.white;
		}
		
		public boolean getImageColorMode() {
			
			try {
				ChannelSwapperToolBit2 cc = new ChannelSwapperToolBit2(firstImage);
				return cc.getPressedPanelManager().getPanelList().getChannelUseInstructions().ChannelsInGrayScale==1;
			} catch (Exception e) {
				
			}
		return false;
		}

	}

}
