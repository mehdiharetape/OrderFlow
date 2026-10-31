package com.example.order_flow.advices;

import com.example.order_flow.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdviceExceptions {

    @ExceptionHandler(IllegalOrNullArgumentException.class)
    public ResponseEntity<String> handleIllegalOrNullArgumentException(){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Invalid parameter!");
    }

    @ExceptionHandler(ProductExistsException.class)
    public ResponseEntity<String> handleProductExistsException(){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("This product already exist!");
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<String> handleProductNotFoundException(){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Product not found!");
    }

    @ExceptionHandler(InventoryProductExistException.class)
    public ResponseEntity<String> handleInventoryProductExistException(){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Inventory for this product already exist!");
    }

    @ExceptionHandler(InventoryNotFoundException.class)
    public ResponseEntity<String> handleInventoryNotFoundException(){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Inventory with this id not found!");
    }

    @ExceptionHandler(InvalidQuantityChangeException.class)
    public ResponseEntity<String> handleInvalidQuantityChangeException(){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Invalid amount to change quantity!");
    }

    @ExceptionHandler(InvalidReservationAmountException.class)
    public ResponseEntity<String> handleInvalidReservationAmountException(){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Out of bound reservation amount!");
    }

    @ExceptionHandler(InventoryNotFoundForProductException.class)
    public ResponseEntity<String> handleInventoryNotFoundForProductException(){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Inventory not found for this product!");
    }

    @ExceptionHandler(MovementNotExistsForProductException.class)
    public ResponseEntity<String> handleMovementNotExistsForProductException(){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Inventory Movement not found for this product!");
    }

    //
    @ExceptionHandler(InactiveProductException.class)
    public ResponseEntity<String> handleInactiveProductException(
            InactiveProductException ex)
    {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<String> handleOrderNotFoundException(
            OrderNotFoundException ex)
    {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    @ExceptionHandler(NullOrderItemListException.class)
    public ResponseEntity<String> handleNullOrderItemListException(){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Order Items is null !!");
    }

    @ExceptionHandler(InvalidOrderItemException.class)
    public ResponseEntity<String> handleInvalidOrderItemException(){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Order Item is invalid !!");
    }

    @ExceptionHandler(DuplicateProductInOrderException.class)
    public ResponseEntity<String> handleDuplicateProductInOrderException(
            DuplicateProductInOrderException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    @ExceptionHandler(OrderCancelFailException.class)
    public ResponseEntity<String> handleOrderCancelFailException(
            OrderCancelFailException ex)
    {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }
}
