package devtaube.countryside.level.components;

import rosequartz.ecb.Component;
import rosequartz.ecb.ECB;
import rosequartz.ecb.Entity;

import static rosequartz.ecb.ECB.*;

public class HasClockComponent implements Component {

    public Entity[] savedState;

    public void saveState() { savedState = ECB.getCopy(); }

    public void loadState() {
        ECB.clearEntities();
        ECB.add(savedState);
        saveState();
    }

    public boolean getStateSaved() { return savedState != null; }

}
