package ru.lesch.service;

import java.awt.Desktop;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public final class YandexSearch {

    private YandexSearch() {
    }

    /**
     * Opens Yandex Video search. Returns false if no supported browser launcher
     * could be started on the current system.
     */
    public static boolean openVideo(String query) {
        if (query == null || query.isBlank()) {
            return false;
        }

        String encoded = URLEncoder.encode(query.trim(), StandardCharsets.UTF_8);
        String url = "https://yandex.ru/video/search?text=" + encoded;

        try {
            URI uri = URI.create(url);
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.BROWSE)) {
                    desktop.browse(uri);
                    return true;
                }
            }

            String os = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
            ProcessBuilder builder;
            if (os.contains("win")) {
                builder = new ProcessBuilder("rundll32", "url.dll,FileProtocolHandler", url);
            } else if (os.contains("mac")) {
                builder = new ProcessBuilder("open", url);
            } else {
                builder = new ProcessBuilder("xdg-open", url);
            }
            builder.start();
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }
}
