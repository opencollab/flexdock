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

import java.awt.Font;
import java.util.StringTokenizer;

import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

/**
 * @author Christopher Butler
 */
public class FontResourceHandler extends ResourceHandler {
    public static final String BASE_FONT_KEY = "Panel.font";

    public Object getResource(String fontData) {
        if(fontData==null) {
            return null;
        }

        String name = null;
        int style = -1;
        int size = -1;

        if(!fontData.endsWith(",")) {
            fontData += ",";
        }

        StringTokenizer st = new StringTokenizer(fontData, ",");
        for(int i=0; st.hasMoreTokens(); i++) {
            switch(i) {
                case 0:
                    name = getFontName(st.nextToken());
                    break;
                case 1:
                    style = getInt(st.nextToken(), Font.PLAIN);
                    break;
                case 2:
                    size = getInt(st.nextToken(), 10);
                    break;
            }
        }

        FontUIResource defaultFont = (FontUIResource)UIManager.getDefaults().getFont(BASE_FONT_KEY);
        if(name==null) {
            name = defaultFont.getName();
        }
        if(style==-1) {
            style = defaultFont.getStyle();
        }
        if(size==-1) {
            size = defaultFont.getSize();
        }

        return new FontUIResource(name, style, size);

    }

    private String getFontName(String data) {
        data = data==null? null: data.trim();
        return data==null || data.length()==0? null: data;
    }

    private int getInt(String data, int defaultValue) {
        data = data==null? "": data.trim();
        try {
            return Integer.parseInt(data);
        } catch(NumberFormatException e) {
            return defaultValue;
        }
    }
}
