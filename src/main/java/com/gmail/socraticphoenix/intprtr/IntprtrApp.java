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
package com.gmail.socraticphoenix.intprtr;

import com.gmail.socraticphoenix.intprtr.ast.ProgramNode;
import com.gmail.socraticphoenix.intprtr.instruction.InstructionRegistry;
import com.gmail.socraticphoenix.intprtr.program.Memory;
import com.gmail.socraticphoenix.intprtr.program.ProgramParser;
import com.gmail.socraticphoenix.intprtr.program.Variable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class IntprtrApp {

    public static void main(String[] args) throws IOException {
        InstructionRegistry.registerDefaults();

        if (args.length < 2) {
            List<String> newArgs = new ArrayList<>();
            Collections.addAll(newArgs, args);
            Scanner sc = new Scanner(System.in);
            if (args.length == 0) {
                System.out.print("Enter rules file> ");
                newArgs.add(sc.nextLine());
            }

            System.out.print("Enter input program> ");
            newArgs.add(sc.nextLine());
            String lenString;
            int len = 0;
            while (true) {
                System.out.print("Enter number of arguments> ");
                lenString = sc.nextLine();
                try {
                    len = Integer.parseInt(lenString);
                    if (len < 0) {
                        System.out.println("Number of arguments must be >= 0.");
                    } else {
                        break;
                    }
                } catch (NumberFormatException e) {
                    System.out.println(lenString + " is not a number.");
                }
            }

            for (int i = 0; i < len; i++) {
                System.out.print("Enter input #" + (i + 1) + "> ");
                newArgs.add(sc.nextLine());
            }
            args = newArgs.toArray(new String[newArgs.size()]);
        }

        String rulesFile = args[0];
        String prog = args[1];

        Path path = Paths.get(rulesFile);
        if (!Files.exists(path)) {
            System.out.println("No file called \"" + rulesFile + "\"");
            return;
        }

        StringBuilder rulesBuilder = new StringBuilder();
        for (String k : Files.readAllLines(path)) {
            if (!k.startsWith("//") && !k.isEmpty()) {
                rulesBuilder.append(k).append("\n");
            }
        }

        String rules = rulesBuilder.toString();

        List<String> flags = new ArrayList<>();
        List<Variable> input = new ArrayList<>();
        for (int i = 2; i < args.length; i++) {
            String arg = args[i];
            if (arg.startsWith("-")) {
                flags.add(arg.replaceFirst("-", ""));
            } else {
                input.add(Variable.parse(arg));
            }
        }

        if (flags.contains("b")) {
            System.out.println("Byte count: " + rules.getBytes(StandardCharsets.UTF_8).length);
        }

        ProgramParser parser = new ProgramParser(rules);

        ProgramNode node = parser.parse();
        Memory memory = new Memory();
        node.execute(prog, memory);
    }

}
