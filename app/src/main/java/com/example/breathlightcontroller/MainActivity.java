package com.example.breathlightcontroller;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    private SerialPort serialPort;
    private OutputStream outputStream;
    private boolean isGreen = true; // 当前颜色是否为绿色

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnChangeColor = findViewById(R.id.btn_change_color);

        try {
            // 打开串口
            serialPort = new SerialPort(new File("/dev/ttyS3"), 9600, 0);
            outputStream = serialPort.getOutputStream();

            // 初始化为绿色灯
            sendCommand("KEEP:GREEN:0:255");

        } catch (Exception e) {
            Toast.makeText(this, "串口打开失败: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
            return;
        }

        btnChangeColor.setOnClickListener(v -> {
            // 切换灯光颜色
            if (isGreen) {
                sendCommand("KEEP:RED:0:255");
            } else {
                sendCommand("KEEP:GREEN:0:255");
            }
            isGreen = !isGreen;
        });
    }

    private void sendCommand(String command) {
        try {
            if (outputStream != null) {
                // 添加换行符发送命令
                command += "\n";
                outputStream.write(command.getBytes());
                outputStream.flush();
                Toast.makeText(this, "命令发送: " + command, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "命令发送失败: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (outputStream != null) {
                outputStream.close();
            }
            if (serialPort != null) {
                serialPort.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
