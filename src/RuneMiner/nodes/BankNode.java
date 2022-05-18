package RuneMiner.nodes;

import RuneMiner.Helper;
import org.dreambot.api.Client;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.MethodProvider;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.NPCs;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Map;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.TaskNode;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.items.Item;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class BankNode extends TaskNode {

    private Helper util = new Helper();

    boolean dropIfNoBank = false;

    int invOreId;
    int invPickaxeId;

    Area bankArea = new Area(3012, 9720, 3014, 9717);

    @Override
    public int priority() {
        return 3;
    }

    @Override
    public boolean accept() {
        return Inventory.isFull() && util.getValidTile(bankArea) != null;
    }

    @Override
    public int execute() {
        return bank();
    }
    private int bank() { // Go to the bank
        log("Inventory full, banking...");

        // First we must be near the ore, check if we can get there

        Tile bankAreaTile = util.getValidTile(bankArea);
        if(bankAreaTile != null) {
            log("got non-null tile from util, " + bankAreaTile.getX() + "/" + bankAreaTile.getY());
            // We can reach the ore, so go to it
            if(!bankArea.contains(getLocalPlayer())) {
                // Travel to ore to check if we have any to mine
                log("walking to bank now...");
                util.walkToTile(bankAreaTile);
            }

            // Check if we are in the area
            if(bankArea.contains(getLocalPlayer())) {
                log("player in bank area...");
                // Bank
                GameObject nearbyBank = GameObjects.closest(obj -> obj != null && obj.hasAction("Use") && obj.getID() == 4483);
                if(nearbyBank != null) {
                    // Deposit ores
                    log("bankDistance: " + nearbyBank.distance());
                    if (nearbyBank.distance() <= 5 && nearbyBank.interact("Use")) {
                        sleepUntil(() -> Bank.isOpen(), Calculations.random(2500, 3250));
                        Bank.depositAllExcept(invPickaxeId);
                        sleepUntil(() -> Inventory.count(invOreId) == 0, 10000);
                        if(Inventory.isEmpty() && Bank.isOpen()) {
                            log("Closing bank...");
                            Bank.close();
                        }
                    }
                } else {
                    log("null banker??");
                }
            }
        } else {
            log("unable to reach bank, cannot bank ore...");
            // Drop if we want
            if(dropIfNoBank) {
                // Drop all ore
                Inventory.dropAll(item -> item.getID() == invOreId);
                sleepUntil(() -> Inventory.count(invOreId) == 0, 10000);
            }
        }
        log("made it to the end..");
        return ThreadLocalRandom.current().nextInt(25, 550 + 1);
    }

    public void setInvOreId(int id) {
         invOreId = id;
    }
    public void setInvPickaxeId(int id) {
         invPickaxeId = id;
    }
}