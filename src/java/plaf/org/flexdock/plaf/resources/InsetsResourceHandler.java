/*
 * Created on 23.03.2005
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
