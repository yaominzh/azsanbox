package com.letstalkdata.iscream.controller;

import com.letstalkdata.iscream.domain.Flavor;
import com.letstalkdata.iscream.domain.Order;
import com.letstalkdata.iscream.domain.Topping;
import com.letstalkdata.iscream.service.IngredientService;
import com.letstalkdata.iscream.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private IngredientService ingredientService;
    private OrderService orderService;

    @Autowired
    public OrderController(IngredientService ingredientService,
                           OrderService orderService) {
        this.ingredientService = ingredientService;
        this.orderService = orderService;
    }

    @RequestMapping(value = "/new", method = RequestMethod.GET)
    public String orderForm(Model model) {
        model.addAttribute("flavors",
                ingredientService.getFlavors());
        model.addAttribute("toppings",
                ingredientService.getToppings());
        return "new-order";
    }

    @RequestMapping(value = "/new", method = RequestMethod.POST)
    public String createOrder(@ModelAttribute NewOrderRequest newOrderRequest,
                              Model model) {
        var flavor = ingredientService.getFlavorById(newOrderRequest.flavor);
        var toppings = Arrays.stream(newOrderRequest.toppings)
                .mapToObj(id -> ingredientService.getToppingById(id))
                .toArray(Topping[]::new);
        var order = new Order(flavor, newOrderRequest.scoops, toppings);

        var priceNumber = order.getTotalPrice();
        var price = NumberFormat.getCurrencyInstance(Locale.US)
                .format(priceNumber);
        model.addAttribute("price", price);

        orderService.save(order);

        return "order-success";
    }

    private static class NewOrderRequest {
        private int flavor;
        private int scoops;
        private int[] toppings;

        public NewOrderRequest() {}

        public int getFlavor() {
            return flavor;
        }

        public void setFlavor(int flavor) {
            this.flavor = flavor;
        }

        public int getScoops() {
            return scoops;
        }

        public void setScoops(int scoops) {
            this.scoops = scoops;
        }

        public int[] getToppings() {
            return toppings;
        }

        public void setToppings(int[] toppings) {
            this.toppings = toppings;
        }
    }
}
