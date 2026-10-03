import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api";
import { logout } from "../utils/auth";
import "./OwnerDashboard.css";

function OwnerDashboard() {
  const [payments, setPayments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [confirmingId, setConfirmingId] = useState(null);
  const [message, setMessage] = useState("");

  const navigate = useNavigate();

  const fetchPayments = async () => {
    try {
      const response = await api.get("/payments");

      console.log("All payments:", response.data);

      setPayments(response.data);
    } catch (error) {
      console.error("Failed to fetch payments:", error);

      setError("Failed to load payments.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchPayments();
  }, []);

  const handleLogout = () => {
    logout();
    window.location.href = "/";
    };

  const handleConfirmPayment = async (payment) => {
    try {
      setConfirmingId(payment.id);
      setMessage("");

      await api.post(`/payments/${payment.id}/confirm`, {
        transactionReference: payment.transactionReference,
        remarks: "Payment verified by owner",
      });

      setMessage("Payment confirmed successfully.");

      await fetchPayments();

    } catch (error) {
      console.error("Failed to confirm payment:", error);

      setMessage(
        error.response?.data?.message ||
        "Failed to confirm payment."
      );
    } finally {
      setConfirmingId(null);
    }
  };

  if (loading) {
    return (
      <div className="owner-page">
        <h2>Loading payments...</h2>
      </div>
    );
  }

  if (error) {
    return (
      <div className="owner-page">
        <h2>{error}</h2>
      </div>
    );
  }

  const pendingPayments = payments.filter(
    (payment) => payment.paymentStatus === "PENDING"
  );

  const confirmedPayments = payments.filter(
    (payment) => payment.paymentStatus === "CONFIRMED"
  );

  const totalConfirmed = confirmedPayments.reduce(
    (total, payment) =>
      total + Number(payment.amount || 0),
    0
  );

  return (
    <div className="owner-page">

      {/* Header */}
      <div className="owner-header">
        <div>
          <h1>RentFlow</h1>
          <p>Owner Dashboard</p>
        </div>

        <button
          className="logout-btn"
          onClick={handleLogout}
        >
          Logout
        </button>
      </div>

      {/* Stats */}
      <div className="owner-stats">

        <div className="owner-stat-card">
          <h3>Total Payments</h3>
          <p>{payments.length}</p>
        </div>

        <div className="owner-stat-card">
          <h3>Pending Confirmation</h3>
          <p>{pendingPayments.length}</p>
        </div>

        <div className="owner-stat-card">
          <h3>Confirmed Amount</h3>
          <p>₹{totalConfirmed}</p>
        </div>

      </div>

      {/* Message */}
      {message && (
        <div
          className={
            message.includes("successfully")
              ? "success-message"
              : "error-message"
          }
        >
          {message}
        </div>
      )}

      {/* Pending Payments */}
      <h2 className="owner-section-title">
        Pending Payments
      </h2>

      {pendingPayments.length === 0 ? (
        <div className="payment-card">
          <p>No pending payments.</p>
        </div>
      ) : (
        pendingPayments.map((payment) => (
          <div
            className="payment-card"
            key={payment.id}
          >

            <div className="payment-grid">

              <div className="payment-item">
                <span>Amount</span>
                <strong>
                  ₹{payment.amount}
                </strong>
              </div>

              <div className="payment-item">
                <span>Payment Method</span>
                <strong>
                  {payment.paymentMethod}
                </strong>
              </div>

              <div className="payment-item">
                <span>Transaction Reference</span>
                <strong>
                  {payment.transactionReference}
                </strong>
              </div>

              <div className="payment-item">
                <span>Status</span>
                <strong>
                  <span className="pending-status">
                    {payment.paymentStatus}
                  </span>
                </strong>
              </div>

            </div>

            <button
              className="confirm-btn"
              onClick={() =>
                handleConfirmPayment(payment)
              }
              disabled={confirmingId === payment.id}
            >
              {confirmingId === payment.id
                ? "Confirming..."
                : "Confirm Payment"}
            </button>

          </div>
        ))
      )}

      {/* Confirmed Payments */}
      <h2 className="owner-section-title">
        Confirmed Payments
      </h2>

      {confirmedPayments.length === 0 ? (
        <div className="payment-card">
          <p>No confirmed payments yet.</p>
        </div>
      ) : (
        confirmedPayments.map((payment) => (
          <div
            className="payment-card"
            key={payment.id}
          >
            <div className="payment-grid">

              <div className="payment-item">
                <span>Amount</span>
                <strong>
                  ₹{payment.amount}
                </strong>
              </div>

              <div className="payment-item">
                <span>Method</span>
                <strong>
                  {payment.paymentMethod}
                </strong>
              </div>

              <div className="payment-item">
                <span>Transaction</span>
                <strong>
                  {payment.transactionReference}
                </strong>
              </div>

              <div className="payment-item">
                <span>Status</span>
                <strong>
                  {payment.paymentStatus}
                </strong>
              </div>

            </div>
          </div>
        ))
      )}

    </div>
  );
}

export default OwnerDashboard;