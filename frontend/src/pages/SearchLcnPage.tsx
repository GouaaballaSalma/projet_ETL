import React, { useState, type FormEvent } from 'react';
import { Search, Eraser, ChevronLeft, ChevronRight, AlertCircle, Loader2 } from 'lucide-react';
import { lcnService, type SearchLcnParams } from '../services/lcnService';
import { TypeClient, TypeIdentifiantPP, TypeIdentifiantPM } from '../types/LcnSynth';
import type { LcnSynthDTO, Page } from '../types/LcnSynth';

const SearchLcnPage: React.FC = () => {
  const [typeClient, setTypeClient] = useState<TypeClient>(TypeClient.PP);
  const [nomComplet, setNomComplet] = useState<string>('');
  const [typeIdentifiant, setTypeIdentifiant] = useState<string>('');
  const [valeurIdentifiant, setValeurIdentifiant] = useState<string>('');

  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const [data, setData] = useState<Page<LcnSynthDTO> | null>(null);
  
  const [currentPage, setCurrentPage] = useState<number>(0);
  const pageSize = 10;

  const handleSearch = async (page: number = 0) => {
    setIsLoading(true);
    setError(null);
    try {
      const params: SearchLcnParams = {
        typeClient,
        page,
        size: pageSize,
      };

      if (nomComplet) params.nomComplet = nomComplet;
      if (valeurIdentifiant) params.identifiant = valeurIdentifiant;
      
      if (typeIdentifiant) {
        if (typeClient === TypeClient.PP) {
          params.typeIdentifiantPP = typeIdentifiant as TypeIdentifiantPP;
        } else {
          params.typeIdentifiantPM = typeIdentifiant as TypeIdentifiantPM;
        }
      }

      const response = await lcnService.rechercherIncidents(params);
      setData(response);
      setCurrentPage(page);
    } catch (err: any) {
      console.error(err);
      setError(err.response?.data?.message || 'Erreur lors de la recherche des incidents.');
    } finally {
      setIsLoading(false);
    }
  };

  const onSubmit = (e: FormEvent) => {
    e.preventDefault();
    handleSearch(0);
  };

  const onReset = () => {
    setTypeClient(TypeClient.PP);
    setNomComplet('');
    setTypeIdentifiant('');
    setValeurIdentifiant('');
    setData(null);
    setError(null);
    setCurrentPage(0);
  };

  const identifiantOptions = typeClient === TypeClient.PP 
    ? Object.values(TypeIdentifiantPP)
    : Object.values(TypeIdentifiantPM);

  return (
    <div className="flex flex-col h-full space-y-4 p-4 lg:p-6 bg-cfg-light min-h-screen">
      <div className="flex justify-between items-center bg-white p-4 shadow-sm border border-gray-200">
        <h1 className="text-xl font-bold text-cfg-dark flex items-center gap-2">
          <Search className="text-cfg-green" /> 
          Recherche Synthétique LCN
        </h1>
        <div className="text-sm font-medium text-gray-500 bg-gray-100 px-3 py-1 rounded-full border border-gray-200">
          Plateforme LCN
        </div>
      </div>

      {/* FILTRES */}
      <div className="bg-white shadow-sm border border-gray-300">
        <div className="bg-cfg-green text-white px-4 py-2 text-sm font-semibold flex items-center">
          Option
        </div>
        <form onSubmit={onSubmit} className="p-4">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4 gap-y-3">
            
            <div className="flex items-center">
              <label className="w-1/3 text-sm font-semibold text-gray-700 text-right pr-4">Type de personne</label>
              <select 
                className="w-2/3 border border-gray-300 bg-orange-50 p-1.5 text-sm focus:outline-none focus:ring-1 focus:ring-cfg-gold focus:border-cfg-gold"
                value={typeClient}
                onChange={(e) => {
                  setTypeClient(e.target.value as TypeClient);
                  setTypeIdentifiant('');
                }}
              >
                <option value={TypeClient.PP}>Personne Physique (PP)</option>
                <option value={TypeClient.PM}>Personne Morale (PM)</option>
              </select>
            </div>

            <div className="flex items-center">
              <label className="w-1/3 text-sm font-semibold text-gray-700 text-right pr-4">Nom complet / Raison Sociale</label>
              <input 
                type="text" 
                className="w-2/3 border border-gray-300 p-1.5 text-sm focus:outline-none focus:ring-1 focus:ring-cfg-green focus:border-cfg-green"
                value={nomComplet}
                onChange={(e) => setNomComplet(e.target.value)}
              />
            </div>

            <div className="flex items-center">
              <label className="w-1/3 text-sm font-semibold text-gray-700 text-right pr-4">Type identifiant 1</label>
              <select 
                className="w-2/3 border border-gray-300 p-1.5 text-sm focus:outline-none focus:ring-1 focus:ring-cfg-green focus:border-cfg-green"
                value={typeIdentifiant}
                onChange={(e) => setTypeIdentifiant(e.target.value)}
              >
                <option value="">Sélectionner</option>
                {identifiantOptions.map(opt => (
                  <option key={opt} value={opt}>{opt}</option>
                ))}
              </select>
            </div>

            <div className="flex items-center">
              <label className="w-1/3 text-sm font-semibold text-gray-700 text-right pr-4">Valeur identifiant 1</label>
              <input 
                type="text" 
                className="w-2/3 border border-gray-300 p-1.5 text-sm focus:outline-none focus:ring-1 focus:ring-cfg-green focus:border-cfg-green"
                value={valeurIdentifiant}
                onChange={(e) => setValeurIdentifiant(e.target.value)}
              />
            </div>
            
          </div>

          <div className="mt-6 flex justify-end gap-3 pt-4 border-t border-gray-100">
            <button 
              type="button" 
              onClick={onReset}
              className="flex items-center px-4 py-1.5 border border-gray-300 bg-gray-50 text-gray-700 text-sm font-medium hover:bg-gray-100 transition-colors shadow-sm"
            >
              <Eraser className="w-4 h-4 mr-2" /> Effacer
            </button>
            <button 
              type="submit" 
              disabled={isLoading}
              className="flex items-center px-4 py-1.5 border border-transparent bg-cfg-green text-white text-sm font-medium hover:bg-[#00472E] transition-colors shadow-sm disabled:opacity-70"
            >
              {isLoading ? <Loader2 className="w-4 h-4 mr-2 animate-spin" /> : <Search className="w-4 h-4 mr-2" />} 
              Rechercher
            </button>
          </div>
        </form>
      </div>

      {error && (
        <div className="bg-red-50 border-l-4 border-red-500 p-4">
          <div className="flex">
            <AlertCircle className="h-5 w-5 text-red-500" />
            <div className="ml-3">
              <p className="text-sm text-red-700">{error}</p>
            </div>
          </div>
        </div>
      )}

      {/* RÉSULTATS */}
      <div className="bg-white shadow-sm border border-gray-300 flex-1 flex flex-col min-h-0">
        <div className="bg-cfg-green text-white px-4 py-2 text-sm font-semibold flex items-center justify-between">
          <span>File d'attente / Résultats</span>
          {data && (
            <span className="text-xs font-normal">
              Total : {data.totalElements} ligne(s)
            </span>
          )}
        </div>
        
        <div className="overflow-x-auto">
          <table className="w-full text-sm text-left whitespace-nowrap">
            <thead className="text-xs text-gray-700 uppercase bg-gray-100 border-b border-gray-300 sticky top-0">
              <tr>
                <th className="px-4 py-2.5 font-semibold border-r border-gray-200">Réf Impayé</th>
                <th className="px-4 py-2.5 font-semibold border-r border-gray-200">Identité</th>
                <th className="px-4 py-2.5 font-semibold border-r border-gray-200">Identifiant</th>
                <th className="px-4 py-2.5 font-semibold border-r border-gray-200 text-right">Montant</th>
                <th className="px-4 py-2.5 font-semibold border-r border-gray-200">Statut</th>
                <th className="px-4 py-2.5 font-semibold">Date Constat</th>
              </tr>
            </thead>
            <tbody>
              {!data ? (
                <tr>
                  <td colSpan={6} className="px-4 py-8 text-center text-gray-500 italic">
                    Aucune recherche effectuée.
                  </td>
                </tr>
              ) : data.content.length === 0 ? (
                <tr>
                  <td colSpan={6} className="px-4 py-8 text-center text-gray-500 italic">
                    Aucun résultat trouvé.
                  </td>
                </tr>
              ) : (
                data.content.map((row, idx) => (
                  <tr key={idx} className="border-b border-gray-200 hover:bg-orange-50 transition-colors">
                    <td className="px-4 py-2 border-r border-gray-200 font-medium text-cfg-dark">{row.refImpaye || '-'}</td>
                    <td className="px-4 py-2 border-r border-gray-200">
                      {row.typeClient === 'PP' ? `${row.nom || ''} ${row.prenom || ''}` : row.raisonSociale || '-'}
                    </td>
                    <td className="px-4 py-2 border-r border-gray-200 text-gray-600 font-mono text-xs">
                      {row.identifiantPrincipal || '-'} <span className="text-[10px] text-gray-400">({row.typeIdentifiant || '-'})</span>
                    </td>
                    <td className="px-4 py-2 border-r border-gray-200 text-right">
                      {row.montant !== null ? new Intl.NumberFormat('fr-MA', { style: 'currency', currency: row.devise || 'MAD' }).format(row.montant) : '-'}
                    </td>
                    <td className="px-4 py-2 border-r border-gray-200">
                      <span className={`px-2 py-0.5 text-xs font-medium rounded border ${row.statut === 'RÉGULARISÉ' ? 'bg-green-100 text-green-800 border-green-200' : 'bg-red-100 text-red-800 border-red-200'}`}>
                        {row.statut || row.codeStatut || 'EN COURS'}
                      </span>
                    </td>
                    <td className="px-4 py-2 text-gray-500">
                      {row.dateConstat ? new Date(row.dateConstat).toLocaleDateString('fr-FR') : '-'}
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>

        {/* PAGINATION */}
        {data && data.totalPages > 1 && (
          <div className="flex items-center justify-between px-4 py-3 bg-gray-50 border-t border-gray-300 mt-auto">
            <span className="text-sm text-gray-700">
              Page <span className="font-semibold text-cfg-dark">{data.number + 1}</span> sur <span className="font-semibold text-cfg-dark">{data.totalPages}</span>
            </span>
            <div className="flex gap-2">
              <button
                onClick={() => handleSearch(currentPage - 1)}
                disabled={data.first || isLoading}
                className="p-1 px-2 border border-gray-300 bg-white text-gray-600 hover:bg-gray-100 disabled:opacity-50 disabled:bg-gray-100 transition-colors flex items-center"
              >
                <ChevronLeft className="w-4 h-4" /> Précédent
              </button>
              <button
                onClick={() => handleSearch(currentPage + 1)}
                disabled={data.last || isLoading}
                className="p-1 px-2 border border-gray-300 bg-white text-gray-600 hover:bg-gray-100 disabled:opacity-50 disabled:bg-gray-100 transition-colors flex items-center"
              >
                Suivant <ChevronRight className="w-4 h-4" />
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default SearchLcnPage;
