package model;

public class Stop {

    private static int stopCounter = 0;
    private String id;
    private String code;
    private String name;

    public Stop(String id, String code, String name) {
        this.id = id;
        this.code = code;
        this.name = name;
        stopCounter++;
    }

    public String getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static int getCount(){
        return stopCounter;
    }

}
