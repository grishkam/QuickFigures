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
 * Version: 2023.2
 */
package graphicalObjects;

import java.io.Serializable;

import graphicalObjects_Shapes.SimpleGraphicalObject;
import locatedObject.Named;
import locatedObject.ShowsOptionsDialog;
import locatedObject.Tagged;

/**An interface for objects that combines many features of other interfaces*/
public interface GraphicalObject extends SimpleGraphicalObject,Tagged, Serializable,Named, ShowsOptionsDialog{
		

}
