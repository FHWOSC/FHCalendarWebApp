package de.yonux.fhcalendarwebapp.task;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.stream.Collectors;

public class FetchCoursesTableTask implements Runnable
{

    private static final String URL_COURSES = "https://intern.fh-wedel.de/~splan/index.html?sicht=1";

    private String result = null;

    @Override
    public void run()
    {
        try
        {
            URL url = new URL(URL_COURSES);
            URLConnection connection = url.openConnection();

            String text;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream())))
            {
                text = reader.lines().collect(Collectors.joining("\n"));
            }

            Document document = Jsoup.parse(text);
            Elements tables = document.getElementsByTag("table");
            if (tables.size() == 1) {
                Element table = tables.get(0);
                table.select("a").forEach(a -> {
                    String href = a.attr("href");
                    a.attr("href", href
                            .replaceFirst("(.*)\\?", "view.jsp?")
                            + "&filename=" + a.attr("title")
                    );
                });
                this.result = table.outerHtml();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getResult() {
        return result;
    }
}
