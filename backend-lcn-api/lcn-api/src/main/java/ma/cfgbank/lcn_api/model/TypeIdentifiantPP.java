package ma.cfgbank.lcn_api.model;

public enum TypeIdentifiantPP {
    CIN("I"),
    PASSEPORT("P"),
    SEJOUR("S");

    private final String code;

    TypeIdentifiantPP(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
