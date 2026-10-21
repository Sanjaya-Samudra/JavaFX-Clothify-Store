package test;

import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.StrokeType;

import java.util.Locale;
import java.util.concurrent.CountDownLatch;

public class TestLowerSectorNoDownwardExpansion {
    public static void main(String[] args) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        try { Platform.startup(latch::countDown); } catch (Exception e) { latch.countDown(); }
        latch.await();

        Platform.runLater(() -> {
            double startAngle = 331.8;
            double sweepAngle = 116.4;

            String normalPath = createAnnularSectorPath(0, 0, 150.0, 230.0, startAngle, sweepAngle);
            SVGPath normalSector = new SVGPath();
            normalSector.setContent(normalPath);
            normalSector.setLayoutX(300.0);
            normalSector.setLayoutY(270.0);

            // Hover: rOuter stays 230.0, rInner becomes 144.0 (expands inward), stroke INSIDE, effect null
            String hoverPath = createAnnularSectorPath(0, 0, 144.0, 230.0, startAngle, sweepAngle);
            SVGPath hoverSector = new SVGPath();
            hoverSector.setContent(hoverPath);
            hoverSector.setLayoutX(300.0);
            hoverSector.setLayoutY(270.0);
            hoverSector.setStroke(Color.web("#f59e0b"));
            hoverSector.setStrokeWidth(3.0);
            hoverSector.setStrokeType(StrokeType.INSIDE);

            Group root = new Group(normalSector, hoverSector);
            Scene scene = new Scene(root, 600, 600);
            root.applyCss();
            root.layout();

            System.out.printf("Normal Sector bounds: minY=%.2f, maxY=%.2f, height=%.2f\n",
                    normalSector.getBoundsInParent().getMinY(),
                    normalSector.getBoundsInParent().getMaxY(),
                    normalSector.getBoundsInParent().getHeight());

            System.out.printf("Hover (rOut=230, rIn=144, Stroke INSIDE): minY=%.2f, maxY=%.2f, height=%.2f\n",
                    hoverSector.getBoundsInParent().getMinY(),
                    hoverSector.getBoundsInParent().getMaxY(),
                    hoverSector.getBoundsInParent().getHeight());

            System.out.printf("DELTA in maxY = %.4f px\n", hoverSector.getBoundsInParent().getMaxY() - normalSector.getBoundsInParent().getMaxY());

            Platform.exit();
        });
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
