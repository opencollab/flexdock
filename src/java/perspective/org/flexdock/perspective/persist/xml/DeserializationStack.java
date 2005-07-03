package org.flexdock.perspective.persist.xml;

import java.util.Stack;

public class DeserializationStack {

    private Stack m_stack = new Stack();
 
    public Object popObject() {
        return m_stack.pop();
    }
    
    public void pushObject(Object object) {
        m_stack.push(object);
    }
    
    public Object peekObject() {
        return m_stack.peek();
    }
    
}
