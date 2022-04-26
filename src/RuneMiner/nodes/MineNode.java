package RuneMiner.nodes;

import org.dreambot.api.Client;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.script.TaskNode;
import org.dreambot.api.wrappers.interactive.GameObject;

import java.util.Date;
import java.util.HashMap;

public class MineNode extends TaskNode{

    static HashMap<Integer, HashMap<String, Long>> worlds;

    @Override
    public int priority() {
        return 2;
    }

    @Override
    public boolean accept() {
        return isOre();
    }

    @Override
    public int execute() {
        return mine();
    }

    private int mine() {

        if (!isMining()) {
            GameObject runite = GameObjects.closest(object -> object.distance() < 2 && (object.getID() == 11374 || object.getID() == 11375));
            if (runite !=null && runite.distance() < 2 ) {
                    runite.interact("Mine");
                    // this is where we should log world+time to hashmap
                    log("Time to mine!");
                    sleepUntil(this::isMining, 3000);

                    log("Mining done, logging time..");
                    long ts = new Date().getTime();
                    log("TS: " + ts);

                    // Get the hashmap in the worlds currently
                    int currentWorld = Client.getCurrentWorld();
                    log("currentWorld: " + currentWorld);
                    int runiteX = runite.getX();
                    int runiteY = runite.getY();
                    String tileString = runiteX + "/" + runiteY;
                    log("tileString: " + tileString);
                    HashMap<String, Long> worldMap = worlds.get(currentWorld);
                    if(worldMap != null) {
                        // Check our worldMap for the tile
                        log("worldMap != null");
                        Long tileTimestamp = worldMap.get(tileString);
                        if(tileTimestamp != null) {
                            log("tileTimestamp != null");
                            // We have a previous timestamp for this tile, update it
                            worldMap.put(tileString, ts);
                        } else {
                            log("tileTimestamp == null");
                            // No previous time stamp for this tile, add it
                            worldMap.put(tileString, ts);
                        }
                    } else {
                        log("worldMap == null");
                        // No worldMap for this world, create worldMap
                        HashMap<String, Long> newWorldMap = new HashMap<String, Long>();
                        newWorldMap.put(tileString, ts);
                        worlds.put(currentWorld, newWorldMap);
                    }
                }
        } else {
            // We are here while mining
            sleep(Calculations.random(500, 1250));
        }
        return 0;
    }

    private boolean isOre() {
        GameObject rock = GameObjects.closest(object -> object.distance() < 2 && (object.getID() == 11374 || object.getID() == 11375));
        if (rock == null) {
            log("Ore not found, hopping worlds [MineNode]");
            return false;
        } else if (rock.exists()) {
            return true;
        }
        return false;
    }

    private boolean isMining() {
        return getLocalPlayer().isAnimating();
    }

    public static void setWorldMap(HashMap<Integer, HashMap<String, Long>> worldMap) {
        worlds = worldMap;
    }
}
