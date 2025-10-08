package de.jostnet.tpex;

public enum Sprache {
    DEUTSCH("de", "bearbeitet"),
    ENGLISCH("en", "edited"),
    FRANZOESISCH("fr", "modifiée"),
    SPANISCH("es", "editado"),
    ITALIENISCH("it", "modificata"),
    NIEDERLAENDISCH("nl", "bewerkt"),
    PORTUGIESISCH("pt", "editado"),
    POLNISCH("pl", "edytowane"),
    JAPANISCH("ja", "編集済み"),
    KOREANISCH("ko", "그룹한 수정"),
    TSCHECHISCH("cs", "upraveno"),
    UNGARISCH("hu", "szerkesztve"),
    SCHWEDISCH("sv", "redigerad"),
    DÄNISCH("da", "redigeret"),
    NORWEGISCH("no", "redigert"),
    FINNISCH("fi", "muokattu"),
    RUMÄNISCH("ro", "editat"),
    GRIECHISCH("el", "επεξεργασμένο"),
    THAILAENDISCH("th", "แก้ไขแล้ว"),
    VIETNAMESISCH("vi", "đã chỉnh sửa"),
    HINDI("hi", "संपादित"),
    ARABISCH("ar", "تم التعديل");

    private final String kuerzel;
    private final String text;

    Sprache(String kuerzel, String text) {
        this.kuerzel = kuerzel;
        this.text = text;
    }

    public String getKuerzel() {
        return kuerzel;
    }

    public String getText() {
        return text;
    }

}
