package com.dacs2.api;

import com.dacs2.model.*;
import com.dacs2.repository.*;
import com.dacs2.service.*;
import com.dacs2.util.OrderStatus;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private NewsService newsService;

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductOrderRepository productOrderRepository;

    @Autowired
    private ProductOrderService productOrderService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentService commentService;

    @Autowired
    private ContactUrlService contactUrlService;

    @Autowired
    private SliderService sliderService;

    @Autowired
    private SupportUrlService supportUrlService;

    @Autowired
    private BrandService brandService;

    NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    String imgPath = System.getProperty("user.dir") + File.separator
            + "src" + File.separator + "main" + File.separator + "resources"
            + File.separator + "static" + File.separator + "img";
    @Autowired
    private CommentRepository commentRepository;

    @PostMapping("/upload/file")
    public ResponseEntity<?> uploadFile(@RequestParam("upload") MultipartFile file) {
        String uploadDir = imgPath + File.separator + "ckeditor_img" + File.separator;

        try {
            file.transferTo(new File(uploadDir + file.getOriginalFilename()));
            return ResponseEntity.ok(new UploadResponse("/img/ckeditor_img/" + file.getOriginalFilename()));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload failed");
        }
    }

    public static class UploadResponse {
        private String url;

        public UploadResponse(String url) {
            this.url = url;
        }

        public String getUrl() {
            return url;
        }
    }

    @GetMapping("/getSales")
    public Map<String, Object> getSales(@RequestParam String type) {
        Map<String, Object> result = new HashMap<>();
        if (type.equals("last7days")) {
            result.put("dates", orderRepository.getOrderStats7Days());
            result.put("salesComplete", orderRepository.getSalesCompleteByWeek());
            result.put("sales", orderRepository.getSalesByWeek());
        } else if (type.equals("month")) {
            result.put("dates", orderRepository.getOrderStatsMonth());
            result.put("salesComplete", orderRepository.getSalesCompleteByMonth());
            result.put("sales", orderRepository.getSalesByMonth());
        } else if (type.equals("lastmonth")) {
            result.put("dates", orderRepository.getOrderStatsLastMonth());
            result.put("salesComplete", orderRepository.getSalesCompleteByLastMonth());
            result.put("sales", orderRepository.getSalesByLastMonth());
        }

        return result;
    }

    @GetMapping("/getOrders")
    public Map<String, Object> getOrdersStats(@RequestParam String type) {
        Map<String, Object> result = new HashMap<>();
        if (type.equals("last7days")) {
            result.put("dates", orderRepository.getOrderStats7Days());
            result.put("ordersComplete", orderRepository.getOrdersComplete7Days());
            result.put("orders", orderRepository.getOrders7Days());
        } else if (type.equals("month")) {
            result.put("dates", orderRepository.getOrderStatsMonth());
            result.put("ordersComplete", orderRepository.getOrdersCompleteMonth());
            result.put("orders", orderRepository.getOrdersMonth());
        } else if (type.equals("lastmonth")) {
            result.put("dates", orderRepository.getOrderStatsLastMonth());
            result.put("ordersComplete", orderRepository.getOrdersCompleteLastMonth());
            result.put("orders", orderRepository.getOrdersLastMonth());
        }

        return result;
    }

    @GetMapping("/getOrder")
    public Map<String, Object> getOrder() {
        Map<String, Object> result = new HashMap<>();
        result.put("totalSalesComplete", currencyFormat.format(orderRepository.getTotalSalesCompleteByMonth()));
        result.put("totalSales", currencyFormat.format(orderRepository.getTotalSalesByMonth() != null ? orderRepository.getTotalSalesByMonth() : 0));
        result.put("totalSalesCompleteLastMonth", currencyFormat.format(orderRepository.getTotalSalesCompleteByLastMonth() != null ? orderRepository.getTotalSalesCompleteByLastMonth() : 0));
        result.put("totalSalesLastMonth", currencyFormat.format(orderRepository.getTotalSalesByLastMonth() != null ? orderRepository.getTotalSalesByLastMonth() : 0));
        result.put("ordersStatus", orderRepository.getOrdersGroupByStatus());
        result.put("countOrders", orderRepository.countOrdersGroupByStatus());
        result.put("ordersComplete", orderRepository.getOrdersComplete());
        result.put("countOrdersByMonth", orderRepository.countOrdersByMonth());
        return result;
    }

    @GetMapping("/getProducts")
    public Map<String, Object> getProducts() {
        Map<String, Object> result = new HashMap<>();
        result.put("productNames", orderRepository.getProductsTop());
        result.put("sumProducts", orderRepository.sumTotalPriceProductTop());

        return result;
    }

    @GetMapping("/getTotalPriceByUsers")
    public Map<String, Object> getTotalPriceByUsers() {
        Map<String, Object> result = new HashMap<>();
        result.put("getUsersTop", orderRepository.getUsersTop());
        result.put("sumTotalPriceByUsers", orderRepository.sumTotalPriceByUsers());

        return result;
    }

    @GetMapping("/update-order-status")
    public Map<String, Object> updateOrderStatus(@RequestParam Integer id, @RequestParam Integer st) throws MessagingException, UnsupportedEncodingException {
        OrderStatus[] values = OrderStatus.values();
        String status = null;

        for (OrderStatus os : values) {
            if (os.getId().equals(st)) {
                status = os.getName();
            }
        }
        Map<String, Object> result = new HashMap<>();
        result.put("statusName", orderService.updateOrderStatus(id, status).getStatus());
        return result;
    }

    @GetMapping("/updateSts")
    public Map<String, Object> updateUserAccount(@RequestParam Boolean status, @RequestParam Integer id, HttpSession session) throws IOException {
        Map<String, Object> result = new HashMap<>();
        result.put("statusName", userService.updateAccountStatus(id, status).getIsEnable() ? "Bật" : "Tắt");
        return result;
    }

    @GetMapping("/delete-news")
    public Map<String, Object> deleteNews(@RequestParam Integer id) {
        Map<String, Object> result = new HashMap<>();
        result.put("isDelete", newsService.deleteNews(id));
        return result;
    }

    @GetMapping("/deleteCart")
    public Map<String, Object> deleteCart(@RequestParam Integer id, @RequestParam Integer userId) {
        Map<String, Object> result = new HashMap<>();
        result.put("isDelete", cartService.deleteCart(id));
        Double totalPrice = cartService.getTotalPrice(userId);
        if (!ObjectUtils.isEmpty(totalPrice)) {
            result.put("totalPrice", NumberFormat.getCurrencyInstance(
                    new Locale("vi", "VN")).format(totalPrice));
        } else {
            result.put("totalPrice", 0);
        }
        return result;
    }

    @GetMapping("updateQuantity")
    public Map<String, Object> updateQuantities(@RequestParam Integer id, @RequestParam Integer quantity, @RequestParam Integer userId) {
        Map<String, Object> result = new HashMap<>();
        result.put("isUpdate", cartService.updateQuantity(id, quantity));
        result.put("totalPrice", NumberFormat.getCurrencyInstance(
                new Locale("vi", "VN")).format(cartService.getTotalPrice(userId)));
        return result;
    }

    @GetMapping("addRate")
    public Map<String, Object> addRate(@RequestParam Integer userId,
                                       @RequestParam Integer productOrderId,
                                       @RequestParam String textComment,
                                       @RequestParam Integer rating) {
        Map<String, Object> result = new HashMap<>();
        ProductOrder productOrder = orderService.ratingProduct(userId, productOrderId, textComment, rating);
        result.put("productOrder", productOrder);
        return result;
    }

    @GetMapping("getProductOrder")
    public Map<String, Object> getProductOrder(@RequestParam Integer productOrderId) {
        Map<String, Object> result = new HashMap<>();
        ProductOrder productOrder = productOrderRepository.findById(productOrderId).get();
        result.put("productImage", productOrder.getProduct().getArrayAnh()[0]);
        result.put("productName", productOrder.getProduct().getTen());
        result.put("productPrice", productOrder.getProduct().getGiaSaleFormatted());
        result.put("quantity", productOrder.getQuantity());
        result.put("productTotalPrice", productOrder.getTotalPriceFormatted());
        result.put("rating", productOrder.getRating().getRating());

        Comment comment = productOrder.getRating().getComment();

        result.put("textContent", comment.getContent());
        result.put("email", orderRepository.findByOrderId(productOrder.getOrderId()).getUser().getEmail());
        result.put("commentId", productOrder.getRating().getComment().getId());

        Comment rep = commentRepository.findByParentComment(comment);

        result.put("repComment", rep == null ? "" : rep.getContent());

        return result;
    }

    @GetMapping("getComments")
    public Map<String, Object> getComments() {
        Map<String, Object> result = new HashMap<>();
        Map<Long, Map<String, Object>> replyComments = commentRepository.findByLevel(1)
                .stream()
                .collect(Collectors.toMap(
                        comment -> comment.getParentComment().getId(),
                        comment -> {
                            Map<String, Object> commentMap = new HashMap<>();
                            commentMap.put("id", comment.getId());
                            commentMap.put("content", comment.getContent());
                            return commentMap;
                        }
                ));
        List<Map<String, Object>> productOrders = productOrderService.getProductOrdersIsRated()
                .stream()
                .map(productOrder -> {
                    Map<String, Object> order = new HashMap<>();
                    order.put("id", productOrder.getId());
                    order.put("parentId", productOrder.getRating().getComment().getId());
                    order.put("orderId", productOrder.getOrderId());
                    order.put("productName", productOrder.getProduct().getTen());
                    order.put("rating", productOrder.getRating().getRating());
                    order.put("comment", productOrder.getRating().getComment().getContent());
                    return order;
                })
                .collect(Collectors.toList());
        result.put("replyComments", replyComments);
        result.put("productOrders", productOrders);
        return result;
    }

    @GetMapping("postComment")
    public Map<String, Object> postComment(@RequestParam Long commentId, @RequestParam String replyComment) {
        Map<String, Object> result = new HashMap<>();
        Comment reply = commentService.addReply(commentId, replyComment, userRepository.getFirstAdmin());
        if (!ObjectUtils.isEmpty(reply)) {
            result.put("replyContent", reply.getContent());
        } else {
            result.put("replyContent", false);
        }
        return result;
    }

    @GetMapping("getContactUrl")
    public List<ContactUrl> getContactUrl() {
        return contactUrlService.getContactUrls();
    }

    @PostMapping("/addContact")
    public ResponseEntity<String> addContact(
            @RequestParam("name") String name,
            @RequestParam("url") String url) throws IOException {

        contactUrlService.addContactUrl(name, url);

        return ResponseEntity.ok("Contact added successfully");
    }

    @GetMapping("/deleteContactUrl")
    public ResponseEntity<String> deleteContactUrl(@RequestParam("id") Integer id) throws IOException {

        contactUrlService.deleteContactUrl(id);

        return ResponseEntity.ok("Contact added successfully");
    }

    @GetMapping("getSlider")
    public List<Slider> getSlider() {
        return sliderService.getSliderList();
    }

    @PostMapping("/addSlider")
    public ResponseEntity<String> addSlider(
            @RequestParam("img") MultipartFile img,
            @RequestParam("url") String url) throws IOException {

        sliderService.addSlider(img, url);

        return ResponseEntity.ok("Contact added successfully");
    }

    @GetMapping("/deleteSlide")
    public ResponseEntity<String> deleteSlide(@RequestParam("id") Integer id) throws IOException {

        sliderService.deleteSlider(id);

        return ResponseEntity.ok("Contact added successfully");
    }


    @GetMapping("getSupportUrl")
    public List<SupportUrl> getSupportUrl() {
        return supportUrlService.getSupportUrl().reversed();
    }

    @PostMapping("/addSupportUrl")
    public ResponseEntity<String> addSupportUrl(
            @RequestParam("name") String title,
            @RequestParam("url") String url,
            @RequestParam("position") String position) {

        supportUrlService.addSupportUrl(title, url, position);

        return ResponseEntity.ok("Contact added successfully");
    }

    @GetMapping("/deletesupportUrl")
    public ResponseEntity<String> deletesupportUrl(@RequestParam("id") Integer id) {

        supportUrlService.deleteSupportUrl(id);

        return ResponseEntity.ok("Contact added successfully");
    }

    @GetMapping("/xoa-thuong-hieu")
    public ResponseEntity<String> deleteBrand(@RequestParam("id") Integer id) {

        brandService.deleteBrand(id);

        return ResponseEntity.ok("Brand deleted successfully");
    }

    @GetMapping("/xoa-danh-muc")
    public ResponseEntity<String> deleteCategory(@RequestParam long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok("Category deleted successfully");
    }

    @GetMapping("/xoa-san-pham")
    public ResponseEntity<String> deleteProduct(@RequestParam int id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok("Product deleted successfully");
    }

}
