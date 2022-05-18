package RuneMiner;

import RuneMiner.nodes.BankNode;
import RuneMiner.nodes.MineNode;
import RuneMiner.nodes.WorldNode;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.script.impl.TaskScript;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@ScriptManifest(category = Category.MINING, name = "RuneMiner", author = "Tokcity", version = 0.2)
public class MainClass extends TaskScript {

    @Override
    public void onStart()   {
        WorldNode worldNode = new WorldNode();
        MineNode mineNode = new MineNode();
        BankNode bankNode = new BankNode();

        // Set World
        mineNode.setWorldMap(worldNode.getWorld());

        // Set Mining Values
        worldNode.setOreRespawnMinutes(mineNode.getOreRespawnMinutes());
        worldNode.setMinOreMineAge(mineNode.getMinOreMineAge());
        worldNode.setMaxOreMineAge(mineNode.getMaxOreMineAge());
        worldNode.setMineArea(mineNode.getMineArea());
        worldNode.setUnminedRockIds(mineNode.getUnminedRockIds());

        // Set Bank Values
        bankNode.setInvOreId(mineNode.getInvOreId());
        bankNode.setInvPickaxeId(mineNode.getInvPickaxeId());

        addNodes(worldNode, mineNode, bankNode);

    }
}
