package com.dacs2.controller;

import com.dacs2.model.Category;
import com.dacs2.model.Product;
import com.dacs2.model.ProductOrder;
import com.dacs2.model.UserDtls;
import com.dacs2.service.*;
import com.dacs2.util.OrderStatus;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

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
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @ModelAttribute
    public void getUserDetails(Principal p, Model m) {

        if (p != null) {
            String email = p.getName();
            UserDtls userDtls = userService.getUserByEmail(email);
            m.addAttribute("user", userDtls);
            m.addAttribute("countCart", cartService.getCountCart(userDtls.getId()));
        }

        m.addAttribute("categorys", categoryService.getCategoryByIsActive());

    }

    @GetMapping("")
    public String index() {
        return "admin/index";
    }

    @GetMapping("/danh-muc")
    public String category(Model m) {
        m.addAttribute("categorys", categoryService.getAllCategory());
        return "admin/category";
    }

    @PostMapping("/luu-danh-muc")
    public String saveCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file, HttpSession session) throws IOException {

        Boolean existCategory = categoryService.existCategory(category.getName());

        String imageName = file != null ? file.getOriginalFilename(): "default.jpg";
        category.setImageName(imageName);
        if (existCategory) {
            session.setAttribute("errorMsg", "Tên danh mục đã tồn tại.");
        } else {

            Category savedCategory = categoryService.saveCategory(category);

            if (ObjectUtils.isEmpty(savedCategory)) {
                session.setAttribute("errorMsg", "Dữ liệu chưa được lưu!");
            } else {

                Path path = Paths.get(imgPath + File.separator + "category_img" + File.separator + savedCategory.getId() + "_" + file.getOriginalFilename());

                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                session.setAttribute("succMsg", "Dữ liệu đã được lưu!");
            }
        }

        return "redirect:/admin/danh-muc";
    }

    @GetMapping("/xoa-danh-muc/{id}")
    public String deleteCategory(@PathVariable long id, HttpSession session) {
        Boolean deleteCategory = categoryService.deleteCategory(id);

        if (deleteCategory) {
            session.setAttribute("succMsg", "Danh mục đã được xóa");
        } else {
            session.setAttribute("errorMsg", "Lỗi");
        }
        return "redirect:/admin/danh-muc";
    }

    @GetMapping("/sua-danh-muc/{id}")
    public String loadEditCategory(@PathVariable long id, Model m) {
        m.addAttribute("category", categoryService.getCategoryById(id));
        return "admin/edit_category";
    }

    @PostMapping("/cap-nhat-danh-muc")
    public String updateCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file, HttpSession session) throws IOException {

        Category category1 = categoryService.getCategoryById(category.getId());
        String imageName = file.isEmpty() ? category1.getImageName():file.getOriginalFilename();

        if (!ObjectUtils.isEmpty(category)) {
            category1.setName(category.getName());
            category1.setIsActive(category.getIsActive());
            category1.setImageName(imageName);
        }

        Category updateCategory = categoryService.saveCategory(category1);

        if (!ObjectUtils.isEmpty(updateCategory)) {

            if (!file.isEmpty()) {
                Path path = Paths.get(imgPath + File.separator + "category_img" + File.separator + category.getId() + "_" + file.getOriginalFilename());

                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }
            session.setAttribute("succMsg", "Danh mục đã được cập nhật");
        } else {
            session.setAttribute("errorMsg", "Lỗi");
        }

        return "redirect:/admin/sua-danh-muc/" + category.getId();
    }

    @GetMapping("/san-pham")
    public String loadViewProduct(Model m,
                                  @RequestParam(name = "trang", defaultValue = "1") Integer pageNumber,
                                  @RequestParam(name = "pageSize", defaultValue = "15") Integer pageSize) {
        Page<Product> page = productService.getAllProductsPagination(pageNumber - 1, pageSize);
        m.addAttribute("search", false);
        m.addAttribute("products", page.getContent());
        m.addAttribute("trang", page.getNumber());
        m.addAttribute("pageSize", pageSize);
        m.addAttribute("totalElements", page.getTotalElements());
        m.addAttribute("totalPages", page.getTotalPages());
        m.addAttribute("isFirst", page.isFirst());
        m.addAttribute("isLast", page.isLast());
        return "admin/products";
    }

    @GetMapping("/them-san-pham")
    public String loadAddProduct(Model m) {
        List<Category> categories = categoryService.getAllCategory();
        m.addAttribute("categories", categories);
        return "admin/add_product";
    }

    @GetMapping("/xoa-san-pham/{id}")
    public String deleteProduct(@PathVariable int id, HttpSession session) {
        Boolean deleteProduct = productService.deleteProduct(id);
        if (deleteProduct) {
            session.setAttribute("succMsg", "Danh mục đã được xóa");
        } else {
            session.setAttribute("errorMsg", "Lỗi");
        }
        return "redirect:/admin/san-pham";
    }

    @GetMapping("/sua-san-pham/{id}")
    public String loadEditProduct(@PathVariable int id, Model m) {
        m.addAttribute("product", productService.getProductById(id));
        m.addAttribute("categories", categoryService.getAllCategory());
        return "admin/edit_product";
    }

    @GetMapping("/test")
    public String uploadTest() {
        return "test";
    }

    @PostMapping("/luu-san-pham")
    public String saveProduct(@ModelAttribute Product product, @RequestParam(value = "imageNames", required = false) String fileNames, HttpSession session) throws IOException {
        int productId = productService.getMaxProductId() + 1;

        String[] imageNames = fileNames.replace("\"", "").split(",");
        ArrayList<String> newImageNames = new ArrayList<>();

        String sourcePath = imgPath + File.separator + "temp_img" + File.separator;
        String targetPath = imgPath + File.separator + "product_img" + File.separator;

        for (String imageName : imageNames) {
            Files.move(Paths.get(sourcePath + imageName), Paths.get(targetPath + productId + "_" + imageName), StandardCopyOption.REPLACE_EXISTING);
            newImageNames.add(productId + "_" + imageName);
        }
        
        product.setAnh(newImageNames.toString().replace("[", "").replace("]",""));
        product.setGiasale(product.getGia() * (100 - product.getSale()) / 100);

        Product saveProduct = productService.saveProduct(product);

        if (!ObjectUtils.isEmpty(saveProduct)) {
            session.setAttribute("succMsg", "Sản phẩm đã được lưu.");
        } else {
            session.setAttribute("errorMsg", "Lỗi");
        }

        return "redirect:/admin/them-san-pham";
    }

    @PostMapping("/cap-nhat-san-pham")
    public String updateProduct(@ModelAttribute Product product, @RequestParam(value = "imageNames", required = false) String fileNames, HttpSession session) throws IOException {
        int index;
        int productId = product.getId();
        Product product1 = productService.getProductById(productId);

        String[] imageNames = fileNames.replace("\"", "").split(",");
        ArrayList<String> newImageNames = new ArrayList<>();

//        list ảnh cũ
        List<String> listAnhCu = new ArrayList<>(Arrays.asList(product1.getArrayAnh()));

        String sourcePath = imgPath + File.separator + "temp_img" + File.separator;
        String targetPath = imgPath + File.separator + "product_img" + File.separator;

        for (String imageName: imageNames) {
            if (listAnhCu.size() > 0 && listAnhCu.contains(imageName)) {
                newImageNames.add(imageName);
                index = listAnhCu.indexOf(imageName);
                listAnhCu.remove(index);
            } else {
                Files.move(Paths.get(sourcePath + imageName), Paths.get(targetPath + productId + "_" + imageName), StandardCopyOption.REPLACE_EXISTING);
                newImageNames.add(productId + "_" + imageName);
            }

        }

        for (String imageName : listAnhCu) {
            System.out.println(imageName);
            Files.deleteIfExists(Paths.get(targetPath + imageName));
        }

        product1.setTen(product.getTen());
        product1.setGia(product.getGia());
        product1.setMota(product.getMota());
        product1.setSoluong(product.getSoluong());
        product1.setDanhmuc(product.getDanhmuc());
        product1.setAnh(product.getAnh());
        product1.setTrangthai(product.getTrangthai());
        product1.setSale(product.getSale());
        product1.setGiasale(product.getGia() * (100 - product.getSale()) / 100);
        product1.setAnh(newImageNames.toString().replace("[", "").replace("]",""));

        Product updateProduct = productService.saveProduct(product1);

        if (!ObjectUtils.isEmpty(updateProduct)) {
            session.setAttribute("succMsg", "Sản phẩm đã cập nhật.");
        } else {
            session.setAttribute("errorMsg", "Lỗi");
        }

        return "redirect:/admin/sua-san-pham/" + product.getId();

    }

    @PostMapping("/xoa-anh")
    public String deleteImage(@RequestParam("fileName") String fileName) {
        String filePath = imgPath + File.separator + "temp_img" + File.separator + fileName;
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }

        return "redirect:/admin/sua-san-pham";
    }

    @PostMapping("/luu-anh")
    public String saveImage(MultipartHttpServletRequest request) throws IOException {

        Iterator<String> itr = request.getFileNames();

        while (itr.hasNext()) {
            String uploadedFile = itr.next();
            MultipartFile file = request.getFile(uploadedFile);
            Path path = Paths.get(imgPath + File.separator + "temp_img" + File.separator + file.getOriginalFilename());

            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        }

        return "redirect:/admin/them-san-pham";
    }

    @GetMapping("/users")
    public String getAllUsers(Model m,
                              @RequestParam(name = "trang", defaultValue = "1") Integer pageNumber,
                              @RequestParam(name = "pageSize", defaultValue = "20") Integer pageSize) {

        m.addAttribute("searchCh", "");
        m.addAttribute("search", false);
        Page<UserDtls> page = userService.getUsers(pageNumber - 1, pageSize, "ROLE_USER");
        m.addAttribute("users", page.getContent());
        m.addAttribute("trang", page.getNumber());
        m.addAttribute("pageSize", pageSize);
        m.addAttribute("totalElements", page.getTotalElements());
        m.addAttribute("totalPages", page.getTotalPages());
        m.addAttribute("isFirst", page.isFirst());
        m.addAttribute("isLast", page.isLast());
        return "/admin/users";
    }

    @GetMapping("/search-users")
    public String searchUsers(Model m, String search,
                              @RequestParam(name = "trang", defaultValue = "1") Integer pageNumber,
                              @RequestParam(name = "pageSize", defaultValue = "20") Integer pageSize) {
        if (search.trim().isEmpty()) {
            return "redirect:/admin/users";
        }
        m.addAttribute("searchCh", search);
        m.addAttribute("search", true);
        Page<UserDtls> page = userService.searchUsers(pageNumber - 1, pageSize, "ROLE_USER", search.trim());
        m.addAttribute("users", page.getContent());
        m.addAttribute("trang", page.getNumber());
        m.addAttribute("pageSize", pageSize);
        m.addAttribute("totalElements", page.getTotalElements());
        m.addAttribute("totalPages", page.getTotalPages());
        m.addAttribute("isFirst", page.isFirst());
        m.addAttribute("isLast", page.isLast());
        return "/admin/users";
    }

    @GetMapping("/updateSts")
    public String updateUserAccount(@RequestParam Boolean status, @RequestParam String role, @RequestParam Integer id, HttpSession session) throws IOException {
        Boolean f = userService.updateAccountStatus(id, status);
        if (f) {
            session.setAttribute("succMsg", "Trạng thái của tài khoản đã được cập nhật!");
        } else {
            session.setAttribute("errorMsg", "Lỗi!");
        }
        if (role == "ROLE_USER") {
            return "redirect:/admin/users";
        }
        return "redirect:/admin/admin";
    }

    @GetMapping("/don-hang")
    public String getAllOrders(Model m,
                               @RequestParam(name = "trang", defaultValue = "1") Integer pageNumber,
                               @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {

        m.addAttribute("searchCh", "");
        m.addAttribute("categories", categoryService.getCategoryByIsActive());
        m.addAttribute("search", false);
        Page<ProductOrder> page = orderService.getAllOrdersPagination(pageNumber - 1, pageSize);
        m.addAttribute("orders", page.getContent());
        m.addAttribute("trang", page.getNumber());
        m.addAttribute("pageSize", pageSize);
        m.addAttribute("totalElements", page.getTotalElements());
        m.addAttribute("totalPages", page.getTotalPages());
        m.addAttribute("isFirst", page.isFirst());
        m.addAttribute("isLast", page.isLast());

        return "/admin/orders";
    }

    @PostMapping("/update-order-status")
    public String updateOrderStatus(@RequestParam String searchOrderId, @RequestParam Integer id, @RequestParam Integer st, @RequestParam Integer trang, HttpSession session) throws MessagingException, UnsupportedEncodingException {
        OrderStatus[] values = OrderStatus.values();
        String status = null;

        for (OrderStatus os : values) {
            if (os.getId().equals(st)) {
                status = os.getName();
            }
        }

        if (!ObjectUtils.isEmpty(orderService.updateOrderStatus(id, status))) {
            session.setAttribute("succMsg", "Đã cập nhật đơn hàng!");
        } else {
            session.setAttribute("succMsg", "Không thể cập nhật đơn hàng!");
        }

        if (searchOrderId.trim().isEmpty()) {
            return "redirect:/admin/don-hang";
        } else {
            if (trang != null) {
                return "redirect:/admin/search-don-hang?orderId=" + searchOrderId + "&trang=" + trang;
            }
            return "redirect:/admin/search-don-hang?orderId=" + searchOrderId;
        }

    }

    @GetMapping("/search-don-hang")
    public String searchOrderProduct(@RequestParam String orderId, Model m,
                                     @RequestParam(name = "trang", defaultValue = "1") Integer pageNumber,
                                     @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        if (orderId.trim().isEmpty()) {
            return "redirect:/admin/don-hang";
        }

        Page<ProductOrder> page = orderService.searchOrderByOrderIdPagination(pageNumber - 1, pageSize, orderId.trim());

        if (ObjectUtils.isEmpty(page.getContent())) {
            m.addAttribute("searchResult", false);
        } else {
            m.addAttribute("searchResult", true);
        }

        m.addAttribute("search", true);
        m.addAttribute("orders", page.getContent());
        m.addAttribute("searchOrderId", orderId);
        m.addAttribute("trang", page.getNumber());
        m.addAttribute("pageSize", pageSize);
        m.addAttribute("totalElements", page.getTotalElements());
        m.addAttribute("totalPages", page.getTotalPages());
        m.addAttribute("isFirst", page.isFirst());
        m.addAttribute("isLast", page.isLast());
        return "/admin/orders";
    }

    @GetMapping("/search-san-pham")
    public String searchProduct(@RequestParam String ch, Model m,
                                @RequestParam(name = "trang", defaultValue = "1") Integer pageNumber,
                                @RequestParam(name = "pageSize", defaultValue = "1") Integer pageSize) {
        if (ch.trim().isEmpty()) {
            return "redirect:/admin/san-pham";
        }
        Page<Product> page = productService.searchProdcutOnAdmin(pageNumber - 1, pageSize, ch.trim());

        if (ObjectUtils.isEmpty(page.getContent())) {
            m.addAttribute("searchResult", false);
        } else {
            m.addAttribute("searchResult", true);
        }

        m.addAttribute("search", true);
        m.addAttribute("products", page.getContent());
        m.addAttribute("searchProduct", ch);
        m.addAttribute("trang", page.getNumber());
        m.addAttribute("pageSize", pageSize);
        m.addAttribute("totalElements", page.getTotalElements());
        m.addAttribute("totalPages", page.getTotalPages());
        m.addAttribute("isFirst", page.isFirst());
        m.addAttribute("isLast", page.isLast());

        return "/admin/products";
    }

    @GetMapping("/them-admin")
    public String addAdmin() {
        return "/admin/add_admin";
    }

    @PostMapping("/luu-admin")
    public String saveAdmin(@ModelAttribute UserDtls user,
                            @RequestParam("img") MultipartFile file,
                            HttpSession session) throws IOException {

        if (userService.existsEmail(user.getEmail())) {
            session.setAttribute("errorMsg", "Email này đã tồn tại!");
            return "redirect:/admin";
        }
        String imageName = file.isEmpty() ? "default.jpg" : file.getOriginalFilename();
        user.setProfileImage(imageName);
        UserDtls saveUser = userService.saveAmin(user);

        if (!ObjectUtils.isEmpty(saveUser)) {
            if (!file.isEmpty()) {

                Path path = Paths.get(imgPath + File.separator + "profile_img" + File.separator + imageName);

                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }
            session.setAttribute("succMsg", "Thêm tài khoản admin thành công!");
        } else {
            session.setAttribute("errorMsg", "Lỗi!");
        }

        return "redirect:/admin";
    }

    @GetMapping("/profile")
    public String profile() {
        return "/admin/profile";
    }

    @PostMapping("/update-profile")
    public String updateProfile(@ModelAttribute UserDtls user, @RequestParam("img") MultipartFile img, HttpSession session) throws IOException {

        if (!ObjectUtils.isEmpty(userService.updateUserProfile(user, img))) {
            session.setAttribute("succMsg", "Đã cập nhật profile của bạn!");
        } else {
            session.setAttribute("errorMsg", "Lỗi cập nhật profile!");
        }


        return "redirect:/admin/profile";
    }

    private UserDtls getLoggedInUserDetails(Principal p) {
        String email = p.getName();
        return userService.getUserByEmail(email);
    }

    @PostMapping("/change-password")
    public String changPassword(@RequestParam String currentPassword, @RequestParam String newPassword, Principal p, HttpSession session) {
        UserDtls user = getLoggedInUserDetails(p);

        if (passwordEncoder.matches(currentPassword, user.getPassword())) {
            String newEncodePassword = passwordEncoder.encode(newPassword);
            user.setPassword(newEncodePassword);
            if (!ObjectUtils.isEmpty(userService.updateUser(user))) {
                session.setAttribute("succMsg", "Thay đổi mật khẩu thành công!");
            } else {
                session.setAttribute("errorMsg", "Chưa thể thay đổi mật khẩu!");
            }
        } else {
            session.setAttribute("errorMsg", "Mật khẩu hiện tại không đúng!");
        }

        return "redirect:/admin/profile";
    }

    @GetMapping("/admin")
    public String admin(Model m) {
        m.addAttribute("admins", userService.getAllAdmin());
        return "/admin/admin";
    }

}


