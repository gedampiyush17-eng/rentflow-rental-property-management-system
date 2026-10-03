import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api";
import "./Tenants.css";

function Tenants() {

  const navigate = useNavigate();

  const [tenants, setTenants] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    loadTenants();
  }, []);

  const loadTenants = async () => {

    try {

      const response = await api.get("/tenants");

      setTenants(response.data);

    } catch (err) {

      console.error(err);

      setError("Failed to load tenants.");

    } finally {

      setLoading(false);

    }
  };


  if (loading) {
    return (
      <div className="tenants-page">
        <h1>My Tenants</h1>
        <p className="loading-text">
          Loading tenants...
        </p>
      </div>
    );
  }


  return (

    <div className="tenants-page">

      {/* ================= HEADER ================= */}

      <div className="tenants-header">

        <div>

          <h1>
            My Tenants
          </h1>

          <p>
            Manage your tenants and their rental information.
          </p>

        </div>


        <button
          className="add-tenant-btn"
          onClick={() => navigate("/tenants/add")}
        >
          + Add Tenant
        </button>

      </div>


      {/* ================= ERROR ================= */}

      {error && (
        <div className="tenant-error">
          {error}
        </div>
      )}


      {/* ================= TENANTS ================= */}

      <div className="tenants-section">

        <h2>
          Tenants ({tenants.length})
        </h2>


        {tenants.length === 0 ? (

          <div className="empty-tenants">

            <p>
              No tenants found.
            </p>

            <button
              onClick={() => navigate("/tenants/add")}
            >
              Add Your First Tenant
            </button>

          </div>

        ) : (

          <div className="tenant-list">

            {tenants.map((tenant) => (

              <div
                className="tenant-card"
                key={tenant.id}
              >

                {/* Avatar */}

                <div className="tenant-avatar">

                  {tenant.firstName
                    ?.charAt(0)
                    .toUpperCase()}

                </div>


                {/* Main information */}

                <div className="tenant-main">

                  <h3>
                    {tenant.firstName}{" "}
                    {tenant.lastName}
                  </h3>

                  <p className="tenant-email">
                    {tenant.email}
                  </p>


                  <div className="tenant-info-row">

                    <div>
                      <span>PHONE</span>
                      <strong>
                        {tenant.phoneNumber || "—"}
                      </strong>
                    </div>

                    <div>
                      <span>OCCUPATION</span>
                      <strong>
                        {tenant.occupation || "—"}
                      </strong>
                    </div>

                    <div>
                      <span>AADHAAR</span>
                      <strong>
                        ••••••••
                      </strong>
                    </div>

                  </div>

                </div>


                {/* Right side */}

                <div className="tenant-card-right">

                  <span className="tenant-status">
                    ACTIVE
                  </span>


                  <button
                    className="view-tenant-btn"
                    onClick={() =>
                      navigate(`/tenants/${tenant.id}`)
                    }
                  >
                    View Details →
                  </button>

                </div>

              </div>

            ))}

          </div>

        )}

      </div>

    </div>

  );
}

export default Tenants;