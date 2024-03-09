package com.example.demo8;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class MultiClockApp extends Application {
    private int currentClockIndex = 0;
    private ClockApp[] clockApps;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        clockApps = new ClockApp[10];
        for (int i = 0; i < clockApps.length; i++) {
            clockApps[i] = new ClockApp();
        }

        clockApps[currentClockIndex].start(primaryStage);
    }

    private void switchToNextClock(Stage primaryStage) {
        currentClockIndex = (currentClockIndex + 6) % clockApps.length;

        clockApps[currentClockIndex].start(primaryStage);
    }

    private class ClockApp {
        private TextField textField;
        private Label label;

        public ClockApp() {
            textField = new TextField();


            label = new Label();
        }

        public void start(Stage primaryStage) {
            // Tạo nút button để chuyển đến đồng hồ tiếp theo
            Button nextButton = new Button("Open");
            nextButton.setOnAction(event -> switchToNextClock(primaryStage));

            // Tạo scene chứa textField, label và nút button
            VBox root = new VBox(10, textField, label, nextButton);
            root.setAlignment(Pos.CENTER);
            root.setPadding(new Insets(10));
            Scene scene = new Scene(root, 400, 200);

            primaryStage.setTitle("Multi Clock App");
            primaryStage.setScene(scene);
            primaryStage.show();

            // Bắt đầu đồng hồ với textfield hiện tại
            startClock();
        }

        private void startClock() {
            // Tạo và chạy đồng hồ trên một luồng riêng biệt
            Clock clock = new Clock(label);
            Thread thread = new Thread(clock);
            thread.setDaemon(true);
            thread.start();
        }

        private void switchToNextClock(Stage primaryStage) {
            // Đóng cửa sổ hiện tại
            primaryStage.close();

            // Chuyển sang đồng hồ tiếp theo
            MultiClockApp.this.switchToNextClock(primaryStage);
        }

        private class Clock implements Runnable {
            private Label label; // Label để hiển thị giờ

            public Clock(Label label) {
                this.label = label;
            }

            @Override
            public void run() {
                try {
                    while (true) {
                        // Lấy thời gian hiện tại
                        Date now = new Date();
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                        sdf.setTimeZone(TimeZone.getTimeZone("GMT+" + currentClockIndex));
                        String time = sdf.format(now);

                        // Cập nhật label trên luồng giao diện JavaFX
                        javafx.application.Platform.runLater(() -> label.setText(time));

                        // Ngủ 1 giây
                        Thread.sleep(1000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}