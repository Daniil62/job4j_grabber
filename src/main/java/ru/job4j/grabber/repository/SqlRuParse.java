package ru.job4j.grabber.repository;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.model.Post;
import ru.job4j.grabber.utils.DateTimeParser;
import ru.job4j.grabber.utils.SqlRuDateTimeParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SqlRuParse implements Parse {

    private final DateTimeParser dateTimeParser;

    private static final String PATH = "https://www.sql.ru/forum/job-offers/";
    private static final String CSS_QUERY_POST_CLASSNAME = ".postslisttopic";
    private static final String CSS_QUERY_MESSAGE_HEADER_CLASSNAME = ".messageHeader";
    private static final String CSS_QUERY_MESSAGE_BODY_CLASSNAME = ".msgBody";
    private static final String CSS_QUERY_MESSAGE_FOOTER_CLASSNAME = ".msgFooter";
    private static final String ATTRIBUTE_KEY = "href";
    private static final String TARGET_SEQUENCE = "java";
    private static final String EXCLUDED_SEQUENCE = "javascript";
    private static final int UNNECESSARY_ELEMENTS_FROM_MESSAGE = 3;
    private static final int FIRST_DOC_ELEMENT = 0;
    private static final int PAGES_COUNT = 5;

    public SqlRuParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    @Override
    public List<Post> list() {
        List<Post> result = new ArrayList<>();
        Document doc = null;
        for (int i = 1; i <= PAGES_COUNT; i++) {
            try {
                doc = Jsoup.connect(String.format("%s%d", PATH, i)).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (doc != null) {
                Elements row = doc.select(CSS_QUERY_POST_CLASSNAME);
                for (Element td : row.subList(UNNECESSARY_ELEMENTS_FROM_MESSAGE, row.size())) {
                    Element child = td.child(FIRST_DOC_ELEMENT);
                    String text = child.text().toLowerCase();
                    if (text.contains(TARGET_SEQUENCE) && !text.contains(EXCLUDED_SEQUENCE)) {
                        result.add(detail(child.attr(ATTRIBUTE_KEY)));
                    }
                }
            }
        }
        return result;
    }

    @Override
    public Post detail(String link) {
        Document doc = null;
        try {
            doc = Jsoup.connect(link).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return doc != null ? new Post(
                doc.select(CSS_QUERY_MESSAGE_HEADER_CLASSNAME).first().text(),
                link,
                doc.select(CSS_QUERY_MESSAGE_BODY_CLASSNAME).text(),
                dateTimeParser.parse(
                        doc.select(CSS_QUERY_MESSAGE_FOOTER_CLASSNAME).text().split(" \\[")[0]
                )
        ) : null;
    }

    public void showPosts() {
        list().forEach(System.out::println);
    }

    public static void main(String[] args) {
        new SqlRuParse(new SqlRuDateTimeParser()).showPosts();
    }
}
