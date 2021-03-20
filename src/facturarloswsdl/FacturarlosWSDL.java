/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facturarloswsdl;

/**
 *
 * @author Apipiltzin
 */


import AnulacionProduccionPKG.AnulacionExecuteResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import static java.lang.Integer.parseInt;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public class FacturarlosWSDL {

    private static Boolean EsProduccion   = false;
    private static String Cliente   = "80000000114K";
    private static String Usuario   = "ADMIN";
    private static String Clave     = "123";
    private static String NitEmisor = "80000000114K";
    private static String XmldocInFilePath      = "";
    private static String NumAutorizacionUuid   = "";
    private static String MotivoAnulacion       = "";
    
    private static String FOutputXmlPdfPath     = "./OutputXmlPdf";
    
    
    /************************************************************************************************/
    /**
     * @return **********************************************************************************************/
    public ResultWSDL Execute_Firma(){
        
        FirmaProduccionPKG.DocumentoExecute parametersProd = null;
        FirmaDesarrolloPKG.DocumentoExecute parametersDes = null;
        if (EsProduccion) {
            
            parametersProd = new FirmaProduccionPKG.DocumentoExecute();  
            parametersProd.setCliente(Cliente);
            parametersProd.setUsuario(Usuario);
            parametersProd.setClave(Clave);
            parametersProd.setNitemisor(NitEmisor);
        } else {
            
            parametersDes = new FirmaDesarrolloPKG.DocumentoExecute();   
            parametersDes.setCliente(Cliente);
            parametersDes.setUsuario(Usuario);
            parametersDes.setClave(Clave);
            parametersDes.setNitemisor(NitEmisor);            
        }
            
        
        try {
            try {   
                


                String XmlInputString = getXmldocInStringFromFile( XmldocInFilePath);
                if (("".equals(XmlInputString))|| (XmlInputString == null)){
                   return new ResultWSDL(-1, "No se pudo cargar el archivo de entrada: " + XmldocInFilePath); 
                } else {
                    
                    
                    
                    if ( EsProduccion ) {
                        FirmaProduccionPKG.DocumentoExecuteResponse ResponseProd;
                        parametersProd.setXmldoc( XmlInputString );
                        ResponseProd = ExecuteFirmaProd(parametersProd);
                        return procesaRespuesta(ResponseProd.getRespuesta());  
                    } else {
                        FirmaDesarrolloPKG.DocumentoExecuteResponse ResponseDes;
                        parametersDes.setXmldoc( XmlInputString );
                        ResponseDes = ExecuteFirmaDes ( parametersDes );
                        return procesaRespuesta(ResponseDes.getRespuesta());  
                    }
                    
                    
                                      
                }
            }
            catch (XPathExpressionException e) {
                return new ResultWSDL(-1, e.getMessage());
            }   
        }
        finally {
             parametersProd = null;
             parametersDes = null;
        }
    }
    /************************************************************************************************/
    /**
     * @return **********************************************************************************************/
    public ResultWSDL Execute_Anulacion(){
        
        AnulacionProduccionPKG.AnulacionExecute parametersProd = null;
        AnulacionDesarrolloPKG.AnulacionExecute parametersDes = null;
        
        if (EsProduccion) {
            
            parametersProd = new AnulacionProduccionPKG.AnulacionExecute();  
            parametersProd.setCliente(Cliente);
            parametersProd.setUsuario(Usuario);
            parametersProd.setClave(Clave);
            parametersProd.setNitemisor(NitEmisor);
            
            parametersProd.setNumautorizacionuuid( NumAutorizacionUuid );
            parametersProd.setMotivoanulacion( MotivoAnulacion );
            
        } else {
            
            parametersDes = new AnulacionDesarrolloPKG.AnulacionExecute();   
            parametersDes.setCliente(Cliente);
            parametersDes.setUsuario(Usuario);
            parametersDes.setClave(Clave);
            parametersDes.setNitemisor(NitEmisor);    
            
            parametersDes.setNumautorizacionuuid( NumAutorizacionUuid );
            parametersDes.setMotivoanulacion( MotivoAnulacion );            
        }
        try {
            try {   
                if ( EsProduccion ) {
                    AnulacionProduccionPKG.AnulacionExecuteResponse ResponseProd;
                    ResponseProd = ExecuteAnulacionProd(parametersProd);
                    return procesaRespuesta(ResponseProd.getRespuesta());  
                } else {
                    AnulacionDesarrolloPKG.AnulacionExecuteResponse ResponseDes;
                    
                    ResponseDes = ExecuteAnulacionDes ( parametersDes );
                    return procesaRespuesta(ResponseDes.getRespuesta());  
                }
            }
            catch (XPathExpressionException e) {
                return new ResultWSDL(-1, e.getMessage());
            }   
        }
        finally {
             parametersProd = null;
             parametersDes = null;
        }
    }
    
    /************************************************************************************************/
    /************************************************************************************************/
    
    private static ResultWSDL procesaRespuesta ( String aResponse ) throws XPathExpressionException {
        
        ResultWSDL RespuestaMain = new ResultWSDL(-1, "NA");            
        RespuestaMain.FullResponse = aResponse;
      
        Document RespuestaXML = convertStringToXMLDocument( RespuestaMain.FullResponse );
        RespuestaXML.getDocumentElement().normalize();
        //RespuestaXML.getAttributes().getNamedItem("")
        
        NodeList Errores;
        Errores = RespuestaXML.getElementsByTagName("Error");
        
        // Revisa si hay error                  
        if (Errores.getLength() > 0 ) {
            
            for (int Index = 0; Index < Errores.getLength(); Index++)
            {
               Node NodoError = Errores.item(Index);
                if (NodoError.getNodeType() == Node.ELEMENT_NODE)
                {
                    if (NodoError.hasAttributes()) {
                        // get attributes names and values
                        NamedNodeMap Atributos = NodoError.getAttributes();
                        for (int i = 0; i < Atributos.getLength(); i++)
                        {
                            Node Atributo = Atributos.item(i);
                            if (("Codigo".equals(Atributo.getNodeName())) && (Index == 0) ) {
                                RespuestaMain.setTextoResp( NodoError.getTextContent() );
                                RespuestaMain.setCodigoResp( parseInt(Atributo.getNodeValue()) );
                            } else {
                                RespuestaMain.setTextoResp( RespuestaMain.getTextoResp() + " ("+"["+Atributo.getNodeValue()+"] "+NodoError.getTextContent() + "); " );
                            }
                        }
                    }
                }
            }
            
        } else
        {
            RespuestaMain.setCodigoResp(0);
            RespuestaMain.setTextoResp( "OK" );
        }
        if ( (RespuestaMain.getCodigoResp() == 0) || (RespuestaMain.getCodigoResp() == 2001) ){ 
            
            // Set propiedades de respuesta
            XPathFactory xpf = XPathFactory.newInstance();
            XPath xpath = xpf.newXPath();
            Element NodoDTE = (Element) xpath.evaluate("/DTE", RespuestaXML, XPathConstants.NODE);
            
            RespuestaMain.setFechaEmision(NodoDTE.getAttribute("FechaEmision"));
            RespuestaMain.setFechaCertificacion(NodoDTE.getAttribute("FechaCertificacion"));
            RespuestaMain.setNumeroAutorizacion(NodoDTE.getAttribute("NumeroAutorizacion"));
            RespuestaMain.setNumero(NodoDTE.getAttribute("Numero"));
            RespuestaMain.setSerie(NodoDTE.getAttribute("Serie"));
           
            RespuestaMain.setFechaAnulacion(NodoDTE.getAttribute("FechaAnulacion"));
             
            File OutputXmlPDFPath = new File(FOutputXmlPdfPath);
            if (!OutputXmlPDFPath.exists()) {
                try {
                  OutputXmlPDFPath.mkdirs();  
                } catch (Exception e) {
                    RespuestaMain.setPathXmlFile("No se pudo crear el directio: " + FOutputXmlPdfPath +" ( Los archivos XML y PDF no fueron creados. ) " + e.getMessage()  );
                }                    
             }            
        
            /* Si la Factura ha sido FIRMADA con 
                exito o si ya ha sido firmada anteriormente,
                el PAC envia los archivo de la factura en XML y PDF */

            //********** XML ***********************************************************************/
            String XmlFileName = "";
            String PdfFileName = "";
            if ( !"".equals(RespuestaMain.getNumeroAutorizacion())){
                XmlFileName = FOutputXmlPdfPath+
                                    RespuestaMain.getNumeroAutorizacion()+"_"+
                                    RespuestaMain.getNumero()+"_"+
                                    RespuestaMain.getSerie()+".xml";
                PdfFileName = FOutputXmlPdfPath+
                                RespuestaMain.getNumeroAutorizacion()+"_"+
                                RespuestaMain.getNumero()+"_"+
                                RespuestaMain.getSerie()+".pdf";  
            
            } else {
                
                XmlFileName = FOutputXmlPdfPath+

                                    NumAutorizacionUuid+"_"+
                                    "Anulado"+".xml";
                PdfFileName = FOutputXmlPdfPath+
                                NumAutorizacionUuid+"_"+
                                    "Anulado"+".pdf";                 
                
            }
                

            
            
            
            // Decodifica XML de Base64 a String
            String NodoXML64 = RespuestaXML.getElementsByTagName("Xml").item(0).getTextContent();
            String NodoXMLString = new String(Base64.getDecoder().decode(NodoXML64), StandardCharsets.UTF_8);
            try {
                RespuestaMain.setPathXmlFile(XmlFileName);
                try (FileWriter XmlFile = new FileWriter(XmlFileName)) {
                    XmlFile.write(NodoXMLString);
                    XmlFile.close();
                }

            } catch (IOException e) {
                RespuestaMain.setCodigoResp(-1);
                RespuestaMain.setTextoResp("Error al guardar archivo XML: " + e.getMessage());
                
            }
            RespuestaMain.setXmlString64(NodoXML64);


            //********** PDF ***********************************************************************/
                   
            // Decodifica PDF de Base64 a String
            String NodoPDF64 = RespuestaXML.getElementsByTagName("Pdf").item(0).getTextContent();
           
            try {
                RespuestaMain.setPathPdfFile(PdfFileName);
                File PdfFile = new File(PdfFileName);
                try (FileOutputStream PdfFileOut = new FileOutputStream(PdfFile)) {
                    byte[] PdfFileByte = Base64.getDecoder().decode(NodoPDF64);
                    PdfFileOut.write(PdfFileByte);
                    
                }
            } catch (IOException e) {
                RespuestaMain.setCodigoResp(-1);
                RespuestaMain.setTextoResp("Error al guardar archivo PDF: " + e.getMessage());

            }
        }
            
        return RespuestaMain;
    }
    /************************************************************************************************/
    /************************************************************************************************/
    private static String getXmldocInStringFromFile(String aXmldocInFilePath){
        try {
            File archivo = new File(aXmldocInFilePath);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = dbf.newDocumentBuilder();
            Document document = documentBuilder.parse(archivo);
            document.getDocumentElement().normalize();
            Node nodeMain = document.getDocumentElement();
            String Result = node2String(nodeMain);
            
            return Result;
        }
        catch (IOException | ParserConfigurationException | TransformerException | SAXException e) {
            //return "";
        }    
        return "";
    }
    /************************************************************************************************/
    /************************************************************************************************/
    static String node2String(Node node) throws TransformerFactoryConfigurationError, TransformerException {
        StreamResult xmlOutput = new StreamResult(new StringWriter());
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.transform(new DOMSource(node), xmlOutput);
        return xmlOutput.getWriter().toString();
    }
    /************************************************************************************************/
    /************************************************************************************************/
    
    private static Document convertStringToXMLDocument(String xmlString) 
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();         
        DocumentBuilder builder = null;
        try
        {
            builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xmlString)));
            return doc;
        } 
        catch (IOException | ParserConfigurationException | SAXException e) 
        {
            e.printStackTrace();
        }
        return null;
    }
    
    /************************************************************************************************/
    /************************************************************************************************/
    void setOutputXmlPDFPath(String value){
        FacturarlosWSDL.FOutputXmlPdfPath = value;
    }
    /************************************************************************************************/
    /************************************************************************************************/
    String getOutputXmlPDFPath(){
        return FacturarlosWSDL.FOutputXmlPdfPath;
    }
    /************************************************************************************************/
    /************************************************************************************************/
    void setCliente(String value){
        FacturarlosWSDL.Cliente = value;
    }
    /************************************************************************************************/
    /************************************************************************************************/
    String getCliente(){
        return FacturarlosWSDL.Cliente;
    }
    /************************************************************************************************/
    /************************************************************************************************/
    void setUsuario(String value){
        FacturarlosWSDL.Usuario = value;
    }
    /************************************************************************************************/
    /************************************************************************************************/
    String getUsuario(){
        return FacturarlosWSDL.Usuario;
    }   
    /************************************************************************************************/
    /************************************************************************************************/
    void setClave(String value){
        FacturarlosWSDL.Clave = value;
    }
    /************************************************************************************************/
    /************************************************************************************************/
    String getClave(){
        return FacturarlosWSDL.Clave;
    }       
    /************************************************************************************************/
    /************************************************************************************************/
    void setNitEmisor(String value){
        FacturarlosWSDL.NitEmisor = value;
    }
    /************************************************************************************************/
    /************************************************************************************************/
    String getNitEmisor(){
        return FacturarlosWSDL.NitEmisor;
    }  
    /************************************************************************************************/
    /************************************************************************************************/
    void setXmldocInFilePath(String value){
        FacturarlosWSDL.XmldocInFilePath = value;
    }
    /************************************************************************************************/
    /************************************************************************************************/
    String getXmldocInFilePath(){
        return FacturarlosWSDL.XmldocInFilePath;
    }   
    /************************************************************************************************/
    /************************************************************************************************/
    void setNumAutorizacionUuid(String value){
        FacturarlosWSDL.NumAutorizacionUuid = value;
    }
    /************************************************************************************************/
    /************************************************************************************************/
    String getNumAutorizacionUuid(){
        return FacturarlosWSDL.NumAutorizacionUuid;
    }  
    /************************************************************************************************/
    /************************************************************************************************/
    void setMotivoAnulacion(String value){
        FacturarlosWSDL.MotivoAnulacion = value;
    }
    /************************************************************************************************/
    /************************************************************************************************/
    String getMotivoAnulacion(){
        return FacturarlosWSDL.MotivoAnulacion;
    }       
    /************************************************************************************************/
    /************************************************************************************************/
    void setEsProduccion(Boolean value){
        FacturarlosWSDL.EsProduccion = value;
    }
    /************************************************************************************************/
    /************************************************************************************************/
    Boolean getEsProduccion(){
        return FacturarlosWSDL.EsProduccion;
    }       

    private static FirmaProduccionPKG.DocumentoExecuteResponse ExecuteFirmaProd(FirmaProduccionPKG.DocumentoExecute parameters) {
        FirmaProduccionPKG.DucumentoProd service = new FirmaProduccionPKG.DucumentoProd();
        FirmaProduccionPKG.DocumentoSoapPortProd port = service.DocumentoSoapPortProd();
        return port.ExecuteFirmaProd(parameters);
    }

    private static FirmaDesarrolloPKG.DocumentoExecuteResponse ExecuteFirmaDes(FirmaDesarrolloPKG.DocumentoExecute parameters) {
        FirmaDesarrolloPKG.DocumentoDes service = new FirmaDesarrolloPKG.DocumentoDes();
        FirmaDesarrolloPKG.DocumentoSoapPort port = service.DocumentoSoapPortDes();
        return port.ExecuteFirmaDes(parameters);
    }

    private static AnulacionExecuteResponse ExecuteAnulacionProd(AnulacionProduccionPKG.AnulacionExecute parameters) {
        AnulacionProduccionPKG.AnulacionProd service = new AnulacionProduccionPKG.AnulacionProd();
        AnulacionProduccionPKG.AnulacionSoapPortProd port = service.AnulacionSoapPortProd();
        return port.ExecuteAnulacionProd(parameters);
    }

    private static AnulacionDesarrolloPKG.AnulacionExecuteResponse ExecuteAnulacionDes(AnulacionDesarrolloPKG.AnulacionExecute parameters) {
        AnulacionDesarrolloPKG.AnulacionDes service = new AnulacionDesarrolloPKG.AnulacionDes();
        AnulacionDesarrolloPKG.AnulacionSoapPortDes port = service.AnulacionSoapPortDes();
        return port.ExecuteAnulacionDes(parameters);
    }
    
    
    /************************************************************************************************/
    /************************************************************************************************/

    
    
}
