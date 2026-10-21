package util;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.InputStream;
import java.util.Optional;

/**
 * Luxury Haute Couture Alert & Dialog System for Clothify.
 * Features vector SVG badges, soft card elevation, polished typography,
 * and eliminates duplicate icons or unrendered emoji boxes on Windows.
 */
public class AlertUtil {

    public static void showSuccess(String title, String message) {
        showCustomDialog(
                title,
                message,
                "M 16 26 L 22 32 L 34 18",
                "#059669",
                "#ecfdf5",
                "#a7f3d0",
                "Got it"
        );
    }

    public static void showInfo(String title, String message) {
        showCustomDialog(
                title,
                message,
                "M 25 16 L 25 18 M 25 23 L 25 34",
                "#4f46e5",
                "#eef2ff",
                "#c7d2fe",
                "OK"
        );
    }

    public static void showError(String title, String message) {
        showCustomDialog(
                title,
                message,
                "M 18 18 L 32 32 M 32 18 L 18 32",
                "#dc2626",
                "#fef2f2",
                "#fecaca",
                "Dismiss"
        );
    }

    public static void showWarning(String title, String message) {
        showCustomDialog(
                title,
                message,
                "M 25 15 L 25 28 M 25 34 L 25 36",
                "#d97706",
                "#fffbeb",
                "#fde68a",
                "Understood"
        );
    }

