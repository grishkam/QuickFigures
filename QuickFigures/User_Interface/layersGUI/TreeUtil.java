package layersGUI;

import java.awt.Point;
import java.util.ArrayList;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import logging.IssueLog;

public class TreeUtil<Type> {
	

	public DefaultMutableTreeNode findDescendantWithuserObject(DefaultMutableTreeNode node, Type o) {
	  if (o==null||node==null) return null;
		if (doesNodeRepresentUserObject(node,o)) return node;
		int chil= 	node.getChildCount();
		for(int i=0; i<chil; i++) try {
			DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(i);
			if (doesNodeRepresentUserObject(child,o)) return child;
			
			DefaultMutableTreeNode grandchild=this.findDescendantWithuserObject(child, o);
			if (doesNodeRepresentUserObject(grandchild,o)) return grandchild;
		} catch (Throwable t) {IssueLog.log(t);}
		
		return null;
		
	}
	
	public TreePath findPathWithUserObject(DefaultMutableTreeNode node, Type o) {
		DefaultMutableTreeNode d = findDescendantWithuserObject(node,o);
		return getPath(d);
	}
	
	/**returns a tree path for the tree node*/
	public TreePath getPath(DefaultMutableTreeNode tn) {
		return new TreePath( getArrayForTreeNode(tn));
	}
	
	/**returns an array of objects that can be used to construct the tree path*/
	private Object[] getArrayForTreeNode(DefaultMutableTreeNode tn) {
		ArrayList<DefaultMutableTreeNode> out = new ArrayList<DefaultMutableTreeNode>();
		
		while (tn!=null) {out.add(tn); tn=(DefaultMutableTreeNode)tn.getParent();}
		
		Object[] out2 = new  Object[out.size()];
		
		
		
		for(int i=0; i<out2.length;  i++) {out2[out.size()-i-1]=out.get(i);
		}
		
		
		
		return out2;
	}
	
	public boolean doesNodeRepresentUserObject(TreeNode node, Type o) {
		if (o==null) return false;
		if (node instanceof DefaultMutableTreeNode) {
			DefaultMutableTreeNode n2=(DefaultMutableTreeNode) node;
			if (n2.getUserObject()==o) return true;
		}
		return false;
	}
	
	public DefaultMutableTreeNode getOrCreateChildWithUserObject(DefaultMutableTreeNode node, Type o, boolean acceptschildren) {
		if (o==null||node==null) return null;
		if (doesNodeRepresentUserObject(node,o)) return node;
		int chil= node.getChildCount();
		for(int i=0; i<chil; i++)  {
			DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(i);
			if (doesNodeRepresentUserObject(child,o)) return child;
		}
		//fireModeStructureChange(node);
		DefaultMutableTreeNode output = new DefaultMutableTreeNode(o, acceptschildren);
		node.add(output);
		return output;
	}
	
	public DefaultMutableTreeNode getNodeforPath(TreePath p) {
		if (p==null) return null;
		Object o=p.getLastPathComponent();
if (o instanceof DefaultMutableTreeNode) {
			
			DefaultMutableTreeNode tn = (DefaultMutableTreeNode) o;
			
				return tn;
				} else return null;
	} 
	
	
	public Object getObjectForTreePath(TreePath p) {
		DefaultMutableTreeNode tn= getNodeforPath( p);
		if (tn==null) return null;
		return tn.getUserObject();
	}
	
	public Object getUserObjectForPoint(JTree tree, Point p1) {
		TreePath tp = tree.getPathForLocation(p1.x, p1.y);
		return getObjectForTreePath(tp);
	}
	
	

}
