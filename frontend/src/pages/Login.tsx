import React, { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { authService } from '../services/authService';
import { Eye, EyeOff } from 'lucide-react';
import cfgBuilding from '../assets/cfgbuilding.png';
import Logo from '../components/Logo';

const Login: React.FC = () => {
  const [email, setEmail] = useState('');
  const [motDePasse, setMotDePasse] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  
  const navigate = useNavigate();
  const location = useLocation();

  const from = location.state?.from?.pathname || "/cherche-lcn";

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setIsLoading(true);

    try {
      await authService.login(email, motDePasse);
      navigate(from, { replace: true });
    } catch (err) {
      setError('Identifiants incorrects. Veuillez réessayer.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex font-sans bg-white">
      {/* Colonne gauche - Visuel */}
      <div className="hidden lg:flex lg:w-1/2 relative bg-cfg-dark">
        <div className="absolute inset-0 z-0 ">
          <img 
            src={cfgBuilding}
            alt="CFG Bank Building" 
            className="w-full h-full object-cover opacity-50"
          />
          <div className="absolute inset-0 bg-gradient-to-t from-black/80 via-black/20 to-transparent"></div>
        </div>
        
        <div className="relative z-10 flex flex-col justify-end p-12 text-white h-full w-full">
          <Logo size="lg" className="mb-6" />
          <h1 className="text-4xl font-bold mb-4 tracking-tight">Bienvenue sur la plateforme LCN</h1>
          <p className="text-lg text-gray-200 max-w-lg">
            Espace réservé aux collaborateurs habilités. Accès sécurisé pour le traitement et le suivi des Lettres de Change Normalisées.
          </p>
        </div>
      </div>

      {/* Colonne droite - Formulaire */}
      <div className="w-full lg:w-1/2 flex items-center justify-center p-8 sm:p-12 lg:p-16 bg-white">
        <div className="w-full max-w-md space-y-10">
          <div className="text-center lg:text-left">
            {/* Logo pour vue mobile */}
            <div className="lg:hidden flex justify-center mb-6">
              <Logo size="lg" />
            </div>
            <h2 className="text-3xl font-extrabold text-gray-900 tracking-tight">
              Connexion
            </h2>
            <p className="mt-2 text-sm text-gray-500">
              Veuillez saisir vos identifiants pour accéder à votre espace.
            </p>
          </div>

          <form className="space-y-6" onSubmit={handleLogin}>
            <div className="space-y-5">
              <div>
                <label htmlFor="email" className="block text-sm font-medium text-gray-700 mb-1">
                  Adresse e-mail
                </label>
                <div className="relative">
                  <input
                    id="email"
                    name="email"
                    type="email"
                    autoComplete="email"
                    required
                    className="appearance-none block w-full px-4 py-3 border border-gray-300 rounded-lg shadow-sm placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-primary focus:border-primary transition-shadow sm:text-sm"
                    placeholder="agent@cfgbank.ma"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                  />
                </div>
              </div>

              <div>
                <label htmlFor="password" className="block text-sm font-medium text-gray-700 mb-1">
                  Mot de passe
                </label>
                <div className="relative">
                  <input
                    id="password"
                    name="password"
                    type={showPassword ? 'text' : 'password'}
                    autoComplete="current-password"
                    required
                    className="appearance-none block w-full px-4 py-3 border border-gray-300 rounded-lg shadow-sm placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-primary focus:border-primary transition-shadow sm:text-sm pr-10"
                    placeholder="••••••••"
                    value={motDePasse}
                    onChange={(e) => setMotDePasse(e.target.value)}
                  />
                  <button
                    type="button"
                    className="absolute inset-y-0 right-0 pr-3 flex items-center text-gray-400 hover:text-gray-600 focus:outline-none"
                    onClick={() => setShowPassword(!showPassword)}
                  >
                    {showPassword ? (
                      <EyeOff className="h-5 w-5" aria-hidden="true" />
                    ) : (
                      <Eye className="h-5 w-5" aria-hidden="true" />
                    )}
                  </button>
                </div>
                <div className="flex items-center justify-end mt-2">
                  <div className="text-sm">
                    <a href="#" className="font-medium text-primary hover:text-primary-hover transition-colors">
                      Mot de passe oublié ?
                    </a>
                  </div>
                </div>
              </div>
            </div>

            {error && (
              <div className="rounded-md bg-red-50 p-4 border border-red-200">
                <div className="flex">
                  <div className="ml-3">
                    <h3 className="text-sm font-medium text-red-800">{error}</h3>
                  </div>
                </div>
              </div>
            )}

            <div>
              <button
                type="submit"
                disabled={isLoading}
                className="w-full flex justify-center py-3 px-4 border border-transparent rounded-lg shadow-sm text-sm font-medium text-white bg-primary hover:bg-primary-hover focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary transition-colors disabled:opacity-70 disabled:cursor-not-allowed"
              >
                {isLoading ? (
                  <span className="flex items-center gap-2">
                    <svg className="animate-spin -ml-1 mr-2 h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                      <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                      <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                    </svg>
                    Connexion en cours...
                  </span>
                ) : (
                  'Se connecter'
                )}
              </button>
            </div>
            
            <div className="mt-6 text-center">
              <p className="text-xs text-gray-400">
                Accès restreint. Toute tentative de connexion non autorisée est tracée et signalée.
              </p>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default Login;
