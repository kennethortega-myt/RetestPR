package pe.gob.onpe.pradminbackend.security.utils;

import com.google.gson.GsonBuilder;

import lombok.extern.slf4j.Slf4j;
import pe.gob.onpe.pradminbackend.security.jwt.JwtAuthentication;
import pe.gob.onpe.pradminbackend.security.jwt.JwtConstant;
import jakarta.servlet.http.HttpServletRequest;

@Slf4j
public class Util {


    public JwtAuthentication infoPC(HttpServletRequest request) {
            JwtAuthentication jwt = new JwtAuthentication();

            String ipAddress = request.getHeader("X-FORWARDED-FOR");
            if (ipAddress == null) {
                ipAddress = request.getRemoteAddr();
            }

            validIpAddress(ipAddress, jwt);

            String userAgent = request.getHeader("User-Agent");
            String browser = "";
            jwt.setAgente(userAgent);

            String user = userAgent.toLowerCase();


            if (user.contains("msie")) {
                String substring = userAgent.substring(userAgent.indexOf("MSIE")).split(";")[0];
                browser = substring.split(" ")[0].replace("MSIE", "IE") + "-" + substring.split(" ")[1];
            } else if (user.contains("safari") && user.contains("version")) {
                browser = (userAgent.substring(userAgent.indexOf("Safari")).split(" ")[0]).split("/")[0] + "-" + (userAgent.substring(userAgent.indexOf("Version")).split(" ")[0]).split("/")[1];
            } else if (user.contains("opr") || user.contains("opera")) {
                if (user.contains("opera")) {
                    browser = (userAgent.substring(userAgent.indexOf("Opera")).split(" ")[0]).split("/")[0] + "-" + (userAgent.substring(userAgent.indexOf("Version")).split(" ")[0]).split("/")[1];
                } else if (user.contains("opr")) {
                    browser = ((userAgent.substring(userAgent.indexOf("OPR")).split(" ")[0]).replace("/", "-")).replace("OPR", "Opera");
                }
            } else if (user.contains("chrome")) {
                browser = (userAgent.substring(userAgent.indexOf("Chrome")).split(" ")[0]).replace("/", "-");
            } else if ((user.indexOf("mozilla/7.0") > -1) || (user.indexOf("netscape6") != -1) || (user.indexOf("mozilla/4.7") != -1) || (user.indexOf("mozilla/4.78") != -1) || (user.indexOf("mozilla/4.08") != -1) || (user.indexOf("mozilla/3") != -1)) {
                browser = "Netscape-?";
            } else if (user.contains("firefox")) {
                browser = (userAgent.substring(userAgent.indexOf("Firefox")).split(" ")[0]).replace("/", "-");
            } else if (user.contains("rv")) {
                browser = "IE-" + user.substring(user.indexOf("rv") + 3, user.indexOf(")"));
            } else {
                browser = "UnKnown";
            }

            jwt.setBrowser(browser);

            return jwt;

    }

    private static void validIpAddress(String ipAddress, JwtAuthentication jwt) {
        if (ipAddress != null) {

            jwt.setIp("127.0.0.0");
            jwt.setMac("onpe.gob.pe");
        }
    }

    public String encryJson(Object object) {
        try {
            CryptoUtils c = CryptoUtils.getInstance(JwtConstant.KEY_VALIDATOR);
            return c.encrypt(new GsonBuilder().setDateFormat("yyyy-MM-dd hh:mm:ss").create().toJson(object));
        } catch (Exception e) {
            log.error("ERROR en encryJson: ", e);
            return "";
        }
    }

    public JwtAuthentication dencryString(String validator) {
        try {
            CryptoUtils c = CryptoUtils.getInstance(JwtConstant.KEY_VALIDATOR);
            return  new GsonBuilder().setDateFormat("yyyy-MM-dd hh:mm:ss").create().fromJson(c.decrypt(validator), JwtAuthentication.class);
        } catch (Exception e) {
            log.error("ERROR en dencryString: ", e);
            return null;
        }
    }


}
