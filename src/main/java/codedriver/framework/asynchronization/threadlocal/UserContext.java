package codedriver.framework.asynchronization.threadlocal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import codedriver.framework.exception.user.NoUserException;

public class UserContext implements Serializable {
	private static final long serialVersionUID = -578199115176786224L;
	private transient static ThreadLocal<UserContext> instance = new ThreadLocal<UserContext>();
	private transient HttpServletRequest request;
	private transient HttpServletResponse response;
	private String tenant;
	private String userName;
	private String userId;
	private String userUuid;
	private String timezone = "+8:00";
	private String token;
	private List<String> roleNameList = new ArrayList<>();

	public static UserContext init(UserContext _userContext) {
		UserContext context = new UserContext();
		if (_userContext != null) {
			context.setUserId(_userContext.getUserId());
			context.setUserUuid(_userContext.getUserUuid());
			context.setUserName(_userContext.getUserName());
			context.setTenant(_userContext.getTenant());
			context.setTimezone(_userContext.getTimezone());
			context.setToken(_userContext.getToken());
			// context.setRequest(_userContext.getRequest());
			// context.setResponse(_userContext.getResponse());
			context.setRoleNameList(_userContext.getRoleNameList());
		}
		instance.set(context);
		return context;
	}

	public static UserContext init(JSONObject jsonObj,String token, String timezone, HttpServletRequest request, HttpServletResponse response) {
		UserContext context = new UserContext();
		context.setUserId(jsonObj.getString("userid"));
		context.setUserUuid(jsonObj.getString("useruuid"));
		context.setUserName(jsonObj.getString("username"));
		context.setTenant(jsonObj.getString("tenant"));
		context.setRequest(request);
		context.setToken(token);
		context.setResponse(response);
		context.setTimezone(timezone);
		JSONArray roleList = jsonObj.getJSONArray("rolelist");
		if (roleList != null && roleList.size() > 0) {
			for (int i = 0; i < roleList.size(); i++) {
				context.addRole(roleList.getString(i));
			}
		}
		instance.set(context);
		return context;
	}

	public void addRole(String role) {
		if (roleNameList == null) {
			roleNameList = new ArrayList<>();
		}
		if (!roleNameList.contains(role)) {
			roleNameList.add(role);
		}
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	private UserContext() {

	}

	public static UserContext get() {
		return instance.get();
	}

	public void release() {
		instance.remove();
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserId() {
		return userId;
	}

	public String getUserId(boolean need) {
		if (StringUtils.isBlank(userId)) {
			throw new NoUserException();
		}
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserUuid() {
		return userUuid;
	}
	
	public String getUserUuid(boolean need) {
		if (StringUtils.isBlank(userUuid)) {
			throw new NoUserException();
		}
		return userUuid;
	}

	public void setUserUuid(String userUuid) {
		this.userUuid = userUuid;
	}

	public List<String> getRoleNameList() {
		return roleNameList;
	}

	public void setRoleNameList(List<String> roleNameList) {
		this.roleNameList = roleNameList;
	}

	public String getTenant() {
		return tenant;
	}

	public void setTenant(String tenant) {
		this.tenant = tenant;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
