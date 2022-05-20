package RuneMiner.nodes;

import RuneMiner.Helper;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.world.World;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.api.script.TaskNode;
import org.dreambot.api.wrappers.interactive.GameObject;

import java.util.Date;
import java.util.HashMap;

public class WorldNode extends TaskNode {

    HashMap<Integer, HashMap<String, Long>> worlds = new HashMap<Integer, HashMap<String, Long>>();

    private Helper util = new Helper();

    Area mineArea;
    int[] unminedRockIds;
    private double oreRespawnMinutes;
    private double minOreMineAge;
    private double maxOreMineAge;

    @Override
    public int priority() {
        return 1;
    }

    @Override
    public boolean accept() {
        return util.shouldHop(mineArea, unminedRockIds, worlds,  minOreMineAge, maxOreMineAge);
    }

    @Override
    public int execute() {
        return hop();
    }

    private int hop() {
        int finalWorldNum = 0;
        World world;
        log("Pre-hop check for known worlds...");
        // Print keys and values
        for (Integer worldNum : worlds.keySet()) {
            log("found world: " + worldNum);
            HashMap<String, Long> worldMap = worlds.get(worldNum);
            if(worldMap != null) {
                log("found worldMap for world: " + worldNum);
                // List all tiles we have for this world and their times
                for (String tileString : worldMap.keySet()) {
                    Long ts = worldMap.get(tileString);
                    long currentTs = new Date().getTime();

                    long age = currentTs - ts;
                    log("tile age: " + age);

                    // Check age to see if we should swap
                    if(age >= minOreMineAge && age <= maxOreMineAge) {
                        log("found world " + worldNum + " tile " + tileString + " with refreshing rocks in " + age / 1000 + "sec");
                        log("refresh world: " + worldNum);
                        finalWorldNum = worldNum;
                    } else if(age >= maxOreMineAge) {
                        log("found expired world " + worldNum + " tile " + tileString + " with rocks refreshed about " + age / 1000 + "sec ago..");
                        // Too old remove timestamp for tileString
                        worldMap.remove(tileString);
                        log("removed " + tileString + " from worldMap " + worldNum);
                        // Check if any tiles left, if not remove
                        if(worldMap.isEmpty()) {
                            log("worldMap " + worldNum + " is now empty, removing from worlds");
                            // Remove the worldMap HashMap from our worlds HashMap
                            worlds.remove(worldNum);
                            log("removed " + worldNum + " from worlds...");
                        }
                    }
                }
            }
        }

        if(finalWorldNum == 0) {
            log("Finding another random world..");
            world = Worlds.getRandomWorld(w -> !w.isPVP() && !w.isF2P() && w.getMinimumLevel() == 0);
        } else {
            world = Worlds.getWorld(finalWorldNum);
        }

        log("Hopping to world: " + world.getWorld());
        WorldHopper.hopWorld(world);

        sleep(5000);
        return 0;
    }

    private boolean isOre() {
        GameObject rock = GameObjects.closest(object -> object.distance() < 2 && (object.getID() == 11374 || object.getID() == 11375));
        if (rock == null) {
            log("Ore not found, hopping worlds [WorldNode]");
            return true;
        } else if (rock.exists()) {
            return false;
        }
        return false;
    }

    public HashMap<Integer, HashMap<String, Long>> getWorld() {
        return worlds;
    }

    public void setOreRespawnMinutes(double o) {
          oreRespawnMinutes = o;
    }
    public void setMinOreMineAge(double mi) {
         minOreMineAge = mi;
    }
    public void setMaxOreMineAge(double ma) {
         maxOreMineAge = ma;
    }
    public void setMineArea(Area ma) { mineArea = ma; }
    public void setUnminedRockIds(int[] ids) { unminedRockIds = ids; }
}
