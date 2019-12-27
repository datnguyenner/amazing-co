package amazingco;

import java.util.ArrayList;
import java.util.List;

public class Node {

	private final String name;
	private final String root;
	private String parent;
	private int height;
	private final List<String>children;

	public Node(String name, String root, String parent, int height) {
		this.name = name;
		this.root = root;
		this.parent = parent;
		this.children = new ArrayList<String>();
		this.height = height;
	}
	
	public void addChild(String child) {
		this.children.add(child);
	}
	
	public String getName() {
		return name;
	}

	public String getRoot() {
		return root;
	}

	public String getParent() {
		return parent;
	}
	
	public void setParent(String parent) {
		this.parent = parent;
	}

	public int getHeight() {
		return height;
	}
	
	public void setHeight(int height) {
		this.height = height;
	}

	public List<String> getChildren() {
		return children;
	}

	@Override
	public String toString() {
		return "Node [name=" + name + ", root=" + root + ", parent=" + parent + ", height=" + height + "]";
	}

}
