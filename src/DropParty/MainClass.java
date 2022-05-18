package DropParty;

import BasicHerb.nodes.BankNode;
import BasicHerb.nodes.CleanNode;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.script.impl.TaskScript;

@ScriptManifest(category = Category.HERBLORE, name = "BasicHerb", author = "Tokcity", version = 0.1)
public class MainClass extends TaskScript {

    @Override
    public void onStart()   {


        addNodes(new BankNode(), new CleanNode());
    }
}
