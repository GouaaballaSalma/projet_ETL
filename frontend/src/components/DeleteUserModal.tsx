import React from 'react';
import { AlertTriangle, X } from 'lucide-react';

interface DeleteUserModalProps {
  isOpen: boolean;
  onClose: () => void;
  onConfirm: () => void;
  userName: string;
}

const DeleteUserModal: React.FC<DeleteUserModalProps> = ({ isOpen, onClose, onConfirm, userName }) => {
  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50 backdrop-blur-sm">
      <div className="bg-white rounded-lg shadow-xl w-full max-w-md p-6 transform transition-all">
        <div className="flex justify-between items-start mb-4">
          <div className="flex items-center gap-3 text-red-600">
            <AlertTriangle className="h-6 w-6" />
            <h3 className="text-lg font-bold">Confirmation de suppression</h3>
          </div>
          <button onClick={onClose} className="text-gray-400 hover:text-gray-600">
            <X className="h-5 w-5" />
          </button>
        </div>
        
        <p className="text-gray-600 my-4">
          Souhaitez-vous supprimer l'utilisateur <span className="font-semibold text-gray-800">{userName}</span> ?
        </p>
        
        <div className="flex justify-end gap-3 mt-6">
          <button 
            onClick={onClose}
            className="px-4 py-2 border border-gray-300 rounded-md text-gray-700 hover:bg-gray-50 font-medium"
          >
            Annuler
          </button>
          <button 
            onClick={onConfirm}
            className="px-4 py-2 bg-red-600 text-white rounded-md hover:bg-red-700 font-medium shadow-sm"
          >
            Supprimer
          </button>
        </div>
      </div>
    </div>
  );
};

export default DeleteUserModal;
