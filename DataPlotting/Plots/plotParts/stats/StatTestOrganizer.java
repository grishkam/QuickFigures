/**
 * Author: Greg Mazo
 * Date Created: Nov 12, 2021
 * Date Modified: Nov 12, 2021
 * Version: 2021.2
 * 
 */
package plotParts.stats;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.JMenu;

import dataSeries.DataSeries;
import fLexibleUIKit.MenuItemExecuter;
import fLexibleUIKit.MenuItemMethod;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayerPane;
import graphicalObjects_Shapes.PathGraphic;
import graphicalObjects_SpecialObjects.ComplexTextGraphic;
import graphicalObjects_SpecialObjects.TextGraphic;
import layout.BasicObjectListHandler;
import locatedObject.LocatedObject2D;
import locatedObject.ObjectContainer;
import locatedObject.RectangleEdges;
import logging.IssueLog;
import plotParts.Core.PlotOrientation;
import plotParts.DataShowingParts.DataShowingShape;
import popupMenusForComplexObjects.DonatesMenu;
import storedValueDialog.ReflectingFieldSettingDialog;
import storedValueDialog.UserChoiceField;
import undo.FieldCopyUndo;
import utilityClasses1.ArraySorter;

/**
 An object with methods related to creating graphics to show a statistical test
 */
public class StatTestOrganizer extends GraphicLayerPane implements Serializable, DonatesMenu {

	
	/**
	 * @param name
	 */
	public StatTestOrganizer() {
		super("test");
		// TODO Auto-generated constructor stub
	}


	@UserChoiceField(optionsForUser = { "p-Vale<x", "Stars", "Exact" })
	public int markType=StatTestShower .LESS_THAN_MARK;


	public static final int LINK_WITH_LINE=0, NO_LINK=1;
	@UserChoiceField(optionsForUser = { "Connection Line", "Put significance above databar" })
	public int linkType=LINK_WITH_LINE;

	@UserChoiceField(optionsForUser = { "Assume Unequal Variances", "Assume Equal Variances", "Paired T-Test" })
	public int tTestType=StatTestShower.NORMAL_T_TEST;


	@UserChoiceField(optionsForUser = { "Two-Tailed", "One-Tailed" })
	public int numberTails=StatTestShower.TW0_TAIL;


	/**the text that displays the result*/
	private ComplexTextGraphic textResult;

	private ConnectorGraphic connector;

	/**the two data series*/
	public DataSeries data1, data2;


	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**When given two data shapes and where they were clicked on, creates a linker graphic for the shapes*/
	public ConnectorGraphic createLinkingLineForShapes(DataShowingShape pressShape2, DataShowingShape dragShape2, double pressX, double pressY, double dragX, double dragY) {
		if (pressShape2==null||dragShape2==null) return null;
		ConnectorGraphic p3 = prepareForVerticalBar(pressShape2, dragShape2, pressX, pressY, dragX, dragY);
		if(pressShape2.getOrientation()==PlotOrientation.BARS_HORIZONTAL) {
			 p3 = prepareForHorizontalBar(pressShape2, dragShape2, pressX, pressY, dragX, dragY);
		}
		
		/**moves the shape a bit further to from the shapes*/
		if(p3.horizontal)
			p3 .moveLocation(20, 0);
		else
			p3 .moveLocation(0, -20);
		;
		 p3.setStrokeColor(Color.black);
		 p3.setStrokeWidth(1);
		return p3;
	}


	/**prepares a connector for comparison of vertical bars. This will be placed above the bars
	 * @param pressShape2
	 * @param dragShape2
	 * @param pressX
	 * @param pressY
	 * @param dragX
	 * @param dragY
	 * @return
	 */
	public ConnectorGraphic prepareForVerticalBar(DataShowingShape pressShape2, DataShowingShape dragShape2,
			double pressX, double pressY, double dragX, double dragY) {
		Point2D.Double pt0 = highestPointInDataShape(pressShape2, pressX, pressY) ;
		Point2D.Double pt1 = highestPointInDataShape(pressShape2, pressX, pressY) ;//end of horizontal bar
		Point2D.Double pt2 = highestPointInDataShape(dragShape2, dragX, dragY) ;//end of horixontal bar
		Point2D.Double pt3 = highestPointInDataShape(dragShape2, dragX, dragY) ;
		if (pt1.y<pt2.y) pt2.y=pt1.y; else pt1.y=pt2.y; 
		
		PathGraphic p1=new PathGraphic(pt1, pt2);
		
		while (doesOverLapDataShapes(p1,  pressShape2.getTopLevelContainer())) {
			p1.getPoints().applyAffine(AffineTransform.getTranslateInstance(0, -5));
			p1.updatePathFromPoints();
			pt1.y-=5;
			pt2.y-=5;
		}
		
		//p1.moveLocation(0, -10);
		p1=new PathGraphic(pt0, pt1, pt2, pt3);
		
		
		Double pmid = new Point2D.Double((pt1.getX()+pt2.getX())/2, pt1.getY());
		ConnectorGraphic p3 = new ConnectorGraphic(false,pt0, pmid, pt3);
		return p3;
	}
	
