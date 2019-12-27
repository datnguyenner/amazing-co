package amazingco;

import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JedisClient {

	private final Jedis jedis;

	public JedisClient(String host, int port, String password) {
		this.jedis = new Jedis(host, port);
		System.out.println("Connected to Redis");

	}

	public void addNode(Node node) {
		StringBuilder sb = new StringBuilder();
		String key = sb.append(node.getName()).append(":root").toString();
		System.out.println(key);
		this.jedis.append(key, node.getRoot() != null ? node.getRoot() : "");

		sb = new StringBuilder();
		key = sb.append(node.getName()).append(":parent").toString();
		this.jedis.append(key, node.getParent() != null ? node.getParent() : "");

		sb = new StringBuilder();
		key = sb.append(node.getName()).append(":height").toString();
		this.jedis.append(key, String.valueOf(node.getHeight()));

		sb = new StringBuilder();
		final String listKey = sb.append(node.getName()).append(":children").toString();

		node.getChildren().forEach(child -> this.jedis.lpush(listKey, child));

	}

	public Node getNode(String name) {

		Node node = new Node(name, this.jedis.get(name + ":root"), this.jedis.get(name + ":parent"),
				Integer.valueOf(this.jedis.get(name + ":height")));

		List<String> childrens = this.jedis.lrange(name + ":children", 0, -1);
		childrens.forEach(child -> node.addChild(child));

		return node;
		
	}

}
