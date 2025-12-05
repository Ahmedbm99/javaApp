package com.example.controller;

import com.example.model.Product;
import com.example.service.ProductService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.math.BigDecimal;
import java.util.List;

/**
 * Contrôleur REST pour gérer les produits
 */
@Path("/api/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductController {
    
    private final ProductService productService;
    
    public ProductController() {
        this.productService = new ProductService();
    }
    
    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    
    /**
     * Récupère tous les produits
     * GET /api/products
     */
    @GET
    public Response getAllProducts() {
        try {
            List<Product> products = productService.getAllProducts();
            return Response.ok(products).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}").build();
        }
    }
    
    /**
     * Récupère un produit par son ID
     * GET /api/products/{id}
     */
    @GET
    @Path("/{id}")
    public Response getProductById(@PathParam("id") Long id) {
        try {
            return productService.getProductById(id)
                    .map(product -> Response.ok(product).build())
                    .orElse(Response.status(Response.Status.NOT_FOUND)
                            .entity("{\"error\": \"Produit non trouvé avec l'ID: " + id + "\"}").build());
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}").build();
        }
    }
    
    /**
     * Crée un nouveau produit
     * POST /api/products
     */
    @POST
    public Response createProduct(Product product) {
        try {
            Product createdProduct = productService.createProduct(product);
            return Response.status(Response.Status.CREATED).entity(createdProduct).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}").build();
        }
    }
    
    /**
     * Met à jour un produit
     * PUT /api/products/{id}
     */
    @PUT
    @Path("/{id}")
    public Response updateProduct(@PathParam("id") Long id, Product product) {
        try {
            Product updatedProduct = productService.updateProduct(id, product);
            return Response.ok(updatedProduct).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}").build();
        }
    }
    
    /**
     * Supprime un produit
     * DELETE /api/products/{id}
     */
    @DELETE
    @Path("/{id}")
    public Response deleteProduct(@PathParam("id") Long id) {
        try {
            boolean deleted = productService.deleteProduct(id);
            if (deleted) {
                return Response.noContent().build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\": \"Produit non trouvé avec l'ID: " + id + "\"}").build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}").build();
        }
    }
    
    /**
     * Récupère les produits par catégorie
     * GET /api/products/category/{category}
     */
    @GET
    @Path("/category/{category}")
    public Response getProductsByCategory(@PathParam("category") String category) {
        try {
            List<Product> products = productService.getProductsByCategory(category);
            return Response.ok(products).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}").build();
        }
    }
    
    /**
     * Récupère les produits en stock
     * GET /api/products/instock
     */
    @GET
    @Path("/instock")
    public Response getProductsInStock() {
        try {
            List<Product> products = productService.getProductsInStock();
            return Response.ok(products).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}").build();
        }
    }
    
    /**
     * Récupère les produits par prix maximum
     * GET /api/products/price/{maxPrice}
     */
    @GET
    @Path("/price/{maxPrice}")
    public Response getProductsByMaxPrice(@PathParam("maxPrice") BigDecimal maxPrice) {
        try {
            List<Product> products = productService.getProductsByMaxPrice(maxPrice);
            return Response.ok(products).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}").build();
        }
    }
    
    /**
     * Compte le nombre de produits
     * GET /api/products/count
     */
    @GET
    @Path("/count")
    public Response countProducts() {
        try {
            long count = productService.countProducts();
            return Response.ok("{\"count\": " + count + "}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}").build();
        }
    }
}
