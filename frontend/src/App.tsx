import { BrowserRouter, Routes, Route, NavLink, Navigate } from 'react-router-dom';
import SearchLcnPage from './pages/SearchLcnPage';
import CreateLcnPage from './pages/CreateLcnPage';

function App() {
  return (
    <BrowserRouter>
      <div className="min-h-screen bg-cfg-light flex flex-col font-sans">
        {/* Navbar Entreprise */}
        <nav className="bg-white border-b border-gray-200 shadow-sm sticky top-0 z-10 w-full">
          <div className="max-w-screen-2xl mx-auto px-4 lg:px-6">
            <div className="flex h-16 justify-between items-center">
              <div className="flex items-center space-x-8">
                <div className="flex items-center gap-2">
                  <div className="w-8 h-8 bg-cfg-green rounded-md flex items-center justify-center text-white font-bold text-sm shadow-sm ring-1 ring-cfg-dark/5">
                    CFG
                  </div>
                  <span className="font-bold text-cfg-dark text-lg tracking-tight">Plateforme LCN</span>
                </div>
                
                <div className="hidden md:flex space-x-2">
                  <NavLink
                    to="/cherche-lcn"
                    className={({ isActive }) =>
                      `px-4 py-2 rounded-md text-sm font-semibold transition-colors ${
                        isActive
                          ? 'bg-cfg-green text-white shadow-sm'
                          : 'text-gray-600 hover:bg-gray-100 hover:text-gray-900 border border-transparent'
                      }`
                    }
                  >
                    Recherche LCN
                  </NavLink>
                  <NavLink
                    to="/saisie"
                    className={({ isActive }) =>
                      `px-4 py-2 rounded-md text-sm font-semibold transition-colors ${
                        isActive
                          ? 'bg-cfg-green text-white shadow-sm'
                          : 'text-gray-600 hover:bg-gray-100 hover:text-gray-900 border border-transparent'
                      }`
                    }
                  >
                    Saisie Manuelle
                  </NavLink>
                </div>
              </div>
              <div className="flex items-center">
                <div className="text-xs font-semibold text-gray-500 bg-gray-100 px-3 py-1.5 rounded-md border border-gray-200 shadow-sm uppercase tracking-wide">
                  Agent LCN
                </div>
              </div>
            </div>
          </div>
        </nav>

        {/* Contenu principal */}
        <main className="flex-1 max-w-screen-2xl w-full mx-auto align-top">
          <Routes>
            <Route path="/" element={<Navigate to="/cherche-lcn" replace />} />
            <Route path="/cherche-lcn" element={<SearchLcnPage />} />
            <Route path="/saisie" element={<CreateLcnPage />} />
          </Routes>
        </main>
      </div>
    </BrowserRouter>
  )
}

export default App;
