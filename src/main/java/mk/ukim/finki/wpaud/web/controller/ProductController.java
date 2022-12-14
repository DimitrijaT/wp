package mk.ukim.finki.wpaud.web.controller;


import mk.ukim.finki.wpaud.model.Category;
import mk.ukim.finki.wpaud.model.Manufacturer;
import mk.ukim.finki.wpaud.model.Product;
import mk.ukim.finki.wpaud.service.CategoryService;
import mk.ukim.finki.wpaud.service.ManufacturerService;
import mk.ukim.finki.wpaud.service.ProductService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final ManufacturerService manufacturerService;

    public ProductController(ProductService productService, CategoryService categoryService, ManufacturerService manufacturerService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.manufacturerService = manufacturerService;
    }

    @GetMapping
    public String getProductPage(@RequestParam(required = false) String error, Model model) {
        if (error != null && !error.isEmpty()) {
            model.addAttribute("hasError", true);
            model.addAttribute("error", error);
        }
        //во моделот ќе ги инјектираме сите продукти
        List<Product> products = this.productService.findAll();
        model.addAttribute("products", products);


        model.addAttribute("bodyContent", "products");
        return "master-template";
    }

    /**
     * localhost:8080/products/67 <- @PathVariable <br>
     * localhost:8080/products?id=78 <- @RequestParam
     */
    @DeleteMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        this.productService.deleteById(id);
        return "redirect:/products";
    }


    /**
     * Зошто ни треба моделот? Во рамки на формата сакаме да ја излистаме целата листа на производители и
     * целата листа на категори и листа на производители со цел да корисникот на клик избере соодветно.
     * Истото треба да го направиме и за manufacturer
     */
    @GetMapping("add-form")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    //мора да специфицираме add-form поради тоа што getProductPage го користи default-ниот "/products" mapping.
    public String addProductPage(Model model) {
        List<Category> categories = this.categoryService.listCategories();
        List<Manufacturer> manufacturers = this.manufacturerService.findAll();

        model.addAttribute("categories", categories);
        model.addAttribute("manufacturers", manufacturers);

        model.addAttribute("bodyContent", "add-product");
        return "master-template";
    }

    @GetMapping("/edit-form/{id}")
    public String editProductPage(@PathVariable Long id, Model model) {
        if (this.productService.findById(id).isPresent()) {
            Product product = this.productService.findById(id).get();
            List<Category> categories = this.categoryService.listCategories();
            List<Manufacturer> manufacturers = this.manufacturerService.findAll();

            model.addAttribute("categories", categories);
            model.addAttribute("manufacturers", manufacturers);
            model.addAttribute("product", product);

            model.addAttribute("bodyContent", "add-product");
            return "master-template";
        }
        return "redirect:/products?error=ProductNotFound";
    }

    @PostMapping("/add")
    public String saveProduct(
            @RequestParam(required = false) Long id,
            @RequestParam String name,
            @RequestParam Double price,
            @RequestParam Integer quantity,
            @RequestParam Long category,
            @RequestParam Long manufacturer) {
        if (id != null) {
            this.productService.edit(id, name, price, quantity, category, manufacturer);
        } else {
            this.productService.save(name, price, quantity, category, manufacturer);
        }
        return "redirect:/products";
    }
}



