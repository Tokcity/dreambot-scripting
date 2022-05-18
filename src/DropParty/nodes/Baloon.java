package DropParty.nodes;

import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.script.TaskNode;
public class Baloon extends TaskNode {

    @Override
    public int priority() {
        return 2;
    }

    @Override
    public boolean accept() {
        return Inventory.onlyContains(249);
    }

    @Override
    public int execute() {
        Bank.openClosest(); {
            sleepUntil(() -> getLocalPlayer().isMoving(), 2000); {
                Bank.depositAllItems(); {
                    Bank.withdrawAll(199); {
                        Bank.close();
                    }
                }
            }
        }
        return 0;
    }
}
