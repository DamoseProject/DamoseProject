package model;

import java.util.List;

public class Cache {

    private static Cache instance;
    private List<Route> routes;




    private Cache() {

    }

    public static Cache getInstance() {
        if(instance == null) {
            instance = new Cache();
        }
        return instance;
    }


}
