package DropParty.nodes;

import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.script.TaskNode;
import org.dreambot.api.wrappers.items.GroundItem;
import org.dreambot.api.methods.magic.Magic;

public class PickNode extends TaskNode {

    @Override
    public int priority() {
        return 0;
    }

    @Override
    public boolean accept() {
        return isLoot();
    }

    @Override
    public int execute() {
        return pick();
    }

    private int pick() {
        log("pick");
        if (isClose()) {
            take();
        } else {
            telegrab();
        }
        return 1;
    }

    public GroundItem loot = GroundItems.closest(item -> item.getItem().getLivePrice() > 5000);

    public boolean test() {
        GroundItem loot2 = GroundItems.closest(item -> item.getItem().getLivePrice() > 5000);
            log(loot2);
            return (loot2 != null);
    }

    public void telegrab() {
        log("Loot is 6 or more spaces away. Casting Telegrab");
        Magic.castSpellOn(Normal.TELEKINETIC_GRAB, loot);
        sleepUntil(() -> !loot.exists(), 10000);
    }

    public void take() {
        log("Loot is 5 or less spaces away. Grabbing");
        loot.interact("Take");
        sleepUntil(() -> !loot.exists(), 10000);
    }

    public boolean isLoot() {
        log(loot);
        return (loot != null);
    }

    public boolean isClose() {
        log("isClose");
        return (loot.distance() <= 5);
    }

}