package de.yonux.fhcalendarwebapp.task;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;

public class    CalendarParserTask implements Runnable {
    private final String url;
    private final OutputStream output;
    private final NameFormat nameFormat;
    private final RoomFormat roomFormat;
    private String fileName;

    public CalendarParserTask(String url, OutputStream output, NameFormat nameFormat, RoomFormat roomFormat) {
        this.url = url;
        this.output = output;
        this.nameFormat = nameFormat;
        this.roomFormat = roomFormat;
    }

    @Override
    public void run() {
        try {
            String[][] time = new String[7][2];
            ArrayList<Event> events = new ArrayList<>();
            boolean read = false;
            URL url = new URL(this.url);
            URLConnection connection = url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                if (line.equals("<!-- CONTENT START -->")) {
                    read = true;
                    continue;
                }
                if (line.equals("<!-- CONTENT END -->")) {
                    break;
                }
                if (read) {
                    sb.append("\n").append(line);
                }
            }

            Document document = Jsoup.parse(sb.toString(), url.toString());
            Element tablePlan = document.selectFirst("tbody");
            Elements trHeaderAndDays = tablePlan.children();
            fileName = "benutzerdefiniert";
            int day = 0;
            for (int i = 0; i < trHeaderAndDays.size(); i++) {
                Element row = trHeaderAndDays.get(i);

                int j0 = 0;
                for (int j = 0; j < row.childrenSize(); j++) {
                    Element course = row.child(j);
                    if (i == 0) {
                        if (j == 0) {
                            if (course.text() != null && course.text().length() > 0) {
                                fileName = course.text();
                            }
                        } else {
                            time[j0] = course.text().split(" - ");
                            j0++;
                        }
                    } else {
                        if (j == 0) {
                            if (course.hasAttr("rowspan")) {
                                System.out.println(i);
                                day++;
                            }
                        } else {
                            int length = 1;
                            if (course.hasAttr("colspan")) {
                                length = Integer.parseInt(course.attr("colspan"));
                            }
                            if (course.childrenSize() > 0) {
                                Element body = course.selectFirst("tbody");
                                String name = "  ";
                                String[] employees = new String[0];
                                String[] rooms = new String[0];
                                for (Element e : body.getAllElements()) {
                                    if (e.attr("class").equals("splan_veranstaltung")) {
                                        name = e.text();
                                    } else if (e.attr("class").equals("splan_mitarbeiter")) {
                                        employees = new String[e.childrenSize()];
                                        for (int m = 0; m < e.childrenSize(); m++) {
                                            Element empl = e.child(m);
                                            employees[m] = formatName(empl.attr("title"), empl.text());
                                        }
                                    } else if (e.attr("class").equals("splan_hoerer_raum")) {
                                        rooms = new String[e.childrenSize()];
                                        for (int n = 0; n < e.childrenSize(); n++) {
                                            Element room = e.child(n);
                                            rooms[n] = formatRoom(e.attr("title"), e.text());
                                        }
                                    }
                                }
                                events.add(new Event(DayOfWeek.of(day), time[j0][0], time[j0 + length - 1][1], name, employees, rooms));
                                j0 += length;
                            } else {
                                j0++;
                            }
                        }
                    }

                }
            }

            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output))) {
                writer.write(
                        "BEGIN:VCALENDAR\r\n" +
                                "VERSION:2.0\r\n" +
                                "PRODID:-//ical.marudot.com//iCal Event Maker\r\n" +
                                "CALSCALE:GREGORIAN\r\n" +
                                "BEGIN:VTIMEZONE\r\n" +
                                "TZID:Europe/Berlin\r\n" +
                                "TZURL:http://tzurl.org/zoneinfo-outlook/Europe/Berlin\r\n" +
                                "X-LIC-LOCATION:Europe/Berlin\r\n" +
                                "BEGIN:DAYLIGHT\r\n" +
                                "TZOFFSETFROM:+0100\r\n" +
                                "TZOFFSETTO:+0200\r\n" +
                                "TZNAME:CEST\r\n" +
                                "DTSTART:19700329T020000\r\n" +
                                "RRULE:FREQ=YEARLY;BYMONTH=3;BYDAY=-1SU\r\n" +
                                "END:DAYLIGHT\r\n" +
                                "BEGIN:STANDARD\r\n" +
                                "TZOFFSETFROM:+0200\r\n" +
                                "TZOFFSETTO:+0100\r\n" +
                                "TZNAME:CET\r\n" +
                                "DTSTART:19701025T030000\r\n" +
                                "RRULE:FREQ=YEARLY;BYMONTH=10;BYDAY=-1SU\r\n" +
                                "END:STANDARD\r\n" +
                                "END:VTIMEZONE\r\n"
                );

                System.out.println();
                System.out.println("~ " + fileName + " ~");
                for (Event e : events) {
                    System.out.println(e.toString());


                    writer.write(replaceUmlauts(
                            "BEGIN:VEVENT\r\n" +
                                    "DTSTAMP:" + formatLocalDateTime(LocalDateTime.now()) + "Z\r\n" +
                                    "UID:" + System.nanoTime() + "\r\n" +
                                    "DTSTART;TZID=Europe/Berlin:" + formatDtStamp(e.day, e.timeStart) + "\r\n" +
                                    "RRULE:FREQ=WEEKLY;BYDAY=" + e.day.name().substring(0, 2).toUpperCase() + "\r\n" +
                                    "DTEND;TZID=Europe/Berlin:" + formatDtStamp(e.day, e.timeEnd) + "\r\n" +
                                    "SUMMARY:" + e.name + "\r\n" +
                                    "DESCRIPTION:" + String.join("; ", e.employees) + "\r\n" +
                                    "LOCATION:" + String.join(", ", e.rooms) + "\r\n" +
                                    "END:VEVENT\r\n"
                    ));
                }
                writer.write("END:VCALENDAR");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String replaceUmlauts(String s) {
        return s
                .replace("ä", "ae")
                .replace("ö", "oe")
                .replace("ü", "ue")
                .replace("Ä", "Ae")
                .replace("Ö", "Oe")
                .replace("Ü", "Ue")
                .replace("ß", "ss");
    }

    private static String formatDtStamp(DayOfWeek day, String time) {

        LocalDate ld = LocalDate.now().with(TemporalAdjusters.nextOrSame(day));
        String[] hm = time.split(":");
        LocalTime lt = LocalTime.of(Integer.parseInt(hm[0]), Integer.parseInt(hm[1]), 0);
        return formatLocalDateTime(LocalDateTime.of(ld, lt));

    }

    private static String formatLocalDateTime(LocalDateTime ldt) {

        DateTimeFormatter fDate = DateTimeFormatter.ofPattern("yyyyMMdd");
        DateTimeFormatter fTime = DateTimeFormatter.ofPattern("HHmmss");
        return fDate.format(ldt) + "T" + fTime.format(ldt);
    }

    public String formatName(String name, String abbr) {
        String[] nameParts = name.split(", ");
        String firstName = nameParts[1];
        String lastName = nameParts[0];

        switch (nameFormat) {
            case NONE:
                return "";

            case ABBR:
                return abbr;

            case FIRST_LAST:
                return String.format("%s %s", firstName,  lastName);

            case FIRST_LAST_ABBR:
                return String.format("%s %s (%s)", firstName,  lastName, abbr);

            case LAST_FIRST:
                return String.format("%s, %s", lastName,  firstName);

            case LAST_FIRST_ABBR:
                return String.format("%s, %s (%s)", lastName,  firstName, abbr);
        }
        throw new IllegalArgumentException("Unknown NameFormat '" + nameFormat + "'");
    }

    public String formatRoom(String name, String abbr) {
        switch (roomFormat) {

            case NONE:
                return "";

            case ABBR:
                return abbr;

            case FULL_NAME:
                return name;

            case FULL_NAME_AND_ABBR:
                return String.format("%s (%s)", name, abbr);
        }
        throw new IllegalArgumentException("Unknown RoomFormat '" + roomFormat + "'");
    }

    public String getFileName() {
        return this.fileName;
    }

    //	private static Event parseCourse(Element course)
//	{
//		String day = null;
//		String timeStart = null;
//		String timeEnd = null;
//		String name = null;
//		String[] employees = null;
//		String[] rooms = null;
//		int length;
//		Element body = course.selectFirst("tbody");
//		if (course.hasAttr("colspan"))
//		{
//			length = Integer.parseInt(course.attr("colspan"));
//		}
//		for (Element e : body.getAllElements())
//		{
//			if (e.attr("class").equals("splan_veranstaltung"))
//			{
//				name  = e.text();
//			}
//			else if (e.attr("class").equals("splan_mitarbeiter"))
//			{
//				employees = new String[e.childrenSize()];
//				for (int i = 0; i < e.childrenSize(); i++)
//				{
//					employees[i] = e.child(i).attr("title") + " (" + e.child(i).text() + ")";
//				}
//			}
//			else if (e.attr("class").equals("splan_hoerer_raum"))
//			{
//				rooms = new String[e.childrenSize()];
//				for (int j = 0; j < e.childrenSize(); j++)
//				{
//					rooms[j] = e.child(j).attr("title");
//				}
//			}
//		}
//		return new Event(day, timeStart, timeEnd, name, employees, rooms);
//	}

    public static class Event {
        private final DayOfWeek day;
        private final String timeStart;
        private final String timeEnd;
        private final String name;
        private final String[] employees;
        private final String[] rooms;

        public Event(DayOfWeek day, String timeStart, String timeEnd, String name, String[] employees, String[] rooms) {
            this.day = day;
            this.timeStart = timeStart;
            this.timeEnd = timeEnd;
            this.name = name;
            this.employees = employees;
            this.rooms = rooms;
        }

        @Override
        public String toString() {
            return day + ", " + timeStart + " Uhr bis " + timeEnd + " Uhr: '" + name + "' bei " + String.join("; ", employees) + " im " + String.join(", ", rooms);
        }


    }

}
