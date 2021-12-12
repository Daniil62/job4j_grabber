package ru.job4j.grabber.repository;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.model.Post;
import ru.job4j.grabber.utils.SqlRuDateTimeParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PostParser {

    private static final String PATH = "https://www.sql.ru/forum/job-offers/";
    private static final String CSS_QUERY_POST_CLASSNAME = ".postslisttopic";
    private static final String CSS_QUERY_MESSAGE_HEADER_CLASSNAME = ".messageHeader";
    private static final String CSS_QUERY_MESSAGE_BODY_CLASSNAME = ".msgBody";
    private static final String CSS_QUERY_MESSAGE_FOOTER_CLASSNAME = ".msgFooter";
    private static final String ATTRIBUTE_KEY = "href";
    private static final int UNNECESSARY_ELEMENTS_FROM_MESSAGE = 3;
    private static final int FIRST_DOC_ELEMENT = 0;

    public List<Post> parsePages(int count) throws IOException {
        List<Post> result = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            Document doc = Jsoup.connect(String.format("%s%d", PATH, i)).get();
            Elements row = doc.select(CSS_QUERY_POST_CLASSNAME);
            for (Element td : row.subList(UNNECESSARY_ELEMENTS_FROM_MESSAGE, row.size())) {
                Element href = td.child(FIRST_DOC_ELEMENT);
                String postPath = href.attr(ATTRIBUTE_KEY);
                result.add(parsePosts(postPath));
            }
        }
        return result;
    }

    public Post parsePosts(String path) throws IOException {
        Document doc = Jsoup.connect(path).get();
        return new Post(
                doc.select(CSS_QUERY_MESSAGE_HEADER_CLASSNAME).first().text(),
                path,
                doc.select(CSS_QUERY_MESSAGE_BODY_CLASSNAME).text(),
                new SqlRuDateTimeParser().parse(
                        doc.select(CSS_QUERY_MESSAGE_FOOTER_CLASSNAME).text().split(" \\[")[0]
                )
        );
    }

    public void showPosts(int count) throws IOException {
        for (Post post : parsePages(count)) {
            System.out.print(post.toString());
        }
    }

    public static void main(String[] args) throws IOException {
        new PostParser().showPosts(1);
    }
}
