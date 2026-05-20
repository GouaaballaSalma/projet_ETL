import api from '../api/axiosConfig';
import type { Page, LcnSynthDTO, TypeClient, TypeIdentifiantPP, TypeIdentifiantPM } from '../types/LcnSynth';

export interface SearchLcnParams {
  typeClient: TypeClient;
  identifiant?: string;
  nomComplet?: string;
  typeIdentifiantPM?: TypeIdentifiantPM | string;
  typeIdentifiantPP?: TypeIdentifiantPP | string;
  page?: number;
  size?: number;
}

export const lcnService = {
  rechercherIncidents: async (params: SearchLcnParams): Promise<Page<LcnSynthDTO>> => {
    // Clean up empty params to avoid sending empty strings
    const cleanedParams = Object.fromEntries(
      Object.entries(params).filter(([_, v]) => v !== undefined && v !== '')
    );
    
    const response = await api.get<Page<LcnSynthDTO>>('/api/lcn/recherche', {
      params: cleanedParams,
    });
    return response.data;
  },

  creerIncidentManuel: async (data: any): Promise<LcnSynthDTO> => {
    const response = await api.post<LcnSynthDTO>('/api/lcn', data);
    return response.data;
  },

  deleteLcn: async (refImpaye: string): Promise<void> => {
    await api.delete(`/api/lcn/${refImpaye}`);
  },

  updateLcn: async (refImpaye: string, data: any): Promise<LcnSynthDTO> => {
    const response = await api.put<LcnSynthDTO>(`/api/lcn/${refImpaye}`, data);
    return response.data;
  },
};
