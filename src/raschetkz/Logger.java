/*
 * The MIT License
 *
 * Copyright 2017 Иван.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package raschetkz;

import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;

/**
 *
 * @author Иван
 */
public class Logger extends OutputStream {
    private String filePath="C:\\NetBeansLogs\\errLog.txt";
    private final Stage messageWindow;
    private char[] buffer;
    private Text text;
    private int iterator;

    Logger(){
        messageWindow=new Stage();
        messageWindow.setTitle("Error!");
        StackPane root=new StackPane();
        text=new Text();

        root.getChildren().add(text);
        Scene scene=new Scene(root,400,400);
        messageWindow.setScene(scene);
        buffer=new char[1024];
    }

    @Override
    public void write(int b) throws IOException {
        buffer[iterator]=(char)b;
        iterator++;
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath,true))) {
            bw.write((char)b);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public void errorLayout(){
        text.setText(String.valueOf(buffer,0,iterator));
        messageWindow.show();
        messageWindow.sizeToScene();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath,true))) {
            bw.newLine();
            bw.newLine();
            bw.write(LocalDateTime.now().toString());
            bw.newLine();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public void initLogs(){
        iterator=0;
    }
}

