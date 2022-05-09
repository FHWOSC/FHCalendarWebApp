package de.yonux.fhcalendarwebapp.task;

public enum RoomFormat {
    /**
     * name not included
     */
    NONE,

    /**
     * abbreviation only
     */
    ABBR,

    /**
     * full room name only
     */
    FULL_NAME,

    /**
     * &lt;room name&gt; (&lt;abbreviation&gt;)
     */
    FULL_NAME_AND_ABBR
}
