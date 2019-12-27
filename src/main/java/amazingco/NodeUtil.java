package amazingco;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class NodeUtil {
	
	JedisClient jedis = new JedisClient("127.0.0.1", 6379, "");

	public List<Node> getDescendants(Node node) {

		List<Node> descendants = new ArrayList<>();

		Deque<Node> nodes = new ArrayDeque<>();
		nodes.push(node);

		while (!nodes.isEmpty()) {

			Node topNode = nodes.pop();
			topNode.getChildren().forEach(child -> {
				Node dataNode = jedis.getNode(child);
				descendants.add(dataNode);
				nodes.push(dataNode);
			});
		}

		return descendants;
	}

	/*
	public void changeParent(Node root, String name, String newParent) {
		
		Node editNode = search(root, name);
		
		System.out.println("test" + editNode);
		
		Node currentParent = editNode.getParent();
		currentParent.getChildren().removeIf(node-> node.getName().equalsIgnoreCase(name));
		
		Node newParentNode = search(root, newParent);
		editNode.setParent(newParentNode);
		editNode.setHeight(newParentNode.getHeight()+1);
		newParentNode.addChild(editNode);

	}
	
	private Node search(Node root,  String name) { 
	    
		if (root==null || root.getName().equalsIgnoreCase(name)) 
	        return root; 
	  
		for(Node node : root.getChildren()) {
			
			Node editNode = search(node, name);
			
			if( editNode != null) return editNode;
		}
		
		return null;
	} 
	*/

}
