package br.com.ctfpost.enums;

public enum Challenge {
    SqlInjection1(1),
    SqlInjection2(2),
    PrivilegeEscalation(3),
    Idor(4),
    CryptographicFailures(5);

    private final int value;

    Challenge(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
