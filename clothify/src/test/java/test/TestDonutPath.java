package test;

import javafx.application.Platform;
import javafx.scene.shape.SVGPath;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TestDonutPath {
    public static void main(String[] args) throws Exception {
        CountDownLatch startupLatch = new CountDownLatch(1);
        try {
            Platform.startup(startupLatch::countDown);
        } catch (IllegalStateException e) {
            startupLatch.countDown();
        }
        startupLatch.await(5, TimeUnit.SECONDS);

        CountDownLatch testLatch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                // Annular ring path from rInner=150 to rOuter=230 around (0, 0)
                String donutPath = "M 0,-230.00 A 230.00,230.00 0 1,1 0,230.00 A 230.00,230.00 0 1,1 0,-230.00 " +
                                  "M 0,-150.00 A 150.00,150.00 0 1,0 0,150.00 A 150.00,150.00 0 1,0 0,-150.00 Z";
                SVGPath path = new SVGPath();
                path.setContent(donutPath);

                System.out.println("Donut bounds: " + path.getBoundsInLocal());

                // Test points at radius 190 (inside the ring):
                System.out.println("Top (0, -190): " + path.contains(0, -190));
                System.out.println("Bottom (0, 190): " + path.contains(0, 190));
                System.out.println("Right (190, 0): " + path.contains(190, 0));
                System.out.println("Left (-190, 0): " + path.contains(-190, 0));
                System.out.println("Diagonal (134, 134): " + path.contains(134.35, 134.35));

                // Test points inside inner hole (< 150):
                System.out.println("Center (0, 0) [should be false]: " + path.contains(0, 0));
                System.out.println("Inner (0, 100) [should be false]: " + path.contains(0, 100));

                // Test points outside ring (> 230):
                System.out.println("Outside (0, 250) [should be false]: " + path.contains(0, 250));

            } catch (Throwable t) {
                t.printStackTrace();
            } finally {
                testLatch.countDown();
            }
        });

        testLatch.await(10, TimeUnit.SECONDS);
        System.exit(0);
    }
}
