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

	public void changeParent(String child, String newParent, JedisClient jedis) {

		String currentParent = jedis.getParent(child);
		jedis.removeChild(currentParent, child);

		int height = Integer.valueOf(jedis.getHeight(newParent));
		jedis.setParent(child, newParent);
		jedis.setHeight(child, height + 1);
		jedis.addChild(newParent, child);

		Node childNode = jedis.getNode(child);
		updateDescendantsHeight(childNode, jedis);

	}

	public boolean isValidNewParent(String child, String newParent, JedisClient jedis) {

		Deque<Node> nodes = new ArrayDeque<>();
		nodes.push(jedis.getNode(child));

		if (child.equalsIgnoreCase(newParent)) {
			return false;
		}

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

}
