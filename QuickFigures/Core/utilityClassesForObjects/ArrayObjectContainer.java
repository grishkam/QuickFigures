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
package utilityClassesForObjects;

import java.awt.Shape;
import java.awt.geom.Area;
import java.util.ArrayList;

/**the most basic object container. used for storage */
public class ArrayObjectContainer implements ObjectContainer {

	
	private ArrayList<LocatedObject2D> array;
	private ArrayList<LocatedObject2D> neverRemove=new ArrayList<LocatedObject2D>();

	public ArrayObjectContainer(ArrayList<?> list) {
		array=new ArrayList<LocatedObject2D>();
		for(Object l: list) {
			if (l instanceof LocatedObject2D) {array.add((LocatedObject2D) l);}
		}
		//array=list;
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

	@Override
	public ArrayList<LocatedObject2D> getLocatedObjects() {
		// TODO Auto-generated method stub
		return array;
	}

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
	
	public static Shape combineOutLines(ArrayList<? extends LocatedObject2D> arrayList) {
		Area a=new Area();
		for(LocatedObject2D l:arrayList)  {
			if (isIgnoredClass(l)) continue;
			a.add(new Area(l.getOutline()));
		}
		return a;
	}

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
	
	
	
	public static Shape combineBounds(ArrayList<? extends LocatedObject2D> arrayList) {
		Area a=new Area();
		for(LocatedObject2D l:arrayList)  {
			if (isIgnoredClass(l)) continue;
			a.add(new Area(l.getBounds()));
		}
		return a;
	}
	
	public static void selectAllOfType(ArrayList<LocatedObject2D> all, LocatedObject2D sel) {
		for(LocatedObject2D item: all) {
			
			if(sel==null||item==null) continue;
			if(item.getClass()==sel.getClass()) item.select();
		}
	}
}
