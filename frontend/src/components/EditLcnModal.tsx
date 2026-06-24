import React, { useState, useEffect } from 'react';
import type { LcnSynthDTO } from '../types/LcnSynth';
import { Loader2, X } from 'lucide-react';

interface EditLcnModalProps {
  isOpen: boolean;
  lcn: LcnSynthDTO | null;
  onClose: () => void;
  onSave: (refImpaye: string, data: any) => Promise<void>;
}

const EditLcnModal: React.FC<EditLcnModalProps> = ({ isOpen, lcn, onClose, onSave }) => {
  const [formData, setFormData] = useState<any>({
    montant: 0,
    devise: 'MAD',
    statut: 'IMPAYÉ',
    dateEmission: '',
    dateEcheance: '',
    dateConstat: '',
    insuffisance: 0,
    rib: '',
    refClient: '',
    typeClient: 'PP',
    nom: '',
    prenom: '',
    typeIdentifiant: '',
    identifiantPrincipal: '',
    dateNaissance: '',
    raisonSociale: '',
    typeIdentifiantPM: '',
    rc: '',
    identifiantFiscal: '',
    codeBanque: '',
    numLcn: ''
  });
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    if (lcn) {
      setFormData({
        refClient: lcn.refClient || '',
        typeClient: lcn.typeClient || 'PP',
        nom: lcn.nom || '',
        prenom: lcn.prenom || '',
        typeIdentifiant: lcn.typeIdentifiant || '',
        identifiantPrincipal: lcn.identifiantPrincipal || '',
        dateNaissance: lcn.dateNaissance ? lcn.dateNaissance.split('T')[0] : '',
        raisonSociale: lcn.raisonSociale || '',
        typeIdentifiantPM: lcn.typeIdentifiantPM || '',
        rc: lcn.rc || '',
        identifiantFiscal: lcn.identifiantFiscal || '',
        codeBanque: lcn.codeBanque || '',
        numLcn: lcn.numLcn || '',
        montant: lcn.montant || 0,
        devise: lcn.devise || 'MAD',
        statut: lcn.statut || 'IMPAYÉ',
        dateEmission: lcn.dateEmission ? lcn.dateEmission.split('T')[0] : '',
        dateEcheance: lcn.dateEcheance ? lcn.dateEcheance.split('T')[0] : '',
        dateConstat: lcn.dateConstat ? lcn.dateConstat.split('T')[0] : '',
        insuffisance: lcn.insuffisance || 0,
        rib: lcn.rib || '',
      });
    }
  }, [lcn]);

  if (!isOpen || !lcn) return null;

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value, type } = e.target;
    setFormData((prev: any) => ({
      ...prev,
      [name]: type === 'number' ? Number(value) : value
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSubmitting(true);
    try {
      const payload = { ...formData };
      
      // Nettoyage des chaînes vides et vérification des types
      Object.keys(payload).forEach(key => {
        if (payload[key] === '') {
          payload[key] = null;
        } else if (['montant', 'insuffisance'].includes(key) && payload[key] !== null) {
          payload[key] = Number(payload[key]);
        }
      }); // <-- CORRECTION ICI : Fermeture correcte du forEach
      
      // Nettoyage strict pour l'Enum TypeIdentifiantPM
      if (payload.typeIdentifiantPM && !['RC', 'IF'].includes(payload.typeIdentifiantPM)) {
          payload.typeIdentifiantPM = null; 
      }

      await onSave(lcn.refImpaye, payload);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4">
      <div className="bg-white rounded-lg shadow-xl w-full max-w-2xl max-h-[90vh] flex flex-col">
        <div className="flex justify-between items-center p-6 border-b border-gray-200">
          <h2 className="text-xl font-bold text-gray-800">
            Modifier la saisie manuelle : {lcn.refImpaye}
          </h2>
          <button onClick={onClose} className="text-gray-400 hover:text-gray-600 transition-colors">
            <X className="w-6 h-6" />
          </button>
        </div>

        <div className="p-6 overflow-y-auto flex-1">
          <form id="edit-lcn-form" onSubmit={handleSubmit} className="grid grid-cols-1 md:grid-cols-2 gap-4">
            
            <div className="col-span-2">
              <h3 className="text-sm font-semibold text-gray-700 border-b pb-2 mb-2">Informations LCN</h3>
            </div>
            
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Montant</label>
              <input
                type="number"
                name="montant"
                step="0.01"
                required
                value={formData.montant}
                onChange={handleChange}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-1 focus:ring-primary focus:border-primary text-sm"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Devise</label>
              <select
                name="devise"
                required
                value={formData.devise}
                onChange={handleChange}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-1 focus:ring-primary focus:border-primary text-sm"
              >
                <option value="MAD">MAD</option>
                <option value="EUR">EUR</option>
                <option value="USD">USD</option>
              </select>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Statut</label>
              <select
                name="statut"
                required
                value={formData.statut}
                onChange={handleChange}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-1 focus:ring-primary focus:border-primary text-sm"
              >
                <option value="IMPAYÉ">Impayé</option>
                <option value="RÉGULARISÉ">Régularisé</option>
                <option value="ANNULÉ">Annulé</option>
              </select>
            </div>
            
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Date d'émission</label>
              <input
                type="date"
                name="dateEmission"
                required
                value={formData.dateEmission}
                onChange={handleChange}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-1 focus:ring-primary focus:border-primary text-sm"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Date d'échéance</label>
              <input
                type="date"
                name="dateEcheance"
                required
                value={formData.dateEcheance}
                onChange={handleChange}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-1 focus:ring-primary focus:border-primary text-sm"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Date de Constat</label>
              <input
                type="date"
                name="dateConstat"
                required
                value={formData.dateConstat}
                onChange={handleChange}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-1 focus:ring-primary focus:border-primary text-sm"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Insuffisance</label>
              <input
                type="number"
                name="insuffisance"
                step="0.01"
                required
                value={formData.insuffisance}
                onChange={handleChange}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-1 focus:ring-primary focus:border-primary text-sm"
              />
            </div>

            <div className="col-span-2">
              <label className="block text-sm font-medium text-gray-700 mb-1">RIB Associé</label>
              <input
                type="text"
                name="rib"
                required
                value={formData.rib}
                onChange={handleChange}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-1 focus:ring-primary focus:border-primary text-sm"
              />
            </div>

          </form>
        </div>

        <div className="p-6 border-t border-gray-200 flex justify-end gap-3 bg-gray-50 rounded-b-lg">
          <button
            type="button"
            onClick={onClose}
            className="px-4 py-2 border border-gray-300 text-gray-700 rounded-md hover:bg-gray-100 transition-colors text-sm font-medium"
            disabled={isSubmitting}
          >
            Annuler
          </button>
          <button
            type="submit"
            form="edit-lcn-form"
            disabled={isSubmitting}
            className="px-4 py-2 bg-primary text-white rounded-md hover:bg-primary-hover transition-colors text-sm font-medium flex items-center disabled:opacity-70"
          >
            {isSubmitting ? (
              <>
                <Loader2 className="w-4 h-4 mr-2 animate-spin" />
                Sauvegarde...
              </>
            ) : (
              'Sauvegarder'
            )}
          </button>
        </div>
      </div>
    </div>
  );
};

export default EditLcnModal;
