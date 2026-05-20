import React from 'react';

interface LogoProps {
  className?: string;
  size?: 'sm' | 'md' | 'lg';
}

const Logo: React.FC<LogoProps> = ({ className = '', size = 'md' }) => {
  const sizeClasses = {
    sm: 'w-10 h-10',
    md: 'w-14 h-14',
    lg: 'w-24 h-24'
  };

  return (
    <div className={`bg-white rounded-full overflow-hidden shadow-md border border-gray-100 flex items-center justify-center shrink-0 ${sizeClasses[size]} ${className}`}>
      <img 
        src="/logo.jpg" 
        alt="Logo CFG Bank" 
        className="w-full h-full object-cover"
      />
    </div>
  );
};

export default Logo;
