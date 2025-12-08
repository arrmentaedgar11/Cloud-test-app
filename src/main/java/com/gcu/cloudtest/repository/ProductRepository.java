package com.gcu.cloudtest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gcu.cloudtest.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
}
