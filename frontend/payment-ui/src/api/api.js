// src/api.js
import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080", // your backend
});

export async function createCheckout(data) {
  const res = await api.post("/payments/checkout", data);
  return res.data; // { paymentId, state, amount }
}

export async function fetchPaymentStatus(paymentId) {
  const res = await api.get(`/payments/${paymentId}`);
  return res.data; // { id, state, amount, taxAmount }
}
