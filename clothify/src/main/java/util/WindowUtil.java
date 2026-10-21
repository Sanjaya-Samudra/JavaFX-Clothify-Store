package util;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.InputStream;

/**
 * Window & Stage Utility for Clothify Atelier POS & ERP.
 * Manages responsive screen fitting, visual bounds clamping, and automatic centering.
 */
public class WindowUtil {

    private static final double DEFAULT_PADDING_X = 24.0;
    private static final double DEFAULT_PADDING_Y = 32.0;

    /**
     * Fits and optimizes a Stage to the user's current display visual bounds upon load up,
     * ensuring no corners extend beyond the screen borders, taskbars, or docks.
     *
     * @param stage The Stage to configure
     * @param prefWidth Desired width if restored/floating
     * @param prefHeight Desired height if restored/floating
     * @param minWidth Absolute minimum width
     * @param minHeight Absolute minimum height
     * @param startMaximized If true, starts maximized so it snaps to fit the window seamlessly
     */
    public static void fitToWindow(Stage stage, double prefWidth, double prefHeight, double minWidth, double minHeight, boolean startMaximized) {
        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();

        double availableWidth = visualBounds.getWidth() - DEFAULT_PADDING_X;
        double availableHeight = visualBounds.getHeight() - DEFAULT_PADDING_Y;

        double targetWidth = Math.min(prefWidth, availableWidth);
        double targetHeight = Math.min(prefHeight, availableHeight);

        stage.setMinWidth(Math.min(minWidth, targetWidth));
        stage.setMinHeight(Math.min(minHeight, targetHeight));

        stage.setWidth(targetWidth);
        stage.setHeight(targetHeight);

        // Center precisely within visual bounds (accounting for OS taskbar position)
        double posX = visualBounds.getMinX() + Math.max(0, (visualBounds.getWidth() - targetWidth) / 2.0);
        double posY = visualBounds.getMinY() + Math.max(0, (visualBounds.getHeight() - targetHeight) / 2.0);
        stage.setX(posX);
        stage.setY(posY);

        applyAppIcon(stage);

        if (startMaximized) {
            stage.setMaximized(true);
        }
    }

    /**
     * Centers a modal or utility stage safely within the primary screen visual bounds.
     */
    public static void centerInVisualBounds(Stage stage, double prefWidth, double prefHeight) {
        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();

        double safeWidth = Math.min(prefWidth, visualBounds.getWidth() - DEFAULT_PADDING_X);
        double safeHeight = Math.min(prefHeight, visualBounds.getHeight() - DEFAULT_PADDING_Y);

        stage.setMinWidth(Math.min(760, safeWidth));
        stage.setMinHeight(Math.min(480, safeHeight));

        stage.setWidth(safeWidth);
        stage.setHeight(safeHeight);

        double posX = visualBounds.getMinX() + Math.max(0, (visualBounds.getWidth() - safeWidth) / 2.0);
        double posY = visualBounds.getMinY() + Math.max(0, (visualBounds.getHeight() - safeHeight) / 2.0);
        stage.setX(posX);
        stage.setY(posY);

        applyAppIcon(stage);
    }

    private static void applyAppIcon(Stage stage) {
        try {
            InputStream iconStream = WindowUtil.class.getResourceAsStream("/Images/clothify-logo.png");
            if (iconStream != null) {
                stage.getIcons().add(new Image(iconStream));
            }
        } catch (Exception ignored) {}
    }
}
