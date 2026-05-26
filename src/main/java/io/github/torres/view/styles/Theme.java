package io.github.torres.view.styles;

import java.awt.Color;
import java.awt.Dimension;

public class Theme {

    // Colors
    public static final Color BACKGROUND_COLOR = new Color(245, 245, 245);

    public static final Color PANEL_COLOR = Color.WHITE;

    public static final Color PRIMARY_COLOR = new Color(52, 152, 219);

    public static final Color SUCCESS_COLOR = new Color(46, 204, 113);

    public static final Color DANGER_COLOR = new Color(231, 76, 60);

    public static final Color SECONDARY_COLOR = new Color(127, 140, 141);

    public static final Color BORDER_COLOR = new Color(220, 220, 220);

    // Sizes
    public static final Dimension BUTTON_SIZE = new Dimension(180, 45);

    public static final Dimension INPUT_SIZE = new Dimension(180, 38);

    // Spinner Configuration Constants
    public static final int SPINNER_INITIAL_VALUE = 1;
    public static final int SPINNER_MAX_VALUE = 1000;
    public static final int SPINNER_MIN_VALUE = 1;
    public static final int SPINNER_STEP = 1;

    // Numeric Limits
    public static final double MIN_PRICE = 0.01;
    public static final double MAX_PRICE = 999999.99;
    public static final int MAX_STOCK = 1000000;

    // String Limits
    public static final int MAX_PRODUCT_NAME_LENGTH = 100;
    public static final int MAX_DESCRIPTION_LENGTH = 1000;

    // Cart Constants
    public static final double CART_TOTAL_EPSILON = 0.01;

    // Cache Configuration
    public static final long CACHE_TIMEOUT_MS = 60000;

    // Table Configuration
    public static final int DEFAULT_TABLE_ROW_HEIGHT = 25;
    public static final int TABLE_COLUMN_MIN_WIDTH = 50;

    // UI Components
    public static final int DIALOG_MIN_WIDTH = 400;
    public static final int DIALOG_MIN_HEIGHT = 300;
    public static final int DIALOG_MAX_WIDTH = 1024;
    public static final int DIALOG_MAX_HEIGHT = 768;

    // Validation Constants
    public static final int RETRY_ATTEMPTS = 3;
    public static final int RETRY_DELAY_MS = 1000;

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private Theme() {
        // Prevent instantiation
    }
}
