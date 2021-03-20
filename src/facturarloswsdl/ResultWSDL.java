package facturarloswsdl;

public class ResultWSDL {
    private int    CodigoResp;
    private String TextoResp;
    
    private String FechaEmision;
    private String FechaCertificacion;
    private String NumeroAutorizacion;
    private String Serie;
    private String Numero;
     
    private String FechaAnulacion;
    
    
    private String XmlString64;
    private String PdfString64;
    
    private String XmlString;
    private String PdfString;
    
    private String PathXmlFile;
    private String PathPdfFile;

    protected String FullResponse;
    
    //private arr[] Errores;
                    
    public ResultWSDL(int aCodigoResp, String aTextoResp) {
        this.CodigoResp = aCodigoResp;
        this.TextoResp = aTextoResp;
    }
    public int getCodigoResp() {
        return CodigoResp;
    }
    public void setCodigoResp(int value) {
        this.CodigoResp = value;
    }
    
    
    public String getTextoResp() {
        return TextoResp;
    }
    public void setTextoResp(String value) {
        this.TextoResp = value;
    }
    
    
    public String getFechaEmision() {
        return FechaEmision;
    }
    public void setFechaEmision(String value) {
        this.FechaEmision = value;
    }    
    
    
     public String getFechaCertificacion() {
        return FechaCertificacion;
    }
    public void setFechaCertificacion(String value) {
        this.FechaCertificacion = value;
    }    
       
    
    public String getNumeroAutorizacion() {
        return NumeroAutorizacion;
    }
    public void setNumeroAutorizacion(String value) {
        this.NumeroAutorizacion = value;
    }
    
    public String getFechaAnulacion() {
        return FechaAnulacion;
    }
    public void setFechaAnulacion(String value) {
        this.FechaAnulacion = value;
    }       
    
    
    public String getSerie() {
        return Serie;
    }
    public void setSerie(String value) {
        this.Serie = value;
    }  
    
    
    public String getNumero() {
        return Numero;
    }
    public void setNumero(String value) {
        this.Numero = value;
    } 
    
    
    public String getXmlString64() {
        return XmlString64;
    }
    void setXmlString64(String value) {
        this.XmlString64 = value;
    }    
    
     
    
    public String getXmlString() {
        return XmlString;
    }
    void setXmlString(String value) {
        this.XmlString = value;
    }       
    public String getPdf64() {
        return PdfString64;
    }
    public void setPdf64(String value) {
        this.PdfString64 = value;
    }  
    
    
    public String getPathXmlFile() {
        return PathXmlFile;
    }
    public void setPathXmlFile(String value) {
        this.PathXmlFile = value;
    }    
    
    
    public String getPathPdfFile() {
        return PathPdfFile;
    }
    public void setPathPdfFile(String value) {
        this.PathPdfFile = value;
    }   

    


    
}