package test;

import javafx.application.Platform;
import javafx.scene.shape.SVGPath;

import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TestAllSectorsHoverBounds {
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
                int n = 3;
                double span = 360.0 / n;
                double start0 = 270.0 - (span / 2.0);
                double gapDelta = 1.8;
                double effectiveSpan = span - (2.0 * gapDelta);

                String[] names = {"ORDER (Top)", "PRODUCT (Lower Right)", "REPORT (Lower Left)"};

                for (int i = 0; i < n; i++) {
                    double baseStart = (start0 + i * span) % 360.0;
                    if (baseStart < 0) baseStart += 360.0;
                    double effectiveStart = (baseStart + gapDelta) % 360.0;

                    String normalPath = createAnnularSectorPath(0, 0, 150.0, 230.0, effectiveStart, effectiveSpan);
                    String hoverPath  = createAnnularSectorPath(0, 0, 150.0, 238.0, effectiveStart, effectiveSpan);

                    SVGPath sector = new SVGPath();
                    sector.setContent(normalPath);
                    var bNormal = sector.getBoundsInLocal();

                    sector.setContent(hoverPath);
                    var bHover = sector.getBoundsInLocal();

                    System.out.println("Sector " + i + ": " + names[i]);
                    System.out.println("  Normal: [minX=" + String.format("%.2f", bNormal.getMinX()) +
                            ", minY=" + String.format("%.2f", bNormal.getMinY()) +
                            ", maxX=" + String.format("%.2f", bNormal.getMaxX()) +
                            ", maxY=" + String.format("%.2f", bNormal.getMaxY()) + "]");
                    System.out.println("  Hover:  [minX=" + String.format("%.2f", bHover.getMinX()) +
                            ", minY=" + String.format("%.2f", bHover.getMinY()) +
                            ", maxX=" + String.format("%.2f", bHover.getMaxX()) +
                            ", maxY=" + String.format("%.2f", bHover.getMaxY()) + "]");

                    // Check top sector downward movement
                    if (i == 0) {
                        System.out.println("  Top Sector maxY difference (downward movement): " +
                                String.format("%.4f", (bHover.getMaxY() - bNormal.getMaxY())));
                        System.out.println("  Top Sector minY difference (upward expansion): " +
                                String.format("%.4f", (bHover.getMinY() - bNormal.getMinY())));
                    }
                }

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
