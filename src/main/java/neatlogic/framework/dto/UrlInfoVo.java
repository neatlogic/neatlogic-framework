package neatlogic.framework.dto;

/**
 * @Title: UrlInfoVo
 * @Package neatlogic.framework.dto
 * @Description: Url信息类，保存url在content中的位置
 * @Author: linbq
 * @Date: 2021/3/15 12:04

 **/
public class UrlInfoVo {
    private int beginIndex;
    private int endIndex;
    private String source;
    private String target;

    public UrlInfoVo(int beginIndex, int endIndex, String source) {
        this.beginIndex = beginIndex;
        this.endIndex = endIndex;
        this.source = source;
    }

    public int getBeginIndex() {
        return beginIndex;
    }

    public void setBeginIndex(int beginIndex) {
        this.beginIndex = beginIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
//        if(StringUtils.isBlank(target) && StringUtils.isNotBlank(source)){
//            //source = /test/api/binary/image/download?id=314907690737664
//            //source = api/binary/image/download?id=314907690737664
//            System.out.println("source=" + source);
//            String[] split = source.split("\\?");
//            String path = split[0];
//            String queryString = split[1];
////            String tenant = path.substring(0, path.indexOf("/", 1));
//            String tenant = TenantContext.get().getTenantUuid();
//            int fromIndex = path.indexOf("api/");
//            int beginIndex = fromIndex;
//            fromIndex += 4;
//            int endIndex = path.indexOf("/", fromIndex) + 1;
//            String token = path.substring(endIndex);
//            String encryptedData = RC4Util.encrypt(token + "/" + tenant + "?" + queryString);
//            String homeUrl = Config.HOME_URL();
//            if(StringUtils.isNotBlank(homeUrl)) {
//                if (!homeUrl.endsWith("/")) {
//                    homeUrl += "/";
//                }
//            }
//            target = homeUrl + "anonymous/" + path.substring(beginIndex, endIndex) + encryptedData;
//        }
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

}
