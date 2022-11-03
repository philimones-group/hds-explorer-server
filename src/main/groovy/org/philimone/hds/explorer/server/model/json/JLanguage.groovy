package org.philimone.hds.explorer.server.model.json

class JLanguage {
    String language
    String displayLanguage

    JLanguage() {

    }

    JLanguage(String language, String displayLanguage) {
        this.language = language
        this.displayLanguage = displayLanguage
    }

    @Override
    boolean equals(Object o) {
        if (o instanceof JLanguage) {
            return this.language.equals(o.language)
        }

        return false
    }
}
