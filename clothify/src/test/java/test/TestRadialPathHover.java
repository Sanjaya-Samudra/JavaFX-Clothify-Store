package test;

import javafx.application.Platform;
import javafx.scene.shape.SVGPath;

import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TestRadialPathHover {
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
                // Top sector (Order Management in Cashier 3-sector mode): midAngle = 270
                double span = 120.0;
                double gapDelta = 1.8;
                double effectiveSpan = span - 2.0 * gapDelta; // 116.4 deg
                double start0 = 270.0 - 60.0; // 210.0
                double effectiveStart = start0 + gapDelta; // 211.8 deg

                String normalPath = createAnnularSectorPath(0, 0, 150.0, 230.0, effectiveStart, effectiveSpan);
                String hoverPath = createAnnularSectorPath(0, 0, 150.0, 238.0, effectiveStart, effectiveSpan);

                SVGPath path = new SVGPath();
                path.setContent(normalPath);

                System.out.println("Normal bounds: " + path.getBoundsInLocal());

                // Set hover path
                path.setContent(hoverPath);
                System.out.println("Hover bounds:  " + path.getBoundsInLocal());

                // Compare minY, maxY:
                // Normal minY should be around -230
                // Hover minY should be around -238 (expanded UPWARD, not downward!)
                double normalMinY = -230.0;
                double hoverMinY = path.getBoundsInLocal().getMinY();
                System.out.println("Hover expanded UPWARD by: " + (hoverMinY - normalMinY));

                // Check inner radius at top: (0, -150)
                System.out.println("Inner radius at (0, -150) contains: " + path.contains(0, -150));
                System.out.println("Inside hole at (0, -145) contains: " + path.contains(0, -145)); // should be false

            } catch (Throwable t) {
                t.printStackTrace();
            } finally {
                testLatch.countDown();
            }
        });

        testLatch.await(10, TimeUnit.SECONDS);
        System.exit(0);
    }

    private static String createAnnularSectorPath(double cx, double cy, double rInner, double rOuter, double startAngleDeg, double sweepAngleDeg) {
        double endAngleDeg = startAngleDeg + sweepAngleDeg;
        double a1Rad = Math.toRadians(startAngleDeg);
        double a2Rad = Math.toRadians(endAngleDeg);

        double x1Out = cx + rOuter * Math.cos(a1Rad);
        double y1Out = cy + rOuter * Math.sin(a1Rad);
        double x2Out = cx + rOuter * Math.cos(a2Rad);
        double y2Out = cy + rOuter * Math.sin(a2Rad);

        double x2In = cx + rInner * Math.cos(a2Rad);
        double y2In = cy + rInner * Math.sin(a2Rad);
        double x1In = cx + rInner * Math.cos(a1Rad);
        double y1In = cy + rInner * Math.sin(a1Rad);

        int largeArc = (sweepAngleDeg > 180.0) ? 1 : 0;

        return String.format(Locale.US,
                "M %.2f,%.2f A %.2f,%.2f 0 %d,1 %.2f,%.2f L %.2f,%.2f A %.2f,%.2f 0 %d,0 %.2f,%.2f Z",
                x1Out, y1Out, rOuter, rOuter, largeArc, x2Out, y2Out,
                x2In, y2In, rInner, rInner, largeArc, x1In, y1In);
    }
}
