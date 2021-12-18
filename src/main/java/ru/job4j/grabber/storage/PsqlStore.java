package ru.job4j.grabber.storage;

import ru.job4j.grabber.model.Post;
import ru.job4j.grabber.storage.config.Configurator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store, AutoCloseable {

    private Connection connection;

    private static final String URL_KEY = "url";
    private static final String DRIVER_KEY = "jdbc.driver";
    private static final String USER_KEY = "username";
    private static final String PASSWORD_KEY = "password";

    public PsqlStore(Properties config) {
        try {
            Class.forName(config.getProperty(DRIVER_KEY));
            connection = DriverManager.getConnection(config.getProperty(URL_KEY),
                    config.getProperty(USER_KEY), config.getProperty(PASSWORD_KEY));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save(Post post) {
        try (PreparedStatement statement = connection.prepareStatement(
                "insert into post(name, text, link, created) values(?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, post.getTitle());
            statement.setString(2, post.getLink());
            statement.setString(3, post.getDescription());
            statement.setTimestamp(4, Timestamp.valueOf(post.getCreated()));
            statement.executeUpdate();
            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    post.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Post> getAll() {
        List<Post> result = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(
                "select * from post")) {
            result = parseResultSetToPostsList(statement.executeQuery());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Post findById(int id) {
        Post result = null;
        try (PreparedStatement statement = connection.prepareStatement(
                "select * from post where id = ?")) {
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                result = parseResultSetToPost(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    private List<Post> parseResultSetToPostsList(ResultSet rs) {
        List<Post> result = new ArrayList<>();
        try {
            while (rs.next()) {
                result.add(parseResultSetToPost(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    private Post parseResultSetToPost(ResultSet rs) {
        Post result = null;
        try {
            result = new Post(
                    rs.getInt(1),
                    rs.getString(2),
                    rs.getString(3),
                    rs.getString(4),
                    rs.getTimestamp(5).toLocalDateTime());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void close() throws Exception {
        if (connection != null) {
            connection.close();
        }
    }

    public static void main(String[] args) {

        PsqlStore store = new PsqlStore(new Configurator("jdbc.properties").getProperties());

        store.save(new Post("first post", "http://link/1.ru", "some text, 1"));
        store.save(new Post("second post", "http://link/2.ru", "some text, 2"));
        store.save(new Post("third post", "http://link/3.ru", "some text, 3"));

        Post post = store.findById(1);

        System.out.println(post.toString());

        for (Post p : store.getAll()) {
            System.out.println(p.toString());
        }
    }
}
