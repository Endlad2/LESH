package ru.lesch.service;

import java.awt.Desktop;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public final class YandexSearch {

private YandexSearch() {}

public static void open(String query) {
try {
String q = URLEncoder.encode(query, StandardCharsets.UTF_8);
String url = "https://yandex.ru/search/?text=" + q + "&lr=21641&clid=2411726";
if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
Desktop.getDesktop().browse(URI.create(url));
} else {
String os = System.getProperty("os.name").toLowerCase();
Runtime rt = Runtime.getRuntime();
if (os.contains("win")) {
rt.exec(new String[]{"rundll32", "url.dll,FileProtocolHandler", url});
} else if (os.contains("mac")) {
rt.exec(new String[]{"open", url});
} else {
rt.exec(new String[]{"xdg-open", url});
}
}
} catch (Exception e) {
e.printStackTrace();
}
}
}