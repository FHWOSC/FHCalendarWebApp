package de.yonux.fhcalendarwebapp.task;

public enum NameFormat {
    /**
     * name not included
     */
    NONE,

    /**
     * abbreviation only
     */
    ABBR,

    /**
     * &lt;first name&gt; &lt;last name&gt;
     */
    FIRST_LAST,

    /**
     * &lt;first name&gt; &lt;last name&gt; (&lt;abbreviation&gt;)
     */
    FIRST_LAST_ABBR,

    /**
     * &lt;last name&gt;, &lt;first name&gt;
     */
    LAST_FIRST,

    /**
     * &lt;last name&gt;, &lt;first name&gt; (&lt;abbreviation&gt;)
     */
    LAST_FIRST_ABBR,
}
