/**
 * Author: Greg Mazo
 * Date Modified: Mar 28, 2021
 * Date Created: Mar 18, 2021
 * Version: 2021.1
 */
package textObjectProperties;

import java.awt.Color;

/**
objects implement this interface if they have a property that modifies a certain color
@see ColorDimmer for more about the 'dimming'
 */
public interface DimsColor {
	/**returns a dimmed version of the color*/
	public Color getDimmedColor(Color c);
}
