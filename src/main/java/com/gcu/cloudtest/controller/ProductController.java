package com.gcu.cloudtest.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.gcu.cloudtest.model.Product;
import com.gcu.cloudtest.repository.ProductRepository;

@Controller
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // Home page to product
    @GetMapping("/")
    public String homeRedirect() {
        logger.info("Redirecting from / to /products");
        return "redirect:/products";
    }

    // List all products
    @GetMapping("/products")
    public String listProducts(Model model) {
        logger.info("Listing all products");
        List<Product> products = productRepository.findAll();
        model.addAttribute("products", products);
        return "products";
    }

   // create new product
    @GetMapping("/products/new")
    public String showCreateForm(Model model) {
        logger.info("Showing create product form");
        model.addAttribute("product", new Product());
        model.addAttribute("formTitle", "Add Product");
        model.addAttribute("submitButtonText", "Create");
        model.addAttribute("isEdit", false);
        return "product-form";
    }

    // Submit product
    @PostMapping("/products")
    public String createProduct(@ModelAttribute("product") Product product) {
        logger.info("Creating product: {}", product.getName());
        productRepository.save(product);
        return "redirect:/products";
    }

    // Edit Product
    @GetMapping("/products/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        logger.info("Showing edit form for product id {}", id);
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isPresent()) {
            model.addAttribute("product", productOptional.get());
            model.addAttribute("formTitle", "Edit Product");
            model.addAttribute("submitButtonText", "Update");
            model.addAttribute("isEdit", true);
            return "product-form";
        } else {
            logger.info("Product id {} not found, redirecting to list", id);
            return "redirect:/products";
        }
    }

    // Handle product submit
    @PostMapping("/products/update/{id}")
    public String updateProduct(@PathVariable("id") Long id, @ModelAttribute("product") Product product) {
        logger.info("Updating product id {}", id);
        product.setId(id);
        productRepository.save(product);
        return "redirect:/products";
    }

    // Delete a product
    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable("id") Long id) {
        logger.info("Deleting product id {}", id);
        productRepository.deleteById(id);
        return "redirect:/products";
    }

    // Show product details
    @GetMapping("/products/{id}")
    public String viewProductDetails(@PathVariable("id") Long id, Model model) {
        logger.info("Viewing details for product id {}", id);
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isPresent()) {
            model.addAttribute("product", productOptional.get());
            return "product-details";
        } else {
            logger.info("Product id {} not found, redirecting to list", id);
            return "redirect:/products";
        }
    }
}
