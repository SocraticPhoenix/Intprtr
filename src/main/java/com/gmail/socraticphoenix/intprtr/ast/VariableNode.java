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
package com.gmail.socraticphoenix.intprtr.ast;

import com.gmail.socraticphoenix.intprtr.program.Memory;
import com.gmail.socraticphoenix.intprtr.program.Variable;

public class VariableNode implements MemberNode {
    private String var;
    private InstructionNode inst;
    private boolean set;

    public VariableNode(String var, InstructionNode inst) {
        this.var = var;
        this.inst = inst;
    }

    public VariableNode(String var) {
        this.var = var;
    }

    public VariableNode(String var, boolean set) {
        this.var = var;
        this.set = set;
    }

    @Override
    public void exec(Memory memory) {
        Variable variable = memory.get(this.var);
        if (this.inst != null) {
            memory.push(variable);
            this.inst.exec(memory);
            variable.setVal(memory.peek());
        } else if (this.set) {
            variable.setVal(memory.pop());
        } else {
            memory.push(variable);
        }
    }

}
