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
 * Version: 2021.1
 */
package locatedObject;

import java.awt.Shape;
import java.awt.geom.Area;
import java.util.ArrayList;

/**the most basic object container. used for storing a collection of objects*/
public class ArrayObjectContainer implements ObjectContainer {

	
	private ArrayList<LocatedObject2D> array;
	private ArrayList<LocatedObject2D> neverRemove=new ArrayList<LocatedObject2D>();//certain objects must never be removed

	/**Creates an object container from the list*/
	public ArrayObjectContainer(ArrayList<?> list) {
		array=new ArrayList<LocatedObject2D>();
		if(list==null) return;
		for(Object l: list) {
			if (l instanceof LocatedObject2D) {array.add((LocatedObject2D) l);}
		}
	
	}

	@Override
	public void takeFromImage(LocatedObject2D roi) {
		if (this.getNeverRemove().contains(roi)) return;
		array.remove(roi);
		
		
	}

	@Override
	public void addItemToImage(LocatedObject2D roi) {
		if (this.getNeverRemove().contains(roi)) return;
		array.add(roi);
		
	}

	@Override
	public void addRoiToImageBack(LocatedObject2D roi) {
		if (this.getNeverRemove().contains(roi)) return;
		array.add(roi);
		
	}

	/**returns all the objects in the container
	 * On feb 6 2021 determined that returning a different array and not the one used to contain the objects
	 * internally fixed a bug that occurs with layout edits*/
	@Override
	public ArrayList<LocatedObject2D> getLocatedObjects() {
		ArrayList<LocatedObject2D> out = new ArrayList<LocatedObject2D>();out.addAll(array); return out;
	}

	/**this */
	@Override
	public LocatedObject2D getSelectionObject() {
		// TODO Auto-generated method stub
		return null;
	}

	public ArrayList<LocatedObject2D> getNeverRemove() {
		if (neverRemove==null) neverRemove=new ArrayList<LocatedObject2D>();
		return neverRemove;
	}

	public void setNeverRemove(ArrayList<LocatedObject2D> neverRemove) {
		
		this.neverRemove = neverRemove;
	}
	
	
	public static Class<?> ignoredClass=null;
	public static Class<?> ignoredClass2=null;
	public static Class<?>[] ignoredClasses=null;
	
	/**Combines the outlines of the objects in the list into one shape
	   will ignore items of certain classes ()*/
	public static Shape combineOutLines(ArrayList<? extends LocatedObject2D> arrayList) {
		Area a=new Area();
		for(LocatedObject2D l:arrayList)  {
			if (isIgnoredClass(l)) continue;
			a.add(new Area(l.getOutline()));
		}
		return a;
	}

	/**returns true if the given object is part of a class that should be ignored*/
	public static boolean isIgnoredClass(LocatedObject2D l) {
		if( ignoredClass!=null&&ignoredClass.isInstance(l)) return true;
		if( ignoredClass2!=null&&ignoredClass2.isInstance(l)) return true;
		if( ignoredClasses!=null) 
			{
			for(Class<?> c: ignoredClasses) {
				if(c.isInstance(l)) return true;
			}
			}
		return false;
	}
	
	
	/**Creates an area from the bounds of all the items in the list.
	 will ignore items of certain classes */
	public static Shape combineBounds(ArrayList<? extends LocatedObject2D> arrayList) {
		Area a=new Area();
		for(LocatedObject2D l:arrayList)  {
			if (isIgnoredClass(l)) continue;
			a.add(new Area(l.getBounds()));
		}
		return a;
	}
	
	/**Selects any items within the list that are of the same class as the example object
	 * @param all the list
	 * @param exampleObject the example object*/
	public static void selectAllOfType(ArrayList<LocatedObject2D> all, LocatedObject2D exampleObject) {
		for(LocatedObject2D item: all) {
			
			if(exampleObject==null||item==null) continue;
			if(item.getClass()==exampleObject.getClass()) item.select();
		}
	}
}
