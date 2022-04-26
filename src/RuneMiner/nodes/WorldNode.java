package RuneMiner.nodes;

import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.world.World;
import org.dreambot.api.methods.world.Worlds;
import org.dreambot.api.methods.worldhopper.WorldHopper;
import org.dreambot.api.script.TaskNode;
import org.dreambot.api.wrappers.interactive.GameObject;

import java.util.Date;
import java.util.HashMap;

public class WorldNode extends TaskNode {

    HashMap<Integer, HashMap<String, Long>> worlds = new HashMap<Integer, HashMap<String, Long>>();

    @Override
    public int priority() {
        return 1;
    }

    @Override
    public boolean accept() {
        return isOre();
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
                    if(age >= 345000) {
                        log("found world with refreshing rocks..");
                        log("refresh world: " + worldNum);
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
}
