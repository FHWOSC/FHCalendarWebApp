package de.yonux.fhcalendarwebapp;

import de.yonux.fhcalendarwebapp.task.FetchCoursesTableTask;
import de.yonux.fhcalendarwebapp.task.FetchCustomizerTask;

import java.io.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet(name = "helloServlet", value = "/hello-servlet")
public class MainServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");

        // Hello
        PrintWriter out = response.getWriter();


        out.println("<html><body>");
        out.println("<h1>Standard</h1>");

        FetchCoursesTableTask courseFetcher = new FetchCoursesTableTask();
        Thread t = new Thread(courseFetcher);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        out.println(courseFetcher.getResult());

        out.println("<br><br>");
        out.println("<h1>Benutzerdefiniert</h1>");

        FetchCustomizerTask customizerFetcher = new FetchCustomizerTask();
        Thread t1 = new Thread(customizerFetcher);
        t1.start();
        try {
            t1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        out.println(customizerFetcher.getResult());

        out.println("</body></html>");

    }

    public void destroy() {
    }
}