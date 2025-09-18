package model;

public enum EstadoCivil {
    SOLTERO,
    CASADO,
    VIUDO,
    UNION_LIBRE,
    DIVORCIADO;

    public static EstadoCivil fromOrdinal(int ordinal) {
        if (ordinal < 0 || ordinal >= values().length) {
            throw new IllegalArgumentException("Ordinal de EstadoCivil inválido: " + ordinal);
        }
        return values()[ordinal];
    }
}
