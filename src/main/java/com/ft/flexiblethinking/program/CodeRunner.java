package com.ft.flexiblethinking.program;

import com.ft.flexiblethinking.config.Config;
import org.springframework.util.DigestUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public class CodeRunner {
    enum ErrMSG {
        OK,
        WRONG_RESULT,
        TIME_OUT,
        COMPILE_ERROR
    };

    private final long compile_seconds = 10;
    private volatile static CodeRunner codeRunner = null;

    private String name = null;
    private String opts = null;
    private CodeRunner(String name, String opts) {
        this.name = name;
        this.opts = opts;
    }

    public static CodeRunner getCodeRunner() {
        if (codeRunner != null) return codeRunner;
        synchronized(CodeRunner.class) {
            if (codeRunner == null) {
                String name = Config.get("compiler");
                assert (!(name == null || name.isEmpty()));
                String options = Config.get("compile.opts");
                options = options == null ? "" : options;
                codeRunner = new CodeRunner(name, options);
            }
        }
        return codeRunner;
    }

    public ErrMSG run(String code, String input, String outputExpect, long nseconds) throws InterruptedException, IOException {
        String coderoot = Config.get("code.root");
        if (!coderoot.endsWith(File.separator))
            coderoot += File.separator;

        String tempFileName = DigestUtils.md5DigestAsHex(
                (code + "奝" + Thread.currentThread().getId() + "奝" + System.currentTimeMillis())
                        .getBytes(StandardCharsets.UTF_8));
        String curRoot = coderoot + tempFileName + File.separator;
        new File(curRoot).mkdir();

        ErrMSG ret = compile(curRoot, "a", code);
        if (ret.equals(ErrMSG.OK)) {
            ret = exec(curRoot, "a", input, outputExpect, nseconds);
        }
        new File(curRoot).delete();
        return ret;
    }

    public ErrMSG compile(String coderoot, String tempFileName, String code) throws InterruptedException, IOException {
        String codeFileName = coderoot + tempFileName;
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(codeFileName))));
        bw.write(code);
        bw.close();
        String exeFileName = coderoot + tempFileName + ".exe";
        String command = name + " " + opts + " " + codeFileName + " " + "-o" + " " + exeFileName;
        Process process = Runtime.getRuntime().exec(command);
        BufferedReader br = new BufferedReader(new InputStreamReader(new BufferedInputStream(process.getErrorStream())));
        String errMsg = null;
        if (!process.waitFor(compile_seconds, TimeUnit.SECONDS)) {
            process.destroy();
            return ErrMSG.COMPILE_ERROR;
        }

        boolean fail = false;
        String line = br.readLine();
        while (line != null) {
            if (line.indexOf("rror") >= 0) {
                return ErrMSG.COMPILE_ERROR;
            }
            line = br.readLine();
        }
        return ErrMSG.OK;
    }

    public ErrMSG exec(String coderoot, String tempFile, String input, String outputExpect, long nseconds) throws IOException, InterruptedException {
        String sh = Config.get("sh");
        sh = sh == null ? "sh" : sh;
        String exeFileName = coderoot + tempFile + ".exe";
        String command = sh + " " + exeFileName;

        ProcessBuilder pb = new ProcessBuilder();
        pb.directory(new File(coderoot)).command(command);
        Process process = pb.start();
        BufferedWriter pbw = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
        BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String result = "";

        if (input != null)
            pbw.write(input);

        if (!process.waitFor(nseconds, TimeUnit.SECONDS)) {
            process.destroy();
            return ErrMSG.TIME_OUT;
        }

        String line = br.readLine();
        while (line != null) {
            result += line + "\n";
            line = br.readLine();
        }

        result = result.trim();
        if (!result.equals(outputExpect.trim())) {
            return ErrMSG.WRONG_RESULT;
        } else {
            return ErrMSG.OK;
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        CodeRunner codeRunner = CodeRunner.getCodeRunner();
        String code = "#include<iostream>\n" +
                "using namespace std;\n" +
                "int main(int argc, char** argv)\n" +
                "{\n" +
                "\tcout<<\"Hello, world!!!\"<<endl;\n" +
                "\treturn 0;\n" +
                "}";
        String input = null;
        String outputExpect = "Hello, world!!!\n";

        System.out.println(codeRunner.run(code, input, outputExpect.trim(), 1).toString());
    }
}
