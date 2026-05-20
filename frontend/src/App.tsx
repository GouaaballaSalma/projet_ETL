import { BrowserRouter, Routes, Route, NavLink, Navigate } from 'react-router-dom';
import SearchLcnPage from './pages/SearchLcnPage';
import CreateLcnPage from './pages/CreateLcnPage';
import Login from './pages/Login';
import ProtectedRoute from './components/ProtectedRoute';
import RoleProtectedRoute from './components/RoleProtectedRoute';
import UserManagement from './pages/admin/UserManagement';
import { authService } from './services/authService';
import Logo from './components/Logo';

const ProtectedLayout = () => {
  return (
    <div className="min-h-screen bg-cfg-light flex flex-col font-sans">
      {/* Navbar Entreprise */}
      <nav className="bg-white border-b border-gray-200 shadow-sm sticky top-0 z-10 w-full">
        <div className="max-w-screen-2xl mx-auto px-6 lg:px-8">
          <div className="flex h-16 justify-between items-center">
            <div className="flex items-center space-x-8">
              <div className="flex items-center gap-8">
                <Logo size="md" />
                <div className="h-8 w-px bg-gray-300 rounded-full"></div>
                <span className="font-bold text-cfg-dark text-lg tracking-tight uppercase">Plateforme LCN</span>
              </div>
              
              <div className="hidden md:flex items-center space-x-2">
                <NavLink
                  to="/cherche-lcn"
                  className={({ isActive }) =>
                    `px-4 py-3 rounded-md text-sm font-semibold transition-colors ${
                      isActive
                        ? 'bg-primary text-white shadow-sm'
                        : 'text-gray-600 hover:bg-gray-100 hover:text-gray-900 border border-transparent'
                    }`
                  }
                >
                  Recherche LCN
                </NavLink>
                {authService.hasAnyRole(['ROLE_ADMIN', 'ROLE_BUSINESS']) && (
                  <NavLink
                    to="/saisie"
                    className={({ isActive }) =>
                      `px-4 py-3 rounded-md text-sm font-semibold transition-colors ${
                        isActive
                          ? 'bg-primary text-white shadow-sm'
                          : 'text-gray-600 hover:bg-gray-100 hover:text-gray-900 border border-transparent'
                      }`
                    }
                  >
                    Saisie Manuelle
                  </NavLink>
                )}
                {authService.hasAnyRole(['ROLE_ADMIN']) && (
                  <NavLink
                    to="/admin/users"
                    className={({ isActive }) =>
                      `px-4 py-3 rounded-md text-sm font-semibold transition-colors ${
                        isActive
                          ? 'bg-primary text-white shadow-sm'
                          : 'text-gray-600 hover:bg-gray-100 hover:text-gray-900 border border-transparent'
                      }`
                    }
                  >
                    Panneau Admin
                  </NavLink>
                )}
              </div>
            </div>
            <div className="flex items-center space-x-4">
              <div className="text-xs font-semibold text-gray-500 bg-gray-100 px-3 py-1.5 rounded-md border border-gray-200 shadow-sm uppercase tracking-wide">
                Agent LCN
              </div>
              <button 
                onClick={() => {
                  authService.logout();
                  window.location.href = '/login';
                }}
                className="text-sm font-medium text-red-600 hover:text-red-800 transition-colors"
              >
                Déconnexion
              </button>
            </div>
          </div>
        </div>
      </nav>

      {/* Contenu principal */}
      <main className="flex-1 max-w-screen-2xl w-full mx-auto align-top">
        <Routes>
          <Route path="/" element={<Navigate to="/cherche-lcn" replace />} />
          <Route path="/cherche-lcn" element={<SearchLcnPage />} />
          <Route path="/saisie" element={
            <RoleProtectedRoute allowedRoles={['ROLE_ADMIN', 'ROLE_BUSINESS']}>
              <CreateLcnPage />
            </RoleProtectedRoute>
          } />
          <Route path="/admin/users" element={
            <RoleProtectedRoute allowedRoles={['ROLE_ADMIN']}>
              <UserManagement />
            </RoleProtectedRoute>
          } />
        </Routes>
      </main>
    </div>
  );
};

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route
          path="/*"
          element={
            <ProtectedRoute>
              <ProtectedLayout />
            </ProtectedRoute>
          }
        />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
