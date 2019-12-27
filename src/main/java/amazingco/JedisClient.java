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
import java.util.Set;

public class JedisClient {

	private final Jedis jedis;

	public JedisClient(String host, int port, String password) {
		this.jedis = new Jedis(host, port);
		System.out.println("Connected to Redis");
	}

	public void addNode(Node node) {
		StringBuilder sb = new StringBuilder();
		String key = sb.append(node.getName()).append(":root").toString();
		this.jedis.set(key, node.getRoot() != null ? node.getRoot() : "");

		sb = new StringBuilder();
		key = sb.append(node.getName()).append(":parent").toString();
		this.jedis.set(key, node.getParent() != null ? node.getParent() : "");

		sb = new StringBuilder();
		key = sb.append(node.getName()).append(":height").toString();
		this.jedis.set(key, String.valueOf(node.getHeight()));

		sb = new StringBuilder();
		final String listKey = sb.append(node.getName()).append(":children").toString();

		node.getChildren().forEach(child -> this.jedis.sadd(listKey, child));

	}

	public Node getNode(String name) {

		Node node = new Node(name, this.jedis.get(name + ":root"), this.jedis.get(name + ":parent"),
				Integer.valueOf(this.jedis.get(name + ":height")));

		Set<String> childrens = this.jedis.smembers(name + ":children");
		childrens.forEach(child -> node.addChild(child));

		return node;
		
	}
	
	public void removeChild(String parent, String child) {

		StringBuilder sb = new StringBuilder();
		final String key = sb.append(parent).append(":children").toString();

		this.jedis.srem(key, child);
		
	}
	
	public String getParent(String node) {

		StringBuilder sb = new StringBuilder();
		String key = sb.append(node).append(":parent").toString();

		return jedis.get(key);
		
	}
	
	public void setParent(String child, String parent) {

		StringBuilder sb = new StringBuilder();
		String key = sb.append(child).append(":parent").toString();

		this.jedis.set(key, parent);
		
	}
	
	public void setHeight(String child, int height) {

		StringBuilder sb = new StringBuilder();
		String key = sb.append(child).append(":height").toString();

		this.jedis.set(key, String.valueOf(height));
		
	}
	
	public String getHeight(String node) {

		StringBuilder sb = new StringBuilder();
		String key = sb.append(node).append(":height").toString();

		return this.jedis.get(key);
		
	}
	
	public void addChild(String parent, String child) {

		StringBuilder sb = new StringBuilder();
		String key = sb.append(parent).append(":children").toString();

		this.jedis.sadd(key, child);
		
	}
	
	public void deleteAllKeys() {
		this.jedis.flushAll();
	}

}
