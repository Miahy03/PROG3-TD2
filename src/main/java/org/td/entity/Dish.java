
package org.td.entity;

public class Dish {

    private Integer id;
    private String name;
    private Double price;
    private Double ingredientsCost;

    public Dish(Integer id, String name, Double price, Double ingredientsCost) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.ingredientsCost = ingredientsCost;
    }

    public Double getDishCost() {
        return ingredientsCost;
    }

    public Double getGrossMargin() {
        if (price == null) {
            throw new RuntimeException("Prix de vente non défini");
        }
        return price - ingredientsCost;
    }

    public Integer getId() { return id; }
    public String getName() { return name; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
}
