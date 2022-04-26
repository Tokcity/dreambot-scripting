package M1D1;

import M1D1.nodes.DropNode;
import M1D1.nodes.MineNode;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.script.impl.TaskScript;

@ScriptManifest(category = Category.MINING, name = "M1D1", author = "Tokcity", version = 0.1)
public class MainClass extends TaskScript {

    @Override
    public void onStart()   {


        addNodes(new MineNode(), new DropNode());
    }
}
