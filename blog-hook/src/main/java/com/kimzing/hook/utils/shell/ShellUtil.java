package com.kimzing.hook.utils.shell;

/**
 * .
 *
 * @author KimZing - kimzing@163.com
 * @since 2020/1/3 14:55
 */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public final class ShellUtil {
    private static Logger log = LoggerFactory.getLogger(ShellUtil.class);

    private ShellUtil() {
    }

    public static void exec(String command) {
        Runtime runtime = Runtime.getRuntime();
        Process process = null;

        try {
            process = runtime.exec(new String[]{"/bin/sh", "-c", command});
            process.waitFor();
        } catch (IOException var11) {
            var11.printStackTrace();
        } catch (InterruptedException var12) {
            var12.printStackTrace();
        }

        InputStream inputStream = process.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String result = (String)reader.lines().reduce((sum, s) -> {
            return sum + "\n" + s;
        }).orElse("无标准输出信息");
        log.info(result);
        InputStream errorStream = process.getErrorStream();
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream));
        String errMsg = (String)errorReader.lines().reduce((sum, s) -> {
            return sum + "\n" + s;
        }).orElse(null);
        if (errMsg != null) {
            log.error(errMsg);
        }

        try {
            reader.close();
            inputStream.close();
            errorReader.close();
            errorStream.close();
        } catch (IOException var10) {
            var10.printStackTrace();
        }

    }
}

