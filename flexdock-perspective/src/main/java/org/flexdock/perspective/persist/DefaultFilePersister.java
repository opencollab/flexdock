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
package org.flexdock.perspective.persist;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import org.flexdock.docking.state.PersistenceException;

/**
 * @author Christopher Butler
 */
public class DefaultFilePersister implements Persister {

    @Override
    public PerspectiveModel load(InputStream in) throws IOException, PersistenceException {
        if(in==null) {
            return null;
        }

        ObjectInputStream ois = null;
        try {
            ois = in instanceof ObjectInputStream? (ObjectInputStream)in:
                  new ObjectInputStream(in);
            return (PerspectiveModel) ois.readObject();
        } catch(ClassNotFoundException ex) {
            throw new PersistenceException("Unable to unmarshal data", ex);
        } finally {
            if(ois != null) {
                ois.close();
            }
        }
    }

    @Override
    public boolean store(OutputStream out, PerspectiveModel info) throws IOException {
        if(info==null || out==null) {
            return false;
        }

        ObjectOutputStream oos = null;
        try {
            oos = out instanceof ObjectOutputStream? (ObjectOutputStream) out:new ObjectOutputStream(out);
            oos.writeObject(info);

            return true;
        } finally {
            if(oos != null) {
                oos.close();
            }
        }
    }

}
