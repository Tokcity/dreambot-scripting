package M1D1.nodes;

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
            log("We aren't currently mining");
            GameObject rock = GameObjects.closest(11364,11365);
            if (rock !=null) {
                log("Rock isn't NULL");
                if (rock.distance() == 1)
                    log("Rock distance = 1");
                    rock.interact("Mine") ;
                    sleepUntil(() -> !rock.exists(), 10000);
            }
        }
        return 0;
    }

    private boolean isMining() {
        return getLocalPlayer().isAnimating();
    }
}
