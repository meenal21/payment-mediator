// src/components/BuyerCheckoutPage.jsx
import React, { useState } from "react";
import {
  createCheckout,
  getPaymentStatus,
  simulateGatewaySuccess,
  markDelivered,
} from "../api/paymentApi";
import "./BuyerCheckoutPage.css";

const ORDER_ITEMS = [
  {
    id: 1,
    name: "Noise-Cancelling Wireless Headphones",
    sellerName: "Acme Audio Store",
    price: 1499,
    qty: 1,
  },
];

const BUYER_ID = 1;
const SELLER_ID = 101;

export default function BuyerCheckoutPage() {
  const [paymentId, setPaymentId] = useState(null);
  const [status, setStatus] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [paymentMethod, setPaymentMethod] = useState("UPI");

  const subtotal = ORDER_ITEMS.reduce(
    (sum, item) => sum + item.price * item.qty,
    0
  );
  const taxAmount = status?.taxAmount ?? 0;
  const total = subtotal + taxAmount;

  const handlePlaceOrder = async () => {
    try {
      setLoading(true);
      setError("");
      setStatus(null);
      setPaymentId(null);

      const data = await createCheckout({
        buyerId: BUYER_ID,
        sellerId: SELLER_ID,
        amount: subtotal,
      });

      setPaymentId(data.paymentId);
      const fullStatus = await getPaymentStatus(data.paymentId);
      setStatus(fullStatus);
    } catch (err) {
      console.error(err);
      setError(err.response?.data?.message || err.message || "Checkout failed");
    } finally {
      setLoading(false);
    }
  };

  const refreshStatus = async () => {
    if (!paymentId) return;
    try {
      setLoading(true);
      setError("");
      const s = await getPaymentStatus(paymentId);
      setStatus(s);
    } catch (err) {
      console.error(err);
      setError(err.response?.data?.message || err.message || "Failed to fetch status");
    } finally {
      setLoading(false);
    }
  };

  const handlePay = async () => {
    if (!paymentId) return;
    try {
      setLoading(true);
      setError("");
      const newStatus = await simulateGatewaySuccess(paymentId, paymentMethod);
      setStatus(newStatus);
    } catch (err) {
      console.error(err);
      setError(err.response?.data?.message || err.message || "Payment failed");
    } finally {
      setLoading(false);
    }
  };

  const handleDelivered = async () => {
    if (!paymentId) return;
    try {
      setLoading(true);
      setError("");
      const newStatus = await markDelivered(paymentId);
      setStatus(newStatus);
    } catch (err) {
      console.error(err);
      setError(err.response?.data?.message || err.message || "Failed to mark delivered");
    } finally {
      setLoading(false);
    }
  };

  const state = status?.state;

  const stepStatus = (target) => {
    const order = [
      "CREATED",
      "TAXED",
      "ESCROW_CREATED",
      "DELIVERED",
      "PAYOUT_COMPLETED",
    ];

    if (!state) return "pending";
    const currentIdx = order.indexOf(state);
    const idx = order.indexOf(target);

    if (currentIdx === -1 || idx === -1) return "pending";
    if (currentIdx > idx) return "done";
    if (currentIdx === idx) return "active";
    return "pending";
  };

  return (
    <div className="buyer-page">
      <header className="buyer-header">
        <div className="logo">Marketplace</div>
        <div className="header-right">
          <span className="header-link">Help</span>
          <span className="avatar">M</span>
        </div>
      </header>

      <main className="buyer-main">
        <div className="checkout-layout">
          {/* LEFT: Order summary */}
          <section className="card order-card">
            <h2 className="card-title">Order summary</h2>

            <div className="order-items">
              {ORDER_ITEMS.map((item) => (
                <div key={item.id} className="order-item">
                  <div className="item-thumb">
                    <div className="thumb-placeholder">🎧</div>
                  </div>
                  <div className="item-info">
                    <div className="item-name">{item.name}</div>
                    <div className="item-meta">
                      Sold by <span className="seller-name">{item.sellerName}</span>
                    </div>
                    <div className="item-meta">Qty: {item.qty}</div>
                  </div>
                  <div className="item-price">₹{item.price}</div>
                </div>
              ))}
            </div>

            <div className="price-breakup">
              <div className="price-row">
                <span>Subtotal</span>
                <span>₹{subtotal}</span>
              </div>
              <div className="price-row">
                <span>Tax (simulated)</span>
                <span>
                  {status?.taxAmount != null ? `₹${status.taxAmount}` : "—"}
                </span>
              </div>
              <div className="price-row total-row">
                <span>To pay</span>
                <span>₹{status?.state ? status.amount + (status.taxAmount ?? 0) : total}</span>
              </div>
            </div>

            <div className="order-footer">
              {!paymentId && (
                <button
                  className="btn btn-primary full-width"
                  onClick={handlePlaceOrder}
                  disabled={loading}
                >
                  {loading ? "Placing order..." : "Calculate Tax & Place Order"}
                </button>
              )}

              {paymentId && (
                <button
                  className="btn btn-ghost full-width"
                  onClick={refreshStatus}
                  disabled={loading}
                >
                  Refresh status
                </button>
              )}

              {error && <div className="alert alert-error">{error}</div>}
            </div>
          </section>

          {/* RIGHT: Payment + tracking */}
          <section className="right-column">
            <div className="card payment-card">
              <h2 className="card-title">Payment</h2>

              {!paymentId && (
                <p className="muted-text">
                  Place your order first to see payment options.
                </p>
              )}

              {paymentId && state === "CREATED" && (
                <p className="muted-text">
                  Refresh and proceed to pay.
                </p>
              )}

              {paymentId && state !== "CREATED" && (
                <>
                  <div className="payment-header">
                    <div>
                      <div className="label">Payment ID</div>
                      <div className="value mono">#{paymentId}</div>
                    </div>
                    <div className="pill">
                      {state === "TAXED" && "Awaiting payment"}
                      {state === "ESCROW_CREATED" && "Payment successful"}
                      {state === "PAYOUT_COMPLETED" && "Completed"}
                      {!["TAXED", "ESCROW_CREATED", "PAYOUT_COMPLETED"].includes(
                        state || ""
                      ) && state}
                    </div>
                  </div>

                  {/* Payment methods */}
                  <div className="payment-methods-card">
                    <div className="section-title">Choose payment method</div>
                    <div className="muted-text small">
                      This simulates the gateway UI. No real money is charged.
                    </div>

                    <div className="pm-list">
                      <label className={`pm-option ${paymentMethod === "UPI" ? "pm-active" : ""}`}>
                        <input
                          type="radio"
                          name="paymentMethod"
                          value="UPI"
                          checked={paymentMethod === "UPI"}
                          onChange={(e) => setPaymentMethod(e.target.value)}
                        />
                        <div className="pm-body">
                          <div className="pm-title">UPI</div>
                          <div className="pm-desc">Google Pay, PhonePe, Paytm UPI</div>
                        </div>
                        <div className="pm-tag">Recommended</div>
                      </label>

                      <label
                        className={`pm-option ${
                          paymentMethod === "CARD" ? "pm-active" : ""
                        }`}
                      >
                        <input
                          type="radio"
                          name="paymentMethod"
                          value="CARD"
                          checked={paymentMethod === "CARD"}
                          onChange={(e) => setPaymentMethod(e.target.value)}
                        />
                        <div className="pm-body">
                          <div className="pm-title">Credit / Debit Card</div>
                          <div className="pm-desc">Visa, Mastercard, RuPay</div>
                        </div>
                      </label>

                      <label
                        className={`pm-option ${
                          paymentMethod === "NETBANKING" ? "pm-active" : ""
                        }`}
                      >
                        <input
                          type="radio"
                          name="paymentMethod"
                          value="NETBANKING"
                          checked={paymentMethod === "NETBANKING"}
                          onChange={(e) => setPaymentMethod(e.target.value)}
                        />
                        <div className="pm-body">
                          <div className="pm-title">Netbanking</div>
                          <div className="pm-desc">All major Indian banks</div>
                        </div>
                      </label>
                    </div>

                    <button
                      className="btn btn-primary full-width"
                      onClick={handlePay}
                      disabled={loading || state !== "TAXED"}
                    >
                      {loading ? "Processing payment..." : "Pay securely"}
                    </button>

                    {state && state !== "TAXED" && (
                      <div className="hint-text">
                        {state === "ESCROW_CREATED"
                          ? "Payment captured into escrow. You can now confirm delivery."
                          : state === "PAYOUT_COMPLETED"
                          ? "Order fully completed and payout done."
                          : "Payment step has progressed."}
                      </div>
                    )}
                  </div>
                </>
              )}
            </div>

            
          </section>
          <section className="card tracking-card">
            {/* Tracking timeline */}
            <div className="card tracking-card">
              <h3 className="card-title">Order status</h3>

              {!paymentId && (
                <p className="muted-text small">
                  Track your order from payment to payout once you place it.
                </p>
              )}

              {paymentId && (
                <>
                  <ol className="timeline">
                    <li className={`timeline-step ${stepStatus("CREATED")}`}>
                      <div className="dot" />
                      <div className="timeline-content">
                        <div className="step-title">Order placed</div>
                        <div className="step-desc">
                          We have created your payment request.
                        </div>
                      </div>
                    </li>

                    <li className={`timeline-step ${stepStatus("TAXED")}`}>
                      <div className="dot" />
                      <div className="timeline-content">
                        <div className="step-title">Tax calculated</div>
                        <div className="step-desc">
                          Final payable amount is computed.
                        </div>
                      </div>
                    </li>

                    <li className={`timeline-step ${stepStatus("ESCROW_CREATED")}`}>
                      <div className="dot" />
                      <div className="timeline-content">
                        <div className="step-title">Paid & in escrow</div>
                        <div className="step-desc">
                          Your money is safely held until delivery.
                        </div>
                      </div>
                    </li>

                    <li className={`timeline-step ${stepStatus("DELIVERED")}`}>
                      <div className="dot" />
                      <div className="timeline-content">
                        <div className="step-title">
                          Delivered{" "}
                          {state === "ESCROW_CREATED" && (
                            <button
                              className="link-button"
                              onClick={handleDelivered}
                              disabled={loading}
                            >
                              Confirm delivery
                            </button>
                          )}
                        </div>
                        <div className="step-desc">
                          After confirmation, we’ll capture and payout.
                        </div>
                      </div>
                    </li>

                    <li className={`timeline-step ${stepStatus("PAYOUT_COMPLETED")}`}>
                      <div className="dot" />
                      <div className="timeline-content">
                        <div className="step-title">Completed</div>
                        <div className="step-desc">
                          Payment captured and payout sent to seller.
                        </div>
                      </div>
                    </li>
                  </ol>

                  {state === "PAYOUT_COMPLETED" && (
                    <div className="success-banner">
                      🎉 Your order is complete. Thanks for shopping with Marketplace.
                    </div>
                  )}
                </>
              )}
            </div>
            </section>
        </div>
      </main>
    </div>
  );
}
