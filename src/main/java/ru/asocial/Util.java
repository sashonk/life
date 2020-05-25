package ru.asocial;

import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

class Util {

    static byte[][] initArray(int width, int height){
        byte[][] array  = new byte[width][];
        for (int i = 0 ; i < width; i++) {
            array[i] = new byte[height];
            Arrays.fill(array[i], (byte)0);
        }
        return array;
    }

    static Figure parseFigure(String path) throws Exception{
        InputStream is = Util.class.getResourceAsStream("/" + path);
        List<String> lines = IOUtils.readLines(is, "utf-8");
        int height = lines.size();
        int width = lines.get(0).length();
        byte[][] buffer = initArray(width, height);
        int alive = 0;
        for (int i = 0; i<height; i++) {
            String line = lines.get(i);
            for (int j = 0; j < width; j++) {
                char c = line.charAt(j);
                byte state;
                if (c == 'O') {
                    state = 1;
                    alive++;
                }
                else if (c == '.') {
                    state = 0;
                }
                else {
                    throw new IllegalArgumentException("Unexpected char: " + c);
                }
                buffer[j][i] = state;
            }
        }
        return new Figure(width, height, buffer, alive);
    }

}
