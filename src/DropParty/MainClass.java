package DropParty;

import DropParty.nodes.PickNode;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.script.impl.TaskScript;

@ScriptManifest(category = Category.MONEYMAKING, name = "DropParty", author = "Tokcity", version = 0.2)
public class MainClass extends TaskScript {

    @Override
    public void onStart()   {
        addNodes(new PickNode());
    }
}
