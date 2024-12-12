package com.dacs2.service;
import com.dacs2.model.Orders;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;

public interface CheckOutService {

    @Transactional
    public String checkOutWithPayOnline(Orders order, String urlReturn);

    public int orderReturn(HttpServletRequest request) throws MessagingException, UnsupportedEncodingException;

}
