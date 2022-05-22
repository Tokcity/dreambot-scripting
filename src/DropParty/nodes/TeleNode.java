package DropParty.nodes;

import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.item.GroundItems;
import org.dreambot.api.methods.magic.Magic;
import org.dreambot.api.methods.magic.Normal;
import org.dreambot.api.script.TaskNode;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.GroundItem;

public class TeleNode extends TaskNode {

    @Override
    public int priority() {
        return 1;
    }

    @Override
    public boolean accept() {
        return true;
    }

    @Override
    public int execute() {
        GroundItem loot = GroundItems.closest("Willow logs");
        if (loot.distance() >= 10) {
            Magic.castSpellOn(Normal.TELEKINETIC_GRAB, loot);
            sleepUntil(() -> !loot.exists(), 10000);
        }
        else
            loot.interact("Take");
        return 0;
    }

    private boolean isMining() {
        return getLocalPlayer().isAnimating();
    }
}
