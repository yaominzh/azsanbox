package com.letstalkdata.iscream.domain;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class Order {

    private int id;
    private List<OrderLineItem> orderLineItems;
    private Timestamp created = Timestamp.valueOf(LocalDateTime.now());
    private BigDecimal totalPrice;


    public Order() {

    }

    public Order(List<Ingredient> ingredients, int scoops) {
        this.orderLineItems = ingredients.stream()
                .map(i -> {
                    int units = i.getType() == Ingredient.Type.ICE_CREAM
                            ? scoops
                            : 1;
                    return new OrderLineItem(this, i, units);
        }).collect(Collectors.toList());
        this.totalPrice = calculatePrice();
    }

    private BigDecimal calculatePrice() {
        return orderLineItems.stream()
                .map(OrderLineItem::getLineItemCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<OrderLineItem> getOrderLineItems() {
        return orderLineItems;
    }

    public void setOrderLineItems(List<OrderLineItem> orderLineItems) {
        this.orderLineItems = orderLineItems;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
}
