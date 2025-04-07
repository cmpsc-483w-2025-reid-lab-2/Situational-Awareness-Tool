import { Heart, Shield } from "lucide-react";

const Navbar = () => {
  return (
    <nav className="bg-navy-900 text-white py-4 px-6 shadow-md">
      <div className="container mx-auto flex items-center justify-between">
        <div className="flex items-center space-x-2">
          <img
            src="/lovable-uploads/f880bba1-6d3b-4789-8237-cc9a760a3f99.png"
            alt="Pulse Precision Police Logo"
            className="h-10 w-10"
          />
          <span className="text-xl font-semibold">
            Situational Awareness Tool
          </span>
        </div>
        <div className="flex items-center space-x-4">
          <div className="flex items-center text-navy-100">
            <Heart className="h-5 w-5 mr-1" />
            <span>Heart Rate Analysis</span>
          </div>
          <div className="flex items-center text-navy-100">
            <Shield className="h-5 w-5 mr-1" />
            <span>MANTIS Integration</span>
          </div>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
