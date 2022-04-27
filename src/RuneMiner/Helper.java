package RuneMiner;

import org.dreambot.api.Client;
import org.dreambot.api.methods.Calculations;
import org.dreambot.api.methods.MethodProvider;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Map;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.tabs.Tab;
import org.dreambot.api.methods.tabs.Tabs;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.items.Item;
import org.dreambot.api.wrappers.map.impl.CollisionMap;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Helper  {
    public void walkToTile(Tile tile) {
        // Check if far away, account for distance sleep
        // Check if on screen and navigate that way
        // Handle energy potions

        int tileFarAwayThreshold = 15;
        int tileFarAwayModifier = 10;
        int sleepUntilDestinationDistance = 0;
        boolean onScreenWalk = false;

        String[] energyPotionNames = { "Super energy potion(1)", "Super energy potion(2)","Super energy potion(3)","Super energy potion(4)", "Energy potion(1)", "Energy potion(2)","Energy potion(3)","Energy potion(4)" };
        List<Item> energyPotions = Inventory.all(item -> Arrays.asList(energyPotionNames).contains(item.getName()));
        for (int i = 0; i < energyPotions.size(); i++) {
            Item item = energyPotions.get(i);
            MethodProvider.log("["+i+"] item: " + item.getName());
        }
        MethodProvider.log("Walking.getRunEnergy(): "+ Walking.getRunEnergy() );
        MethodProvider.log("Walking.getRunThreshold(): " + Walking.getRunThreshold());
        // Check if we have runEnergyPotions and our energy is low
        while(Walking.getRunEnergy() <= Walking.getRunThreshold() && energyPotions.size() > 0) {
            MethodProvider.log("lowEnergy: " + Walking.getRunEnergy());
            MethodProvider.log("drinking energy potion...");
            takePotion(energyPotions);
            MethodProvider.sleep(Calculations.random(500, 1250));
        }
        double distanceToTile = Client.getLocalPlayer().distance(tile);
        MethodProvider.log("distanceToTile: " + distanceToTile);
        if(distanceToTile >= tileFarAwayThreshold) {
            // Tile far, adjust sleepUntilDistance
            MethodProvider.log("distanceToTile: " + distanceToTile + " - tileFarAwayThreshold: " + tileFarAwayThreshold);
            sleepUntilDestinationDistance = tileFarAwayModifier;
        }
        if(Map.isTileOnScreen(tile) && Map.isVisible(tile) && isTileOnScreen(tile)) {
            // On Screen Walk
            onScreenWalk = true;
        }
        if(Walking.canWalk(tile)){
            MethodProvider.log("Walking to mineArea onScreen...");
            if(onScreenWalk) {
                Walking.walkOnScreen(tile);
            } else {
                Walking.walk(tile);
            }
            while(Walking.getDestinationDistance() >= sleepUntilDestinationDistance) {
                MethodProvider.log("Walking.getDestinationDistance(): " + Walking.getDestinationDistance() + "/" + sleepUntilDestinationDistance);
                MethodProvider.sleep(ThreadLocalRandom.current().nextInt(525, 1250 + 1));
            }
            MethodProvider.log("Walking to destination, completed...");
            MethodProvider.sleepUntil(() -> Walking.getDestinationDistance() <= 0, 60000 * 60); // 1 Hour time limit for travel
        } else {
            MethodProvider.log("Unable to walk to tile: " + tile.getX() + "/" + tile.getY());
        }
        MethodProvider.sleep(ThreadLocalRandom.current().nextInt(25, 550 + 1));
    }

    public boolean isTileOnScreen(Tile t){
        return Client.getViewport().isOnGameScreen(Map.tileToScreen(t));
    }

    public void takePotion(List<Item> potions) {
        // Take a potion
        Item potion = potions.get(0);
        if(potion != null) {
            if(Tabs.getOpen() != Tab.INVENTORY) {
                Tabs.open(Tab.INVENTORY);
                MethodProvider.log("opened inventory");
            }
            MethodProvider.log("taking potion: " + potion.getName());
            potion.interact();
            MethodProvider.sleep(Calculations.random(750, 1250));
            while(Inventory.contains(item -> item.getID() == 229)) {
                int startInvItemCount = Inventory.count(229);
                MethodProvider.log("dropping empty potion vial");
                Inventory.drop(229);
                MethodProvider.sleepUntil(() -> Inventory.count(229) < startInvItemCount, 60000 * 5);
            }
            MethodProvider.sleep(Calculations.random(450, 950));
        }
    }
    public boolean oreNearby(Area mineArea, int[] unminedRockIds, HashMap<Integer, HashMap<String, Long>> worlds, double minOreMineAge, double maxOreMineAge) {
        // First we must be near the ore, check if we can get there
        // First get a valid standable tile
        Tile mineAreaTile = getValidTile(mineArea);

        if(mineAreaTile == null) {
            MethodProvider.log("unable to reach ore, cannot mine ore...");
            return false;
        }
        // We can reach the ore, so go to it
        if(!mineArea.contains(Client.getLocalPlayer())) {
            // Travel to ore to check if we have any to mine
            walkToTile(mineAreaTile);
        }

        // Check if we are in the area
        if(mineArea.contains(Client.getLocalPlayer())) {
            // See if we have any ore nearby
            GameObject unminedOre = GameObjects.closest(object -> Arrays.stream(unminedRockIds).anyMatch(i -> i == object.getID()) && mineArea.contains(object));
            if(unminedOre != null) {
                return true;
            }
            // We have no unmined ore, check the worlds
            int currentWorld = Client.getCurrentWorld();
            HashMap<String, Long> worldMap = worlds.get(Client.getCurrentWorld());
            if(worldMap != null) {
                // We have tiles for this world
                // Get tiles
                // List all tiles we have for this world and their times
                for (String tileString : worldMap.keySet()) {
                    Long ts = worldMap.get(tileString);
                    long currentTs = new Date().getTime();
                    long age = currentTs - ts;

                    // Check age to see if we should stay or skip
                    if(age >= minOreMineAge && age <= maxOreMineAge) {
                        MethodProvider.log("in world " + currentWorld + " with tile " + tileString + " with refreshing rocks in " + age / 1000 + "sec");
                        // Stay
                        return true;
                    } else if(age >= maxOreMineAge) {
                        MethodProvider.log("found expired world " + currentWorld + " tile " + tileString + " with rocks refreshed about " + age / 1000 + "sec ago..");
                        // Too old remove timestamp for tileString
                        worldMap.remove(tileString);
                        MethodProvider.log("removed " + tileString + " from worldMap " + currentWorld);
                        // Check if any tiles left, if not remove
                        if(worldMap.isEmpty()) {
                            MethodProvider.log("worldMap " + currentWorld + " is now empty, removing from worlds");
                            // Remove the worldMap HashMap from our worlds HashMap
                            worlds.remove(currentWorld);
                            MethodProvider.log("removed " + currentWorld + " from worlds...");
                        }
                    }
                }
            } else {
                MethodProvider.log("No world map and no ore found on world " + currentWorld + " skipping...");
                return false;
            }
        }
        return false;
    }
    public Tile getValidTile(Area area) {
        // Check if we are in area standing which should be valid tile?
        if(area.contains(Client.getLocalPlayer())) {
            return Client.getLocalPlayer().getTile();
        }
        // First we must be near the ore, check if we can get there
        // First get a valid standable tile
        boolean needValidTile = true;
        Tile areaTile = null;
        int tileTries = 0;
        int maxTileTries = 20;
        while(needValidTile) {
            if(tileTries >= maxTileTries) {
                MethodProvider.log("hit maxTileTries, exiting loop...");
                areaTile = null;
                break;
            }
            areaTile = area.getRandomTile();
             MethodProvider.log("checking random tile " + areaTile.getX() + "/" + areaTile.getY());
            int aTileFlag = Map.getFlag(areaTile);
            boolean canStand = CollisionMap.isBlocked(aTileFlag);
            if(canStand && Walking.canWalk(areaTile)) {
                needValidTile = false;
                MethodProvider.log("found valid tile at " + areaTile.getX() + "/" + areaTile.getY());
            }
            tileTries++;
        }
        return areaTile;
    }
}
