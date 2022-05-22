package M1D1.nodes;

import org.dreambot.api.script.TaskNode;
import org.dreambot.api.methods.container.impl.Inventory;

public class DropNode extends TaskNode {

    @Override
    public int priority() {
        return 2;
    }

    @Override
    public boolean accept() {
        return Inventory.contains(440);
    }

    @Override
    public int execute() {
        if(!isMining()) {
            Inventory.dropAll(440);
        }
        return 0;
    }
    private boolean isMining() {
        return getLocalPlayer().isAnimating();
    }
}
