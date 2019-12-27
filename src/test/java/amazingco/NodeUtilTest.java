package amazingco;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import org.junit.After;
import static org.junit.Assert.assertEquals;

public class NodeUtilTest {

	NodeUtil util;
	JedisClient jedis;

	@Before
	public void createTreeNode() {

		util = new NodeUtil();
		jedis = new JedisClient("127.0.0.1", 6379, "");

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

	@Test
	public void testGetDecendants() {

		List<Node> descendants = util.getDescendants(jedis.getNode("A"), jedis);

		String arr[] = { "C", "B", "E", "D", "F", "G", "H", "J", "K", "I" };
		Set<String> descendantsA = new HashSet<>(Arrays.asList(arr));
		descendants.forEach(descendant -> {
			assertEquals(true, descendantsA.contains(descendant.getName()));
			descendantsA.remove(descendant.getName());
		});
		assertEquals(true, descendantsA.size() == 0);

		String arr2[] = { "G", "H", "J", "K", "I" };
		Set<String> descendantsC = new HashSet<>(Arrays.asList(arr2));
		descendants = util.getDescendants(jedis.getNode("C"), jedis);
		descendants.forEach(descendant -> {
			assertEquals(true, descendantsC.contains(descendant.getName()));
			descendantsC.remove(descendant.getName());
		});
		assertEquals(true, descendantsA.size() == 0);


		String arr3[] = { "J", "K", "I" };
		Set<String> descendantsH = new HashSet<>(Arrays.asList(arr3));
		descendants = util.getDescendants(jedis.getNode("H"), jedis);
		descendants.forEach(descendant -> {
			assertEquals(true, descendantsH.contains(descendant.getName()));
			descendantsH.remove(descendant.getName());
		});
		assertEquals(true, descendantsA.size() == 0);


	}

	@Test
	public void testChangeParent() {
		List<Node> descendants = util.getDescendants(jedis.getNode("A"), jedis);
		assertEquals(false, descendants.get(descendants.size() - 1).getHeight() == 5);
		
		util.changeParent("B", "K", jedis);
		descendants = util.getDescendants(jedis.getNode("A"), jedis);
		assertEquals(true, descendants.get(descendants.size() - 1).getHeight() == 5);
		assertEquals(true, descendants.get(0).getHeight() == 1);

	}

	@Test
	public void testInvalidParent() {

		assertEquals(false, util.isValidNewParent("B", "B", jedis));
		assertEquals(false, util.isValidNewParent("B", "E", jedis));
		assertEquals(false, util.isValidNewParent("B", "F", jedis));
		assertEquals(true, util.isValidNewParent("B", "C", jedis));
		assertEquals(true, util.isValidNewParent("B", "K", jedis));

	}

	@After
	public void deleteAllKeys() {

		jedis.deleteAllKeys();

	}

}