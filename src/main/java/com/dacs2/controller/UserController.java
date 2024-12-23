package com.dacs2.controller;

import com.dacs2.model.Cart;
import com.dacs2.model.OrderRequest;
import com.dacs2.model.Orders;
import com.dacs2.model.UserDtls;
import com.dacs2.service.*;
import com.dacs2.util.OrderStatus;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/user")
public class UserController {

    NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CheckOutService checkOutService;

    @Autowired
    private WebInfoService webInfoService;

    @Autowired
    private SupportUrlService supportUrlService;

    @Autowired
    private ContactUrlService contactUrlService;

    @GetMapping("/")
    public String home() {
        return "user/home";
    }

    @ModelAttribute
    public void getUserDetails(Principal p, Model m) {

        if (p != null) {
            String email = p.getName();
            UserDtls userDtls = userService.getUserByEmail(email);
            m.addAttribute("user", userDtls);
            m.addAttribute("countCart", cartService.getCountCart(userDtls.getId()));
        }

        m.addAttribute("webInfo", webInfoService.getWebInfo());
        m.addAttribute("categorys", categoryService.getCategoryByIsActive());
        m.addAttribute("supportUrls", supportUrlService.getSupportUrl());
        m.addAttribute("contactUrls", contactUrlService.getContactUrls());
        m.addAttribute("placeFreeShip", userService.getFirstAdmin().getCity());

    }

    @GetMapping("/addCart")
    public String addToCart(@RequestParam Integer pid, @RequestParam Integer uid, HttpSession session) {
        Cart saveCart = cartService.saveCart(pid, uid);
        if (ObjectUtils.isEmpty(saveCart)) {
            session.setAttribute("errorMsg", "Lỗi! Không thể thêm sản phẩm vào giỏ hàng!");
        } else {
            session.setAttribute("succMsg", "Sản phẩm đã được thêm vào giỏ hàng!");
        }

        return "redirect:/san-pham/san-pham-id=" + pid;
    }

    @GetMapping("/gio-hang")
    public String loadCartPage(Principal p, Model m) {

        UserDtls user = getLoggedInUserDetails(p);
        List<Cart> carts = cartService.getCartByUser(user.getId());
        m.addAttribute("carts", carts);
        if (!ObjectUtils.isEmpty(carts)) {
            m.addAttribute("totalPrice", NumberFormat.getCurrencyInstance(
                    new Locale("vi", "VN")).format(cartService.getTotalPrice(user.getId())));
        }
        return "user/cart";
    }

    private UserDtls getLoggedInUserDetails(Principal p) {
        String email = p.getName();
        return userService.getUserByEmail(email);
    }

    @GetMapping("/orders")
    public String orderPage(Principal p, Model m) {
        UserDtls user = getLoggedInUserDetails(p);

        if (cartService.getTotalPrice(user.getId()) != null) {
            m.addAttribute("totalPrice", numberFormat.format(cartService.getTotalPrice(user.getId())));
            m.addAttribute("shipPrice0", numberFormat.format(0));
            m.addAttribute("shipPrice50k", numberFormat.format(50000));
            m.addAttribute("totalPrice50k", numberFormat.format(cartService.getTotalPrice(user.getId()) + 50000));
        }

        return "user/order";
    }

    @GetMapping("/thanh-toan-thanh-cong")
    public String loadSuccess() {
        return "/user/success";
    }

    @PostMapping("/save-order")
    public String saveOrder(@ModelAttribute OrderRequest request, Principal p,
                            HttpServletRequest httpServletRequest)
            throws MessagingException, UnsupportedEncodingException {

        String baseUrl = httpServletRequest.getScheme() + "://" + httpServletRequest.getServerName() + ":" + httpServletRequest.getServerPort();
        Orders createOrder = orderService.createOrder(getLoggedInUserDetails(p).getId(), request);

        if (request.getPaymentType().equals("ONLINE")) {
            String vnpayUrl = checkOutService.checkOutWithPayOnline(createOrder, baseUrl);

            return "redirect:" + vnpayUrl;
        }

        orderService.saveOrder(createOrder);
        return "redirect:/user/thanh-toan-thanh-cong";
    }

    @GetMapping("/don-hang")
    public String myOrder(Model m, Principal p) {
        UserDtls user = getLoggedInUserDetails(p);
        m.addAttribute("orders", orderService.getOrdersByUserId(user.getId()));
        return "user/my_orders";
    }

    @GetMapping("/update-status")
    public String updateOrderStatus(@RequestParam Integer id, @RequestParam Integer st, HttpSession session) throws MessagingException, UnsupportedEncodingException {

        OrderStatus[] values = OrderStatus.values();
        String status = null;

        for (OrderStatus os : values) {
            if (os.getId().equals(st)) {
                status = os.getName();
            }
        }

        if (!ObjectUtils.isEmpty(orderService.updateOrderStatus(id, status))) {
            session.setAttribute("succMsg", "Đã hủy đơn hàng!");
        } else {
            session.setAttribute("succMsg", "Không thể hủy đơn hàng!");
        }

        return "redirect:/user/don-hang";
    }

    @GetMapping("/profile")
    public String profile() {
        return "/user/profile";
    }

    @PostMapping("/update-profile")
    public String updateProfile(@ModelAttribute UserDtls user, @RequestParam("img") MultipartFile img, HttpSession session) throws IOException {

        if (!ObjectUtils.isEmpty(userService.updateUserProfile(user, img))) {
            session.setAttribute("succMsg", "Đã cập nhật profile của bạn!");
        } else {
            session.setAttribute("errorMsg", "Lỗi cập nhật profile!");
        }


        return "redirect:/user/profile";
    }

    @PostMapping("/change-password")
    public String changPassword(@RequestParam String currentPassword, @RequestParam String newPassword, Principal p, HttpSession session) {
        UserDtls user = getLoggedInUserDetails(p);

        if (passwordEncoder.matches(currentPassword, user.getPassword())) {
            if (currentPassword.equals(newPassword)) {
                session.setAttribute("errorMsg", "Mật khẩu mới không được giống mật khẩu cũ!");
            } else {
                String newEncodePassword = passwordEncoder.encode(newPassword);
                user.setPassword(newEncodePassword);
                if (!ObjectUtils.isEmpty(userService.updateUser(user))) {
                    session.setAttribute("succMsg", "Thay đổi mật khẩu thành công!");
                } else {
                    session.setAttribute("errorMsg", "Chưa thể thay đổi mật khẩu!");
                }
            }
        } else {
            session.setAttribute("errorMsg", "Mật khẩu hiện tại không đúng!");
        }

        return "redirect:/user/profile";
    }

    @GetMapping("/xem-don-hang/id={orderId}")
    public String xemDonHang(@PathVariable String orderId, Model model) {
        Orders orders = orderService.getOrderByOrderId(orderId);
        model.addAttribute("order", orders);
        model.addAttribute("listProductOrders", orderService.getProductOrdersByOrderId(orderId));
        model.addAttribute("totalPrice", NumberFormat.getCurrencyInstance(
                new Locale("vi", "VN")).format(orderService.getOrderByOrderId(orderId).getTotalPrice()));
        return "user/view_my_order";
    }
}
