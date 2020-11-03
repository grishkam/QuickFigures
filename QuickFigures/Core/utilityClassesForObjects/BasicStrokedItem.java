package utilityClassesForObjects;

import java.awt.BasicStroke;
import java.awt.Color;
import java.io.Serializable;

public class BasicStrokedItem implements StrokedItem , Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Color strokeColor=Color.white;
	Color fillColor=Color.white;
	boolean filled=false;
	
	protected float strokeWidth=1;
	float[] dash=new float[]{4,4};
	
	int end=BasicStroke.CAP_BUTT;
	int join=BasicStroke.JOIN_BEVEL;
	
	double miterLimit=1;
	


	public float[] getDashes() {return dash;}
	public void setDashes(float[] dash) {this.dash=dash;}
	
	public static void copyStrokeProps(StrokedItem recipient, StrokedItem source) {
		recipient.setStrokeColor(source.getStrokeColor());
		recipient.setStrokeJoin(source.getStrokeJoin());
		recipient.setStrokeCap(source.getStrokeCap());
		recipient.setStrokeWidth(source.getStrokeWidth());
		recipient.setDashes(source.getDashes());
		recipient.setMiterLimit(source.getMiterLimit());
		
	}
	
	public static void scaleStrokeProps(StrokedItem recipient, double mag) {
		recipient.setStrokeWidth( (float) (recipient.getStrokeWidth()*mag));
		scaleDashes(recipient, mag);
		
	recipient.setMiterLimit(recipient.getMiterLimit()*mag);
	}
	protected static void scaleDashes(StrokedItem recipient, double mag) {
		float[] d = recipient.getDashes();
		float[] d2 = new float[recipient.getDashes().length];
		for(int i=0; i<d.length; i++) {
			d2[i]=(float) (d[i]*mag);
		}
		recipient.setDashes(d2);
	}
	
	public void setMiterLimit(double miter) {
		 miterLimit=miter;
	}
	public double getMiterLimit() {
		return  miterLimit;
	}
	public void setStrokeWidth(float strokeWidth) {
		this.strokeWidth = strokeWidth;
	}
	@Override
	public Color getStrokeColor() {
		return strokeColor;
	}
	
	@Override
	public BasicStroke getStroke() {
		float width = getStrokeWidth();
		float limit = (float) getMiterLimit();
		float[] d = this.getDashes();
		if (limit<1) limit=1;
		if (width<0) width=0;
		if (d==null) d=new float[] {100000};
		return new BasicStroke(width, end, join, limit, d, 2);
	}
	
	@Override
	public void setStroke(BasicStroke stroke) {
		end=stroke.getEndCap();
		join=stroke.getLineJoin();
		dash=stroke.getDashArray();
		miterLimit=stroke.getMiterLimit();
		setStrokeWidth(stroke.getLineWidth());
	}
	
	@Override
	public float getStrokeWidth() {
		return strokeWidth;
	}

	@Override
	public void setStrokeColor(Color c) {
		strokeColor=c;
		
	}
	
	public int getStrokeJoin() {
		return join;
	}
	public int getStrokeCap(){
		return end;
	}
	
	public void setStrokeJoin(int j) {
		join=j;
	}
	public void setStrokeCap(int e){
		end=e;
	}

}
