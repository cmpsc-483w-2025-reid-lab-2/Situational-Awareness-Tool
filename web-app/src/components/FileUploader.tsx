
import { useState, useRef } from "react";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { UploadCloud } from "lucide-react";
import { toast } from "sonner";

interface FileUploaderProps {
  title: string;
  description: string;
  icon: React.ReactNode;
  onFileUpload: (file: File) => void;
}

const FileUploader = ({ title, description, icon, onFileUpload }: FileUploaderProps) => {
  const [fileName, setFileName] = useState<string | null>(null);
  const [isDragging, setIsDragging] = useState(false);
  const fileInputRef = useRef<HTMLInputElement>(null);

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      validateAndProcessFile(file);
    }
  };

  const validateAndProcessFile = (file: File) => {
    // Check file extension
    if (!file.name.endsWith('.csv')) {
      toast.error("Please upload a CSV file");
      return;
    }

    // Check file size (5MB limit)
    if (file.size > 5 * 1024 * 1024) {
      toast.error("File size exceeds 5MB limit");
      return;
    }

    setFileName(file.name);
    onFileUpload(file);
    toast.success(`Successfully uploaded ${file.name}`);
  };

  const handleDragOver = (e: React.DragEvent<HTMLDivElement>) => {
    e.preventDefault();
    setIsDragging(true);
  };

  const handleDragLeave = () => {
    setIsDragging(false);
  };

  const handleDrop = (e: React.DragEvent<HTMLDivElement>) => {
    e.preventDefault();
    setIsDragging(false);
    
    const file = e.dataTransfer.files?.[0];
    if (file) {
      validateAndProcessFile(file);
    }
  };

  const handleClick = () => {
    fileInputRef.current?.click();
  };

  return (
    <Card className="w-full shadow-md border-navy-100">
      <CardHeader className="bg-navy-50 rounded-t-lg">
        <CardTitle className="flex items-center text-navy-900">
          {icon}
          <span className="ml-2">{title}</span>
        </CardTitle>
      </CardHeader>
      <CardContent className="pt-6">
        <div
          className={`file-drop-area ${isDragging ? 'active' : ''}`}
          onDragOver={handleDragOver}
          onDragLeave={handleDragLeave}
          onDrop={handleDrop}
          onClick={handleClick}
        >
          <input
            type="file"
            ref={fileInputRef}
            onChange={handleFileChange}
            accept=".csv"
            className="hidden"
          />
          <UploadCloud className="mx-auto h-12 w-12 text-navy-400" />
          <p className="mt-2 text-navy-900 font-medium">{fileName || "Drop your CSV file here"}</p>
          <p className="text-sm text-navy-600 mt-1">{description}</p>
          <Button variant="outline" className="mt-4 border-navy-300 text-navy-800">
            Select File
          </Button>
        </div>
      </CardContent>
    </Card>
  );
};

export default FileUploader;
