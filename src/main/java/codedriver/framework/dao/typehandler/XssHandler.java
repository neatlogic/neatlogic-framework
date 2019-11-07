package codedriver.framework.dao.typehandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class XssHandler implements TypeHandler<String> {

	@Override
	public void setParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
		String v = "";
		if (StringUtils.isNotBlank(parameter)) {
			try {
				JSONObject j = JSONObject.parseObject(parameter);
				encodeHtml(j);
				v = j.toString();
			} catch (Exception ex) {
				try {
					JSONArray j = JSONArray.parseArray(parameter);
					encodeHtml(j);
					v = j.toString();
				} catch (Exception ex2) {
					v = encodeHtml(parameter);
				}
			}
		}
		ps.setString(i, v);
	}

	private static String encodeHtml(String str) {
		if (StringUtils.isNotBlank(str)) {
			// str = str.replace("&", "&amp;");
			str = str.replace("<", "&lt;");
			str = str.replace(">", "&gt;");
			str = str.replace("'", "&#39;");
			str = str.replace("\"", "&quot;");
			return str;
		}
		return "";
	}

	public static void main(String[] aa) {
		JSONObject a = new JSONObject();
		JSONObject b = new JSONObject();
		JSONArray c = new JSONArray();
		JSONObject d = new JSONObject();

		b.put("c", "<a>c</a>");
		a.put("a", "<a>as</a>");
		a.put("b", b);
		d.put("d", "<a>d</a>");
		c.add("<a>d</a>");
		c.add(d);
		a.put("c", c);
		encodeHtml(a);
		System.out.println(a);
	}

	private static void encodeHtml(JSONObject j) {
		Set<String> set = j.keySet();
		Iterator<String> it = set.iterator();
		while (it.hasNext()) {
			String key = it.next().toString();
			String newKey = encodeHtml(key);
			if (!newKey.equals(key)) {
				Object value = j.get(key);
				j.remove(key);
				j.put(newKey, value);
			}
			if (j.get(newKey) instanceof JSONObject) {
				encodeHtml(j.getJSONObject(newKey));
			} else if (j.get(newKey) instanceof JSONArray) {
				encodeHtml(j.getJSONArray(newKey));
			} else if (j.get(newKey) instanceof String) {
				j.put(newKey, encodeHtml(j.getString(newKey)));
			}
		}
	}

	private static void encodeHtml(JSONArray j) {
		for (int i = 0; i < j.size(); i++) {
			if (j.get(i) instanceof JSONObject) {
				encodeHtml(j.getJSONObject(i));
			} else if (j.get(i) instanceof JSONArray) {
				encodeHtml(j.getJSONArray(i));
			} else if (j.get(i) instanceof String) {
				j.set(i, encodeHtml(j.getString(i)));
			}
		}
	}

	@Override
	public String getResult(ResultSet rs, String columnName) throws SQLException {
		String v = rs.getString(columnName);
		if (v != null) {
			try {
				JSONObject j = JSONObject.parseObject(v);
				encodeHtml(j);
				v = j.toString();
			} catch (Exception ex) {
				try {
					JSONArray j = JSONArray.parseArray(v);
					encodeHtml(j);
					v = j.toString();
				} catch (Exception ex2) {
					v = encodeHtml(v);
				}
			}
			return v;
		}
		return null;
	}

	@Override
	public String getResult(ResultSet rs, int columnIndex) throws SQLException {
		String v = rs.getString(columnIndex);
		if (v != null) {
			try {
				JSONObject j = JSONObject.parseObject(v);
				encodeHtml(j);
				v = j.toString();
			} catch (Exception ex) {
				try {
					JSONArray j = JSONArray.parseArray(v);
					encodeHtml(j);
					v = j.toString();
				} catch (Exception ex2) {
					v = encodeHtml(v);
				}
			}
			return v;
		}
		return null;
	}

	@Override
	public String getResult(CallableStatement cs, int columnIndex) throws SQLException {
		String v = cs.getString(columnIndex);
		if (v != null) {
			try {
				JSONObject j = JSONObject.parseObject(v);
				encodeHtml(j);
				v = j.toString();
			} catch (Exception ex) {
				try {
					JSONArray j = JSONArray.parseArray(v);
					encodeHtml(j);
					v = j.toString();
				} catch (Exception ex2) {
					v = encodeHtml(v);
				}
			}
			return v;
		}
		return null;
	}

}
