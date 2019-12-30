package amazingco;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class NodeUtil {

	public List<Node> getDescendants(Node node, JedisClient jedis) {

		List<Node> descendants = new ArrayList<>();

		Deque<Node> nodes = new ArrayDeque<>();
		nodes.push(node);

		while (!nodes.isEmpty()) { // Used level order traversal to get descendants

			Node topNode = nodes.pop();
			topNode.getChildren().forEach(child -> {
				Node dataNode = jedis.getNode(child);
				descendants.add(dataNode);
				nodes.push(dataNode);
			});
		}

		return descendants;
	}

	public void changeParent(String child, String newParent, JedisClient jedis) {

		
		String currentParent = jedis.getParent(child);
		
		if(currentParent != null) {
			jedis.removeChild(currentParent, child);
		}

		int height = Integer.valueOf(jedis.getHeight(newParent));
		jedis.setParent(child, newParent);
		jedis.setHeight(child, height + 1);
		jedis.addChild(newParent, child);
		jedis.setRoot(child, jedis.getRoot(newParent));

		Node childNode = jedis.getNode(child);
		updateDescendantsHeight(childNode, jedis);

	}

	public boolean isValidNewParent(String child, String newParent, JedisClient jedis) {

		Node childNode = jedis.getNode(child);
		
		if(childNode != null) {
			
			if (child.equalsIgnoreCase(newParent)) {
				return false;
			}
			
			Deque<Node> nodes = new ArrayDeque<>();
			nodes.push(childNode);

			while (!nodes.isEmpty()) {
				Node topNode = nodes.pop();
				for (String childName : topNode.getChildren()) {
					if (childName.equalsIgnoreCase(newParent)) {
						return false;
					}
					Node dataNode = jedis.getNode(childName);
					nodes.push(dataNode);
				}
			}	
		}

		return true;
	}

	private void updateDescendantsHeight(Node node, JedisClient jedis) {

		Deque<Node> nodes = new ArrayDeque<>();
		nodes.push(node);

		while (!nodes.isEmpty()) {
			Node topNode = nodes.pop();
			topNode.getChildren().forEach(child -> {
				Node dataNode = jedis.getNode(child);
				jedis.setHeight(child, topNode.getHeight() + 1);
				nodes.push(dataNode);
			});
		}
	}
	
	// Use for testing purposes
	public void buildTreeNode(JedisClient jedis) {
		
		Node nodeA = new Node("A", null, null, 0);
		Node nodeB = new Node("B", "A", "A", 1);
		Node nodeC = new Node("C", "A", "A", 1);
		nodeA.addChild(nodeB.getName());
		nodeA.addChild(nodeC.getName());

		Node nodeD = new Node("D", "A", "B", 2);
		Node nodeE = new Node("E", "A", "B", 2);
		Node nodeF = new Node("F", "A", "B", 2);
		nodeB.addChild(nodeD.getName());
		nodeB.addChild(nodeE.getName());
		nodeB.addChild(nodeF.getName());

		Node nodeG = new Node("G", "A", "C", 2);
		Node nodeH = new Node("H", "A", "C", 2);
		nodeC.addChild(nodeG.getName());
		nodeC.addChild(nodeH.getName());

		Node nodeI = new Node("I", "A", "H", 3);
		Node nodeJ = new Node("J", "A", "H", 3);
		Node nodeK = new Node("K", "A", "H", 3);
		nodeH.addChild(nodeI.getName());
		nodeH.addChild(nodeJ.getName());
		nodeH.addChild(nodeK.getName());

		jedis.addNode(nodeA);
		jedis.addNode(nodeB);
		jedis.addNode(nodeC);
		jedis.addNode(nodeD);
		jedis.addNode(nodeE);
		jedis.addNode(nodeF);
		jedis.addNode(nodeG);
		jedis.addNode(nodeH);
		jedis.addNode(nodeI);
		jedis.addNode(nodeJ);
		jedis.addNode(nodeK);
		
	}

}
