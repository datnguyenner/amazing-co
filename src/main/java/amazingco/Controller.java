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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
public class Controller {

	private final JedisClient jedis = new JedisClient("redis", 6379, "");
	private final NodeUtil util = new NodeUtil();

	@RequestMapping("/")
	public String home() {
		return "Welcome to Amazing Co";
	}

	@RequestMapping(value = "nodes/{node}/descendants", method = RequestMethod.GET)
	public List<Node> getDescendants(@PathVariable String node) throws InterruptedException {

		Node savedNode = jedis.getNode(node);

		if (savedNode == null) {
			throw new NodeNotFoundException();
		}

		return util.getDescendants(jedis.getNode(node), jedis);
	}

	@PostMapping(value = "nodes/{node}/change-parent")
	public ResponseEntity<String> changeParent(@PathVariable String node, @RequestBody String newParent) {

		if (jedis.getNode(newParent) == null) {
			throw new NodeNotFoundException();
		}

		if (util.isValidNewParent(node, newParent, jedis)) {
			util.changeParent(node, newParent, jedis);
			return ResponseEntity.status(HttpStatus.OK).build();
		}
		
		return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
	}

	@PostMapping(value = "/useTestData")
	public ResponseEntity<String> useTestData() {

		util.buildTreeNode(jedis);

		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "No such node") // 404
	public class NodeNotFoundException extends RuntimeException {
	}
}