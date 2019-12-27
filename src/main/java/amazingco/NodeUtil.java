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

}
