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
package org.flexdock.docking.adapter;

/**
 * This POJO contains values for an adapted components dockable requirements.
 *
 * @author Christopher Butler
 */
public class AdapterMapping {
    private String className;

    private String dragSource;

    private String dragSourceList;

    private String frameDragSource;

    private String frameDragSourceList;

    private String persistentId;

    private String tabText;

    private String dockbarIcon;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getDockbarIcon() {
        return dockbarIcon;
    }

    public void setDockbarIcon(String dockbarIcon) {
        this.dockbarIcon = dockbarIcon;
    }

    public String getDragSource() {
        return dragSource;
    }

    public void setDragSource(String dragSource) {
        this.dragSource = dragSource;
    }

    public String getDragSourceList() {
        return dragSourceList;
    }

    public void setDragSourceList(String dragSourceList) {
        this.dragSourceList = dragSourceList;
    }

    public String getFrameDragSource() {
        return frameDragSource;
    }

    public void setFrameDragSource(String frameDragSource) {
        this.frameDragSource = frameDragSource;
    }

    public String getFrameDragSourceList() {
        return frameDragSourceList;
    }

    public void setFrameDragSourceList(String frameDragSourceList) {
        this.frameDragSourceList = frameDragSourceList;
    }

    public String getPersistentId() {
        return persistentId;
    }

    public void setPersistentId(String persistentId) {
        this.persistentId = persistentId;
    }

    public String getTabText() {
        return tabText;
    }

    public void setTabText(String tabText) {
        this.tabText = tabText;
    }
}
