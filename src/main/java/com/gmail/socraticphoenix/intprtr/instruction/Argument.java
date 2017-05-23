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
package com.gmail.socraticphoenix.intprtr.instruction;

import com.gmail.socraticphoenix.collect.coupling.Pair;
import com.gmail.socraticphoenix.intprtr.ProgramSystem;
import com.gmail.socraticphoenix.intprtr.program.Memory;
import com.gmail.socraticphoenix.intprtr.program.Variable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Argument {
    private Type[] types;
    private boolean optional;
    private boolean prefersStack;

    public Argument(Type[] types, boolean optional, boolean prefersStack) {
        this.types = types;
        this.optional = optional;
        this.prefersStack = prefersStack;
    }

    public Argument(Type type, boolean optional, boolean prefersStack) {
        this(new Type[]{type}, optional, prefersStack);
    }

    public static Argument of(Type... types) {
        return new Argument(types, false, false);
    }

    public static Argument of(boolean optional, boolean prefersStack, Type... types) {
        return new Argument(types, optional, prefersStack);
    }

    public static Argument any(boolean optional, boolean prefersStack) {
        return new Argument(Type.ANY, optional, prefersStack);
    }

    public static Argument any() {
        return Argument.any(false, false);
    }

    public static Argument number(boolean optional, boolean prefersStack) {
        return new Argument(Type.NUMBER, optional, prefersStack);
    }

    public static Argument number() {
        return Argument.number(false, false);
    }

    public static Argument string(boolean optional, boolean prefersStack) {
        return new Argument(Type.STRING, optional, prefersStack);
    }

    public static Argument string() {
        return Argument.string(false, false);
    }

    public static Argument array(boolean optional, boolean prefersStack) {
        return new Argument(Type.ANY, optional, prefersStack);
    }

    public static Argument array() {
        return Argument.array(false, false);
    }

    private static void resetBacklog(List<Variable> backLog, Memory memory) {
        for (Variable v : backLog) {
            memory.push(v);
        }

        backLog.clear();
    }

    public static List<Variable> collect(List<Argument> arguments, Memory memory) {
        List<Variable> backLog = new ArrayList<>();
        List<Variable> result = new ArrayList<>();

        for (Argument argument : arguments) {
            Variable var;
            if (!memory.getStack().isEmpty()) {
                boolean found = false;
                do {
                    var = memory.pop();
                    if (!argument.matches(var).getB()) {
                        backLog.add(var);
                    } else {
                        found = true;
                        break;
                    }
                } while (!memory.getStack().isEmpty());

                if (found) {
                    resetBacklog(backLog, memory);
                    result.add(var);
                    continue;
                }
            }

            if (argument.prefersStack) {
                Variable def = memory.defaultVal(argument.types[0]);
                if (!argument.matches(def).getB()) {
                    var = ProgramSystem.in(argument);
                } else {
                    var = def;
                }
            } else if (!argument.optional) {
                var = ProgramSystem.in(argument);
            } else {
                var = new Variable(null);
            }

            resetBacklog(backLog, memory);
            result.add(var);
        }

        return result;
    }

    public Pair<String, Boolean> matches(Variable var) {
        for (Type type : this.types) {
            if (type.matches(var)) {
                return Pair.of("Succesful match", true);
            }
        }

        return Pair.of("Type mismatch, expected one of " + Arrays.toString(this.types) + ", but got " + var.getType(), false);
    }

    public boolean isOptional() {
        return this.optional;
    }

}
