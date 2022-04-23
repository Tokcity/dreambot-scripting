package PowerMine.nodes;

import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.script.TaskNode;
import org.dreambot.api.wrappers.interactive.GameObject;

public class MineNode extends TaskNode {

    @Override
    public int priority() {
        return 1;
    }

    @Override
    public boolean accept() {
        return true;
    }

    @Override
    public int execute() {
        if (!isMining()) {
            GameObject rock = GameObjects.closest(11364,11365);
            if (rock !=null) {
                if (rock.distance() == 1)
                    rock.interact("Mine") ;{
                        sleepUntil(() -> isMining(), 2500);
                    }
                }
            }
        return 0;
    }

    private boolean isMining() {
        return getLocalPlayer().isAnimating();
    }
}
