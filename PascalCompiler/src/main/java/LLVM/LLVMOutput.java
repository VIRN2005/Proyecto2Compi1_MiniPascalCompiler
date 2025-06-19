package LLVM;

import java.io.*;
import java.util.List;

public class LLVMOutput {

    /**
     * Compiles LLVM IR stored in a file into an executable using Clang.
     * @param irFilename The filename of the LLVM IR code.
     */
    public String run(String irFilename, List<String> llvmIRCode) {
        // Write the LLVM IR code to a file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(irFilename))) {
            for (String line : llvmIRCode) {
                writer.write(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Error writing LLVM IR code to file.";
        }


        // Compile the LLVM IR code using Clang
        try {
            // Create the Clang command
            String command = "cd \"C:\\Users\\wilme\\Downloads\\PascalCompiler_Compi2\\PascalCompiler\\Programs\" &&";
            //Get Only the name of the file
            String name = irFilename.substring(irFilename.lastIndexOf("\\") + 1);
            String wsl = command + " wsl lli \"" + name + "\"";
            System.out.println(wsl);

            // Execute the Clang command

            ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", wsl);
            processBuilder.redirectErrorStream(true); // Combine standard output and error
            Process process = processBuilder.start();

            // Capture the output
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            System.out.println("Output:");
            String output = "";
            while ((line = reader.readLine()) != null) {
                output += line + "\n";
            }

            // Wait for the process to complete and get the exit code
            int exitCode = process.waitFor();
            output += "Process exited with code: " + exitCode;
            return output;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return "Error compiling LLVM IR code.";
    }
}
