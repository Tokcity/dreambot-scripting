package PowerMine.nodes;

import org.dreambot.api.script.TaskNode;
import org.dreambot.api.methods.container.impl.Inventory;

public class DropNode extends TaskNode {

    @Override
    public int priority() {
        return 2;
    }

    @Override
    public boolean accept() {
        return Inventory.isFull();
    }

    @Override
    public int execute() {
        Inventory.dropAll(440);
        return 0;
    }
}
