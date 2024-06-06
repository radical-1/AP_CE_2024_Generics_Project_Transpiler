package Tests;

import Languages.C.C;
import Languages.Code;
import Languages.Java.Java;
import Languages.Rust.Rust;
import Transpiler.AbstractSyntaxTree;
import Transpiler.Transpiler;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import org.junit.Test;
import static org.junit.Assert.*;

public class Testcases {

    @Test
    public void testCTranslation() throws IOException, URISyntaxException {
        String cCode = new String(Files.readAllBytes(Paths.get(
                Objects.requireNonNull(getClass().getResource("test1.c")).toURI())));
        C c = new C(cCode);
        AbstractSyntaxTree tree = c.parseToAST();
        Java java = new Java(new Java(tree).generateCode());
        Rust rust = new Rust(new Rust(tree).generateCode());

        Transpiler<Code> transpiler = new Transpiler();
        transpiler.addCode(c);
        transpiler.addCode(java);
        transpiler.addCode(rust);
        List<Code> runnables = transpiler.getSimilarRunnables(c);
        assertEquals(runnables.size(), 3);
    }

    @Test
    public void testJavaTranslation() throws IOException, URISyntaxException {
        String javaCode = new String(Files.readAllBytes(Paths.get(
                Objects.requireNonNull(getClass().getResource("test2.j")).toURI())));
        Java java = new Java(javaCode);
        AbstractSyntaxTree tree = java.parseToAST();
        C c = new C(new C(tree).generateCode());
        Rust rust = new Rust(new Rust(tree).generateCode());
        Transpiler<Code> transpiler = new Transpiler();
        transpiler.addCode(c);
        transpiler.addCode(java);
        transpiler.addCode(rust);

        List<Code> runnables = transpiler.getSimilarRunnables(c);
        assertEquals(runnables.size(), 3);
    }

    @Test
    public void testRustTranslation() throws IOException, URISyntaxException {
        String rustCode = new String(Files.readAllBytes(Paths.get(
                Objects.requireNonNull(getClass().getResource("test3.rs")).toURI())));
        Rust rust = new Rust(rustCode);
        AbstractSyntaxTree tree = rust.parseToAST();
        Java java = new Java(new Java(tree).generateCode());
        C c = new C(new C(tree).generateCode());
        Transpiler<Code> transpiler = new Transpiler();
        transpiler.addCode(c);
        transpiler.addCode(java);
        transpiler.addCode(rust);
        List<Code> runnables = transpiler.getSimilarRunnables(c);
        assertEquals(runnables.size(), 3);
    }

    @Test
    public void testDifferentCodes() throws IOException, URISyntaxException {
        String cCode = new String(Files.readAllBytes(Paths.get(
                Objects.requireNonNull(getClass().getResource("test4.c")).toURI())));
        String javaCode = new String(Files.readAllBytes(Paths.get(
                Objects.requireNonNull(getClass().getResource("test4.j")).toURI())));
        String rustCode = new String(Files.readAllBytes(Paths.get(
                Objects.requireNonNull(getClass().getResource("test4.rs")).toURI())));
        C c = new C(cCode);
        Java java = new Java(javaCode);
        Rust rust = new Rust(rustCode);
        Transpiler<Code> transpiler = new Transpiler();
        transpiler.addCode(c);
        transpiler.addCode(java);
        transpiler.addCode(rust);
        List<Code> uniques = transpiler. getUniqueRunnables();
        assertEquals(c.parseToAST(), java.parseToAST());
        assertEquals(uniques.size(), 1);
    }
    @Test
    public void testForDebugging() throw IOExeption, URISyntaxException {
        String cCode = new String(Files.readAllBytes(Paths.get(
                Objects.requireNonNull(getClass().getResource("test1.c")).toURI())));
        String javaCode = new String(Files.readAllBytes(Paths.get(
                Objects.requireNonNull(getClass().getResource("test2.j")).toURI())));
        String rustCode = new String(Files.readAllBytes(Paths.get(
                Objects.requireNonNull(getClass().getResource("test3.rs")).toURI())));
        C c = new C(cCode);
        AbstractSyntaxTree treeForC = c.parseToAST();
        Java java = new Java(javaCode);
        AbstractSyntaxTree treeForJava = java.parseToAST();
        Rust rust = new Rust(rustCode);
        AbstractSyntaxTree treeForRust = rust.parseToAST();
        Transpiler<Code> transpilerForC = new Transpiler();
        transpilerForC.addCode(new Java(treeForC));
        transpilerForC.addCode(new Rust(treeForC));
        for(String code; transpilerForC.getCodes()) {
            System.out.println(code);
            System.out.println("..........................................");
        }
        System.out.println("/////////////////////////////////////////");
        Transpiler<Code> transpilerForJava = new Transpiler();
        transpilerForJava.addCode(new C(treeForC));
        transpilerForJava.addCode(new Rust(treeForC));
        for(String code; transpilerForJava.getCodes()) {
            System.out.println(code);
            System.out.println("..........................................");
        }
        System.out.println("/////////////////////////////////////////");
        Transpiler<Code> transpilerForRust = new Transpiler();
        transpilerForRust.addCode(new Java(treeForC));
        transpilerForRust.addCode(new C(treeForC));
        for(String code; transpilerForRust.getCodes()) {
            System.out.println(code);
            System.out.println("..........................................");
        }
        System.out.println("/////////////////////////////////////////");


}
