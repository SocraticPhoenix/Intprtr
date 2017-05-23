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

import com.gmail.socraticphoenix.intprtr.ast.InstructionNode;
import com.gmail.socraticphoenix.intprtr.ast.MemberNode;
import com.gmail.socraticphoenix.intprtr.ast.ProgramNode;
import com.gmail.socraticphoenix.intprtr.ast.RuleNode;
import com.gmail.socraticphoenix.intprtr.ast.ValueNode;
import com.gmail.socraticphoenix.intprtr.ast.VariableNode;
import com.gmail.socraticphoenix.intprtr.instruction.InstructionRegistry;
import com.gmail.socraticphoenix.parse.CharacterStream;

import java.util.ArrayList;
import java.util.List;

public class ProgramParser {
    private CharacterStream stream;

    public ProgramParser(String content) {
        this.stream = new CharacterStream(content);
    }

    public ProgramNode parse() {
        List<RuleNode> nodes = new ArrayList<>();
        while (this.stream.hasNext()) {
            nodes.add(this.parseRule());
        }

        return new ProgramNode(nodes);
    }

    public RuleNode parseRule() {
        char c = this.stream.next().get();
        List<MemberNode> nodes = new ArrayList<>();
        while (this.stream.hasNext()) {
            this.stream.consumeAll(' ');
            nodes.add(this.next());
            this.stream.consumeAll(' ');
            if(this.stream.isNext('\n')) {
                this.stream.consumeAll('\n');
                break;
            }
        }
        return new RuleNode(c, nodes);
    }

    public MemberNode next() {
        if(this.instructionIsNext()) {
            return this.nextInstruction();
        } else if (this.variableIsNext()) {
            return this.nextVar();
        } else {
            return this.nextValue();
        }
    }

    public boolean instructionIsNext() {
        return this.stream.hasNext() && InstructionRegistry.getInstruction(this.stream.peek().get()).isPresent();
    }

    public boolean variableIsNext() {
        return this.stream.isNext(c -> 'A' <= c && c <= 'Z');
    }

    public InstructionNode nextInstruction() {
        return new InstructionNode(InstructionRegistry.getInstruction(this.stream.next().get()).get());
    }

    public ValueNode nextValue() {
        return new ValueNode(Variable.parse(this.stream));
    }

    public VariableNode nextVar() {
        String var = String.valueOf(this.stream.next().get());
        if (this.instructionIsNext()) {
            return new VariableNode(var, this.nextInstruction());
        } else {
            if (this.stream.isNext('=')) {
                return new VariableNode(var, true);
            } else if (this.stream.isNext(' ')) {
                this.stream.consumeAll(' ');
                return new VariableNode(var, false);
            } else {
                return new VariableNode(var, false);
            }
        }
    }

}
