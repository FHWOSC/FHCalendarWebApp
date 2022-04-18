package de.yonux.fhcalendarwebapp.task;

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.stream.Collectors;

public class FetchCustomizerTask implements Runnable {

    private static final String URL_CUSTOMIZER = "https://intern.fh-wedel.de/~splan/index.html?typ=benutzer_vz&layout=0";
    private String result;

    @Override
    public void run() {
        try
        {
            URL url = new URL(URL_CUSTOMIZER);
            URLConnection connection = url.openConnection();

            String text;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream())))
            {
                text = reader.lines().collect(Collectors.joining("\n"));
            }

            Document document = Jsoup.parse(text);
            Elements forms = document.getElementsByTag("form");
            if (forms.size() == 4) {
                Element form = forms.get(3);
                form.select("a").forEach(a -> a.replaceWith(new TextNode(a.text())));
                form.children().append("<input type=\"hidden\" name=\"filename\" value=\"benutzerdefiniert\">");
                form.attr("action", "download");
                this.result = form.outerHtml();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getResult() {
        return result;
    }
}
