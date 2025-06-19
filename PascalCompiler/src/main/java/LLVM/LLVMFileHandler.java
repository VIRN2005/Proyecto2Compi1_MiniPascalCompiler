package LLVM;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class LLVMFileHandler {

    /**
     * Writes a list of LLVM IR lines to a specified file.
     * @param filename The name of the file to write to.
     * @param lines The LLVM IR lines to be written.
     */
    public void writeToFile(String filename, List<String> lines, String inputFilename) {
        // Using try-with-resources to ensure that resources are closed after the program is finished
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writeHeader(writer, filename, inputFilename);
            writeIRLines(writer, lines);
            writeFooter(writer);
        } catch (IOException e) {
            System.err.println("Failed to write to file: " + filename);
            e.printStackTrace();
        }
    }

    private void writeHeader(BufferedWriter writer, String filename, String inputFilename) throws IOException {
        writer.write("; ModuleID = 'MiniPascal'\n");
        writer.write("source_filename = \"" + inputFilename + "\"\n");
        writer.write("target datalayout = \"e-m:e-p270:32:32-p271:32:32-p272:64:64-i64:64-f80:128-n8:16:32:64-S128\"\n");
        writer.write("target triple = \"x86_64-pc-linux-gnu\"\n\n");
    }

    private void writeIRLines(BufferedWriter writer, List<String> lines) throws IOException {
        for (String line : lines) {
            writer.write(line + "\n");
        }
    }

    private void writeFooter(BufferedWriter writer) throws IOException {
        writer.write("\n; Function Attrs: noinline nounwind optnone uwtable\n");
        writer.write("declare i32 @printf(i8*, ...) #0\n\n");
        writer.write("attributes #0 = { noinline nounwind optnone uwtable \"correctly-rounded-divide-sqrt-fp-math\"=\"false\" \"disable-tail-calls\"=\"false\" \"frame-pointer\"=\"all\" \"less-precise-fpmad\"=\"false\" \"min-legal-vector-width\"=\"0\" \"no-infs-fp-math\"=\"false\" \"no-jump-tables\"=\"false\" \"no-nans-fp-math\"=\"false\" \"no-signed-zeros-fp-math\"=\"false\" \"no-trapping-math\"=\"false\" \"stack-protector-buffer-size\"=\"8\" \"target-cpu\"=\"x86-64\" \"target-features\"=\"+cx8,+fxsr,+mmx,+sse,+sse2,+x87\" \"unsafe-fp-math\"=\"false\" \"use-soft-float\"=\"false\" }\n\n");
        writer.write("!llvm.module.flags = !{!0}\n");
        writer.write("!llvm.ident = !{!1}\n\n");
        writer.write("!0 = !{i32 1, !\"wchar_size\", i32 4}\n");
        writer.write("!1 = !{!\"clang version 10.0.0-4ubuntu1 \"}\n");
    }
}
