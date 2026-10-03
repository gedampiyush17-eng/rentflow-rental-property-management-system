import { useEffect, useState } from "react";
import api from "../services/api";
import "./Payments.css";

function Payments() {
  const [payments, setPayments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [selectedPayment, setSelectedPayment] = useState(null);

  const [confirming, setConfirming] = useState(false);
  const [confirmRemarks, setConfirmRemarks] = useState("");

  useEffect(() => {
    loadPayments();
  }, []);

  const loadPayments = async () => {
    try {
      setLoading(true);
      setError("");

      const response = await api.get("/payments");

      setPayments(response.data);
    } catch (error) {
      console.error(error);

      setError(
        error.response?.data?.message ||
        "Failed to load payments."
      );
    } finally {
      setLoading(false);
    }
  };

  const handleConfirmPayment = async () => {
    if (!selectedPayment) {
      return;
    }

    try {
      setConfirming(true);

      await api.post(
        `/payments/${selectedPayment.id}/confirm`,
        {
          transactionReference:
            selectedPayment.transactionReference,

          remarks:
            confirmRemarks.trim() ||
            "Payment confirmed by owner."
        }
      );

      setSelectedPayment(null);
      setConfirmRemarks("");

      await loadPayments();

    } catch (error) {
      console.error(error);

      alert(
        error.response?.data?.message ||
        "Failed to confirm payment."
      );

    } finally {
      setConfirming(false);
    }
  };

  const getStatusClass = (status) => {
    switch (status) {
      case "CONFIRMED":
        return "payment-status confirmed";

      case "PENDING":
        return "payment-status pending";

      case "FAILED":
        return "payment-status failed";

      default:
        return "payment-status";
    }
  };

  const totalAmount = payments.reduce(
    (sum, payment) =>
      sum + Number(payment.amount || 0),
    0
  );

  const confirmedAmount = payments
    .filter(
      (payment) =>
        payment.paymentStatus === "CONFIRMED"
    )
    .reduce(
      (sum, payment) =>
        sum + Number(payment.amount || 0),
      0
    );

  const pendingAmount = payments
    .filter(
      (payment) =>
        payment.paymentStatus === "PENDING"
    )
    .reduce(
      (sum, payment) =>
        sum + Number(payment.amount || 0),
      0
    );

  if (loading) {
    return (
      <div className="payments-page">
        <div className="payments-loading">
          Loading payments...
        </div>
      </div>
    );
  }

  return (
    <div className="payments-page">

      {/* HEADER */}

      <div className="payments-header">

        <div>
          <h1>Payments</h1>

          <p>
            View and manage rental payments.
          </p>
        </div>

      </div>


      {/* ERROR */}

      {error && (
        <div className="payment-error">
          {error}
        </div>
      )}


      {/* STATS */}

      <div className="payment-stats">

        <div className="payment-stat-card">

          <span className="stat-label">
            Total Payments
          </span>

          <strong>
            {payments.length}
          </strong>

        </div>


        <div className="payment-stat-card">

          <span className="stat-label">
            Total Amount
          </span>

          <strong>
            ₹{totalAmount.toLocaleString("en-IN")}
          </strong>

        </div>


        <div className="payment-stat-card">

          <span className="stat-label">
            Confirmed
          </span>

          <strong>
            ₹{confirmedAmount.toLocaleString("en-IN")}
          </strong>

        </div>


        <div className="payment-stat-card">

          <span className="stat-label">
            Pending
          </span>

          <strong>
            ₹{pendingAmount.toLocaleString("en-IN")}
          </strong>

        </div>

      </div>


      {/* PAYMENT TABLE */}

      <div className="payments-section">

        <div className="section-header">

          <div>
            <h2>
              Payment History
            </h2>

            <p>
              All recorded rental payments
            </p>
          </div>

        </div>


        {payments.length === 0 ? (

          <div className="empty-payments">

            <h3>
              No payments yet
            </h3>

            <p>
              Payments will appear here once tenants
              make payments.
            </p>

          </div>

        ) : (

          <div className="payments-table-wrapper">

            <table className="payments-table">

              <thead>

                <tr>

                  <th>
                    Payment
                  </th>

                  <th>
                    Rent Cycle
                  </th>

                  <th>
                    Amount
                  </th>

                  <th>
                    Method
                  </th>

                  <th>
                    Transaction
                  </th>

                  <th>
                    Status
                  </th>

                  <th>
                    Date
                  </th>

                  <th>
                    Action
                  </th>

                </tr>

              </thead>


              <tbody>

                {payments.map((payment) => (

                  <tr key={payment.id}>

                    <td>

                      <div className="payment-id">

                        <strong>
                          Payment
                        </strong>

                        <span>
                          {payment.id}
                        </span>

                      </div>

                    </td>


                    <td>

                      <span className="cycle-id">
                        {payment.rentCycleId}
                      </span>

                    </td>


                    <td>

                      <strong>
                        ₹
                        {Number(
                          payment.amount || 0
                        ).toLocaleString("en-IN")}
                      </strong>

                    </td>


                    <td>

                      <span className="payment-method">
                        {payment.paymentMethod}
                      </span>

                    </td>


                    <td>

                      <span className="transaction-reference">

                        {payment.transactionReference ||
                          "—"}

                      </span>

                    </td>


                    <td>

                      <span
                        className={getStatusClass(
                          payment.paymentStatus
                        )}
                      >
                        {payment.paymentStatus}
                      </span>

                    </td>


                    <td>

                      <span className="payment-date">

                        {payment.createdAt
                          ? new Date(
                              payment.createdAt
                            ).toLocaleDateString(
                              "en-IN"
                            )
                          : "—"}

                      </span>

                    </td>


                    <td>

                      {payment.paymentStatus ===
                        "PENDING" ? (

                        <button
                          className="confirm-payment-btn"
                          onClick={() =>
                            setSelectedPayment(
                              payment
                            )
                          }
                        >
                          Confirm
                        </button>

                      ) : (

                        <button
                          className="view-payment-btn"
                          onClick={() =>
                            setSelectedPayment(
                              payment
                            )
                          }
                        >
                          View
                        </button>

                      )}

                    </td>

                  </tr>

                ))}

              </tbody>

            </table>

          </div>

        )}

      </div>


      {/* PAYMENT DETAILS MODAL */}

      {selectedPayment && (

        <div className="payment-modal-overlay">

          <div className="payment-modal">

            <div className="payment-modal-header">

              <div>

                <h2>
                  Payment Details
                </h2>

                <p>
                  Review payment information
                </p>

              </div>

              <button
                className="close-modal-btn"
                onClick={() => {
                  setSelectedPayment(null);
                  setConfirmRemarks("");
                }}
              >
                ×
              </button>

            </div>


            <div className="payment-details">

              <div className="payment-detail-row">

                <span>
                  Payment ID
                </span>

                <strong>
                  {selectedPayment.id}
                </strong>

              </div>


              <div className="payment-detail-row">

                <span>
                  Rent Cycle ID
                </span>

                <strong>
                  {selectedPayment.rentCycleId}
                </strong>

              </div>


              <div className="payment-detail-row">

                <span>
                  Amount
                </span>

                <strong>
                  ₹
                  {Number(
                    selectedPayment.amount || 0
                  ).toLocaleString("en-IN")}
                </strong>

              </div>


              <div className="payment-detail-row">

                <span>
                  Payment Method
                </span>

                <strong>
                  {selectedPayment.paymentMethod}
                </strong>

              </div>


              <div className="payment-detail-row">

                <span>
                  Transaction Reference
                </span>

                <strong>
                  {selectedPayment.transactionReference ||
                    "—"}
                </strong>

              </div>


              <div className="payment-detail-row">

                <span>
                  Status
                </span>

                <span
                  className={getStatusClass(
                    selectedPayment.paymentStatus
                  )}
                >
                  {selectedPayment.paymentStatus}
                </span>

              </div>


              <div className="payment-detail-row">

                <span>
                  Created
                </span>

                <strong>
                  {selectedPayment.createdAt
                    ? new Date(
                        selectedPayment.createdAt
                      ).toLocaleString("en-IN")
                    : "—"}
                </strong>

              </div>

            </div>


            {/* CONFIRM SECTION */}

            {selectedPayment.paymentStatus ===
              "PENDING" && (

              <div className="confirm-section">

                <label>
                  Confirmation Remarks
                </label>

                <textarea
                  value={confirmRemarks}
                  onChange={(e) =>
                    setConfirmRemarks(
                      e.target.value
                    )
                  }
                  placeholder="Optional remarks..."
                  rows="3"
                />

              </div>

            )}


            <div className="payment-modal-actions">

              <button
                className="modal-cancel-btn"
                onClick={() => {
                  setSelectedPayment(null);
                  setConfirmRemarks("");
                }}
              >
                Close
              </button>


              {selectedPayment.paymentStatus ===
                "PENDING" && (

                <button
                  className="modal-confirm-btn"
                  onClick={handleConfirmPayment}
                  disabled={confirming}
                >
                  {confirming
                    ? "Confirming..."
                    : "Confirm Payment"}
                </button>

              )}

            </div>

          </div>

        </div>

      )}

    </div>
  );
}

export default Payments;