package com.sergitxin.flexilearn;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.noconnor.junitperf.JUnitPerfInterceptor;
import com.github.noconnor.junitperf.JUnitPerfTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(JUnitPerfInterceptor.class)
class JUnitPerfSanityTest {

    @Test
    @JUnitPerfTest(threads = 2, durationMs = 1_000, warmUpMs = 100)
    void testRendimientoBasico() {
        // Operacion simple para validar la integracion de JUnitPerf.
        assertTrue((21 * 2) == 42);
    }
}
