package com.gcu.cloudtest.controller;

import java.util.ArrayList;
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

import com.gcu.cloudtest.model.CartItem;
import com.gcu.cloudtest.model.Product;
import com.gcu.cloudtest.repository.ProductRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // Home/Products

    @GetMapping("/")
    public String homeRedirect() {
        logger.info("Redirecting from / to /products");
        return "redirect:/products";
    }

    @GetMapping("/products")
    public String listProducts(Model model) {
        logger.info("Listing all products");
        List<Product> products = productRepository.findAll();
        model.addAttribute("products", products);
        return "products";
    }

    // Summary

    @GetMapping("/products/summary")
    public String productSummary(Model model) {
        logger.info("Showing product summary");
        List<Product> products = productRepository.findAll();

        long productCount = products.size();
        int totalQuantity = products.stream()
                .mapToInt(p -> p.getQuantity() == null ? 0 : p.getQuantity())
                .sum();
        double totalValue = products.stream()
                .mapToDouble(p -> {
                    if (p.getPrice() == null || p.getQuantity() == null) {
                        return 0.0;
                    }
                    return p.getPrice() * p.getQuantity();
                })
                .sum();

        model.addAttribute("products", products);
        model.addAttribute("productCount", productCount);
        model.addAttribute("totalQuantity", totalQuantity);
        model.addAttribute("totalValue", totalValue);

        return "product-summary";
    }

    // Create, Edit, Delete

    @GetMapping("/products/new")
    public String showCreateForm(Model model) {
        logger.info("Showing create product form");
        model.addAttribute("product", new Product());
        model.addAttribute("formTitle", "Add Product");
        model.addAttribute("submitButtonText", "Create");
        model.addAttribute("isEdit", false);
        return "product-form";
    }

    @PostMapping("/products")
    public String createProduct(@ModelAttribute("product") Product product) {
        logger.info("Creating product: {}", product.getName());
        productRepository.save(product);
        return "redirect:/products";
    }

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

    @PostMapping("/products/update/{id}")
    public String updateProduct(@PathVariable("id") Long id, @ModelAttribute("product") Product product) {
        logger.info("Updating product id {}", id);
        product.setId(id);
        productRepository.save(product);
        return "redirect:/products";
    }

    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable("id") Long id) {
        logger.info("Deleting product id {}", id);
        productRepository.deleteById(id);
        return "redirect:/products";
    }

    // product details

    @GetMapping("/products/{id}")
    public String viewProductDetails(@PathVariable("id") Long id, Model model) {
        logger.info("Viewing details for product id {}", id);
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isPresent()) {
            model.addAttribute("product", productOptional.get());
            return "product-details";
        } else {
            logger.info("Product id {} not found, redirecting to list");
            return "redirect:/products";
        }
    }

    

    @SuppressWarnings("unchecked")
    private List<CartItem> getCart(HttpSession session) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute("cart", cart);
        }
        return cart;
    }

    // Cart Items

    @GetMapping("/cart")
    public String viewCart(Model model, HttpSession session) {
        logger.info("Viewing cart");
        List<CartItem> cart = getCart(session);

        double total = cart.stream()
                .mapToDouble(item -> {
                    Double price = item.getProduct().getPrice();
                    int qty = item.getQuantity();
                    if (price == null) {
                        return 0.0;
                    }
                    return price * qty;
                })
                .sum();

        model.addAttribute("cartItems", cart);
        model.addAttribute("total", total);

        return "cart";
    }

    @GetMapping("/cart/add/{id}")
    public String addToCart(@PathVariable("id") Long id, HttpSession session) {
        logger.info("Adding product id {} to cart", id);
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isPresent()) {
            List<CartItem> cart = getCart(session);
            boolean found = false;

            for (CartItem item : cart) {
                if (item.getProduct().getId().equals(id)) {
                    item.setQuantity(item.getQuantity() + 1);
                    found = true;
                    break;
                }
            }

            if (!found) {
                cart.add(new CartItem(productOptional.get(), 1));
            }

            session.setAttribute("cart", cart);
        }
        return "redirect:/cart";
    }

    @GetMapping("/cart/remove/{id}")
    public String removeFromCart(@PathVariable("id") Long id, HttpSession session) {
        logger.info("Removing product id {} from cart", id);
        List<CartItem> cart = getCart(session);
        cart.removeIf(item -> item.getProduct().getId().equals(id));
        session.setAttribute("cart", cart);
        return "redirect:/cart";
    }

    @GetMapping("/cart/clear")
    public String clearCart(HttpSession session) {
        logger.info("Clearing cart");
        session.removeAttribute("cart");
        return "redirect:/cart";
    }
}
