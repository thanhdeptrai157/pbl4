package org.Client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class HistoryWeb {
    public static List<History> getHistoryWeb(int count){
        String source = "HistoryWeb";
        String command = "wevtutil qe Application /q:\"*[System[Provider[@Name='" + source + "']]]\" /f:text ";

        try {
            Process process = Runtime.getRuntime().exec(command);

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            Stack<String> stack = new Stack<>();

            while ((line = reader.readLine()) != null) {
                stack.push(line);
            }
            List<History> list = new ArrayList<>();
            for(int i = 0; i < count; ++i){
                list.add(getInfoHistory(stack));
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.err.println("Lệnh không thành công. Mã lỗi: " + exitCode);
            }

            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static History getInfoHistory(Stack<String> stack){
        History history = new History();
        history.setUrl(getInfor(stack, "url"));
        history.setDate(getInfor(stack, "Date"));
        return history;
    }

    private static String getInfor(Stack<String> stack, String info){
        while(!stack.peek().contains(info)){
            stack.pop();
        }
        return stack.pop();
    }
}
