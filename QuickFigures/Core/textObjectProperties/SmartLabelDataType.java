/**
 * Author: Greg Mazo
 * Date Created: Nov 3, 2021
 * Date Modified: Nov 3, 2021
 * Version: 2021.2
 */
package textObjectProperties;

/**An enum listing the type of information that does into smart labels*/
public enum SmartLabelDataType {
		LOCATION_IN_FIGURE("%order%", "Location in figure"),
		TIME("%t%", "Frame index of panel"),
		SLICE("%z%", "Slice index of panel"), 
		CHANNEL("%c%", "channel index of panel");

		private String code;
		private String name;

		/**
		 * @param string
		 */
		SmartLabelDataType(String string, String menuName) {
			this.setCode(string);
			this.name=menuName;
			
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}
		
		public String toString() {return name+code;}
		
}