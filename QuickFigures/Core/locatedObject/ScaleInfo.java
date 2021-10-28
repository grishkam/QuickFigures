/*******************************************************************************
 * Copyright (c) 2021 Gregory Mazo
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
/**
 * Author: Greg Mazo
 * Date Modified: Jan 4, 2021
 * Version: 2021.2
 */
package locatedObject;


import java.awt.geom.Dimension2D;
import java.io.Serializable;

import layout.RetrievableOption;

/**Stores information regarding the
number of pixels that corresponds to a physical unit distance*/
public class ScaleInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@RetrievableOption(key="units",  label="units")
	private String units="pixels";
	
	@RetrievableOption(key="pixHeight",  label="The Pixel Height")
	private double pixelHeight=1;
	
	@RetrievableOption(key="pixWidth",  label="The Pixel Width")
	private double pixelWidth=1;
	
	@RetrievableOption(key="pixDepth",  label="The Pixel Depth")
	private double pixelDepth=1;
	
	public ScaleInfo() {}
	
	/**changes units from 'micron' to 'Âµm'*/
	protected void correctMicron() {
		if (units.equals("micron")) units="µm";
	}
	
	/**
	 * @param unitname the units used
	 * @param pixeldim the pixel size*/
	public ScaleInfo(String unitname, double pixeldim) {
		setUnits(unitname);
		setPixelWidth(pixeldim);
		setPixelHeight(pixeldim);
	}
	
	public ScaleInfo(String unitname, double pixeldimw, double pixeldimh) {
		setUnits(unitname);
		setPixelWidth(pixeldimw);
		setPixelHeight(pixeldimh);
	}
	
	/**returns a summary string*/
	public String toString() {
		return "units = "+this.getUnits() +"  pixelWdith = "+this.getPixelWidth()+"  pixelHeight = "+this.getPixelHeight();
	}
	
	/**Creates a copy*/
	public ScaleInfo copy() {
		ScaleInfo output = new ScaleInfo(getUnits(), getPixelWidth(), getPixelHeight()) ;
		output.setPixelDepth(pixelDepth);
		return output;
	}
	
	
	
	/**scales the pixel dimensions to match a scale applied to an image*/
	public void scaleXY(double mag) {
		setPixelHeight(getPixelHeight() / mag);
		setPixelWidth(getPixelWidth() / mag);
	}
	
	/**given the dimensions of the image in pixels this scales them*/
	public double[] convertPixelsToUnits(Dimension2D d) {
		double width=d.getWidth()*this.getPixelWidth();
		double height=d.getHeight()*this.getPixelHeight();
		return new double[] {width, height};
	}
	
	/**Creates a scaled copy*/
	public ScaleInfo getScaledCopyXY(double mag) {
		ScaleInfo outut =copy();
		outut.scaleXY(mag);
		return outut;
	}

	public double getPixelHeight() {
		return pixelHeight;
	}

	public void setPixelHeight(double pixelHeight) {
		this.pixelHeight = pixelHeight;
	}

	public double getPixelWidth() {
		return pixelWidth;
	}

	public void setPixelWidth(double pixelWidth) {
		this.pixelWidth = pixelWidth;
	}

	public double getPixelDepth() {
		return pixelDepth;
	}

	public void setPixelDepth(double pixelDepth) {
		this.pixelDepth = pixelDepth;
	}

	public String getUnits() {
		return units;
	}

	public void setUnits(String units) {
		this.units = units;
	}

}
