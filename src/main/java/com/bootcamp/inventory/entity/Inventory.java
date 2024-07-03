package com.bootcamp.inventory.entity;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name="inventory")
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true)
    private String sku;

    //    private Type type;
//    public enum Type{
//        WRAP, PROTEIN, VEGGIES, CHEESE, SAUCE
//    }
    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String status;
//    public enum Status{
//        CREATED, MODIFIED, SOLD
//    }

    @Column(nullable = false)
    private String location;

    //    @Column(nullable = false, columnDefinition = "JSON")
//    private JsonNode attributes;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String attributes;

    @Column(nullable = false)
    private double cost;

    @Column(nullable = false)
    private double sellingPrice;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private String createdBy;

    public void setId(long id){
        this.id=id;
    }
    public void setSku(String sku) {
        this.sku = sku;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    //    public void setAttributes(JsonNode attributes) {
//        this.attributes = attributes;
//    }
    public void setAttributes(JsonNode attributes) {
        this.attributes = JsonUtils.toJsonString(attributes);
    }
    public void setCost(double cost) {
        this.cost = cost;
    }

    public void setSellingPrice(double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public long getId() {
        return id;
    }

    public String getSku() {
        return sku;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    public String getLocation() {
        return location;
    }

    //    public JsonNode getAttributes() {
//        return attributes;
//    }
    public JsonNode getAttributes() {
        return JsonUtils.toJsonNode(this.attributes);
    }
    public double getCost() {
        return cost;
    }

    public double getSellingPrice() {
        return sellingPrice;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }
}
