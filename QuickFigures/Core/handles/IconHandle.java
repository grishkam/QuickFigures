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
package handles;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.Icon;

import applicationAdapters.CanvasMouseEvent;
import icons.MiniToolBarIcon;

/**A handle that displays an icon*/
public class IconHandle extends SmartHandle {
	
	public static final int _DEFAULT_MAX_SIZE = MiniToolBarIcon.ICON_SIZE;
	protected int subtract = 0;
	
	/**Some icons are drawn shifted slightly*/
	protected int xShift=0;
	protected int yShift=0;
	
	/**The max size for the handle. Even if icons are larger, the handle will be this size*/
	public int maxWidth=_DEFAULT_MAX_SIZE;
	int maxHeight=_DEFAULT_MAX_SIZE;

	public IconHandle(Icon i, Point2D p) {
		
		this.setCordinateLocation(p);
		this.icon=i;
	}
	
	public void drawIcon(Graphics2D graphics, Point2D pt) {
		if (getIcon()!=null) {
			getIcon().paintIcon(null, graphics, getxShift()+(int)pt.getX()-this.getIcon().getIconWidth()/2, getyShift()+(int)pt.getY()-getIcon().getIconHeight()/2);
			
		};
	}
	

	protected Shape createStandardHandleShape(Point2D pt) {
		double widthr =getDrawnHandleWidth();
		double heightr = icon.getIconHeight()-subtract;
		
		if(heightr>this.maxHeight)heightr =this.maxHeight;
		double xr = pt.getX()-widthr/2;
		double yr = pt.getY()-heightr/2;
		
		return new Rectangle2D.Double(xr,yr, widthr, heightr);
	}

	public double getDrawnHandleWidth() {
		double widthr= icon.getIconWidth()-subtract;
		if(widthr>this.maxWidth)widthr=this.maxWidth;
		return (int) widthr;
	}
	

	public int getxShift() {
		return xShift;
	}
	public int getyShift() {
		return yShift;
	}

	public void setyShift(int yShift) {
		this.yShift = yShift;
	}
	public void setxShift(int xShift) {
		this.xShift = xShift;
	}



	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public void showPopupMenu(CanvasMouseEvent canvasMouseEventWrapper) {
	}

}
