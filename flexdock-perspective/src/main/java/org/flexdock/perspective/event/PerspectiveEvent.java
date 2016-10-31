package org.flexdock.perspective.event;

import org.flexdock.event.Event;
import org.flexdock.perspective.Perspective;

/**
 *
 * @author Mateusz Szczap
 */
public class PerspectiveEvent extends Event {
    public final static int CHANGED = 1;
    public final static int RESET = 2;

    private Perspective oldPerspective;

    public PerspectiveEvent(Perspective perspective, Perspective oldPerspective, int eventType) {
        super(perspective, eventType);
        this.oldPerspective = oldPerspective;
    }

    public Perspective getPerspective() {
        return (Perspective)getSource();
    }

    public Perspective getOldPerspective() {
        return oldPerspective;
    }
}
