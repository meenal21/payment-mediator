// src/api/paymentApi.js
import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080",
});

export async function createCheckout({ buyerId, sellerId, amount }) {
  const idempotencyKey = `checkout-${buyerId}-${Date.now()}`;
  const res = await api.post("/payments/checkout", {
    buyerId,
    sellerId,
    amount,
    idempotencyKey,
  });
  return res.data; // { paymentId, state, amount }
}

export async function getPaymentStatus(paymentId) {
  const res = await api.get(`/payments/${paymentId}`);
  return res.data; // { id, state, amount, taxAmount }
}

export async function simulateGatewaySuccess(paymentId, paymentMethod) {
  const res = await api.post(`/payments/${paymentId}/gateway-success`, {
    paymentMethod,
  });
  return res.data; // PaymentStatusResponse
}

export async function markDelivered(paymentId) {
  const res = await api.post(`/payments/${paymentId}/delivered`);
  return res.data; // PaymentStatusResponse
}
