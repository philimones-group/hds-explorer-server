package org.philimone.hds.explorer.server.model.main.collect.raw

class RawMessage implements Serializable {
    String domain
    String text
    String[] columns

    RawMessage(String text, String[] columns) {
        this.text = text
        this.columns = columns
    }

    RawMessage(String text, List<String> columns) {
        this.text = text
        this.columns = columns.toArray(new String[0])
    }
}
