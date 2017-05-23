/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 socraticphoenix@gmail.com
 * Copyright (c) 2016 contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.gmail.socraticphoenix.intprtr.program;

import com.gmail.socraticphoenix.intprtr.instruction.Type;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class Memory {
    private Stack<Variable> stack;
    private Map<String, Variable> vars;

    public Memory() {
        this.stack = new Stack<>();
        this.vars = new HashMap<>();
    }

    public Stack<Variable> getStack() {
        return this.stack;
    }

    public Variable defaultVal(Type type) {
        switch (type) {
            case ARRAY:
                return new Variable(new ArrayList<Variable>());
            case NUMBER:
                return new Variable(BigDecimal.ZERO);
            case STRING:
                return new Variable("");
            default:
                throw new IllegalStateException("Unreachable code");
        }
    }

    public Variable pop() {
        if (this.stack.isEmpty()) {
            this.stack.push(new Variable(BigDecimal.ZERO));
        }

        return this.stack.pop();
    }

    public Variable peek() {
        if (this.stack.isEmpty()) {
            this.stack.push(new Variable(BigDecimal.ZERO));
        }

        return this.stack.peek();
    }

    public void push(Variable var) {
        this.stack.push(var);
    }

    public Variable get(String key) {
        if (!this.vars.containsKey(key)) {
            this.vars.put(key, new Variable(BigDecimal.ZERO));
        }

        return this.vars.get(key);
    }

    public void set(String key, Variable var) {
        this.vars.put(key, var);
    }

}
