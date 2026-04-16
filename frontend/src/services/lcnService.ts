import api from './api';
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
    
    const response = await api.get<Page<LcnSynthDTO>>('/lcn/recherche', {
      params: cleanedParams,
    });
    return response.data;
  },

  creerIncidentManuel: async (data: any): Promise<LcnSynthDTO> => {
    const response = await api.post<LcnSynthDTO>('/lcn', data);
    return response.data;
  },
};
