package obed.org.apirest.model;

public enum WearType {
    fn("Factory New"),
    mw("Minimal Wear"),
    ft("Field Tested"),
    ww("Well Worn"),
    bs("Battle Scarred"),
    none("None");
    private final String wearType;
    WearType(String wearType) {
        this.wearType = wearType;
    }
    public String getWearType() {
        return wearType;
    }
}