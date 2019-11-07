package codedriver.framework.threadlocal;

import java.io.Serializable;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RequestContext implements Serializable {
	/**
	 * @Fields serialVersionUID : TODO
	 */
	private static final long serialVersionUID = 7300783475150278920L;
	private static ThreadLocal<RequestContext> instance = new ThreadLocal<RequestContext>();
	private transient HttpServletRequest request;
	private transient HttpServletResponse response;
	private Cookie[] cookies;
	private String currentUser;
	private String host;
	private Integer port;
	private String scheme;
	private Boolean isXssFiltered = false;

	private RequestContext(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
		this.cookies = request.getCookies();
		if (request.getUserPrincipal() != null) {
			this.currentUser = request.getUserPrincipal().getName();
		}
		this.host = request.getServerName();
		this.port = request.getServerPort();
		this.scheme = request.getScheme();
	}

	private RequestContext() {

	}


	public void setCurrentUser(String _currentUser) {
		currentUser = _currentUser;
	}

	public String getHost() {
		return host;
	}


	public Integer getPort() {
		return port;
	}

	public String getScheme() {
		return scheme;
	}

	private RequestContext(String currentUser) {
		this.currentUser = currentUser;
	}

	public Boolean getIsXssFiltered() {
		return isXssFiltered;
	}

	public void setIsXssFiltered(Boolean isXssFiltered) {
		this.isXssFiltered = isXssFiltered;
	}

	public static RequestContext get() {
		return instance.get();
	}

	public static RequestContext init(HttpServletRequest request, HttpServletResponse response) {
		RequestContext context = null;
		if (request != null) {
			context = new RequestContext(request, response);
		} else {
			context = new RequestContext("SYSTEM");
		}
		instance.set(context);
		return context;
	}

	public static RequestContext init(String currentUser) {
		RequestContext context = new RequestContext(currentUser);
		instance.set(context);
		return context;
	}

	public static RequestContext init() {
		RequestContext context = new RequestContext();
		instance.set(context);
		return context;
	}

	public static RequestContext init(RequestContext _context) {
		if (_context != null) {
			RequestContext context = new RequestContext(_context.getCurrentUser());
			Cookie[] _cookies = _context.getCookies();
			if (_cookies != null) {
				Cookie[] newcookies = _cookies.clone();
				context.cookies = newcookies;
				context.host = _context.getHost();
				context.port = _context.getPort();
				context.scheme = _context.getScheme();
			}
			instance.set(context);
			return context;
		} else {
			return null;
		}

	}

	public void release() {
		instance.remove();
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public Cookie[] getCookies() {
		return cookies;
	}

	public String getCurrentUser() {
		return currentUser;
	}
}
