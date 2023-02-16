package ru.serykhd.hh.cities;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import ru.serykhd.mysql.Database;
import ru.serykhd.mysql.DatabaseCredentials;
import ru.serykhd.mysql.impl.MySQL;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class HHCities {

    public static void main(String[] args) throws IOException {
        HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create("https://hh.ru/area_switcher")).build();

        // (span, span,area-switcher-title area-switcher-title_9, span) > (span, span,area-switcher-title area-switcher-***, span)

        Database database = new MySQL(new DatabaseCredentials().setDatabase("sqlconnector").setPassword("root"));

        HttpClient.newHttpClient().sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(s -> {
                  //  System.out.println(s);
                    Document document = Jsoup.parse(s);

                    AtomicInteger lastCountryId = new AtomicInteger();

                    AtomicBoolean bb = new AtomicBoolean(false);

                    document.getAllElements().forEach(element -> {
                        if (element.className().equals("bloko-gap bloko-gap_top")) {
                            bb.set(true);
                        }

                        if (!bb.get()) {
                            return;
                        }

                        if (element.className().startsWith("area-switcher-title area-switcher-")) {
                            System.out.println(element.text());

                            try {
                                lastCountryId.set(database.executeUpdate("INSERT INTO `countrys`(`country`) VALUES ('" + element.text() + "')").get().getGeneratedKey());
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            } catch (ExecutionException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        if (element.className().equals("area-switcher-city")) {
                         //   System.out.println(element.text());



                            try { // IGNORE потому что почему на hh.ru Александровка два раза
                                database.executeVoidUpdate("INSERT IGNORE INTO `cities`(`countryId`, `city`) VALUES ("+ lastCountryId + ", '" + element.text() + "')").get();
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            } catch (ExecutionException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
                }).join();
    }
}