	/**prepares a connector for comparison of vertical bars
	 * @param pressShape2
	 * @param dragShape2
	 * @param pressX
	 * @param pressY
	 * @param dragX
	 * @param dragY
	 * @return
	 */
	public ConnectorGraphic prepareForHorizontalBar(DataShowingShape pressShape2, DataShowingShape dragShape2,
			double pressX, double pressY, double dragX, double dragY) {
		Point2D.Double pt0 = rightmostPointInDataShape(pressShape2, pressX, pressY) ;
		Point2D.Double pt1 = rightmostPointInDataShape(pressShape2, pressX, pressY) ;//end of bar
		Point2D.Double pt2 = rightmostPointInDataShape(dragShape2, dragX, dragY) ;//end of bar
		Point2D.Double pt3 = rightmostPointInDataShape(dragShape2, dragX, dragY) ;
		if (pt1.x>pt2.x) pt2.x=pt1.x; else pt1.x=pt2.x; 
		
		
		PathGraphic p1=new PathGraphic(pt1, pt2);
		
		/**moves line to the right until it no longer overlaps any data shape*/
		while (doesOverLapDataShapes(p1, pressShape2.getTopLevelContainer())) {
			p1.getPoints().applyAffine(AffineTransform.getTranslateInstance(5, 0));
			p1.updatePathFromPoints();
			pt1.x+=5;
			pt2.x+=5;
		}
		
		//p1.moveLocation(0, -10);
		p1=new PathGraphic(pt0, pt1, pt2, pt3);
		
		
		Double pmid = new Point2D.Double( pt1.getX(), (pt1.getY()+pt2.getY())/2);
		ConnectorGraphic p3 = new ConnectorGraphic( true, pt0, pmid, pt3);
		
		return p3;
	}

/**returns true if the horizontal line overlaps with data shapes*/
	protected boolean doesOverLapDataShapes(PathGraphic p1, ObjectContainer image) {
		Rectangle r = p1.getBounds();
		r.height=2;
		ArrayList<LocatedObject2D> items = new BasicObjectListHandler().getOverlapOverlaypingOrContainedItems(r, image);
		items=new ArraySorter<LocatedObject2D>().getThoseOfClass(items, DataShowingShape.class);
		boolean overlapsDataShapes=items.size()>0;
		
		return overlapsDataShapes;
	}

	/**returns the point in the shape with the lowest y value*/
	public Point2D.Double highestPointInDataShape(DataShowingShape pressShape2, double x, double y) {
		/**if (pressShape2 instanceof DataBarShape) {
			GraphicLayer p = pressShape2.getParentLayer();
			if (p instanceof GenericDataSeriesGroup) {
				if (null!=((GenericDataSeriesGroup) p).getErrorBar())
				return highestPointInDataShape(((GenericDataSeriesGroup) p).getErrorBar(), x, y);
			}
		}*/
		
		Rectangle bounds = pressShape2.getBounds();
		DataSeries part = pressShape2.getPartialSeriesDrawnAtLocation(x, y);
		if (part!=null) bounds=pressShape2.getPartialShapeAtLocation(x, y).getBounds();
		
		return new Point2D.Double(bounds.getCenterX(),bounds.getMinY());
	
	}
	
