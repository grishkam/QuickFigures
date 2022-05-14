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
 * Version: 2022.1
 */
package locatedObject;

import java.io.IOException;
import java.util.ArrayList;

import utilityClasses1.ArraySorter;

/**maintains a list of location change listeners*/
public class LocationChangeListenerList extends
		ArrayList<LocationChangeListener> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	
	/**returns an array with all the listeners*/
	public LocationChangeListener[] getAll() {
		LocationChangeListener[] obs=new LocationChangeListener[this.size()];
		for(int i=0;i<obs.length; i++ ) {
			obs[i]=this.get(i);
		}
	
		return obs;
	}

	public void notifyListenersOfMoveMent(LocatedObject2D l) {
		for(LocationChangeListener lis: getAll()) {
			if (lis==null) continue;
			lis.objectMoved(l);
		}
	
	}
	
	public void notifyListenersOfSizeChange(LocatedObject2D l) {
		for(LocationChangeListener lis: getAll()) {
			if (lis==null) continue;
			lis.objectSizeChanged(l);
		}
		
	}
	
	public void notifyListenersOfUserSizeChange(LocatedObject2D l) {
		for(LocationChangeListener lis: getAll()) {
			if (lis==null) continue;
			lis.userSizeChanged(l);
		}
		
	}
	
	public void notifyListenersOfDeath(LocatedObject2D l) {
		//ArrayList<locationChangeListener> list = this;
		for(LocationChangeListener lis: getAll()) {
			if (lis==null) continue;
			lis.objectEliminated(l);
		}
	}
	
	
	public void notifyListenersOfUserMove(LocatedObject2D l) {
		for(LocationChangeListener lis: getAll()) {
			if (lis==null) continue;
			lis.userMoved(l);
		}
	}
	

	
	private void writeObject(java.io.ObjectOutputStream out)
		     throws IOException {
		ArraySorter.removeDeadItems(this);
		ArraySorter.removeNonSerialiazble(this);
		out.defaultWriteObject();
	}
	
	
//	public static void main(String[] args) {}
	
	
}
