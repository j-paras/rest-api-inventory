package com.bootcamp.inventory.service.validator;

import com.bootcamp.inventory.configuration.ItemType;
import com.bootcamp.inventory.entity.Inventory;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class CreateInventoryRequestValidator {
    public static final double MAX_ALLOWED_PRICE = 1000000.0;
    public static final double MIN_ALLOWED_PRICE = 0.0;

    private boolean isValidRequest = false;

    public boolean isValid(Inventory inventory) {
        this.typeIsValid(inventory.getType())
                .locationIsValid(inventory.getLocation())
                .isAttributesValid(inventory.getType(), inventory.getAttributes())
                .priceIsValid(inventory.getCost())
                .priceIsValid(inventory.getSellingPrice());
        return isValidRequest;
    }

    private CreateInventoryRequestValidator typeIsValid(String type) {
        if (!isValidRequest) return this;
        if (type.isEmpty()) {
            isValidRequest = false;
            return this;
        }
        for (ItemType itemType : ItemType.values()) {
            if (type.toUpperCase().equals(itemType.name())) {
                isValidRequest = true;
                return this;
            }
        }
        isValidRequest = false;
        return this;

    }

    private CreateInventoryRequestValidator locationIsValid(String location) {
        if (!isValidRequest) return this;

        if (location == null || location.isEmpty()) {
            isValidRequest = false;
            return this;
        }
        String pattern = "^.*$";
        isValidRequest = location.matches(pattern);
        return this;
    }

    private CreateInventoryRequestValidator priceIsValid(double price) {
        if (!isValidRequest) return this;

        if (price < MIN_ALLOWED_PRICE || price > MAX_ALLOWED_PRICE) {
            isValidRequest = false;
            return this;
        }
        isValidRequest = Math.round(price * 100.0) / 100.0 == price;
        return this;
    }

    private CreateInventoryRequestValidator isAttributesValid(String type, JsonNode attributes) {
        if (!isValidRequest) return this;

        switch (type) {
            case "CAR":
            case "BIKE":
                return validateVehicleAttributes(attributes);
            case "MOBILE":
                return validateMobileAttributes(attributes);
            default:
                isValidRequest = false;
                return this;
        }
    }

    private CreateInventoryRequestValidator validateVehicleAttributes(JsonNode attributes) {
        String vin = attributes.get("vin") != null ? attributes.get("vin").asText() : "";
        String brand = attributes.get("brand") != null ? attributes.get("brand").asText() : "";
        String model = attributes.get("model") != null ? attributes.get("model").asText() : "";
        String yearStr = attributes.get("year") != null ? attributes.get("year").asText() : "";
        int year;
        try {
            year = Integer.parseInt(yearStr);
        } catch (NumberFormatException e) {
            isValidRequest = false;
            return this;
        }
        isValidRequest = !vin.isEmpty() && !brand.isEmpty() && !model.isEmpty() && isValidYear(year, 2015);
        return this;
    }

    private CreateInventoryRequestValidator validateMobileAttributes(JsonNode attributes) {
        String imei = attributes.get("imei") != null ? attributes.get("imei").asText() : "";
        String brand = attributes.get("brand") != null ? attributes.get("brand").asText() : "";
        String model = attributes.get("model") != null ? attributes.get("model").asText() : "";
        int year = attributes.get("year") != null ? attributes.get("year").asInt() : 0;
        isValidRequest = !imei.isEmpty() && !brand.isEmpty() && !model.isEmpty() && isValidYear(year, 2020);
        return this;
    }

    private boolean isValidYear(int current_year, int min_year) {
        return current_year >= min_year && current_year <= LocalDate.now().getYear();
    }
}
