package com.dacs2.model;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class OrderRequest {

    private String fullName;

    private String phoneNumber;

    private String city;

    private String prefecture;

    private String ward;

    private String address;

    private String paymentType;

}
