package org.flexdock.test.view;

import org.flexdock.view.Titlebar;
import org.junit.jupiter.api.Test;

import javax.swing.AbstractAction;
import javax.swing.Action;
import java.awt.event.ActionEvent;

import static org.junit.jupiter.api.Assertions.*;

class TitlebarTest {

    private static Action createAction(String name) {
        return new AbstractAction(name) {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        };
    }

    @Test
    void testTitlebarWithActions() {
        Titlebar titlebar = new Titlebar(new Action[] {createAction("first"), createAction("second")});

        assertEquals(2, titlebar.getActions().length);
        assertNotNull(titlebar.getActionButton("first"));
        assertNotNull(titlebar.getActionButton("second"));
    }

    @Test
    void testActionAddedAfterEmptyActions() {
        Titlebar titlebar = new Titlebar(new Action[0]);

        titlebar.addAction(createAction("added"));

        assertEquals(1, titlebar.getActions().length);
        assertNotNull(titlebar.getActionButton("added"));
    }

}
