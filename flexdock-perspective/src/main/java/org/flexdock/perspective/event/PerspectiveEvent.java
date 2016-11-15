/*
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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
