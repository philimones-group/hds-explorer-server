package org.philimone.hds.explorer.server.model.json.dashboard

class PyramidBar {

    Integer id
    Integer male
    Integer female //must be a negative value
    String ageRange //a string representing a range of dates "0-4",
    Integer minAge
    Integer maxAge
}

