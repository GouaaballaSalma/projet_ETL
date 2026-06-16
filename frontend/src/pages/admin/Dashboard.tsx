import React, { useState, useEffect } from 'react';
import api from '../../api/axiosConfig';
import { BarChart3, AlertTriangle, Users, TrendingUp, DollarSign } from 'lucide-react';

interface DashboardStats {
  totalIncidents: number;
  montantGlobal: number;
  repartitionTypeClient: Record<string, number>;
}

interface ClientRiskScore {
  nomClient: string;
  typeClient: string;
  totalIncidents: number;
  montantTotal: number;
  riskScore: number;
  niveauRisque: 'ÉLEVÉ' | 'MOYEN' | 'FAIBLE';
}

const Dashboard: React.FC = () => {
  const [stats, setStats] = useState<DashboardStats | null>(null);
  const [riskScores, setRiskScores] = useState<ClientRiskScore[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const fetchDashboardData = async () => {
      try {
        setIsLoading(true);
        const [statsRes, riskRes] = await Promise.all([
          api.get('/api/admin/dashboard/stats'),
          api.get('/api/admin/dashboard/risk-scoring')
        ]);
        setStats(statsRes.data);
        setRiskScores(riskRes.data);
      } catch (err) {
        console.error("Erreur lors du chargement du tableau de bord", err);
      } finally {
        setIsLoading(false);
      }
    };
    fetchDashboardData();
  }, []);

  if (isLoading) {
    return <div className="p-8 text-center text-gray-500">Chargement du tableau de bord...</div>;
  }

  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat('fr-MA', { style: 'currency', currency: 'MAD' }).format(amount);
  };

  return (
    <div className="p-8 space-y-8">
      {/* Header */}
      <div className="bg-white p-5 rounded-lg shadow-sm border border-gray-100 flex items-center gap-3">
        <BarChart3 className="text-red-600 h-6 w-6" /> 
        <h1 className="text-xl font-bold text-gray-900">Tableau de Bord des Incidents (Scoring)</h1>
      </div>

      {/* KPIs Cards */}
      {stats && (
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          <div className="bg-white p-6 rounded-lg shadow-sm border border-gray-100 flex items-center gap-4">
            <div className="bg-red-100 p-3 rounded-full text-red-600">
              <AlertTriangle className="h-6 w-6" />
            </div>
            <div>
              <p className="text-sm font-medium text-gray-500">Total des incidents</p>
              <h3 className="text-2xl font-bold text-gray-900">{stats.totalIncidents}</h3>
            </div>
          </div>
          
          <div className="bg-white p-6 rounded-lg shadow-sm border border-gray-100 flex items-center gap-4">
            <div className="bg-orange-100 p-3 rounded-full text-orange-600">
              <DollarSign className="h-6 w-6" />
            </div>
            <div>
              <p className="text-sm font-medium text-gray-500">Montant global impayé</p>
              <h3 className="text-2xl font-bold text-gray-900">{formatCurrency(stats.montantGlobal)}</h3>
            </div>
          </div>

          <div className="bg-white p-6 rounded-lg shadow-sm border border-gray-100 flex items-center gap-4">
            <div className="bg-blue-100 p-3 rounded-full text-blue-600">
              <Users className="h-6 w-6" />
            </div>
            <div>
              <p className="text-sm font-medium text-gray-500">Répartition PP / PM</p>
              <div className="flex gap-4 mt-1">
                <div className="text-sm"><span className="font-bold text-gray-800">{stats.repartitionTypeClient['PP'] || 0}</span> PP</div>
                <div className="text-sm"><span className="font-bold text-gray-800">{stats.repartitionTypeClient['PM'] || 0}</span> PM</div>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Top Clients Risk Table */}
      <div className="bg-white rounded-lg shadow-sm border border-gray-200 overflow-hidden">
        <div className="p-6 border-b border-gray-200 flex items-center gap-2">
          <TrendingUp className="text-gray-600 h-5 w-5" />
          <h2 className="text-lg font-bold text-gray-800">Top Clients à Risque</h2>
        </div>
        
        <div className="overflow-x-auto">
          <table className="w-full text-left text-sm text-gray-600">
            <thead className="bg-gray-50 text-gray-700 uppercase font-semibold border-b border-gray-200">
              <tr>
                <th className="px-6 py-4">Client</th>
                <th className="px-6 py-4 text-center">Type</th>
                <th className="px-6 py-4 text-center">Incidents</th>
                <th className="px-6 py-4 text-right">Montant Cumulé</th>
                <th className="px-6 py-4">Score de Risque</th>
                <th className="px-6 py-4 text-center">Niveau</th>
              </tr>
            </thead>
            <tbody>
              {riskScores.length === 0 ? (
                <tr>
                  <td colSpan={6} className="px-6 py-8 text-center text-gray-500">Aucune donnée disponible</td>
                </tr>
              ) : (
                riskScores.map((client, index) => {
                  let badgeClass = "bg-green-100 text-green-800 border-green-200";
                  if (client.niveauRisque === 'ÉLEVÉ') badgeClass = "bg-red-100 text-red-800 border-red-200";
                  if (client.niveauRisque === 'MOYEN') badgeClass = "bg-orange-100 text-orange-800 border-orange-200";

                  // Calcul d'un pourcentage pour la barre (max 200 pour le visuel par ex)
                  const progressWidth = Math.min(100, (client.riskScore / 150) * 100);
                  let progressColor = "bg-green-500";
                  if (client.niveauRisque === 'ÉLEVÉ') progressColor = "bg-red-500";
                  if (client.niveauRisque === 'MOYEN') progressColor = "bg-orange-500";

                  return (
                    <tr key={index} className="border-b border-gray-100 hover:bg-gray-50">
                      <td className="px-6 py-4 font-bold text-gray-900">{client.nomClient}</td>
                      <td className="px-6 py-4 text-center font-medium">{client.typeClient}</td>
                      <td className="px-6 py-4 text-center">{client.totalIncidents}</td>
                      <td className="px-6 py-4 text-right font-medium text-gray-900">{formatCurrency(client.montantTotal)}</td>
                      <td className="px-6 py-4">
                        <div className="flex items-center gap-3">
                          <span className="font-bold w-8">{client.riskScore.toFixed(1)}</span>
                          <div className="w-full bg-gray-200 rounded-full h-2">
                            <div className={`${progressColor} h-2 rounded-full`} style={{ width: `${progressWidth}%` }}></div>
                          </div>
                        </div>
                      </td>
                      <td className="px-6 py-4 text-center">
                        <span className={`px-3 py-1 rounded-full text-xs font-bold border ${badgeClass}`}>
                          {client.niveauRisque}
                        </span>
                      </td>
                    </tr>
                  );
                })
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
