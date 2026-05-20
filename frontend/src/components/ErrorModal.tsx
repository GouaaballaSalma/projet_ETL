import React from 'react';
import { ShieldAlert, X } from 'lucide-react';

interface ErrorModalProps {
  isOpen: boolean;
  onClose: () => void;
  message: string;
}

const ErrorModal: React.FC<ErrorModalProps> = ({ isOpen, onClose, message }) => {
  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-[60] flex items-center justify-center bg-black bg-opacity-50 backdrop-blur-sm">
      <div className="bg-white rounded-lg shadow-xl w-full max-w-sm p-6 transform transition-all border-t-4 border-red-600">
        <div className="flex justify-between items-start mb-2">
          <div className="flex items-center gap-3 text-red-600">
            <ShieldAlert className="h-6 w-6" />
            <h3 className="text-lg font-bold">Action refusée</h3>
          </div>
          <button onClick={onClose} className="text-gray-400 hover:text-gray-600 transition-colors">
            <X className="h-5 w-5" />
          </button>
        </div>
        
        <p className="text-gray-700 my-4 text-sm font-medium">
          {message}
        </p>
        
        <div className="flex justify-end mt-4">
          <button 
            onClick={onClose}
            className="px-5 py-2 bg-gray-100 text-gray-800 rounded-md hover:bg-gray-200 font-semibold transition-colors"
          >
            Fermer
          </button>
        </div>
      </div>
    </div>
  );
};

export default ErrorModal;
