package ru.job4j.grabber;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import ru.job4j.grabber.repository.Parse;
import ru.job4j.grabber.repository.SqlRuParse;
import ru.job4j.grabber.storage.PsqlStore;
import ru.job4j.grabber.storage.Store;
import ru.job4j.grabber.storage.config.Configurator;
import ru.job4j.grabber.utils.SqlRuDateTimeParser;

import java.io.*;
import java.util.Properties;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class Grabber implements Grab {

    private final Properties config = new Properties();

    private static final String PROPERTIES_FILE_NAME = "app.properties";
    private static final String PARSE = "parse";
    private static final String STORE = "store";
    private static final String TIME_KEY = "time";

    public Grabber() {
        configure();
    }

    public Scheduler scheduler() {
        Scheduler scheduler = null;
        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return scheduler;
    }

    public void configure() {
        try (InputStream in = Grabber.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE_NAME)) {
            if (in != null) {
                config.load(in);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init(Parse parse, Store store, Scheduler scheduler) {
        JobDataMap data = new JobDataMap();
        data.put(STORE, store);
        data.put(PARSE, parse);
        try {
            scheduler.scheduleJob(getJobDetail(data),
                    getTrigger(getSimpleScheduleBuilder()));
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    private JobDetail getJobDetail(JobDataMap data) {
        return newJob(GrabJob.class)
                    .usingJobData(data)
                    .build();
    }

    private Trigger getTrigger(SimpleScheduleBuilder times) {
        return newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
    }

    private SimpleScheduleBuilder getSimpleScheduleBuilder() {
        return simpleSchedule()
                    .withIntervalInHours(Integer.parseInt(config.getProperty(TIME_KEY)))
                    .repeatForever();
    }

    public static class GrabJob implements Job {

        @Override
        public void execute(JobExecutionContext context) {
            JobDataMap map = context.getJobDetail().getJobDataMap();
            Store store = (Store) map.get(STORE);
            Parse parse = (Parse) map.get(PARSE);
            if (store != null && parse != null) {
                parse.list().forEach(store::save);
            }
        }
    }

    public static void main(String[] args) {
        Grabber grab = new Grabber();
        Scheduler scheduler = grab.scheduler();
        Store store = new PsqlStore(new Configurator("jdbc.properties").getProperties());
        grab.init(new SqlRuParse(new SqlRuDateTimeParser()), store, scheduler);

        store.getAll().forEach(System.out::println);
    }
}
