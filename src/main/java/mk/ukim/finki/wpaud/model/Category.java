package mk.ukim.finki.wpaud.model;


import lombok.Data;

@Data
public class Category {

    private Long id;
    private String name;
    private String Description;

    public Category(String name, String surname) {
        this.id = (long) (Math.random() * 1000);
        this.name = name;
        this.Description = surname;
    }
}


