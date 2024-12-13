package com.sahuid.learnroom.utils;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ThrowUtil {

    public static void throwIf(boolean judge, Supplier<? extends RuntimeException> exception) {
        if (judge) {
            throw exception.get() ;
        }
    }
}
