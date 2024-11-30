package com.dacs2.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.text.NumberFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String orderId;

    @ManyToOne
    private Product product;

    private Double price;

    private Integer quantity;

    public String getTotalPriceFormatted() {
        return NumberFormat.getCurrencyInstance(new Locale("vi", "VN")).format(quantity * price);
    }

    @ManyToOne
    @JoinColumn(name = "rating_id")
    private Rating rating;

}
