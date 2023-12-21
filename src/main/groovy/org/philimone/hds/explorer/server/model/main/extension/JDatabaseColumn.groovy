package org.philimone.hds.explorer.server.model.main.extension

class JDatabaseColumn {
    String table
    String name
    String type
    String size

    @Override
    String toString() {
        return this.name
    }
}

