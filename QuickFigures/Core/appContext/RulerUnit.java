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
 
 * 
 */
package appContext;

/**
 This stores information about the units that may be used for rulers
 */
public enum RulerUnit {
	
	
		
	DEFAULT_INCH("inch", "Inch", " in ", 4, 20, 1, 14),
	DEFAULT_CM("cm", "Centimeter",  " cm ", 2, 20, 2.54, 14);
		
	public static String key="RulerUnit";
	
	String label="";
	
	private int fractionMark=4;//rulers will include marks for fraction of units
	private int maxMarks;//limit to how many inches are actually drawn to rulers
	private double conversionFactor=1;//conversion factor from inches

	private String shortName;

	private String unitName;

	private int fontSize;

	RulerUnit(String shortName, String unitName,  String label, int fMark, int maxMark, double conversion, int fontSize) {
		 this.shortName=shortName;
		 this.unitName=unitName;
		
		 this.label=label;
		 this.fractionMark=fMark;
		 this.maxMarks=maxMark;
		 this.conversionFactor=conversion;
		 this.fontSize=fontSize;
	 }
	
	
	
	
	/**returns how many units are in one inch,cm or other unit*/
	public double getUnitSize() {return ImageDPIHandler.getInchDefinition()/conversionFactor;}
	
	/**returns the label that is drawn on the rulers*/
	public String getLabel() {return label;}
	
	/**returns what fraction of a unit, the minor tick marks are drawn at*/
	public int getFractionMark() {return fractionMark;}
	
	/**returns what fraction of a unit, the minor tick marks are drawn at*/
	public int getMaxMark() {return (int) (maxMarks* conversionFactor);}
	
	
	/**the name of the unit*/
	public String getName() {return unitName;}
	
	public RulerUnit getInchVersion() {
		return DEFAULT_INCH;
	}
	
	public RulerUnit getCMVersion() {
		return DEFAULT_CM;
		
	}




	/**
	 * returns the fontsize for the unit
	 * @return
	 */
	public int getFontSize() {
		return fontSize;
	}


	/**when given a string, returns the matching the ruler unit*/
	public static RulerUnit getUnirByName(String name) {
		for(RulerUnit r: RulerUnit.values()) {
			if(r.getShortName().equals(name))
				return r;
		}
		return DEFAULT_INCH;
	}




	public String getShortName() {
		return shortName;
	}

	
	
	

}
