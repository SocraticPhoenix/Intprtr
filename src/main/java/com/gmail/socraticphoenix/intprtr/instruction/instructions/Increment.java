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
package com.gmail.socraticphoenix.intprtr.instruction.instructions;

import com.gmail.socraticphoenix.collect.Items;
import com.gmail.socraticphoenix.intprtr.instruction.Argument;
import com.gmail.socraticphoenix.intprtr.instruction.Instruction;
import com.gmail.socraticphoenix.intprtr.program.Memory;
import com.gmail.socraticphoenix.intprtr.program.Variable;

import java.math.BigDecimal;
import java.util.List;

public class Increment implements Instruction {

    @Override
    public char id() {
        return 'i';
    }

    @Override
    public String name() {
        return "increment";
    }

    @Override
    public void exec(Memory memory, List<Variable> args) {
        BigDecimal a = args.get(0).convertToNumber().orElse(BigDecimal.ONE);
        memory.push(new Variable(a.add(BigDecimal.ONE)));
    }

    @Override
    public List<Argument> arguments() {
        return Items.buildList(Argument.number(true, true));
    }

}