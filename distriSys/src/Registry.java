import java.util.List;
import java.util.Random;

/**
 * Created by Cullen on 1/21/2015.
 */
public class Registry {

    List<Node> nodes;
    Random rand;

    public Registry() {
        rand = new Random();
    }

    public void register(Node node) {
        boolean match;
        do {
            node.id = rand.nextInt(128);
            match = false;
            for(Node n: nodes) {
                if (n.id == node.id) {
                    match = true;
                    break;
                }
            }
        } while(match);
        nodes.add(node);
    }

    public void unregister(Node node) {
        nodes.remove(node);
    }
}
