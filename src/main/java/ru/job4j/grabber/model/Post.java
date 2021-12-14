package ru.job4j.grabber.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Post {

    private int id;
    private String title;
    private String link;
    private String description;
    private LocalDateTime created;

    public Post(String title, String link, String description) {
        this.title = title;
        this.link = link;
        this.description = description;
        this.created = LocalDateTime.now();
    }

    public Post(String title, String link, String description, LocalDateTime created) {
        this.title = title;
        this.link = link;
        this.description = description;
        this.created = created;
    }

    public Post(int id, String title, String link, String description, LocalDateTime created) {
        this.id = id;
        this.title = title;
        this.link = link;
        this.description = description;
        this.created = created;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Post)) {
            return false;
        }
        Post post = (Post) o;
        return Objects.equals(id, post.id)
                && Objects.equals(title, post.title)
                && Objects.equals(link, post.link);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(id);
        result *= 31 + title.hashCode();
        result *= 31 + link.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return String.format(
                "%sPost {%sid: %d%stitle: %s%slink: %s%sdescription: %s%screated: %s%s}",
                System.lineSeparator(),
                System.lineSeparator(),
                id,
                System.lineSeparator(),
                title,
                System.lineSeparator(),
                link,
                System.lineSeparator(),
                description,
                System.lineSeparator(),
                created,
                System.lineSeparator()
        );
    }
}
