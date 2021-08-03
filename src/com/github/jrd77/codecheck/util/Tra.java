package com.github.jrd77.codecheck.util;


/**
 * @author zhen.wang
 * @description TODO
 * @date 2021/7/20 20:42
 */
public class Tra {

    public static class ConsoleLineHandler implements LineHandler {
        public ConsoleLineHandler() {
        }

        public void handle(String line) {
            System.out.println(line);
        }
    }
}
