package rubenchristoffer.hetty;

import java.io.IOException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;

import rubenchristoffer.hetty.HTTPRequest.RequestMethod;

public class HTTPNavigator {
	
	private HTTPConnection con;
	private HTTPRequest req;
	private HTTPResponse res;
	
	public HTTPNavigator (HTTPConnection con, HTTPRequest req) {
		this.con = con;
		this.req = req;
	}
	
	public boolean NavigateNext () throws UnknownHostException, IOException {
		return NavigateNext (true);
	}
	
	public boolean NavigateNext (boolean applyFilters) throws UnknownHostException, IOException {
		if (!con.IsConnectionOpen())
			con.OpenConnection();
		
		con.SendPacket(req, applyFilters);
		
		res = (HTTPResponse) con.ReadPacket();
		
		List<String> locationHeaders = res.GetHeaders("Location");
		
		if (res.GetHeaders("Set-Cookie").size() > 0) {
			req.ChangeOrAddHeader("Cookie", HTTPRequest.GenerateCookieHeader(res.ParseCookies()));
		}
		
		if (locationHeaders.size() > 0) {
			URL newURL = new URL(locationHeaders.get(0));
			
			if (con.IsConnectionOpen()) {
				// Close connection when Location URL is set to same as last
				if (!newURL.getHost().equals(con.GetURL().getHost()) || newURL.toString().equals(con.GetURL().toString())) {
					con.CloseConnection();
				}
			}
			
			req.SetRequestMethod(RequestMethod.GET);
			con.SetURL (newURL);
			
			return true;
		}
		
		return false;
	}
	
	public void NavigateTillEnd (boolean applyFilters) throws UnknownHostException, IOException {
		while (NavigateNext(applyFilters)) {}
	}
	
	public HTTPConnection GetConnection () {
		return con;
	}
	
	public HTTPRequest GetCurrentRequest () {
		return req;
	}
	
	public HTTPResponse GetLastResponse () {
		return res;
	}
	
}
