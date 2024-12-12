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
import java.util.List;
import java.util.Locale;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String orderId;

    private String orderName;

    private Date orderDate;

    private Double totalPrice;

    @ManyToOne
    private UserDtls user;

    private String status;

    private String paymentType;

    @OneToOne(cascade = CascadeType.ALL)
    private OrderAddress orderAddress;

    private Boolean isPaid;

    private Boolean processed;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "orderId", referencedColumnName = "orderId")
    private List<ProductOrder> productOrders;

    public String getDateFormatted() {
        return orderDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }

    public String getTotalPriceFormatted() {
        return NumberFormat.getCurrencyInstance(new Locale("vi", "VN")).format(totalPrice);
    }

    public String getFullAdressFormatted() {
        return orderAddress.getAddress() + ", " +
                orderAddress.getWard() + ", " +
                orderAddress.getPrefecture() + ", " +
                orderAddress.getCity();
    }

}
