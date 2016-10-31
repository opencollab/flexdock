package org.flexdock.view.actions;

public class ActionFactory {

    public static DefaultCloseAction createCloseAction() {
        return new DefaultCloseAction();
    }

    public static DefaultDisplayAction createDisplayAction() {
        return new DefaultDisplayAction();
    }

    public static DefaultPinAction createPinAction() {
        return new DefaultPinAction();
    }

}
