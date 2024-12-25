package com.dacs2.controller;

import com.dacs2.model.*;
import com.dacs2.service.*;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.*;

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

    @Autowired
    private NewsService newsService;

    @Autowired
    private WebInfoService webInfoService;

    @Autowired
    private BrandService brandService;

    @ModelAttribute
    public void getUserDetails(Principal p, Model m) {

        if (p != null) {
            String email = p.getName();
            UserDtls userDtls = userService.getUserByEmail(email);
            m.addAttribute("user", userDtls);
            m.addAttribute("countCart", cartService.getCountCart(userDtls.getId()));
            m.addAttribute("webInfo", webInfoService.getWebInfo());
        }

        m.addAttribute("categorys", categoryService.getCategoryByIsActive());

    }

    @GetMapping("")
    public String index() {
        return "admin/index";
    }

    @GetMapping("/danh-muc")
    public String category(Model m) {
        m.addAttribute("categorys", categoryService.getAllCategory().reversed());
        return "admin/category";
    }

    @PostMapping("/luu-danh-muc")
    public String saveCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file, HttpSession session) throws IOException {
        category.setName(category.getName().trim());
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

                Path path = Paths.get(imgPath + File.separator + "category_img" + File.separator + file.getOriginalFilename());

                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                session.setAttribute("succMsg", "Dữ liệu đã được lưu!");
            }
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
        category.setName(category.getName().trim());

        Category currentCategory = categoryService.getCategoryById(category.getId());
        String imageName = file.isEmpty() ? currentCategory.getImageName():file.getOriginalFilename();
        category.setImageName(imageName);

        if (currentCategory.getName().equals(category.getName())) {
            Category savedCategory = categoryService.saveCategory(category);

            if (ObjectUtils.isEmpty(savedCategory)) {
                session.setAttribute("errorMsg", "Dữ liệu chưa được lưu!");
            } else {

                if (!file.isEmpty()) {
                    Path path = Paths.get(imgPath + File.separator + "category_img" + File.separator + file.getOriginalFilename());

                    Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                }
                session.setAttribute("succMsg", "Danh mục đã được cập nhật");
            }
        } else {
            Boolean existCategory = categoryService.existCategory(category.getName());

            if (existCategory) {
                session.setAttribute("errorMsg", "Tên danh mục đã tồn tại.");
            } else {

                Category savedCategory = categoryService.saveCategory(category);

                if (ObjectUtils.isEmpty(savedCategory)) {
                    session.setAttribute("errorMsg", "Dữ liệu chưa được lưu!");
                } else {

                    if (!file.isEmpty()) {
                        Path path = Paths.get(imgPath + File.separator + "category_img" + File.separator + file.getOriginalFilename());

                        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                    }
                    session.setAttribute("succMsg", "Danh mục đã được cập nhật");
                }
            }
        }

        return "redirect:/admin/sua-danh-muc/" + category.getId();
    }

    @GetMapping("/thuong-hieu")
    public String brand(Model m) {
        m.addAttribute("brands", brandService.getAllBrand().reversed());
        return "admin/brand";
    }

    @PostMapping("/luu-thuong-hieu")
    public String saveBrand(@ModelAttribute Brand brand, HttpSession session) {

        brand.setName(brand.getName().trim());

        Boolean existBrand = brandService.existBrand(brand.getName());

        if (existBrand) {
            session.setAttribute("errorMsg", "Tên thương hiệu đã tồn tại.");
        } else {

            Brand savedBrand = brandService.save(brand);

            if (ObjectUtils.isEmpty(savedBrand)) {
                session.setAttribute("errorMsg", "Đã thêm tên thương hiệu!");
            } else {
                session.setAttribute("succMsg", "Dữ liệu đã được lưu!");
            }
        }

        return "redirect:/admin/thuong-hieu";
    }

    @GetMapping("/sua-thuong-hieu/{id}")
    public String loadEditBrand(@PathVariable Integer id, Model m) {
        m.addAttribute("brand", brandService.getBrandById(id));
        return "admin/edit_brand";
    }

    @PostMapping("/cap-nhat-thuong-hieu")
    public String updateBrand(@ModelAttribute Brand brand, HttpSession session) {

        brand.setName(brand.getName().trim());

        Brand currentBrand = brandService.getBrandById(brand.getId());

        if (currentBrand.getName().equals(brand.getName())) {
            Brand savedBrand = brandService.save(brand);

            if (!ObjectUtils.isEmpty(savedBrand)) {
                session.setAttribute("succMsg", "Đã cập nhật thương hiệu");
            } else {
                session.setAttribute("errorMsg", "Lỗi");
            }
        } else {
            Boolean existBrand = brandService.existBrand(brand.getName());

            if (existBrand) {
                session.setAttribute("errorMsg", "Tên thương hiệu đã tồn tại.");
            } else {

                Brand savedBrand = brandService.save(brand);

                if (!ObjectUtils.isEmpty(savedBrand)) {
                    session.setAttribute("succMsg", "Đã cập nhật thương hiệu");
                } else {
                    session.setAttribute("errorMsg", "Lỗi");
                }

            }
        }

        return "redirect:/admin/sua-thuong-hieu/" + brand.getId();
    }

    @GetMapping("/san-pham")
    public String loadViewProduct(Model m,
                                  @RequestParam(name = "trang", defaultValue = "1") Integer pageNumber,
                                  @RequestParam(value = "sap-xep", defaultValue = "") String sapxep,
                                  @RequestParam(name = "pageSize", defaultValue = "40") Integer pageSize) {
        Page<Product> page = productService.getAllProductsPagination(pageNumber - 1, pageSize, sapxep);
        m.addAttribute("search", false);
        m.addAttribute("products", page.getContent());
        m.addAttribute("sapXep", sapxep);
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
        m.addAttribute("brands", brandService.getAllBrand());
        return "admin/add_product";
    }

    @GetMapping("/sua-san-pham/{id}")
    public String loadEditProduct(@PathVariable int id, Model m) {
        m.addAttribute("product", productService.getProductById(id));
        m.addAttribute("categories", categoryService.getAllCategory());
        m.addAttribute("brands", brandService.getAllBrand());
        return "admin/edit_product";
    }

    @GetMapping("/test")
    public String uploadTest() {
        return "test";
    }

    @PostMapping("/luu-san-pham")
    public String saveProduct(@ModelAttribute Product product, @RequestParam("brand") Integer brandId, @RequestParam("category") Long categoryId, @RequestParam(value = "imageNames", required = false) String fileNames, HttpSession session) throws IOException {
        String[] imageNames = fileNames.replace("\"", "").split(",");
        ArrayList<String> newImageNames = new ArrayList<>();

        String sourcePath = imgPath + File.separator + "temp_img" + File.separator;
        String targetPath = imgPath + File.separator + "product_img" + File.separator;

        for (String imageName : imageNames) {
            Files.move(Paths.get(sourcePath + imageName), Paths.get(targetPath + imageName), StandardCopyOption.REPLACE_EXISTING);
            newImageNames.add(imageName);
        }
        
        product.setAnh(newImageNames.toString().replace("[", "").replace("]",""));
        product.setGiasale(product.getGia() * (100 - product.getSale()) / 100);
        product.setDanhmuc(categoryService.getCategoryById(categoryId));
        product.setBrand(brandService.getBrandById(brandId));
        Product saveProduct = productService.saveProduct(product);

        if (!ObjectUtils.isEmpty(saveProduct)) {
            session.setAttribute("succMsg", "Sản phẩm đã được lưu.");
        } else {
            session.setAttribute("errorMsg", "Lỗi");
        }

        return "redirect:/admin/them-san-pham";
    }

    @PostMapping("/cap-nhat-san-pham")
    public String updateProduct(@ModelAttribute Product product, @RequestParam("brand") Integer brandId, @RequestParam("category") Long categoryId, @RequestParam(value = "imageNames", required = false) String fileNames, HttpSession session) throws IOException {
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
                Files.move(Paths.get(sourcePath + imageName), Paths.get(targetPath + imageName), StandardCopyOption.REPLACE_EXISTING);
                newImageNames.add(imageName);
            }

        }

        product1.setTen(product.getTen());
        product1.setGia(product.getGia());
        product1.setMota(product.getMota());
        product1.setSoluong(product.getSoluong());
        product1.setDanhmuc(categoryService.getCategoryById(categoryId));
        product1.setBrand(brandService.getBrandById(brandId));
        product1.setAnh(product.getAnh());
        product1.setTrangthai(product.getTrangthai());
        product1.setSale(product.getSale());
        product1.setGiasale(product.getGia() * (100 - product.getSale()) / 100);
        System.out.println(newImageNames.toString().replace("[", "").replace("]",""));
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
                              @RequestParam(name = "pageSize", defaultValue = "40") Integer pageSize) {

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

    @GetMapping("/don-hang")
    public String getAllOrders(Model m,
                               @RequestParam(name = "trang", defaultValue = "1") Integer pageNumber,
                               @RequestParam(name = "pageSize", defaultValue = "40") Integer pageSize) {

        m.addAttribute("searchCh", "");
        m.addAttribute("categories", categoryService.getCategoryByIsActive());
        m.addAttribute("search", false);
        Page<Orders> page = orderService.getAllOrdersPagination(pageNumber - 1, pageSize);
        m.addAttribute("orders", page.getContent());
        m.addAttribute("trang", page.getNumber());
        m.addAttribute("pageSize", pageSize);
        m.addAttribute("totalElements", page.getTotalElements());
        m.addAttribute("totalPages", page.getTotalPages());
        m.addAttribute("isFirst", page.isFirst());
        m.addAttribute("isLast", page.isLast());

        return "/admin/orders";
    }

    @GetMapping("/search-don-hang")
    public String searchOrderProduct(@RequestParam String orderId, Model m,
                                     @RequestParam(name = "trang", defaultValue = "1") Integer pageNumber,
                                     @RequestParam(name = "pageSize", defaultValue = "40") Integer pageSize) {
        if (orderId.trim().isEmpty()) {
            return "redirect:/admin/don-hang";
        }

        Page<Orders> page = orderService.searchOrderByOrderIdPagination(pageNumber - 1, pageSize, orderId.trim());

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
                                @RequestParam(value = "sap-xep", defaultValue = "") String sapxep,
                                @RequestParam(name = "trang", defaultValue = "1") Integer pageNumber,
                                @RequestParam(name = "pageSize", defaultValue = "40") Integer pageSize) {
        if (ch.trim().isEmpty()) {
            return "redirect:/admin/san-pham";
        }
        Page<Product> page = productService.searchProdcutOnAdmin(pageNumber - 1, pageSize, ch.trim(), sapxep);
        m.addAttribute("sapXep", sapxep);
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

    @GetMapping("/them-bai-viet")
    public String addNews() {
        return "/admin/add_news";
    }

    @PostMapping("/save-news")
    public String saveNews(@ModelAttribute News news, @RequestParam("file") MultipartFile file, HttpSession session) throws IOException {

        if (!file.isEmpty()) {
            news.setImageName(file.getOriginalFilename());
            Path path = Paths.get(imgPath + File.separator + "news" + File.separator + file.getOriginalFilename());

            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        }

        if (!ObjectUtils.isEmpty(newsService.saveNews(news))) {
            session.setAttribute("succMsg", "Đã thêm bài viết!");
        } else {
            session.setAttribute("errorMsg", "Lỗi!");
        }

        return "redirect:/admin/them-bai-viet";

    }

    @GetMapping("/bai-viet")
    public String getAllNews(Model m,
                             @RequestParam(name = "trang", defaultValue = "1") Integer pageNumber,
                             @RequestParam(name = "pageSize", defaultValue = "40") Integer pageSize) {

        m.addAttribute("searchCh", "");
        m.addAttribute("search", false);
        Page<News> page = newsService.getNewsByPage(pageNumber - 1, pageSize);
        m.addAttribute("newsList", page.getContent());
        m.addAttribute("trang", page.getNumber());
        m.addAttribute("pageSize", pageSize);
        m.addAttribute("totalElements", page.getTotalElements());
        m.addAttribute("totalPages", page.getTotalPages());
        m.addAttribute("isFirst", page.isFirst());
        m.addAttribute("isLast", page.isLast());


        return "/admin/news";

    }

    @GetMapping("/search-bai-viet")
    public String searchNews(Model m,
                             @RequestParam String ch,
                             @RequestParam(name = "trang", defaultValue = "1") Integer pageNumber,
                             @RequestParam(name = "pageSize", defaultValue = "40") Integer pageSize) {
        if (ch.trim().isEmpty()) {
            return "redirect:/admin/bai-viet";
        }

        m.addAttribute("searchCh", "");
        m.addAttribute("search", false);
        Page<News> page = newsService.searchNews(ch.trim(), pageNumber - 1, pageSize);
        m.addAttribute("newsList", page.getContent());
        m.addAttribute("trang", page.getNumber());
        m.addAttribute("pageSize", pageSize);
        m.addAttribute("totalElements", page.getTotalElements());
        m.addAttribute("totalPages", page.getTotalPages());
        m.addAttribute("isFirst", page.isFirst());
        m.addAttribute("isLast", page.isLast());


        return "/admin/news";

    }

    @GetMapping("/cap-nhat-bai-viet/{id}")
    public String loadEditNews(@PathVariable Integer id, Model m) {
        m.addAttribute("news", newsService.getNewsById(id));
        return "/admin/edit_news";
    }

    @PostMapping("/update-news")
    public String updateNews(@ModelAttribute News news, @RequestParam("file") MultipartFile file, HttpSession session) throws IOException {
        News updateNews = newsService.getNewsById(news.getId());

        if (!file.isEmpty()) {
            updateNews.setImageName(file.getOriginalFilename());
            Path path = Paths.get(imgPath + File.separator + "news" + File.separator + file.getOriginalFilename());

            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        }

        if (!ObjectUtils.isEmpty(news)) {
            updateNews.setTitle(news.getTitle());
            updateNews.setContent(news.getContent());
            updateNews.setStatus(news.getStatus());
            updateNews.setStyle(news.getStyle());
        }

        if (!ObjectUtils.isEmpty(newsService.updateNews(updateNews))) {
            session.setAttribute("succMsg", "Đã cập nhật thành công!");
        } else {
            session.setAttribute("errorMsg", "Lỗi!");
        }

        return "redirect:/admin/cap-nhat-bai-viet/" + news.getId();

    }

    @GetMapping("/danh-gia")
    public String danhGia() {
        return "/admin/rating";
    }

    @GetMapping("/khac")
    public String other() {
        return "/admin/other";
    }

    @PostMapping("/update-webcomponents")
    public String updateWebComponents(@ModelAttribute WebInfo webInfo, @RequestParam(value = "img", required = false) MultipartFile img, HttpSession session) throws IOException {

        if (!ObjectUtils.isEmpty(webInfoService.updateWebInfo(webInfo, img))) {
            session.setAttribute("succMsg", "Đã cập nhật thông tin website của bạn!");
        } else {
            session.setAttribute("errorMsg", "Lỗi cập nhật!");
        }
        return "redirect:/admin/khac";
    }

}


