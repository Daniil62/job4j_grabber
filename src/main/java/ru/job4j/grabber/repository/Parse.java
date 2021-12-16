package ru.job4j.grabber.repository;

import ru.job4j.grabber.model.Post;

import java.util.List;

public interface Parse {

    List<Post> list();

    Post detail(String link);
}
