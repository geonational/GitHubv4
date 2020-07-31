/*  Sample of a GraphQL query into Github v4 passing in variables
The query  will display the open issues as well as the assignees
Steven Moslin - 07JUL2020  - Initial

*/
package com.geonational.graphql1;
import org.json.JSONObject;
import org.json.*;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;


import java.io.*;
import java.nio.charset.*;
import org.apache.commons.io.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

public class GraphQlGitIssues {

    public static void main(String[] args) throws IOException {
		sendPost();
		System.out.println("POST DONE");
	}

 public static String readQuery() throws IOException {
     // Put the graphql query in a file for easier formatting
     // Use the Github v4 explorer tool to build the query
    File file = new File("src/main/resources/queryql.txt");
    return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
}
    
 
 public static String readVars() throws IOException {
  
// You may want to pass these in as parameters
String vOwner = "geonational";
String vRepos = "GitHubv4";
String vAssignee = "geonational";
String vLabel = "jenkinsdeployment";
     
     
     // The varibles file will have positional string markers           
     File file = new File("src/main/resources/varsql.txt"); 
    
     String theString =  String.format(FileUtils.readFileToString(file, StandardCharsets.UTF_8),'"'+vOwner+'"',
            '"'+vRepos+'"','"'+vAssignee+'"','"'+vLabel+'"');
    
     System.out.println(theString);
    return theString;
}
 
    
 
 
 
public static void sendPost() throws IOException {
    
				
        CloseableHttpClient client= HttpClients.createDefault();
        CloseableHttpResponse response= null;
      
        HttpPost httpPost = new HttpPost("https://api.github.com/graphql");

        
        // You need to create a login key within your Github profile
       httpPost.addHeader("Authorization","Bearer xxxxxxxxxxxxxxxxxxxxxxxxxxx");
        
        httpPost.addHeader("Accept","application/json");
		
	String  graphQuery  = new String ();	        
	graphQuery = readQuery();
        
        String  graphVariables = new String();	
	graphVariables = readVars();

        // Load the graphql query to send
        JSONObject jsonObj = new JSONObject();     
        jsonObj.put("query", graphQuery);
        jsonObj.put("variables", graphVariables);
        
        


        try {
            StringEntity entity= new StringEntity(jsonObj.toString());

            httpPost.setEntity(entity);
            response= client.execute(httpPost);
            
           

        }

        catch(UnsupportedEncodingException e){
            e.printStackTrace();
        }
        
        catch(IOException e){
            e.printStackTrace();
        }

        
         try {
        
            String jsonString;
            jsonString = EntityUtils.toString(response.getEntity());
           // This is all the returned data from Github
            JSONObject jo = new JSONObject(jsonString);
            
            // you could paste the jo object into a JSON display tool to see the format
            JSONArray ghIssues = jo.getJSONObject("data").getJSONObject("repository")
                    .getJSONObject("issues").getJSONArray("edges");
          
            // Need a for loop for go through the nodes 
            for (Object o : ghIssues) {
        JSONObject jsonLineItem = (JSONObject) o;
         JSONObject issue_node_access = jsonLineItem.getJSONObject("node");
         //System.out.println(issue_node_access);
         
         // Within the node we can access directly
         String iTitle = issue_node_access.getString("title");
        String iUrl = issue_node_access.getString("url");
        String iHTML = issue_node_access.getString("bodyHTML");
        System.out.println(iUrl);
        System.out.println(iTitle);
        System.out.println(iHTML);
            
        
        // You can have more than one assignee, so need to go through the nodes to get them all
        JSONArray assignees_node_traverse = issue_node_access.getJSONObject("assignees").getJSONArray("edges");
        //System.out.println(assignees_node_traverse);
            
                 for (Object o2 : assignees_node_traverse) {
            JSONObject jsonLineItem2 = (JSONObject) o2;
            JSONObject assignees_node_access = jsonLineItem2.getJSONObject("node");
            String aLogin = assignees_node_access.getString("login");
            System.out.println(aLogin);
            }
            
            
                  
           //Dump it all
            // System.out.println(ghIssues);           
                     
            }    
        }
        
        
                
        catch(Exception e){
            e.printStackTrace();
        }

    
}
}

    