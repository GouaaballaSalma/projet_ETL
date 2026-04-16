export const TypeIdentifiantPP = {
  CIN: 'CIN',
  PASSEPORT: 'PASSEPORT',
  SEJOUR: 'SEJOUR',
} as const;
export type TypeIdentifiantPP = typeof TypeIdentifiantPP[keyof typeof TypeIdentifiantPP];

export const TypeIdentifiantPM = {
  RC: 'RC',
  IF: 'IF',
} as const;
export type TypeIdentifiantPM = typeof TypeIdentifiantPM[keyof typeof TypeIdentifiantPM];

export const TypeClient = {
  PP: 'PP',
  PM: 'PM',
} as const;
export type TypeClient = typeof TypeClient[keyof typeof TypeClient];

export interface LcnSynthDTO {
  refImpaye: string;
  lot: string;
  refClient: string;
  typeClient: string;
  nom: string;
  prenom: string;
  typeIdentifiant: string;
  identifiantPrincipal: string;
  dateNaissance: string;
  raisonSociale: string;
  rc: string;
  identifiantFiscal: string;
  codeBanque: string;
  numLcn: string;
  montant: number;
  devise: string;
  dateEmission: string;
  dateEcheance: string;
  dateConstat: string;
  insuffisance: number;
  codeStatut: string;
  statut: string;
  dateStatut: string;
  rib: string;
  dateArrete: string;
  dateCharge: string;
}

export interface CreateLcnSynthRequest {
  refClient: string;
  typeClient: TypeClient;
  nom?: string;
  prenom?: string;
  typeIdentifiant?: TypeIdentifiantPP | string;
  identifiantPrincipal?: string;
  dateNaissance?: string;
  raisonSociale?: string;
  typeIdentifiantPM?: TypeIdentifiantPM | string;
  rc?: string;
  identifiantFiscal?: string;
  codeBanque: string;
  numLcn: string;
  montant: number;
  devise: string;
  dateEmission: string;
  dateEcheance: string;
  dateConstat: string;
  insuffisance: number;
  rib: string;
}

export interface Page<T> {
  content: T[];
  pageable: {
    pageNumber: number;
    pageSize: number;
    sort: {
      empty: boolean;
      sorted: boolean;
      unsorted: boolean;
    };
    offset: number;
    unpaged: boolean;
    paged: boolean;
  };
  last: boolean;
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
  sort: {
    empty: boolean;
    sorted: boolean;
    unsorted: boolean;
  };
  first: boolean;
  numberOfElements: number;
  empty: boolean;
}
