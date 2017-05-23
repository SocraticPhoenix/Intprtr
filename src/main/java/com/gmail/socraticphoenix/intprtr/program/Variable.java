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

import com.gmail.socraticphoenix.collect.Items;
import com.gmail.socraticphoenix.intprtr.instruction.Type;
import com.gmail.socraticphoenix.parse.CharacterStream;
import com.gmail.socraticphoenix.parse.Strings;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

public class Variable {
    private static char[] digits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    private Object val;
    private Type type;

    public Variable(Object val) {
        this.val = val;
        this.type = val == null ? Type.NULL : Type.from(this.val.getClass());
    }

    public static boolean isArrayNext(CharacterStream stream) {
        return stream.isNext('[');
    }

    public static boolean isNumberNext(CharacterStream stream) {
        if (stream.isNext(digits)) {
            return true;
        } else if (stream.isNext('-')) {
            for (char c : digits) {
                if (stream.isNext("-" + c) || stream.isNext("-." + c)) {
                    return true;
                }
            }
        } else if (stream.isNext('.')) {
            for (char c : digits) {
                if (stream.isNext("." + c)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static Variable readNumber(CharacterStream stream) {
        boolean n = stream.isNext('-');
        stream.consume('-');
        String val = stream.nextUntil(c -> !Items.contains(c, digits));
        String dec = "0";
        if (stream.isNext('.')) {
            stream.consume('.');
            dec = stream.nextUntil(c -> !Items.contains(c, digits));
        }

        return new Variable(new BigDecimal((n ? "-" : "") + val + "." + dec));
    }

    public static Variable readString(CharacterStream stream) {
        stream.consume('"');
        Variable v = new Variable(stream.nextUntil(c -> c == '"', Strings.javaEscapeFormat()));
        stream.consume('"');
        return v;
    }

    public static Variable readArray(CharacterStream stream) {
        stream.consumeAll(' ');
        stream.consume('[');
        Stack<List<Variable>> constStack = new Stack<>();
        constStack.push(new ArrayList<>());
        while (true) {
            if (Variable.isNumberNext(stream)) {
                constStack.peek().add(Variable.readNumber(stream));
            } else if (stream.isNext(":")) {
                stream.consume(':');
                constStack.push(new ArrayList<>());
            } else if (stream.isNext(";") && constStack.size() > 1) {
                stream.consume(';');
                Variable arr = new Variable(constStack.pop());
                constStack.peek().add(arr);
            } else if (stream.isNext(']')) {
                stream.consume(']');
                break;
            } else {
                constStack.peek().add(Variable.readString(stream));
            }
            stream.consumeAll(' ');
        }

        while (constStack.size() > 1) {
            Variable arr = new Variable(constStack.pop());
            constStack.peek().add(arr);
        }

        return new Variable(constStack.pop());
    }

    public static Variable parse(CharacterStream stream) {
        if (Variable.isNumberNext(stream)) {
            return Variable.readNumber(stream);
        } else if (Variable.isArrayNext(stream)) {
            return Variable.readArray(stream);
        } else {
            return Variable.readString(stream);
        }
    }

    public static Variable parse(String k) {
        return Variable.parse(new CharacterStream(k));
    }

    public Type getType() {
        return this.type;
    }

    public String toString() {
        return this.val == null ? "null" : this.val instanceof String ? "\"" + Strings.escape((String) this.val) + "\"" : this.val.toString();
    }

    public Object getVal() {
        return this.val;
    }

    public Variable setVal(Object val) {
        this.val = val;
        this.type = Type.from(this.val.getClass());
        return this;
    }

    public boolean isNull() {
        return this.val == null;
    }

    public Optional<BigDecimal> getAsNumber() {
        return this.val instanceof BigDecimal ? Optional.of((BigDecimal) this.val) : Optional.empty();
    }

    public Optional<String> getAsString() {
        return this.val instanceof String ? Optional.of((String) this.val) : Optional.empty();
    }

    public Optional<List<Variable>> getAsList() {
        return this.val instanceof List ? Optional.of((List<Variable>) this.val) : Optional.empty();
    }

    public Optional<BigDecimal> convertToNumber() {
        return Optional.ofNullable(this.getAsNumber().orElseGet(() -> {
            Optional<String> k = this.getAsString();
            if (k.isPresent()) {
                try {
                    return new BigDecimal(k.get());
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            return null;
        }));
    }

    public Optional<String> convertToString() {
        return Optional.ofNullable(this.getAsString().orElse(this.getAsString().orElse(null)));
    }

    public Optional<List<Variable>> convertToList() {
        return Optional.ofNullable(this.getAsList().orElseGet(() -> {
            Optional<String> stringOptional = this.getAsString();
            if (stringOptional.isPresent()) {
                List<Variable> res = new ArrayList<>();
                for (char c : stringOptional.get().toCharArray()) {
                    res.add(new Variable(String.valueOf(c)));
                }
                return res;
            }

            Optional<BigDecimal> decimalOptional = this.getAsNumber();
            if (decimalOptional.isPresent()) {
                List<Variable> res = new ArrayList<>();
                BigInteger digits = decimalOptional.get().unscaledValue();
                boolean neg = digits.compareTo(BigInteger.ZERO) < 0;
                digits = digits.abs();
                do {
                    BigInteger[] resAndRem = digits.divideAndRemainder(BigInteger.TEN);
                    res.add(new Variable(resAndRem[1]));
                    digits = resAndRem[0];
                } while (digits.compareTo(BigInteger.ZERO) != 0);

                if (neg) {
                    Variable first = res.get(0);
                    first.setVal(first.getAsNumber().get().negate());
                }
                return res;
            }

            return null;
        }));
    }

    public String printableString() {
        return this.val instanceof String ? String.valueOf(this.val) : this.toString();
    }

}
