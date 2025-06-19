package org.main;

import ErrorClasses.MyErrorListener;
import ErrorClasses.SemanticErrorCollector;

import IntermediateRepresentation.TACGenerator;
import IntermediateRepresentation.TACInstruction;
import IntermediateRepresentation.TACInstructionCollector;
import IntermediateRepresentation.TACOptimizer;
import LLVM.LLVMFileHandler;
import LLVM.LLVMOutput;
import LLVM.LLVMTranslator;
import SematicAnalisis.DeclarationCollector;

import SematicAnalisis.SemanticAnalizer;
import Symbols.SymbolTable;

import MiniPascalClasses.MiniPascalLexer;
import MiniPascalClasses.MiniPascalParser;


import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.nio.file.Files;
import java.util.Comparator;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.util.List;
import java.util.ListResourceBundle;
import java.util.stream.Stream;
import javax.swing.JFileChooser;
import static org.antlr.v4.runtime.CharStreams.fromFileName;



public class Main {
    public static final String ANSI_RESET = "\u001B[0m";
    public static void main(String[] args) {
        String input = "";
        File fichero = null;
        Stream<String> lineStream = null;
        try {
            JFileChooser filechooser = new JFileChooser("./");
            FileNameExtensionFilter filtro = new FileNameExtensionFilter("Archivo de Texto", "txt");
            filechooser.setFileFilter(filtro);
            int seleccion = filechooser.showOpenDialog(null);
            if (seleccion == JFileChooser.APPROVE_OPTION) {
                fichero = filechooser.getSelectedFile();
                input = fichero.getAbsolutePath();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        CharStream charStream = null;
        try{
            lineStream = Files.lines(fichero.toPath());
            charStream = fromFileName(input);
        }catch (Exception e){
            System.err.println("No se ha seleccionado un archivo");
            System.exit(1);
        }

        MiniPascalLexer lexer = new MiniPascalLexer(charStream);
        lexer.removeErrorListeners();
        lexer.addErrorListener(new MyErrorListener(true));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        MiniPascalParser parser = new MiniPascalParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(new MyErrorListener(false));
        ParseTree tree = parser.program_block();
        MyErrorListener.errors.sort(Comparator.comparingInt(MyErrorListener.MyError::line));
        MyErrorListener.errors.forEach(error -> {
            System.err.println(error.errorMsg());
        });
        MyVisitor visitor = new MyVisitor();
        String str = visitor.visit(tree);

        if(MyErrorListener.errors.size() == 0) {
            SymbolTable symbolTable = new SymbolTable();
            SemanticErrorCollector errorCollector = new SemanticErrorCollector();
            DeclarationCollector declarationCollector = new DeclarationCollector(symbolTable, errorCollector);
            declarationCollector.visit(tree);
            System.out.println("Declaration Collector Errors: " + errorCollector.getErrors().size());

            SemanticAnalizer semanticAnalizer = new SemanticAnalizer(symbolTable, errorCollector);
            semanticAnalizer.visit(tree);
            if(semanticAnalizer.getErrorCollector().getErrorsCount() == 0){
                System.out.println("No Semantic Analysis Errors");
                TACInstructionCollector tacInstructionCollector = new TACInstructionCollector();
                TACGenerator tacGenerator = new TACGenerator(tacInstructionCollector, symbolTable);
                tacGenerator.visit(tree);
                System.out.println("Intermediate Representation:");
                tacGenerator.printInstructions();
                tacGenerator.printTACInstructions();

                System.out.println();
                System.out.println("Optimized Intermediate Representation:");
                TACOptimizer optimizer = new TACOptimizer(tacInstructionCollector.getInstructions());
                List<TACInstruction> optimizedInstructions = optimizer.optimize();
                for (TACInstruction instruction : optimizedInstructions) {
                    System.out.println(instruction.toString());
                }

                LLVMTranslator llvmTranslator = new LLVMTranslator();
                LLVMFileHandler fileHandler = new LLVMFileHandler();
                LLVMOutput llvmOutput = new LLVMOutput();

                List<String> llvmIRCode = llvmTranslator.translateAll(optimizedInstructions);

                String outputFilename = input.substring(0, input.lastIndexOf('.')) + ".ll";
                fileHandler.writeToFile(outputFilename, llvmIRCode, input);

                llvmOutput.run(outputFilename, llvmIRCode);



            } else {
                String [] lines = lineStream.toArray(String[]::new);

                System.out.println("Semantic Analysis Errors: " + errorCollector.getErrors().size());
                errorCollector.getErrors().forEach(error -> {
                    String send = error.toString() + "\n";
                    send += lines[error.getLine() - 1] + "\n";
                    send += underline2(error.getColumn()) + "\n";
                    System.err.println(send);
                });
            }
        }
    }

    public static String underline2(int charPositionInLine) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < charPositionInLine; i++) {
            sb.append(" ");
        }
        sb.append("^");
        return sb.toString();
    }
}