import React, { useState, useEffect } from 'react';
import api from '../../api/axiosConfig';
import { Users, Trash2 } from 'lucide-react';
import DeleteUserModal from '../../components/DeleteUserModal';

interface UtilisateurResponse {
  id: number;
  email: string;
  nomComplet: string;
  role: string;
  actif: boolean;
}

const UserManagement: React.FC = () => {
  const [users, setUsers] = useState<UtilisateurResponse[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState('');

  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [userToDelete, setUserToDelete] = useState<UtilisateurResponse | null>(null);

  // Form state
  const [email, setEmail] = useState('');
  const [nomComplet, setNomComplet] = useState('');
  const [motDePasse, setMotDePasse] = useState('');
  const [role, setRole] = useState('ROLE_BUSINESS');
  const [isSubmitting, setIsSubmitting] = useState(false);

  const fetchUsers = async () => {
    try {
      setIsLoading(true);
      const response = await api.get('/api/admin/users');
      setUsers(response.data);
      setError('');
    } catch (err) {
      setError('Erreur lors de la récupération des utilisateurs.');
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchUsers();
  }, []);

  const handleCreateUser = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      setIsSubmitting(true);
      await api.post('/api/admin/users', {
        email,
        nomComplet,
        motDePasse,
        role
      });
      // Reset form
      setEmail('');
      setNomComplet('');
      setMotDePasse('');
      setRole('ROLE_BUSINESS');
      // Refresh list
      fetchUsers();
    } catch (err) {
      setError('Erreur lors de la création de l\'utilisateur.');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleToggleStatus = async (id: number) => {
    try {
      await api.patch(`/api/admin/users/${id}/toggle-status`);
      fetchUsers();
    } catch (err) {
      setError('Erreur lors de la modification du statut.');
    }
  };

  const confirmDeleteUser = async () => {
    if (!userToDelete) return;
    try {
      await api.delete(`/api/admin/users/${userToDelete.id}`);
      setIsDeleteModalOpen(false);
      setUserToDelete(null);
      fetchUsers();
    } catch (err: any) {
      if (err.response && err.response.data && err.response.data.message) {
        setError(err.response.data.message);
      } else {
        setError("Erreur lors de la suppression de l'utilisateur.");
      }
      setIsDeleteModalOpen(false);
      setUserToDelete(null);
    }
  };

  return (
    <div className="p-8">
      {/* En-tête stylisé harmonisé */}
      <div className="bg-white p-5 rounded-lg shadow-sm border border-gray-100 flex items-center justify-between mb-6">
        <div className="flex items-center gap-3">
          <Users className="text-red-600 h-6 w-6" /> 
          <h1 className="text-xl font-bold text-gray-900">Gestion des Utilisateurs</h1>
        </div>
      </div>

      {error && (
        <div className="mb-6 p-4 bg-red-50 border border-red-200 rounded-md text-red-800 text-sm">
          {error}
        </div>
      )}

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        
        {/* Colonne gauche: Formulaire de création */}
        <div className="lg:col-span-1 bg-white p-6 rounded-lg shadow-sm border border-gray-200">
          <h2 className="text-xl font-bold text-gray-800 mb-4">Nouvel Utilisateur</h2>
          <form onSubmit={handleCreateUser} className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Nom Complet</label>
              <input
                type="text"
                required
                value={nomComplet}
                onChange={e => setNomComplet(e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-primary focus:border-primary text-sm"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Email</label>
              <input
                type="email"
                required
                value={email}
                onChange={e => setEmail(e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-primary focus:border-primary text-sm"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Mot de passe</label>
              <input
                type="password"
                required
                value={motDePasse}
                onChange={e => setMotDePasse(e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-primary focus:border-primary text-sm"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Rôle</label>
              <select
                value={role}
                onChange={e => setRole(e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-primary focus:border-primary text-sm"
              >
                <option value="ROLE_ADMIN">Administrateur</option>
                <option value="ROLE_BUSINESS">Business (CRUD)</option>
                <option value="ROLE_READ_ONLY">Lecture Seule</option>
                <option value="ROLE_API_CLIENT">Client API</option>
              </select>
            </div>
            <button
              type="submit"
              disabled={isSubmitting}
              className="w-full mt-4 py-2 px-4 bg-primary text-white font-medium rounded-md hover:bg-primary-hover transition-colors disabled:opacity-50"
            >
              {isSubmitting ? 'Création...' : 'Créer l\'utilisateur'}
            </button>
          </form>
        </div>

        {/* Colonne droite: Liste des utilisateurs */}
        <div className="lg:col-span-2 bg-white rounded-lg shadow-sm border border-gray-200 overflow-hidden">
          <div className="p-6 border-b border-gray-200">
            <h2 className="text-xl font-bold text-gray-800">Utilisateurs Enregistrés</h2>
          </div>
          
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm text-gray-600">
              <thead className="bg-gray-50 text-gray-700 uppercase font-semibold border-b border-gray-200">
                <tr>
                  <th className="px-6 py-4">Nom Complet</th>
                  <th className="px-6 py-4">Email</th>
                  <th className="px-6 py-4">Rôle</th>
                  <th className="px-6 py-4 text-center">Statut</th>
                  <th className="px-6 py-4 text-center">Actions</th>
                </tr>
              </thead>
              <tbody>
                {isLoading ? (
                  <tr>
                    <td colSpan={5} className="px-6 py-8 text-center text-gray-500">
                      Chargement des utilisateurs...
                    </td>
                  </tr>
                ) : users.length === 0 ? (
                  <tr>
                    <td colSpan={5} className="px-6 py-8 text-center text-gray-500">
                      Aucun utilisateur trouvé.
                    </td>
                  </tr>
                ) : (
                  users.map((user) => (
                    <tr key={user.id} className="border-b border-gray-100 hover:bg-gray-50">
                      <td className="px-6 py-4 font-medium text-gray-900">{user.nomComplet}</td>
                      <td className="px-6 py-4">{user.email}</td>
                      <td className="px-6 py-4">
                        <span className={`px-2 py-1 rounded-full text-xs font-medium ${
                          user.role === 'ROLE_ADMIN' 
                            ? 'bg-purple-100 text-purple-800' 
                            : user.role === 'ROLE_BUSINESS'
                            ? 'bg-blue-100 text-blue-800'
                            : user.role === 'ROLE_READ_ONLY'
                            ? 'bg-gray-100 text-gray-800'
                            : 'bg-green-100 text-green-800'
                        }`}>
                          {user.role}
                        </span>
                      </td>
                      <td className="px-6 py-4 text-center">
                        <span className={`px-2 py-1 rounded-full text-xs font-medium ${
                          user.actif !== false ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'
                        }`}>
                          {user.actif !== false ? 'Actif' : 'Inactif'}
                        </span>
                      </td>
                      <td className="px-6 py-4 text-center">
                        <div className="flex justify-center space-x-2">
                          <button
                            onClick={() => handleToggleStatus(user.id)}
                            className={`px-3 py-1 text-xs font-semibold rounded-md transition-colors ${
                              user.actif !== false
                                ? 'bg-orange-50 text-orange-600 hover:bg-orange-100 border border-orange-200' 
                                : 'bg-green-50 text-green-600 hover:bg-green-100 border border-green-200'
                            }`}
                          >
                            {user.actif !== false ? 'Désactiver' : 'Activer'}
                          </button>
                          <button
                            onClick={() => {
                              setUserToDelete(user);
                              setIsDeleteModalOpen(true);
                            }}
                            className="p-1.5 text-red-600 bg-transparent rounded-full hover:bg-red-50 hover:text-red-800 transition-colors"
                            title="Supprimer"
                          >
                            <Trash2 className="w-5 h-5" />
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </div>

      </div>
      <DeleteUserModal
        isOpen={isDeleteModalOpen}
        onClose={() => setIsDeleteModalOpen(false)}
        onConfirm={confirmDeleteUser}
        userName={userToDelete?.nomComplet || ''}
      />
    </div>
  );
};

export default UserManagement;
