package BasicHerb.nodes;

import org.dreambot.api.script.TaskNode;
import org.dreambot.api.methods.container.impl.Inventory;

public class CleanNode extends TaskNode {

    @Override
    public int priority() {
        return 1;
    }

    @Override
    public boolean accept() {
        return Inventory.contains(199);
    }

    @Override
    public int execute() {
        Inventory.interact(199, "Clean"); {
            sleepUntil(() -> Inventory.onlyContains(249), 4000);
                }
        return 0;
    }
}
