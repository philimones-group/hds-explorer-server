package org.philimone.hds.explorer.server.openhds.http

import net.betainteractive.utilities.StringUtil
import org.apache.http.auth.AuthenticationException
import org.philimone.hds.explorer.server.openhds.xml.XmlModel

import java.nio.charset.StandardCharsets

/**
 * Created by paul on 8/29/16.
 */
class RestApiAccess {
    private String url = "";
    private String username = "";
    private String password = "";
    private HttpURLConnection connection;

    public RestApiAccess(String username, String password) {
        //this.url = !url.endsWith("/") ? url+"/" : url;
        this.username = username;
        this.password = password;
    }

    private HttpURLConnection createConnection(String urlString){


        String auth = username + ":" + password;
        byte[] encodedAuth = Base64.getEncoder().encode((username + ":" + password).getBytes(StandardCharsets.UTF_8))
        String authHeaderValue = "Basic " + new String(encodedAuth)


        URL url = new URL(urlString)
        this.connection = (HttpURLConnection) url.openConnection();


        this.connection.setConnectTimeout(20000);
        this.connection.setReadTimeout(20000);
        this.connection.setDoInput(true);
        this.connection.setDoOutput(true);
        this.connection.setUseCaches(true);
        this.connection.setRequestMethod("POST");
        this.connection.setRequestProperty("Accept", "application/xml");
        this.connection.setRequestProperty("Content-Type", "application/xml");
        this.connection.setRequestProperty("Authorization", authHeaderValue);

        this.connection.setRequestProperty("Connection", "Close");
        this.connection.setRequestProperty("http.keepAlive", "false");


        return connection;
    }

    public Response send(String serviceUrl, XmlModel model){

        HttpURLConnection connection = createConnection(serviceUrl)
        Response response = new Response();
        //response.status = "Error";

        try {

            // Write XML
            OutputStream output = connection.getOutputStream();
            output.write(model.getXml().getBytes());
            output.flush();
            output.close();


            //println "content ${connection.getHeaderField("Content-Type")}"
            //println("Response= " + connection.getResponseMessage() +", The response code is: " + connection.getResponseCode()+", type="+connection.getContentType()+", size="+connection.getContentLength());

            def responseText = ""
            def responseCode = connection.getResponseCode()
            def InputStream inputStream = null

            // Read XML
            //this.connection.setDoInput(true);
            if (responseCode >= HttpURLConnection.HTTP_CREATED && responseCode < HttpURLConnection.HTTP_BAD_REQUEST){
                inputStream = connection.getInputStream();
            } else {
                inputStream = connection.getErrorStream();
            }

            if (inputStream != null){
                byte[] res = new byte[2048];
                int i = 0;
                StringBuilder responseBuilder = new StringBuilder();
                while ((i = inputStream.read(res)) != -1) {
                    responseBuilder.append(new String(res, 0, i));
                }
                inputStream.close();

                //read output
                responseText = responseBuilder.toString();
            }

            //println("Response= " + responseString +", The response code is: " + connection.getResponseCode()+", type="+connection.getContentType()+", size="+connection.getContentLength());

            //Cleaning Output
            if (responseText != null){

                def toRemove1 = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><failure>"; //<errors>
                def toRemove2 = "</failure>"; //</errors>
                def toRemove3 = "<errors>"
                def toRemove4 = "</errors>"
                def errors_split_break = "</errors><errors>"

                def text = responseText
                responseText = ""

                text = StringUtil.remove(text, [toRemove1, toRemove2] );

                def spt = text.split(errors_split_break)

                spt.eachWithIndex { String line, int i ->
                    line = StringUtil.remove(line, [toRemove3, toRemove4])
                    responseText += "${line}${i<spt.length-1 ? "\n" : ""}"
                }

            }

            response.code = connection.getResponseCode();
            response.bodyText = responseText;

        } catch (UnsupportedEncodingException ex) {
            //System.out.println("\nEr1");
            ex.printStackTrace();
            response.errorOccurred = true;
            response.bodyText = ex.getMessage();
        } catch (IOException ex) {
            //System.out.println("\nEr2");
            ex.printStackTrace();
            response.errorOccurred = true;
            response.bodyText = ex.getMessage();
        } catch (AuthenticationException ex) {
            //System.out.println("\nEr3");
            ex.printStackTrace();
            response.errorOccurred = true;
            response.bodyText = ex.getMessage();
        }

        response.xmlContent = model.getXml()

        return response;
    }

    /**
     public void getVisits(){
     try {
     //visits

     HttpGet httpGet = new HttpGet(url+"visits");
     //processResponse();

     HttpResponse response = null;
     httpGet.addHeader(new BasicScheme().authenticate(credentials, httpGet, null));
     httpGet.addHeader("content-type", "text/xml");

     response = httpClient.execute(httpGet);

     HttpEntity entity = response.getEntity();
     InputStream ipsXml = entity.getContent();

     Scanner scan = new Scanner(ipsXml);


     while (scan.hasNext()){
     System.out.print("" + scan.next());
     }

     System.out.println();

     } catch (AuthenticationException ex) {
     ex.printStackTrace();
     } catch (IOException ex) {
     ex.printStackTrace();
     }

     }
     */

    public class Response {
        private boolean errorOccurred;
        private int code;
        private String bodyText;
        private String xmlContent;

        void setErrorOccurred(boolean errorOccurred) {
            this.errorOccurred = errorOccurred
        }

        public String getBodyText() {
            return bodyText;
        }

        public int getCode() {
            return code;
        }

        public boolean isStatus(int statusCode){
            return this.code==statusCode;
        }

        public boolean hasErrors(){
            return errorOccurred;
        }

        boolean getErrorOccurred() {
            return errorOccurred
        }

        @Override
        public String toString() {
            return code+"\n"+bodyText;
        }
    }
}
