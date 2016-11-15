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
package org.flexdock.plaf.resources;

import java.awt.Insets;

/**
 * @author Claudio Romano
 */
public class InsetsResourceHandler extends ResourceHandler {

    public Object getResource(String data) {
//      pattern should be "top, left, bottom, right"
        String[] args = getArgs(data);
        int top = getInt(args, 0);
        int left = getInt(args, 1);
        int bottom = getInt(args, 2);
        int right = getInt(args, 3);


        return new Insets(top, left, bottom, right);
    }

    private int getInt(String args[], int index) {
        return args.length>index? getInt(args[index]): 0;
    }

    private int getInt(String data) {
        try {
            return Integer.parseInt(data);
        } catch(Exception e) {
            System.err.println("Exception: " +e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }
}
