import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;
import org.apache.http.client.*;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.io.*;

public class AppServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static String ACCESS_TOKEN = "";

	public void GetAccessCode() throws IOException{
		String httpsURL = "https://accounts.google.com/o/oauth2/auth?scope=https://www.googleapis.com/auth/tasks&redirect_uri=http://localhost:8080/GoogleTaskManager/index.html&response_type=code&client_id=42850542154-0k9ai2ivso7m4n7kg4grlj5j3bl391na.apps.googleusercontent.com&approval_prompt=force";
		URI myurl = null;
		try {
			myurl = new URI(httpsURL);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		java.awt.Desktop.getDesktop().browse(myurl);  
	}

	public void GetAccessToken(String code) throws IOException{
		String httpsURL = "https://www.googleapis.com/oauth2/v4/token?code="+code+"&client_id=42850542154-0k9ai2ivso7m4n7kg4grlj5j3bl391na.apps.googleusercontent.com&client_secret=rHScyCy-grEVpT19HvZFUHji&grant_type=authorization_code&redirect_uri=http://localhost:8080/GoogleTaskManager/index.html";


		URL obj = new URL(httpsURL);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("POST");

		con.setDoOutput(true);
		OutputStream os = con.getOutputStream();
		os.flush();
		os.close();

		int responseCode = con.getResponseCode();

		if (responseCode == HttpURLConnection.HTTP_OK) { //success
			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			JSONObject json = null;
			try {
				json = (JSONObject)new JSONParser().parse(response.toString());
			} catch (ParseException e) {
				e.printStackTrace();
			}

			ACCESS_TOKEN = (String) json.get("access_token");
		}
	}

	public void GetTaskList(String code) throws IOException{

	}



	public String GetList(String code) throws IOException{

		if(ACCESS_TOKEN.equalsIgnoreCase(""))
		{
			GetAccessToken(code);			
		}
		String newplease = "https://www.googleapis.com/tasks/v1/users/@me/lists?access_token="+ACCESS_TOKEN;


		HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(newplease);

		HttpResponse response1 = client.execute(request);

		if(response1.getStatusLine().getStatusCode() == 200)
		{
			BufferedReader rd = new BufferedReader(
					new InputStreamReader(response1.getEntity().getContent()));

			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			JSONObject json1 = null;
			try {
				json1 = (JSONObject)new JSONParser().parse(result.toString());
				json1.put("at", ACCESS_TOKEN);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return json1.toString();

		}
		return "";        	

	}  

	public String GetAllTasks(String tlid , String at) throws IOException{

		String httpsURL = "https://www.googleapis.com/tasks/v1/lists/"+tlid+"/tasks?access_token="+at;
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(httpsURL);

		HttpResponse response1 = client.execute(request);

		if(response1.getStatusLine().getStatusCode() == 200)
		{
			BufferedReader rd = new BufferedReader(
					new InputStreamReader(response1.getEntity().getContent()));

			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			JSONObject json1 = null;
			try {
				json1 = (JSONObject)new JSONParser().parse(result.toString());
				json1.put("at", ACCESS_TOKEN);
			} catch (ParseException e) {
				e.printStackTrace();
			}

			return json1.toString();

		}
		return "";
	}  


	public String DeleteTask(String tlid , String at , String taskid) throws IOException{

		String httpsURL = "https://www.googleapis.com/tasks/v1/lists/"+tlid+"/tasks/"+taskid+"?access_token="+at;
		HttpClient client = HttpClientBuilder.create().build();
		HttpDelete request = new HttpDelete(httpsURL);

		HttpResponse response1 = client.execute(request);

		if(response1.getStatusLine().getStatusCode() == 200)
		{
			return "Success";           
		}
		return "Error";
	}  

	public String AddTask(String tlid , String at , String task) throws IOException{

		String httpsURL = "https://www.googleapis.com/tasks/v1/lists/"+tlid+"/tasks?access_token="+at;
		HttpClient client = HttpClientBuilder.create().build();
		HttpPost request = new HttpPost(httpsURL);


		String tasknew = "{title:\""+task+"\"}";
		request.setEntity(new StringEntity(tasknew, "UTF8"));
		request.setHeader("Content-type", "application/json");


		HttpResponse response1 = client.execute(request);
		if(response1.getStatusLine().getStatusCode() == 200)
		{
			return "Success";           
		}
		return "Error";
	} 

	public void deleteTasks(String listid , String [] tasks) throws IOException{


		for(int k=0;k<tasks.length;k++)
		{
			DeleteTask(listid , ACCESS_TOKEN, tasks[k]);
		}
	} 

	public void DeleteAllTasks(String tlid , String at) throws IOException{

		String newplease = "https://www.googleapis.com/tasks/v1/users/@me/lists?access_token="+ACCESS_TOKEN;

		HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(newplease);

		HttpResponse response1 = client.execute(request);

		if(response1.getStatusLine().getStatusCode() == 200)
		{
			BufferedReader rd = new BufferedReader(
					new InputStreamReader(response1.getEntity().getContent()));

			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}

			JSONObject json1 = null;
			String listid = "";
			String[] tasks ;
			try {
				json1 = (JSONObject)new JSONParser().parse(result.toString());
				JSONArray jsonArray = (JSONArray) json1.get("items");
				json1 = (JSONObject) jsonArray.get(0);
				listid = (String) json1.get("id");
			} catch (ParseException e) {
				e.printStackTrace();
			}

			String httpsURL = "https://www.googleapis.com/tasks/v1/lists/"+listid+"/tasks?access_token="+ACCESS_TOKEN;
			HttpClient client1 = HttpClientBuilder.create().build();
			HttpGet request1 = new HttpGet(httpsURL);

			HttpResponse response11 = client1.execute(request1);

			if(response11.getStatusLine().getStatusCode() == 200)
			{
				BufferedReader rd1 = new BufferedReader(
						new InputStreamReader(response11.getEntity().getContent()));

				StringBuffer result1 = new StringBuffer();
				String line1 = "";
				while ((line1 = rd1.readLine()) != null) {
					result1.append(line1);
				}
				try {						
					JSONObject jsonO = (JSONObject)new JSONParser().parse(result1.toString());
					JSONArray jsonArray = (JSONArray) jsonO.get("items");

					tasks = new String[jsonArray.size()];
					for(int k=0;k<jsonArray.size() ; k++)
					{
						JSONObject j = (JSONObject) jsonArray.get(k);
						tasks[k] = (String) j.get("id");
					}
					deleteTasks(listid, tasks);

				} catch (ParseException e) {
					e.printStackTrace();
				}

			}
		}
	} 

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {


		String func = req.getParameter("key");

		if(func.equalsIgnoreCase("access"))
		{
			GetAccessCode();
		}
		else if(func.equalsIgnoreCase("code"))
		{
			String code = req.getParameter("code");
			String result = GetList(code);

			resp.getWriter().write(result);
		}

		else if(func.equalsIgnoreCase("tasks"))
		{

			String method = req.getParameter("method");
			String tlid = req.getParameter("tasklistid");
			String at = req.getParameter("at");

			if(method.equalsIgnoreCase("GetAll"))
			{
				String tasks = GetAllTasks(tlid, at);
				resp.getWriter().write(tasks);
			}
			else if(method.equalsIgnoreCase("Delete"))
			{
				String taskid = req.getParameter("taskid");
				String status = DeleteTask(tlid, at ,taskid);
				resp.getWriter().write(status);
			}
			else if(method.equalsIgnoreCase("AddNew"))
			{
				String task = req.getParameter("task");
				String status = AddTask(tlid, at , task);
				resp.getWriter().write(status);
			}
			else if(method.equalsIgnoreCase("DeleteAll"))
			{
				DeleteAllTasks(tlid, at);
			}

		}





	}
}
