import {
  NavLink,
  Outlet,
  useNavigate
} from "react-router-dom";

import "./OwnerLayout.css";


function OwnerLayout() {

  const navigate = useNavigate();


  const handleLogout = () => {

    localStorage.removeItem("token");
    localStorage.removeItem("role");

    navigate("/");

    window.location.reload();
  };


  return (

    <div className="owner-layout">


      {/* SIDEBAR */}

      <aside className="owner-sidebar">


        {/* LOGO */}

        <div className="sidebar-logo">

          <div className="logo-icon">
            ◆
          </div>

          <span>
            RentFlow
          </span>

        </div>


        {/* NAVIGATION */}

        <nav className="sidebar-nav">


          {/* OWNER DASHBOARD */}

          <NavLink
            to="/owner"
            end
            className="sidebar-link"
          >

            <span className="nav-icon">
              ⌂
            </span>

            <span>
              Owner Dashboard
            </span>

          </NavLink>


          {/* PROPERTIES */}

          <NavLink
            to="/properties"
            className="sidebar-link"
          >

            <span className="nav-icon">
              ▣
            </span>

            <span>
              Properties
            </span>

          </NavLink>


          {/* UNITS */}

          <NavLink
            to="/units"
            className="sidebar-link"
          >

            <span className="nav-icon">
              ▦
            </span>

            <span>
              Units
            </span>

          </NavLink>


          {/* TENANTS */}

          <NavLink
            to="/tenants"
            className="sidebar-link"
          >

            <span className="nav-icon">
              ♙
            </span>

            <span>
              Tenants
            </span>

          </NavLink>


          {/* LEASES */}

          <NavLink
            to="/leases"
            className="sidebar-link"
          >

            <span className="nav-icon">
              ▤
            </span>

            <span>
              Leases
            </span>

          </NavLink>


          {/* RENT CYCLES */}

          <NavLink
            to="/rent-cycles"
            className="sidebar-link"
          >

            <span className="nav-icon">
              ◷
            </span>

            <span>
              Rent Cycles
            </span>

          </NavLink>


          {/* PAYMENTS */}

          <NavLink
            to="/payments"
            className="sidebar-link"
          >

            <span className="nav-icon">
              ₹
            </span>

            <span>
              Payments
            </span>

          </NavLink>


          {/* RECEIPTS */}

          <NavLink
            to="/receipts"
            className="sidebar-link"
          >

            <span className="nav-icon">
              ▤
            </span>

            <span>
              Receipts
            </span>

          </NavLink>


        </nav>


        {/* LOGOUT */}

        <button
          className="logout-link"
          onClick={handleLogout}
        >

          <span className="logout-icon">
            ↪
          </span>

          Logout

        </button>


      </aside>


      {/* MAIN CONTENT */}

      <div className="owner-main">


        {/* TOP BAR */}

        <header className="owner-topbar">

          <div></div>


          <div className="owner-user">

            <div className="owner-avatar">
              P
            </div>

            <div className="owner-user-info">

              <strong>
                Piyush Gedam
              </strong>

              <span>
                Owner
              </span>

            </div>

          </div>

        </header>


        {/* PAGE CONTENT */}

        <main className="owner-content">

          <Outlet />

        </main>


      </div>


    </div>

  );
}


export default OwnerLayout;