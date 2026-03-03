
package quiz.model;

public enum Option {
    A, B;

    public static Option fromCode(String code) {
        if (code == null) throw new IllegalArgumentException("Missing correct option code");
        code = code.trim().toLowerCase();
        return switch (code) {
            case "a" -> A;
            case "b" -> B;
            default -> throw new IllegalArgumentException("Invalid correct option code: " + code);
        };
    }

    public String toCode() {
        return this == A ? "a" : "b";
    }

    public Option flipped() {
        return this == A ? B : A;
    }
}
