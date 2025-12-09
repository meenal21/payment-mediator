package org.example.payment.api;

import org.example.payment.api.dto.CheckoutRequest;
import org.example.payment.api.dto.CheckoutResponse;
import org.example.payment.api.dto.PayRequest;
import org.example.payment.api.dto.PaymentStatusResponse;
import org.example.payment.mediator.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<CheckoutResponse> checkout(@RequestBody CheckoutRequest request) {
        CheckoutResponse response = paymentService.createCheckout(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentStatusResponse> getStatus(@PathVariable Long id) {
        PaymentStatusResponse response = paymentService.getStatus(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/gateway-success")
    public ResponseEntity<PaymentStatusResponse> gatewaySuccess(
            @PathVariable Long id,
            @RequestBody(required = false) PayRequest request) {

        // request can be null if you don't need paymentMethod
        if (request == null) {
            request = new PayRequest();
            request.setPaymentMethod("DUMMY");
        }
        return ResponseEntity.ok(paymentService.simulateGatewaySuccess(id, request));
    }

    @PostMapping("/{id}/delivered")
    public ResponseEntity<PaymentStatusResponse> delivered(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.markDelivered(id));
    }

}
