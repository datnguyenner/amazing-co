package amazingco;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
public class Controller {

	private final JedisClient jedis = new JedisClient("127.0.0.1", 6379, "");
	private final NodeUtil util = new NodeUtil();

	@RequestMapping("/")
	public String home() {
		return "Welcome to Amazing Co";
	}
	
	@RequestMapping(value = "nodes/{node}/descendants", method = RequestMethod.GET)
	public List<Node> getDescendants(@PathVariable String node) throws InterruptedException {
		return util.getDescendants(jedis.getNode(node), jedis);
	}
	
	@PostMapping(value = "nodes/{child}/changeParent")
	public ResponseEntity<String> changeParent(@PathVariable String child, @RequestBody String newParent) {

		if(util.isValidNewParent(child, newParent, jedis)) {
			util.changeParent(child, newParent, jedis);
		}

		return ResponseEntity.status(HttpStatus.OK).build();
	}
	
	@PostMapping(value = "/useTestData")
	public ResponseEntity<String> useTestData() {

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

		return ResponseEntity.status(HttpStatus.OK).build();
	}
}