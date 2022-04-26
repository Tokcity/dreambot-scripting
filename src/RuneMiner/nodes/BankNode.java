package RuneMiner.nodes;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.script.TaskNode;

public class BankNode extends TaskNode {

    @Override
    public int priority() {
        return 3;
    }

    @Override
    public boolean accept() {
        return Inventory.isFull();
    }

    @Override
    public int execute() {
        return bank();
    }
    private int bank() {
        log("Time to bank!");
        sleep(5000);
        Inventory.dropAll(449);
        return 0;
    }
}