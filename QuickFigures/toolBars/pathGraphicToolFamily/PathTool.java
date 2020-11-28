package pathGraphicToolFamily;

import java.awt.Point;
import javax.swing.Icon;

import externalToolBar.IconWrappingToolIcon;
import graphicTools.GraphicTool;
import graphicalObjectHandles.SmartHandle;
import graphicalObjects_BasicShapes.PathGraphic;
import graphicalObjects_LayerTypes.GraphicGroup;
import standardDialog.GraphicDisplayComponent;

public class PathTool extends GraphicTool{
	protected PathGraphic pathGraphic;

	boolean defaultCurved=true;

	//PathGraphic model= new PathGraphic(new Point(0,0)); {model.setStrokeColor(Color.green); model.setStrokeWidth(2);}
	SmartHandle handleSmart ;

	private int handleMode=PathGraphic.ANCHOR_HANDLE_ONLY_MODE;;
	{createIconSet("icons2/DrawCurveLineIcon.jpg","icons2/LineIconPressed.jpg","icons2/LineIcon.jpg");
	this.realtimeshow=false;
	}
	
	public PathTool() {
		this(false,false);
	}
	
	public PathTool(boolean curve, boolean symetricCurve) {
		defaultCurved=curve;
		this.set= IconWrappingToolIcon.createIconSet(getDefaultIcon()) ;
		if (!curve) this.handleMode=PathGraphic.ANCHOR_HANDLE_ONLY_MODE;
		else {
			handleMode=PathGraphic.CURVE_CONTROL_HANDLES_LINKED;
			if (symetricCurve) {handleMode=PathGraphic.CURVE_CONTROL_SYMETRIC_MODE;}
		}
		
	}
	
	
	public void mousePressed() {
		retorePathGraphicToNoral() ;
		super.mousePressed();
		;
		
		if (this.getSelectedObject()!=pathGraphic) {
			retorePathGraphicToNoral() ;
		}
		
		if (this.getSelectedObject()instanceof PathGraphic) {
			this.pathGraphic=(PathGraphic) getSelectedObject();
			
			//handleSmart = pathGraphic.getSmartHandleList().getHandleNumber(this.getPressedHandle());
		}
		
		onPathPress();
		}
	
	public int getMode() {
		return this.handleMode;
		
	}
	
	public void onPathPress() {
		if(pathGraphic==null) return;
		pathGraphic.setHandleMode(getMode()) ;
	if (shiftDown()&&getMode()>0) {
		pathGraphic.setHandleMode(getMode()-1);
			
		
	}
		
	}
	
	
	public void mouseExited() {
		retorePathGraphicToNoral() ;
		super.mouseReleased();
		
	}
	

	void retorePathGraphicToNoral() {
		if (pathGraphic!=null) {
			pathGraphic.setHandleMode(PathGraphic.THREE_HANDLE_MODE);
			//pathGraphic.setHandleMode(PathGraphic.allSelectedHandleMode);
		}
	}
	
	@Override
	public void mouseDragged() {
		super.mouseDragged();
		
		
	}
	
	@Override
	public String getToolName() {
		if (defaultCurved&&getMode()==PathGraphic.CURVE_CONTROL_SYMETRIC_MODE) {return "Adjust Curvature Symetric Tool";}
		if (defaultCurved) {return "Adjust Curvature Tool";}
		return "Move Anchor Point Tool";
	}
	
	public Icon getDefaultIcon() {
		return new GraphicDisplayComponent(createIcon() );
		//return createImageIcon();
	}
	
	GraphicGroup createIcon() {
		GraphicGroup out = new GraphicGroup(); ;//RectangularGraphic.blankRect(new Rectangle(0,0,14,12), Color.BLACK);//ArrowGraphic.createDefaltOutlineArrow(this.getFillColor(), this.getStrokeColor());
		/**	Point2D p1=new Point2D.Double(10,9);
			Point2D p2=new Point2D.Double(10, 11);
			Point2D p3=new Point2D.Double(9, 10);
			Point2D p4=new Point2D.Double(11,10);*/
			PathGraphic p = new PathGraphic(new Point(0,0));
			p.select();
			p.getPoints().get(0).setAnchorPoint(new Point(9, 9));
			if (defaultCurved) {
				
				p.getPoints().get(0).setCurveControl1(new Point(1, 8));
				p.getPoints().get(0).setCurveControl2(new Point(16, 8));
				p.setHandleMode(PathGraphic.THREE_HANDLE_MODE);
			}
		/**	RectangularGraphic rect1 = RectangularGraphic.blankRect(new Rectangle(6,6,4,4), Color.white) ;
			rect1.setFillColor(Color.white);
			rect1.setStrokeWidth(2);
			rect1.setStrokeColor(Color.black);*/
			
			out.getTheLayer().add(p);
		
			
		;
		//out.setAngle(this.getAngle());
		return out;
	}	
	
	@Override
	public String getToolSubMenuName() {
		return "Edit Points";
	}
	
	
}
