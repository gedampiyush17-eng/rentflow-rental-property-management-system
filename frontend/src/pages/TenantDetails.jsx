import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import api from "../services/api";
import "./TenantDetails.css";

function TenantDetails() {

  const { id } = useParams();
  const navigate = useNavigate();

  const [tenant, setTenant] = useState(null);
  const [lease, setLease] = useState(null);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");


  useEffect(() => {
    loadTenantDetails();
  }, [id]);


  const loadTenantDetails = async () => {

    try {

      /* ================= TENANT ================= */

      const tenantResponse =
        await api.get(`/tenants/${id}`);

      setTenant(tenantResponse.data);


      /* ================= LEASE ================= */

      const leasesResponse =
        await api.get("/leases");

      const leases = leasesResponse.data;


      /*
       * Find the active lease belonging
       * to this tenant.
       */

      const tenantLease = leases.find(
        (item) =>
          String(item.tenantId) === String(id) &&
          item.leaseStatus === "ACTIVE"
      );


      setLease(tenantLease || null);


    } catch (err) {

      console.error(err);

      setError(
        "Failed to load tenant details."
      );

    } finally {

      setLoading(false);

    }
  };


  /* ================= LOADING ================= */

  if (loading) {

    return (

      <div className="tenant-details-page">

        <div className="tenant-details-loading">

          Loading tenant details...

        </div>

      </div>

    );

  }


  /* ================= ERROR ================= */

  if (error) {

    return (

      <div className="tenant-details-page">

        <button
          className="back-to-tenants"
          onClick={() => navigate("/tenants")}
        >
          ← Back to Tenants
        </button>

        <div className="tenant-details-error">

          {error}

        </div>

      </div>

    );

  }


  /* ================= NOT FOUND ================= */

  if (!tenant) {

    return (

      <div className="tenant-details-page">

        Tenant not found.

      </div>

    );

  }


  /* ================= UI ================= */

  return (

    <div className="tenant-details-page">


      {/* ================= BACK ================= */}

      <button
        className="back-to-tenants"
        onClick={() => navigate("/tenants")}
      >
        ← Back to Tenants
      </button>


      {/* ================= PROFILE HEADER ================= */}

      <div className="tenant-profile-header">

        <div className="tenant-profile-avatar">

          {tenant.firstName
            ?.charAt(0)
            .toUpperCase()}

        </div>


        <div className="tenant-profile-info">

          <h1>

            {tenant.firstName}{" "}

            {tenant.lastName}

          </h1>


          <p className="tenant-profile-email">

            {tenant.email}

          </p>


          <span className="tenant-detail-status">

            ACTIVE

          </span>

        </div>

      </div>


      {/* ================= PERSONAL INFORMATION ================= */}

      <div className="tenant-information-card">

        <h2>

          Personal Information

        </h2>


        <div className="tenant-information-grid">


          <div className="tenant-information-item">

            <span>
              First Name
            </span>

            <strong>
              {tenant.firstName || "—"}
            </strong>

          </div>


          <div className="tenant-information-item">

            <span>
              Last Name
            </span>

            <strong>
              {tenant.lastName || "—"}
            </strong>

          </div>


          <div className="tenant-information-item">

            <span>
              Email
            </span>

            <strong>
              {tenant.email || "—"}
            </strong>

          </div>


          <div className="tenant-information-item">

            <span>
              Phone
            </span>

            <strong>
              {tenant.phoneNumber || "—"}
            </strong>

          </div>


          <div className="tenant-information-item">

            <span>
              Occupation
            </span>

            <strong>
              {tenant.occupation || "—"}
            </strong>

          </div>


          <div className="tenant-information-item">

            <span>
              Date of Birth
            </span>

            <strong>
              {tenant.dateOfBirth || "—"}
            </strong>

          </div>


          <div className="tenant-information-item">

            <span>
              Aadhaar Number
            </span>

            <strong>
              ••••••••
            </strong>

          </div>


          <div className="tenant-information-item">

            <span>
              Emergency Contact
            </span>

            <strong>
              {tenant.emergencyContactName || "—"}
            </strong>

          </div>


          <div className="tenant-information-item">

            <span>
              Emergency Phone
            </span>

            <strong>
              {tenant.emergencyContactPhone || "—"}
            </strong>

          </div>


        </div>

      </div>


      {/* ================= RENTAL INFORMATION ================= */}

      <div className="tenant-rental-card">

        <h2>

          Rental Information

        </h2>


        {lease ? (

          <div className="tenant-information-grid">


            {/* UNIT */}

            <div className="tenant-information-item">

              <span>
                Unit
              </span>

              <strong>
                {lease.unitId || "—"}
              </strong>

            </div>


            {/* MONTHLY RENT */}

            <div className="tenant-information-item">

              <span>
                Monthly Rent
              </span>

              <strong>
                ₹{lease.monthlyRent || 0}
              </strong>

            </div>


            {/* SECURITY DEPOSIT */}

            <div className="tenant-information-item">

              <span>
                Security Deposit
              </span>

              <strong>
                ₹{lease.securityDeposit || 0}
              </strong>

            </div>


            {/* START DATE */}

            <div className="tenant-information-item">

              <span>
                Lease Start
              </span>

              <strong>
                {lease.leaseStartDate || "—"}
              </strong>

            </div>


            {/* END DATE */}

            <div className="tenant-information-item">

              <span>
                Lease End
              </span>

              <strong>
                {lease.leaseEndDate || "—"}
              </strong>

            </div>


            {/* STATUS */}

            <div className="tenant-information-item">

              <span>
                Lease Status
              </span>

              <strong>

                <span className="tenant-detail-status">

                  {lease.leaseStatus || "—"}

                </span>

              </strong>

            </div>


          </div>

        ) : (

          <p className="tenant-rental-empty">

            No active lease assigned to this tenant.

          </p>

        )}

      </div>


    </div>

  );

}

export default TenantDetails;