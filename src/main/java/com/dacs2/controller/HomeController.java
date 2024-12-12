package com.dacs2.controller;

import com.dacs2.model.*;
import com.dacs2.service.*;
import com.dacs2.util.CommonUtil;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    String imgPath = System.getProperty("user.dir") + File.separator
            + "src" + File.separator + "main" + File.separator + "resources"
            + File.separator + "static" + File.separator + "img";

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CartService cartService;

    @Autowired
    private CheckOutService checkOutService;

    @Autowired
    private WebInfoService webInfoService;

    @Autowired
    private SliderService sliderService;

    @Autowired
    private SupportUrlService supportUrlService;

    @Autowired
    private ContactUrlService contactUrlService;

    @Autowired
    private NewsService newsService;

    @Autowired
    private RatingService ratingService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private BrandService brandService;

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

    }

    @GetMapping("/")
    public String index(Model m) {
        List<Category> categorys = categoryService.getCategoryByIsActive();
        m.addAttribute("categorys", categorys);

        HashMap<String, List<Product>> map = new HashMap<>();
        for (Category category : categorys) {
            List<Product> products = productService.getProductByDanhMuc(category.getName());
            map.put(category.getName(), products.subList(0, Math.min(products.size(), 8)));
        }
        m.addAttribute("mapProduct", map);
        m.addAttribute("slides", sliderService.getSliderList());
        m.addAttribute("blogs", newsService.getNewsByStyle("Blog").stream().limit(9).collect(Collectors.toList()));
        return "index";
    }

    @GetMapping("/dang-nhap")
    public String login() {
        return "login";
    }

    @GetMapping("/dang-ky")
    public String register() {
        return "register";
    }

    @GetMapping("/san-pham")
    public String product(Model m,
                          @RequestParam(value = "danh-muc", defaultValue = "") String danhMuc,
                          @RequestParam(value = "thuong-hieu", defaultValue = "") String thuongHieu,
                          @RequestParam(name = "trang", defaultValue = "1") Integer pageNumber,
                          @RequestParam(name = "pageSize", defaultValue = "20") Integer pageSize) {
        m.addAttribute("searchCh", "");
        m.addAttribute("categories", categoryService.getCategoryByIsActive());
        m.addAttribute("brands", brandService.getAllBrandIsActive());
        m.addAttribute("danhMuc", danhMuc.trim());
        m.addAttribute("thuongHieu", thuongHieu.trim());
        m.addAttribute("search", false);
        Page<Product> page = productService.getAllProductsForHomePagination(pageNumber - 1, pageSize, danhMuc.trim(), thuongHieu.trim());
        m.addAttribute("products", page.getContent());
        m.addAttribute("trang", page.getNumber());
        m.addAttribute("pageSize", pageSize);
        m.addAttribute("totalElements", page.getTotalElements());
        m.addAttribute("totalPages", page.getTotalPages());
        m.addAttribute("isFirst", page.isFirst());
        m.addAttribute("isLast", page.isLast());

        return "product";
    }

    @GetMapping("/san-pham/san-pham-id={id}")
    public String viewProduct(@PathVariable int id, Model m) {
        Product product = productService.getProductById(id);
        m.addAttribute("category", product.getDanhmuc());
        m.addAttribute("product", product);
        m.addAttribute("listProduct", productService.getProductForView(product.getId()));
        m.addAttribute("ratings", ratingService.getRatingsByProductId(product));
        m.addAttribute("rate_number", String.format("%.1f", product.getTongsoSao() * 1.0 / product.getSoluongDanhgia()));
        m.addAttribute("star1", ratingService.count(product, 5));

        for (int i = 1; i < 6; i++) {
            Integer starCount = ratingService.count(product, i);
            m.addAttribute("star" + i, starCount != null ? starCount : 0);
        }

        HashMap<Long, Comment> map = new HashMap<>();

        for (Comment comment : commentService.getAllReplyByAdmin(product)) {
            map.put(comment.getParentComment().getId(), comment);
        }

        m.addAttribute("replies", map);
        return "view_product";
    }

    @PostMapping("/luu-user")
    public String saveUser(@ModelAttribute UserDtls user,
                           HttpServletRequest request,
                           @RequestParam("img") MultipartFile file,
                           HttpSession session) throws IOException, MessagingException {

        if (userService.existsEmail(user.getEmail())) {
            session.setAttribute("errorMsg", "Email này đã tồn tại!");
            return "redirect:/dang-ky";
        }

        String imageName = file.isEmpty() ? "default.jpg" : file.getOriginalFilename();
        user.setProfileImage(imageName);
        UserDtls addUser = userService.addUser(user);

        if (!ObjectUtils.isEmpty(addUser)) {

            String confirmToken = UUID.randomUUID().toString();
            userService.updateConfirmEmailToken(addUser.getEmail(), confirmToken);

            String url = CommonUtil.generateUrl(request) + "/xac-nhan-tai-khoan?token=" + confirmToken;

            Boolean sendMail = commonUtil.sendConfirmEmail(url, addUser.getEmail());

            if (!file.isEmpty()) {

                Path path = Paths.get(imgPath + File.separator + "profile_img" + File.separator + imageName);

                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }

            if (sendMail) {
                session.setAttribute("succMsg", "Đã gửi xác nhận tài khoản qua mail của bạn!");
            } else {
                session.setAttribute("errorMsg", "Lỗi!");
            }

        } else {
            session.setAttribute("errorMsg", "Lỗi!");
        }

        return "redirect:/dang-ky";
    }

    @GetMapping("/xac-nhan-tai-khoan")
    public String showConfirmedEmail(@RequestParam String token, Model m) {

        UserDtls user = userService.confirmEmail(token);

        if (user == null) {
            m.addAttribute("msg", "Đường link không hiệu dụng!");
            return "message";
        }
        m.addAttribute("token", token);
        return "user/confirmsuccess";
    }

    @GetMapping("/quen-mat-khau")
    public String showForgotPassword() {
        return "forgot_password";
    }

    @PostMapping("/check-email")
    public String processForgotPassword(@RequestParam String email, HttpSession session, HttpServletRequest request) throws MessagingException, UnsupportedEncodingException {

        UserDtls userDtls = userService.getUserByEmail(email);

        if (ObjectUtils.isEmpty(userDtls)) {
            session.setAttribute("errorMsg", "không tìm thấy tài khoản!");
        } else {

            String resetToken = UUID.randomUUID().toString();
            userService.updateUserResetToken(email, resetToken);

            String url = CommonUtil.generateUrl(request) + "/reset-mat-khau?token=" + resetToken;

            Boolean sendMail = commonUtil.sendMail(url, email);

            if (sendMail) {
                session.setAttribute("succMsg", "Đã gửi xác nhận thay đổi mật khẩu qua mail của bạn!");
            } else {
                session.setAttribute("errorMsg", "Lỗi!");
            }
        }

        return "redirect:/quen-mat-khau";
    }

    @GetMapping("/reset-mat-khau")
    public String showResetPassword(@RequestParam String token, Model m) {

        UserDtls user = userService.getUserByToken(token);

        if (user == null) {
            m.addAttribute("msg", "Đường link không hiệu dụng!");
            return "message";
        }
        m.addAttribute("token", token);
        return "reset_password";
    }

    @PostMapping("/reset-mat-khau")
    public String resetPassword(@RequestParam String token, @RequestParam String password, Model m) {

        UserDtls user = userService.getUserByToken(token);

        if (user == null) {
            m.addAttribute("errorMsg", "Đường link không hiệu dụng!");
            return "message";
        } else {
            user.setPassword(passwordEncoder.encode(password));
            user.setResetToken(null);
            userService.updateUser(user);
            m.addAttribute("msg", "Đã thay đổi mật khẩu thành công!");
            return "message";
        }
    }

    @GetMapping("/search")
    public String searchProduct(@RequestParam String ch, Model m,
                                @RequestParam(name = "trang", defaultValue = "1") Integer pageNumber,
                                @RequestParam(name = "pageSize", defaultValue = "36") Integer pageSize) {

        if (ch.trim().isEmpty()) {
            return "redirect:/san-pham";
        }

        m.addAttribute("categories", categoryService.getCategoryByIsActive());
        m.addAttribute("paramValue", "");
        m.addAttribute("searchCh", ch.trim());
        Page<Product> page = productService.searchProductPagination(pageNumber - 1, pageSize, ch.trim());
        if (ObjectUtils.isEmpty(page.getContent())) {
            m.addAttribute("searchResult", false);
        } else {
            m.addAttribute("searchResult", true);
        }
        m.addAttribute("search", true);
        m.addAttribute("products", page.getContent());
        m.addAttribute("trang", page.getNumber());
        m.addAttribute("pageSize", pageSize);
        m.addAttribute("totalPages", page.getTotalPages());
        m.addAttribute("totalElements", page.getTotalElements());
        m.addAttribute("isFirst", page.isFirst());
        m.addAttribute("isLast", page.isLast());

        return "product";
    }

    @GetMapping("/vnpay-payment")
    public String InfoPaying(HttpServletRequest request, Model m) throws MessagingException, UnsupportedEncodingException {
        String orderInfo = request.getParameter("vnp_OrderInfo");
        String paymentTime = request.getParameter("vnp_PayDate");
        String transactionId = request.getParameter("vnp_TransactionNo");
        Double totalPrice = Double.parseDouble(request.getParameter("vnp_Amount"));

        DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDateTime dateTime = LocalDateTime.parse(paymentTime, inputFormat);
        DateTimeFormatter outputFormat = DateTimeFormatter.ofPattern("HH-mm-ss dd/MM/yyyy");

        int paymentStatus = checkOutService.orderReturn(request);

        m.addAttribute("orderId", orderInfo);
        m.addAttribute("totalPrice", NumberFormat.getCurrencyInstance(new Locale("vi", "VN")).format(totalPrice / 100));
        m.addAttribute("paymentTime", dateTime.format(outputFormat));
        m.addAttribute("transactionId", transactionId);
        m.addAttribute("ordersuccess", paymentStatus);

        return "user/orderStatus";
    }

    @GetMapping("/blogs")
    public String blogs(Model m,
                        @RequestParam(name = "trang", defaultValue = "1") Integer pageNumber,
                        @RequestParam(name = "pageSize", defaultValue = "20") Integer pageSize) {
        Page<News> page = newsService.getAllNewsForHome(pageNumber - 1, pageSize);
        m.addAttribute("blogs", page.getContent());
        m.addAttribute("trang", page.getNumber());
        m.addAttribute("pageSize", pageSize);
        m.addAttribute("totalElements", page.getTotalElements());
        m.addAttribute("totalPages", page.getTotalPages());
        m.addAttribute("isFirst", page.isFirst());
        m.addAttribute("isLast", page.isLast());
        return "blogs";
    }

    @GetMapping("/blogs/blog-id={id}")
    public String blogsById(@PathVariable Integer id, Model m) {
        News blog = newsService.getNewsById(id);
        m.addAttribute("blog", blog);
        return "blog";
    }


}
