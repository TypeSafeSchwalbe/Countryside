package devtaube.countryside.menu.components;

import rosequartz.ecb.Component;

public class ClickableItemComponent implements Component {

    public final Runnable onClick;

    public ClickableItemComponent(Runnable onClick) { this.onClick = onClick; }

}
