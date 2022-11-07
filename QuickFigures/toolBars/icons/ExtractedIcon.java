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
 * Date Modified: Jan 5, 2021
 * Version: 2022.2
 */
package icons;

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;

/**
an icon that draws only the surface parts of a graphic tool icon
@see GraphicToolIcon
 */
public class ExtractedIcon implements Icon {

	
	private GraphicToolIcon innerIcon;

	public ExtractedIcon(GraphicToolIcon icon) {
		innerIcon=icon;
	}
	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		innerIcon.paintObjectOntoIcon(c, g, x, y);

	}

	@Override
	public int getIconWidth() {
		return innerIcon.getIconWidth();
	}

	@Override
	public int getIconHeight() {
		return innerIcon.getIconHeight();
	}

}
