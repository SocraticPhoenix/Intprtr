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

import com.gmail.socraticphoenix.intprtr.instruction.instructions.Add;
import com.gmail.socraticphoenix.intprtr.instruction.instructions.CharCodepointConvert;
import com.gmail.socraticphoenix.intprtr.instruction.instructions.Increment;
import com.gmail.socraticphoenix.intprtr.instruction.instructions.Print;
import com.gmail.socraticphoenix.intprtr.instruction.instructions.Remainder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InstructionRegistry {
    private static Map<Character, Instruction> instructions = new HashMap<>();

    public static void register(Instruction instruction) {
        char key = instruction.id();
        if (instructions.containsKey(key)) {
            throw new IllegalStateException("id '" + key + "' already occupied by " + instructions.get(key).name() + ". (error while attempting to register " + instruction.name() + ")");
        } else {
            instructions.put(key, instruction);
        }
    }

    public static Optional<Instruction> getInstruction(char c) {
        return Optional.ofNullable(instructions.get(c));
    }

    private static void r(Instruction... instructions) {
        for (Instruction i : instructions) {
            register(i);
        }
    }

    public static void registerDefaults() {
        r(new Add(),
                new Remainder(),
                new Print(),
                new Increment(),
                new CharCodepointConvert());
    }


}
