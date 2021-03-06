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
 * Date Modified: Jan 15, 2021
 * Date Created: Jan 6, 2021
 * Version: 2021.1
 */
package icons;

import java.awt.Component;
import java.awt.Graphics;

/**An icon that is the size of a tree icon but meant only to act as space filler*/
public class EmptyIcon extends GenericTreeIcon {
	
	@Override
	public void paintIcon(Component arg0, Graphics arg1, int arg2, int arg3) {
	}

}
