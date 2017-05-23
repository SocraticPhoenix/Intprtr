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

import com.gmail.socraticphoenix.intprtr.program.Variable;

import java.math.BigDecimal;
import java.util.List;

public enum Type {
    ARRAY(List.class) {
        @Override
        public boolean matches(Variable variable) {
            return variable.convertToList().isPresent();
        }
    },
    NUMBER(BigDecimal.class) {
        @Override
        public boolean matches(Variable variable) {
            return variable.convertToNumber().isPresent();
        }
    },
    STRING(String.class) {
        @Override
        public boolean matches(Variable variable) {
            return variable.convertToString().isPresent();
        }
    },
    NULL(Void.class) {
        @Override
        public boolean matches(Variable variable) {
            return variable.isNull();
        }
    },
    ANY(Object.class) {
        @Override
        public boolean matches(Variable variable) {
            return true;
        }
    };

    private Class type;

    Type(Class type) {
        this.type = type;
    }

    public abstract boolean matches(Variable variable);

    public static Type from(Class c) {
        for(Type t : values()) {
            if(t.type.equals(c)) {
                return t;
            }
        }

        return null;
    }

}