	/**returns the point in the shape with the highest value*/
	private Point2D.Double rightmostPointInDataShape(DataShowingShape pressShape2, double x, double y) {
		/**if (pressShape2 instanceof DataBarShape) {
			GraphicLayer p = pressShape2.getParentLayer();
			if (p instanceof GenericDataSeriesGroup) {
				if (null!=((GenericDataSeriesGroup) p).getErrorBar())
				return highestPointInDataShape(((GenericDataSeriesGroup) p).getErrorBar(), x, y);
			}
		}*/
		
		Rectangle bounds = pressShape2.getBounds();
		DataSeries part = pressShape2.getPartialSeriesDrawnAtLocation(x, y);
		if (part!=null) bounds=pressShape2.getPartialShapeAtLocation(x, y).getBounds();
		
		return new Point2D.Double(bounds.getMaxX(),bounds.getCenterY());
	
	}
	
	
	/**updates thep value for the current parameters of the test*/
	public void updateTest() {
		ComplexTextGraphic t = this.createTextForTest(data1, data2, connector, null);
		IssueLog.log("Updating test to "+t.toString());
		 this.textResult.setParagraph(t.getParagraph().copy());
		 textResult.setupSegOutlines();
		 IssueLog.log("Updated test to "+textResult.toString()+"   willshow="+this.hasItem(textResult));
		 textResult.updateDisplay();
		 
	}
	
	
	/**creates a text item that displays the results of the test*/
	public ComplexTextGraphic createTextForTest(DataSeries data1, DataSeries data2, ConnectorGraphic preliminaryPath, DataShowingShape dragShape) {
		return createTextForTest(data1, data2, preliminaryPath, this.useLinkingLine(), dragShape, new StatTestShower(tTestType, numberTails, markType));
	}
	
	
	/**creates a text item that displays the results of the test*/
	public ComplexTextGraphic createTextForTest(DataSeries data1, DataSeries data2, ConnectorGraphic preliminaryPath, boolean usesLinkingLine, DataShowingShape dragShape, StatTestShower test) {
		
		
		
		if (data1.getIncludedValues().length()<3) return null;
		if (data2.getIncludedValues().length()<3) return null;
		
		double pValue;
		try {
			pValue = test.calculatePValue(data1, data2);
			
		} catch (Exception e) {
			return null;
		}
		
		
		ComplexTextGraphic text = test.createTextForPValue(pValue);
		double ty=0; 
		double tx=0;
		if(preliminaryPath!=null) {
			ty = preliminaryPath.getBounds().getMinY();
			tx = preliminaryPath.getBounds().getCenterX();
		}
		if (!usesLinkingLine && dragShape!=null) {
			Point2D.Double h =highestPointInDataShape(dragShape, dragShape.getBounds().getCenterX(), dragShape.getBounds().getCenterY());
			ty=h.getY();
			tx=h.getX();
		}
		text.setLocationType(RectangleEdges.BOTTOM);
		text.setLocation(tx, ty);
		
		
		
		return text;
	}
	
	public boolean useLinkingLine() {
		return linkType==LINK_WITH_LINE;
	}
	
	
	public StatTestOrganizer copy() {
		StatTestOrganizer output = new StatTestOrganizer();
		 FieldCopyUndo.copyFeilds(this, output);
		 return output;
	}


	/**
	 * @param d1
	 * @param d2
	 */
	public void setDataSeries(DataSeries d1, DataSeries d2) {
		this.data1=d1;
		this.data2=d2;
		if(data1==null || data2==null) {
			IssueLog.log("invalid data input");
		}
	}


	/**
	 * @param preliminaryPath
	 */
	public void setLinker(ConnectorGraphic preliminaryPath) {
		this.connector=preliminaryPath;
		addItemToLayer(preliminaryPath);
	}
	
	
	@Override
	public void showOptionsDialog() {
		options();
	}


	/**
	 displays the options dialog
	 */
	@MenuItemMethod(menuActionCommand = "Update stat test", menuText = "test properties")
	public void options() {
		ReflectingFieldSettingDialog dialog = new ReflectingFieldSettingDialog(this, "markType", "linkType", "tTestType", "numberTails");
		dialog.setModal(true);
				dialog.showDialog();
		this.updateTest();
	}


	/**
	 * @param text
	 */
	public void setTheText(ComplexTextGraphic text) {
		this.textResult=text;
		this.addItemToLayer(text);
	}


	/**returns a menu related to statistical tests to the */
	@Override
	public JMenu getDonatedMenuFor(Object requestor) {
		if(requestor instanceof ZoomableGraphic && this.hasItem((ZoomableGraphic) requestor)) {
			JMenu jMenu = new MenuItemExecuter(this).getJMenu();
			jMenu.setText("statistics");
			return jMenu;
		}
		return null;
	}
	
}
