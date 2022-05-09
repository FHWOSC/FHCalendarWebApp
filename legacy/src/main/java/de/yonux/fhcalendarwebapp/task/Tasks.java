package de.yonux.fhcalendarwebapp.task;

public class Tasks {

    public static String runCourseFetcher() {
        FetchCoursesTableTask courseFetcher = new FetchCoursesTableTask();
        Thread t = new Thread(courseFetcher);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return courseFetcher.getResult();
    }

    public static String runCustomizerFetcher() {
        FetchCustomizerTask customizerFetcher = new FetchCustomizerTask();
        Thread t = new Thread(customizerFetcher);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return customizerFetcher.getResult();
    }
}
