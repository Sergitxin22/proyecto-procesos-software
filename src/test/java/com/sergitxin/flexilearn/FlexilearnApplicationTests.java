package com.sergitxin.flexilearn;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MockitoSanityTest {

    @Mock
    List<String> list;

    @Test
    void testDependenciaMockito() {
        when(list.get(0)).thenReturn("test");

        assertEquals("test", list.get(0));
        verify(list).get(0);
    }
}