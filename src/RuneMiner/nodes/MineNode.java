package RuneMiner.nodes;

import RuneMiner.Helper;
import org.apache.commons.lang3.ArrayUtils;
import org.dreambot.api.Client;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Map;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.TaskNode;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.Player;
import org.dreambot.api.wrappers.items.Item;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class MineNode extends TaskNode{

    static HashMap<Integer, HashMap<String, Long>> worlds;

    private Helper util = new Helper();

    //Area mineArea = new Area(3030, 9725, 3044, 9713);
    Area mineArea = new Area(3046, 9726, 3047, 9725);
    int[] unminedRockIds = { 11374, 11375 };
    int[] minedRockIds = { 11390, 11391 };

    int invOreId = 449;
    int invPickaxeId = 1275;

    Tile prefMiningSpot = new Tile(3046,9725,0);

    double oreRespawnMinutes = 2.5;
    double minOreMineAge = (60 * 1000 * oreRespawnMinutes);
    double maxOreMineAge = (60 * 1000 * (oreRespawnMinutes + 1));

    @Override
    public int priority() {
        return 2;
    }

    @Override
    public boolean accept() {
        return util.oreNearby(mineArea, unminedRockIds, worlds,  minOreMineAge, maxOreMineAge);
    } // Check if there is any ore nearby, return true if ore found

    @Override
    public int execute() {
        return mine();
    }

    private int mine() {
        if (!isMining()) {
            GameObject ore = GameObjects.closest(object -> Arrays.stream(unminedRockIds).anyMatch(i -> i == object.getID()) && mineArea.contains(object));
            if (ore != null) {
                int oresInInv = Inventory.count(invOreId);
                ore.interact("Mine");
                    // this is where we should log world+time to hashmap
                    sleepUntil(() -> Inventory.count(invOreId) > oresInInv, 3000);

                    log("Mining done, logging time..");
                    long ts = new Date().getTime();
                    log("TS: " + ts);

                    // Get the hashmap in the worlds currently
                    int currentWorld = Client.getCurrentWorld();
                    log("currentWorld: " + currentWorld);
                    int runiteX = ore.getX();
                    int runiteY = ore.getY();
                    String tileString = runiteX + "/" + runiteY;
                    log("tileString: " + tileString);
                    HashMap<String, Long> worldMap = worlds.get(currentWorld);
                    if(worldMap != null) {
                        // We've been to this world before
                        log("returned to world " + currentWorld);
                        // Check our worldMap for the tile
                        Long tileTimestamp = worldMap.get(tileString);
                        if(tileTimestamp != null) {
                            log("tileTimestamp != null");
                            long age = ts - tileTimestamp;
                            // We have a previous timestamp for this tile, update it
                            worldMap.put(tileString, ts);
                            log("updated existing tileString: " + tileString + " with timestamp: " + ts + " for worldMap " + currentWorld + " [visited world " + (age / 1000) + "ms ago]");
                        } else {
                            log("tileTimestamp == null");
                            // No previous time stamp for this tile, add it
                            worldMap.put(tileString, ts);
                            log("put new tileString: " + tileString + " with timestamp: " + ts + " for worldMap " + currentWorld + " [returned world]");
                        }
                    } else {
                        log("first time visiting world " + currentWorld);
                        // No worldMap for this world, create worldMap
                        HashMap<String, Long> newWorldMap = new HashMap<String, Long>();
                        newWorldMap.put(tileString, ts);
                        worlds.put(currentWorld, newWorldMap);
                        log("put new tileString: " + tileString + " with timestamp: " + ts + " for worldMap " + currentWorld );
                    }
                } else {
                log("ore is null??");
            }
        } else {
            // We are here while mining
            sleep(Calculations.random(500, 1250));
        }
        return 0;
    }

    private boolean isMining() {
        return getLocalPlayer().isAnimating();
    }

    public void setWorldMap (HashMap<Integer, HashMap<String, Long>> w) {
        worlds = w;
    }

    public double getOreRespawnMinutes() {
        return oreRespawnMinutes;
    }
    public double getMinOreMineAge() {
        return minOreMineAge;
    }
    public double getMaxOreMineAge() {
        return maxOreMineAge;
    }
    public int getInvOreId() {
        return invOreId;
    }
    public int getInvPickaxeId() {
        return invPickaxeId;
    }
    public Area getMineArea() { return mineArea; }
    public int[] getUnminedRockIds() { return unminedRockIds; }
}
