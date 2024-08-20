package org.philimone.hds.explorer.server.model.json

class JActionResult {
    enum Result {
        ERROR, SUCCESS
    }

    String result = Result.ERROR.name()
    String title = ""
    String message = ""
    List data
}
