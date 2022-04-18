package de.yonux.fhcalendarwebapp;

import de.yonux.fhcalendarwebapp.task.CalendarParserTask;
import de.yonux.fhcalendarwebapp.task.NameFormat;
import de.yonux.fhcalendarwebapp.task.RoomFormat;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

@WebServlet(name = "downloadServlet", value = "/download")
public class DownloadServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        String filename = req.getParameter("filename");
        NameFormat nameFormat = NameFormat.valueOf(req.getParameter("nameformat").toUpperCase());
        RoomFormat roomFormat = RoomFormat.valueOf(req.getParameter("roomformat").toUpperCase());

        String url;
        String query = req.getQueryString();
        String link = req.getParameter("url");
        if (link != null) {
            url = link;
        } else {
            System.out.println("DownloadServlet.doGet " + query);
            url = "https://intern.fh-wedel.de/~splan/index.html?" + query;
        }

        resp.setContentType("APPLICATION/OCTET-STREAM");
        resp.setHeader("Content-Disposition","attachment; filename=\"" + filename + ".ics\"");

        try (OutputStream output = resp.getOutputStream()) {
            CalendarParserTask calendarParser = new CalendarParserTask(url, output, nameFormat, roomFormat);
            Thread t = new Thread(calendarParser);
            t.start();
            t.join();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }


    }
}
