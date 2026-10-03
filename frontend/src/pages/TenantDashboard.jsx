import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

import api from "../services/api";
import { logout } from "../utils/auth";

import "./TenantDashboard.css";

function TenantDashboard() {

  const [rentCycles, setRentCycles] = useState([]);

  const [loading, setLoading] = useState(true);

  const [error, setError] = useState("");

  const [qrCode, setQrCode] = useState(null);

  const [qrLoading, setQrLoading] = useState(false);

  const [qrError, setQrError] = useState("");

  const [paymentAmount, setPaymentAmount] = useState("");

  const [paymentLoading, setPaymentLoading] = useState(false);

  const [paymentMessage, setPaymentMessage] = useState("");

  const [transactionReference, setTransactionReference] = useState("");

  const navigate = useNavigate();


  /* =====================================================
     FETCH RENT CYCLES
     ===================================================== */

  const fetchRentCycles = async () => {

    try {

      const response = await api.get("/rent-cycles/my");

      console.log("My rent cycles:", response.data);

      setRentCycles(response.data);

    } catch (error) {

      console.error("Failed to fetch rent cycles:", error);

      setError("Failed to load rent information.");

    } finally {

      setLoading(false);

    }

  };


  useEffect(() => {

    fetchRentCycles();

  }, []);


  /* =====================================================
     LOGOUT
     ===================================================== */

  const handleLogout = () => {

    logout();

    window.location.href = "/";

  };


  /* =====================================================
     PAY RENT / GENERATE QR
     ===================================================== */

  const handlePayRent = async () => {

    if (!latestCycle) return;

    try {

      setQrLoading(true);

      setQrError("");

      setQrCode(null);


      const response = await api.get(
        `/qr/${latestCycle.id}`
      );


      console.log("QR response:", response.data);

      setQrCode(response.data);

    } catch (error) {

      console.error("Failed to generate QR:", error);

      setQrError("Failed to generate payment QR.");

    } finally {

      setQrLoading(false);

    }

  };


  /* =====================================================
     SIMULATE PAYMENT
     ===================================================== */

  const handleSimulatePayment = async () => {

    if (!latestCycle) return;


    if (!paymentAmount || Number(paymentAmount) <= 0) {

      setPaymentMessage("Enter a valid payment amount.");

      return;

    }


    if (
      Number(paymentAmount) >
      Number(latestCycle.balanceDue)
    ) {

      setPaymentMessage(
        "Payment cannot be greater than the balance due."
      );

      return;

    }


    try {

      setPaymentLoading(true);

      setPaymentMessage("");

      setTransactionReference("");


      const response = await api.post(
        "/payments/simulate",
        {
          rentCycleId: latestCycle.id,
          amount: Number(paymentAmount),
        }
      );


      console.log(
        "Payment simulation response:",
        response.data
      );


      const transaction =
        response.data.transactionReference;


      setTransactionReference(transaction);


      setPaymentMessage(
        "Payment submitted successfully. Waiting for owner confirmation."
      );


      setPaymentAmount("");


      await fetchRentCycles();


    } catch (error) {

      console.error(
        "Payment simulation failed:",
        error
      );


      setPaymentMessage(
        error.response?.data?.message ||
        "Payment simulation failed."
      );


    } finally {

      setPaymentLoading(false);

    }

  };


  /* =====================================================
     LOADING
     ===================================================== */

  if (loading) {

    return (

      <div className="tenant-page">

        <h2>
          Loading your rent information...
        </h2>

      </div>

    );

  }


  /* =====================================================
     ERROR
     ===================================================== */

  if (error) {

    return (

      <div className="tenant-page">

        <h2>
          {error}
        </h2>

      </div>

    );

  }


  /* =====================================================
     FIND LATEST CYCLE
     ===================================================== */

  const unpaidCycles = rentCycles
    .filter(
      (cycle) => cycle.status !== "PAID"
    )
    .sort(
      (a, b) =>
        new Date(a.dueDate) -
        new Date(b.dueDate)
    );


  const latestCycle =
    unpaidCycles[0] || rentCycles[0];


  /*
   * These become 0 when tenant has
   * no rent cycle yet.
   */

  const amountDue =
    latestCycle?.amountDue || 0;

  const amountPaid =
    latestCycle?.amountPaid || 0;

  const balanceDue =
    latestCycle?.balanceDue || 0;


  /* =====================================================
     UI
     ===================================================== */

  return (

    <div className="tenant-page">


      {/* ================= HEADER ================= */}

      <div className="tenant-header">

        <div>

          <h1>
            RentFlow
          </h1>

          <p>
            Tenant Dashboard
          </p>

        </div>


        <button
          className="logout-btn"
          onClick={handleLogout}
        >
          Logout
        </button>

      </div>


      {/* ================= WELCOME ================= */}

      <div>

        <h2 className="section-title">
          Welcome back 👋
        </h2>


        <p
          style={{
            color: "#64748b",
            marginBottom: "25px",
          }}
        >
          Here's your rental overview.
        </p>

      </div>


      {/* ================= STATISTICS ================= */}

      {/* 
         IMPORTANT:
         These cards ALWAYS appear.
         If there is no lease/rent cycle,
         they simply show ₹0.
      */}

      <div className="stats">


        <div className="stat-card">

          <h3>
            Amount Due
          </h3>

          <p>
            ₹{amountDue}
          </p>

        </div>


        <div className="stat-card">

          <h3>
            Amount Paid
          </h3>

          <p>
            ₹{amountPaid}
          </p>

        </div>


        <div className="stat-card">

          <h3>
            Balance Due
          </h3>

          <p>
            ₹{balanceDue}
          </p>

        </div>


      </div>


      {/* ================= CURRENT RENT CYCLE ================= */}

      <h2 className="section-title">

        Current Rent Cycle

      </h2>


      {latestCycle ? (

        <div className="rent-card">


          <div className="rent-info">


            <div className="info-item">

              <span>
                Due Date
              </span>

              <strong>
                {latestCycle.dueDate}
              </strong>

            </div>


            <div className="info-item">

              <span>
                Amount
              </span>

              <strong>
                ₹{latestCycle.amountDue}
              </strong>

            </div>


            <div className="info-item">

              <span>
                Status
              </span>

              <strong>

                <span className="status">
                  {latestCycle.status}
                </span>

              </strong>

            </div>


          </div>


          {/* PAY RENT */}

          {latestCycle.status !== "PAID" && (

            <button
              className="pay-btn"
              onClick={handlePayRent}
              disabled={qrLoading}
            >

              {qrLoading
                ? "Generating QR..."
                : "Pay Rent"}

            </button>

          )}


          {/* QR ERROR */}

          {qrError && (

            <p
              style={{
                color: "#dc2626",
                marginTop: "15px",
              }}
            >
              {qrError}
            </p>

          )}


          {/* ================= QR ================= */}

          {qrCode && (

            <div
              style={{
                marginTop: "25px",
                padding: "25px",
                background: "#f8fafc",
                borderRadius: "12px",
                textAlign: "center",
              }}
            >

              <h3>
                Scan to Pay
              </h3>


              <div
                style={{
                  marginTop: "15px",
                }}
              >

                {typeof qrCode === "string" ? (

                  <img
                    src={
                      qrCode.startsWith("data:")
                        ? qrCode
                        : `data:image/png;base64,${qrCode}`
                    }
                    alt="UPI Payment QR"
                    style={{
                      width: "250px",
                      height: "250px",
                      objectFit: "contain",
                    }}
                  />

                ) : (

                  <p>
                    QR generated successfully.
                  </p>

                )}

              </div>


              <p
                style={{
                  marginTop: "15px",
                  color: "#64748b",
                }}
              >

                Amount: ₹{latestCycle.balanceDue}

              </p>


              {/* ================= PAYMENT AMOUNT ================= */}

              <div
                style={{
                  marginTop: "20px",
                  maxWidth: "350px",
                  marginLeft: "auto",
                  marginRight: "auto",
                }}
              >

                <label
                  style={{
                    display: "block",
                    marginBottom: "8px",
                    fontWeight: "bold",
                  }}
                >
                  Payment Amount
                </label>


                <input
                  type="number"
                  min="1"
                  max={latestCycle.balanceDue}
                  value={paymentAmount}
                  onChange={(e) =>
                    setPaymentAmount(e.target.value)
                  }
                  placeholder="Enter amount"
                  style={{
                    width: "100%",
                    padding: "12px",
                    border: "1px solid #ccc",
                    borderRadius: "6px",
                    fontSize: "16px",
                  }}
                />


                <button
                  className="pay-btn"
                  onClick={handleSimulatePayment}
                  disabled={paymentLoading}
                  style={{
                    marginTop: "15px",
                  }}
                >

                  {paymentLoading
                    ? "Processing..."
                    : "Simulate Payment"}

                </button>

              </div>


              {/* ================= PAYMENT RESULT ================= */}

              {paymentMessage && (

                <p
                  style={{
                    marginTop: "20px",
                    fontWeight: "bold",
                    color:
                      paymentMessage.includes(
                        "successfully"
                      )
                        ? "#15803d"
                        : "#dc2626",
                  }}
                >
                  {paymentMessage}
                </p>

              )}


              {/* ================= TRANSACTION ================= */}

              {transactionReference && (

                <div
                  style={{
                    marginTop: "15px",
                    padding: "12px",
                    background: "#ecfdf5",
                    borderRadius: "8px",
                  }}
                >

                  <p style={{ margin: 0 }}>
                    Transaction Reference
                  </p>


                  <strong>
                    {transactionReference}
                  </strong>

                </div>

              )}

            </div>

          )}

        </div>

      ) : (

        /* ================= NO CURRENT CYCLE ================= */

        <div className="rent-card">

          <p>
            No active rent cycle found.
          </p>

        </div>

      )}


      {/* ================= RENT HISTORY ================= */}

      <h2 className="section-title">

        Rent Cycle History

      </h2>


      {rentCycles.length > 0 ? (

        rentCycles.map((cycle) => (

          <div
            className="rent-card"
            key={cycle.id}
          >

            <div className="rent-info">


              <div className="info-item">

                <span>
                  Due Date
                </span>

                <strong>
                  {cycle.dueDate}
                </strong>

              </div>


              <div className="info-item">

                <span>
                  Amount
                </span>

                <strong>
                  ₹{cycle.amountDue}
                </strong>

              </div>


              <div className="info-item">

                <span>
                  Status
                </span>

                <strong>

                  <span className="status">
                    {cycle.status}
                  </span>

                </strong>

              </div>


            </div>

          </div>

        ))

      ) : (

        <div className="rent-card">

          <p>
            No rent cycle history available yet.
          </p>

        </div>

      )}

    </div>

  );

}

export default TenantDashboard;