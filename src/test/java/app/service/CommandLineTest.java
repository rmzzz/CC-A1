package app.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

class CommandLineTest {

    @BeforeEach
    void setUp() {
    }

    @Test
    void fromCommandLine() {
    }

    @Test
    void getUrl() {
    }

    @Test
    void getDepth() {
    }

    @Test
    void getTargetLanguage() {
    }

    @Test
    void isValid() {
    }

    @Test
    void printUsage() {
        PrintStream out = System.out;
        try {
            PrintStream spied = Mockito.spy(out);
            System.setOut(spied);

            new CommandLine().printUsage();

            ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
            Mockito.verify(spied).println(stringCaptor.capture());

            assertTrue(stringCaptor.getValue().contains("url"));
            assertTrue(stringCaptor.getValue().contains("depth"));
            assertTrue(stringCaptor.getValue().contains("lang"));
        } finally {
            System.setOut(out);
        }
    }
}