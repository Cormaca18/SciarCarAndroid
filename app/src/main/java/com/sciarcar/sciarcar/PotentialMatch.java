package com.sciarcar.sciarcar;

/**
 * Created by Dennis on 14/03/2018.
 */

public class PotentialMatch {

    String origin;
    String destination;
    String peopleCount;
    String tripId;
    boolean circle;
    boolean checked;

    public PotentialMatch(String origin, String dest, String pplCount, String tripId, boolean checked, boolean circle){

        this.origin = origin;
        this.destination = dest;
        this.peopleCount = pplCount;
        this.tripId = tripId;
        this.circle = circle;
        this.checked = checked;

    }

}
