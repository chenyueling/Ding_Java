package cn.jpush.alertme.factory.plugins.oli;

import cn.jpush.alertme.factory.common.http.HttpRequestException;
import cn.jpush.alertme.factory.common.http.NativeHttpClient;
import cn.jpush.alertme.factory.util.JsonUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class OliConfigCache {
	private static final String url = "http://apis.juhe.cn/cnoil/oil_city?key=fa546aaf58000424700163ae7701ce4b&dtype=json";
	private static Map<String, OliPrice> cache = new HashMap<String, OliPrice>();
	public static String getPrice(String city, String oliType) {
		if(StringUtils.isEmpty(city) || StringUtils.isEmpty(oliType) ||cache==null||cache.isEmpty()){
			return null;
		}
		OliPrice prices = cache.get(city);
		String price = null;
		if(prices!=null){
			if("b90".equals(oliType)) {
				price = prices.getB90();
			} else if ("b93".equals(oliType)) {
				price = prices.getB93();
			} else if ("b97".equals(oliType)) {
				price = prices.getB97();
			} else if ("b0".equals(oliType)) {
				price = prices.getB0();
			} else {
				//pass
			}
		}
		return price;
	}
	public static void reload() {
		Map<String, OliPrice> newCache = fetch();
		if (newCache != null && !newCache.isEmpty()) {
			synchronized (OliConfigCache.class) {
				cache = newCache;
			}
		}

	}
	public static boolean init() {
		reload();
		return !cache.isEmpty();
	}
	public static void initIfNeed() {
		if (cache == null || cache.isEmpty()) {
			reload();
		}
	}

	public static Map<String, OliPrice> fetch() {
		Map<String, OliPrice> newCache = new HashMap<String, OliPrice>();
		try {
			String response = NativeHttpClient.get(url);
			JsonObject jsonObject = JsonUtil.format(response, JsonObject.class);
			String resultCode = jsonObject.get("resultcode").getAsString();
			if ("200".equals(resultCode)) {
				JsonArray jsonArray = jsonObject.getAsJsonArray("result");
				if (jsonArray != null && jsonArray.size() > 0) {
					for (int i = 0; i < jsonArray.size(); i++) {
						JsonObject oliPrice = jsonArray.get(i)
						        .getAsJsonObject();
						OliPrice op = OliPrice.toOliPrice(oliPrice);
						if (op != null) {
							newCache.put(op.getCity(), op);
						}
					}
				}
			}
		} catch (HttpRequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			// pass
		}
		return newCache;
	}

	public static class OliPrice {
		private String city;
		private String b90;
		private String b93;
		private String b97;
		private String b0;

		public String getCity() {
			return city;
		}

		public void setCity(String city) {
			this.city = city;
		}

		public String getB90() {
			return b90;
		}

		public void setB90(String b90) {
			this.b90 = b90;
		}

		public String getB93() {
			return b93;
		}

		public void setB93(String b93) {
			this.b93 = b93;
		}

		public String getB97() {
			return b97;
		}

		public void setB97(String b97) {
			this.b97 = b97;
		}

		public String getB0() {
			return b0;
		}

		public void setB0(String b0) {
			this.b0 = b0;
		}

		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("city=").append(city).append(",b90=").append(b90)
			        .append(",b93=").append(b93).append(",b97=").append(b97)
			        .append(",b0=").append(b0).append("\n");
			return sb.toString();
		}

		public static OliPrice toOliPrice(JsonObject jsonObject) {
			if (jsonObject == null) {
				return null;
			}
			String _city = null;
			String _b90 = null;
			String _b93 = null;
			String _b97 = null;
			String _b0 = null;
			try {
				_city = jsonObject.get("city").getAsString();
				_b90 = jsonObject.get("b90").getAsString();
				_b93 = jsonObject.get("b93").getAsString();
				_b97 = jsonObject.get("b97").getAsString();
				_b0 = jsonObject.get("b0").getAsString();
				if (!StringUtils.isEmpty(_city) && !StringUtils.isEmpty(_b90)
				        && !StringUtils.isEmpty(_b93)
				        && !StringUtils.isEmpty(_b97)
				        && !StringUtils.isEmpty(_b0)) {
					OliPrice oliPrice = new OliPrice();
					oliPrice.setB0(_b0);
					oliPrice.setB90(_b90);
					oliPrice.setB93(_b93);
					oliPrice.setB97(_b97);
					oliPrice.setCity(_city);
					return oliPrice;
				}
			} catch (Exception e) {
				// pass invalid json format
			}
			return null;
		}
	}
}
