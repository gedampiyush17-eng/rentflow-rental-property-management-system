import { useEffect, useState } from "react";
import api from "../services/api";
import "./Receipts.css";

function Receipts() {

  const [payments, setPayments] = useState([]);
  const [receipts, setReceipts] = useState([]);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [generating, setGenerating] = useState(null);


  useEffect(() => {
    loadPayments();
  }, []);


  /* =========================================================
     LOAD CONFIRMED PAYMENTS
     ========================================================= */

  const loadPayments = async () => {

    try {

      setLoading(true);
      setError("");

      const response = await api.get("/payments");

      const confirmedPayments =
        response.data.filter(
          (payment) =>
            payment.paymentStatus === "CONFIRMED"
        );

      setPayments(confirmedPayments);

      await loadExistingReceipts(
        confirmedPayments
      );

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


  /* =========================================================
     LOAD EXISTING RECEIPTS
     ========================================================= */

  const loadExistingReceipts = async (
    paymentList
  ) => {

    const receiptResults = [];

    for (const payment of paymentList) {

      try {

        const response = await api.get(
          `/receipts/payment/${payment.id}`
        );

        receiptResults.push(
          response.data
        );

      } catch (error) {

        /*
         * Receipt does not exist yet.
         * This is not treated as a page error.
         */

        console.log(
          `No receipt found for payment ${payment.id}`
        );

      }

    }

    setReceipts(receiptResults);

  };


  /* =========================================================
     GENERATE RECEIPT
     ========================================================= */

  const generateReceipt = async (
    paymentId
  ) => {

    try {

      setGenerating(paymentId);
      setError("");

      const response = await api.post(
        `/receipts/payment/${paymentId}`
      );


      setReceipts((previous) => {

        const alreadyExists =
          previous.some(
            (receipt) =>
              receipt.id === response.data.id
          );


        if (alreadyExists) {
          return previous;
        }


        return [
          ...previous,
          response.data
        ];

      });

    } catch (error) {

      console.error(error);

      setError(
        error.response?.data?.message ||
        "Failed to generate receipt."
      );

    } finally {

      setGenerating(null);

    }

  };


  /* =========================================================
     FIND RECEIPT FOR PAYMENT
     ========================================================= */

  const getReceiptForPayment = (
    paymentId
  ) => {

    return receipts.find(
      (receipt) =>
        receipt.paymentId === paymentId
    );

  };


  /* =========================================================
     OPEN PDF RECEIPT
     ========================================================= */

  const openReceipt = (receipt) => {

    const token =
      localStorage.getItem("token");


    const pdfUrl =
      `http://localhost:8080/api/receipts/${receipt.id}/pdf`;


    fetch(pdfUrl, {

      method: "GET",

      headers: {
        Authorization: `Bearer ${token}`,
      },

    })

      .then(async (response) => {

        if (!response.ok) {

          throw new Error(
            "Failed to load receipt PDF"
          );

        }


        const blob =
          await response.blob();


        const url =
          window.URL.createObjectURL(
            blob
          );


        /*
         * Open the generated PDF
         * in a new browser tab.
         */

        window.open(
          url,
          "_blank"
        );


        /*
         * Release the temporary
         * browser object URL later.
         */

        setTimeout(() => {

          window.URL.revokeObjectURL(
            url
          );

        }, 10000);

      })

      .catch((error) => {

        console.error(
          "Failed to open receipt PDF:",
          error
        );

        setError(
          "Failed to open receipt PDF."
        );

      });

  };


  /* =========================================================
     LOADING
     ========================================================= */

  if (loading) {

    return (

      <div className="receipts-page">

        <div className="receipts-loading">

          Loading receipts...

        </div>

      </div>

    );

  }


  /* =========================================================
     PAGE
     ========================================================= */

  return (

    <div className="receipts-page">


      {/* HEADER */}

      <div className="receipts-header">

        <div>

          <h1>
            Receipts
          </h1>

          <p>
            Manage receipts generated for
            confirmed payments.
          </p>

        </div>

      </div>


      {/* ERROR */}

      {error && (

        <div className="receipt-error">

          {error}

        </div>

      )}


      {/* =====================================================
          STATISTICS
          ===================================================== */}

      <div className="receipt-stats">


        {/* CONFIRMED PAYMENTS */}

        <div className="receipt-stat-card">

          <span>
            Confirmed Payments
          </span>

          <strong>
            {payments.length}
          </strong>

        </div>


        {/* GENERATED */}

        <div className="receipt-stat-card">

          <span>
            Receipts Generated
          </span>

          <strong>
            {receipts.length}
          </strong>

        </div>


        {/* PENDING */}

        <div className="receipt-stat-card">

          <span>
            Receipts Pending
          </span>

          <strong>

            {
              payments.filter(
                (payment) =>
                  !getReceiptForPayment(
                    payment.id
                  )
              ).length
            }

          </strong>

        </div>


      </div>


      {/* =====================================================
          RECEIPTS SECTION
          ===================================================== */}

      <div className="receipts-section">


        {/* SECTION HEADER */}

        <div className="section-header">

          <div>

            <h2>
              Payment Receipts
            </h2>

            <p>
              Receipts are available for
              confirmed payments.
            </p>

          </div>

        </div>


        {/* ===================================================
            NO PAYMENTS
            =================================================== */}

        {payments.length === 0 ? (

          <div className="empty-receipts">

            <h3>
              No confirmed payments
            </h3>

            <p>
              Receipts will appear here after
              payments are confirmed.
            </p>

          </div>

        ) : (

          /* =================================================
             TABLE
             ================================================= */

          <div className="receipts-table-wrapper">

            <table className="receipts-table">


              {/* TABLE HEADER */}

              <thead>

                <tr>

                  <th>
                    Payment
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
                    Receipt
                  </th>

                  <th>
                    Action
                  </th>

                </tr>

              </thead>


              {/* TABLE BODY */}

              <tbody>

                {payments.map(
                  (payment) => {

                    const receipt =
                      getReceiptForPayment(
                        payment.id
                      );


                    return (

                      <tr
                        key={payment.id}
                      >


                        {/* PAYMENT */}

                        <td>

                          <div className="receipt-payment-id">

                            <strong>
                              Payment
                            </strong>

                            <span>
                              {payment.id}
                            </span>

                          </div>

                        </td>


                        {/* AMOUNT */}

                        <td>

                          <strong>

                            ₹
                            {Number(
                              payment.amount || 0
                            ).toLocaleString(
                              "en-IN"
                            )}

                          </strong>

                        </td>


                        {/* METHOD */}

                        <td>

                          {payment.paymentMethod}

                        </td>


                        {/* TRANSACTION */}

                        <td>

                          <span className="receipt-transaction">

                            {
                              payment.transactionReference ||
                              "—"
                            }

                          </span>

                        </td>


                        {/* RECEIPT STATUS */}

                        <td>

                          {receipt ? (

                            <span className="receipt-generated">

                              ✓ Generated

                            </span>

                          ) : (

                            <span className="receipt-pending">

                              Not Generated

                            </span>

                          )}

                        </td>


                        {/* ACTION */}

                        <td>


                          {receipt ? (

                            <button
                              className="view-receipt-btn"
                              onClick={() =>
                                openReceipt(
                                  receipt
                                )
                              }
                            >

                              View PDF

                            </button>

                          ) : (

                            <button
                              className="generate-receipt-btn"
                              onClick={() =>
                                generateReceipt(
                                  payment.id
                                )
                              }
                              disabled={
                                generating ===
                                payment.id
                              }
                            >

                              {
                                generating ===
                                payment.id
                                  ? "Generating..."
                                  : "Generate Receipt"
                              }

                            </button>

                          )}


                        </td>


                      </tr>

                    );

                  }
                )}

              </tbody>


            </table>

          </div>

        )}

      </div>


    </div>

  );

}


export default Receipts;