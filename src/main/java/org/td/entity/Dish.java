
package org.td.entity;

public class Dish {

    private int id;
    private String name;
    private double price;
    private DishTypeEnum type;
    private CategoryEnum category;

    public Dish(int id, String name, double price, DishTypeEnum type, CategoryEnum category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.type = type;
        this.category = category;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public DishTypeEnum getType() { return type; }
    public CategoryEnum getCategory() { return category; }
}