    public static boolean showConfirmation(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.initStyle(StageStyle.DECORATED);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setTitle("Clothify — " + title);
        alert.setGraphic(null);

        DialogPane pane = alert.getDialogPane();
        pane.setGraphic(null);
        pane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        pane.setStyle("-fx-background-color: #ffffff; -fx-padding: 24; -fx-background-radius: 16; -fx-border-color: #e2e8f0; -fx-border-radius: 16; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(15, 23, 42, 0.12), 16, 0, 0, 4);");
        try {
            pane.getStylesheets().add(AlertUtil.class.getResource("/modern-theme.css").toExternalForm());
        } catch (Exception ignored) {}

        HBox contentBox = new HBox(18);
        contentBox.setAlignment(Pos.TOP_LEFT);

        StackPane iconBadge = createSvgBadge(
                "M 19 20 C 19 14 31 14 31 21 C 31 25 25 27 25 30 M 25 35 L 25 36",
                "#0284c7",
                "#f0f9ff",
                "#bae6fd"
        );

        VBox textBox = new VBox(6);
        HBox.setHgrow(textBox, Priority.ALWAYS);

        Label lblTitle = new Label(header != null && !header.isEmpty() ? header : title);
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        lblTitle.setTextFill(Color.web("#0f172a"));

        Label lblContent = new Label(content);
        lblContent.setFont(Font.font("Segoe UI", 13.5));
        lblContent.setTextFill(Color.web("#475569"));
        lblContent.setWrapText(true);
        lblContent.setMaxWidth(380);

        textBox.getChildren().addAll(lblTitle, lblContent);
        contentBox.getChildren().addAll(iconBadge, textBox);
        pane.setContent(contentBox);

        Button okButton = (Button) pane.lookupButton(ButtonType.OK);
        if (okButton != null) {
            okButton.setStyle("-fx-background-color: #0f172a; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px; -fx-background-radius: 20; -fx-padding: 8 24; -fx-cursor: hand;");
            okButton.setText("Confirm");
            okButton.setOnMouseEntered(e -> okButton.setStyle("-fx-background-color: #1e293b; -fx-text-fill: #f59e0b; -fx-font-weight: bold; -fx-font-size: 13px; -fx-background-radius: 20; -fx-padding: 8 24; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(15, 23, 42, 0.25), 8, 0, 0, 2);"));
            okButton.setOnMouseExited(e -> okButton.setStyle("-fx-background-color: #0f172a; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px; -fx-background-radius: 20; -fx-padding: 8 24; -fx-cursor: hand;"));
        }

        Button cancelButton = (Button) pane.lookupButton(ButtonType.CANCEL);
        if (cancelButton != null) {
            cancelButton.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #475569; -fx-font-weight: bold; -fx-font-size: 13px; -fx-background-radius: 20; -fx-padding: 8 20; -fx-cursor: hand;");
            cancelButton.setText("Cancel");
            cancelButton.setOnMouseEntered(e -> cancelButton.setStyle("-fx-background-color: #e2e8f0; -fx-text-fill: #0f172a; -fx-font-weight: bold; -fx-font-size: 13px; -fx-background-radius: 20; -fx-padding: 8 20; -fx-cursor: hand;"));
            cancelButton.setOnMouseExited(e -> cancelButton.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #475569; -fx-font-weight: bold; -fx-font-size: 13px; -fx-background-radius: 20; -fx-padding: 8 20; -fx-cursor: hand;"));
        }

        applyWindowIcon(alert);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    private static void showCustomDialog(String title, String message, String svgPath, String primaryColor, String bgColor, String borderColor, String buttonText) {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.initStyle(StageStyle.DECORATED);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setTitle("Clothify — " + title);
        alert.setGraphic(null);

        DialogPane pane = alert.getDialogPane();
        pane.setGraphic(null);
        pane.getButtonTypes().add(ButtonType.OK);
        pane.setStyle("-fx-background-color: #ffffff; -fx-padding: 24; -fx-background-radius: 16; -fx-border-color: #e2e8f0; -fx-border-radius: 16; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(15, 23, 42, 0.12), 16, 0, 0, 4);");
        try {
            pane.getStylesheets().add(AlertUtil.class.getResource("/modern-theme.css").toExternalForm());
        } catch (Exception ignored) {}

        HBox contentBox = new HBox(18);
        contentBox.setAlignment(Pos.TOP_LEFT);

        StackPane iconBadge = createSvgBadge(svgPath, primaryColor, bgColor, borderColor);

        VBox textBox = new VBox(6);
        HBox.setHgrow(textBox, Priority.ALWAYS);

        Label lblTitle = new Label(title);
        lblTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        lblTitle.setTextFill(Color.web("#0f172a"));

        Label lblMessage = new Label(message);
        lblMessage.setFont(Font.font("Segoe UI", 13.5));
        lblMessage.setTextFill(Color.web("#475569"));
        lblMessage.setWrapText(true);
        lblMessage.setMaxWidth(390);

        textBox.getChildren().addAll(lblTitle, lblMessage);
        contentBox.getChildren().addAll(iconBadge, textBox);
        pane.setContent(contentBox);

        Button okButton = (Button) pane.lookupButton(ButtonType.OK);
        if (okButton != null) {
            okButton.setStyle(String.format("-fx-background-color: %s; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px; -fx-background-radius: 20; -fx-padding: 8 26; -fx-cursor: hand;", primaryColor));
            okButton.setText(buttonText);
            okButton.setOnMouseEntered(e -> okButton.setStyle(String.format("-fx-background-color: %s; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px; -fx-background-radius: 20; -fx-padding: 8 26; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 8, 0, 0, 2);", primaryColor)));
            okButton.setOnMouseExited(e -> okButton.setStyle(String.format("-fx-background-color: %s; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px; -fx-background-radius: 20; -fx-padding: 8 26; -fx-cursor: hand;", primaryColor)));
        }

        applyWindowIcon(alert);
        alert.show();
    }

    private static StackPane createSvgBadge(String svgPathData, String strokeColor, String bgColor, String borderColor) {
        StackPane badge = new StackPane();
        badge.setPrefSize(50, 50);
        badge.setMinSize(50, 50);
        badge.setMaxSize(50, 50);

        Circle circle = new Circle(25);
        circle.setFill(Color.web(bgColor));
        circle.setStroke(Color.web(borderColor));
        circle.setStrokeWidth(1.5);

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(15, 23, 42, 0.08));
        shadow.setRadius(8);
        shadow.setOffsetY(2);
        circle.setEffect(shadow);

        SVGPath path = new SVGPath();
        path.setContent(svgPathData);
        path.setStroke(Color.web(strokeColor));
        path.setStrokeWidth(3.0);
        path.setStrokeLineCap(StrokeLineCap.ROUND);
        path.setStrokeLineJoin(StrokeLineJoin.ROUND);
        path.setFill(Color.TRANSPARENT);

        badge.getChildren().addAll(circle, path);
        return badge;
    }

    private static void applyWindowIcon(Alert alert) {
        try {
            DialogPane pane = alert.getDialogPane();
            if (pane.getScene() != null && pane.getScene().getWindow() instanceof Stage) {
                Stage stage = (Stage) pane.getScene().getWindow();
                InputStream is = AlertUtil.class.getResourceAsStream("/Images/logo-background-removed-nav.png");
                if (is != null) {
                    stage.getIcons().add(new Image(is));
                }
            }
        } catch (Exception ignored) {
        }
    }
}
