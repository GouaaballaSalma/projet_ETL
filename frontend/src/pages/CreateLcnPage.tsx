import React, { useState, type FormEvent } from 'react';
import { Save, XCircle, CheckCircle, AlertCircle, Loader2, User, Building, FileText, CreditCard } from 'lucide-react';
import { lcnService } from '../services/lcnService';
import { TypeClient, TypeIdentifiantPP, TypeIdentifiantPM } from '../types/LcnSynth';
import type { CreateLcnSynthRequest } from '../types/LcnSynth';

const CreateLcnPage: React.FC = () => {
  const initialState: Partial<CreateLcnSynthRequest> = {
    typeClient: TypeClient.PP,
    devise: 'MAD',
    refClient: '',
    numLcn: '',
    codeBanque: '',
    montant: 0.01,
    insuffisance: 0.01,
    rib: '',
    dateEmission: '',
    dateEcheance: '',
    dateConstat: '',
  };

  const [formData, setFormData] = useState<Partial<CreateLcnSynthRequest>>(initialState);
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [success, setSuccess] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const [validationErrors, setValidationErrors] = useState<string[]>([]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: name === 'montant' || name === 'insuffisance' ? Number(value) : value
    }));
  };

  const handleReset = () => {
    setFormData(initialState);
    setError(null);
    setValidationErrors([]);
    setSuccess(false);
  };

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
    setError(null);
    setValidationErrors([]);
    setSuccess(false);

    try {
      // Nettoyage rapide pour ne pas envoyer dedonnées de l'autre type de client
      const payload = { ...formData };
      if (payload.typeClient === TypeClient.PP) {
        delete payload.raisonSociale;
        delete payload.typeIdentifiantPM;
        delete payload.rc;
        delete payload.identifiantFiscal;
      } else {
        delete payload.nom;
        delete payload.prenom;
        delete payload.typeIdentifiant;
        delete payload.identifiantPrincipal;
      }

      await lcnService.creerIncidentManuel(payload as CreateLcnSynthRequest);
      setSuccess(true);
      setFormData(initialState); // Reset le formulaire après succès
    } catch (err: any) {
      console.error(err);
      if (err.response?.data?.errors) {
        const errData = err.response.data.errors;
        let msgs: string[] = [];
        // Supporte { "rib": "Msg" } OU [{ field: "rib", defaultMessage: "Msg" }]
        if (Array.isArray(errData)) {
          msgs = errData.map((e: any) => `${e.field || 'Champ'} : ${e.defaultMessage || e.message || 'Erreur'}`);
        } else if (typeof errData === 'object') {
          msgs = Object.entries(errData).map(([field, msg]) => `${field} : ${msg}`);
        }
        setValidationErrors(msgs);
        setError('La validation des champs a échoué, veuillez corriger les erreurs ci-dessous.');
      } else {
        setError(err.response?.data?.message || err.message || 'Erreur lors de la création de l\'incident LCN.');
        setValidationErrors([]);
      }
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="flex flex-col p-4 lg:p-6 min-h-screen">
      
      <div className="bg-white shadow-sm border border-gray-200 mb-6">
        <div className="flex justify-between items-center p-4 border-b border-gray-200 bg-gray-50/50">
          <h1 className="text-xl font-bold text-cfg-dark flex items-center gap-2">
            <FileText className="text-cfg-green" /> 
            Saisie Manuelle d'Incident LCN
          </h1>
        </div>
      </div>

      {success && (
        <div className="bg-green-50 border-l-4 border-cfg-green p-4 mb-6 shadow-sm flex items-start">
          <CheckCircle className="h-5 w-5 text-cfg-green mt-0.5" />
          <div className="ml-3">
            <h3 className="text-sm font-bold text-green-800">Succès</h3>
            <p className="text-sm text-green-700 mt-1">L'incident LCN a été créé et enregistré avec succès dans la base de données.</p>
          </div>
        </div>
      )}

      {error && (
        <div className="bg-red-50 border-l-4 border-red-600 p-4 mb-6 shadow-sm flex items-start">
          <AlertCircle className="h-5 w-5 text-red-600 mt-0.5" />
          <div className="ml-3 w-full">
            <h3 className="text-sm font-bold text-red-800">Échec de l'enregistrement</h3>
            <p className="text-sm text-red-700 mt-1">{error}</p>
            {validationErrors.length > 0 && (
              <ul className="mt-2 text-xs text-red-700 bg-red-100/50 p-3 rounded list-disc pl-5">
                 {validationErrors.map((msg, idx) => (
                    <li key={idx} className="mb-0.5 font-medium">{msg}</li>
                 ))}
              </ul>
            )}
          </div>
        </div>
      )}

      <form onSubmit={handleSubmit} className="space-y-6">
        
        {/* BLOC 1: IDENTITE DU DEBITEUR */}
        <div className="bg-white shadow-sm border border-gray-300 rounded-sm overflow-hidden">
          <div className="bg-gray-100 border-b border-t-2 border-t-cfg-green px-4 py-2.5 flex items-center gap-2">
            {formData.typeClient === TypeClient.PP ? (
              <User className="w-4 h-4 text-gray-500" />
            ) : (
              <Building className="w-4 h-4 text-gray-500" />
            )}
            <h2 className="text-sm font-bold text-cfg-dark uppercase tracking-wide">
              1. Identité du Débiteur (Client Impayé)
            </h2>
          </div>
          
          <div className="p-5 grid grid-cols-12 gap-6 bg-orange-50/20">
            <div className="col-span-12 md:col-span-6 lg:col-span-4">
              <label className="block text-xs font-semibold text-gray-700 mb-1">Type de Client <span className="text-red-500">*</span></label>
              <select 
                name="typeClient"
                className="w-full border border-gray-300 bg-white p-2 text-sm focus:outline-none focus:ring-1 focus:ring-cfg-gold focus:border-cfg-gold shadow-sm"
                value={formData.typeClient}
                onChange={handleChange}
                required
              >
                <option value={TypeClient.PP}>Personne Physique (PP)</option>
                <option value={TypeClient.PM}>Personne Morale (PM)</option>
              </select>
            </div>

            <div className="col-span-12 md:col-span-6 lg:col-span-4">
              <label className="block text-xs font-semibold text-gray-700 mb-1">Réf Client <span className="text-red-500">*</span></label>
              <input 
                type="text" 
                name="refClient"
                maxLength={12}
                className="w-full border border-gray-300 bg-white p-2 text-sm focus:outline-none focus:ring-1 focus:ring-cfg-green focus:border-cfg-green shadow-sm"
                value={formData.refClient || ''}
                onChange={handleChange}
                required
              />
            </div>

            <div className="col-span-12 lg:col-span-4 lg:block hidden"></div>

            {formData.typeClient === TypeClient.PP ? (
              <>
                <div className="col-span-12 md:col-span-6 lg:col-span-4">
                  <label className="block text-xs font-semibold text-gray-700 mb-1">Nom</label>
                  <input 
                    type="text" name="nom" maxLength={50}
                    className="w-full border border-gray-300 p-2 text-sm focus:outline-none focus:ring-1 focus:ring-cfg-green focus:border-cfg-green shadow-sm"
                    value={formData.nom || ''} onChange={handleChange}
                  />
                </div>
                <div className="col-span-12 md:col-span-6 lg:col-span-4">
                  <label className="block text-xs font-semibold text-gray-700 mb-1">Prénom</label>
                  <input 
                    type="text" name="prenom" maxLength={50}
                    className="w-full border border-gray-300 p-2 text-sm focus:outline-none focus:ring-1 focus:ring-cfg-green focus:border-cfg-green shadow-sm"
                    value={formData.prenom || ''} onChange={handleChange}
                  />
                </div>
                <div className="col-span-12 lg:col-span-4 lg:block hidden"></div>

                <div className="col-span-12 md:col-span-6 lg:col-span-4">
                  <label className="block text-xs font-semibold text-gray-700 mb-1">Type Identifiant</label>
                  <select 
                    name="typeIdentifiant"
                    className="w-full border border-gray-300 p-2 text-sm focus:outline-none focus:ring-1 focus:ring-cfg-green focus:border-cfg-green shadow-sm"
                    value={formData.typeIdentifiant || ''} onChange={handleChange}
                  >
                    <option value="">Sélectionner</option>
                    <option value="I">CIN</option>
                    <option value="P">Passeport</option>
                    <option value="S">Séjour</option>
                  </select>
                </div>
                <div className="col-span-12 md:col-span-6 lg:col-span-4">
                  <label className="block text-xs font-semibold text-gray-700 mb-1">Valeur Identifiant</label>
                  <input 
                    type="text" name="identifiantPrincipal" maxLength={20}
                    className="w-full border border-gray-300 p-2 text-sm focus:outline-none focus:ring-1 focus:ring-cfg-green focus:border-cfg-green shadow-sm"
                    value={formData.identifiantPrincipal || ''} onChange={handleChange}
                  />
                </div>
              </>
            ) : (
              <>
                <div className="col-span-12 md:col-span-8 lg:col-span-8">
                  <label className="block text-xs font-semibold text-gray-700 mb-1">Raison Sociale</label>
                  <input 
                    type="text" name="raisonSociale" maxLength={120}
                    className="w-full border border-gray-300 p-2 text-sm focus:outline-none focus:ring-1 focus:ring-cfg-green focus:border-cfg-green shadow-sm"
                    value={formData.raisonSociale || ''} onChange={handleChange}
                  />
                </div>
                <div className="col-span-12 lg:col-span-4 lg:block hidden"></div>
                
                <div className="col-span-12 md:col-span-6 lg:col-span-4">
                  <label className="block text-xs font-semibold text-gray-700 mb-1">Type Identifiant PM</label>
                  <select 
                    name="typeIdentifiantPM"
                    className="w-full border border-gray-300 p-2 text-sm focus:outline-none focus:ring-1 focus:ring-cfg-green focus:border-cfg-green shadow-sm"
                    value={formData.typeIdentifiantPM || ''} onChange={handleChange}
                  >
                    <option value="">Sélectionner</option>
                    {/* Le DTO de Spring exige le mappage direct sous forme d'Enum "RC" ou "IF" */}
                    <option value="RC">Registre de Commerce (RC)</option>
                    <option value="IF">Identifiant Fiscal (IF)</option>
                  </select>
                </div>
                <div className="col-span-12 md:col-span-6 lg:col-span-4">
                   <label className="block text-xs font-semibold text-gray-700 mb-1">Valeur (RC ou IF)</label>
                   <input 
                    type="text" name={formData.typeIdentifiantPM === TypeIdentifiantPM.RC ? 'rc' : 'identifiantFiscal'} maxLength={20}
                    className="w-full border border-gray-300 p-2 text-sm focus:outline-none focus:ring-1 focus:ring-cfg-green focus:border-cfg-green shadow-sm"
                    value={formData.typeIdentifiantPM === TypeIdentifiantPM.RC ? (formData.rc || '') : (formData.identifiantFiscal || '')} 
                    onChange={handleChange}
                    placeholder={formData.typeIdentifiantPM ? `Entrez le ${formData.typeIdentifiantPM}` : ''}
                  />
                </div>
              </>
            )}
          </div>
        </div>

        {/* BLOC 2: REFERENCES BANCAIRES & LCN */}
        <div className="bg-white shadow-sm border border-gray-300 rounded-sm overflow-hidden">
           <div className="bg-gray-100 border-b border-t-2 border-t-cfg-green px-4 py-2.5 flex items-center gap-2">
            <CreditCard className="w-4 h-4 text-gray-500" />
            <h2 className="text-sm font-bold text-cfg-dark uppercase tracking-wide">
              2. Références Bancaires & LCN
            </h2>
          </div>
          <div className="p-5 grid grid-cols-12 gap-6">
            <div className="col-span-12 md:col-span-12 lg:col-span-6">
              <label className="block text-xs font-semibold text-gray-700 mb-1">RIB <span className="text-red-500">*</span></label>
              <input 
                type="text" name="rib" maxLength={24} minLength={24}
                className="w-full border border-gray-300 p-2 text-sm focus:outline-none focus:ring-1 focus:ring-cfg-green focus:border-cfg-green shadow-sm font-mono tracking-wider"
                value={formData.rib || ''} onChange={handleChange}
                placeholder="000000000000000000000000"
                required
              />
            </div>
            <div className="col-span-12 md:col-span-6 lg:col-span-3">
              <label className="block text-xs font-semibold text-gray-700 mb-1">Code Banque <span className="text-red-500">*</span></label>
              <input 
                type="text" name="codeBanque" maxLength={3}
                className="w-full border border-gray-300 p-2 text-sm focus:outline-none focus:ring-1 focus:ring-cfg-green focus:border-cfg-green shadow-sm font-mono"
                value={formData.codeBanque || ''} onChange={handleChange}
                required
              />
            </div>
            <div className="col-span-12 md:col-span-6 lg:col-span-3">
              <label className="block text-xs font-semibold text-gray-700 mb-1">Numéro LCN <span className="text-red-500">*</span></label>
              <input 
                type="text" name="numLcn" maxLength={10}
                className="w-full border border-gray-300 p-2 text-sm focus:outline-none focus:ring-1 focus:ring-cfg-green focus:border-cfg-green shadow-sm font-mono font-bold"
                value={formData.numLcn || ''} onChange={handleChange}
                required
              />
            </div>
          </div>
        </div>

        {/* BLOC 3: DETAILS FINANCIERS & CONSTAT */}
        <div className="bg-white shadow-sm border border-gray-300 rounded-sm overflow-hidden">
           <div className="bg-gray-100 border-b border-t-2 border-t-cfg-green px-4 py-2.5 flex items-center gap-2">
            <AlertCircle className="w-4 h-4 text-gray-500" />
            <h2 className="text-sm font-bold text-cfg-dark uppercase tracking-wide">
              3. Détails Financiers & Constat
            </h2>
          </div>
          <div className="p-5 grid grid-cols-12 gap-6">
            <div className="col-span-12 md:col-span-6 lg:col-span-4">
              <label className="block text-xs font-semibold text-gray-700 mb-1">Montant <span className="text-red-500">*</span></label>
              <div className="relative">
                <input 
                  type="number" name="montant" step="0.01" min="0.01"
                  className="w-full border border-gray-300 p-2 pr-12 text-sm focus:outline-none focus:ring-1 focus:ring-cfg-green focus:border-cfg-green shadow-sm text-right font-mono"
                  value={formData.montant || ''} onChange={handleChange} required
                />
                <div className="absolute inset-y-0 right-0 pr-3 flex items-center pointer-events-none">
                  <span className="text-gray-500 sm:text-xs">MAD</span>
                </div>
              </div>
            </div>

            <div className="col-span-12 md:col-span-6 lg:col-span-4">
              <label className="block text-xs font-semibold text-gray-700 mb-1">Devise <span className="text-red-500">*</span></label>
              <input 
                type="text" name="devise" maxLength={3} readOnly
                className="w-full border border-gray-300 bg-gray-50 p-2 text-sm shadow-sm"
                value={formData.devise || 'MAD'} onChange={handleChange} required
              />
            </div>

            <div className="col-span-12 md:col-span-6 lg:col-span-4">
              <label className="block text-xs font-semibold text-gray-700 mb-1">Insuffisance <span className="text-red-500">*</span></label>
              <div className="relative">
                <input 
                  type="number" name="insuffisance" step="0.01" min="0.01"
                  className="w-full border border-gray-300 p-2 pr-12 text-sm focus:outline-none focus:ring-1 focus:ring-cfg-green focus:border-cfg-green shadow-sm text-right font-mono text-red-600 font-semibold"
                  value={formData.insuffisance !== undefined ? formData.insuffisance : ''} onChange={handleChange} required
                />
                 <div className="absolute inset-y-0 right-0 pr-3 flex items-center pointer-events-none">
                  <span className="text-gray-500 sm:text-xs">MAD</span>
                </div>
              </div>
            </div>

            <div className="col-span-12 md:col-span-4 lg:col-span-4">
              <label className="block text-xs font-semibold text-gray-700 mb-1">Date d'émission <span className="text-red-500">*</span></label>
              <input 
                type="date" name="dateEmission"
                className="w-full border border-gray-300 p-2 text-sm focus:outline-none focus:ring-1 focus:ring-cfg-green focus:border-cfg-green shadow-sm"
                value={formData.dateEmission || ''} onChange={handleChange} required
              />
            </div>

            <div className="col-span-12 md:col-span-4 lg:col-span-4">
              <label className="block text-xs font-semibold text-gray-700 mb-1">Date d'échéance <span className="text-red-500">*</span></label>
              <input 
                type="date" name="dateEcheance"
                className="w-full border border-gray-300 p-2 text-sm focus:outline-none focus:ring-1 focus:ring-cfg-green focus:border-cfg-green shadow-sm"
                value={formData.dateEcheance || ''} onChange={handleChange} required
              />
            </div>

            <div className="col-span-12 md:col-span-4 lg:col-span-4">
              <label className="block text-xs font-semibold text-gray-700 mb-1">Date de constat <span className="text-red-500">*</span></label>
              <input 
                type="date" name="dateConstat"
                className="w-full border border-gray-300 p-2 text-sm focus:outline-none focus:ring-1 focus:ring-cfg-green focus:border-cfg-green shadow-sm bg-orange-50/50"
                value={formData.dateConstat || ''} onChange={handleChange} required
              />
            </div>
          </div>
        </div>

        {/* ACTIONS */}
        <div className="flex justify-end gap-4 pt-4 border-t border-gray-200">
          <button 
            type="button" 
            onClick={handleReset}
            className="flex items-center px-6 py-2.5 border border-gray-300 bg-white text-gray-700 text-sm font-semibold hover:bg-gray-50 transition-colors shadow-sm"
          >
            <XCircle className="w-4 h-4 mr-2" /> Annuler
          </button>
          <button 
            type="submit" 
            disabled={isLoading}
            className="flex items-center px-6 py-2.5 border border-transparent bg-cfg-green text-white text-sm font-bold hover:bg-[#00472E] transition-colors shadow-sm disabled:opacity-70 focus:ring-2 focus:ring-offset-2 focus:ring-cfg-green"
          >
            {isLoading ? <Loader2 className="w-4 h-4 mr-2 animate-spin" /> : <Save className="w-4 h-4 mr-2" />} 
            Enregistrer l'incident
          </button>
        </div>

      </form>
    </div>
  );
};

export default CreateLcnPage;
