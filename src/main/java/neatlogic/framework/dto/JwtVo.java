package neatlogic.framework.dto;

public class JwtVo {
    private String cc;
    private String jwthead;
    private String jwtbody;
    private String jwtsign;
    public String getCc() {
        return cc;
    }
    public void setCc(String cc) {
        this.cc = cc;
    }
    public String getJwthead() {
        return jwthead;
    }
    public void setJwthead(String jwthead) {
        this.jwthead = jwthead;
    }
    public String getJwtbody() {
        return jwtbody;
    }
    public void setJwtbody(String jwtbody) {
        this.jwtbody = jwtbody;
    }
    public String getJwtsign() {
        return jwtsign;
    }
    public void setJwtsign(String jwtsign) {
        this.jwtsign = jwtsign;
    }
    
}
